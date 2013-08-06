package p4idea.perforce;

import com.google.common.collect.Lists;
import com.intellij.openapi.vcs.VcsException;
import com.perforce.p4java.core.*;
import com.perforce.p4java.core.file.IFileSpec;
import com.perforce.p4java.exception.*;
import com.perforce.p4java.impl.generic.core.file.FileSpec;

import java.util.Arrays;
import java.util.List;

class P4Changelists extends P4Wrapper
{
  private IChangelist getDefaultChangelist() throws ConnectionException, AccessException, RequestException
  {
    return getP4Server().getChangelist( IChangelist.DEFAULT );
  }

  void revertChangelist( int changelist ) throws ConnectionException, RequestException,
      AccessException
  {
    try
    {
      String result = getP4Server().deletePendingChangelist( changelist );
      processResult( result );
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public List<IChangelistSummary> getChangelists( String path ) throws ConnectionException, RequestException,
      AccessException
  {
    try
    {
      IFileSpec fileSpec = new FileSpec( path );
      List<IFileSpec> fileSpecs = Arrays.asList( fileSpec );
      return getP4Server().getChangelists( -1, fileSpecs, null, null, false, true, false, false );
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public List<String> getChangelistFiles( int changelist ) throws ConnectionException, RequestException,
      AccessException
  {
    try
    {
      List<String> files = Lists.newArrayList();
      for ( IFileSpec file : getP4Server().getChangelistFiles( changelist ) )
      {
        files.add( file.getDepotPathString() );
      }
      return files;
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public IUser getUser( String id ) throws ConnectionException, AccessException, VcsException
  {
    try
    {
      return getP4Server().getUser( id );
    }
    catch ( ConnectionException | AccessException | RequestException e )
    {
      getP4().handleP4Exception( String.format( "Error looking up user \"%s\"", id ), e );
      return null;
    }
    finally
    {
      attemptDisconnect();
    }
  }
}
