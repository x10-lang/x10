import x10.array.RectRegion1;
import x10.array.Point1;

class Current {

  def translateTestTrivial(r: Int, p1: Point(r), p2: Point(r)) {
    val p: Point(r) = p1 + p2;
  }

  /*def pointArithmeticTest(r: Region, a: Array[Int](r), p1: Point(r.rank), p2: Point(p1.rank), p3: Point(p1.rank){self == p1 + p2, self in r}, p4: Point(p1.rank){self == p1 * 42}) {
    for (p: Point(r.rank){self in r} in r) a(p);
    a(p3);
    // Next line relies on known bug
    //a(p1 + p2);
  }

  def translateTest(a1: Array[Int], p: Point(a1.rank), a2: Array[Int](a1.rank){a1.region.translate(p) in self.region}) {
    for (val p2: Point(a1.rank){self in a1.region} in a1) {
      val target: Point(a1.rank){self == p2 + p} = p2 + p;
      //a2(target) = 42;
    }
  }

  def stencilTest(rank: Int, inner: Array[Double](rank), stencil: Region(rank), outer: Array[Double](rank)) {
    for (p1 in inner) {
      var sum: Double = 0;
      var count: Int = 0;
      for (p2 in stencil) {
	sum += outer(p1 + p2);
	count++;
      }
      inner(p1) = sum / count;
    }
  }*/

}
