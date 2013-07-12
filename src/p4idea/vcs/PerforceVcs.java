package p4idea.vcs;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.*;
import com.intellij.openapi.vcs.changes.ChangeListEditHandler;
import com.intellij.openapi.vcs.changes.ChangeProvider;
import com.intellij.openapi.vcs.checkin.CheckinEnvironment;
import com.intellij.openapi.vcs.versionBrowser.CommittedChangeList;
import com.intellij.openapi.vfs.VirtualFile;
import com.perforce.p4java.exception.AccessException;
import com.perforce.p4java.exception.ConnectionException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import p4idea.P4Logger;
import p4idea.perforce.P4Wrapper;

public class PerforceVcs extends AbstractVcs<CommittedChangeList>
{
  public static final String NAME = "Perforce";
  private final P4Configurable _configurable;
  private final P4ChangeProvider _changeProvider;
  private final P4RootChecker _rootChecker;
  private final P4EditFileProvider _editFileProvider;

  public PerforceVcs( @NotNull Project project )
  {
    super( project, NAME );

    _configurable = new P4Configurable( project );
    _changeProvider = new P4ChangeProvider();
    _rootChecker = new P4RootChecker( project );
    _editFileProvider = new P4EditFileProvider();
  }

  @Override
  public String getDisplayName()
  {
    return NAME;
  }

  @NotNull
  @Override
  public synchronized Configurable getConfigurable()
  {
    return _configurable;
  }

  @Override
  public boolean fileExistsInVcs( FilePath path )
  {
    log( "fileExistsInVcs(): " + path.getPresentableUrl() );
    final VirtualFile virtualFile = path.getVirtualFile();
    if ( virtualFile != null )
    {
      final FileStatus fileStatus = FileStatusManager.getInstance( myProject ).getStatus( virtualFile );
      P4Logger.getInstance().log( "File status: " + fileStatus );
      return fileStatus != FileStatus.UNKNOWN && fileStatus != FileStatus.ADDED;
    }
    P4Logger.getInstance().log( "VirtualFile was null" );
    return true;
  }

  @Override
  public boolean fileIsUnderVcs( FilePath filePath )
  {
    log( "fileIsUnderVcs: " + filePath.getPresentableUrl() );
    return super.fileIsUnderVcs( filePath );
  }

  @Nullable
  @Override
  public ChangeProvider getChangeProvider()
  {
    return _changeProvider;
  }

  @Nullable
  @Override
  public CheckinEnvironment getCheckinEnvironment()
  {
    log( "getCheckinEnvironment()" );
    return super.getCheckinEnvironment();
  }

  @Override
  public CheckoutProvider getCheckoutProvider()
  {
    log( "getCheckoutProvider()" );
    return super.getCheckoutProvider();
  }

  @Nullable
  @Override
  public EditFileProvider getEditFileProvider()
  {
    return _editFileProvider;
  }

  @Nullable
  @Override
  public ChangeListEditHandler getEditHandler()
  {
    log( "getEditHandler()" );
    return super.getEditHandler();
  }

  @Override
  public boolean isTrackingUnchangedContent()
  {
    log( "isTrackingUnchangedContent()" );
    return super.isTrackingUnchangedContent();
  }

  @Nullable
  @Override
  public VcsRootChecker getRootChecker()
  {
    return _rootChecker;
  }

  @Override
  public VcsType getType()
  {
    return VcsType.centralized;
  }

  @Override
  public boolean areDirectoriesVersionedItems()
  {
    log( "areDirectoriesVersionedItems()" );
    return false;
  }

  @Override
  protected void deactivate()
  {
    try
    {
      P4Wrapper.getP4().disconnect();
    }
    catch ( ConnectionException | AccessException e )
    {
      P4Logger.getInstance().error( e.getMessage(), e );
    }
  }

  @Override
  public void enableIntegration()
  {
    log( "enableIntegration()" );
    super.enableIntegration();
  }

  private void log( String message )
  {
    P4Logger.getInstance().log( message );
  }
}
