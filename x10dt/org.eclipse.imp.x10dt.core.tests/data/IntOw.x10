public class IntOw {
  public static def main(argv:Rail[String]) {
    type Icogep(n:Int) = Int{self >= n};
    val underworld : Icogep(0) = 1;
  }
}