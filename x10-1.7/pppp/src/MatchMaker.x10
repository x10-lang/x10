 interface Person {
   def setPartner(p:Person):Void;
  // returns the value set by the last call to setPartner.
   def partner():Person;
 }
/** 
    matchMaker may be invoked simultaneously. Each invocation <code>m.matchMaker(a)</code> must
    wait until some other invocation <code>m.matchMaker(b)</code> has happened. Now both
    calls should be able to return. After the return, it must be the case that 
    <code>a.partner()</code> returns <code>b</code> and vice versa. 
 */
public class MatchMaker {
     static class Pair {
	 a: Person;
	 b: Person;
	 def this(a) {
	     this.a=a;
	 }
	 def pairup(b:Person) {
	     a.partner(b);
	     b.partner(a);
	     // pair up before setting this.b
	     this.b=b; 
	 }
     }
     var p:Pair;
     def matchMaker(b:Person) {
	 atomic {
	     if (p != null) {
		 p.pairup(b);
		 return;
	     }
	     p = new Pair(b);
	 }
	 // cannot return until partner is known.
	 await p.b != null;
   }
 }
