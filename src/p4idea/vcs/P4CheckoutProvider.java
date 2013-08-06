package p4idea.vcs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.CheckoutProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import p4idea.P4Logger;

class P4CheckoutProvider implements CheckoutProvider
{
  @Override
  public void doCheckout( @NotNull Project project, @Nullable Listener listener )
  {
    P4Logger.getInstance().log( String.format( "doCheckout( %s )", project.getName() ) );
  }

  @Override
  public String getVcsName()
  {
    return PerforceVcs.NAME;
  }
}
