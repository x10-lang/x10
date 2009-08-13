import x10.array.RectRegion1;
import x10.array.Point1;

class Simple {

  public static def main(args: Rail[String]) {
    (new Simple()).f();
  }

  def f() {
    val r1: RectRegion1{self == 1..42} = 1..42;
    val r2: RectRegion1{self == 1..10} = 1..10;
    Console.OUT.println(r2 in r1);
    Console.OUT.println(r1 in r2);
    val r3: Region{self in r1} = r1;
    val a = Array.make[Int](r1);
    val r4: Region{self in a.region} = 1..5;
  }

  def pointTest(a: Array[Int]{self.region == r}, r: RectRegion1{self == 1..10}, p1: Point(a.rank){self in a.region}, p2: Point(r.rank){self in r}, p3: Point1{x1 == 10}) {
    a(p1);
    a(p2);
    a(p3);
  }

  def partialcopy(a: Array[Int], b: Array[Int](a.rank)) {
    // Reported bug: type inference fails.
    for (val p: Point{self.rank == a.rank, self.rank == b.rank, self in a.region, self in b.region} in a.region && b.region) a(p) = b(p);
  }

  def transitiveTest(a: Array[Int], r1: Region(a.rank){self in a.region}, r2: Region(a.rank){self in r1}) {
    //for (val p: Point(r2.rank){self in r2} in r2) a(p) = 42;
  }

  def intTest(a: Array[Int](1){self.region == r}, r: RectRegion1{self == 1..10}) {
    a(5) = 42;
  }

  def syntaxTest(r: RectRegion1{self == 1..10}) {
    val r2: RectRegion1{self.intervalMin == 1, self.intervalMax == 10, self.rank == 1, self.rect} = r;
  }

}
