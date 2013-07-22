package p4idea.cache;

import com.google.common.collect.Maps;
import com.perforce.p4java.core.file.IFileSpec;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class MapBasedIFileSpecCache implements ICache<IFileSpec>
{
  private final Map<Integer, IFileSpec> _entries;

  public MapBasedIFileSpecCache()
  {
    _entries = Maps.newLinkedHashMap();
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
    IFileSpecCacheKey key = getCacheKey( entry );
    checkNotNull( key );
    return _entries.put( key.getKey(), entry );
  }

  @Override
  public boolean flushEntry( IFileSpec entry )
  {
    IFileSpecCacheKey key = getCacheKey( entry );
    checkNotNull( key );
    return null != _entries.remove( key.getKey() );
  }

  @Override
  public void flush()
  {
    _entries.clear();
  }

  @Override
  public IFileSpecCacheKey getCacheKey( IFileSpec entry )
  {
    checkNotNull( entry );

    HashCodeBuilder builder = new HashCodeBuilder();
    builder.append( entry.getPreferredPathString() );
    return new IFileSpecCacheKey( builder.toHashCode() );
  }

  class IFileSpecCacheKey implements CacheKey<IFileSpec>
  {
    private final int _value;

    public IFileSpecCacheKey( int value )
    {
      _value = value;
    }

    public int getKey()
    {
      return _value;
    }
  }
}
