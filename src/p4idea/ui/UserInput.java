package p4idea.ui;

import com.intellij.openapi.ui.Messages;
import com.perforce.p4java.exception.*;
import com.perforce.p4java.server.IServerInfo;
import p4idea.P4Logger;
import p4idea.perforce.P4Settings;
import p4idea.perforce.P4Wrapper;

import javax.swing.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class UserInput
{
  public static IServerInfo requestCredentials( final P4Settings settings )
  {
    final String msg = String.format( "Perforce Password for User %s", settings.getP4user() );
    final String title = "Login to Perforce";

    String password = Messages.showPasswordDialog( msg, title );

    IServerInfo result = null;
    try
    {
      result = settings.login( password );
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

    if ( null == result )
    {
      try
      {
        P4Wrapper.getP4().disconnect();
      }
      catch ( ConnectionException | AccessException e )
      {
        P4Logger.getInstance().error( "Error disconnecting from Perforce", e );
      }
    }
    return result;
  }

  public static void displayPerforceInfo( JPanel panel, IServerInfo info )
  {
    StringBuilder message = new StringBuilder();
    message.append( "<html>" );
    for ( String line : getPerforceInfo( info ) )
    {
      message.append( line ).append( "<br>" );
    }
    message.append( "</html>" );

    final String title = "Perforce Connection Successful";
    Messages.showInfoMessage( panel, message.toString(), title );
  }

  public static List<String> getPerforceInfo( IServerInfo info )
  {
    List<String> message = new ArrayList<>();
    message.add( String.format( "Server: %s", info.getServerAddress() ) );
    message.add( String.format( "User: %s", info.getUserName() ) );
    message.add( String.format( "Client: %s", info.getClientName() ) );
    message.add( String.format( "Root: %s", info.getClientRoot() ) );
    message.add( String.format( "Version: %s", info.getServerVersion() ) );
    message.add( String.format( "Uptime: %s", info.getServerUptime() ) );
    message.add( String.format( "Server Root: %s", info.getServerRoot() ) );

    return message;
  }
}
