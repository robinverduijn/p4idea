package p4idea.vcs;

import com.google.common.collect.Lists;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vcs.*;
import com.intellij.openapi.vcs.changes.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.perforce.p4java.core.file.IFileSpec;
import com.perforce.p4java.exception.P4JavaException;
import p4idea.P4Logger;
import p4idea.perforce.P4Wrapper;

import java.io.File;
import java.util.*;

public class P4ChangeProvider implements ChangeProvider
{
  private final Project _project;

  public P4ChangeProvider( Project project )
  {
    _project = project;
  }

  @Override
  public void getChanges( VcsDirtyScope dirtyScope, ChangelistBuilder builder, ProgressIndicator progress,
                          ChangeListManagerGate addGate ) throws VcsException
  {
    if ( dirtyScope.getDirtyFiles().size() <= 0 )
    {
      return;
    }
    final Collection<FilePath> unversionedFiles = Lists.newArrayList();
    final Collection<Change> changes = collectChanges( dirtyScope, unversionedFiles );

    addFiles( getFilesToAdd( changes ) );
    deleteFiles( getFilesToDelete( changes ) );
    for ( Change change : changes )
    {
      builder.processChange( change, PerforceVcs.getKey() );
    }

    final List<FilePath> filesToRevert = Lists.newArrayList();
    for ( FilePath path : unversionedFiles )
    {
      filesToRevert.add( path );
      builder.processUnversionedFile( path.getVirtualFile() );
    }
    revertFiles( filesToRevert, false );
  }

  @Override
  public boolean isModifiedDocumentTrackingRequired()
  {
    // Used by VcsDirtyScopeManager in core platform code
    return true;
  }

  @Override
  public void doCleanup( List<VirtualFile> files )
  {
    P4Logger.getInstance().log( "doCleanup()" );
    for ( VirtualFile file : files )
    {
      P4Logger.getInstance().log( "- " + file.getCanonicalPath() );
    }
  }

  private Collection<Change> collectChanges( VcsDirtyScope dirtyScope, Collection<FilePath> unversionedFiles )
      throws VcsException
  {
    Collection<Change> changes = Lists.newArrayList();
    List<FilePath> dirtyFiles = Lists.newArrayList( dirtyScope.getDirtyFiles() );
    try
    {
      for ( IFileSpec status : P4Wrapper.getP4().getStatus( dirtyFiles ) )
      {
        if ( null != status.getDepotPathString() )
        {
          File file = new File( status.getLocalPathString() );
          FilePath path = removeFromList( dirtyFiles, file );
          if ( null != path )
          {
            if ( file.exists() )
            {
              changes.add( getVersionedEdit( path, status ) );
            }
            else
            {
              changes.add( getVersionedDelete( path, status ) );
            }
          }
          else
          {
            // This should never happen
            throw new VcsException( "Unknown file: " + file );
          }
        }
        // else: file does not exist in depot, deal with unversioned files below
      }
      for ( FilePath path : dirtyFiles )
      {
        File file = path.getIOFile();
        if ( file.exists() )
        {
          changes.add( getUnversionedAdd( path ) );
        }
        else
        {
          // Unversioned delete does not require any changes on the server
          unversionedFiles.add( path );
        }
      }
      return changes;
    }
    catch ( P4JavaException e )
    {
      throw new VcsException( e );
    }
  }

  private Change getVersionedEdit( FilePath path, IFileSpec file )
  {
    P4Logger.getInstance().log( "Open for edit: " + path.getPath() + " " + getP4DebugStatus( file ) );
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

  private FilePath removeFromList( Collection<FilePath> dirtyFiles, File file )
  {
    Iterator<FilePath> iter = dirtyFiles.iterator();
    while ( iter.hasNext() )
    {
      FilePath dirtyFile = iter.next();
      if ( FileUtil.filesEqual( dirtyFile.getIOFile(), file ) )
      {
        iter.remove();
        return dirtyFile;
      }
    }
    return null;
  }

  private List<FilePath> getFilesToAdd( Collection<Change> changes )
  {
    ArrayList<FilePath> files = Lists.newArrayList();
    for ( Change change : changes )
    {
      if ( change.getFileStatus() == FileStatus.ADDED )
      {
        files.add( change.getAfterRevision().getFile() );
      }
    }
    return files;
  }

  private List<FilePath> getFilesToDelete( Collection<Change> changes )
  {
    ArrayList<FilePath> files = Lists.newArrayList();
    for ( Change change : changes )
    {
      if ( change.getFileStatus() == FileStatus.DELETED )
      {
        files.add( change.getBeforeRevision().getFile() );
      }
    }
    return files;
  }

  private void addFiles( List<FilePath> files ) throws VcsException
  {
    if ( files.isEmpty() )
    {
      return;
    }
    try
    {
      Collection<IFileSpec> added = P4Wrapper.getP4().openForAdd( files );
      for ( IFileSpec file : added )
      {
        String path = file.getClientPathString();
        if ( null != path )
        {
          P4Logger.getInstance().log( "Opened for add: " + path );
        }
      }
    }
    catch ( P4JavaException e )
    {
      throw new VcsException( e );
    }
  }

  private void deleteFiles( List<FilePath> files ) throws VcsException
  {
    if ( files.isEmpty() )
    {
      return;
    }
    try
    {
      P4Wrapper p4 = P4Wrapper.getP4();
      p4.revert( files, true );
      Collection<IFileSpec> deleted = p4.openForDelete( files );
      for ( IFileSpec file : deleted )
      {
        String path = file.getClientPathString();
        if ( null != path )
        {
          P4Logger.getInstance().log( "Opened for delete: " + path );
        }
      }
    }
    catch ( P4JavaException e )
    {
      throw new VcsException( e );
    }
  }

  private void revertFiles( List<FilePath> files, boolean quiet ) throws VcsException
  {
    if ( files.isEmpty() )
    {
      return;
    }
    try
    {
      Collection<IFileSpec> reverted = P4Wrapper.getP4().revert( files, quiet );
      if ( quiet )
      {
        return;
      }
      for ( IFileSpec file : reverted )
      {
        String path = file.getClientPathString();
        if ( null != path )
        {
          P4Logger.getInstance().log( "Reverted: " + path );
        }
      }
    }
    catch ( P4JavaException e )
    {
      throw new VcsException( e );
    }
  }

  private String getP4DebugStatus( IFileSpec status )
  {
    StringBuilder msg = new StringBuilder();
    msg.append( "action: " ).append( status.getAction() ).append( ", " );
    msg.append( "depotPath: " ).append( status.getDepotPathString() ).append( ", " );
    msg.append( "changelist: " ).append( status.getChangelistId() ).append( ", " );
    msg.append( "startRevision: " ).append( status.getStartRevision() ).append( ", " );
    msg.append( "endRevision: " ).append( status.getEndRevision() );
    return msg.toString();
  }
}
