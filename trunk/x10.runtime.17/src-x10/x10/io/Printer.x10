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
package x10.io;

import x10.compiler.Native;

public class Printer extends FilterWriter {
    public def this(w: Writer) { super(w); }

    private const NEWLINE = '\n'; // System.getProperty("line.separator");

    public def println(): Void = print(NEWLINE);
    
    public def print(o: Object): Void {
        if (o == null)
            print("null");
        else
            print(o.toString());
    }

    public def print(s: String): Void {
        try {
            for (var i: int = 0; i < s.length(); i++) {
                val ch = s(i);
                writeChar(ch);
            }
        }
        catch (e: IOException) {
            throw new IORuntimeException(e.getMessage());
        }
    }

    public def println(o: Object): Void {
        print(o);
        println();
    }
    
    public def println(s: String): Void {
        print(s);
        println();
    }
    
    public def printf(fmt: String): Void { printf(fmt, []); }
    public def printf(fmt: String, o1: Object): Void { printf(fmt, [o1]); }
    public def printf(fmt: String, o1: Object, o2: Object): Void { printf(fmt, [o1,o2]); }
    public def printf(fmt: String, o1: Object, o2: Object, o3: Object): Void { printf(fmt, [o1,o2,o3]); }
    public def printf(fmt: String, o1: Object, o2: Object, o3: Object, o4: Object): Void { printf(fmt, [o1,o2,o3,o4]); }
    public def printf(fmt: String, o1: Object, o2: Object, o3: Object, o4: Object, o5: Object): Void { printf(fmt, [o1,o2,o3,o4,o5]); }
    public def printf(fmt: String, o1: Object, o2: Object, o3: Object, o4: Object, o5: Object, o6: Object): Void { printf(fmt, [o1,o2,o3,o4,o5,o6]); }
    
    public def printf(fmt: String, args: Rail[Object]): Void { println(String.format(fmt, args)); }
    public def printf(fmt: String, args: ValRail[Object]): Void { println(String.format(fmt, args)); }
}
