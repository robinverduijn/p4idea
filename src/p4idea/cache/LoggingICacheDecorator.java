package p4idea.cache;

import com.perforce.p4java.core.file.IFileSpec;
import p4idea.P4Logger;

import static com.google.common.base.Preconditions.checkNotNull;

public class LoggingICacheDecorator implements ICache<IFileSpec>
{
  private final ICache<IFileSpec> _cache;
  private final P4Logger _logger;

  public LoggingICacheDecorator( ICache<IFileSpec> cache )
  {
    checkNotNull( cache );
    _cache = cache;
    _logger = P4Logger.getInstance();
  }

  @Override
  public String getCacheName()
  {
    return _cache.getCacheName();
  }

  @Override
  public IFileSpec getEntry( CacheKey<IFileSpec> key )
  {
    IFileSpec result = _cache.getEntry( key );
    if ( null == result )
    {
      log( String.format( "Cache miss for %d", key.getKey() ) );
    }
    else
    {
      log( String.format( "Cache hit for %s: %d", result.getPreferredPathString(), key.getKey() ) );
    }
    return result;
  }

  @Override
  public IFileSpec putEntry( IFileSpec entry )
  {
    IFileSpec result = _cache.putEntry( entry );
    int key = _cache.getCacheKey( entry ).getKey();
    if ( null != result )
    {
      log( String.format( "Cached existing %s: %d", entry.getPreferredPathString(), key ) );
    }
    else
    {
      log( String.format( "Cached new entry for %s: %d", entry.getPreferredPathString(), key ) );
    }
    return result;
  }

  @Override
  public boolean flushEntry( IFileSpec entry )
  {
    boolean result = _cache.flushEntry( entry );
    int key = _cache.getCacheKey( entry ).getKey();
    if ( result )
    {
      log( String.format( "Flushed %s: %d", entry.getPreferredPathString(), key ) );
    }
    else
    {
      log( String.format( "Could not flush %s: %d", entry.getPreferredPathString(), key ) );
    }
    return result;
  }

  @Override
  public void flush()
  {
    _cache.flush();
    log( String.format( "Flushed" ) );
  }

  @Override
  public CacheKey<IFileSpec> getCacheKey( IFileSpec entry )
  {
    return _cache.getCacheKey( entry );
  }

  private void log( String message )
  {
    _logger.log( String.format( "[%s]: %s", _cache.getCacheName(), message ) );
  }
}
