import harness.x10Test;

/**
 * Tests a Block,Block distribution of a three dimensional array over two axes.
 * @author milthorpe
 */
public class TestBlockBlockDist extends x10Test {
	public def run(): Boolean = {
        // array region is 40 * 50 * 60
        val r = Region.makeRectangular(0, 39);
        val gridRegion = r * 0..49 * 0..59;
        val gridDist1 = Dist.makeBlockBlock(gridRegion, 0, 1);
        val placeCounts = Rail.make[Int](Place.MAX_PLACES);

        // attempt to apply to every point in the region.  if it fails with BPE, there's a problem
        for (p in gridRegion) {
            val place = gridDist1(p);
            placeCounts(place.id)++;
        }
        var total : Int = 0;
        for ([q] in 0..(placeCounts.length-1)) {
            total += placeCounts(q);
        }
        chk(total == 40*50*60);

        val myArray = DistArray.make[Double](gridDist1, (p : Point) => 0.0);
        finish ateach (p in myArray) {
            myArray(p) = myArray(p) + 2.0;
        }

        val gridDist2 = Dist.makeBlockBlock(gridRegion, 1, 2);


        return true;
	}

	public static def main(args:Array[String]) {
		new TestBlockBlockDist().execute();
	}
}
