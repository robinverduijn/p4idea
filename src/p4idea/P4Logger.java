package p4idea;

import com.intellij.openapi.diagnostic.Logger;
import com.perforce.p4java.core.file.IFileSpec;

public class P4Logger
{
  private static final P4Logger INSTANCE = new P4Logger();

  private P4Logger()
  {
  }

  public static P4Logger getInstance()
  {
    return INSTANCE;
  }

  public void log( String message )
  {
    Logger.getInstance( "P4" ).info( message );
  }

  public void error( String message, Throwable t )
  {
    Logger.getInstance( "P4" ).error( message, t );
  }

  public String getP4DebugStatus( IFileSpec status )
  {
    StringBuilder msg = new StringBuilder();
    msg.append( "action: " ).append( status.getAction() ).append( ", " );
    msg.append( "depotPath: " ).append( status.getDepotPathString() ).append( ", " );
    msg.append( "localPath: " ).append( status.getLocalPathString() ).append( ", " );
    msg.append( "clientPath: " ).append( status.getClientPathString() ).append( ", " );
    msg.append( "originalPath: " ).append( status.getOriginalPathString() ).append( ", " );
    msg.append( "changelist: " ).append( status.getChangelistId() ).append( ", " );
    msg.append( "startRevision: " ).append( status.getStartRevision() ).append( ", " );
    msg.append( "endRevision: " ).append( status.getEndRevision() );
    return msg.toString();
  }
}
