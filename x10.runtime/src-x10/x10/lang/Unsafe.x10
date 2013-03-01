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

package x10.lang;

import x10.compiler.Inline;
import x10.util.IndexedMemoryChunk;

public final class Unsafe {

    static class Token {}

    private static val token = new Token();

    public static def allocRailUninitialized[T](size:Long) {
        return new Rail[T](token, size);
    }

    public static def allocRailUninitialized[T](size:Int) {
        return new Rail[T](token, size);
    }
}
