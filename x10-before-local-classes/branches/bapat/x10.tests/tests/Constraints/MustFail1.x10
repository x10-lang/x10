class MustFail1 {

  // This is an example of why old-style code now fails.  Hooray for breaking tons of old code!
  def notIntTest(val a: Array[Int], val p: Point(a.rank)) {
    a(p) = 42;
  }

}
