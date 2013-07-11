package net.rvd.idea;

import com.intellij.openapi.components.*;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.perforce.p4java.exception.AccessException;
import com.perforce.p4java.exception.ConnectionException;
import net.rvd.perforce.P4Wrapper;
import net.rvd.perforce.PluginSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "PerforcePluginSettings", storages = { @Storage(id = "default", file = StoragePathMacros.PROJECT_FILE) })
public class PerforcePlugin implements ProjectComponent, PersistentStateComponent<PluginSettings>
{
  private static PerforcePlugin INSTANCE;
  private final PerforceFileListener _listener = new PerforceFileListener();
  private PluginSettings _settings = new PluginSettings();

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
    LocalFileSystem.getInstance().addVirtualFileListener( _listener );
  }

  @Override
  public void disposeComponent()
  {
    LocalFileSystem.getInstance().removeVirtualFileListener( _listener );
    try
    {
      P4Wrapper.getInstance().disconnect();
    }
    catch ( ConnectionException | AccessException e )
    {
      PluginLogger.error( e.getMessage(), e );
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

  @Nullable
  @Override
  public PluginSettings getState()
  {
    return _settings;
  }

  @Override
  public void loadState( PluginSettings settings )
  {
    _settings = settings;
    try
    {
      P4Wrapper p4 = P4Wrapper.getInstance().initialize( _settings );
    }
    catch ( ConnectionException | AccessException e )
    {
      PluginLogger.error( "Invalid settings: " + _settings, e );
    }
  }
}
