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

import x10.core.Rail;
import x10.core.Ref;
import x10.rtt.NamedType;
import x10.rtt.RuntimeType;
import x10.rtt.Type;

public class InputStream extends Ref {
 
    private Object writeReplace() throws java.io.ObjectStreamException {
        return new x10.serialization.SerializationProxy(this);
    }

    public void $_serialize(x10.serialization.X10JavaSerializer $serializer) throws java.io.IOException {
        // TODO need check
        $serializer.write(stream);
    }
    public static x10.serialization.X10JavaSerializable $_deserialize_body(InputStream $_obj, x10.serialization.X10JavaDeserializer $deserializer) throws java.io.IOException {
        // TODO need check
        $_obj.stream = (java.io.InputStream) $deserializer.readObject();
        return $_obj;
    }
    public static x10.serialization.X10JavaSerializable $_deserializer(x10.serialization.X10JavaDeserializer $deserializer) throws java.io.IOException {
        InputStream $_obj = new InputStream((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
    }

    private java.io.InputStream stream;
    
    public java.io.InputStream getJavaInputStream() {
        return stream;
    }

    // constructor just for allocation
    public InputStream(java.lang.System[] $dummy) {
        super($dummy);
    }
    
    public final InputStream x10$io$InputStreamReader$InputStream$$init$S(java.io.InputStream stream) {
        this.stream = stream;
        return this;
    }
    
    // creation method for java code (1-phase java constructor)
    public InputStream(java.io.InputStream stream) {
        this((java.lang.System[]) null);
        x10$io$InputStreamReader$InputStream$$init$S(stream);
    }
    
    public void close() {
        try {
            stream.close();
        } catch (java.io.IOException e) {
            throw new x10.io.IOException(e.getMessage());
        }
    }
    
    public int read() {
        try {
            return stream.read();
        } catch (java.io.IOException e) {
            throw new x10.io.IOException(e.getMessage());
        }
    }
    
    // XTENLANG-2680
    public int read$O() {
        try {
            return stream.read();
        } catch (java.io.IOException e) {
            throw new x10.io.IOException(e.getMessage());
        }
    }

    public void read(byte[] b, int off, int len) {
        try {
            stream.read(b, off, len);
        } catch (java.io.IOException e) {
            throw new x10.io.IOException(e.getMessage());
        }
    }

    // XTENLANG-2680
    // LONG_RAIL: unsafe int cast
    public void read__0$1x10$lang$Byte$2(Rail r, long off, long len) {
        try {
            stream.read(r.getByteArray(), (int)off, (int)len);
        } catch (java.io.IOException e) {
            throw new x10.io.IOException(e.getMessage());
        }
    }
    
    public long available() {
        try {
            return (long) stream.available();
        } catch (java.io.IOException e) {
            throw new x10.io.IOException(e.getMessage());
        }
    }
    
    // XTENLANG-2680
    public long available$O() {
        try {
            return (long) stream.available();
        } catch (java.io.IOException e) {
            throw new x10.io.IOException(e.getMessage());
        }
    }

    public void skip(long n) {
        try {
            stream.skip(n);
        } catch (java.io.IOException e) {
            throw new x10.io.IOException(e.getMessage());
        }
    }
    
    // LONG_RAIL: unsafe int cast
    public void mark(long readlimit) {
        stream.mark((int) readlimit);
    }
    
    public void reset() {
        try {
            stream.reset();
        } catch (java.io.IOException e) {
            throw new x10.io.IOException(e.getMessage());
        }
    }
    
    public boolean markSupported() {
        return stream.markSupported();
    }
    
    // XTENLANG-2680
    public boolean markSupported$O() {
        return stream.markSupported();
    }

    //
    // Runtime type information
    //
    public static final RuntimeType<InputStream> $RTT = NamedType.<InputStream> make(
        "x10.io.InputStreamReader.InputStream",
        InputStream.class
    );
    public RuntimeType<?> $getRTT() { return $RTT; }
    public Type<?> $getParam(int i) { return null; }

}
