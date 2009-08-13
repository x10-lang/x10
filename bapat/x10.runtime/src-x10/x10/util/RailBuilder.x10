package x10.util;

public class RailBuilder[T] implements Builder[T,Rail[T]] {
    val buf: GrowableRail[T];

    public def this() {
        buf = new GrowableRail[T]();
    }

    public def this(size: Int) {
        buf = new GrowableRail[T](size);
    }

    public def add(x: T): RailBuilder[T] {
        buf.add(x);
        return this;
    }

    public def length(): Int {
        return buf.length();
    }

    public def result(): Rail[T] {
        return buf.toRail();
    }
}

