package net.rvd.idea;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class ConfigurationDialog extends AnAction
{
  public void actionPerformed( AnActionEvent event )
  {
    Project project = event.getData( PlatformDataKeys.PROJECT );
    String root = Messages.showInputDialog( project, "Perforce root", "Please Enter the Perforce Root", Messages.getQuestionIcon() );

    Messages.showMessageDialog( project, String.format( "Using P4ROOT: %s", root ), "Information", Messages.getInformationIcon() );
  }
}