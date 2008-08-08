package x10.lang;

public interface Indexable[I,V] extends (I)=>V {
    def get(i: I): V;
}

