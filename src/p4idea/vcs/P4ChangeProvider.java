package p4idea.vcs;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.perforce.p4java.core.file.IFileSpec;
import com.perforce.p4java.exception.P4JavaException;
import p4idea.P4Logger;
import p4idea.perforce.P4Wrapper;

import java.util.Collection;
import java.util.List;

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
    final P4ChangeCollector collector = new P4ChangeCollector( _project );

    final Collection<Change> changes = collector.collectChanges( dirtyScope );
    final Collection<FilePath> unversionedFiles = collector.getUnversionedFiles();

    doAddFiles( collector.getFilesToAdd() );
    doDeleteFiles( collector.getFilesToDelete() );
    doRevertFiles( unversionedFiles );

    for ( Change change : changes )
    {
      builder.processChange( change, PerforceVcs.getKey() );
    }
    for ( FilePath path : unversionedFiles )
    {
      builder.processUnversionedFile( path.getVirtualFile() );
    }
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
      P4Logger.getInstance().log( String.format( "- %s", file.getCanonicalPath() ) );
    }
  }

  private void doAddFiles( List<FilePath> files ) throws VcsException
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
          P4Logger.getInstance().log( String.format( "Opened for add: %s", path ) );
        }
      }
    }
    catch ( P4JavaException e )
    {
      P4Wrapper.getP4().handleP4Exception( "Error adding files", e );
    }
  }

  private void doDeleteFiles( List<FilePath> files ) throws VcsException
  {
    if ( files.isEmpty() )
    {
      return;
    }
    try
    {
      P4Wrapper p4 = P4Wrapper.getP4();
      List<IFileSpec> fileSpecs = p4.getHave( files );
      p4.revert( fileSpecs, true );
      Collection<IFileSpec> deleted = p4.openForDelete( files );
      for ( IFileSpec file : deleted )
      {
        String path = file.getClientPathString();
        if ( null != path )
        {
          P4Logger.getInstance().log( String.format( "Opened for delete: %s", path ) );
        }
      }
    }
    catch ( P4JavaException e )
    {
      P4Wrapper.getP4().handleP4Exception( "Error deleting files", e );
    }
  }

  private void doRevertFiles( Collection<FilePath> files ) throws VcsException
  {
    if ( files.isEmpty() )
    {
      return;
    }
    try
    {
      Collection<IFileSpec> reverted = P4Wrapper.getP4().revert( files, true );
      for ( IFileSpec file : reverted )
      {
        String path = file.getClientPathString();
        if ( null != path )
        {
          P4Logger.getInstance().log( String.format( "Reverted: %s", path ) );
        }
      }
    }
    catch ( P4JavaException e )
    {
      P4Wrapper.getP4().handleP4Exception( "Error reverting files", e );
    }
  }
}
