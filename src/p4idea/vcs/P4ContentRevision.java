package p4idea.vcs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Throwable2Computable;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import com.intellij.openapi.vcs.impl.ContentRevisionCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class P4ContentRevision implements ContentRevision
{
  private final Project _project;
  private final FilePath _path;
  private final VcsRevisionNumber _revision;

  public P4ContentRevision( Project project, FilePath path, int revision )
  {
    _project = project;
    _path = path;
    _revision = new VcsRevisionNumber.Int( revision );
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append( getClass().getSimpleName() );
    sb.append( "{ " );
    sb.append( "path=" ).append( _path );
    sb.append( ", " );
    sb.append( "revision=" ).append( _revision.asString() );
    sb.append( " }" );
    return sb.toString();
  }

  @Nullable
  @Override
  public String getContent() throws VcsException
  {
    try
    {
      return ContentRevisionCache.getOrLoadAsString( _project, _path, _revision, PerforceVcs.getKey(),
          ContentRevisionCache.UniqueType.REPOSITORY_CONTENT, new P4ContentLoader() );
    }
    catch ( IOException e )
    {
      throw new VcsException( e );
    }
  }

  @NotNull
  @Override
  public FilePath getFile()
  {
    return _path;
  }

  @NotNull
  @Override
  public VcsRevisionNumber getRevisionNumber()
  {
    return _revision;
  }

  private class P4ContentLoader implements Throwable2Computable<byte[], VcsException, IOException>
  {
    @Override
    public byte[] compute() throws VcsException, IOException
    {
      return FileUtil.loadFileBytes( _path.getIOFile() );
    }
  }
}
