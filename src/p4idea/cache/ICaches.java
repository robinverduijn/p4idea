package p4idea.cache;

import com.google.common.collect.Lists;
import com.perforce.p4java.exception.AccessException;
import com.perforce.p4java.exception.ConnectionException;
import p4idea.P4Logger;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class ICaches
{
  public static <T> void putEntries( ICache<T> cache, Collection<T> entries )
  {
    checkNotNull( entries );
    for ( T entry : entries )
    {
      cache.putEntry( entry );
    }
  }

  public static <T> List<T> makeCachedCall( ICache<T> cache, List<T> input, IListInvoker<T> args ) throws
      ConnectionException, AccessException
  {
    checkNotNull( input );

    // TODO: properly deal with empty case by caching it as well?
    boolean wasEmpty = input.isEmpty();

    List<T> alreadyCached = filterCachedEntries( cache, input );
    if ( wasEmpty || !input.isEmpty() )
    {
      List<T> fresh = args.applyToList( input );
      putEntries( cache, fresh );
      alreadyCached.addAll( fresh );
    }
    return alreadyCached;
  }

  public static <T> List<T> filterCachedEntries( ICache<T> cache, List<T> input )
  {
    checkNotNull( input );
    List<T> cachedEntries = Lists.newArrayList();

    Iterator<T> iter = input.iterator();
    while ( iter.hasNext() )
    {
      T entry = iter.next();
      ICache.CacheKey<T> key = cache.getCacheKey( entry );
      T cachedEntry = cache.getEntry( key );
      if ( null != cachedEntry )
      {
        cachedEntries.add( cachedEntry );
        iter.remove();
      }
    }
    return cachedEntries;
  }

  public static interface IListInvoker<T>
  {
    List<T> applyToList( List<T> list ) throws ConnectionException, AccessException;
  }
}
