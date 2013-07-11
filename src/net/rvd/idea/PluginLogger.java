package net.rvd.idea;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFilePropertyEvent;
import com.perforce.p4java.exception.AccessException;
import com.perforce.p4java.exception.ConnectionException;
import net.rvd.perforce.P4Wrapper;

import java.io.File;

public class PluginLogger
{
  private static boolean shouldBeHandled( String path )
  {
    boolean result = false;
    try
    {
      File file = new File( path );
      File root = P4Wrapper.getInstance().getP4Root();
      result = null != root && file.getAbsolutePath().startsWith( root.getAbsolutePath() );
      PluginLogger.log( String.format( "%s under %s: %b", file, root, result ) );
    }
    catch ( ConnectionException e )
    {
      error( "Error connecting to Perforce", e );
    }
    catch ( AccessException ae )
    {
      if ( !ConfigurationDialog.requestCredentials() )
      {
        try
        {
          P4Wrapper.getInstance().disconnect();
        }
        catch ( ConnectionException | AccessException e )
        {
          error( "Error disconnecting from Perforce", e );
        }
      }
    }
    return result;
  }

  public static void log( String message, VirtualFilePropertyEvent event )
  {
    String path = event.getFile().getCanonicalPath();
    log( String.format( "%s: %s (property %s)", message, path, event.getPropertyName() ) );
  }

  public static void log( String message, VirtualFileEvent event )
  {
    String path = event.getFile().getCanonicalPath();
    if ( shouldBeHandled( path ) )
    {
      log( String.format( "%s: %s", message, path ) );
    }
  }

  public static void log( String message )
  {
    Logger.getInstance( "PerforcePlugin" ).info( message );
  }

  public static void error( String message, Throwable t )
  {
    PluginManager.getLogger().error( message, t );
  }
}
