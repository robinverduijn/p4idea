package p4idea.cache.impl;

import com.google.common.collect.Maps;
import com.perforce.p4java.core.file.IFileSpec;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class MapBasedIFileSpecCache extends AbstractICache<IFileSpec>
{
  private final Map<Integer, IFileSpec> _entries;

  public MapBasedIFileSpecCache( final String name, final int initialSize, final long ttl )
  {
    super( name, initialSize, ttl );

    _entries = Maps.newConcurrentMap();
  }

  @Override
  public IFileSpec getEntry( ICacheKey<IFileSpec> key )
  {
    checkNotNull( key );
    return _entries.get( key.getKey() );
  }

  @Override
  public IFileSpec putEntry( IFileSpec entry )
  {
    ICacheKey<IFileSpec> key = getCacheKey( entry );
    checkNotNull( key );
    return _entries.put( key.getKey(), entry );
  }

  @Override
  public boolean flushEntry( IFileSpec entry )
  {
    ICacheKey<IFileSpec> key = getCacheKey( entry );
    checkNotNull( key );
    return null != _entries.remove( key.getKey() );
  }

  @Override
  public void flush()
  {
    _entries.clear();
  }

  @Override
  public ICacheKey<IFileSpec> getCacheKey( IFileSpec entry )
  {
    return new IFileSpecHashingCacheKey( entry );
  }
}
