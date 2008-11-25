/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.compiler.Native;
import x10.io.Console;
import x10.util.Timer;

public class System {

    private def this() {}
    
    public const err = Console.ERR;
    public const input = Console.IN;
    public const out = Console.OUT;

    public static def currentTimeMillis() = Timer.milliTime();
    public static def nanoTime() = Timer.nanoTime();

    @Native("java", "java.lang.System.exit(#1)")
    @Native("c++", "x10aux::system_utils::exit(#1)")
    public static native def exit(code: Int):void;

    public static def exit() = exit(-1);

    /* TODO: XTENLANG-180.  Provide full System properties API in straight X10 */
    @Native("java", "java.lang.System.setProperty(#1,#2)")
    @Native("c++", "assert(false)") // FIXME: Trivial definition to allow XRX compilation to go through.
    public static native def setProperty(p:String,v:String):void;
}
