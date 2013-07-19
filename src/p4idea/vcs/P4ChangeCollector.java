package p4idea.vcs;

import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.*;
import com.intellij.openapi.vcs.changes.*;
import com.perforce.p4java.core.file.IFileSpec;
import com.perforce.p4java.exception.P4JavaException;
import p4idea.FileLists;
import p4idea.perforce.P4Ignore;
import p4idea.perforce.P4Wrapper;

import java.io.File;
import java.util.*;

public class P4ChangeCollector
{
  private final Project _project;
  private final P4Ignore _p4ignore;
  private final Collection<Change> _changes;
  private final Collection<FilePath> _unversionedFiles;

  public P4ChangeCollector( Project project )
  {
    _project = project;
    _p4ignore = new P4Ignore();
    _changes = Lists.newArrayList();
    _unversionedFiles = Lists.newArrayList();
  }

  public Collection<Change> collectChanges( VcsDirtyScope dirtyScope )
      throws VcsException
  {
    List<FilePath> dirtyFiles = Lists.newArrayList( dirtyScope.getDirtyFiles() );
    try
    {
      for ( IFileSpec status : P4Wrapper.getP4().getStatus( dirtyFiles ) )
      {
        if ( null != status.getDepotPathString() )
        {
          File file = new File( status.getLocalPathString() );
          FilePath path = FileLists.removeFromList( dirtyFiles, file );
          if ( null != path )
          {
            if ( file.exists() )
            {
              _changes.add( getVersionedEdit( path, status ) );
            }
            else
            {
              _changes.add( getVersionedDelete( path, status ) );
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
          _changes.add( getUnversionedAdd( path ) );
        }
        else
        {
          // Unversioned delete does not require any changes on the server
          _unversionedFiles.add( path );
        }
      }
    }
    catch ( P4JavaException e )
    {
      P4Wrapper.getP4().handleP4Exception( e );
    }
    return _changes;
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

  private Change getUnversionedAdd( FilePath path )
  {
    ContentRevision after = new P4ContentRevision( _project, path, -1 );
    return new Change( null, after, FileStatus.ADDED );
  }

  public List<FilePath> getFilesToAdd()
  {
    ArrayList<FilePath> files = Lists.newArrayList();
    for ( Change change : _changes )
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
    for ( Change change : _changes )
    {
      if ( change.getFileStatus() == FileStatus.DELETED )
      {
        files.add( change.getBeforeRevision().getFile() );
      }
    }
    return files;
  }

  public Collection<FilePath> getFilesToRevert()
  {
    return _unversionedFiles;
  }
}
