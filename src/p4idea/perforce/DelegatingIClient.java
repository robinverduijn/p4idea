package p4idea.perforce;

import com.perforce.p4java.client.IClient;
import com.perforce.p4java.core.IChangelist;
import com.perforce.p4java.core.file.IFileSpec;
import com.perforce.p4java.core.file.IntegrationOptions;
import com.perforce.p4java.exception.*;
import com.perforce.p4java.impl.generic.client.ClientView;
import com.perforce.p4java.option.client.*;
import com.perforce.p4java.option.server.OpenedFilesOptions;
import com.perforce.p4java.server.IServer;
import com.perforce.p4java.server.callback.IStreamingCallback;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

public class DelegatingIClient implements IClient
{
  protected final IClient _client;

  public DelegatingIClient( IClient client )
  {
    _client = client;
  }

  public ClientView getClientView()
  {
    return _client.getClientView();
  }

  public void setClientView( ClientView iClientViewMappings )
  {
    _client.setClientView( iClientViewMappings );
  }

  public IServer getServer()
  {
    return _client.getServer();
  }

  public void setServer( IServer iServer )
  {
    _client.setServer( iServer );
  }

  public List<IFileSpec> sync( List<IFileSpec> iFileSpecs, boolean b, boolean b2, boolean b3, boolean b4 ) throws
      ConnectionException, RequestException, AccessException
  {
    return _client.sync( iFileSpecs, b, b2, b3, b4 );
  }

  public List<IFileSpec> sync( List<IFileSpec> iFileSpecs, SyncOptions syncOptions ) throws P4JavaException
  {
    return _client.sync( iFileSpecs, syncOptions );
  }

  public void sync( List<IFileSpec> iFileSpecs, SyncOptions syncOptions, IStreamingCallback iStreamingCallback,
                    int i ) throws P4JavaException
  {
    _client.sync( iFileSpecs, syncOptions, iStreamingCallback, i );
  }

  public List<IFileSpec> labelSync( List<IFileSpec> iFileSpecs, String s, boolean b, boolean b2,
                                    boolean b3 ) throws ConnectionException, RequestException, AccessException
  {
    return _client.labelSync( iFileSpecs, s, b, b2, b3 );
  }

  public List<IFileSpec> labelSync( List<IFileSpec> iFileSpecs, String s, LabelSyncOptions labelSyncOptions )
      throws P4JavaException
  {
    return _client.labelSync( iFileSpecs, s, labelSyncOptions );
  }

  public IChangelist createChangelist( IChangelist iChangelist ) throws ConnectionException, RequestException,
      AccessException
  {
    return _client.createChangelist( iChangelist );
  }

  public List<IFileSpec> addFiles( List<IFileSpec> iFileSpecs, boolean b, int i, String s, boolean b2 ) throws
      ConnectionException, AccessException
  {
    return _client.addFiles( iFileSpecs, b, i, s, b2 );
  }

  public List<IFileSpec> addFiles( List<IFileSpec> iFileSpecs, AddFilesOptions addFilesOptions ) throws P4JavaException
  {
    return _client.addFiles( iFileSpecs, addFilesOptions );
  }

  public List<IFileSpec> editFiles( List<IFileSpec> iFileSpecs, boolean b, boolean b2, int i,
                                    String s ) throws RequestException, ConnectionException, AccessException
  {
    return _client.editFiles( iFileSpecs, b, b2, i, s );
  }

  public List<IFileSpec> editFiles( List<IFileSpec> iFileSpecs, EditFilesOptions editFilesOptions ) throws
      P4JavaException
  {
    return _client.editFiles( iFileSpecs, editFilesOptions );
  }

  public List<IFileSpec> revertFiles( List<IFileSpec> iFileSpecs, boolean b, int i, boolean b2, boolean b3 ) throws
      ConnectionException, AccessException
  {
    return _client.revertFiles( iFileSpecs, b, i, b2, b3 );
  }

  public List<IFileSpec> revertFiles( List<IFileSpec> iFileSpecs, RevertFilesOptions revertFilesOptions ) throws
      P4JavaException
  {
    return _client.revertFiles( iFileSpecs, revertFilesOptions );
  }

  public List<IFileSpec> deleteFiles( List<IFileSpec> iFileSpecs, int i, boolean b ) throws ConnectionException,
      AccessException
  {
    return _client.deleteFiles( iFileSpecs, i, b );
  }

  public List<IFileSpec> deleteFiles( List<IFileSpec> iFileSpecs, DeleteFilesOptions deleteFilesOptions ) throws
      P4JavaException
  {
    return _client.deleteFiles( iFileSpecs, deleteFilesOptions );
  }

  public List<IFileSpec> openedFiles( List<IFileSpec> iFileSpecs, int i, int i2 ) throws ConnectionException,
      AccessException
  {
    return _client.openedFiles( iFileSpecs, i, i2 );
  }

  public List<IFileSpec> openedFiles( List<IFileSpec> iFileSpecs, OpenedFilesOptions openedFilesOptions ) throws
      P4JavaException
  {
    return _client.openedFiles( iFileSpecs, openedFilesOptions );
  }

  public List<IFileSpec> haveList( List<IFileSpec> iFileSpecs ) throws ConnectionException, AccessException
  {
    return _client.haveList( iFileSpecs );
  }

  public List<IFileSpec> where( List<IFileSpec> iFileSpecs ) throws ConnectionException, AccessException
  {
    return _client.where( iFileSpecs );
  }

  public List<IFileSpec> reopenFiles( List<IFileSpec> iFileSpecs, int i, String s ) throws ConnectionException,
      AccessException
  {
    return _client.reopenFiles( iFileSpecs, i, s );
  }

  public List<IFileSpec> reopenFiles( List<IFileSpec> iFileSpecs, ReopenFilesOptions reopenFilesOptions ) throws
      P4JavaException
  {
    return _client.reopenFiles( iFileSpecs, reopenFilesOptions );
  }

  public List<IFileSpec> integrateFiles( int i, boolean b, IntegrationOptions integrationOptions, String s,
                                         IFileSpec iFileSpec, IFileSpec iFileSpec2 ) throws ConnectionException,
      AccessException
  {
    return _client.integrateFiles( i, b, integrationOptions, s, iFileSpec, iFileSpec2 );
  }

  public List<IFileSpec> integrateFiles( IFileSpec iFileSpec, IFileSpec iFileSpec2, String s, IntegrateFilesOptions
      integrateFilesOptions ) throws P4JavaException
  {
    return _client.integrateFiles( iFileSpec, iFileSpec2, s, integrateFilesOptions );
  }

  public List<IFileSpec> integrateFiles( IFileSpec iFileSpec, List<IFileSpec> iFileSpecs, IntegrateFilesOptions
      integrateFilesOptions ) throws P4JavaException
  {
    return _client.integrateFiles( iFileSpec, iFileSpecs, integrateFilesOptions );
  }

  public List<IFileSpec> resolveFilesAuto( List<IFileSpec> iFileSpecs, boolean b, boolean b2, boolean b3, boolean
      b4, boolean b5 ) throws ConnectionException, AccessException
  {
    return _client.resolveFilesAuto( iFileSpecs, b, b2, b3, b4, b5 );
  }

  public List<IFileSpec> resolveFilesAuto( List<IFileSpec> iFileSpecs, ResolveFilesAutoOptions
      resolveFilesAutoOptions ) throws P4JavaException
  {
    return _client.resolveFilesAuto( iFileSpecs, resolveFilesAutoOptions );
  }

  public IFileSpec resolveFile( IFileSpec iFileSpec, InputStream inputStream ) throws ConnectionException,
      RequestException, AccessException
  {
    return _client.resolveFile( iFileSpec, inputStream );
  }

  public IFileSpec resolveFile( IFileSpec iFileSpec, InputStream inputStream, boolean b, int i, int i2 ) throws
      ConnectionException, RequestException, AccessException
  {
    return _client.resolveFile( iFileSpec, inputStream, b, i, i2 );
  }

  public List<IFileSpec> resolvedFiles( List<IFileSpec> iFileSpecs, boolean b ) throws ConnectionException,
      AccessException
  {
    return _client.resolvedFiles( iFileSpecs, b );
  }

  public List<IFileSpec> resolvedFiles( List<IFileSpec> iFileSpecs, ResolvedFilesOptions resolvedFilesOptions )
      throws P4JavaException
  {
    return _client.resolvedFiles( iFileSpecs, resolvedFilesOptions );
  }

  public List<IFileSpec> lockFiles( List<IFileSpec> iFileSpecs, int i ) throws ConnectionException, AccessException
  {
    return _client.lockFiles( iFileSpecs, i );
  }

  public List<IFileSpec> lockFiles( List<IFileSpec> iFileSpecs, LockFilesOptions lockFilesOptions ) throws
      P4JavaException
  {
    return _client.lockFiles( iFileSpecs, lockFilesOptions );
  }

  public List<IFileSpec> unlockFiles( List<IFileSpec> iFileSpecs, int i, boolean b ) throws ConnectionException,
      AccessException
  {
    return _client.unlockFiles( iFileSpecs, i, b );
  }

  public List<IFileSpec> unlockFiles( List<IFileSpec> iFileSpecs, UnlockFilesOptions unlockFilesOptions ) throws
      P4JavaException
  {
    return _client.unlockFiles( iFileSpecs, unlockFilesOptions );
  }

  public List<IFileSpec> getDiffFiles( List<IFileSpec> iFileSpecs, int i, boolean b, boolean b2, boolean b3,
                                       boolean b4, boolean b5, boolean b6, boolean b7 ) throws ConnectionException,
      RequestException, AccessException
  {
    return _client.getDiffFiles( iFileSpecs, i, b, b2, b3, b4, b5, b6, b7 );
  }

  public List<IFileSpec> getDiffFiles( List<IFileSpec> iFileSpecs, GetDiffFilesOptions getDiffFilesOptions ) throws
      P4JavaException
  {
    return _client.getDiffFiles( iFileSpecs, getDiffFilesOptions );
  }

  public List<IFileSpec> shelveFiles( List<IFileSpec> iFileSpecs, int i, ShelveFilesOptions shelveFilesOptions )
      throws P4JavaException
  {
    return _client.shelveFiles( iFileSpecs, i, shelveFilesOptions );
  }

  public List<IFileSpec> unshelveFiles( List<IFileSpec> iFileSpecs, int i, int i2, UnshelveFilesOptions
      unshelveFilesOptions ) throws P4JavaException
  {
    return _client.unshelveFiles( iFileSpecs, i, i2, unshelveFilesOptions );
  }

  public List<IFileSpec> shelveChangelist( int i, List<IFileSpec> iFileSpecs, boolean b, boolean b2,
                                           boolean b3 ) throws ConnectionException, RequestException, AccessException
  {
    return _client.shelveChangelist( i, iFileSpecs, b, b2, b3 );
  }

  public List<IFileSpec> shelveChangelist( IChangelist iChangelist ) throws ConnectionException, RequestException,
      AccessException
  {
    return _client.shelveChangelist( iChangelist );
  }

  public List<IFileSpec> unshelveChangelist( int i, List<IFileSpec> iFileSpecs, int i2, boolean b, boolean b2 )
      throws ConnectionException, RequestException, AccessException
  {
    return _client.unshelveChangelist( i, iFileSpecs, i2, b, b2 );
  }

  public List<IFileSpec> copyFiles( IFileSpec iFileSpec, IFileSpec iFileSpec2, String s,
                                    CopyFilesOptions copyFilesOptions ) throws P4JavaException
  {
    return _client.copyFiles( iFileSpec, iFileSpec2, s, copyFilesOptions );
  }

  public List<IFileSpec> copyFiles( IFileSpec iFileSpec, List<IFileSpec> iFileSpecs,
                                    CopyFilesOptions copyFilesOptions ) throws P4JavaException
  {
    return _client.copyFiles( iFileSpec, iFileSpecs, copyFilesOptions );
  }

  public List<IFileSpec> mergeFiles( IFileSpec iFileSpec, List<IFileSpec> iFileSpecs,
                                     MergeFilesOptions mergeFilesOptions ) throws P4JavaException
  {
    return _client.mergeFiles( iFileSpec, iFileSpecs, mergeFilesOptions );
  }

  public List<IFileSpec> reconcileFiles( List<IFileSpec> iFileSpecs, ReconcileFilesOptions reconcileFilesOptions )
      throws P4JavaException
  {
    return _client.reconcileFiles( iFileSpecs, reconcileFilesOptions );
  }

  public List<IFileSpec> populateFiles( IFileSpec iFileSpec, List<IFileSpec> iFileSpecs,
                                        PopulateFilesOptions populateFilesOptions ) throws P4JavaException
  {
    return _client.populateFiles( iFileSpec, iFileSpecs, populateFilesOptions );
  }

  public String getName()
  {
    return _client.getName();
  }

  public void setName( String s )
  {
    _client.setName( s );
  }

  public Date getUpdated()
  {
    return _client.getUpdated();
  }

  public void setUpdated( Date date )
  {
    _client.setUpdated( date );
  }

  public Date getAccessed()
  {
    return _client.getAccessed();
  }

  public void setAccessed( Date date )
  {
    _client.setAccessed( date );
  }

  public String getOwnerName()
  {
    return _client.getOwnerName();
  }

  public void setOwnerName( String s )
  {
    _client.setOwnerName( s );
  }

  public String getHostName()
  {
    return _client.getHostName();
  }

  public void setHostName( String s )
  {
    _client.setHostName( s );
  }

  public String getDescription()
  {
    return _client.getDescription();
  }

  public void setDescription( String s )
  {
    _client.setDescription( s );
  }

  public String getRoot()
  {
    return _client.getRoot();
  }

  public void setRoot( String s )
  {
    _client.setRoot( s );
  }

  public List<String> getAlternateRoots()
  {
    return _client.getAlternateRoots();
  }

  public void setAlternateRoots( List<String> strings )
  {
    _client.setAlternateRoots( strings );
  }

  public ClientLineEnd getLineEnd()
  {
    return _client.getLineEnd();
  }

  public void setLineEnd( ClientLineEnd clientLineEnd )
  {
    _client.setLineEnd( clientLineEnd );
  }

  public IClientOptions getOptions()
  {
    return _client.getOptions();
  }

  public void setOptions( IClientOptions iClientOptions )
  {
    _client.setOptions( iClientOptions );
  }

  public IClientSubmitOptions getSubmitOptions()
  {
    return _client.getSubmitOptions();
  }

  public void setSubmitOptions( IClientSubmitOptions iClientSubmitOptions )
  {
    _client.setSubmitOptions( iClientSubmitOptions );
  }

  public String getStream()
  {
    return _client.getStream();
  }

  public boolean isStream()
  {
    return _client.isStream();
  }

  public void setStream( String s )
  {
    _client.setStream( s );
  }

  public String getServerId()
  {
    return _client.getServerId();
  }

  public void setServerId( String s )
  {
    _client.setServerId( s );
  }

  public int getStreamAtChange()
  {
    return _client.getStreamAtChange();
  }

  public void setStreamAtChange( int i )
  {
    _client.setStreamAtChange( i );
  }

  public boolean canRefresh()
  {
    return _client.canRefresh();
  }

  public void refresh() throws ConnectionException, RequestException, AccessException
  {
    _client.refresh();
  }

  public boolean canUpdate()
  {
    return _client.canUpdate();
  }

  public void update() throws ConnectionException, RequestException, AccessException
  {
    _client.update();
  }

  public void update( boolean b ) throws ConnectionException, RequestException, AccessException
  {
    _client.update( b );
  }
}
