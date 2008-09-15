package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "java.lang.Math")
public final class Math {

   @Native("java", "java.lang.Math.E")
   public static val E:double=2.718281828459045D;
   
   @Native("java", "java.lang.Math.PI")
   public static val PI:double=3.141592653589793D;
   
   @Native("java", "java.lang.Math.abs(#1)")
   public static native def abs(a:Double):Double;
   
    @Native("java", "java.lang.Math.abs(#1)")
   public static native def abs(a:Int):Int;
   
    @Native("java", "java.lang.Math.abs(#1)")
   public static native def abs(a:Float):Float;
   
    @Native("java", "java.lang.Math.abs(#1)")
   public static native def abs(a:Long):Long;
   
     @Native("java", "java.lang.Math.pow(#1,#2)")
   public static native def pow(a:Double, b:Double):Double;
   
      @Native("java", "java.lang.Math.exp(#1)")
   public static native def exp(a:Double):Double;
   
        @Native("java", "java.lang.Math.cos(#1)")
   public static native def cos(a:Double):Double;
   
        @Native("java", "java.lang.Math.sin(#1)")
   public static native def sin(a:Double):Double;
   
   @Native("java", "java.lang.Math.sqrt(#1)")
   public static native def sqrt(a:Double):Double;
   

}