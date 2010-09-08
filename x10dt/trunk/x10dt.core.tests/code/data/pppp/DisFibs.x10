/**
 A simple program illustrating idiom for use of distributed array.

 @author vj (Appeared in the Final Exam for PPPP at Columbia U, Fall 2009.)
 */
class DisFibs {
    def fib(i:Int):Int = i < 2 ? 1 : fib(i-1) + fib(i-2);
    def disFibs(b:Array[Int]):Void {
       finish ateach(p in b.dist) 
           b(p) = fib(b(p));
      }
}
