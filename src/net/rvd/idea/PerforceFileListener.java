package net.rvd.idea;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.vfs.*;

public class PerforceFileListener extends VirtualFileAdapter
{
  @Override
  public void propertyChanged( VirtualFilePropertyEvent event )
  {
    Logger.log( "propertyChanged", event );
  }

  @Override
  public void beforePropertyChange( VirtualFilePropertyEvent event )
  {
    Logger.log( "beforePropertyChange", event );
  }

  @Override
  public void beforeContentsChange( VirtualFileEvent event )
  {
    Logger.log( "beforeContentsChange", event );
  }

  @Override
  public void beforeFileDeletion( VirtualFileEvent event )
  {
    Logger.log( "beforeFileDeletion", event );
  }

  @Override
  public void beforeFileMovement( VirtualFileMoveEvent event )
  {
    Logger.log( "beforeFileMovement", event );
  }

  @Override
  public void contentsChanged( VirtualFileEvent event )
  {
    Logger.log( "contentsChanged", event );
  }

  @Override
  public void fileCreated( VirtualFileEvent event )
  {
    Logger.log( "fileCreated", event );
  }

  @Override
  public void fileDeleted( VirtualFileEvent event )
  {
    Logger.log( "fileDeleted", event );
  }

  @Override
  public void fileMoved( VirtualFileMoveEvent event )
  {
    Logger.log( "fileMoved", event );
  }

  @Override
  public void fileCopied( VirtualFileCopyEvent event )
  {
    Logger.log( "fileCopied", event );
  }
}
