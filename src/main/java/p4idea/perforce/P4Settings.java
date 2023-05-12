package p4idea.perforce;

import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.perforce.p4java.exception.*;
import com.perforce.p4java.server.IServerInfo;
import org.jetbrains.annotations.NotNull;
import p4idea.P4Logger;

import java.net.URISyntaxException;

@State(name = "P4Settings", storages = { @Storage(value = StoragePathMacros.PRODUCT_WORKSPACE_FILE) })
public class P4Settings implements ProjectComponent, PersistentStateComponent<P4Settings>
{
  private String _p4client;
  private String _p4port;
  private String _p4user;

  public static P4Settings clone( P4Settings settings )
  {
    P4Settings result = new P4Settings();
    XmlSerializerUtil.copyBean( settings, result );
    return result;
  }

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

  @Override
  public void initComponent()
  {
  }

  @Override
  public void disposeComponent()
  {
  }

  @NotNull
  @Override
  public P4Settings getState()
  {
    return this;
  }

  @Override
  public void loadState( @NotNull P4Settings settings )
  {
    if ( null == settings )
    {
      return;
    }
    XmlSerializerUtil.copyBean( settings, this );
    if ( !isUnset() )
    {
      P4Logger.getInstance().log( String.format( "%s", this ) );
      apply();
    }
  }

  public void apply()
  {
    try
    {
      P4Wrapper.getP4().initialize( this );
    }
    catch ( ConnectionException | AccessException e )
    {
      P4Logger.getInstance().error( String.format( "Invalid settings: %s", this ), e );
    }
  }

  public boolean isUnset()
  {
    return isEmpty( _p4port ) || isEmpty( _p4user ) || isEmpty( _p4client );
  }

  private boolean isEmpty( String text )
  {
    return null == text || text.trim().isEmpty();
  }

  public IServerInfo verify() throws ConnectionException, AccessException,
      RequestException
  {
    P4Wrapper p4 = P4Wrapper.getP4().initialize( this );
    return p4.showServerInfo();
  }

  public IServerInfo login( String password ) throws ConnectionException, AccessException, ConfigException,
      ResourceException, NoSuchObjectException, RequestException, URISyntaxException
  {
    P4Wrapper p4 = P4Wrapper.getP4().initialize( this );
    p4.login( password );
    return p4.showServerInfo();
  }
}
