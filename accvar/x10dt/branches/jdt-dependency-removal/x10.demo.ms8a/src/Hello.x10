import x10.io.Console;

public class Hello {
    public static def main(args:Rail[String]) {
         if (args.length > 0) {
           Console.OUT.println("The first arg is: " + args(0));
         }
         Console.OUT.println("Hello X10 world ");
         val h = new Hello();  // final variable
         var result : Boolean = h.myMethod(); // mutable variable
         Console.OUT.println("The answer is: " + result);
    }
    /** x10doc comment for myMethod */
    public def myMethod() = true;
}
