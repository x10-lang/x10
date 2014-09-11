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

import x10.core.Any;
import x10.core.Rail;
import x10.rtt.NamedType;
import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.rtt.Types;
import x10.serialization.X10JavaSerializer;

import java.io.IOException;

public final class NativeFile extends java.io.File implements Any {

	// TODO
//	public NativeFile(java.lang.System[] $dummy){}

    public NativeFile(String pathname) {
        super(pathname);
    }
    
    @Override
    public String getCanonicalPath() {
        try {
            return super.getCanonicalPath();
        } catch (java.io.IOException e) {
            throw new x10.io.IOException(e.getMessage());
        }
    }

    public Rail listInternal() {
        return x10.runtime.impl.java.ArrayUtils.makeRailFromJavaArray(Types.STRING, list());
    }

    // Following workaround is no longer required.
//    // XTENLANG-2680
//    public String getCanonicalPath$O() {
//        try {
//            return super.getCanonicalPath();
//        } catch (java.io.IOException e) {
//            throw x10.runtime.impl.java.ThrowableUtils.getCorrespondingX10Throwable(e);
//        }
//    }
//
//    // XTENLANG-2680
//    public String getAbsolutePath$O() {
//    	return super.getAbsolutePath();
//    }
//
//    // XTENLANG-2680
//    public boolean exists$O() {
//		return super.exists();
//	}
//
//    // XTENLANG-2680
//    public boolean isDirectory$O() {
//		return super.isDirectory();
//	}
//
//    // XTENLANG-2680
//    public boolean isFile$O() {
//		return super.isFile();
//	}
//
//    // XTENLANG-2680
//    public boolean canRead$O() {
//		return super.canRead();
//	}
//
//    // XTENLANG-2680
//    public boolean canWrite$O() {
//		return super.canWrite();
//	}
//
//    // XTENLANG-2680
//    public boolean isHidden$O() {
//		return super.isHidden();
//	}
//
//    // XTENLANG-2680
//    public long lastModified$O() {
//		return super.lastModified();
//	}
//
//    // XTENLANG-2680
//    public long length$O() {
//		return super.length();
//	}
//
//    // XTENLANG-2680
//    public boolean setLastModified$O(long time) {
//		return super.setLastModified(time);
//	}

	//
    // Runtime type information
    //
    public static final RuntimeType<NativeFile> $RTT = NamedType.<NativeFile> make(
        "x10.io.File.NativeFile",
        NativeFile.class
    );
    public RuntimeType<NativeFile> $getRTT() { return $RTT; }
    public Type<?> $getParam(int i) { return null; }

    private Object writeReplace() throws java.io.ObjectStreamException {
        return new x10.serialization.SerializationProxy(this);
    }

    public void $_serialize(X10JavaSerializer serializer) throws IOException {
        throw new x10.io.NotSerializableException("Cannot serialize " + getClass());
    }

    public short $_get_serialization_id() {
        throw new x10.io.NotSerializableException("Cannot serialize " + getClass());
    }

}
