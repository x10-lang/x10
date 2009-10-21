package x10.util;

import x10.compiler.Native;

public class StringBuilder implements Builder[Object,String] {
    val buf: ValRailBuilder[Char]!;

    public def this() {
        buf = new ValRailBuilder[Char]();
    }

    public def add(o:Object): StringBuilder {
        if (o == null)
            return addString("null");
        else
            return addString(o.toString());
    }

    public def add(x:Boolean) = addString(x.toString());
    public def add(x:Byte) = addString(x.toString());
    public def add(x:Char) = addString(x.toString());
    public def add(x:Short) = addString(x.toString());
    public def add(x:Int) = addString(x.toString());
    public def add(x:Long) = addString(x.toString());
    public def add(x:Float) = addString(x.toString());
    public def add(x:Double) = addString(x.toString());
    
    protected def addString(s: String): StringBuilder {
        for (var i: int = 0; i < s.length(); i++) {
            val ch = s(i);
            buf.add(ch);
        }
        // BROKEN code gen
        /*
        for (ch in s.chars()) {
            buf.add(ch);
        }
        */
        return this;
    }
    
    public def length() {
        return buf.length();
    }
    
    @Native("java", "new String(#1.getCharArray())")
    @Native("c++", "x10aux::vrc_to_string(#1)")
    private static native def makeString(ValRail[Char]): String;

    public def result(): String {
        return makeString(buf.result());
    }
    
    public def toString() = result();
}
