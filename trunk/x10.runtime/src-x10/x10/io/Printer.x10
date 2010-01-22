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

    private const NEWLINE:Char = '\n'; // System.getProperty("line.separator");

    public global def println(): Void = print(NEWLINE);
    
    public final global def println(o:Any): Void {
        print(o==null? "null\n" : o.toString()+"\n");
    }
    public final global def print(o:Any): Void {
    	print(o==null? "null" : o.toString());
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

    public global def printf(fmt: String): Void { printf(fmt, []); }
    public global def printf(fmt: String, o1: Any): Void { printf(fmt, [o1]); }
    public global def printf(fmt: String, o1: Any, o2: Any): Void { printf(fmt, [o1,o2]); }
    public global def printf(fmt: String, o1: Any, o2: Any, o3: Any): Void { printf(fmt, [o1,o2,o3]); }
    public global def printf(fmt: String, o1: Any, o2: Any, o3: Any, o4: Any): Void { 
        printf(fmt, [o1,o2,o3,o4]); 
    }
    public global def printf(fmt: String, o1: Any, o2: Any, o3: Any, o4: Any, o5: Any): Void { 
       printf(fmt, [o1,o2,o3,o4,o5]); 
    }
    public global def printf(fmt: String, o1: Any, o2: Any, o3: Any, o4: Any, o5: Any, o6: Any): Void { 
       printf(fmt, [o1,o2,o3,o4,o5,o6]); 
    }
    public global def printf(fmt: String, args: Rail[Any]): Void { print(String.format(fmt, args)); }
    public global def printf(fmt: String, args: ValRail[Any]): Void { print(String.format(fmt, args)); }
        
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
