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

public class FileInputStream extends InputStream {
    
    private static final long serialVersionUID = 1L;

    // constructor just for allocation
    public FileInputStream(java.lang.System[] $dummy) {
        super($dummy);
    }

    public final FileInputStream x10$io$FileReader$FileInputStream$$init$S(String name) {
        try {
            // XTENLANG-3063
//            super.$init(new java.io.FileInputStream(name));
            super.x10$io$InputStreamReader$InputStream$$init$S(new java.io.FileInputStream(name));
            return this;
        } catch (java.io.FileNotFoundException e) {
            throw x10.core.ThrowableUtilities.getCorrespondingX10Throwable(e);
        }
    }
    // XTENLANG-3063
    // not used if X10PrettyPrinterVisitor.supportConstructorWithThrows == true
    public FileInputStream $init(String name) {
        return x10$io$FileReader$FileInputStream$$init$S(name);
    }

    // not used
//    // creation method for java code (factory method)
//    public static FileInputStream $make(String name) {
//        return new FileInputStream((java.lang.System[]) null).$init(name);
//    }
    // creation method for java code (1-phase java constructor)
    public FileInputStream(String name) {
        this((java.lang.System[]) null);
        // XTENLANG-3063
//        $init(name);
        x10$io$FileReader$FileInputStream$$init$S(name);
    }


    //
    // Runtime type information
    //
    public static final RuntimeType<FileInputStream> $RTT = NamedType.<FileInputStream> make(
        "x10.io.FileReader.FileInputStream",
        FileInputStream.class,
        new Type[] { InputStream.$RTT }
    );
    public RuntimeType<?> $getRTT() { return $RTT; }
    public Type<?> $getParam(int i) { return null; }

}
