import x10.io.Console;

/**
 * The classic hello world program, with a twist - lists each place
 * @author Beth Tibbitts (with help from Igor)
 */
class HelloWholeWorld {
  public static def main(args:Rail[String]):void {
     ateach (p in Dist.makeUnique()) {
     	Console.OUT.println("Hello World from place "+here.id);
     }
  }
}


