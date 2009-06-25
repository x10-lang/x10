package x10.lang;

public interface SetOps_T {

    T $not();
    T $and(T that);
    T $or(T that);
    T $minus(T that);
}
