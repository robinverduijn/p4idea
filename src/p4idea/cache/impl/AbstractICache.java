package p4idea.cache.impl;

import p4idea.cache.ICache;

import static com.google.common.base.Preconditions.checkNotNull;

abstract public class AbstractICache<T> implements ICache<T>
{
  private final String _name;

  @SuppressWarnings("UnusedParameters")
  public AbstractICache( final String name, final int initialSize, final long ttl )
  {
    checkNotNull( name );
    _name = name;
  }

  @Override
  public String getCacheName()
  {
    return _name;
  }
}
