package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "java.lang.Math")
public class Math {

   @Native("java", "E")
   val E:double=2.718281828459045D;
   
   @Native("java", "PI")
   val PI:double=3.141592653589793D;
   
   @Native("java", "java.lang.Math.abs(#1)")
   public static native def abs(a:Double):Double;
   
    @Native("java", "java.lang.Math.abs(#1)")
   public static native def abs(a:Int):Int;
   

}