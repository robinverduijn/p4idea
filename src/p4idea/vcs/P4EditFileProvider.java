package p4idea.vcs;

import com.intellij.openapi.vcs.EditFileProvider;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.VirtualFile;
import com.perforce.p4java.exception.P4JavaException;
import p4idea.P4Logger;
import p4idea.perforce.P4Wrapper;

public class P4EditFileProvider implements EditFileProvider
{
  @Override
  public void editFiles( VirtualFile[] files ) throws VcsException
  {
    try
    {
      P4Wrapper.getP4().openForEdit( files );
    }
    catch ( P4JavaException e )
    {
      throw new VcsException( e );
    }
  }

  @Override
  public String getRequestText()
  {
    P4Logger.getInstance().log( "getRequestText()" );
    return null;
  }
}
