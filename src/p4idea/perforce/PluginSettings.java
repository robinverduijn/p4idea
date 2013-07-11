package p4idea.perforce;

import com.perforce.p4java.exception.*;

import java.net.URISyntaxException;

public class PluginSettings
{
  private String _p4client;
  private String _p4port;
  private String _p4user;

  public String getP4client()
  {
    return _p4client;
  }

  public void setP4client( String p4client )
  {
    _p4client = p4client;
  }

  public String getP4port()
  {
    return _p4port;
  }

  public void setP4port( String p4port )
  {
    _p4port = p4port;
  }

  public String getP4user()
  {
    return _p4user;
  }

  public void setP4user( String p4user )
  {
    _p4user = p4user;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append( getClass().getSimpleName() );
    sb.append( "{ " );
    sb.append( "p4port=" ).append( _p4port );
    sb.append( ", " );
    sb.append( "p4user=" ).append( _p4user );
    sb.append( ", " );
    sb.append( "p4client=" ).append( _p4client );
    sb.append( " }" );
    return sb.toString();
  }

  public boolean isUnset()
  {
    return null == _p4port || null == _p4user || null == _p4client;
  }

  public void verify() throws ConnectionException, AccessException,
      RequestException
  {
    P4Wrapper p4 = P4Wrapper.getInstance().initialize( this );
    p4.showServerInfo();
  }

  public void login( String password ) throws ConnectionException, AccessException, ConfigException,
      ResourceException, NoSuchObjectException, RequestException, URISyntaxException
  {
    P4Wrapper p4 = P4Wrapper.getInstance().initialize( this );
    p4.login( password );
    p4.showServerInfo();
  }
}
