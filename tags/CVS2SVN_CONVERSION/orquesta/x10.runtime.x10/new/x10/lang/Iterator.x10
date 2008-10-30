package x10.lang;

public interface Iterator[+T] {
    def hasNext(): boolean;
    def next(): T;
}
