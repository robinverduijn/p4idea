package p4idea.cache;

import com.perforce.p4java.client.IClient;

public class CachingIClient extends DelegatingIClient
{
  public CachingIClient( IClient client )
  {
    super( client );
  }
}
