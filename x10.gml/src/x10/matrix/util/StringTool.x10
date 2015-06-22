package x10.matrix.util;

public class StringTool {

       public static def parse[T](s:String):T {
	   if (T==Float) return Float.parseFloat(s) as T;
	   if (T==Double) return Double.parseDouble(s) as T;
	   if (T==Int) return Int.parseInt(s) as T;
	   if (T==Long) return Long.parseLong(s) as T;
	   throw new Exception("Unexpected type T: Must be one of Float, Double, Int or Long.");
       }
}
