package x10.lang;

public interface Iterator[IteratorT] {
        public def hasNext(): boolean;
        public def next(): IteratorT;
}
