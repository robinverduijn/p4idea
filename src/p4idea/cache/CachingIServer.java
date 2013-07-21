package p4idea.cache;

import com.perforce.p4java.client.IClient;
import com.perforce.p4java.exception.*;
import com.perforce.p4java.server.IServer;

public class CachingIServer extends DelegatingIServer
{
  public CachingIServer( IServer server )
  {
    super( server );
  }

  public IClient getClient( String s ) throws ConnectionException, RequestException, AccessException
  {
    IClient client = _server.getClient( s );
    if ( null == client || client instanceof DelegatingIClient )
    {
      return client;
    }
    return new CachingIClient( client );
  }
}
