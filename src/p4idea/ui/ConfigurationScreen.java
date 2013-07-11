package p4idea.ui;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.perforce.p4java.exception.*;
import p4idea.P4Logger;
import p4idea.PerforcePlugin;

public class ConfigurationScreen extends AnAction
{
  public void actionPerformed( AnActionEvent event )
  {
    UserInput.getInstance().requestSettings();
    try
    {
      PerforcePlugin.getInstance().getState().verify();
    }
    catch ( AccessException ae )
    {
      UserInput.getInstance().requestCredentials();
    }
    catch ( ConnectionException | RequestException e )
    {
      final String msg = "Error Connecting to Perforce";
      P4Logger.getInstance().error( msg, e );
      Messages.showErrorDialog( e.getMessage(), msg );
    }
  }
}