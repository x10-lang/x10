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

package x10.compiler;

/** 
 * A class that allows transmission of compiler command line 
 * flags to to code in the class libraries that wants to check
 * compilation modes.
 */
public class CompilerFlags { 

    /**
     * @return <code>true</code> if the compiler was invoked with
     *          the -NO_CHECKS flags, <code>false</code> otherwise.
     */
    @Native("java", "(!`NO_CHECKS`)")
    @Native("c++", "BOUNDS_CHECK_BOOL")
    @CompileTimeConstant("!NO_CHECKS")
    public static native def checkBounds():Boolean;

    /**
     * @return <code>true</code> if the compiler was invoked with
     *          the -NO_CHECKS flags, <code>false</code> otherwise.
     */
    @Native("java", "(!`NO_CHECKS`)")
    @Native("c++", "PLACE_CHECK_BOOL")
    @CompileTimeConstant("!NO_CHECKS")
    public static native def checkPlace():Boolean;

    /**
     * Should bounds checking operations be optimized by using unsigned compares
     */
    @Native("java", "true")
    @Native("c++", "true")
    public static native def useUnsigned():Boolean;

    /**
     * A false that is not understood by the constant propagator
     */
    @Native("java", "false")
    @Native("c++", "false")
    public static native def FALSE():Boolean;
    
    /**
     * A "true" that is not understood by the constant propagator
     */
    @Native("java", "true")
    @Native("c++",  "true")
    public static native def TRUE():Boolean;
    
}
