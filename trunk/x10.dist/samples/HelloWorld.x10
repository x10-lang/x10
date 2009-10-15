import x10.io.Console;

/**
 * The classic hello world program, shows how to output to the console.
 * @author ??
 * @author vj 
 */
class HelloWorld {
  public static def main(args:Rail[String]):void {
     at (here.next()) {
     	Console.OUT.println("Hello World from place "+here.id);
     }
  }
}


