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
import x10.io.Console;
import x10.util.Timer;

public class System {

    @NativeRep("java", "java.lang.System", null, null)
    private static class NativeSystem {

        // Hide the constructor.
        private def this() = { }
    
        @Native("java", "java.lang.System.exit(#1)")
        public static native def exit(code: Int): void;
    
        @Native("java", "java.lang.System.exit(-1)")
        public static native def exit():Void;

        public static native def setProperty(p:String, v:String): void;
    }
    
    private def this() {}
    
    public const err = Console.ERR;
    public const input = Console.IN;
    public const out = Console.OUT;
    public static def currentTimeMillis() = Timer.milliTime();
    public static def nanoTime() = Timer.nanoTime();

    public static def exit(code: Int) { NativeSystem.exit();}
    public static def setProperty(p:String,v:String) = NativeSystem.setProperty(p,v);


}
