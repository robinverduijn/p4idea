package p4idea.perforce;

import com.perforce.p4java.client.IClient;
import com.perforce.p4java.core.file.IFileSpec;
import com.perforce.p4java.exception.*;
import com.perforce.p4java.server.IServer;
import p4idea.cache.ICache;
import p4idea.cache.ICaches;
import p4idea.cache.impl.NonCachingIFileSpecCache;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class CachingIServer extends DelegatingIServer
{
  private final ICache<IFileSpec> _openedCache;

  public CachingIServer( IServer server )
  {
    super( server );

    final int initialSize = 10000;
    final int ttl = 15 * 60 * 1000;
    //_openedCache = new LoggingICacheDecorator( new GuavaBasedIFileSpecCache( "p4OpenedCache", initialSize, ttl ) );
    _openedCache = new NonCachingIFileSpecCache( "p4OpenedCache", initialSize, ttl );
  }

  public IClient getClient( String s ) throws ConnectionException, RequestException, AccessException
  {
    IClient client = _server.getClient( s );
    if ( null == client || client instanceof DelegatingIClient )
    {
      return client;
    }
    return new CachingIClient( client );
  }

  @Override
  public List<IFileSpec> getOpenedFiles( List<IFileSpec> iFileSpecs, boolean allClients, String clientName,
                                         int maxFiles,
                                         int changeListId ) throws ConnectionException, AccessException
  {
    checkNotNull( iFileSpecs );

    OpenedFilesInvoker invoker = new OpenedFilesInvoker( allClients, clientName, maxFiles, changeListId );
    return ICaches.makeCachedCall( _openedCache, iFileSpecs, invoker );
  }

  private class OpenedFilesInvoker implements ICaches.IListInvoker<IFileSpec>
  {
    final boolean _allClients;
    final String _clientName;
    final int _maxFiles;
    final int _changeListId;

    public OpenedFilesInvoker( boolean allClients, String clientName, int maxFiles, int changeListId )
    {
      _allClients = allClients;
      _clientName = clientName;
      _maxFiles = maxFiles;
      _changeListId = changeListId;
    }

    @Override
    public List<IFileSpec> invokeOn( List<IFileSpec> list ) throws ConnectionException, AccessException
    {
      return _server.getOpenedFiles( list, _allClients, _clientName, _maxFiles, _changeListId );
    }
  }
}
