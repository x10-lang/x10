public class Hello1 {
    public static def main(args: Array[String](1)) {
         if (args.size() > 0) {
           Console.OUT.println("The first arg is: "+args(0));
         }
         Console.OUT.println("Hello X10 world");
         val h = new Hello1();  // final variable
         var result: boolean = h.myMethod(); 
         Console.OUT.println("The answer is: "+result);
    }
    /** x10doc comment for myMethod */
    public def myMethod(): boolean = {
       return true;
    }
}