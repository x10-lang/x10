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

package x10.core.io;

import x10.core.Rail;
import x10.core.Ref;
import x10.rtt.NamedType;
import x10.rtt.RuntimeType;
import x10.rtt.Type;

public class OutputStream extends Ref {
  
    private Object writeReplace() throws java.io.ObjectStreamException {
        return new x10.serialization.SerializationProxy(this);
    }

    public void $_serialize(x10.serialization.X10JavaSerializer $serializer) throws java.io.IOException {
        // TODO need check
        $serializer.write(stream);
    }
    public static x10.serialization.X10JavaSerializable $_deserialize_body(OutputStream $_obj, x10.serialization.X10JavaDeserializer $deserializer) throws java.io.IOException {
        // TODO need check
        $_obj.stream = (java.io.OutputStream) $deserializer.readObject();
        return $_obj;
    }
    public static x10.serialization.X10JavaSerializable $_deserializer(x10.serialization.X10JavaDeserializer $deserializer) throws java.io.IOException {
        OutputStream $_obj = new OutputStream((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
    }

    // XTENLANG-2680
    /*private*/public java.io.OutputStream stream;

    // constructor just for allocation
    public OutputStream(java.lang.System[] $dummy) {
        super($dummy);
    }

    public final OutputStream x10$io$OutputStreamWriter$OutputStream$$init$S(java.io.OutputStream stream) {
        this.stream = stream;
        return this;
    }
    
    // creation method for java code (1-phase java constructor)
    public OutputStream(java.io.OutputStream stream) {
        this((java.lang.System[]) null);
        x10$io$OutputStreamWriter$OutputStream$$init$S(stream);
    }
    
    public void close() {
        try {
            stream.close();
        } catch (java.io.IOException e) {
            throw new x10.io.IOException(e.getMessage());
        }
    }
    
    public void flush() {
        try {
            stream.flush();
        } catch (java.io.IOException e) {
            throw new x10.io.IOException(e.getMessage());
        }
    }
    
    public void write(int b) {
        try {
            stream.write(b);
        } catch (java.io.IOException e) {
            throw new x10.io.IOException(e.getMessage());
        }
    }
    
    public void write(byte[] b) {
        try {
            stream.write(b);
        } catch (java.io.IOException e) {
            throw new x10.io.IOException(e.getMessage());
        }
    }

    public void write(String s) {
        try {
            stream.write(s.getBytes());
        } catch (java.io.IOException e) {
            throw new x10.io.IOException(e.getMessage());
        }
    }
    
    // XTENLANG-2680
    public void write__0$1x10$lang$Byte$2(Rail buf) {
        try {
            stream.write(buf.getByteArray());
        } catch (java.io.IOException e) {
            throw new x10.io.IOException(e.getMessage());
        }
    }

    public void write(byte[] b, int off, int len) {
        try {
            stream.write(b, off, len);
        } catch (java.io.IOException e) {
            throw new x10.io.IOException(e.getMessage());
        }
    }
    
    // XTENLANG-2680
    // LONG_RAIL: unsafe int cast
    public void write__0$1x10$lang$Byte$2(Rail buf, long off, long len) {
        try {
            stream.write(buf.getByteArray(), (int)off, (int)len);
        } catch (java.io.IOException e) {
            throw new x10.io.IOException(e.getMessage());
        }
    }

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
        OutputStream.class
    );
    public RuntimeType<?> $getRTT() { return $RTT; }
    public Type<?> $getParam(int i) { return null; }


    //
    // NOTE: this class is only used in @Native annotation of x10.io.Writer.getNativeOutputStream()
    //
    public static class WriterOutputStream extends OutputStream {
        private x10.io.Writer w;

        public WriterOutputStream(java.lang.System[] $dummy) {
            super($dummy);
        }

        public final WriterOutputStream x10$io$OutputStreamWriter$OutputStream$WriterOutputStream$$init$S(x10.io.Writer w) {
            // NOTE: since the backing stream is not set, all APIs of OutputStream must be overridden.
            super.x10$io$OutputStreamWriter$OutputStream$$init$S((java.io.OutputStream)null);
            this.w = w;
            return this;
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
            throw new java.lang.UnsupportedOperationException();
        }
        @Override
        public void flush() {
            throw new java.lang.UnsupportedOperationException();
        }
        @Override
        public void write(byte[] b) {
            throw new java.lang.UnsupportedOperationException();
        }
        @Override
        public void write(byte[] b, int off, int len) {
            throw new java.lang.UnsupportedOperationException();
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
