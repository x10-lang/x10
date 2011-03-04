import x10.io.Console;

class HelloWorldPar {
  public static def main(args:Rail[String]):void {
    finish ateach (p in Dist.makeUnique()) {
      Console.OUT.println("Hello World from Place" +p);
    }
  }
}


