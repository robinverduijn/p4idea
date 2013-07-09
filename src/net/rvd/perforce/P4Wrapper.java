package net.rvd.perforce;

import com.perforce.p4java.client.IClient;
import com.perforce.p4java.core.*;
import com.perforce.p4java.core.file.IFileSpec;
import com.perforce.p4java.exception.*;
import com.perforce.p4java.impl.generic.core.file.FileSpec;
import com.perforce.p4java.server.*;
import net.rvd.idea.Logger;

import java.io.File;
import java.util.*;

public class P4Wrapper
{
  private static final String P4_SERVER = "p4java://perforce.local:1666";
  private static final String P4_USERNAME = "guest";
  private static final String P4_PASSWORD = "guest";

  private static final P4Wrapper INSTANCE = new P4Wrapper( null, null, null, true );

  private IServer _server;
  private String _clientSpec;
  private String _username;
  private String _password;
  private boolean _autoDisconnect;

  private List<String> _messages;

  public static P4Wrapper getInstance()
  {
    return INSTANCE;
  }

  public P4Wrapper( String clientSpec, String username, String password, boolean autoDisconnect )
  {
    _messages = new ArrayList<String>();

    if ( null == username || null == password )
    {
      _username = P4_USERNAME;
      _password = P4_PASSWORD;
    }
    else
    {
      _username = username;
      _password = password;
    }

    _clientSpec = clientSpec;
    _autoDisconnect = autoDisconnect;
  }

  /**
   * Returns a Perforce server connection for the Perforce server. The connection will have been authenticated as the
   * "guest" user. The caller is responsible for calling {@link IServer#disconnect()} on the returned connection
   * when it is no longer needed.
   */
  private IServer getP4Server() throws Exception
  {
    if ( null != _server )
    {
      return _server;
    }
    _server = ServerFactory.getServer( P4_SERVER, null );
    _server.setUserName( _username );

    _server.connect();
    _server.login( _password );

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

  private void attemptDisconnect() throws ConnectionException, AccessException
  {
    disconnect( false );
  }

  private void disconnect( boolean force ) throws ConnectionException, AccessException
  {
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

      for ( String message : _messages )
      {
        Logger.log( "P4: " + message );
      }
      _messages.clear();
    }
  }

  public List<String> getChangelistFiles( int changelist ) throws Exception
  {
    try
    {
      List<String> files = new ArrayList<String>();
      for ( IFileSpec file : getP4Server().getChangelistFiles( changelist ) )
      {
        files.add( file.getDepotPathString() );
      }
      return files;
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public IUser getUser( String id ) throws Exception
  {
    try
    {
      return getP4Server().getUser( id );
    }
    catch ( Exception e )
    {
      Logger.log( String.format( "P4: error looking up user \"%s\"", id ), e );
      return null;
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public void showServerInfo() throws Exception
  {
    try
    {
      IServerInfo info = getP4Server().getServerInfo();
      if ( info != null )
      {
        Logger.log( String.format( "Version: %s", info.getServerVersion() ) );
        Logger.log( String.format( "Uptime: %s", info.getServerUptime() ) );
        Logger.log( String.format( "Server Root: %s", info.getServerRoot() ) );
        Logger.log( String.format( "Client: %s", info.getClientName() ) );
        Logger.log( String.format( "Root: %s", info.getClientRoot() ) );
        Logger.log( String.format( "User: %s", info.getUserName() ) );
      }
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public IChangelist createChangelist( String description ) throws Exception
  {
    try
    {
      IClient client = getP4Server().getCurrentClient();
      return CoreFactory.createChangelist( client, description, true );
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public void revertChangelist( IChangelist changelist ) throws Exception
  {
    revertChangelist( changelist.getId() );
  }

  public void revertChangelist( int changelist ) throws Exception
  {
    try
    {
      String result = getP4Server().deletePendingChangelist( changelist );
      _messages.add( result );
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public void submitChangelist( IChangelist changelist ) throws ConnectionException, RequestException, AccessException
  {
    try
    {
      changelist.submit( false );
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public List<IFileSpec> openForAdd( IChangelist changelist, List<File> files ) throws Exception
  {
    List<IFileSpec> fileSpecs = new ArrayList<>();
    for ( File file : files )
    {
      fileSpecs.add( new FileSpec( file.getAbsolutePath() ) );
    }
    try
    {
      IClient client = getP4Server().getCurrentClient();
      return client.addFiles( fileSpecs, false, changelist.getId(), null, false );
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public List<IFileSpec> openForEdit( IChangelist changelist, File file ) throws Exception
  {
    return openForEdit( changelist, Arrays.asList( file ) );
  }

  public List<IFileSpec> openForEdit( IChangelist changelist, List<File> files ) throws Exception
  {
    List<IFileSpec> fileSpecs = new ArrayList<>();
    for ( File file : files )
    {
      fileSpecs.add( new FileSpec( file.getAbsolutePath() ) );
    }
    try
    {
      IClient client = getP4Server().getCurrentClient();
      return client.editFiles( fileSpecs, false, false, changelist.getId(), null );
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public int revertEdit( File file ) throws Exception
  {
    List<IFileSpec> reverted = revertEdit( Arrays.asList( file ), false );
    if ( reverted.isEmpty() )
    {
      return -1;
    }
    Logger.log( String.format( "Reverted: %s", file ) );
    return reverted.get( 0 ).getChangelistId();
  }

  public List<IFileSpec> revertEdit( List<File> files, boolean revertOnlyUnchanged ) throws Exception
  {
    List<IFileSpec> fileSpecs = new ArrayList<>();
    for ( File file : files )
    {
      fileSpecs.add( new FileSpec( file.getAbsolutePath() ) );
    }

    try
    {
      IClient client = getP4Server().getCurrentClient();
      List<IFileSpec> openedFiles = client.openedFiles( fileSpecs, -1, -1 );
      if ( openedFiles.isEmpty() )
      {
        Logger.log( "File(s) were not open for edit: " + files );
        return Collections.emptyList();
      }

      List<IFileSpec> revertedFiles = client.revertFiles( openedFiles, false, -1, revertOnlyUnchanged, false );
      if ( !revertedFiles.isEmpty() )
      {
        Logger.log( String.format( "Reverted %d file(s)", revertedFiles.size() ) );
      }
      return revertedFiles;
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public List<IChangelistSummary> getChangelists( String path ) throws Exception
  {
    try
    {
      IFileSpec fileSpec = new FileSpec( path );
      List<IFileSpec> fileSpecs = Arrays.asList( fileSpec );
      return getP4Server().getChangelists( -1, fileSpecs, null, null, false, true, false, false );
    }
    finally
    {
      attemptDisconnect();
    }
  }
}
