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

import x10.compiler.Native;

public final class Unsafe {

    static class Token {}

    private static val token = new Token();

    public static def allocRailUninitialized[T](size:Long):Rail[T]{self.size==size} {
        return new Rail[T](token, size, false);
    }

    public static def allocRailUninitialized[T](size:Int):Rail[T] {
        return new Rail[T](token, size, false);
    }

    public static def allocRailZeroed[T](size:Long):Rail[T]{self.size==size} {
        return new Rail[T](token, size, true);
    }

    public static def allocRailZeroed[T](size:Int):Rail[T] {
        return new Rail[T](token, size, true);
    }

    public static def clearRail[T](x:Rail[T]):void {
        x.clear();
    }

    public static def clearRail[T](x:Rail[T], start:long, numElems:long):void {
        x.clear(start, numElems);
    }

    @Native("c++", "x10aux::dealloc(#o)")
    public static def dealloc[T](o:T){ T isref } :void {}
}
