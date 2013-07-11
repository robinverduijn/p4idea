package p4idea.vcs;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.versionBrowser.CommittedChangeList;
import org.jetbrains.annotations.NotNull;

public class PerforceVcs extends AbstractVcs<CommittedChangeList>
{
  public static final String NAME = "Perforce";

  private final Configurable _configurable;

  public PerforceVcs( @NotNull Project project )
  {
    super( project, NAME );
    _configurable = new P4Configurable( project );
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
