package p4idea.vcs;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.*;
import com.intellij.openapi.vcs.changes.ChangeListEditHandler;
import com.intellij.openapi.vcs.changes.ChangeProvider;
import com.intellij.openapi.vcs.checkin.CheckinEnvironment;
import com.intellij.openapi.vcs.rollback.RollbackEnvironment;
import com.intellij.openapi.vcs.versionBrowser.CommittedChangeList;
import com.intellij.openapi.vfs.VirtualFile;
import com.perforce.p4java.exception.AccessException;
import com.perforce.p4java.exception.ConnectionException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import p4idea.P4Logger;
import p4idea.perforce.P4Wrapper;
import p4idea.ui.P4SettingsValidator;

public class PerforceVcs extends AbstractVcs<CommittedChangeList>
{
  public static final String NAME = "Perforce";
  private static final VcsKey KEY = AbstractVcs.createKey( NAME );
  public static PerforceVcs Instance;
  private final P4Configurable _configurable;
  private final P4ChangeProvider _changeProvider;
  private final P4RootChecker _rootChecker;
  private final P4EditFileProvider _editFileProvider;
  private final P4SettingsValidator _validator;
  private final P4CheckinEnvironment _checkinEnvironment;
  private final P4CheckoutProvider _checkoutProvider;
  private final P4CommittedChangesProvider _committedChangesProvider;
  private final P4RollbackEnvironment _rollbackEnvironment;

  public PerforceVcs( @NotNull Project project )
  {
    super( project, NAME );

    _configurable = new P4Configurable( project );
    _changeProvider = new P4ChangeProvider( project );
    _rootChecker = new P4RootChecker();
    _editFileProvider = new P4EditFileProvider();
    _validator = new P4SettingsValidator( project );
    _checkinEnvironment = new P4CheckinEnvironment();
    _checkoutProvider = new P4CheckoutProvider();
    _committedChangesProvider = new P4CommittedChangesProvider();
    _rollbackEnvironment = new P4RollbackEnvironment();

    Instance = this;
  }

  public static VcsKey getKey()
  {
    return KEY;
  }

  @Override
  public String getDisplayName()
  {
    return NAME;
  }

  @NotNull
  public P4SettingsValidator getValidator()
  {
    return _validator;
  }

  @NotNull
  @Override
  public synchronized Configurable getConfigurable()
  {
    return _configurable;
  }

  @Override
  public boolean fileExistsInVcs( FilePath filePath )
  {
    if ( !fileIsUnderVcs( filePath ) )
    {
      return false;
    }

    final VirtualFile virtualFile = filePath.getVirtualFile();
    final FileStatus fileStatus = FileStatusManager.getInstance( getProject() ).getStatus( virtualFile );
    boolean alreadyExists = fileStatus != FileStatus.UNKNOWN && fileStatus != FileStatus.ADDED;
    if ( !alreadyExists )
    {
      log( String.format( "fileExistsInVcs(): file status for %s: %s", filePath.getPath(), fileStatus ) );
    }
    return alreadyExists;
  }

  @Override
  public boolean fileIsUnderVcs( FilePath filePath )
  {
    final VirtualFile virtualFile = filePath.getVirtualFile();
    if ( virtualFile == null )
    {
      return false;
    }

    String path = virtualFile.getCanonicalPath();
    return null != path && !_rootChecker.isInvalidMapping( path );
  }

  @Nullable
  @Override
  public ChangeProvider getChangeProvider()
  {
    return _changeProvider;
  }

  @Nullable
  @Override
  public EditFileProvider getEditFileProvider()
  {
    return _editFileProvider;
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

  private void log( String message )
  {
    P4Logger.getInstance().log( message );
  }

  @Nullable
  @Override
  public ChangeListEditHandler getEditHandler()
  {
    log( "getEditHandler()" );
    return super.getEditHandler();
  }

  @Override
  public void enableIntegration()
  {
    log( "enableIntegration()" );
    super.enableIntegration();
  }

  @Nullable
  @Override
  public CheckinEnvironment getCheckinEnvironment()
  {
    return _checkinEnvironment;
  }

  @Override
  public CheckoutProvider getCheckoutProvider()
  {
    return _checkoutProvider;
  }

  @Nullable
  @Override
  public CommittedChangesProvider getCommittedChangesProvider()
  {
    return _committedChangesProvider;
  }

  @Nullable
  @Override
  public RollbackEnvironment getRollbackEnvironment()
  {
    return _rollbackEnvironment;
  }
}
