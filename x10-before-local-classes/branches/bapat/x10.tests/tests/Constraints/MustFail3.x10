import x10.array.RectRegion1;

class MustFail3 {

  def intTest(a: Array[Int](1){self.region == 1..10}) {
    a(42) = 42;
  }

}
