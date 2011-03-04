import x10.io.Console;

public class Hello {
    public static def main(var args: Rail[String]): void = {
         Console.OUT.println("Hello X10 world");
         var h :Hello = new Hello();
         var myBool:boolean = h.myMethod();
         Console.OUT.println("The answer is: "+myBool);
    }

    /** x10doc comment for myMethod */;
    public def myMethod(): boolean = {
       return true;
    }
}
