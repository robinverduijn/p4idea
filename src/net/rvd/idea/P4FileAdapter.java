package net.rvd.idea;

import com.intellij.openapi.vfs.*;
import com.perforce.p4java.exception.AccessException;
import com.perforce.p4java.exception.ConnectionException;
import net.rvd.idea.ui.UserInput;
import net.rvd.perforce.P4Wrapper;

import java.io.File;

public class P4FileAdapter extends VirtualFileAdapter
{
  @Override
  public void beforePropertyChange( VirtualFilePropertyEvent event )
  {
    // TODO
    if ( shouldHandle( event ) )
    {
      P4Logger.getInstance().log( "beforePropertyChange", event );
    }
  }

  @Override
  public void propertyChanged( VirtualFilePropertyEvent event )
  {
    // TODO
    if ( shouldHandle( event ) )
    {
      P4Logger.getInstance().log( "propertyChanged", event );
    }
  }

  @Override
  public void beforeContentsChange( VirtualFileEvent event )
  {
    // Corresponds to "open for edit"
    if ( shouldHandle( event ) )
    {
      P4Logger.getInstance().log( "beforeContentsChange", event );
    }
  }

  @Override
  public void contentsChanged( VirtualFileEvent event )
  {
    // TODO
    if ( shouldHandle( event ) )
    {
      P4Logger.getInstance().log( "contentsChanged", event );
    }
  }

  @Override
  public void beforeFileDeletion( VirtualFileEvent event )
  {
    // TODO
    if ( shouldHandle( event ) )
    {
      P4Logger.getInstance().log( "beforeFileDeletion", event );
    }
  }

  @Override
  public void fileDeleted( VirtualFileEvent event )
  {
    // Corresponds to "delete"
    if ( shouldHandle( event ) )
    {
      P4Logger.getInstance().log( "fileDeleted", event );
    }
  }

  @Override
  public void beforeFileMovement( VirtualFileMoveEvent event )
  {
    // TODO
    if ( shouldHandle( event ) )
    {
      P4Logger.getInstance().log( "beforeFileMovement", event );
    }
  }

  @Override
  public void fileMoved( VirtualFileMoveEvent event )
  {
    // Corresponds to "rename"
    if ( shouldHandle( event ) )
    {
      P4Logger.getInstance().log( "fileMoved", event );
    }
  }

  @Override
  public void fileCreated( VirtualFileEvent event )
  {
    // Corresponds to "open for add"
    if ( shouldHandle( event ) )
    {
      P4Logger.getInstance().log( "fileCreated", event );
    }
  }

  @Override
  public void fileCopied( VirtualFileCopyEvent event )
  {
    // TODO
    if ( shouldHandle( event ) )
    {
      P4Logger.getInstance().log( "fileCopied", event );
    }
  }

  private boolean shouldHandle( VirtualFileEvent event )
  {
    try
    {
      File file = new File( event.getFile().getCanonicalPath() );
      File root = P4Wrapper.getInstance().getP4Root();
      return null != root && file.getAbsolutePath().startsWith( root.getAbsolutePath() );
    }
    catch ( ConnectionException e )
    {
      P4Logger.getInstance().error( "Error connecting to Perforce", e );
    }
    catch ( AccessException ae )
    {
      if ( !UserInput.getInstance().requestCredentials() )
      {
        try
        {
          P4Wrapper.getInstance().disconnect();
        }
        catch ( ConnectionException | AccessException e )
        {
          P4Logger.getInstance().error( "Error disconnecting from Perforce", e );
        }
      }
    }
    return false;
  }
}
