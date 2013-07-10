package net.rvd.perforce;

import com.perforce.p4java.client.IClient;
import com.perforce.p4java.exception.AccessException;
import com.perforce.p4java.exception.ConnectionException;
import com.perforce.p4java.server.*;
import net.rvd.idea.PluginLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class P4Wrapper
{
  private static final P4Wrapper INSTANCE = new P4Wrapper();
  protected List<String> _messages = new ArrayList<>();
  private IServer _server;
  private String _clientSpec;
  private String _p4port;
  private File _p4root;
  private String _username;
  private boolean _autoDisconnect = false;

  P4Wrapper()
  {
  }

  public static P4Wrapper getInstance()
  {
    return INSTANCE;
  }

  public P4Wrapper initialize( PluginSettings settings ) throws ConnectionException, AccessException
  {
    disconnect();

    _clientSpec = settings.getP4client();
    _p4port = settings.getP4port();
    _p4root = null;
    _username = settings.getP4user();

    return this;
  }

  public File getP4Root()
  {
    if ( null == _p4root )
    {
      try
      {
        _p4root = new File( getP4Server().getCurrentClient().getRoot() );
      }
      catch( Exception e )
      {
        PluginLogger.error( "Unable to determine P4 root", e );
      }
    }
    return _p4root;
  }

  public void login( String password ) throws Exception
  {
    try
    {
      getP4Server( password );
    }
    finally
    {
      attemptDisconnect();
    }
  }

  /**
   * Returns a Perforce server connection for the Perforce server. The connection will not be authenticated unless an
   * (optional null) password is specified. The caller is responsible for calling {@link IServer#disconnect()} on the
   * returned connection when it is no longer needed.
   */
  protected IServer getP4Server() throws Exception
  {
    return getP4Server( null );
  }

  private IServer getP4Server( String password ) throws Exception
  {
    if ( null != _server )
    {
      return _server;
    }
    _server = ServerFactory.getServer( String.format( "p4java://%s", _p4port ), null );
    _server.setUserName( _username );

    _server.connect();

    if ( null != password )
    {
      // Password never gets stored, just used here for authentication
      _server.login( password );
    }

    if ( null != _clientSpec )
    {
      IClient client = _server.getClient( _clientSpec );
      _server.setCurrentClient( client );
    }

    return _server;
  }

  public void disconnect() throws ConnectionException, AccessException
  {
    disconnect( true );
  }

  protected void attemptDisconnect() throws ConnectionException, AccessException
  {
    disconnect( false );
  }

  private void disconnect( boolean force ) throws ConnectionException, AccessException
  {
    flushMessages();

    if ( !force && !_autoDisconnect )
    {
      return;
    }

    if ( null != _server )
    {
      if ( _server.isConnected() )
      {
        _server.disconnect();
      }
      _server = null;
      flushMessages();
    }
  }

  private void flushMessages()
  {
    for ( String message : _messages )
    {
      PluginLogger.log( "P4: " + message );
    }
    _messages.clear();
  }

  public void showServerInfo() throws Exception
  {
    try
    {
      IServerInfo info = getP4Server().getServerInfo();
      if ( info != null )
      {
        PluginLogger.log( String.format( "Server: %s", info.getServerAddress() ) );
        PluginLogger.log( String.format( "User: %s", info.getUserName() ) );
        PluginLogger.log( String.format( "Client: %s", info.getClientName() ) );
        PluginLogger.log( String.format( "Root: %s", info.getClientRoot() ) );
        PluginLogger.log( String.format( "Version: %s", info.getServerVersion() ) );
        PluginLogger.log( String.format( "Uptime: %s", info.getServerUptime() ) );
        PluginLogger.log( String.format( "Server Root: %s", info.getServerRoot() ) );
      }
      else
      {
        PluginLogger.log( "Unable to determine server info" );
      }
    }
    finally
    {
      attemptDisconnect();
    }
  }
}
