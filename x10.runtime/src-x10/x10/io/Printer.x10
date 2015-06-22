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

package x10.io;

import x10.compiler.Native;

import x10.util.concurrent.Lock;

/**
 * Usage:
 *
 * try {
 *    val input = new File(inputFileName);
 *    val output = new File(outputFileName);
 *    val p = output.printer();
 *    for (line in input.lines()) {
 *       p.println(line);
 *    }
 *    p.flush();
 * } catch (IOException) { }
 */    
public class Printer extends FilterWriter {
    public def this(w: Writer) { super(w); }

    private static NEWLINE:Char = '\n'; // System.getProperty("line.separator");

    private lock = new Lock();
    
    public def println(): void { print(NEWLINE); }
    
    public final def println(o:Any): void {
        print(o==null? "null\n" : o.toString()+"\n");
    }
    // this is needed to use @Native for CheckedThrowable#toString().
    public final def println(e:CheckedThrowable): void {
    	print(e==null? "null\n" : e.toString()+"\n");
    }
    
    public final def print(o:Any): void {
    	print(o==null? "null" : o.toString());
    }

    public def print(s:String): void {
        lock.lock();
        try {
            if (s == null) {
                write("null");
            } else {
                write(s);
            }
        } finally {
            lock.unlock();
        }
    }
    // this is needed to use @Native for CheckedThrowable#toString().
    public final def print(e:CheckedThrowable): void {
    	print(e==null? "null" : e.toString());
    }

    public def printf(fmt: String): void { printfRail(fmt, new Rail[Any](0)); }
    public def printf(fmt: String, o1: Any): void { printfRail(fmt, [o1 as Any]); }
    public def printf(fmt: String, o1: Any, o2: Any): void { printfRail(fmt, [o1 as Any,o2]); }
    public def printf(fmt: String, o1: Any, o2: Any, o3: Any): void { printfRail(fmt, [o1 as Any,o2,o3]); }
    public def printf(fmt: String, o1: Any, o2: Any, o3: Any, o4: Any): void { printfRail(fmt, [o1 as Any,o2,o3,o4]); }
    public def printf(fmt: String, o1: Any, o2: Any, o3: Any, o4: Any, o5: Any): void { printfRail(fmt, [o1 as Any,o2,o3,o4,o5]); }
    public def printf(fmt: String, o1: Any, o2: Any, o3: Any, o4: Any, o5: Any, o6: Any): void { printfRail(fmt, [o1 as Any,o2,o3,o4,o5,o6]); }

    public def printf(fmt: String, args: Rail[Any]): void { 
        printfRail(fmt, args); 
    }
    public def printfRail(fmt: String, args: Rail[Any]): void { 
        print(String.format(fmt, args));
    }
        
    public def flush(): void {
        try {
            super.flush();
        }
        catch (IOException) { }
    }
    
    public def close(): void {
        try {
            super.close();
        }
        catch (IOException) { }
    }
    
}
