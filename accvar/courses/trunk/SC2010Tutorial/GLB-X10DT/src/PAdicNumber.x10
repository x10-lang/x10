
/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */
import x10.compiler.NonEscaping;

/**
 * A representation of a number in K "digits" of base P, with 
 * "most significant digit" first.
 * 
 * <p> Useful in hypercube based routing.
 */

public class PAdicNumber(P:Int, K:Int) {

    // digits(0) is the most significant digit.
   val digits: Rail[Int](K);

    public static def pow(w:Int, var n:Int) {
        var result:Int=1;
        while (n-- > 0) result *= w;
        return result;
    }

    @NonEscaping public final def pow(n:Int) = pow(P, n);

    public def this(p:Int, k:Int, x:Int):PAdicNumber{self.P==p, self.K==k} {
        property(p,k);
        digits = Rail.make(k, (i:Int) => { val wi = pow(i); (x % (p*wi))/wi});
    }

    def this (p:Int, k:Int, ds:Rail[Int](k)) {
        property(p, k);
        digits = ds;
    }

    /**
     * Numerical less than relationship, implemented by lexicographic
     * ordering on representation.
     */
    public operator this < (that:PAdicNumber(P,K)):Boolean {
        var i:Int =0;
        for (; i < K && digits(i) < that.digits(i); ++i) ;
        return i==K-1;
    }
        
    /**
     * Two numbers are equal if they have the same base, the same number
     * of digits and the same digits.
     */
     public def equals(o:Any):Boolean {
         if (! (o instanceof PAdicNumber)) 
             return false;
         val other = o as PAdicNumber;
         if (P != other.P || K != other.K) 
             return false;
         for (var i:Int=0; i < K; ++i)
             if (digits(i) != other.digits(i))
                 return false;
         return true;
     }
     public def hashCode():Int {
         return P+K+digits.hashCode();
     }
                
     /**
      * Return the number distance d away along dimension dim (using
      * modulo arithmetic).
      */
      public def delta(d:Int, dim:Int)= 
	  new PAdicNumber(P, K, Rail.make(K, 
		   (i:Int) => (i==dim ?  (digits(i)+d)% P : digits(i))));
                
      /**
       * Return the number distance d away along dimension dim (using
        modulo arithmetic).  * If d >= bound, keep adding d to the
        dim'th component (using modulo arithmetic) * until you reach a
        number < bound. Assume: this < bound.
      */
      public def boundedDelta(d:Int, dim:Int, bound:Int): PAdicNumber(P,K) {
	  val o = new PAdicNumber(P, K, Rail.make(K, 
		(i:Int) => (i==dim ?  (digits(i)+d)% P : digits(i))));
                    
	  val od = o.toDecimal();
	  if (od < bound)
	      return o; 
	  var q:PAdicNumber(P,K)  = o.delta(d,dim);
	  var qd:Int = q.toDecimal();
	  while (qd >= bound) {
                 q = q.delta(d,dim);
                 qd = q.toDecimal();
                 if (qd == od)
                     break;
             }
             return q;
         }
             
      /** Return a decimal representation of this.
       * 
       */
      public def toDecimal():Int {
	  var result:Int=digits(K-1);
	  for (var i:Int=K-1; i > 0; i--) {
	      result = result*P + digits(i-1);
	  }
	  return result;
      }
      /**
       * A string representation in base P, with most
       * significant digit to the left.
       */
       public def toString() {
	   var result:String="";
	   for (var i:Int=K-1; i >= 0; i--) {
	       result += digits(i);
	       if (i > 0)
		   result += "^";
	   }
	   return result;
       }
                        
       public static def main2(args: Rail[String]) {
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
	       Console.OUT.println("" + x + "==> " + p + "; " + p.toDecimal());
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
       } 
       
       public static def main(args: Array[String](1)) {
	   val n = args.size;
	   if (n < 2) {
	       Console.OUT.println("Usage: PAdicNumbers P:Int k:Int");
	       return;
	   }
	   val P = Int.parseInt(args(0));
	   val k = Int.parseInt(args(1));
	   val w = NetworkGenerator.findW(P, k);
	   Console.OUT.println("P=" + P + " w="  + w + " k=" + k);
	   
	   val m = NetworkGenerator.generateSparseEmbedding(P,k);
	   for ([r] in 0..P-1) {
	       Console.OUT.print("Place " + r + ":" + new PAdicNumber(w,k, r) + "=> " );
	       for ([i] in 0..k-1) {
		   Console.OUT.print(" " + m(r)(i) + ":" 
				     + (m(r)(i) == -1 ? "-1" : new PAdicNumber(w, k, m(r)(i)).toString()));
	       }
	       Console.OUT.println();
	   }
	   Console.OUT.println("done.");
       } 
}
