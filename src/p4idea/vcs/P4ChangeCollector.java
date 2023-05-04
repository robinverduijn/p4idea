package p4idea.vcs;

import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.*;
import com.intellij.openapi.vcs.changes.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.perforce.p4java.core.file.FileSpecOpStatus;
import com.perforce.p4java.core.file.IFileSpec;
import com.perforce.p4java.exception.*;
import p4idea.FileLists;
import p4idea.P4Logger;
import p4idea.perforce.P4Ignore;
import p4idea.perforce.P4Wrapper;

import java.io.File;
import java.util.*;

class P4ChangeCollector
{
  private final Project _project;
  private final P4Ignore _p4ignore;
  private final Collection<Change> _changes;
  private final Collection<FilePath> _unversionedAdditions;
  private final Collection<FilePath> _unversionedDeletions;
  private final Collection<VirtualFile> _unversionedFiles;

  public P4ChangeCollector( Project project )
  {
    _project = project;
    _p4ignore = new P4Ignore();
    _changes = Lists.newArrayList();
    _unversionedAdditions = Lists.newArrayList();
    _unversionedDeletions = Lists.newArrayList();
    _unversionedFiles = Lists.newArrayList();
  }

  @SuppressWarnings( "ConstantConditions" )
  public static FilePath getFileForChange( Change change ) throws VcsException
  {
    switch ( change.getType() )
    {
      case DELETED:
        return change.getBeforeRevision().getFile();
      case MODIFICATION:
        return change.getAfterRevision().getFile();
      case MOVED:
        return change.getAfterRevision().getFile();
      case NEW:
        return change.getAfterRevision().getFile();
      default:
        throw new VcsException( String.format( "Could not determine file from %s", change.getVirtualFile() ) );
    }
  }

  public Collection<Change> collectChanges( VcsDirtyScope dirtyScope )
      throws VcsException
  {
    _changes.clear();
    _unversionedAdditions.clear();
    _unversionedDeletions.clear();
    try
    {
      if ( !dirtyScope.getDirtyFiles().isEmpty() )
      {
        processLocalFiles( dirtyScope.getDirtyFiles() );
        for ( FilePath filePath : _p4ignore.getIgnored( dirtyScope.getDirtyFiles() ) )
        {
          _unversionedFiles.add( filePath.getVirtualFile() );
        }
      }
      processOpenP4Files();
    }
    catch ( P4JavaException e )
    {
      P4Wrapper.getP4().handleP4Exception( "Error collecting changes", e );
    }
    return _changes;
  }

  private void processLocalFiles( Collection<FilePath> dirtyFiles ) throws ConnectionException, AccessException,
      VcsException
  {
    Collection<FilePath> localFiles = _p4ignore.getUnignored( dirtyFiles );
    for ( IFileSpec status : P4Wrapper.getP4().getWhere( FileLists.fromFilePaths( localFiles ) ) )
    {
      if ( null != status.getLocalPathString() )
      {
        File file = new File( status.getLocalPathString() );
        FilePath path = FileLists.removeFromList( dirtyFiles, file );
        if ( null != path )
        {
          if ( file.exists() )
          {
            if ( isLocal( path ) )
            {
              _unversionedAdditions.add( path );
            }
          }
          else
          {
            _unversionedDeletions.add( path );
          }
        }
        else
        {
          // This should never happen
          throw new VcsException( String.format( "Unknown file: %s", file ) );
        }
      }
      else
      {
        String info = P4Logger.getInstance().getP4DebugStatus( status );
        throw new VcsException( String.format( "Unable to determine local path for %s", info ) );
      }
    }
  }

  private boolean isLocal( FilePath filePath ) throws ConnectionException, AccessException
  {
    boolean result = false;
    for ( IFileSpec status : P4Wrapper.getP4().getHave( FileLists.fromFilePaths( Arrays.asList( filePath ) ) ) )
    {
      if ( status.getOpStatus() != FileSpecOpStatus.VALID )
      {
        result = true;
      }
    }
    return result;
  }

  private void processOpenP4Files() throws VcsException
  {
    try
    {
      P4Wrapper p4 = P4Wrapper.getP4();
      List<IFileSpec> openFiles = p4.getOpenFiles();
      if ( openFiles.isEmpty() )
      {
        return;
      }

      List<IFileSpec> whereList = p4.getWhere( FileLists.getDepotPaths( openFiles ) );
      for ( IFileSpec file : FileLists.mergeLocalPaths( openFiles, whereList ) )
      {
        String pathStr = file.getLocalPathString();
        FilePath path = new FilePathImpl( new File( pathStr ), false );
        switch ( file.getAction() )
        {
          case ADD:
            if ( !_unversionedDeletions.contains( path ) )
            {
              _unversionedAdditions.remove( path );
              _changes.add( getVersionedAdd( path, file ) );
            }
            break;
          case BRANCH:
            _unversionedAdditions.remove( path );
            _changes.add( getVersionedBranch( path, file ) );
            break;
          case EDIT:
            _changes.add( getVersionedEdit( path, file ) );
            break;
          case DELETE:
            _unversionedDeletions.remove( path );
            _changes.add( getVersionedDelete( path, file ) );
            break;
          case INTEGRATE:
            _changes.add( getVersionedIntegrate( path, file ) );
            break;
          default:
            P4Logger.getInstance().log( String.format( "Unknown file action: %s for %s", file.getAction(), file ) );
            _changes.add( getVersionedUnknown( path, file ) );
            break;
        }
      }
    }
    catch ( ConnectionException | AccessException e )
    {
      P4Wrapper.getP4().handleP4Exception( "Error retrieving P4 change status", e );
    }
  }

  private Change getVersionedEdit( FilePath path, IFileSpec file )
  {
    ContentRevision before = P4ContentRevision.create( _project, path, file.getEndRevision() );
    ContentRevision after = P4ContentRevision.create( _project, path, null );
    return new Change( before, after, FileStatus.MODIFIED );
  }

  Change getVersionedDelete( FilePath path, IFileSpec file )
  {
    ContentRevision before = P4ContentRevision.create( _project, path, file.getEndRevision() );
    ContentRevision after = null;
    return new Change( before, after, FileStatus.DELETED );
  }

  Change getUnversionedAdd( FilePath path )
  {
    ContentRevision before = null;
    ContentRevision after = P4ContentRevision.create( _project, path, null );
    return new Change( before, after, FileStatus.ADDED );
  }

  private Change getVersionedAdd( FilePath path, IFileSpec file )
  {
    ContentRevision before = null;
    ContentRevision after = P4ContentRevision.create( _project, path, file.getEndRevision() );
    return new Change( before, after, FileStatus.ADDED );
  }

  private Change getVersionedBranch( FilePath path, IFileSpec file )
  {
    ContentRevision before = null;
    ContentRevision after = P4ContentRevision.create( _project, path, file.getEndRevision() );
    return new Change( before, after, FileStatus.MERGE );
  }

  private Change getVersionedIntegrate( FilePath path, IFileSpec file )
  {
    ContentRevision before = P4ContentRevision.create( _project, path, file.getEndRevision() );
    ContentRevision after = P4ContentRevision.create( _project, path, null );
    return new Change( before, after, FileStatus.MERGE );
  }

  private Change getVersionedUnknown( FilePath path, IFileSpec file )
  {
    ContentRevision before = P4ContentRevision.create( _project, path, file.getStartRevision() );
    ContentRevision after = P4ContentRevision.create( _project, path, file.getEndRevision() );
    return new Change( before, after, FileStatus.UNKNOWN );
  }

  public Collection<FilePath> getFilesToAdd()
  {
    return _unversionedAdditions;
  }

  public Collection<FilePath> getFilesToDelete()
  {
    return _unversionedDeletions;
  }

  public Collection<VirtualFile> getUnversionedFiles()
  {
    return _unversionedFiles;
  }
}
