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

public /*value*/ class Printer extends FilterWriter {
    public def this(w: Writer) { super(w); }

    private const NEWLINE = '\n'; // System.getProperty("line.separator");

    public def println(): Void = print(new Nullable[Char](NEWLINE));
    
    public def print(o: Object): Void {
        if (o == null)
            print("null");
        else
            print(o.toString());
    }
    
    public def print(o: Byte) = print(o.toString());
    public def print(o: Short) = print(o.toString());
    public def print(o: Int) = print(o.toString());
    public def print(o: Long) = print(o.toString());
    public def print(o: Float) = print(o.toString());
    public def print(o: Double) = print(o.toString());
    public def print(o: Boolean) = print(o.toString());
    public def print(o: Char) = print(o.toString());

    public def print(s: String): Void {
        try {
            val b = s.bytes();
            write(b, 0, b.length);
        }
        catch (e: IOException) {
            throw new IORuntimeException(e.getMessage());
        }
    }

    public def println(o: Object): Void {
        print(o);
        println();
    }
    
    public def println(o: Byte) = println(o.toString());
    public def println(o: Short) = println(o.toString());
    public def println(o: Int) = println(o.toString());
    public def println(o: Long) = println(o.toString());
    public def println(o: Float) = println(o.toString());
    public def println(o: Double) = println(o.toString());
    public def println(o: Boolean) = println(o.toString());
    public def println(o: Char) = println(o.toString());
    
    public def println(s: String): Void {
        print(s);
        println();
    }
    
    
    public final def printf(fmt: String): void
        { printf(fmt, []); }
    
    public final def printf[S](fmt: String, s:S): void
        { printf(fmt, [new Nullable[S](s)]); }
    
    public final def printf[S,T](fmt: String, s:S, t:T): void
        { printf(fmt, [new Nullable[S](s),new Nullable[T](t)]); }
    
    public final def printf[S,T,U](fmt: String, s:S, t:T, u:U): void
        { printf(fmt, [new Nullable[S](s),new Nullable[T](t),
                       new Nullable[U](u)]); }
    
    public final def printf[S,T,U,V](fmt: String, s:S, t:T, u:U, v:V): void
        { printf(fmt, [new Nullable[S](s),new Nullable[T](t),
                       new Nullable[U](u),new Nullable[V](v)]); }
    
    public final def printf[S,T,U,V,W](fmt: String, s:S, t:T, u:U, v:V, w:W): void
        { printf(fmt, [new Nullable[S](s),new Nullable[T](t),
                       new Nullable[U](u),new Nullable[V](v),
                       new Nullable[W](w)]); }
    
    public final def printf[S,T,U,V,W,X](fmt: String, s:S, t:T, u:U, v:V, w:W, x:X): void
        { printf(fmt, [new Nullable[S](s),new Nullable[T](t),
                       new Nullable[U](u),new Nullable[V](v),
                       new Nullable[W](w),new Nullable[X](x)]); }
    
    public final def printf[S,T,U,V,W,X,Y](fmt: String, s:S, t:T, u:U, v:V, w:W, x:X, y:Y): void
        { printf(fmt, [new Nullable[S](s),new Nullable[T](t),
                       new Nullable[U](u),new Nullable[V](v),
                       new Nullable[W](w),new Nullable[X](x),
                       new Nullable[Y](y)]); }
                       
    public final def printf[S,T,U,V,W,X,Y,Z](fmt: String, s:S, t:T, u:U, v:V, w:W, x:X, y:Y, z:Z): void
        { printf(fmt, [new Nullable[S](s),new Nullable[T](t),
                       new Nullable[U](u),new Nullable[V](v),
                       new Nullable[W](w),new Nullable[X](x),
                       new Nullable[Y](y),new Nullable[Z](z)]); }
    
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
