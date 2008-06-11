package x10.lang;

/**

 */
public interface Arithmetic[T where T has static zero() and static unit()] {
    def add(x:T):T;
    def sub(x:T):T;
    def mul(x:T):T;
    def div(x:T):T;
    def cosub(x:T):T;
    def codiv(T x);
    def neginv():T;
    def mulinv():T;
    def eq(y:T):boolean;
    def lt(y:T):boolean;
    def gt(y:T):boolean;
    def le(y:T):boolean;
    def ge(y:T):boolean;
    def ne(y:T):boolean;
}
