/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "x10.core.MultipleExceptions")
public class MultipleExceptions extends RuntimeException {
    public native def this(s:ValRail[Throwable]);
    
    @Native("java", "(#0).exceptions()")
    native public def exceptions(): ValRail[Exception];
}
