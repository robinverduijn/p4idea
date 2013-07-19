package p4idea.perforce;

import com.google.common.io.Files;
import com.intellij.openapi.vcs.FilePath;
import com.perforce.p4java.exception.AccessException;
import com.perforce.p4java.exception.ConnectionException;
import p4idea.P4Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

public class P4Ignore
{
  public Collection<FilePath> p4ignore( Collection<FilePath> files ) throws ConnectionException, AccessException
  {
    Iterator<FilePath> iter = files.iterator();
    while ( iter.hasNext() )
    {
      File file = iter.next().getIOFile();
      File p4ignore = findP4Ignore( file );
      if ( null != p4ignore )
      {
        if ( getsIgnored( p4ignore, file ) )
        {
          iter.remove();
        }
      }
    }
    return files;
  }

  private File findP4Ignore( File file ) throws ConnectionException, AccessException
  {
    if ( file.isDirectory() )
    {
      File p4ignore = new File( file, ".p4ignore" );
      if ( p4ignore.exists() && p4ignore.isFile() )
      {
        return p4ignore;
      }
    }
    File parentFile = file.getParentFile();
    if ( null != parentFile && P4Wrapper.getP4().isValidMapping( parentFile.getAbsolutePath() ) )
    {
      return findP4Ignore( parentFile );
    }
    return null;
  }

  private boolean getsIgnored( File p4ignore, File file )
  {
    try
    {
      List<String> lines = Files.readLines( p4ignore, Charset.defaultCharset() );
      for ( String line : lines )
      {
        line = line.trim();
        if ( line.startsWith( "#" ) )
        {
          continue;
        }
        if ( file.getName().matches( line.replaceAll( "\\*", "(.*)" ) ) )
        {
          return true;
        }
      }
    }
    catch ( IOException e )
    {
      P4Logger.getInstance().error( String.format( "Error reading %s", p4ignore ), e );
    }
    return false;
  }
}
