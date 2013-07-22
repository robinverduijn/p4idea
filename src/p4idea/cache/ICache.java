package p4idea.cache;

public interface ICache<T>
{
  String getCacheName();

  T getEntry( ICacheKey<T> key );

  T putEntry( T entry );

  boolean flushEntry( T entry );

  void flush();

  ICacheKey<T> getCacheKey( T entry );

  interface ICacheKey<T>
  {
    public int getKey();
  }
}
