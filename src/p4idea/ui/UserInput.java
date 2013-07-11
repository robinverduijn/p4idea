package p4idea.ui;

import com.intellij.openapi.ui.Messages;
import com.perforce.p4java.exception.*;
import p4idea.P4Logger;
import p4idea.PerforcePlugin;
import p4idea.perforce.PluginSettings;

import java.net.URISyntaxException;

public class UserInput
{
  private static final UserInput INSTANCE = new UserInput();

  private UserInput()
  {
  }

  public static final UserInput getInstance()
  {
    return INSTANCE;
  }

  public boolean requestCredentials()
  {
    final PluginSettings settings = PerforcePlugin.getInstance().getState();
    final String msg = String.format( "Perforce Password for User %s", settings.getP4user() );
    final String title = "Login to Perforce";

    String password = Messages.showPasswordDialog( msg, title );
    try
    {
      settings.login( password );
      return true;
    }
    catch ( ConnectionException | RequestException e )
    {
      final String error = "Error Connecting to Perforce";
      P4Logger.getInstance().error( error, e );
      Messages.showErrorDialog( e.getMessage(), error );
    }
    catch ( AccessException e )
    {
      final String error = String.format( "Invalid Credentials for User %s", settings.getP4user() );
      P4Logger.getInstance().error( error, e );
      Messages.showErrorDialog( e.getMessage(), error );
    }
    catch ( ConfigException | ResourceException | NoSuchObjectException | URISyntaxException e )
    {
      final String error = "Perforce Server Error";
      P4Logger.getInstance().error( error, e );
      Messages.showErrorDialog( e.getMessage(), error );
    }
    return false;
  }

  public void requestSettings()
  {
    final String title = "Perforce Settings";
    PluginSettings settings = PerforcePlugin.getInstance().getState();

    String port = promptForText( "Server", title, settings.getP4port() );
    if ( !isEmpty( port ) )
    {
      settings.setP4port( port );
    }

    String client = promptForText( "Client Spec", title, settings.getP4client() );
    if ( !isEmpty( client ) )
    {
      settings.setP4client( client );
    }

    String user = promptForText( "Username", title, settings.getP4user() );
    if ( !isEmpty( user ) )
    {
      settings.setP4user( user );
    }
  }

  private String promptForText( String prompt, String title, String defaultValue )
  {
    return Messages.showInputDialog( prompt, title, Messages.getQuestionIcon(), defaultValue, null );
  }

  private boolean isEmpty( String text )
  {
    return null == text || "".equals( text.trim() );
  }
}
