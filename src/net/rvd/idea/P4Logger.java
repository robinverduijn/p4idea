package net.rvd.idea;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFilePropertyEvent;

public class P4Logger
{
  private static final P4Logger INSTANCE = new P4Logger();

  private P4Logger()
  {
  }

  public static P4Logger getInstance()
  {
    return INSTANCE;
  }

  public void log( String message, VirtualFilePropertyEvent event )
  {
    String path = event.getFile().getCanonicalPath();
    log( String.format( "%s: %s (property %s)", message, path, event.getPropertyName() ) );
  }

  public void log( String message, VirtualFileEvent event )
  {
    String path = event.getFile().getCanonicalPath();
    log( String.format( "%s: %s", message, path ) );
  }

  public void log( String message )
  {
    Logger.getInstance( "P4" ).info( message );
  }

  public void error( String message, Throwable t )
  {
    PluginManager.getLogger().error( message, t );
  }
}
