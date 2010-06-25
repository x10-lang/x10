/**
 A simple program illustrating idiom for (parallel) construction of a rail.

 @author vj (Appeared in the Final Exam for PPPP at Columbia U, Fall 2009.)
 */
class Fibs {
    def fib(i:Int):Int = i < 2 ? 1 : fib(i-1) + fib(i-2);
    def fibs(a:ValRail[Int]):ValRail[Int] 
	= Rail.makeVal[Int](a.length, (i:Nat)=> fib(a(i)));
}
