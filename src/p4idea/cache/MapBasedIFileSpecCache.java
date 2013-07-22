package p4idea.cache;

import com.google.common.collect.Maps;
import com.perforce.p4java.core.file.IFileSpec;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class MapBasedIFileSpecCache implements ICache<IFileSpec>
{
  private final Map<Integer, IFileSpec> _entries;
  private final String _name;

  public MapBasedIFileSpecCache( final String name )
  {
    _entries = Maps.newLinkedHashMap();
    _name = name;
  }

  public String getCacheName()
  {
    return _name;
  }

  @Override
  public IFileSpec getEntry( CacheKey<IFileSpec> key )
  {
    checkNotNull( key );
    return _entries.get( key.getKey() );
  }

  @Override
  public IFileSpec putEntry( IFileSpec entry )
  {
    CacheKey<IFileSpec> key = getCacheKey( entry );
    checkNotNull( key );
    return _entries.put( key.getKey(), entry );
  }

  @Override
  public boolean flushEntry( IFileSpec entry )
  {
    CacheKey<IFileSpec> key = getCacheKey( entry );
    checkNotNull( key );
    return null != _entries.remove( key.getKey() );
  }

  @Override
  public void flush()
  {
    _entries.clear();
  }

  @Override
  public CacheKey<IFileSpec> getCacheKey( IFileSpec entry )
  {
    return new IFileSpecHashingCacheKey( entry );
  }
}
