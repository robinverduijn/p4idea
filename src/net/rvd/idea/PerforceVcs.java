package net.rvd.idea;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.VcsKey;
import org.jetbrains.annotations.NotNull;

public class PerforceVcs extends AbstractVcs
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
}
