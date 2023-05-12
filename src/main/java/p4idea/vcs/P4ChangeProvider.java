package p4idea.vcs;

import com.google.common.collect.Lists;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.*;
import com.intellij.openapi.vcs.changes.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.perforce.p4java.core.file.FileSpecOpStatus;
import com.perforce.p4java.core.file.IFileSpec;
import com.perforce.p4java.exception.*;
import com.perforce.p4java.impl.generic.core.file.FileSpec;
import org.jetbrains.annotations.NotNull;
import p4idea.FileLists;
import p4idea.P4Logger;
import p4idea.perforce.P4Wrapper;

import java.util.Collection;
import java.util.List;

class P4ChangeProvider implements ChangeProvider
{
  private final Project _project;

  public P4ChangeProvider( Project project )
  {
    _project = project;
  }

  @Override
  public void getChanges( @NotNull VcsDirtyScope dirtyScope, @NotNull ChangelistBuilder builder, @NotNull ProgressIndicator progress,
                          @NotNull ChangeListManagerGate addGate ) throws VcsException
  {
    final P4ChangeCollector collector = new P4ChangeCollector( _project );
    final Collection<Change> changes = collector.collectChanges( dirtyScope );

    changes.addAll( addFiles( collector ) );
    changes.addAll( deleteFiles( collector ) );
    for ( VirtualFile file : collector.getUnversionedFiles() )
    {
      builder.processUnversionedFile( file );
    }

    for ( Change change : changes )
    {
      builder.processChange( change, PerforceVcs.getKey() );
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

  private Collection<Change> addFiles( P4ChangeCollector collector ) throws VcsException
  {
    final Collection<FilePath> files = collector.getFilesToAdd();
    final Collection<Change> changes = Lists.newArrayList();
    if ( files.isEmpty() )
    {
      return changes;
    }
    try
    {
      List<IFileSpec> added = P4Wrapper.getP4().openForAdd( FileLists.fromFilePaths( files ) );
      for ( IFileSpec file : added )
      {
        String path = file.getClientPathString();
        if ( null != path )
        {
          if ( file.getOpStatus() == FileSpecOpStatus.VALID )
          {
            FilePath filePath = new LocalFilePath( path, false );
            changes.add( collector.getUnversionedAdd( filePath ) );
            P4Logger.getInstance().log( String.format( "Opened for add: %s", path ) );
          }
        }
      }
    }
    catch ( P4JavaException e )
    {
      P4Wrapper.getP4().handleP4Exception( "Error adding files", e );
    }
    return changes;
  }

  private Collection<Change> deleteFiles( P4ChangeCollector collector ) throws VcsException
  {
    final Collection<FilePath> files = collector.getFilesToDelete();
    final Collection<Change> changes = Lists.newArrayList();
    if ( files.isEmpty() )
    {
      return changes;
    }
    try
    {
      final P4Wrapper p4 = P4Wrapper.getP4();

      // First, revert any files which may currently be open (this also deals with files to delete which were
      // temporarily opened for edit for the duration of this action by P4EditFileProvider
      p4.revert( p4.getOpenFiles( FileLists.fromFilePaths( files ) ) );

      // Then, perform a P4 delete on the remainder as they must be versioned
      List<IFileSpec> deleted = p4.openForDelete( filterDeletableFiles( files ) );
      for ( IFileSpec file : deleted )
      {
        String path = file.getClientPathString();
        if ( null != path )
        {
          if ( file.getOpStatus() == FileSpecOpStatus.VALID )
          {
            FilePath filePath = new LocalFilePath( path, false );
            changes.add( collector.getVersionedDelete( filePath, file ) );
            P4Logger.getInstance().log( String.format( "Opened for delete: %s", path ) );
          }
        }
      }
    }
    catch ( P4JavaException e )
    {
      P4Wrapper.getP4().handleP4Exception( "Error deleting files", e );
    }
    return changes;
  }

  private List<IFileSpec> filterDeletableFiles( Collection<FilePath> files ) throws ConnectionException,
      AccessException
  {
    List<IFileSpec> deletable = Lists.newArrayList();
    for ( IFileSpec fileSpec : P4Wrapper.getP4().getHave( FileLists.fromFilePaths( files ) ) )
    {
      if ( null != fileSpec.getClientPathString() )
      {
        deletable.add( new FileSpec( fileSpec.getClientPathString() ) );
      }
    }
    return deletable;
  }
}
