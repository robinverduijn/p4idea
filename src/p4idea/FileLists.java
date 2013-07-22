package p4idea;

import com.google.common.collect.Lists;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vfs.VirtualFile;
import com.perforce.p4java.core.file.IFileSpec;
import com.perforce.p4java.impl.generic.core.file.FileSpec;

import java.io.File;
import java.util.*;

public class FileLists
{
  public static List<IFileSpec> fromVirtualFiles( VirtualFile[] virtualFiles )
  {
    List<IFileSpec> fileSpecs = Lists.newArrayList();
    for ( VirtualFile virtualFile : virtualFiles )
    {
      fileSpecs.add( new FileSpec( virtualFile.getPath() ) );
    }
    return fileSpecs;
  }

  public static List<IFileSpec> fromFilePaths( Collection<FilePath> filePaths )
  {
    List<IFileSpec> fileSpecs = Lists.newArrayList();
    for ( FilePath filePath : filePaths )
    {
      fileSpecs.add( new FileSpec( filePath.getPath() ) );
    }
    return fileSpecs;
  }

  public static List<IFileSpec> fromStrings( Collection<String> files )
  {
    List<IFileSpec> fileSpecs = Lists.newArrayList();
    for ( String file : files )
    {
      fileSpecs.add( new FileSpec( file ) );
    }
    return fileSpecs;
  }

  public static FilePath removeFromList( Collection<FilePath> dirtyFiles, File file )
  {
    Iterator<FilePath> iter = dirtyFiles.iterator();
    while ( iter.hasNext() )
    {
      FilePath dirtyFile = iter.next();
      if ( FileUtil.filesEqual( dirtyFile.getIOFile(), file ) )
      {
        iter.remove();
        return dirtyFile;
      }
    }
    return null;
  }

  public static List<IFileSpec> mergeLocalPaths( List<IFileSpec> haveList, List<IFileSpec> whereList )
  {
    for ( IFileSpec have : haveList )
    {
      IFileSpec where = findInList( whereList, have.getDepotPathString() );
      if ( null != where )
      {
        have.setLocalPath( where.getLocalPathString() );
      }
    }
    return haveList;
  }

  private static IFileSpec findInList( List<IFileSpec> fileSpecs, String depotPath )
  {
    for ( IFileSpec file : fileSpecs )
    {
      if ( depotPath.equals( file.getDepotPathString() ) )
      {
        return file;
      }
    }
    return null;
  }

  public static List<String> getDepotPaths( List<IFileSpec> fileSpecs )
  {
    final List<String> depotPaths = new ArrayList<>( fileSpecs.size() );
    for ( IFileSpec file : fileSpecs )
    {
      depotPaths.add( file.getDepotPathString() );
    }
    return depotPaths;
  }
}
