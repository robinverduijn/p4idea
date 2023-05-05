package p4idea.vcs;

import com.intellij.openapi.vcs.VcsDirectoryMapping;
import com.intellij.openapi.vcs.VcsKey;
import com.intellij.openapi.vcs.VcsRootChecker;
import com.perforce.p4java.exception.AccessException;
import com.perforce.p4java.exception.ConnectionException;
import org.jetbrains.annotations.NotNull;
import p4idea.P4Logger;
import p4idea.perforce.P4Wrapper;

import java.util.Collection;
import java.util.Collections;

class P4RootChecker extends VcsRootChecker
{
  @Override
  public boolean validateRoot( @NotNull String path )
  {
    try
    {
      return !P4Wrapper.getP4().isValidMapping( path );
    }
    catch ( ConnectionException | AccessException e )
    {
      P4Logger.getInstance().error( "Error determining P4 mapping", e );
      return true;
    }
  }

  @Override
  public @NotNull VcsKey getSupportedVcs()
  {
    return PerforceVcs.getKey();
  }
}
