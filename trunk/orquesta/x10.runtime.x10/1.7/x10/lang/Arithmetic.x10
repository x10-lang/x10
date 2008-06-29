package x10.lang;

/**

 */
public interface Arithmetic[T]{T <: Arithmetic[T]} {
    def add(x: T): T;
    def sub(x: T): T;
    def mul(x: T): T;
    def div(x: T): T;
    def mod(x: T): T;
    def cosub(x: T): T;
    def codiv(x: T): T;
    def neginv(): T;
    def mulinv(): T;
    
    // static def zero(): T;
    // static def unit(): T;
}
