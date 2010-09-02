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
    var p:Person;
    def matchMaker(q:Person):Person {
	 atomic {
	     if (p != null) {
		 p.setPartner(q);
		 q.setPartner(p);
		 return p;
	     }
	     p = q;
	 }
	 // cannot return until partner is known.
	 await q.partner()!= null;
   }
 }
