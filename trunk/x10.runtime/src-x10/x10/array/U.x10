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

    public static def unsupported(o: Any, op: String): RuntimeException {
        return unsupported(o.typeName() + " does not support " + op);
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

    public static final class IllegalOperationException extends RuntimeException {
        public def this(message: String) { super(message); }
        public def fillInStackTrace(): Throwable { return this; }
    }

    public static final class UnsupportedOperationException extends RuntimeException {
        public def this(message: String) { super(message); }
        public def fillInStackTrace(): Throwable { return this; }
    }
}
