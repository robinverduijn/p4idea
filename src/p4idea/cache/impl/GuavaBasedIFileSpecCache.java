package p4idea.cache.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.perforce.p4java.core.file.IFileSpec;

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

public class GuavaBasedIFileSpecCache extends AbstractICache<IFileSpec>
{
  private final Cache<Integer, IFileSpec> _cache;

  public GuavaBasedIFileSpecCache( final String name, final int initialSize, final long ttl )
  {
    super( name, initialSize, ttl );

    _cache = CacheBuilder.newBuilder()
        .expireAfterAccess( ttl, TimeUnit.MILLISECONDS )
        .initialCapacity( initialSize )
        .build();
  }

  @Override
  public IFileSpec getEntry( ICacheKey<IFileSpec> key )
  {
    checkNotNull( key );
    return _cache.getIfPresent( key.getKey() );
  }

  @Override
  public IFileSpec putEntry( IFileSpec entry )
  {
    ICacheKey<IFileSpec> key = getCacheKey( entry );
    checkNotNull( key );
    _cache.put( key.getKey(), entry );
    return null;
  }

  @Override
  public boolean flushEntry( IFileSpec entry )
  {
    ICacheKey<IFileSpec> key = getCacheKey( entry );
    checkNotNull( key );
    _cache.invalidate( key.getKey() );
    return false;
  }

  @Override
  public void flush()
  {
    _cache.invalidateAll();
  }

  @Override
  public ICacheKey<IFileSpec> getCacheKey( IFileSpec entry )
  {
    return new IFileSpecHashingCacheKey( entry );
  }
}
