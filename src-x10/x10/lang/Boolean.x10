/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "boolean", "x10.core.BoxedBoolean", "x10.types.Type.BOOLEAN")
@NativeRep("c++", "x10_boolean", "x10_boolean", null)
public final value Boolean {
    // Binary and unary operations and conversions are built-in.  No need to declare them here.
    
    @Native("java", "true")
    @Native("c++", "true")
    public const TRUE = true;

    @Native("java", "false")
    @Native("c++", "false")
    public const FALSE = false;

    @Native("java", "java.lang.Boolean.toString(#0)")
    @Native("c++", "x10aux::to_string(#0)")
    public native def toString(): String;
    
    @Native("java", "java.lang.Boolean.parseBoolean(#1)")
    @Native("c++", "x10aux::boolean_utils::parseBoolean(#1)")
    public native static def parseBoolean(String): Boolean;
}
