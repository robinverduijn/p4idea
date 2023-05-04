package p4idea.perforce;

import com.perforce.p4java.admin.IDbSchema;
import com.perforce.p4java.admin.IProtectionEntry;
import com.perforce.p4java.client.IClient;
import com.perforce.p4java.client.IClientSummary;
import com.perforce.p4java.core.*;
import com.perforce.p4java.core.file.*;
import com.perforce.p4java.exception.*;
import com.perforce.p4java.option.server.DescribeOptions;
import com.perforce.p4java.server.*;
import com.perforce.p4java.server.callback.*;

import java.io.InputStream;
import java.nio.charset.UnsupportedCharsetException;
import java.util.*;

class DelegatingIServer implements IServer
{
  final IServer _server;

  DelegatingIServer( IServer server )
  {
    _server = server;
  }

  public Properties getProperties()
  {
    return _server.getProperties();
  }

  public ICommandCallback registerCallback( ICommandCallback iCommandCallback )
  {
    return _server.registerCallback( iCommandCallback );
  }

  public IProgressCallback registerProgressCallback( IProgressCallback iProgressCallback )
  {
    return _server.registerProgressCallback( iProgressCallback );
  }

  public ISSOCallback registerSSOCallback( ISSOCallback issoCallback, String s )
  {
    return _server.registerSSOCallback( issoCallback, s );
  }

  public ServerStatus getStatus()
  {
    return _server.getStatus();
  }

  public boolean setCharsetName( String s ) throws UnsupportedCharsetException
  {
    return _server.setCharsetName( s );
  }

  public String getCharsetName()
  {
    return _server.getCharsetName();
  }

  public int getServerVersionNumber()
  {
    return _server.getServerVersionNumber();
  }

  public boolean isCaseSensitive()
  {
    return _server.isCaseSensitive();
  }

  public boolean supportsUnicode() throws ConnectionException, RequestException, AccessException
  {
    return _server.supportsUnicode();
  }

  public boolean supportsSmartMove() throws ConnectionException, RequestException, AccessException
  {
    return _server.supportsSmartMove();
  }

  public String[] getKnownCharsets()
  {
    return _server.getKnownCharsets();
  }

  public String getAuthTicket()
  {
    return _server.getAuthTicket();
  }

  public void setAuthTicket( String s )
  {
    _server.setAuthTicket( s );
  }

  public String getWorkingDirectory()
  {
    return _server.getWorkingDirectory();
  }

  public void setWorkingDirectory( String s )
  {
    _server.setWorkingDirectory( s );
  }

  public String getUserName()
  {
    return _server.getUserName();
  }

  public void setUserName( String s )
  {
    _server.setUserName( s );
  }

  public void connect() throws ConnectionException, AccessException, RequestException, ConfigException
  {
    _server.connect();
  }

  public boolean isConnected()
  {
    return _server.isConnected();
  }

  public void disconnect() throws ConnectionException, AccessException
  {
    _server.disconnect();
  }

  public void login( String s, boolean b ) throws ConnectionException, RequestException, AccessException,
      ConfigException
  {
    _server.login( s, b );
  }

  public void login( String s ) throws ConnectionException, RequestException, AccessException, ConfigException
  {
    _server.login( s );
  }

  public String getLoginStatus() throws P4JavaException
  {
    return _server.getLoginStatus();
  }

  public void logout() throws ConnectionException, RequestException, AccessException, ConfigException
  {
    _server.logout();
  }

  public IServerInfo getServerInfo() throws ConnectionException, RequestException, AccessException
  {
    return _server.getServerInfo();
  }

  public List<IDepot> getDepots() throws ConnectionException, RequestException, AccessException
  {
    return _server.getDepots();
  }

  public IUser getUser( String s ) throws ConnectionException, RequestException, AccessException
  {
    return _server.getUser( s );
  }

  public String createUser( IUser iUser, boolean b ) throws ConnectionException, RequestException, AccessException
  {
    return _server.createUser( iUser, b );
  }

  public String updateUser( IUser iUser, boolean b ) throws ConnectionException, RequestException, AccessException
  {
    return _server.updateUser( iUser, b );
  }

  public String deleteUser( String s, boolean b ) throws ConnectionException, RequestException, AccessException
  {
    return _server.deleteUser( s, b );
  }

  public List<IUserSummary> getUsers( List<String> strings, int i ) throws ConnectionException, RequestException,
      AccessException
  {
    return _server.getUsers( strings, i );
  }

  public List<IUserGroup> getUserGroups( String s, boolean b, boolean b2, int i ) throws ConnectionException,
      RequestException, AccessException
  {
    return _server.getUserGroups( s, b, b2, i );
  }

  public IUserGroup getUserGroup( String s ) throws ConnectionException, RequestException, AccessException
  {
    return _server.getUserGroup( s );
  }

  public String createUserGroup( IUserGroup iUserGroup ) throws ConnectionException, RequestException, AccessException
  {
    return _server.createUserGroup( iUserGroup );
  }

  public String updateUserGroup( IUserGroup iUserGroup, boolean b ) throws ConnectionException, RequestException,
      AccessException
  {
    return _server.updateUserGroup( iUserGroup, b );
  }

  public String deleteUserGroup( IUserGroup iUserGroup ) throws ConnectionException, RequestException, AccessException
  {
    return _server.deleteUserGroup( iUserGroup );
  }

  public List<IProtectionEntry> getProtectionEntries( boolean b, String s, String s2, String s3,
                                                      List<IFileSpec> iFileSpecs ) throws ConnectionException,
      RequestException, AccessException
  {
    return _server.getProtectionEntries( b, s, s2, s3, iFileSpecs );
  }

  public List<IClientSummary> getClients( String s, String s2, int i ) throws ConnectionException, RequestException,
      AccessException
  {
    return _server.getClients( s, s2, i );
  }

  public List<ILabelSummary> getLabels( String s, int i, String s2, List<IFileSpec> iFileSpecs ) throws
      ConnectionException, RequestException, AccessException
  {
    return _server.getLabels( s, i, s2, iFileSpecs );
  }

  public ILabel getLabel( String s ) throws ConnectionException, RequestException, AccessException
  {
    return _server.getLabel( s );
  }

  public String createLabel( ILabel iLabel ) throws ConnectionException, RequestException, AccessException
  {
    return _server.createLabel( iLabel );
  }

  public String updateLabel( ILabel iLabel ) throws ConnectionException, RequestException, AccessException
  {
    return _server.updateLabel( iLabel );
  }

  public String deleteLabel( String s, boolean b ) throws ConnectionException, RequestException, AccessException
  {
    return _server.deleteLabel( s, b );
  }

  public List<IFileSpec> tagFiles( List<IFileSpec> iFileSpecs, String s, boolean b,
                                   boolean b2 ) throws ConnectionException, RequestException, AccessException
  {
    return _server.tagFiles( iFileSpecs, s, b, b2 );
  }

  public List<IBranchSpecSummary> getBranchSpecs( String s, String s2, int i ) throws ConnectionException,
      RequestException, AccessException
  {
    return _server.getBranchSpecs( s, s2, i );
  }

  public IBranchSpec getBranchSpec( String s ) throws ConnectionException, RequestException, AccessException
  {
    return _server.getBranchSpec( s );
  }

  public String createBranchSpec( IBranchSpec iBranchSpec ) throws ConnectionException, RequestException,
      AccessException
  {
    return _server.createBranchSpec( iBranchSpec );
  }

  public String updateBranchSpec( IBranchSpec iBranchSpec ) throws ConnectionException, RequestException,
      AccessException
  {
    return _server.updateBranchSpec( iBranchSpec );
  }

  public String deleteBranchSpec( String s, boolean b ) throws ConnectionException, RequestException, AccessException
  {
    return _server.deleteBranchSpec( s, b );
  }

  public IClient getCurrentClient()
  {
    return _server.getCurrentClient();
  }

  public void setCurrentClient( IClient iClient ) throws ConnectionException, RequestException, AccessException
  {
    _server.setCurrentClient( iClient );
  }

  public IClient getClient( String s ) throws ConnectionException, RequestException, AccessException
  {
    return _server.getClient( s );
  }

  public IClient getClient( IClientSummary iClientSummary ) throws ConnectionException, RequestException,
      AccessException
  {
    return _server.getClient( iClientSummary );
  }

  public IClient getClientTemplate( String s ) throws ConnectionException, RequestException, AccessException
  {
    return _server.getClientTemplate( s );
  }

  public IClient getClientTemplate( String s, boolean b ) throws ConnectionException, RequestException,
      AccessException
  {
    return _server.getClientTemplate( s, b );
  }

  public String createClient( IClient iClient ) throws ConnectionException, RequestException, AccessException
  {
    return _server.createClient( iClient );
  }

  public String updateClient( IClient iClient ) throws ConnectionException, RequestException, AccessException
  {
    return _server.updateClient( iClient );
  }

  public String deleteClient( String s, boolean b ) throws ConnectionException, RequestException, AccessException
  {
    return _server.deleteClient( s, b );
  }

  public List<IFileSpec> getDepotFiles( List<IFileSpec> iFileSpecs, boolean b ) throws ConnectionException,
      AccessException
  {
    return _server.getDepotFiles( iFileSpecs, b );
  }

  public List<IFileAnnotation> getFileAnnotations( List<IFileSpec> iFileSpecs, DiffType diffType, boolean b,
                                                   boolean b2, boolean b3 ) throws ConnectionException,
      RequestException, AccessException
  {
    return _server.getFileAnnotations( iFileSpecs, diffType, b, b2, b3 );
  }

  public List<IFileSpec> moveFile( int i, boolean b, boolean b2, String s, IFileSpec iFileSpec,
                                   IFileSpec iFileSpec2 ) throws ConnectionException, RequestException, AccessException
  {
    return _server.moveFile( i, b, b2, s, iFileSpec, iFileSpec2 );
  }

  public List<IFileSpec> getDirectories( List<IFileSpec> iFileSpecs, boolean b, boolean b2, boolean b3 ) throws
      ConnectionException, AccessException
  {
    return _server.getDirectories( iFileSpecs, b, b2, b3 );
  }

  public List<IChangelistSummary> getChangelists( int i, List<IFileSpec> iFileSpecs, String s, String s2, boolean
      b, IChangelist.Type type, boolean b2 ) throws ConnectionException, RequestException, AccessException
  {
    return _server.getChangelists( i, iFileSpecs, s, s2, b, type, b2 );
  }

  public List<IChangelistSummary> getChangelists( int i, List<IFileSpec> iFileSpecs, String s, String s2,
                                                  boolean b, boolean b2, boolean b3,
                                                  boolean b4 ) throws ConnectionException, RequestException,
      AccessException
  {
    return _server.getChangelists( i, iFileSpecs, s, s2, b, b2, b3, b4 );
  }

  public IChangelist getChangelist( int i ) throws ConnectionException, RequestException, AccessException
  {
    return _server.getChangelist( i );
  }

  public String deletePendingChangelist( int i ) throws ConnectionException, RequestException, AccessException
  {
    return _server.deletePendingChangelist( i );
  }

  public List<IFileSpec> getChangelistFiles( int i ) throws ConnectionException, RequestException, AccessException
  {
    return _server.getChangelistFiles( i );
  }

  public InputStream getChangelistDiffs( int i, DiffType diffType ) throws ConnectionException, RequestException,
      AccessException
  {
    return _server.getChangelistDiffs( i, diffType );
  }

  public InputStream getChangelistDiffsStream( int i, DescribeOptions describeOptions ) throws ConnectionException,
      RequestException, AccessException
  {
    return _server.getChangelistDiffsStream( i, describeOptions );
  }

  public InputStream getFileContents( List<IFileSpec> iFileSpecs, boolean b, boolean b2 ) throws ConnectionException,
      RequestException, AccessException
  {
    return _server.getFileContents( iFileSpecs, b, b2 );
  }

  public Map<IFileSpec, List<IFileRevisionData>> getRevisionHistory( List<IFileSpec> iFileSpecs, int i, boolean b,
                                                                     boolean b2, boolean b3, boolean b4 ) throws
      ConnectionException, AccessException
  {
    return _server.getRevisionHistory( iFileSpecs, i, b, b2, b3, b4 );
  }

  public List<IUserSummary> getReviews( int i, List<IFileSpec> iFileSpecs ) throws ConnectionException,
      RequestException, AccessException
  {
    return _server.getReviews( i, iFileSpecs );
  }

  public List<IFileSpec> getOpenedFiles( List<IFileSpec> iFileSpecs, boolean b, String s, int i,
                                         int i2 ) throws ConnectionException, AccessException
  {
    return _server.getOpenedFiles( iFileSpecs, b, s, i, i2 );
  }

  public List<IExtendedFileSpec> getExtendedFiles( List<IFileSpec> iFileSpecs, int i, int i2, int i3,
                                                   FileStatOutputOptions fileStatOutputOptions,
                                                   FileStatAncilliaryOptions fileStatAncilliaryOptions ) throws
      ConnectionException, AccessException
  {
    return _server.getExtendedFiles( iFileSpecs, i, i2, i3, fileStatOutputOptions,
        fileStatAncilliaryOptions );
  }

  public List<IFileSpec> getSubmittedIntegrations( List<IFileSpec> iFileSpecs, String s,
                                                   boolean b ) throws ConnectionException, RequestException,
      AccessException
  {
    return _server.getSubmittedIntegrations( iFileSpecs, s, b );
  }

  public List<IChangelist> getInterchanges( IFileSpec iFileSpec, IFileSpec iFileSpec2, boolean b, boolean b2,
                                            int i ) throws ConnectionException, RequestException, AccessException
  {
    return _server.getInterchanges( iFileSpec, iFileSpec2, b, b2, i );
  }

  public List<IChangelist> getInterchanges( String s, List<IFileSpec> iFileSpecs, List<IFileSpec> iFileSpecs2,
                                            boolean b, boolean b2, int i, boolean b3, boolean b4 ) throws
      ConnectionException, RequestException, AccessException
  {
    return _server.getInterchanges( s, iFileSpecs, iFileSpecs2, b, b2, i, b3, b4 );
  }

  public List<IJob> getJobs( List<IFileSpec> iFileSpecs, int i, boolean b, boolean b2, boolean b3,
                             String s ) throws ConnectionException, RequestException, AccessException
  {
    return _server.getJobs( iFileSpecs, i, b, b2, b3, s );
  }

  public IJob getJob( String s ) throws ConnectionException, RequestException, AccessException
  {
    return _server.getJob( s );
  }

  public IJob createJob( Map<String, Object> stringObjectMap ) throws ConnectionException, RequestException,
      AccessException
  {
    return _server.createJob( stringObjectMap );
  }

  public String updateJob( IJob iJob ) throws ConnectionException, RequestException, AccessException
  {
    return _server.updateJob( iJob );
  }

  public String deleteJob( String s ) throws ConnectionException, RequestException, AccessException
  {
    return _server.deleteJob( s );
  }

  public IJobSpec getJobSpec() throws ConnectionException, RequestException, AccessException
  {
    return _server.getJobSpec();
  }

  public List<IFix> getFixList( List<IFileSpec> iFileSpecs, int i, String s, boolean b, int i2 ) throws
      ConnectionException, RequestException, AccessException
  {
    return _server.getFixList( iFileSpecs, i, s, b, i2 );
  }

  public List<IFix> fixJobs( List<String> strings, int i, String s, boolean b ) throws ConnectionException,
      RequestException, AccessException

  {
    return _server.fixJobs( strings, i, s, b );
  }

  public List<IServerProcess> getServerProcesses() throws ConnectionException, RequestException, AccessException
  {
    return _server.getServerProcesses();
  }

  public InputStream getServerFileDiffs( IFileSpec iFileSpec, IFileSpec iFileSpec2, String s, DiffType diffType,
                                         boolean b, boolean b2, boolean b3 ) throws ConnectionException,
      RequestException, AccessException
  {
    return _server.getServerFileDiffs( iFileSpec, iFileSpec2, s, diffType, b, b2, b3 );
  }

  public List<IFileDiff> getFileDiffs( IFileSpec iFileSpec, IFileSpec iFileSpec2, String s, DiffType diffType,
                                       boolean b, boolean b2, boolean b3 ) throws ConnectionException,
      RequestException, AccessException
  {
    return _server.getFileDiffs( iFileSpec, iFileSpec2, s, diffType, b, b2, b3 );
  }

  public Map<String, Object>[] execMapCmd( String s, String[] strings, Map<String,
      Object> stringObjectMap ) throws ConnectionException, RequestException, AccessException
  {
    return _server.execMapCmd( s, strings, stringObjectMap );
  }

  public Map<String, Object>[] execInputStringMapCmd( String s, String[] strings, String s2 ) throws P4JavaException
  {
    return _server.execInputStringMapCmd( s, strings, s2 );
  }

  public Map<String, Object>[] execQuietMapCmd( String s, String[] strings, Map<String, Object> stringObjectMap )
      throws ConnectionException, RequestException, AccessException
  {
    return _server.execQuietMapCmd( s, strings, stringObjectMap );
  }

  public InputStream execStreamCmd( String s, String[] strings ) throws ConnectionException, RequestException,
      AccessException
  {
    return _server.execStreamCmd( s, strings );
  }

  public InputStream execQuietStreamCmd( String s, String[] strings ) throws ConnectionException, RequestException,
      AccessException
  {
    return _server.execQuietStreamCmd( s, strings );
  }

  public void execStreamingMapCommand( String s, String[] strings, Map<String, Object> stringObjectMap,
                                       IStreamingCallback iStreamingCallback, int i ) throws P4JavaException
  {
    _server.execStreamingMapCommand( s, strings, stringObjectMap, iStreamingCallback, i );
  }

  public void execInputStringStreamingMapComd( String s, String[] strings, String s2,
                                               IStreamingCallback iStreamingCallback, int i ) throws P4JavaException
  {
    _server.execInputStringStreamingMapComd( s, strings, s2, iStreamingCallback, i );
  }

  public String getCounter( String s ) throws ConnectionException, RequestException, AccessException
  {
    return _server.getCounter( s );
  }

  public void setCounter( String s, String s2, boolean b ) throws ConnectionException, RequestException, AccessException
  {
    _server.setCounter( s, s2, b );
  }

  public void deleteCounter( String s, boolean b ) throws ConnectionException, RequestException, AccessException
  {
    _server.deleteCounter( s, b );
  }

  public Map<String, String> getCounters() throws ConnectionException, RequestException, AccessException
  {
    return _server.getCounters();
  }

  public List<IDbSchema> getDbSchema( List<String> strings ) throws ConnectionException, RequestException,
      AccessException
  {
    return _server.getDbSchema( strings );
  }

  public List<Map<String, Object>> getExportRecords( boolean b, long l, int i, long l2, boolean b2, String s,
                                                     String s2 ) throws ConnectionException, RequestException,
      AccessException
  {
    return _server.getExportRecords( b, l, i, l2, b2, s, s2 );
  }
}
