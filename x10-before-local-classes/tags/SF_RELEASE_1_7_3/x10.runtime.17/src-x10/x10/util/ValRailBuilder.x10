package x10.util;

public class ValRailBuilder[T] implements Builder[T,ValRail[T]] {
    val buf: GrowableRail[T];

    public def this() {
        buf = new GrowableRail[T]();
    }

    public def this(size: Int) {
        buf = new GrowableRail[T](size);
    }

    public def add(x: T): ValRailBuilder[T] {
        buf.add(x);
        return this;
    }
    
    public def length(): Int {
        return buf.length();
    }

    public def result(): ValRail[T] {
        return buf.toValRail();
    }
}

