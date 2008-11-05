package x10.util;

public class StringBuilder implements Builder[Object,String] {
    val buf: ValRailBuilder[Char];

    public def this() {
        buf = new ValRailBuilder[Char]();
        len = 0;
    }

    public def add(o: Object): StringBuilder {
        if (o == null)
            return addString("null");
        else
            return addString(o.toString());
    }
    
    protected def addString(s: String): StringBuilder {
        for (ch in s.chars()) {
            buf.add(ch);
        }
        return this;
    }
    
    @Native("java", "new String(#1.getCharArray())")
    private static native def makeString(ValRail[Char]);

    public def result(): String {
        return makeString(buf.result());
    }
    
    public def toString() = result();
}
