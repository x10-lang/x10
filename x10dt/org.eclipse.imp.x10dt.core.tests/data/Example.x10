public class Example {
	   
    public static def main(args:Rail[String]): Void {
        val R =   [1..3];
        val D: Dist = R -> here;
        val ar: Array[int] = Array.make[int](D , (p: Point) => 0 );
        val p = [1]; //here
        at(ar.dist(p)) ar(p) = 0;

    }
}