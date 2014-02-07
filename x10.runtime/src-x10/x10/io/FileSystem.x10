/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.io;

import x10.compiler.Incomplete;

public abstract class FileSystem {
    static SEPARATOR_CHAR: Char = '/'; // System.getProperty("x10.io.fileSeparator")(0);
    static SEPARATOR: String = "/"; // System.getProperty("x10.io.fileSeparator");
    static PATH_SEPARATOR_CHAR: Char = ':'; // System.getProperty("x10.io.pathSeparator")(0);
    static PATH_SEPARATOR: String = ":"; // System.getProperty("x10.io.pathSeparator");

    @Incomplete def delete(File) : void //throws IOException 
    { throw new UnsupportedOperationException();}
    @Incomplete def deleteOnExit(File) : void //throws IOException
    {         throw new UnsupportedOperationException(); }
    @Incomplete def rename(f: File, t: File) : void //throws IOException
        {         throw new UnsupportedOperationException(); }
    @Incomplete def mkdir(File) : void //throws IOException
    {         throw new UnsupportedOperationException(); }                                              
    @Incomplete def mkdirs(File) : void //throws IOException
    {         throw new UnsupportedOperationException(); }
    @Incomplete def exists(File) : Boolean
            {         throw new UnsupportedOperationException(); }
    @Incomplete def size(File) : Long //throws IOException
    {         throw new UnsupportedOperationException(); }
    @Incomplete def listFiles(File): Rail[File] //throws IOException
    {         throw new UnsupportedOperationException(); }
    @Incomplete def listFiles(File, (File) => Boolean): Rail[File] //throws IOException
    {         throw new UnsupportedOperationException(); }
}
