package x10.lang;

public value class Range[T](low: T, high: T){T <: Comparable[T]} implements Contains[T], ContainsAll[Iterable[T]], Iterable[T] {
    def contains(x: T) = low <= x && x <= high;
    def containsAll(xs: Iterable[T]) = {
        for (x: T in xs)
            if (! (x in this)) return false;
        true
    }
    def iterator(): Iterator[T] = new RangeIterator[T](low, high);
    
    static class RangeIterator[T] implements Iterator[T] {
        var current: T;
        val high: T;
        def this[T](low: T, high: T) = {
            this.current = low;
            this.high = high;
        }
        def hasNext() = current <= high;
        def next() = { return current++; }
    }
}