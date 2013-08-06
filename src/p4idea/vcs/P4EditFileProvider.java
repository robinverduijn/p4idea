package p4idea.vcs;

import com.intellij.openapi.vcs.EditFileProvider;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.VirtualFile;
import com.perforce.p4java.core.file.FileSpecOpStatus;
import com.perforce.p4java.core.file.IFileSpec;
import com.perforce.p4java.exception.P4JavaException;
import p4idea.FileLists;
import p4idea.P4Logger;
import p4idea.perforce.P4Wrapper;

import java.util.List;

class P4EditFileProvider implements EditFileProvider
{
  @Override
  public void editFiles( VirtualFile[] files ) throws VcsException
  {
    try
    {
      List<IFileSpec> opened = P4Wrapper.getP4().openForEdit( FileLists.fromVirtualFiles( files ) );
      for ( IFileSpec file : opened )
      {
        String path = file.getClientPathString();
        if ( null != path )
        {
          if ( file.getOpStatus() == FileSpecOpStatus.VALID )
          {
            P4Logger.getInstance().log( String.format( "Open for edit: %s", path ) );
          }
        }
      }
    }
    catch ( P4JavaException e )
    {
      P4Wrapper.getP4().handleP4Exception( "Error opening files for edit", e );
    }
  }

  @Override
  public String getRequestText()
  {
    P4Logger.getInstance().log( "getRequestText()" );
    return null;
  }
}
