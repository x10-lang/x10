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
public value Printer extends FilterWriter {
    public def this(w: Writer) { super(w); }

    private const NEWLINE = '\n'; // System.getProperty("line.separator");

    public def println(): Void = print(NEWLINE as Box[Char]);
    
    public def print(o:Object): Void {
        if (o == null)
            print("null");
        else
            print(o.toString());
    }

    public def print(s:String): Void {
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
    public final def print[T](x:T){T<:Primitive} = print(x.toString());

    public final def println[T](x:T): Void {
        print(x);
        println();
    }
    */

    public final def print(x:Boolean) = print(x.toString());
    public final def print(x:Byte) = print(x.toString());
    public final def print(x:Char) = print(x.toString());
    public final def print(x:Short) = print(x.toString());
    public final def print(x:Int) = print(x.toString());
    public final def print(x:Long) = print(x.toString());
    public final def print(x:Float) = print(x.toString());
    public final def print(x:Double) = print(x.toString());


    public final def println(x:Object): Void {
        print(x);
        println();
    }
    public final def println(x:Boolean): Void {
        print(x);
        println();
    }
    public final def println(x:Byte): Void {
        print(x);
        println();
    }
    public final def println(x:Char): Void {
        print(x);
        println();
    }
    public final def println(x:Short): Void {
        print(x);
        println();
    }
    public final def println(x:Int): Void {
        print(x);
        println();
    }
    public final def println(x:Long): Void {
        print(x);
        println();
    }
    public final def println(x:Float): Void {
        print(x);
        println();
    }
    public final def println(x:Double): Void {
        print(x);
        println();
    }
    
    public def printf(fmt: String): Void { printf(fmt, []); }
    public def printf(fmt: String, o1: Object): Void { printf(fmt, [o1]); }
    public def printf(fmt: String, o1: Object, o2: Object): Void { printf(fmt, [o1,o2]); }
    public def printf(fmt: String, o1: Object, o2: Object, o3: Object): Void { printf(fmt, [o1,o2,o3]); }
    public def printf(fmt: String, o1: Object, o2: Object, o3: Object, o4: Object): Void { printf(fmt, [o1,o2,o3,o4]); }
    public def printf(fmt: String, o1: Object, o2: Object, o3: Object, o4: Object, o5: Object): Void { printf(fmt, [o1,o2,o3,o4,o5]); }
    public def printf(fmt: String, o1: Object, o2: Object, o3: Object, o4: Object, o5: Object, o6: Object): Void { printf(fmt, [o1,o2,o3,o4,o5,o6]); }
    
    public def printf(fmt: String, args: Rail[Object]): Void { print(String.format(fmt, args)); }
    public def printf(fmt: String, args: ValRail[Object]): Void { print(String.format(fmt, args)); }
    
        
    public def flush(): Void {
        try {
            super.flush();
        }
        catch (IOException) { }
    }
    
    public def close(): Void {
        try {
            super.close();
        }
        catch (IOException) { }
    }
    
}
