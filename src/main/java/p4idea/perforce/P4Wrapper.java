package p4idea.perforce;

import com.google.common.collect.Lists;
import com.intellij.openapi.vcs.VcsException;
import com.perforce.p4java.client.IClient;
import com.perforce.p4java.core.*;
import com.perforce.p4java.core.file.FileSpecOpStatus;
import com.perforce.p4java.core.file.IFileSpec;
import com.perforce.p4java.exception.*;
import com.perforce.p4java.impl.generic.core.file.FileSpec;
import com.perforce.p4java.server.*;
import org.jetbrains.annotations.NotNull;
import p4idea.P4Logger;
import p4idea.ui.UserInput;
import p4idea.vcs.PerforceVcs;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

public class P4Wrapper
{
  private static final P4Wrapper P4 = new P4Wrapper();
  private final List<String> _messages = Lists.newArrayList();
  private final boolean _autoDisconnect;
  private final boolean _verbose;
  private final boolean _verboseErrors;
  private IServer _server;
  private P4Settings _settings;
  private File _p4root;

  P4Wrapper()
  {
    _autoDisconnect = false;
    _verbose = true;
    _verboseErrors = true;
  }

  public static P4Wrapper getP4()
  {
    return P4;
  }

  public P4Wrapper initialize( P4Settings settings ) throws ConnectionException, AccessException
  {
    disconnect();

    _p4root = null;
    _settings = settings;

    return this;
  }

  /**
   * Returns a Perforce server connection for the Perforce server. The connection will not be authenticated unless an
   * (optional null) password is specified. The caller is responsible for calling {@link IServer#disconnect()} on the
   * returned connection when it is no longer needed.
   */
  IServer getP4Server() throws AccessException, ConnectionException
  {
    try
    {
      return getP4Server( null );
    }
    catch ( NoSuchObjectException | URISyntaxException | ResourceException | ConfigException | RequestException e )
    {
      throw new ConnectionException( e );
    }
  }

  private IServer getP4Server( String password ) throws ConnectionException, ConfigException, NoSuchObjectException,
      ResourceException, URISyntaxException, RequestException, AccessException
  {
    if ( null != _server )
    {
      return _server;
    }
    if ( null == _settings || _settings.isUnset() )
    {
      throw new ConfigException( "P4 server settings not set" );
    }
    IServer server = ServerFactory.getServer( String.format( "p4java://%s", _settings.getP4port() ), null );
    _server = new CachingIServer( server );
    _server.setUserName( _settings.getP4user() );

    _server.connect();

    if ( null != password )
    {
      // Password never gets stored, just used here for authentication
      _server.login( password );
    }

    if ( null != _settings.getP4client() )
    {
      IClient client = _server.getClient( _settings.getP4client() );
      _server.setCurrentClient( client );
    }

    return _server;
  }

  public void disconnect() throws ConnectionException, AccessException
  {
    disconnect( true );
  }

  void attemptDisconnect() throws ConnectionException, AccessException
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

  File getP4Root() throws ConnectionException, AccessException
  {
    if ( null == _p4root )
    {
      IClient client = getCurrentClient();
      if ( null != client )
      {
        _p4root = new File( client.getRoot() );
      }
    }
    return _p4root;
  }

  public boolean isValidMapping( @NotNull String path ) throws ConnectionException, AccessException
  {
    try
    {
      File root = getP4Root();
      String filePath = new File( path ).getAbsolutePath();
      return null != root && filePath.startsWith( root.getAbsolutePath() );
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public void login( String password ) throws ConnectionException, RequestException, URISyntaxException,
      ResourceException, AccessException, ConfigException, NoSuchObjectException
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

  private void flushMessages()
  {
    for ( String message : _messages )
    {
      P4Logger.getInstance().log( String.format( "P4: %s", message ) );
    }
    _messages.clear();
  }

  private List<IFileSpec> processResults( List<IFileSpec> files )
  {
    for ( IFileSpec file : files )
    {
      if ( file != null )
      {
        String msg = null;
        FileSpecOpStatus status = file.getOpStatus();
        if ( status == FileSpecOpStatus.VALID )
        {
          if ( _verbose )
          {
            msg = file.getStatusMessage();
          }
        }
        else
        {
          msg = String.format( "%s: %s", status, file.getStatusMessage() );
          if ( _verboseErrors )
          {
            if ( status == FileSpecOpStatus.CLIENT_ERROR || status == FileSpecOpStatus.ERROR )
            {
              msg = P4Logger.getInstance().buildException( msg );
            }
          }
        }
        processResult( msg );
      }
    }
    return files;
  }

  void processResult( String result )
  {
    if ( null != result )
    {
      _messages.add( result );
    }
  }

  private IClient getCurrentClient() throws ConnectionException, AccessException
  {
    IClient client = getP4Server().getCurrentClient();
    if ( null == client )
    {
      if ( !PerforceVcs.Instance.getValidator().checkExecutableAndNotifyIfNeeded() )
      {
        throw new AccessException( "Unable to connect to Perforce" );
      }
    }
    return client;
  }

  public void handleP4Exception( String message, P4JavaException e ) throws VcsException
  {
    if ( PerforceVcs.Instance.getValidator().checkExecutableAndNotifyIfNeeded() )
    {
      return;
    }
    throw new VcsException( message, e );
  }

  public IServerInfo showServerInfo() throws ConnectionException, RequestException, AccessException
  {
    try
    {
      IServerInfo info = getP4Server().getServerInfo();
      if ( info != null )
      {
        for ( String line : UserInput.getPerforceInfo( info ) )
        {
          P4Logger.getInstance().log( line );
        }
      }
      else
      {
        P4Logger.getInstance().log( "Unable to determine server info" );
      }
      return info;
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public List<IFileSpec> openForEdit( List<IFileSpec> fileSpecs ) throws P4JavaException
  {
    if ( fileSpecs.isEmpty() )
    {
      return fileSpecs;
    }
    try
    {
      IClient client = getCurrentClient();
      return processResults( client.editFiles( fileSpecs, false, false, -1, null ) );
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public List<IFileSpec> openForAdd( List<IFileSpec> fileSpecs ) throws ConnectionException,
      AccessException
  {
    if ( fileSpecs.isEmpty() )
    {
      return fileSpecs;
    }
    try
    {
      IClient client = getCurrentClient();
      return processResults( client.addFiles( fileSpecs, false, -1, null, false ) );
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public List<IFileSpec> openForDelete( List<IFileSpec> fileSpecs ) throws ConnectionException,
      AccessException
  {
    if ( fileSpecs.isEmpty() )
    {
      return fileSpecs;
    }
    try
    {
      IClient client = getCurrentClient();
      return processResults( client.deleteFiles( fileSpecs, -1, false ) );
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public List<IFileSpec> getOpenFiles() throws ConnectionException, AccessException
  {
    List<IFileSpec> files = Lists.newArrayList();
    return getOpenFiles( files );
  }

  public List<IFileSpec> getOpenFiles( List<IFileSpec> fileSpecs ) throws ConnectionException, AccessException
  {
    try
    {
      return getP4Server().getOpenedFiles( fileSpecs, false, _settings.getP4client(), -1, -1 );
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public List<IFileSpec> getWhere( List<IFileSpec> fileSpecs ) throws ConnectionException, AccessException
  {
    if ( fileSpecs.isEmpty() )
    {
      // We do not allow blanket where() calls, just return empty in that case
      return fileSpecs;
    }
    try
    {
      IClient client = getCurrentClient();
      return processResults( client.where( fileSpecs ) );
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public List<IFileSpec> getHave( List<IFileSpec> fileSpecs ) throws ConnectionException, AccessException
  {
    try
    {
      IClient client = getCurrentClient();
      return client.haveList( fileSpecs );
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public void revert( List<IFileSpec> fileSpecs ) throws ConnectionException,
      AccessException
  {
    if ( fileSpecs.isEmpty() )
    {
      return;
    }
    try
    {
      IClient client = getCurrentClient();
      List<IFileSpec> reverted = client.revertFiles( fileSpecs, false, -1, false, false );
      for ( IFileSpec file : reverted )
      {
        String path = file.getClientPathString();
        if ( null != path )
        {
          P4Logger.getInstance().log( String.format( "Reverted: %s", path ) );
        }
      }
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public boolean checkValidCredentials() throws ConnectionException, AccessException
  {
    try
    {
      getP4Server().getDepots();
      return true;
    }
    catch ( ConnectionException | AccessException | RequestException e )
    {
      return false;
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public IChangelist createChangelist( List<IFileSpec> fileSpecs, String description ) throws P4JavaException
  {
    try
    {
      IClient client = getP4Server().getCurrentClient();
      IChangelist changelist = CoreFactory.createChangelist( client, description, true );
      P4Logger log = P4Logger.getInstance();
      log.log( String.format( "Created changelist %d", changelist.getId() ) );
      processResults( client.reopenFiles( fileSpecs, changelist.getId(), null ) );
      return changelist;
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
      // Not implemented for now
      //List<IFileSpec> fileSpecs = changelist.submit( false );
      List<IFileSpec> fileSpecs = changelist.getFiles( false );
      processResults( fileSpecs );
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public List<IChangelistSummary> getChangelists( String path, int maxCount ) throws ConnectionException,
      RequestException, AccessException
  {
    try
    {
      IFileSpec fileSpec = new FileSpec( path );
      List<IFileSpec> fileSpecs = List.of( fileSpec );
      return getP4Server().getChangelists( maxCount, fileSpecs, null, null, false, true, false, false );
    }
    finally
    {
      attemptDisconnect();
    }
  }
}