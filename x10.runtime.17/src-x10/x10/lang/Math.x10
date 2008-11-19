/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

public final value Math {
   public const E = 2.718281828459045D;
   public const PI = 3.141592653589793D;
   
   public static safe def abs(a:Double):Double = a<=0?0-a:a;
   public static safe def abs(a:Int):Int= a<0?-a:a;
   public static safe def abs(a:Float):Float=a<=0?0-a:a;
   public static safe def abs(a:Long):Long=a<0?-a:a;

   @Native("java", "java.lang.Math.pow(#1, #2)")
   @Native("c++", "x10aux::math_utils::pow_mu(#1,#2)")
   public static native def pow(a:Double, b:Double):Double;

   @Native("java", "java.lang.Math.exp(#1)")
   @Native("c++", "x10aux::math_utils::exp_mu(#1)")
   public static native def exp(a:Double):Double;

   @Native("java", "java.lang.Math.cos(#1)")
   @Native("c++", "x10aux::math_utils::cos_mu(#1)")
   public static native def cos(a:Double):Double;

   @Native("java", "java.lang.Math.sin(#1)")
   @Native("c++", "x10aux::math_utils::sin_mu(#1)")
   public static native def sin(a:Double):Double;

   @Native("java", "java.lang.Math.sqrt(#1)")
   @Native("c++", "x10aux::math_utils::sqrt_mu(#1)")
   public static native def sqrt(a:Double):Double;
   
   public static safe def max(a:Int, b:Int)= a<b?b:a;
   public static safe def max(a:Double, b:Double)= a<b?b:a;
   public static safe def min(a:Int, b:Int)= a<b?a:b;
   public static safe def min(a:Double, b:Double)= a<b?a:b;
   
    public static safe def powerOf2(var p:int) {
	  if (p <=0) return false;
	  while (true) { 
	     if (p%2==1) return false;
	     p=p/2; 
	     if (p==1) return true;
	  }
    }
    public static safe def log2(var p:Int):Int {
	   assert powerOf2(p);
	   var i:Int=0;
	   while (p>1) { p=p/2; i++;}
	   return i;
    }
	 // returns 2^(max(0,i))
    public static safe def pow2(i:Int) {
	  var p:Int = 1;
	  for (var j:Int=i; j > 0; j--) p*=2;
	  return p;
    }
}
