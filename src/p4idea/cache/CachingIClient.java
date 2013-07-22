package p4idea.cache;

import com.perforce.p4java.client.IClient;
import com.perforce.p4java.core.file.IFileSpec;
import com.perforce.p4java.exception.AccessException;
import com.perforce.p4java.exception.ConnectionException;
import p4idea.P4Logger;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class CachingIClient extends DelegatingIClient
{
  private final ICache<IFileSpec> _whereCache;

  public CachingIClient( IClient client )
  {
    super( client );
    _whereCache = new LoggingICacheDecorator( new MapBasedIFileSpecCache() );
  }

  @Override
  public List<IFileSpec> where( final List<IFileSpec> iFileSpecs ) throws ConnectionException, AccessException
  {
    checkNotNull( iFileSpecs );

    WhereArguments args = new WhereArguments();
    return ICaches.makeCachedCall( _whereCache, iFileSpecs, args );
  }

  public class WhereArguments implements ICaches.IListInvoker<IFileSpec>
  {
    @Override
    public List<IFileSpec> applyToList( List<IFileSpec> list ) throws ConnectionException, AccessException
    {
      P4Logger.getInstance().log( "Making a P4.where() call" );
      return _client.where( list );
    }
  }
}
