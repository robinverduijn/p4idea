package p4idea.perforce;

import com.perforce.p4java.client.IClient;
import com.perforce.p4java.core.*;
import com.perforce.p4java.core.file.IFileSpec;
import com.perforce.p4java.exception.*;
import com.perforce.p4java.impl.generic.core.file.FileSpec;
import p4idea.P4Logger;

import java.io.File;
import java.util.*;

public class P4WrapperTemp extends P4Wrapper
{
  public void revertChangelist( IChangelist changelist ) throws ConnectionException, RequestException,
      AccessException
  {
    revertChangelist( changelist.getId() );
  }

  public void revertChangelist( int changelist ) throws ConnectionException, RequestException,
      AccessException
  {
    try
    {
      String result = getP4Server().deletePendingChangelist( changelist );
      _messages.add( result );
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public void submitChangelist( IChangelist changelist ) throws ConnectionException, RequestException, AccessException
  {
    try
    {
      changelist.submit( false );
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public List<IFileSpec> openForAdd( IChangelist changelist, List<File> files ) throws ConnectionException,
      AccessException
  {
    List<IFileSpec> fileSpecs = new ArrayList<>();
    for ( File file : files )
    {
      fileSpecs.add( new FileSpec( file.getAbsolutePath() ) );
    }
    try
    {
      IClient client = getP4Server().getCurrentClient();
      return client.addFiles( fileSpecs, false, changelist.getId(), null, false );
    }
    finally
    {
      attemptDisconnect();
    }
  }

  public int revertEdit( File file ) throws ConnectionException,
      AccessException
  {
    List<IFileSpec> reverted = revertEdit( Arrays.asList( file ), false );
    if ( reverted.isEmpty() )
    {
      return -1;
    }
    P4Logger.getInstance().log( String.format( "Reverted: %s", file ) );
    return reverted.get( 0 ).getChangelistId();
  }

  public List<IFileSpec> revertEdit( List<File> files, boolean revertOnlyUnchanged ) throws ConnectionException,
      AccessException
  {
    List<IFileSpec> fileSpecs = new ArrayList<>();
    for ( File file : files )
    {
      fileSpecs.add( new FileSpec( file.getAbsolutePath() ) );
    }

    try
    {
      IClient client = getP4Server().getCurrentClient();
      List<IFileSpec> openedFiles = client.openedFiles( fileSpecs, -1, -1 );
      if ( openedFiles.isEmpty() )
      {
        P4Logger.getInstance().log( "File(s) were not open for edit: " + files );
        return Collections.emptyList();
      }

      List<IFileSpec> revertedFiles = client.revertFiles( openedFiles, false, -1, revertOnlyUnchanged, false );
      if ( !revertedFiles.isEmpty() )
      {
        P4Logger.getInstance().log( String.format( "Reverted %d file(s)", revertedFiles.size() ) );
      }
      return revertedFiles;
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
      List<String> files = new ArrayList<>();
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

  public IUser getUser( String id ) throws ConnectionException, AccessException
  {
    try
    {
      return getP4Server().getUser( id );
    }
    catch ( ConnectionException | AccessException | RequestException e )
    {
      final String error = String.format( "P4: error looking up user \"%s\"", id );
      P4Logger.getInstance().error( error, e );
      return null;
    }
    finally
    {
      attemptDisconnect();
    }
  }
}
