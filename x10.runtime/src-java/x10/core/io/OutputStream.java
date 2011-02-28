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

import x10.core.Ref;
import x10.rtt.RuntimeType;
import x10.rtt.Type;

public class OutputStream extends Ref {
  
	private static final long serialVersionUID = 1L;

    private final java.io.OutputStream stream;

    // NOTE: be careful when using this constructor, all APIs must be overridden.
    public OutputStream() {
        this.stream = null;
    }

    public OutputStream(java.io.OutputStream stream) {
        this.stream = stream;
    }
    
    public void close() {
        try {
            stream.close();
        } catch (java.io.IOException e) {
            throw x10.core.ThrowableUtilities.getCorrespondingX10Exception(e);
        }
    }
    
    public void flush() {
        try {
            stream.flush();
        } catch (java.io.IOException e) {
            throw x10.core.ThrowableUtilities.getCorrespondingX10Exception(e);
        }
    }
    
    public void write(int b) {
        try {
            stream.write(b);
        } catch (java.io.IOException e) {
            throw x10.core.ThrowableUtilities.getCorrespondingX10Exception(e);
        }
    }
    
    public void write(byte[] b) {
        try {
            stream.write(b);
        } catch (java.io.IOException e) {
            throw x10.core.ThrowableUtilities.getCorrespondingX10Exception(e);
        }
    }
    
    public void write(byte[] b, int off, int len) {
        try {
            stream.write(b, off, len);
        } catch (java.io.IOException e) {
            throw x10.core.ThrowableUtilities.getCorrespondingX10Exception(e);
        }
    }
    
    //
    // Runtime type information
    //
    public static final RuntimeType<OutputStream> $RTT = new RuntimeType<OutputStream>(
        OutputStream.class,
        new Type[] { x10.rtt.Types.OBJECT }
    ) {
        @Override
        public String typeName() {
            return "x10.io.OutputStreamWriter.OutputStream";
        }
    };
    public RuntimeType<?> $getRTT() { return $RTT; }
    public Type<?> $getParam(int i) { return null; }

}
