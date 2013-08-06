package p4idea.vcs;

import com.google.common.collect.Lists;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vcs.*;
import com.intellij.openapi.vcs.changes.committed.*;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import com.intellij.openapi.vcs.versionBrowser.ChangesBrowserSettingsEditor;
import com.intellij.openapi.vcs.versionBrowser.CommittedChangeList;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.AsynchConsumer;
import com.perforce.p4java.core.IChangelistSummary;
import com.perforce.p4java.core.file.IFileSpec;
import com.perforce.p4java.exception.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import p4idea.FileLists;
import p4idea.P4Logger;
import p4idea.perforce.P4Wrapper;

import java.util.Arrays;
import java.util.List;

class P4CommittedChangesProvider implements CommittedChangesProvider<P4CommittedChangeList,
    P4ChangeBrowserSettings>
{
  @NotNull
  @Override
  public P4ChangeBrowserSettings createDefaultSettings()
  {
    return new P4ChangeBrowserSettings();
  }

  @Override
  public ChangesBrowserSettingsEditor<P4ChangeBrowserSettings> createFilterUI( boolean showDateFilter )
  {
    return null;
  }

  @Nullable
  @Override
  public RepositoryLocation getLocationFor( FilePath root )
  {
    try
    {
      List<IFileSpec> fileSpecs = P4Wrapper.getP4().getWhere( FileLists.fromFilePaths( Arrays.asList( root ) ) );
      return P4RepositoryLocation.create( fileSpecs.get( 0 ) );
    }
    catch ( ConnectionException | AccessException e )
    {
      final String msg = "Unable to determine repository location for %s";
      P4Logger.getInstance().error( String.format( msg, root ), e );
      return null;
    }
  }

  @Nullable
  @Override
  public RepositoryLocation getLocationFor( FilePath root, String repositoryPath )
  {
    return getLocationFor( root );
  }

  @Nullable
  @Override
  public VcsCommittedListsZipper getZipper()
  {
    return null;
  }

  @Override
  public List<P4CommittedChangeList> getCommittedChanges( P4ChangeBrowserSettings settings, RepositoryLocation location,
                                                          int maxCount ) throws
      VcsException
  {
    try
    {
      List<P4CommittedChangeList> results = Lists.newArrayList();
      List<IChangelistSummary> changelists = P4Wrapper.getP4().getChangelists( location.getKey(), maxCount );
      for ( IChangelistSummary changelist : changelists )
      {
        results.add( new P4CommittedChangeList( changelist ) );
      }
      return results;
    }
    catch ( ConnectionException | RequestException | AccessException e )
    {
      throw new VcsException( e );
    }
  }

  @Override
  public void loadCommittedChanges( P4ChangeBrowserSettings settings, RepositoryLocation location, int maxCount,
                                    AsynchConsumer<CommittedChangeList> consumer ) throws VcsException
  {
    try
    {
      for ( CommittedChangeList changeList : getCommittedChanges( settings, location, maxCount ) )
      {
        consumer.consume( changeList );
      }
    }
    finally
    {
      consumer.finished();
    }
  }

  @Override
  public ChangeListColumn[] getColumns()
  {
    return new ChangeListColumn[]{ ChangeListColumn.NUMBER, ChangeListColumn.DATE, ChangeListColumn.NAME,
                                   ChangeListColumn.DESCRIPTION };
  }

  @Nullable
  @Override
  public VcsCommittedViewAuxiliary createActions( DecoratorManager manager, RepositoryLocation location )
  {
    return null;
  }

  @Override
  public int getUnlimitedCountValue()
  {
    return -1;
  }

  @Nullable
  @Override
  public Pair<P4CommittedChangeList, FilePath> getOneList( VirtualFile file, VcsRevisionNumber number ) throws
      VcsException
  {
    try
    {
      List<IChangelistSummary> changelists = P4Wrapper.getP4().getChangelists( file.getPath(), 1 );
      if ( changelists.isEmpty() )
      {
        return null;
      }
      final P4CommittedChangeList changelist = new P4CommittedChangeList( changelists.get( 0 ) );
      final FilePath path = new FilePathImpl( file );
      return new Pair<>( changelist, path );
    }
    catch ( ConnectionException | RequestException | AccessException e )
    {
      final String msg = "Unable to determine history for %s";
      P4Logger.getInstance().error( String.format( msg, file ), e );
      return null;
    }
  }

  @Override
  public RepositoryLocation getForNonLocal( VirtualFile file )
  {
    return null;
  }

  @Override
  public boolean supportsIncomingChanges()
  {
    return false;
  }
}
