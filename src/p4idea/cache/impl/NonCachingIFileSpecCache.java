package p4idea.cache.impl;

import com.perforce.p4java.core.file.IFileSpec;

public class NonCachingIFileSpecCache extends AbstractICache<IFileSpec>
{
  public NonCachingIFileSpecCache( final String name, final int initialSize, final long ttl )
  {
    super( name, initialSize, ttl );
  }

  @Override
  public IFileSpec getEntry( ICacheKey<IFileSpec> key )
  {
    return null;
  }

  @Override
  public IFileSpec putEntry( IFileSpec entry )
  {
    return null;
  }

  @Override
  public boolean flushEntry( IFileSpec entry )
  {
    return false;
  }

  @Override
  public void flush()
  {
  }

  @Override
  public ICacheKey<IFileSpec> getCacheKey( IFileSpec entry )
  {
    return new IFileSpecHashingCacheKey( entry );
  }
}
