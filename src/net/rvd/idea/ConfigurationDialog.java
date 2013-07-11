package net.rvd.idea;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.perforce.p4java.exception.*;
import net.rvd.perforce.PluginSettings;

import java.net.URISyntaxException;

public class ConfigurationDialog extends AnAction
{
  public void actionPerformed( AnActionEvent event )
  {
    final String title = "Perforce Settings";

    PluginSettings settings = PerforcePlugin.getInstance().getState();
    Project project = event.getData( PlatformDataKeys.PROJECT );

    String root = Messages.showInputDialog( project, "Perforce Root", title, Messages.getQuestionIcon(),
        settings.getP4port(), null );
    if ( !isEmpty( root ) )
    {
      settings.setP4port( root );
    }

    String client = Messages.showInputDialog( project, "Perforce Client", title, Messages.getQuestionIcon(),
        settings.getP4client(), null );
    if ( !isEmpty( client ) )
    {
      settings.setP4client( client );
    }

    String user = Messages.showInputDialog( project, "Perforce User", title, Messages.getQuestionIcon(),
        settings.getP4user(), null );
    if ( !isEmpty( user ) )
    {
      settings.setP4user( user );
    }

    try
    {
      settings.verify();
    }
    catch ( AccessException ae )
    {
      requestCredentials();
    }
    catch ( ConnectionException | RequestException e )
    {
      PluginLogger.error( "", e );
      Messages.showErrorDialog( e.getMessage(), "Error connecting to Perforce" );
    }
  }

  public static boolean requestCredentials()
  {
    PluginSettings settings = PerforcePlugin.getInstance().getState();

    final String msg = String.format( "Perforce Password for user %s", settings.getP4user() );
    final String title = "Login to Perforce";
    String password = Messages.showPasswordDialog( msg, title );
    try
    {
      settings.login( password );
      return true;
    }
    catch ( ConnectionException | RequestException e )
    {
      final String error = "Error connecting to Perforce";
      PluginLogger.error( error, e );
      Messages.showErrorDialog( e.getMessage(), error );
    }
    catch ( AccessException e )
    {
      final String error = String.format( "Invalid credentials for user %s", settings.getP4user() );
      PluginLogger.error( error, e );
      Messages.showErrorDialog( e.getMessage(), error );
    }
    catch ( ConfigException | ResourceException | NoSuchObjectException | URISyntaxException e )
    {
      final String error = "Perforce server error";
      PluginLogger.error( error, e );
      Messages.showErrorDialog( e.getMessage(), error );
    }
    return false;
  }

  private boolean isEmpty( String text )
  {
    return null == text || "".equals( text.trim() );
  }
}