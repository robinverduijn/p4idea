package p4idea.perforce;

import com.google.common.collect.Lists;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vfs.VirtualFile;
import com.perforce.p4java.client.IClient;
import com.perforce.p4java.core.file.FileSpecOpStatus;
import com.perforce.p4java.core.file.IFileSpec;
import com.perforce.p4java.exception.*;
import com.perforce.p4java.impl.generic.core.file.FileSpec;
import com.perforce.p4java.server.*;
import org.jetbrains.annotations.NotNull;
import p4idea.P4Logger;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

public class P4Wrapper
{
  private static final P4Wrapper P4 = new P4Wrapper();
  protected final List<String> _messages = Lists.newArrayList();
  private final boolean _autoDisconnect;
  private final boolean _verboseErrors;
  private IServer _server;
  private String _clientSpec;
  private String _p4port;
  private File _p4root;
  private String _username;

  P4Wrapper()
  {
    _autoDisconnect = false;
    _verboseErrors = true;
  }

  public static P4Wrapper getP4()
  {
    return P4;
  }

  public P4Wrapper initialize( P4Settings settings ) throws ConnectionException, AccessException
  {
    disconnect();

    _clientSpec = settings.getP4client();
    _p4port = settings.getP4port();
    _p4root = null;
    _username = settings.getP4user();

    return this;
  }

  /**
   * Returns a Perforce server connection for the Perforce server. The connection will not be authenticated unless an
   * (optional null) password is specified. The caller is responsible for calling {@link IServer#disconnect()} on the
   * returned connection when it is no longer needed.
   */
  protected IServer getP4Server() throws AccessException, ConnectionException
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

  private File getP4Root() throws ConnectionException, AccessException
  {
    if ( null == _p4root )
    {
      IClient client = getP4Server().getCurrentClient();
      if ( null != client )
      {
        _p4root = new File( client.getRoot() );
      }
    }
    return _p4root;
  }

  public boolean isInvalidMapping( @NotNull String path ) throws ConnectionException, AccessException
  {
    try
    {
      File root = getP4Root();
      String filePath = new File( path ).getAbsolutePath();
      return null == root || !filePath.startsWith( root.getAbsolutePath() );
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
      P4Logger.getInstance().log( "P4: " + message );
    }
    _messages.clear();
  }

  protected List<IFileSpec> fromVirtualFiles( VirtualFile[] virtualFiles )
  {
    List<IFileSpec> fileSpecs = Lists.newArrayList();
    for ( VirtualFile virtualFile : virtualFiles )
    {
      fileSpecs.add( new FileSpec( virtualFile.getPath() ) );
    }
    return fileSpecs;
  }

  protected List<IFileSpec> fromFilePaths( Collection<FilePath> filePaths )
  {
    List<IFileSpec> fileSpecs = Lists.newArrayList();
    for ( FilePath filePath : filePaths )
    {
      fileSpecs.add( new FileSpec( filePath.getPath() ) );
    }
    return fileSpecs;
  }

  private Collection<IFileSpec> processResults( Collection<IFileSpec> files )
  {
    for ( IFileSpec file : files )
    {
      if ( file != null )
      {
        String msg;
        FileSpecOpStatus status = file.getOpStatus();
        if ( status != FileSpecOpStatus.VALID )
        {
          msg = String.format( "%s: %s", status, file.getStatusMessage() );
          if ( _verboseErrors )
          {
            msg = buildException( msg );
          }
        }
        else
        {
          msg = file.getStatusMessage();
        }
        if ( null != msg )
        {
          _messages.add( msg );
        }
      }
    }
    return files;
  }

  private String buildException( String msg )
  {
    StringWriter sw = new StringWriter();
    try ( PrintWriter pw = new PrintWriter( sw ) )
    {
      new Exception( msg ).printStackTrace( pw );
      return sw.toString();
    }
  }

  public IServerInfo showServerInfo() throws ConnectionException, RequestException, AccessException
  {
    try
    {
      IServerInfo info = getP4Server().getServerInfo();
      if ( info != null )
      {
        P4Logger logger = P4Logger.getInstance();
        logger.log( String.format( "Server: %s", info.getServerAddress() ) );
        logger.log( String.format( "User: %s", info.getUserName() ) );
        logger.log( String.format( "Client: %s", info.getClientName() ) );
        logger.log( String.format( "Root: %s", info.getClientRoot() ) );
        logger.log( String.format( "Version: %s", info.getServerVersion() ) );
        logger.log( String.format( "Uptime: %s", info.getServerUptime() ) );
        logger.log( String.format( "Server Root: %s", info.getServerRoot() ) );
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

  public Collection<IFileSpec> openForEdit( VirtualFile[] files ) throws P4JavaException
  {
    List<IFileSpec> fileSpecs = fromVirtualFiles( files );
    try
    {
      IClient client = getP4Server().getCurrentClient();
      return processResults( client.editFiles( fileSpecs, false, false, -1, null ) );
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public Collection<IFileSpec> openForAdd( Collection<FilePath> files ) throws ConnectionException,
      AccessException
  {
    List<IFileSpec> fileSpecs = fromFilePaths( files );
    try
    {
      IClient client = getP4Server().getCurrentClient();
      return processResults( client.addFiles( fileSpecs, false, -1, null, false ) );
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public Collection<IFileSpec> openForDelete( Collection<FilePath> files ) throws ConnectionException,
      AccessException
  {
    List<IFileSpec> fileSpecs = fromFilePaths( files );
    try
    {
      IClient client = getP4Server().getCurrentClient();
      return processResults( client.deleteFiles( fileSpecs, -1, false ) );
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public Collection<IFileSpec> getStatus( Collection<FilePath> files ) throws ConnectionException, AccessException
  {
    List<IFileSpec> fileSpecs = fromFilePaths( files );
    try
    {
      //return processResults( getP4Server().getOpenedFiles( fileSpecs, false, _clientSpec, -1, -1 ) );
      IClient client = getP4Server().getCurrentClient();
      return client.haveList( fileSpecs );
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public Collection<IFileSpec> revert( Collection<FilePath> files, boolean quiet ) throws ConnectionException,
      AccessException
  {
    List<IFileSpec> fileSpecs = fromFilePaths( files );
    try
    {
      IClient client = getP4Server().getCurrentClient();
      List<IFileSpec> revertedFiles = client.revertFiles( fileSpecs, false, -1, false, false );
      if ( quiet )
      {
        return revertedFiles;
      }
      return processResults( revertedFiles );
    }
    finally
    {
      attemptDisconnect();
    }
  }
}