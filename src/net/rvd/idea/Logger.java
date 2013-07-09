package net.rvd.idea;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFilePropertyEvent;

public class Logger
{
  public static void log( String message, VirtualFilePropertyEvent event )
  {
    log( String.format( "%s: %s (property %s)", message, event.getFileName(), event.getPropertyName() ) );
  }

  public static void log( String message, VirtualFileEvent event )
  {
    log( String.format( "%s: %s", message, event.getFileName() ) );
  }

  public static void log( String message )
  {
    PluginManager.getLogger().error( String.format( "[RVD] %s", message ) );
  }

  public static void log( String message, Throwable t )
  {
    PluginManager.getLogger().error( String.format( "[RVD] %s", message ), t );
  }
}
