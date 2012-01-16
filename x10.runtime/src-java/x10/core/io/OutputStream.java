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
import x10.rtt.Types;

public class OutputStream extends Ref {
  
	private static final long serialVersionUID = 1L;

    // XTENLANG-2680
    /*private*/public java.io.OutputStream stream;

    // constructor just for allocation
    public OutputStream(java.lang.System[] $dummy) {
        super($dummy);
    }

    public OutputStream $init(java.io.OutputStream stream) {
        this.stream = stream;
        return this;
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
    
    // XTENLANG-2680
    // for !Emitter.manglePrimitivesAsShortName
    public void write__0$1x10$lang$Byte$2(x10.array.Array buf) {
        try {
            stream.write(buf.raw().getByteArray());
        } catch (java.io.IOException e) {
            throw x10.core.ThrowableUtilities.getCorrespondingX10Exception(e);
        }
    }
//    // for Emitter.manglePrimitivesAsShortName
//    public void write__0$1$B$2(x10.array.Array buf) {
//        try {
//            stream.write(buf.raw().getByteArray());
//        } catch (java.io.IOException e) {
//            throw x10.core.ThrowableUtilities.getCorrespondingX10Exception(e);
//        }
//    }

    public void write(byte[] b, int off, int len) {
        try {
            stream.write(b, off, len);
        } catch (java.io.IOException e) {
            throw x10.core.ThrowableUtilities.getCorrespondingX10Exception(e);
        }
    }
    
    // XTENLANG-2680
    // for !Emitter.manglePrimitivesAsShortName
    public void write__0$1x10$lang$Byte$2(x10.array.Array buf, int off, int len) {
        try {
            stream.write(buf.raw().getByteArray(), off, len);
        } catch (java.io.IOException e) {
            throw x10.core.ThrowableUtilities.getCorrespondingX10Exception(e);
        }
    }
//    // for Emitter.manglePrimitivesAsShortName
//    public void write__0$1$B$2(x10.array.Array buf, int off, int len) {
//        try {
//            stream.write(buf.raw().getByteArray(), off, len);
//        } catch (java.io.IOException e) {
//            throw x10.core.ThrowableUtilities.getCorrespondingX10Exception(e);
//        }
//    }

    public static OutputStream getNativeOutputStream(x10.io.Writer w) {
        OutputStream os = null;
        x10.io.Writer ww = w;
        while (true) {
            if (ww instanceof x10.io.FilterWriter) {
                ww = ((x10.io.FilterWriter) ww).w;
            }
            else if (ww instanceof x10.io.OutputStreamWriter) {
                os = ((x10.io.OutputStreamWriter) ww).out;
                break;
            }
            else if (ww instanceof x10.io.StringWriter) {
                // TODO
                assert false;
                break;
            }
            else {
                if (ww != null) {
                    // TODO unknown subtype of Writer
                    assert false;
                }
                break;
            }
        }
        return os;
    }

    //
    // Runtime type information
    //
    public static final RuntimeType<OutputStream> $RTT = NamedType.<OutputStream> make(
        "x10.io.OutputStreamWriter.OutputStream",
        OutputStream.class,
        new Type[] { Types.OBJECT }
    );
    @Override
    public RuntimeType<?> $getRTT() { return $RTT; }
    @Override
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
        public static final RuntimeType<WriterOutputStream> $RTT = NamedType.<WriterOutputStream> make(
            "x10.io.OutputStreamWriter.OutputStream.WriterOutputStream",
            WriterOutputStream.class,
            new Type[] { OutputStream.$RTT }
        );
        public RuntimeType<?> $getRTT() { return $RTT; }
        public Type<?> $getParam(int i) { return null; }
    }

}
