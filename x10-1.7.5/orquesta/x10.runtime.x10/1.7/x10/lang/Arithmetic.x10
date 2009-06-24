package x10.lang;

public interface Arithmetic[T](enabled: boolean) {
    def add(x: T){enabled}: T;
    def sub(x: T){enabled}: T;
    def mul(x: T){enabled}: T;
    def div(x: T){enabled}: T;
    def mod(x: T){enabled}: T;
    def cosub(x: T){enabled}: T;
    def codiv(x: T){enabled}: T;
    def neginv(){enabled}: T;
    def mulinv(){enabled}: T;
    
    // static def zero(): T;
    // static def unit(): T;
}
