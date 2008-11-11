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
    
    // DO NOT CALL: this is public only so @Native annotations in other packages can use it.
    public static def getNative(w: Writer): OutputStreamWriter.OutputStream {
        if (w instanceof OutputStreamWriter) {
            return (w as OutputStreamWriter).stream();
        }
        else if (w instanceof FilterWriter) {
            return getNative( (w as FilterWriter).inner() );
        }
        throw new IORuntimeException("Could not get native output stream");
    }
    
    @Native("java", "new java.io.PrintStream(x10.io.Printer.getNative(#0)).printf(#1)")
    public incomplete def printf(fmt: String): Void;
    @Native("java", "new java.io.PrintStream(x10.io.Printer.getNative(#0)).printf(#1, #2)")
    public incomplete def printf(fmt: String, o1: Object): Void;
    @Native("java", "new java.io.PrintStream(x10.io.Printer.getNative(#0)).printf(#1, #2, #3)")
    public incomplete def printf(fmt: String, o1: Object, o2: Object): Void;
    @Native("java", "new java.io.PrintStream(x10.io.Printer.getNative(#0)).printf(#1, #2, #3, #4)")
    public incomplete def printf(fmt: String, o1: Object, o2: Object, o3: Object): Void;
    @Native("java", "new java.io.PrintStream(x10.io.Printer.getNative(#0)).printf(#1, #2, #3, #4, #5)")
    public incomplete def printf(fmt: String, o1: Object, o2: Object, o3: Object, o4: Object): Void;
    @Native("java", "new java.io.PrintStream(x10.io.Printer.getNative(#0)).printf(#1, #2, #3, #4, #5, #6)")
    public incomplete def printf(fmt: String, o1: Object, o2: Object, o3: Object, o4: Object, o5: Object): Void;
    
    @Native("java", "new java.io.PrintStream(x10.io.Printer.getNative(#0)).printf(#1, (Object[]) #2.getBackingArray())")
    public incomplete def printf(fmt: String, args: Rail[Object]): Void;
    
    @Native("java", "new java.io.PrintStream(x10.io.Printer.getNative(#0)).printf(#1, (Object[]) #2.getBackingArray())")
    public incomplete def printf(fmt: String, args: ValRail[Object]): Void;
        
}
