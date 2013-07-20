package p4idea.vcs;

import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.*;
import com.intellij.openapi.vcs.changes.*;
import com.perforce.p4java.core.file.IFileSpec;
import com.perforce.p4java.exception.*;
import p4idea.FileLists;
import p4idea.P4Logger;
import p4idea.perforce.P4Ignore;
import p4idea.perforce.P4Wrapper;

import java.io.File;
import java.util.*;

public class P4ChangeCollector
{
  private final Project _project;
  private final P4Ignore _p4ignore;
  private final Collection<Change> _changes;
  private final Collection<Change> _localChanges;
  private final Collection<FilePath> _unversionedFiles;

  public P4ChangeCollector( Project project )
  {
    _project = project;
    _p4ignore = new P4Ignore();
    _changes = Lists.newArrayList();
    _localChanges = Lists.newArrayList();
    _unversionedFiles = Lists.newArrayList();
  }

  public Collection<Change> collectChanges( VcsDirtyScope dirtyScope )
      throws VcsException
  {
    try
    {
      if ( !dirtyScope.getDirtyFiles().isEmpty() )
      {
        processLocalFiles( dirtyScope.getDirtyFiles() );
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
    for ( IFileSpec status : P4Wrapper.getP4().getWhere( dirtyFiles ) )
    {
      if ( null != status.getLocalPathString() )
      {
        File file = new File( status.getLocalPathString() );
        FilePath path = FileLists.removeFromList( dirtyFiles, file );
        if ( null != path )
        {
          if ( file.exists() )
          {
            _localChanges.add( getVersionedEdit( path, status ) );
          }
          else
          {
            _localChanges.add( getVersionedDelete( path, status ) );
          }
        }
        else
        {
          // This should never happen
          throw new VcsException( String.format( "Unknown file: %s", file ) );
        }
      }
      // else: file does not exist in depot, deal with unversioned files below
    }

    for ( FilePath path : _p4ignore.p4ignore( dirtyFiles ) )
    {
      File file = path.getIOFile();
      if ( file.exists() )
      {
        _localChanges.add( getUnversionedAdd( path ) );
      }
      else
      {
        // Unversioned delete does not require any changes on the server
        _unversionedFiles.add( path );
      }
    }

    _changes.addAll( _localChanges );
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
            _changes.add( getVersionedAdd( path, file ) );
            break;
          case EDIT:
            _changes.add( getVersionedEdit( path, file ) );
            break;
          case DELETE:
            _changes.add( getVersionedDelete( path, file ) );
            break;
          default:
            P4Logger.getInstance().log( String.format( "Unknown file action: %s for %s", file.getAction(), file ) );
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
    ContentRevision before = new P4ContentRevision( _project, path, file.getEndRevision() );
    return new Change( before, null, FileStatus.MODIFIED );
  }

  private Change getVersionedDelete( FilePath path, IFileSpec file )
  {
    ContentRevision before = new P4ContentRevision( _project, path, file.getEndRevision() );
    return new Change( before, null, FileStatus.DELETED );
  }

  private Change getVersionedAdd( FilePath path, IFileSpec file )
  {
    ContentRevision after = new P4ContentRevision( _project, path, file.getEndRevision() );
    return new Change( null, after, FileStatus.ADDED );
  }

  private Change getUnversionedAdd( FilePath path )
  {
    ContentRevision after = new P4ContentRevision( _project, path, -1 );
    return new Change( null, after, FileStatus.ADDED );
  }

  public List<FilePath> getFilesToAdd()
  {
    ArrayList<FilePath> files = Lists.newArrayList();
    for ( Change change : _localChanges )
    {
      if ( change.getFileStatus() == FileStatus.ADDED )
      {
        files.add( change.getAfterRevision().getFile() );
      }
    }
    return files;
  }

  public List<FilePath> getFilesToDelete()
  {
    ArrayList<FilePath> files = Lists.newArrayList();
    for ( Change change : _localChanges )
    {
      if ( change.getFileStatus() == FileStatus.DELETED )
      {
        files.add( change.getBeforeRevision().getFile() );
      }
    }
    return files;
  }

  public Collection<FilePath> getUnversionedFiles()
  {
    return _unversionedFiles;
  }
}
