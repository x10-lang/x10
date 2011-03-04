public class Mandana1 {
  public static def main(argv:Array[String]) {
    val R =   1..3;
    val D = R -> here ;
    val ar <: DistArray[int] = DistArray.make[int](D , (p: Point) => 0 );
    val p = [1];
    at(ar.dist(p)) 
         ar(p) = 0;
  }
}