package p4idea.vcs;

import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.rollback.RollbackEnvironment;
import com.intellij.openapi.vcs.rollback.RollbackProgressListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.perforce.p4java.exception.AccessException;
import com.perforce.p4java.exception.ConnectionException;
import org.jetbrains.annotations.NotNull;
import p4idea.FileLists;
import p4idea.perforce.P4Wrapper;

import java.util.List;

class P4RollbackEnvironment implements RollbackEnvironment
{
  @Override
  public @NotNull String getRollbackOperationName()
  {
    return "Revert";
  }

  @Override
  public void rollbackChanges( List<? extends Change> changes, List<VcsException> vcsExceptions,
                               @NotNull RollbackProgressListener listener )
  {
    P4Wrapper p4 = P4Wrapper.getP4();
    for ( Change change : changes )
    {
      try
      {
        FilePath path = P4ChangeCollector.getFileForChange( change );
        p4.revert( FileLists.fromFilePaths( List.of( path ) ) );
        listener.accept( change );
      }
      catch ( VcsException e )
      {
        vcsExceptions.add( e );
      }
      catch ( ConnectionException | AccessException e )
      {
        vcsExceptions.add( new VcsException( e ) );
      }
    }
  }

  @Override
  public void rollbackMissingFileDeletion( List<? extends FilePath> files, List<? super VcsException> exceptions, RollbackProgressListener listener )
  {
    // Not implemented (probably not necessary)
  }

  @Override
  public void rollbackModifiedWithoutCheckout( List<? extends VirtualFile> files, List<? super VcsException> exceptions, RollbackProgressListener listener )
  {
    // Not implemented (probably not necessary)
  }

  @Override
  public void rollbackIfUnchanged( VirtualFile file )
  {
    // Not implemented (probably not necessary)
  }
}
