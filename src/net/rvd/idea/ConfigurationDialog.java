package net.rvd.idea;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.perforce.p4java.exception.AccessException;
import net.rvd.perforce.PluginSettings;

public class ConfigurationDialog extends AnAction
{
  public void actionPerformed( AnActionEvent event )
  {
    final String title = "Perforce Settings";

    PluginSettings settings = PerforcePlugin.getInstance().getState();
    Project project = event.getData( PlatformDataKeys.PROJECT );

    String root = Messages.showInputDialog( project, "Perforce Root", title, Messages.getQuestionIcon(), settings.getP4port(), null );
    if ( !isEmpty( root ) )
    {
      settings.setP4port( root );
    }

    String client = Messages.showInputDialog( project, "Perforce Client", title, Messages.getQuestionIcon(), settings.getP4client(), null );
    if ( !isEmpty( client ) )
    {
      settings.setP4client( client );
    }

    String user = Messages.showInputDialog( project, "Perforce User", title, Messages.getQuestionIcon(), settings.getP4user(), null );
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
      try
      {
        final String msg = String.format( "Perforce Password for user %s", user );
        String password = Messages.showPasswordDialog( project, msg, title, null );
        settings.login( password );
      }
      catch ( Exception e )
      {
        PluginLogger.error( "", e );
        Messages.showErrorDialog( e.getMessage(), "Error connecting to Perforce" );
      }
    }
    catch ( Exception e )
    {
      PluginLogger.error( "", e );
      Messages.showErrorDialog( e.getMessage(), "Error connecting to Perforce" );
    }
  }

  private boolean isEmpty( String text )
  {
    return null == text || "".equals( text.trim() );
  }
}