package p4idea.cache;

public interface ICache<T>
{
  T getEntry( CacheKey<T> key );

  T putEntry( T entry );

  boolean flushEntry( T entry );

  void flush();

  CacheKey<T> getCacheKey( T entry );

  interface CacheKey<T>
  {
    public int getKey();
  }
}
