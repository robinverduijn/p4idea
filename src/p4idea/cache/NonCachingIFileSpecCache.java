package p4idea.cache;

import com.perforce.p4java.core.file.IFileSpec;

public class NonCachingIFileSpecCache implements ICache<IFileSpec>
{
  private final String _name;

  public NonCachingIFileSpecCache( String name )
  {
    _name = name;
  }

  @Override
  public String getCacheName()
  {
    return _name;
  }

  @Override
  public IFileSpec getEntry( CacheKey<IFileSpec> key )
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
  public CacheKey<IFileSpec> getCacheKey( IFileSpec entry )
  {
    return new IFileSpecHashingCacheKey( entry );
  }
}
