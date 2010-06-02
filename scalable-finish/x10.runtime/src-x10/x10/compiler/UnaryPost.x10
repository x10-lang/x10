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

package x10.compiler;

import x10.compiler.Native;

public abstract class UnaryPost {
    @Native("java", "x10.core.UnaryPost.beforeIncrement(#1)")
    public static global safe def beforeIncrement(v:Byte): Byte = (v - 1) as Byte;
    @Native("java", "x10.core.UnaryPost.beforeDecrement(#1)")
    public static global safe def beforeDecrement(v:Byte): Byte = (v + 1) as Byte;
    @Native("java", "x10.core.UnaryPost.beforeIncrement(#1)")
    public static global safe def beforeIncrement(v:Short): Short = (v - 1) as Short;
    @Native("java", "x10.core.UnaryPost.beforeDecrement(#1)")
    public static global safe def beforeDecrement(v:Short): Short = (v + 1) as Short;
    @Native("java", "x10.core.UnaryPost.beforeIncrement(#1)")
    public static global safe def beforeIncrement(v:Int): Int = v - 1;
    @Native("java", "x10.core.UnaryPost.beforeDecrement(#1)")
    public static global safe def beforeDecrement(v:Int): Int = v + 1;
    @Native("java", "x10.core.UnaryPost.beforeIncrement(#1)")
    public static global safe def beforeIncrement(v:Long): Long = v - 1L;
    @Native("java", "x10.core.UnaryPost.beforeDecrement(#1)")
    public static global safe def beforeDecrement(v:Long): Long = v + 1L;
    @Native("java", "x10.core.UnaryPost.beforeIncrement(#1)")
    public static global safe def beforeIncrement(v:Float): Float = v - 1.0F;
    @Native("java", "x10.core.UnaryPost.beforeDecrement(#1)")
    public static global safe def beforeDecrement(v:Float): Float = v + 1.0F;
    @Native("java", "x10.core.UnaryPost.beforeIncrement(#1)")
    public static global safe def beforeIncrement(v:Double): Double = v - 1.0;
    @Native("java", "x10.core.UnaryPost.beforeDecrement(#1)")
    public static global safe def beforeDecrement(v:Double): Double = v + 1.0;
}
