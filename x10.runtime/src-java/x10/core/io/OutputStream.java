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
import x10.rtt.NamedType;
import x10.rtt.RuntimeType;
import x10.rtt.Type;

public class OutputStream extends Ref {
  
	private static final long serialVersionUID = 1L;

    private java.io.OutputStream stream;

    public OutputStream(java.lang.System[] $dummy) {
        super($dummy);
    }

    public void $init(java.io.OutputStream stream) {
        this.stream = stream;
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
    public static final RuntimeType<OutputStream> $RTT = new NamedType<OutputStream>(
        "x10.io.OutputStreamWriter.OutputStream",
        OutputStream.class,
        new Type[] { x10.rtt.Types.OBJECT }
    );
    public RuntimeType<?> $getRTT() { return $RTT; }
    public Type<?> $getParam(int i) { return null; }


    //
    // NOTE: this class is only used in @Native annotation of x10.io.Writer.getNativeOutputStream()
    //
    public static class WriterOutputStream extends OutputStream {
        private static final long serialVersionUID = 1L;
        private x10.io.Writer w;

        public WriterOutputStream(java.lang.System[] $dummy) {
            super($dummy);
        }

        public void $init(x10.io.Writer w) {
            // NOTE: since the backing stream is not set, all APIs of OutputStream must be overridden.
            super.$init((java.io.OutputStream)null);
            this.w = w;
        }
        
        public WriterOutputStream(x10.io.Writer w) {
            // NOTE: since the backing stream is not set, all APIs of OutputStream must be overridden.
            super((java.io.OutputStream)null);
            this.w = w;
        }

        @Override
        public void write(int x) {
            w.write((byte) x);
        }
        @Override
        public void close() {
            throw new x10.lang.UnsupportedOperationException();
        }
        @Override
        public void flush() {
            throw new x10.lang.UnsupportedOperationException();
        }
        @Override
        public void write(byte[] b) {
            throw new x10.lang.UnsupportedOperationException();
        }
        @Override
        public void write(byte[] b, int off, int len) {
            throw new x10.lang.UnsupportedOperationException();
        }
        public static final RuntimeType<WriterOutputStream> $RTT = new NamedType<WriterOutputStream>(
            "x10.io.OutputStreamWriter.OutputStream.WriterOutputStream",
            WriterOutputStream.class,
            new Type[] { OutputStream.$RTT }
        );
        public RuntimeType<?> $getRTT() { return $RTT; }
        public Type<?> $getParam(int i) { return null; }
    }

}
