package p4idea.vcs;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.*;
import p4idea.PerforcePlugin;
import p4idea.ui.PerforceVcsPanel;

import javax.swing.*;

public class P4Configurable implements Configurable
{
  private PerforceVcsPanel _panel;
  private Project _project;

  public P4Configurable( @NotNull Project project )
  {
    _project = project;
  }

  @Nls
  @Override
  public String getDisplayName()
  {
    return PerforceVcs.NAME;
  }

  @Nullable
  @Override
  public String getHelpTopic()
  {
    return "project.propVCSSupport.VCSs.Perforce";
  }

  @Nullable
  @Override
  public JComponent createComponent()
  {
    _panel = new PerforceVcsPanel();
    return _panel.getPanel();
  }

  @Override
  public boolean isModified()
  {
    return _panel.isModified();
  }

  @Override
  public void apply() throws ConfigurationException
  {
    _panel.apply( PerforcePlugin.getInstance().getState() );
  }

  @Override
  public void reset()
  {
    _panel.initialize();
  }

  @Override
  public void disposeUIResources()
  {
  }
}
