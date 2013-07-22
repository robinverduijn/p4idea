package p4idea.perforce;

import com.perforce.p4java.client.IClient;
import com.perforce.p4java.core.file.IFileSpec;
import com.perforce.p4java.exception.AccessException;
import com.perforce.p4java.exception.ConnectionException;
import p4idea.cache.ICache;
import p4idea.cache.ICaches;
import p4idea.cache.impl.NonCachingIFileSpecCache;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class CachingIClient extends DelegatingIClient
{
  private final ICache<IFileSpec> _haveCache;
  private final ICache<IFileSpec> _whereCache;

  public CachingIClient( IClient client )
  {
    super( client );

    final int initialSize = 10000;
    final int ttl = 15 * 60 * 1000;
    //_haveCache = new LoggingICacheDecorator( new GuavaBasedIFileSpecCache( "p4HaveCache", initialSize, ttl ) );
    //_whereCache = new LoggingICacheDecorator( new GuavaBasedIFileSpecCache( "p4WhereCache", initialSize, ttl ) );
    _haveCache = new NonCachingIFileSpecCache( "p4HaveCache", initialSize, ttl );
    _whereCache = new NonCachingIFileSpecCache( "p4WhereCache", initialSize, ttl );
  }

  @Override
  public List<IFileSpec> haveList( List<IFileSpec> iFileSpecs ) throws ConnectionException, AccessException
  {
    checkNotNull( iFileSpecs );

    HaveInvoker invoker = new HaveInvoker();
    return ICaches.makeCachedCall( _haveCache, iFileSpecs, invoker );
  }

  @Override
  public List<IFileSpec> where( final List<IFileSpec> iFileSpecs ) throws ConnectionException, AccessException
  {
    checkNotNull( iFileSpecs );

    WhereInvoker invoker = new WhereInvoker();
    return ICaches.makeCachedCall( _whereCache, iFileSpecs, invoker );
  }

  public class HaveInvoker implements ICaches.IListInvoker<IFileSpec>
  {
    @Override
    public List<IFileSpec> invokeOn( List<IFileSpec> list ) throws ConnectionException, AccessException
    {
      return _client.haveList( list );
    }
  }

  public class WhereInvoker implements ICaches.IListInvoker<IFileSpec>
  {
    @Override
    public List<IFileSpec> invokeOn( List<IFileSpec> list ) throws ConnectionException, AccessException
    {
      return _client.where( list );
    }
  }
}
