/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.io;

import x10.compiler.Native;

/**
 * Usage:
 *
 * try {
 *   val in = new File(inputFileName);
 *   val out = new File(outputFileName);
 *   val p = out.printer();
 *   for (line in in.lines()) {
 *      line = line.chop();
 *      p.println(line);
 *   }
 * }
 * catch (IOException e) { }
 */    
public class Printer extends FilterWriter {
    public def this(w: Writer) { super(w); }

    private const NEWLINE = '\n'; // System.getProperty("line.separator");

    public global def println(): Void = print(NEWLINE as Box[Char]);
    
    public global def print(o:Object): Void {
        if (o == null)
            print("null");
        else
            print(o.toString());
    }

    public global def print(s:String): Void {
        try {
            val b = s.bytes();
            write(b, 0, b.length);
        }
        catch (e: IOException) {
            throw new IORuntimeException(e.getMessage());
        }
    }

    /* TODO: Top/toString/struct.
             Once front-end changes are made so that the compiler
             understands that all types provide a toString,
             then we can use this nice clean code and kill
             all of the print/println specializations below.
    public final global def print[T](x:T){T<:Primitive} = print(x.toString());

    public final global def println[T](x:T): Void {
        print(x);
        println();
    }
    */

    public final global def print(x:Boolean) = print(x.toString());
    public final global def print(x:Byte) = print(x.toString());
    public final global def print(x:Char) = print(x.toString());
    public final global def print(x:Short) = print(x.toString());
    public final global def print(x:Int) = print(x.toString());
    public final global def print(x:Long) = print(x.toString());
    public final global def print(x:Float) = print(x.toString());
    public final global def print(x:Double) = print(x.toString());


    public final global def println(x:Object): Void {
        print(x);
        println();
    }
    public final global def println(x:Boolean): Void {
        print(x);
        println();
    }
    public final global def println(x:Byte): Void {
        print(x);
        println();
    }
    public final global def println(x:Char): Void {
        print(x);
        println();
    }
    public final global def println(x:Short): Void {
        print(x);
        println();
    }
    public final global def println(x:Int): Void {
        print(x);
        println();
    }
    public final global def println(x:Long): Void {
        print(x);
        println();
    }
    public final global def println(x:Float): Void {
        print(x);
        println();
    }
    public final global def println(x:Double): Void {
        print(x);
        println();
    }
    
    public global def printf(fmt: String): Void { printf(fmt, []); }
    public global def printf(fmt: String, o1: Object): Void { printf(fmt, [o1]); }
    public global def printf(fmt: String, o1: Object, o2: Object): Void { printf(fmt, [o1,o2]); }
    public global def printf(fmt: String, o1: Object, o2: Object, o3: Object): Void { printf(fmt, [o1,o2,o3]); }
    public global def printf(fmt: String, o1: Object, o2: Object, o3: Object, o4: Object): Void { printf(fmt, [o1,o2,o3,o4]); }
    public global def printf(fmt: String, o1: Object, o2: Object, o3: Object, o4: Object, o5: Object): Void { printf(fmt, [o1,o2,o3,o4,o5]); }
    public global def printf(fmt: String, o1: Object, o2: Object, o3: Object, o4: Object, o5: Object, o6: Object): Void { printf(fmt, [o1,o2,o3,o4,o5,o6]); }
    
    public global def printf(fmt: String, args: Rail[Object]): Void { print(String.format(fmt, args)); }
    public global def printf(fmt: String, args: ValRail[Object]): Void { print(String.format(fmt, args)); }
    
        
    public global def flush(): Void {
        try {
            super.flush();
        }
        catch (IOException) { }
    }
    
    public global def close(): Void {
        try {
            super.close();
        }
        catch (IOException) { }
    }
    
}
