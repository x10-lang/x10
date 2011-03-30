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

	private FileInputStream(String name) throws java.io.FileNotFoundException {
        super(new java.io.FileInputStream(name));
    }

    public static FileInputStream make(String name) {
        try {
            return new FileInputStream(name);
        } catch (java.io.FileNotFoundException e) {
            throw x10.core.ThrowableUtilities.getCorrespondingX10Exception(e);
        }
    }
    
    //
    // Runtime type information
    //
    public static final RuntimeType<FileInputStream> $RTT = new NamedType<FileInputStream>(
        "x10.io.FileReader.FileInputStream",
        FileInputStream.class,
        new Type[] { InputStream.$RTT }
    );
    public RuntimeType<?> $getRTT() { return $RTT; }
    public Type<?> $getParam(int i) { return null; }

}
