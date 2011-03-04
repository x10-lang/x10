public class JHereEqHere {
  public static def main(argv:Array[String]) {
     x10.io.Console.OUT.println("eek(here,here)=" + eek(here, here));
  }
  public static def eek[T](a:T, b:T): Boolean {
      if (a == null || b == null) return false;
      return a.equals(b);
    }

}