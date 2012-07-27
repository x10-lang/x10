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

public class InputStream extends Ref {
  
    private static final long serialVersionUID = 1L;

    private java.io.InputStream stream;

    // constructor just for allocation
    public InputStream(java.lang.System[] $dummy) {
        super($dummy);
    }
    
    public final InputStream x10$io$InputStreamReader$InputStream$$init$S(java.io.InputStream stream) {
        this.stream = stream;
        return this;
    }
    // XTENLANG-3063
    // not used if X10PrettyPrinterVisitor.supportConstructorWithThrows == true
    public InputStream $init(java.io.InputStream stream) {
        return x10$io$InputStreamReader$InputStream$$init$S(stream);
    }
    
    // creation method for java code (1-phase java constructor)
    public InputStream(java.io.InputStream stream) {
        this((java.lang.System[]) null);
        // XTENLANG-3063
//        $init(stream);
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
    public void read__0$1x10$lang$Byte$2(x10.array.Array r, int off, int len) {
        try {
            stream.read(r.raw().getByteArray(), off, len);
        } catch (java.io.IOException e) {
            throw new x10.io.IOException(e.getMessage());
        }
    }
    
    public int available() {
        try {
            return stream.available();
        } catch (java.io.IOException e) {
            throw new x10.io.IOException(e.getMessage());
        }
    }
    
    // XTENLANG-2680
    public int available$O() {
        try {
            return stream.available();
        } catch (java.io.IOException e) {
            throw new x10.io.IOException(e.getMessage());
        }
    }

    public void skip(int n) {
        try {
            stream.skip(n);
        } catch (java.io.IOException e) {
            throw new x10.io.IOException(e.getMessage());
        }
    }
    
    public void mark(int readlimit) {
        stream.mark(readlimit);
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
        InputStream.class,
        new Type[] { Types.OBJECT }
    );
    public RuntimeType<?> $getRTT() { return $RTT; }
    public Type<?> $getParam(int i) { return null; }

}
