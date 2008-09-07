package x10.core;

public interface Settable<D,R> {
    R set(R v, D i);
}
