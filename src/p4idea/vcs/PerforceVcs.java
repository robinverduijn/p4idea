package p4idea.vcs;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.versionBrowser.CommittedChangeList;
import org.jetbrains.annotations.NotNull;

public class PerforceVcs extends AbstractVcs<CommittedChangeList>
{
  private static final String NAME = "Perforce";

  private Configurable _configurable;

  public PerforceVcs( @NotNull Project project )
  {
    super( project, NAME );
  }

  @Override
  public String getDisplayName()
  {
    return NAME;
  }

  @NotNull
  @Override
  public synchronized Configurable getConfigurable()
  {
    return _configurable;
  }
}
