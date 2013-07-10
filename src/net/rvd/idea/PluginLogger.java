package net.rvd.idea;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFilePropertyEvent;
import net.rvd.perforce.P4Wrapper;

import java.io.File;

public class PluginLogger
{
  public static void log( String message, VirtualFilePropertyEvent event )
  {
    String path = event.getFile().getCanonicalPath();
    log( String.format( "%s: %s (property %s)", message, path, event.getPropertyName() ) );
  }

  public static void log( String message, VirtualFileEvent event )
  {
    File file = new File( event.getFile().getCanonicalPath() );
    File root = P4Wrapper.getInstance().getP4Root();
    boolean val = file.getAbsolutePath().startsWith( root.getAbsolutePath() );
    PluginLogger.log( String.format( "%s under %s? %b", file, root, val ) );
    log( String.format( "%s: %s", message, file ) );
  }

  public static void log( String message )
  {
    System.out.println( message );
    Logger.getInstance( "PerforcePlugin" ).info( message );
  }

  public static void error( String message, Throwable t )
  {
    log( message );
    PluginManager.getLogger().error( message, t );
  }
}
