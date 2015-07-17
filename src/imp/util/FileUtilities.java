/*
 * Utilities to copy files and directories
 */
package imp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 *
 * @author Graham Smith and Hazel Rudge-Pickard
 * http://www.crazysquirrel.com/computing/java/basics/java-file-and-directory-copying.jspx
 */

public class FileUtilities {

  private FileUtilities() {}
  
  public static final void copy( File source, File destination ) throws IOException {
    if( source.isDirectory() ) {
      copyDirectory( source, destination );
    } else {
      copyFile( source, destination );
    }
  }
  
  public static final void copyDirectory( File source, File destination ) throws IOException {
    if( !source.isDirectory() ) {
      throw new IllegalArgumentException( "Source (" + source.getPath() + ") must be a directory." );
    }
    
    if( !source.exists() ) {
      throw new IllegalArgumentException( "Source directory (" + source.getPath() + ") doesn't exist." );
    }
    
    if( destination.exists() ) {
      throw new IllegalArgumentException( "Destination (" + destination.getPath() + ") exists." );
    }
    
    destination.mkdirs();
    File[] files = source.listFiles();
    
    for( File file : files ) {
      if( file.isDirectory() ) {
        copyDirectory( file, new File( destination, file.getName() ) );
      } else {
        copyFile( file, new File( destination, file.getName() ) );
      }
    }
  }
  
  public static final void copyFile( File source, File destination ) throws IOException {
    FileChannel sourceChannel = new FileInputStream( source ).getChannel();
    FileChannel targetChannel = new FileOutputStream( destination ).getChannel();
    sourceChannel.transferTo(0, sourceChannel.size(), targetChannel);
    sourceChannel.close();
    targetChannel.close();
  }
}    

