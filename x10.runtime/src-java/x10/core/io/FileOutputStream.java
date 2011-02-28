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

import x10.rtt.RuntimeType;
import x10.rtt.Type;

public class FileOutputStream extends OutputStream {
    
	private static final long serialVersionUID = 1L;

    public FileOutputStream(String name) throws java.io.FileNotFoundException {
        super(new java.io.FileOutputStream(name));
    }
    
    //
    // Runtime type information
    //
    public static final RuntimeType<FileOutputStream> $RTT = new RuntimeType<FileOutputStream>(
        FileOutputStream.class,
        new Type[] { OutputStream.$RTT }
    ) {
        @Override
        public String typeName() {
            return "x10.io.FileWriter.FileOutputStream";
        }
    };
    public RuntimeType<?> $getRTT() { return $RTT; }
    public Type<?> $getParam(int i) { return null; }

}
