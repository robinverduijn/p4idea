package p4idea.vcs;

import com.intellij.openapi.vcs.VcsDirectoryMapping;
import com.intellij.openapi.vcs.VcsRootChecker;
import com.perforce.p4java.exception.AccessException;
import com.perforce.p4java.exception.ConnectionException;
import org.jetbrains.annotations.NotNull;
import p4idea.P4Logger;
import p4idea.perforce.P4Wrapper;

import java.util.Collection;
import java.util.Collections;

public class P4RootChecker implements VcsRootChecker
{
  @NotNull
  @Override
  public Collection<String> getUnregisteredRoots()
  {
    return Collections.emptyList();
  }

  @Override
  public boolean isInvalidMapping( @NotNull VcsDirectoryMapping mapping )
  {
    return isInvalidMapping( mapping.systemIndependentPath() );
  }

  public boolean isInvalidMapping( @NotNull String path )
  {
    try
    {
      return P4Wrapper.getP4().isInvalidMapping( path );
    }
    catch ( ConnectionException | AccessException e )
    {
      P4Logger.getInstance().error( "Error determining P4 mapping", e );
      return true;
    }
  }
}
