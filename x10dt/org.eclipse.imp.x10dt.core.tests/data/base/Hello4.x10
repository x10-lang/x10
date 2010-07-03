public class Hello4 extends Hellooo {
    public static def main(args: Rail[String]!) {
         if (args.length > 0) {
           Console.OUT.println("The first arg is: "+args(0));
         }
         Console.OUT.println("Hello X10 world");
         val h = new Hello4();  // final variable
         var result: boolean = h.myMethod(); 
         Console.OUT.println("The answer is: "+result);
    }
    /** x10doc comment for myMethod */
    public def myMethod(): boolean = {
       return true;
    }
}