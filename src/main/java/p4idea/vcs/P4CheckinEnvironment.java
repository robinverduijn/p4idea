package p4idea.vcs;

import com.google.common.collect.Lists;
import com.intellij.openapi.vcs.*;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeList;
import com.intellij.openapi.vcs.checkin.CheckinEnvironment;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.*;
import com.perforce.p4java.core.IChangelist;
import com.perforce.p4java.exception.P4JavaException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import p4idea.FileLists;
import p4idea.P4Logger;
import p4idea.perforce.P4Wrapper;

import java.util.List;
import java.util.Set;

class P4CheckinEnvironment implements CheckinEnvironment
{
  @Nullable
  @Override
  public RefreshableOnComponent createAdditionalOptionsPanel( CheckinProjectPanel panel, PairConsumer<Object, Object>
      additionalDataConsumer )
  {
    return null;
  }

  @Nullable
  @Override
  public String getDefaultMessageFor( FilePath[] filesToCheckin )
  {
    return null;
  }

  @Nullable
  @Override
  public String getHelpId()
  {
    return null;
  }

  @Override
  public String getCheckinOperationName()
  {
    return "Submit";
  }

  @Nullable
  @Override
  public List<VcsException> commit( List<Change> changes, String preparedComment )
  {
    return commit( changes, preparedComment, FunctionUtil.<Object, Object>nullConstant(), null );
  }

  @Nullable
  @Override
  public List<VcsException> commit( List<Change> changes, String preparedComment, @NotNull NullableFunction<Object,
      Object> parametersHolder, Set<String> feedback )
  {
    List<VcsException> errors = Lists.newArrayList();
    List<FilePath> files = Lists.newArrayList();
    for ( Change change : changes )
    {
      try
      {
        files.add( P4ChangeCollector.getFileForChange( change ) );
      }
      catch ( VcsException e )
      {
        errors.add( e );
      }
    }

    if ( !files.isEmpty() )
    {
      try
      {
        P4Wrapper p4 = P4Wrapper.getP4();
        IChangelist changelist = p4.createChangelist( FileLists.fromFilePaths( files ), preparedComment );
        p4.submitChangelist( changelist );

      }
      catch ( P4JavaException e )
      {
        errors.add( new VcsException( e ) );
      }
    }
    return errors;
  }

  @Nullable
  @Override
  public List<VcsException> scheduleMissingFileForDeletion( List<FilePath> files )
  {
    P4Logger.getInstance().log( "scheduleMissingFileForDeletion()" );
    return Lists.newArrayList();
  }

  @Nullable
  @Override
  public List<VcsException> scheduleUnversionedFilesForAddition( List<VirtualFile> files )
  {
    P4Logger.getInstance().log( "scheduleUnversionedFilesForAddition()" );
    return Lists.newArrayList();
  }

  @Override
  public boolean keepChangeListAfterCommit( ChangeList changeList )
  {
    // Perhaps change to false if/when we actually implement p4 submit
    return true;
  }

  @Override
  public boolean isRefreshAfterCommitNeeded()
  {
    return true;
  }
}
