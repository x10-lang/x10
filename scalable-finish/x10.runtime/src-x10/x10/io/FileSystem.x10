/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.io;

/**
 * Usage:
 *
 * try {
 *   val in = new File(inputFileName);
 *   val out = new File(outputFileName);
 *   val p = out.printer();
 *   for (line in in.lines()) {
 *      line = line.chop();
 *      p.println(line);
 *   }
 * }
 * catch (IOException e) { }
 */    
public abstract class FileSystem {
    const SEPARATOR_CHAR: Char = '/'; // System.getProperty("x10.io.fileSeparator")(0);
    const SEPARATOR: String = "/"; // System.getProperty("x10.io.fileSeparator");
    const PATH_SEPARATOR_CHAR: Char = ':'; // System.getProperty("x10.io.pathSeparator")(0);
    const PATH_SEPARATOR: String = ":"; // System.getProperty("x10.io.pathSeparator");

    incomplete def delete(File) : Void throws IOException;
    incomplete def deleteOnExit(File) : Void throws IOException;
    incomplete def rename(f: File, t: File) : Void throws IOException;
    incomplete def mkdir(File) : Void throws IOException;
    incomplete def mkdirs(File) : Void throws IOException;
    incomplete def exists(File) : Boolean;
    incomplete def size(File) : Long throws IOException;

    incomplete def listFiles(File): ValRail[File] throws IOException;
    incomplete def listFiles(File, (File) => Boolean): ValRail[File] throws IOException;
}
