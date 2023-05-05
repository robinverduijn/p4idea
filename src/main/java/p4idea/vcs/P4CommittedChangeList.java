package p4idea.vcs;

import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.versionBrowser.CommittedChangeList;
import com.perforce.p4java.core.IChangelistSummary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Date;

class P4CommittedChangeList implements CommittedChangeList
{
  private final IChangelistSummary _changelist;

  public P4CommittedChangeList( @NotNull IChangelistSummary changelist )
  {
    _changelist = changelist;
  }

  @Override
  public String getCommitterName()
  {
    return _changelist.getUsername();
  }

  @Override
  public Date getCommitDate()
  {
    return _changelist.getDate();
  }

  @Override
  public long getNumber()
  {
    return _changelist.getId();
  }

  @Override
  public @NlsSafe @Nullable String getBranch()
  {
    return null;
  }

  @Override
  public AbstractVcs getVcs()
  {
    return PerforceVcs.Instance;
  }

  @Override
  public Collection<Change> getChangesWithMovedTrees()
  {
    return null;
  }

  @Override
  public boolean isModifiable()
  {
    return false;
  }

  @Override
  public void setDescription( String newMessage )
  {
    _changelist.setDescription( newMessage );
  }

  @Override
  public Collection<Change> getChanges()
  {
    return null;
  }

  @NotNull
  @Override
  public String getName()
  {
    return String.format( "%d: %s", _changelist.getId(), _changelist.getDescription() );
  }

  @Override
  public String getComment()
  {
    return _changelist.getDescription();
  }
}
