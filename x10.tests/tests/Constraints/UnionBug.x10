import x10.array.RectRegion1;
import x10.array.Point1;

class UnionBug {

  def unionTest(val r1: Region, val r2: Region(r1.rank), val a: Array[Int](r1 || r2)) {
    for (val p in r1) a(p) = 42;
    for (val p in r2) a(p) = 42;
  }

}
