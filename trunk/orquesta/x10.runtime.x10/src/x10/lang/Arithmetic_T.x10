package x10.lang;

public interface Arithmetic_T {

    T add(T x);
    T sub(T x);
    T mul(T x);
    T div(T x);
    T cosub(T x);
    T codiv(T x);
    T neginv();
    T mulinv();

    boolean eq(T x);
    boolean lt(T x);
    boolean gt(T x);
    boolean le(T x);
    boolean ge(T x);
    boolean ne(T x);
}


