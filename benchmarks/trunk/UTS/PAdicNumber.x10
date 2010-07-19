/**
 * A representation of a number in K "digits" of base P.
 * Useful in hypercube based routing.
 */
public class PAdicNumber(P:Int, K:Int) {
	global val digits: ValRail[Int]/*(K)*/;
	public static def pow(w:Int, var n:Int) {
		var result:Int=1;
	while (n-- > 0) result *= w;
	return result;
	}
	def pow(n:Int) = pow(P, n);
	public def this(p:Int, k:Int, x:Int):PAdicNumber{self.P==p, self.K==k} {
		property(p,k);
		digits = ValRail.make
	     (k, (i:Int) => { val wi = pow(i); (x % (p*wi))/wi});
	}
	def this (p:Int, k:Int, ds:ValRail[Int]/*(k)*/) {
		property(p, k);
		digits = ds;
	}
	/**
	 * Return the number distance d away along dimension dim (using modulo arithmetic). 
	 */
	public def delta(d:Int, dim:Int)= 
		new PAdicNumber(P, K, ValRail.make(K, (i:Int)=> (i==dim ? (digits(i)+d)% P : digits(i))));
	
	public def toDecimal():Int {
		 var result:Int=digits(K-1);
	     for (var i:Int=K-1; i > 0; i--) {
		    result = result*P + digits(i-1);
	     }
	     return result;
	}
	global safe public def toString() {
		var result:String="";
		for (var i:Int=K-1; i >= 0; i--) {
			result += digits(i);
			if (i > 0)
				result += ".";
		}
		return result;
	}
	
  /*
  public static def main(args: Rail[String]!) {
	  val n = args.length;
	  if (n < 2) {
		  Console.OUT.println("Usage: PAdicNumbers w:Int k:Int n1:Int ... nk:Int");
		  return;
	  }
	  val w = Int.parseInt(args(0));
	  val k = Int.parseInt(args(1));
	  Console.OUT.println("w="  + w + " k=" + k);
	  for (var i:Int=2; i < n; ++i) {
		  val x = Int.parseInt(args(i));
		  val p = new PAdicNumber(w, k, x);
		  Console.OUT.println(x + "==> " + p + "; " + p.toDecimal());
	  }
	  if (n == 2) {
		  val high = pow(w,k);
		  for (var x:Int=0; x < high; x++) {
			  val p = new PAdicNumber(w, k, x);
			  val px = p.toDecimal();
			  if (x != px)
				  Console.OUT.println("Error for " + x + " p=" + p + " p.toDecimal()=" + px);
		  }
	  }
	  Console.OUT.println("done.");
  } */
}
