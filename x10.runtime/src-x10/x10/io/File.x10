/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.io;

import x10.compiler.NativeRep;
import x10.compiler.Native;
import x10.compiler.Incomplete;

/**
 * Represents a file path.
 * Modeled after java.nio.file.Path.
 *
 * Usage:
 * <pre>
 * try {
 *    val input = new File(inputFileName);
 *    val output = new File(outputFileName);
 *    val p = output.printer();
 *    for (line in input.lines()) {
 *       p.println(line);
 *    }
 *    p.flush();
 * } catch (IOException) { }
 * </pre>
 */
public class File {
    @NativeRep("java", "x10.core.io.NativeFile", null, "x10.core.io.NativeFile.$RTT")
    @NativeRep("c++", "x10::io::File__NativeFile*", "x10::io::File__NativeFile", null)
    protected final static class NativeFile {
        native def this(String);

        @Native("java", "#this.getName()")
        native def getName(): String;
        
        @Native("java", "#this.getParent()")
        native def getParent(): String;
        
        @Native("java", "#this.getPath()")
        native def getPath(): String;

        @Native("java", "#this.isAbsolute()")
        native def isAbsolute(): Boolean;

        @Native("java", "#this.getAbsolutePath()")
        native def getAbsolutePath(): String;

        @Native("java", "#this.getCanonicalPath()")
        native def getCanonicalPath(): String; //throws IOException;

        @Native("java", "#this.canRead()")
        native def canRead(): Boolean;

        @Native("java", "#this.canWrite()")
        native def canWrite(): Boolean;

        @Native("java", "#this.exists()")
        native def exists(): Boolean;

        @Native("java", "#this.isDirectory()")
        native def isDirectory(): Boolean;

        @Native("java", "#this.isFile()")
        native def isFile(): Boolean;

        @Native("java", "#this.isHidden()")
        native def isHidden(): Boolean;

        @Native("java", "#this.lastModified()")
        native def lastModified(): Long;

        @Native("java", "#this.length()")
        native def length(): Long;

        @Native("java", "#this.setLastModified(#v)")
        native def setLastModified(v:Long): Boolean;

        @Native("java", "#this.delete()")
        @Native("c++", "(#this)->del()") // delete is a keyword in C++, we we need to rename method in @Native
        native def delete(): Boolean;

        @Native("java", "#this.listInternal()")
        native def list(): Rail[String];

        @Native("java", "#this.mkdir()")
        native def mkdir(): Boolean;

        @Native("java", "#this.mkdirs()")
        native def mkdirs(): Boolean;

        @Native("java", "#this.renameTo(#dest)")
        native def renameTo(dest:NativeFile): Boolean;
    }

/*
FileSystem operations

    public boolean createNewFile()       throws java.io.IOException;
    public void deleteOnExit();
    public java.lang.String[] list(java.io.FilenameFilter);
    public java.io.File[] listFiles();
    public java.io.File[] listFiles(java.io.FilenameFilter);
    public java.io.File[] listFiles(java.io.FileFilter);
    public boolean setReadOnly();
    public static java.io.File[] listRoots();
    public static java.io.File createTempFile(java.lang.String, java.lang.String, java.io.File)       throws java.io.IOException;
    public static java.io.File createTempFile(java.lang.String, java.lang.String)       throws java.io.IOException;
*/

    public static SEPARATOR: Char = '/';
    public static PATH_SEPARATOR: Char = ':';

    // TODO: This is questionable.
    //       What does it mean to send a File to another node?
    val parent: File;
    val name: String;
    val absolute: Boolean;

    public def this(fullName: String) {
        val i = fullName.lastIndexOf(SEPARATOR);
        if (i == 0n) {
                parent = null;
                name = fullName;
                absolute = true;
        }
        else if (i >= 0n) {
                parent = new File(fullName.substring(0n, i));
                name = fullName.substring(i+1n, fullName.length());
                absolute = fullName.charAt(0n) == PATH_SEPARATOR;
        }
        else {
                parent = null;
                name = fullName;
                absolute = false;
        }
    }

    public def this(p: File, n: String) {
        assert p != null;
        parent = p;
        name = n;
        absolute = p != null && p.absolute;
    }

    public def toString():String = getPath();

    public def lines(): ReaderIterator[String] //throws IOException 
     = openRead().lines();
    public def chars(): ReaderIterator[Char] //throws IOException
     = openRead().chars();
    public def bytes(): ReaderIterator[Byte] //throws IOException 
    = openRead().bytes();
    public def openRead():FileReader //throws IOException 
    = new FileReader(this);
    public def openWrite() = openWrite(false);
    public def openWrite(append:Boolean):FileWriter //throws IOException 
    = new FileWriter(this, append);
    public def printer() = printer(false);
    public def printer(append:Boolean):Printer //throws IOException 
    = new Printer(openWrite(append));

    public def getName(): String = name;
    public def getParentFile(): File = parent;
    public def getPath(): String = parent == null ? name : (parent.getPath() + SEPARATOR + name);
    public def isAbsolute(): Boolean = absolute;

    protected def nativeFile()  = new NativeFile(getPath());

    public def getAbsoluteFile(): File = new File(nativeFile().getAbsolutePath());
    public def getCanonicalFile(): File  // throws IOException 
    = new File(nativeFile().getCanonicalPath());

    public def exists(): Boolean = nativeFile().exists();

    // OS-specific
    @Incomplete def isSymbolicLink(): Boolean {         throw new UnsupportedOperationException();}
    @Incomplete def isAlias(): Boolean  {         throw new UnsupportedOperationException(); }
    @Incomplete def hardLinkCount(): Boolean {         throw new UnsupportedOperationException(); }
    @Incomplete def inodeNumber(): Long {         throw new UnsupportedOperationException(); }
    @Incomplete def permissions(): Int {         throw new UnsupportedOperationException();} 
    // FilePermission;
    
    public def isDirectory(): Boolean = nativeFile().isDirectory();
    public def isFile(): Boolean = nativeFile().isFile();
    public def isHidden(): Boolean = nativeFile().isHidden();

    public def lastModified(): Long = nativeFile().lastModified();
    public def setLastModified(t:Long): Boolean = nativeFile().setLastModified(t);
    public def size(): Long = nativeFile().length();

    @Incomplete public def compareTo(f:File): Int {         throw new UnsupportedOperationException();}

    public def canRead(): Boolean = nativeFile().canRead();
    public def canWrite(): Boolean = nativeFile().canWrite();

    public def delete(): Boolean = nativeFile().delete();
    public def list(): Rail[String] = nativeFile().list();
    public def mkdir(): Boolean = nativeFile().mkdir();
    public def mkdirs(): Boolean = nativeFile().mkdirs();
    public def renameTo(dest:File): Boolean = nativeFile().renameTo(dest.nativeFile());
}
