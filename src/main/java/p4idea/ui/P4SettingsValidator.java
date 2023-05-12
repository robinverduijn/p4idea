package p4idea.ui;

import com.intellij.execution.ExecutableValidator;
import com.intellij.openapi.project.Project;
import com.perforce.p4java.exception.AccessException;
import com.perforce.p4java.exception.ConnectionException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import p4idea.perforce.P4Settings;
import p4idea.perforce.P4Wrapper;

public class P4SettingsValidator extends ExecutableValidator
{
  private static final String TITLE = "Invalid Perforce Settings: ";
  private static final String DESCRIPTION = "Please inspect your P4 settings.";
  private static final String SAFE_DESCRIPTION = "Please inspect your P4 settings (safe mode).";

  public P4SettingsValidator( @NotNull Project project )
  {
    super( project, TITLE, DESCRIPTION, SAFE_DESCRIPTION );
  }

  @Override
  protected String getCurrentExecutable()
  {
    return "";
  }

  @NotNull
  @Override
  protected @Nls String getConfigurableDisplayName()
  {
    return "P4 Settings";
  }

  @Override
  protected boolean isExecutableValid( @NotNull String executable )
  {
    P4Settings settings = myProject.getComponent( P4Settings.class );
    if ( settings.isUnset() )
    {
      return false;
    }
    try
    {
      return P4Wrapper.getP4().checkValidCredentials();
    }
    catch ( AccessException e )
    {
      return null != UserInput.requestCredentials( settings );
    }
    catch ( ConnectionException e )
    {
      return false;
    }
  }
}