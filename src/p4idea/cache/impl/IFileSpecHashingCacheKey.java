package p4idea.cache.impl;

import com.perforce.p4java.core.file.IFileSpec;
import org.apache.commons.lang.builder.HashCodeBuilder;
import p4idea.cache.ICache;

import static com.google.common.base.Preconditions.checkNotNull;

public class IFileSpecHashingCacheKey implements ICache.ICacheKey<IFileSpec>
{
  private final int _value;

  public IFileSpecHashingCacheKey( IFileSpec entry )
  {
    checkNotNull( entry );

    HashCodeBuilder builder = new HashCodeBuilder();
    builder.append( entry.getPreferredPathString() );
    _value = builder.toHashCode();
  }

  public int getKey()
  {
    return _value;
  }
}
