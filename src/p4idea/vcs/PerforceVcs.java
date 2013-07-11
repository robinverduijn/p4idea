package p4idea.vcs;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.VcsKey;
import p4idea.P4Logger;
import org.jetbrains.annotations.NotNull;

public class PerforceVcs extends AbstractVcs implements Disposable
{
  private Configurable _configurable;

  protected PerforceVcs( Project project, String name, VcsKey key )
  {
    super( project, name, key );
  }

  @Override
  public String getDisplayName()
  {
    return "Perforce VCS Integration";
  }

  @NotNull
  @Override
  public synchronized Configurable getConfigurable()
  {
    return _configurable;
  }

  @Override
  public void dispose()
  {
    P4Logger.getInstance().log( "Trying to dispose of PerforceVcs provider" );
  }
}
