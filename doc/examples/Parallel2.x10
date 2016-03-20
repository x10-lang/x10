import x10.array.DenseIterationSpace_2;

class Parallel2 {

    public static operator for (space: DenseIterationSpace_2,
                                body: (i:Long, j:Long)=>void) {
        finish {
            for (i in space.min0 .. space.max0) {
                async for (j in space.min1 .. space.max1) {
                    body(i, j);
                }
            }
        }
    }

    public static def main(Rail[String]) {
        val counts = new Rail[Long](10,0);
        Parallel2.for(i:Long, j:Long in 0..9 * 1..10) {
            counts(i) = i * j + counts(i);
        }
        Console.OUT.println(counts);
    }
}
