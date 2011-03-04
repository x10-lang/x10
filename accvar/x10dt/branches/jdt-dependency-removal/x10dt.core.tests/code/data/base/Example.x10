public class Example {
	   
    public static def main(args:Array[String](1)): Void {
        val R =  1..3;
        val D: Dist = R -> here;
        val ar: DistArray[int] = DistArray.make[int](D , (p: Point) => 0 );
        val p = [1]; //here
        at(ar.dist(p)) ar(p) = 0;

    }
}