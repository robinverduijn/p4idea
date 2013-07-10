package net.rvd.idea;

import com.intellij.openapi.vfs.*;
import net.rvd.perforce.P4Wrapper;

import java.io.File;

public class PerforceFileListener extends VirtualFileAdapter
{
  @Override
  public void beforePropertyChange( VirtualFilePropertyEvent event )
  {
    // TODO
    PluginLogger.log( "beforePropertyChange", event );
  }

  @Override
  public void propertyChanged( VirtualFilePropertyEvent event )
  {
    // TODO
    PluginLogger.log( "propertyChanged", event );
  }

  @Override
  public void beforeContentsChange( VirtualFileEvent event )
  {
    // Corresponds to "open for edit"
    PluginLogger.log( "beforeContentsChange", event );
  }

  @Override
  public void contentsChanged( VirtualFileEvent event )
  {
    // TODO
    PluginLogger.log( "contentsChanged", event );
  }

  @Override
  public void beforeFileDeletion( VirtualFileEvent event )
  {
    // TODO
    PluginLogger.log( "beforeFileDeletion", event );
  }

  @Override
  public void fileDeleted( VirtualFileEvent event )
  {
    // Corresponds to "delete"
    PluginLogger.log( "fileDeleted", event );
  }

  @Override
  public void beforeFileMovement( VirtualFileMoveEvent event )
  {
    // TODO
    PluginLogger.log( "beforeFileMovement", event );
  }

  @Override
  public void fileMoved( VirtualFileMoveEvent event )
  {
    // Corresponds to "rename"
    PluginLogger.log( "fileMoved", event );
  }

  @Override
  public void fileCreated( VirtualFileEvent event )
  {
    // Corresponds to "open for add"
    PluginLogger.log( "fileCreated", event );
  }

  @Override
  public void fileCopied( VirtualFileCopyEvent event )
  {
    // TODO
    PluginLogger.log( "fileCopied", event );
  }
}
