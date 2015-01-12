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

package x10.core.io;

import x10.rtt.NamedType;
import x10.rtt.RuntimeType;
import x10.rtt.Type;

/*
 * NOTE: In X10, all FileInputStreams are buffered (matches libC)
 */
public class FileInputStream extends InputStream {

    // constructor just for allocation
    public FileInputStream(java.lang.System[] $dummy) {
        super($dummy);
    }

    public final FileInputStream x10$io$FileReader$FileInputStream$$init$S(String name) {
        try {
            super.x10$io$InputStreamReader$InputStream$$init$S(new java.io.BufferedInputStream(new java.io.FileInputStream(name)));
            return this;
        } catch (java.io.FileNotFoundException e) {
            throw new x10.io.FileNotFoundException(e.getMessage());
        }
    }

    // creation method for java code (1-phase java constructor)
    public FileInputStream(String name) {
        this((java.lang.System[]) null);
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
