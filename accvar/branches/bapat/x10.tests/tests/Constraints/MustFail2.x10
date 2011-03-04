class MustFail2 {

  def wrongDirectionTest(a: Array[Int], r: Region(a.rank){a.region in self}) {
    // Should fail
    for (val p: Point(r.rank){self in r} in r) a(p);
  }

}
