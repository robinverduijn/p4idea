package p4idea.vcs;

import com.intellij.openapi.vcs.RepositoryLocation;
import com.intellij.openapi.vcs.VcsException;
import com.perforce.p4java.core.file.IFileSpec;
import p4idea.P4Logger;

public class P4RepositoryLocation implements RepositoryLocation
{
  private final IFileSpec _fileSpec;

  private P4RepositoryLocation( IFileSpec fileSpec )
  {
    _fileSpec = fileSpec;
  }

  public static P4RepositoryLocation create( IFileSpec fileSpec )
  {
    if ( null == fileSpec.getDepotPathString() )
    {
      final String msg = "Unable to determine repository location for %s";
      P4Logger.getInstance().log( String.format( msg, fileSpec ) );
      return null;
    }
    return new P4RepositoryLocation( fileSpec );
  }

  @Override
  public String toPresentableString()
  {
    return _fileSpec.getPreferredPathString();
  }

  @Override
  public String getKey()
  {
    return _fileSpec.getDepotPathString();
  }

  @Override
  public void onBeforeBatch() throws VcsException
  {
  }

  @Override
  public void onAfterBatch()
  {
  }
}
