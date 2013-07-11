package p4idea.vcs;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.*;
import com.intellij.openapi.vfs.VirtualFile;
import p4idea.P4Logger;

import java.util.List;

public class P4ChangeProvider implements ChangeProvider
{
  @Override
  public void getChanges( VcsDirtyScope dirtyScope, ChangelistBuilder builder, ProgressIndicator progress,
                          ChangeListManagerGate addGate ) throws VcsException
  {
    P4Logger.getInstance().log( "getChanges()" );
  }

  @Override
  public boolean isModifiedDocumentTrackingRequired()
  {
    P4Logger.getInstance().log( "getChanges()" );
    return false;
  }

  @Override
  public void doCleanup( List<VirtualFile> files )
  {
    P4Logger.getInstance().log( "doCleanup()" );
    for ( VirtualFile file : files )
    {
      P4Logger.getInstance().log( "- " + file.getCanonicalPath() );
    }
  }
}
