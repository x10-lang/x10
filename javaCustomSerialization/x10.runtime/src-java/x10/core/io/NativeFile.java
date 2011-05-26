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
import x10.rtt.NamedType;
import x10.rtt.RuntimeType;
import x10.rtt.Type;

public class NativeFile extends java.io.File implements RefI {

	private static final long serialVersionUID = 1L;

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
            throw x10.core.ThrowableUtilities.getCorrespondingX10Exception(e);
        }
    }

    // XTENLANG-2680
    public String getCanonicalPath$O() {
        try {
            return super.getCanonicalPath();
        } catch (java.io.IOException e) {
            throw x10.core.ThrowableUtilities.getCorrespondingX10Exception(e);
        }
    }

    // XTENLANG-2680
    public String getAbsolutePath$O() {
    	return super.getAbsolutePath();
    }

    // XTENLANG-2680
    public boolean exists$O() {
		return super.exists();
	}

    // XTENLANG-2680
    public boolean isDirectory$O() {
		return super.isDirectory();
	}

    // XTENLANG-2680
    public boolean isFile$O() {
		return super.isFile();
	}

    // XTENLANG-2680
    public boolean canRead$O() {
		return super.canRead();
	}

    // XTENLANG-2680
    public boolean canWrite$O() {
		return super.canWrite();
	}

    // XTENLANG-2680
    public boolean isHidden$O() {
		return super.isHidden();
	}

    // XTENLANG-2680
    public long lastModified$O() {
		return super.lastModified();
	}

    // XTENLANG-2680
    public long length$O() {
		return super.length();
	}

    // XTENLANG-2680
    public boolean setLastModified$O(long time) {
		return super.setLastModified(time);
	}

	//
    // Runtime type information
    //
    public static final RuntimeType<NativeFile> $RTT = new NamedType<NativeFile>(
        "x10.io.File.NativeFile",
        NativeFile.class,
        new Type[] { x10.rtt.Types.OBJECT }
    );
    public RuntimeType<NativeFile> $getRTT() { return $RTT; }
    public Type<?> $getParam(int i) { return null; }

}
