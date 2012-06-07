/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.core.io;

import x10.rtt.NamedType;
import x10.rtt.RuntimeType;
import x10.rtt.Type;

public class FileOutputStream extends OutputStream {
    
    private static final long serialVersionUID = 1L;

    // constructor just for allocation
    public FileOutputStream(java.lang.System[] $dummy) {
        super($dummy);
    }

    public final FileOutputStream x10$io$FileReader$FileOutputStream$$init$S(String name, boolean append) {
        try {
            // XTENLANG-3063
//            super.$init(new java.io.FileOutputStream(name, append));
            super.x10$io$OutputStreamWriter$OutputStream$$init$S(new java.io.FileOutputStream(name, append));
            return this;
        } catch (java.io.FileNotFoundException e) {
            throw x10.core.ThrowableUtilities.getCorrespondingX10Throwable(e);
        }
    }
    // XTENLANG-3063
    // not used if X10PrettyPrinterVisitor.supportConstructorWithThrows == true
    public FileOutputStream $init(String name, boolean append) {
        return x10$io$FileReader$FileOutputStream$$init$S(name, append);
    }

    // not used
//    // creation method for java code (factory method)
//    public static FileOutputStream $make(String name, boolean append) {
//        return new FileOutputStream((java.lang.System[]) null).$init(name, append);
//    }
    // creation method for java code (1-phase java constructor)
    public FileOutputStream(String name, boolean append) {
        this((java.lang.System[]) null);
        // XTENLANG-3063
//        $init(name, append);
        x10$io$FileReader$FileOutputStream$$init$S(name, append);
    }


    //
    // Runtime type information
    //
    public static final RuntimeType<FileOutputStream> $RTT = NamedType.<FileOutputStream> make(
        "x10.io.FileWriter.FileOutputStream",
        FileOutputStream.class,
        new Type[] { OutputStream.$RTT }
    );
    public RuntimeType<?> $getRTT() { return $RTT; }
    public Type<?> $getParam(int i) { return null; }

}
