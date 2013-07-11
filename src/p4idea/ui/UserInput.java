package p4idea.ui;

import com.intellij.openapi.ui.Messages;
import com.perforce.p4java.exception.*;
import com.perforce.p4java.server.IServerInfo;
import p4idea.P4Logger;
import p4idea.perforce.P4Settings;

import javax.swing.*;
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

  public IServerInfo requestCredentials( final P4Settings settings )
  {
    final String msg = String.format( "Perforce Password for User %s", settings.getP4user() );
    final String title = "Login to Perforce";

    String password = Messages.showPasswordDialog( msg, title );
    try
    {
      return settings.login( password );
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
    return null;
  }

  public void displayPerforceInfo( JPanel panel, IServerInfo info )
  {
    final String title = "Perforce Connection Successful";
    StringBuilder message = new StringBuilder();
    message.append( "<html>" );
    message.append( String.format( "Server: %s<br>", info.getServerAddress() ) );
    message.append( String.format( "User: %s<br>", info.getUserName() ) );
    message.append( String.format( "Client: %s<br>", info.getClientName() ) );
    message.append( String.format( "Root: %s<br>", info.getClientRoot() ) );
    message.append( String.format( "Version: %s<br>", info.getServerVersion() ) );
    message.append( String.format( "Uptime: %s<br>", info.getServerUptime() ) );
    message.append( String.format( "Server Root: %s", info.getServerRoot() ) );
    message.append( "</html>" );

    Messages.showInfoMessage( panel, message.toString(), title );
  }
}