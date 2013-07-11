package p4idea.vcs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsDirectoryMapping;
import com.intellij.openapi.vcs.VcsRootChecker;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.perforce.p4java.exception.AccessException;
import com.perforce.p4java.exception.ConnectionException;
import com.perforce.p4java.server.IServerInfo;
import org.jetbrains.annotations.NotNull;
import p4idea.P4Logger;
import p4idea.perforce.P4Settings;
import p4idea.perforce.P4Wrapper;
import p4idea.ui.UserInput;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

public class P4RootChecker implements VcsRootChecker
{
  private final Project _project;

  public P4RootChecker( Project project )
  {
    _project = project;
  }

  @NotNull
  @Override
  public Collection<String> getUnregisteredRoots()
  {
    P4Logger.getInstance().log( "getUnregisteredRoots()" );
    return Collections.emptyList();
  }

  @Override
  public boolean isInvalidMapping( @NotNull VcsDirectoryMapping mapping )
  {
    P4Logger.getInstance().log( "isInvalidMapping()" );
    P4Logger.getInstance().log( mapping.getDirectory() );
    P4Logger.getInstance().log( mapping.getVcs() );
    return false;
  }

  private boolean shouldHandle( VirtualFileEvent event )
  {
    P4Settings settings = _project.getComponent( P4Settings.class );
    if ( settings.isUnset() )
    {
      P4Logger.getInstance().log( String.format( "Incomplete Perforce Settings: %s", settings ) );
      return false;
    }
    try
    {
      File file = new File( event.getFile().getCanonicalPath() );
      File root = P4Wrapper.getP4().getP4Root();
      return null != root && file.getAbsolutePath().startsWith( root.getAbsolutePath() );
    }
    catch ( ConnectionException | AccessException ae )
    {
      IServerInfo info = UserInput.requestCredentials( settings );
      if ( null == info )
      {
        try
        {
          P4Wrapper.getP4().disconnect();
        }
        catch ( ConnectionException | AccessException e )
        {
          P4Logger.getInstance().error( "Error disconnecting from Perforce", e );
        }
      }
      return false;
    }
  }
}
