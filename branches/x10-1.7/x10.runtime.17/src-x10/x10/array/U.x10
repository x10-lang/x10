// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

import x10.io.Console;

public class U {

    public static def pr(s: String): void {
        Console.OUT.println(s);
    }

    public static def xxx(s: String): void {
        Console.OUT.println("xxx " + s);
    }

    public static def where(s: String): void {
        //printStackTrace doesn't exist anymore
        Console.ERR.println("where("+s+") called!");
        //new Exception(s).printStackTrace();
    }

    public static def unsupported(o: Object, op: String): RuntimeException {
        return unsupported(o/*.getClass().getName()*/ + " does not support " + op);
    }
    
    public static def unsupported(o: Value, op: String): RuntimeException {
        return unsupported(o/*.getClass().getName()*/ + " does not support " + op);
    }

    public static def illegal(): RuntimeException {
        return illegal("illegal operation");
    }

    public static def unsupported(msg: String): RuntimeException {
        return new UnsupportedOperationException(msg);
    }

    public static def illegal(msg: String): RuntimeException {
        return new IllegalOperationException(msg);
    }
}
