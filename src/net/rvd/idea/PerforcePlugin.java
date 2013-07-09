package net.rvd.idea;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.vfs.LocalFileSystem;
import org.jetbrains.annotations.NotNull;

public class PerforcePlugin implements ProjectComponent/*, PersistentStateComponent*/
{
  private final PerforceFileListener _listener = new PerforceFileListener();

  @Override
  public void initComponent()
  {
    LocalFileSystem.getInstance().addVirtualFileListener( _listener );
  }

  @Override
  public void disposeComponent()
  {
    LocalFileSystem.getInstance().removeVirtualFileListener( _listener );
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
}
