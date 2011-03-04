package x10.util;
	
public class MapIterator[-S,+T] implements Iterator[T] {
    val i: Iterator[S]!;
    val f: (S) => T;

    def this(i: Iterator[S]!, f: (S) => T) {
        this.i = i;
	    this.f = f;
	}
	    
	public def hasNext(): Boolean = i.hasNext();
	public def next(): T = f(i.next());
}
