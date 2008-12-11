package x10.util;

import x10.compiler.Native;

public class StringBuilder implements Builder[Object,String] {
    val buf: ValRailBuilder[Char];

    public def this() {
        buf = new ValRailBuilder[Char]();
    }

    public def add(o: Object): Builder[Object,String] {
        if (o == null)
            return addString("null");
        else
            return addString(o.toString());
    }
    
    protected def addString(s: String): Builder[Object,String]  {
        for (ch in s.chars()) {
            buf.add(ch);
        }
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
