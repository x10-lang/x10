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

import x10.core.RefI;
import x10.rtt.RuntimeType;
import x10.rtt.Type;

public class NativeFile extends java.io.File implements RefI {

	private static final long serialVersionUID = 1L;

    public NativeFile(String pathname) {
        super(pathname);
    }
    
    @Override
    public String getCanonicalPath() {
        try {
            return super.getCanonicalPath();
        } catch (java.io.IOException e) {
            throw x10.core.ThrowableUtilities.getCorrespondingX10Exception(e);
        }
    }

    //
    // Runtime type information
    //
    public static final RuntimeType<NativeFile> $RTT = new RuntimeType<NativeFile>(
        NativeFile.class,
        new Type[] { x10.rtt.Types.OBJECT }
    ) {
        @Override
        public String typeName() {
            return "x10.io.File.NativeFile";
        }
    };
    public RuntimeType<NativeFile> $getRTT() { return $RTT; }
    public Type<?> $getParam(int i) { return null; }

}
