/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */
package x10.io;

import x10.compiler.NativeRep;
import x10.compiler.Native;

/**
 * Represents a file path.
 * Modeled after java.nio.file.Path.
 *
 * Usage:
 *
 * try {
 *    val input = new File(inputFileName);
 *    val output = new File(outputFileName);
 *    val p = output.printer();
 *    for (line in input.lines()) {
 *       line = line.chop();
 *       p.println(line);
 *    }
 * }
 * catch (IOException) { }
 */
public class File {
    @NativeRep("java", "java.io.File", null, null)
    @NativeRep("c++", "x10aux::ref<x10::io::File__NativeFile>", "x10::io::File__NativeFile", null)
    protected final static class NativeFile {
        native def this(String);

        @Native("java", "#0.getName()")
        @Native("c++", "(#0)->getName()")
        native def getName(): String;
        @Native("java", "#0.getParent()")
        @Native("c++", "(#0)->getParent()")
        native def getParent(): String;
        @Native("java", "#0.getPath()")
        @Native("c++", "(#0)->getPath()")
        native def getPath(): String;

        @Native("java", "#0.isAbsolute()")
        @Native("c++", "(#0)->isAbsolute()")
        native def isAbsolute(): Boolean;

        @Native("java", "#0.getAbsolutePath()")
        @Native("c++", "(#0)->getAbsolutePath()")
        native def getAbsolutePath(): String;
        // here's what we must do if we make IOException non-native
        //@Native("java", "new Object() { String eval(java.io.File f) { try { return f.getCanonicalPath(); } catch (java.io.IOException e) { throw new x10.io.IOException(e.getMessage()); } } }.eval(#0)")
        @Native("java", "#0.getCanonicalPath()")
        @Native("c++", "(#0)->getCanonicalPath()")
        native def getCanonicalPath(): String throws IOException;

        @Native("java", "#0.canRead()")
        @Native("c++", "(#0)->canRead()")
        native def canRead(): Boolean;
        @Native("java", "#0.canWrite()")
        @Native("c++", "(#0)->canWrite()")
        native def canWrite(): Boolean;
        @Native("java", "#0.exists()")
        @Native("c++", "(#0)->exists()")
        native def exists(): Boolean;
        @Native("java", "#0.isDirectory()")
        @Native("c++", "(#0)->isDirectory()")
        native def isDirectory(): Boolean;
        @Native("java", "#0.isFile()")
        @Native("c++", "(#0)->isFile()")
        native def isFile(): Boolean;
        @Native("java", "#0.isHidden()")
        @Native("c++", "(#0)->isHidden()")
        native def isHidden(): Boolean;
        @Native("java", "#0.lastModified()")
        @Native("c++", "(#0)->lastModified()")
        native def lastModified(): Long;
        @Native("java", "#0.length()")
        @Native("c++", "(#0)->length()")
        native def length(): Long;

        @Native("java", "#0.setLastModified(#1)")
        @Native("c++", "(#0)->setLastModified(#1)")
        native def setLastModified(Long): Boolean;
    }

/*
FileSystem operations

    public boolean createNewFile()       throws java.io.IOException;
    public boolean delete();
    public void deleteOnExit();
    public java.lang.String[] list();
    public java.lang.String[] list(java.io.FilenameFilter);
    public java.io.File[] listFiles();
    public java.io.File[] listFiles(java.io.FilenameFilter);
    public java.io.File[] listFiles(java.io.FileFilter);
    public boolean mkdir();
    public boolean mkdirs();
    public boolean renameTo(java.io.File);
    public boolean setReadOnly();
    public static java.io.File[] listRoots();
    public static java.io.File createTempFile(java.lang.String, java.lang.String, java.io.File)       throws java.io.IOException;
    public static java.io.File createTempFile(java.lang.String, java.lang.String)       throws java.io.IOException;
*/

    public const SEPARATOR: Char = '/';
    public const PATH_SEPARATOR: Char = ':';

    val parent: File{self.at(this)};
    val name: String;
    val absolute: Boolean;

    public def this(fullName: String) {
        val i = fullName.lastIndexOf(SEPARATOR);
        if (i == 0) {
                parent = null;
                name = fullName;
                absolute = true;
        }
        else if (i >= 0) {
                parent = new File(fullName.substring(0, i));
                name = fullName.substring(i+1, fullName.length());
                absolute = fullName.charAt(0) == PATH_SEPARATOR;
        }
        else {
                parent = null;
                name = fullName;
                absolute = false;
        }
    }

    public def this(p: File{self.at(here)}, n: String) {
        assert p != null;
        parent = p;
        name = n;
        absolute = p != null && p.absolute;
    }

    // incomplete def this(u: URI);

    public def lines(): ReaderIterator[String] throws IOException = openRead().lines();
    public def chars(): ReaderIterator[Char] throws IOException = openRead().chars();
    public def bytes(): ReaderIterator[Byte] throws IOException = openRead().bytes();
    public def openRead() throws IOException = new FileReader(this);
    public def openWrite() throws IOException = new FileWriter(this);
    public def printer() throws IOException = new Printer(openWrite());

    public def getName(): String = name;
    public def getParentFile(): File{self.at(this)} = parent;
    public def getPath(): String = parent == null ? name : (parent.getPath() + SEPARATOR + name);
    public def isAbsolute(): Boolean = absolute;

    protected def nativeFile()  = new NativeFile(getPath());

    public def getAbsoluteFile(): File{self.at(here)} = new File(nativeFile().getAbsolutePath());
    public def getCanonicalFile(): File{self.at(here)}  throws IOException = new File(nativeFile().getCanonicalPath());

    // incomplete def toURL(): URL;
    // incomplete def toURI(): URI;

    public def exists(): Boolean = nativeFile().exists();

    // OS-specific
    incomplete def isSymbolicLink(): Boolean;
    incomplete def isAlias(): Boolean;
    incomplete def hardLinkCount(): Boolean;
    incomplete def inodeNumber(): Long;
    incomplete def permissions(): Int; // FilePermission;

    public def isDirectory(): Boolean = nativeFile().isDirectory();
    public def isFile(): Boolean = nativeFile().isFile();
    public def isHidden(): Boolean = nativeFile().isHidden();

    public def lastModified(): Long = nativeFile().lastModified();
    public def setLastModified(t:Long): Boolean = nativeFile().setLastModified(t);
    public def size(): Long = nativeFile().length();

    incomplete public def compareTo(File): Int;

    public def canRead(): Boolean = nativeFile().canRead();
    public def canWrite(): Boolean = nativeFile().canWrite();
}
