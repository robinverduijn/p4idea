package p4idea.vcs;

import com.google.common.collect.Lists;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.*;
import com.intellij.openapi.vcs.changes.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.perforce.p4java.core.file.IFileSpec;
import com.perforce.p4java.exception.*;
import com.perforce.p4java.impl.generic.core.file.FileSpec;
import p4idea.P4Logger;
import p4idea.perforce.P4Wrapper;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class P4ChangeProvider implements ChangeProvider
{
  private final P4ChangeCollector _collector;

  public P4ChangeProvider( Project project )
  {
    _collector = new P4ChangeCollector( project );
  }

  @Override
  public void getChanges( VcsDirtyScope dirtyScope, ChangelistBuilder builder, ProgressIndicator progress,
                          ChangeListManagerGate addGate ) throws VcsException
  {
    final Collection<Change> changes = _collector.collectChanges( dirtyScope );

    changes.addAll( addFiles( _collector.getFilesToAdd() ) );
    changes.addAll( deleteFiles( _collector.getFilesToDelete() ) );

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

  private Collection<Change> addFiles( Collection<FilePath> files ) throws VcsException
  {
    final Collection<Change> changes = Lists.newArrayList();
    if ( files.isEmpty() )
    {
      return changes;
    }
    try
    {
      Collection<IFileSpec> added = P4Wrapper.getP4().openForAdd( files );
      for ( IFileSpec file : added )
      {
        String path = file.getClientPathString();
        if ( null != path )
        {
          FilePath filePath = new FilePathImpl( new File( path ), false );
          changes.add( _collector.getUnversionedAdd( filePath ) );
          P4Logger.getInstance().log( String.format( "Opened for add: %s", path ) );
        }
      }
    }
    catch ( P4JavaException e )
    {
      P4Wrapper.getP4().handleP4Exception( "Error adding files", e );
    }
    return changes;
  }

  private Collection<Change> deleteFiles( Collection<FilePath> files ) throws VcsException
  {
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
      Collection<IFileSpec> reverted = p4.revert( p4.getOpenFiles( files ), true );
      for ( IFileSpec file : reverted )
      {
        String path = file.getClientPathString();
        if ( null != path )
        {
          P4Logger.getInstance().log( String.format( "Reverted: %s", path ) );
        }
      }

      // Then, perform a P4 delete on the remainder as they must be versioned
      Collection<IFileSpec> deleted = p4.openForDelete( filterDeletableFiles( files ) );
      for ( IFileSpec file : deleted )
      {
        String path = file.getClientPathString();
        if ( null != path )
        {
          FilePath filePath = new FilePathImpl( new File( path ), false );
          changes.add( _collector.getVersionedDelete( filePath, file ) );
          P4Logger.getInstance().log( String.format( "Opened for delete: %s", path ) );
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
    for ( IFileSpec fileSpec : P4Wrapper.getP4().getHave( files ) )
    {
      if ( null != fileSpec.getClientPathString() )
      {
        deletable.add( new FileSpec( fileSpec.getClientPathString() ) );
      }
    }
    return deletable;
  }
}
