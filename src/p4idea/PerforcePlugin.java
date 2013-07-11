package p4idea;

import com.intellij.openapi.components.*;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.perforce.p4java.exception.AccessException;
import com.perforce.p4java.exception.ConnectionException;
import org.jetbrains.annotations.NotNull;
import p4idea.perforce.P4Settings;
import p4idea.perforce.P4Wrapper;
import p4idea.vcs.P4FileAdapter;

@State( name = "P4Settings", storages = { @Storage( id = "default", file = StoragePathMacros.PROJECT_FILE ) } )
public class PerforcePlugin implements ProjectComponent, PersistentStateComponent<P4Settings>
{
  private static PerforcePlugin INSTANCE;
  private final P4FileAdapter _adapter = new P4FileAdapter();
  private P4Settings _settings = new P4Settings();

  public PerforcePlugin()
  {
    INSTANCE = this;
  }

  public static PerforcePlugin getInstance()
  {
    return INSTANCE;
  }

  @Override
  public void initComponent()
  {
    //LocalFileSystem.getInstance().addVirtualFileListener( _adapter );
  }

  @Override
  public void disposeComponent()
  {
    //LocalFileSystem.getInstance().removeVirtualFileListener( _adapter );
    try
    {
      P4Wrapper.getInstance().disconnect();
    }
    catch ( ConnectionException | AccessException e )
    {
      P4Logger.getInstance().error( e.getMessage(), e );
    }
  }

  @NotNull
  @Override
  public String getComponentName()
  {
    return getClass().getName();
  }

  @Override
  public void projectOpened()
  {
  }

  @Override
  public void projectClosed()
  {
  }

  @NotNull
  @Override
  public P4Settings getState()
  {
    return _settings;
  }

  @Override
  public void loadState( P4Settings settings )
  {
    if ( null != settings )
    {
      _settings = settings;
      P4Logger.getInstance().log( String.format( "Using %s", _settings ) );
    }
    try
    {
      P4Wrapper.getInstance().initialize( _settings );
    }
    catch ( ConnectionException | AccessException e )
    {
      P4Logger.getInstance().error( String.format( "Invalid settings: %s", _settings ), e );
    }
  }
}
