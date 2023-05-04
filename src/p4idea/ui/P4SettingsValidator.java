package p4idea.ui;

import com.intellij.execution.ExecutableValidator;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.perforce.p4java.exception.AccessException;
import com.perforce.p4java.exception.ConnectionException;
import org.jetbrains.annotations.NotNull;
import p4idea.perforce.P4Settings;
import p4idea.perforce.P4Wrapper;
import p4idea.vcs.P4Configurable;

public class P4SettingsValidator extends ExecutableValidator
{
  private static final String TITLE = "Invalid Perforce Settings: ";
  private static final String DESCRIPTION = "Please inspect your P4 settings.";

  public P4SettingsValidator( @NotNull Project project )
  {
    super( project, TITLE, DESCRIPTION );
  }

  @Override
  protected String getCurrentExecutable()
  {
    return "";
  }

  @NotNull
  @Override
  protected Configurable getConfigurable()
  {
    return new P4Configurable( myProject );
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
