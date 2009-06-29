import x10.io.Console;
/**
 * The classic hello world program, shows how to define a
 * method on an object, using the def syntax.
 * @author ??
 * @author vj 
 */

public class Hello {
    public static def main(Rail[String])  {
         Console.OUT.println("Hello, X10 world!");
         val h= new Hello();
         val myBool= h.myMethod();
         Console.OUT.println("The answer is: "+myBool);
    }
    def myMethod()=true;
}