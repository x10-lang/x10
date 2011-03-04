import x10.io.Console;
public class Hello {
    public static def main(args:Rail[String]){
         if(args.length>0){
           Console.OUT.println("The first arg is: "+args(0));
         }
         Console.OUT.println("Hello, X10 world!");
         val h = new Hello(); 
         var result: Boolean = h.myMethod(); 
         Console.OUT.println("The answer is: "+result);
    }
   
    public def myMethod()=true;
}