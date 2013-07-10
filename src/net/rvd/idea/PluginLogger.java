package net.rvd.idea;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFilePropertyEvent;

public class PluginLogger
{
  public static void log( String message, VirtualFilePropertyEvent event )
  {
    String path = event.getFile().getCanonicalPath();
    log( String.format( "%s: %s (property %s)", message, path, event.getPropertyName() ) );
  }

  public static void log( String message, VirtualFileEvent event )
  {
    String path = event.getFile().getCanonicalPath();
    log( String.format( "%s: %s", message, path ) );
  }

  public static void log( String message )
  {
    final String msg = String.format( "[RVD] %s", message );
    System.out.println( msg );
    PluginManager.getLogger().info( msg );
  }

  public static void log( String message, Throwable t )
  {
    final String msg = String.format( "[RVD] %s", message );
    System.err.println( msg );
    PluginManager.getLogger().error( msg, t );
  }
}
