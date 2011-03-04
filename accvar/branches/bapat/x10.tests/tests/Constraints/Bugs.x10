class Bugs {

  def nelsonOppenBug() {
    val r1 = 1..10;
    val r2: Region{self in r1} = 2..9;
  }

  // This needs to infer from r2 in r1 that r2.rank == r1.rank.  And it does.  Yay!
  def nelsonOppenTest(r1: Region, r2: Region{self in r1}) {
    val rank: Int{self == r1.rank, self == r2.rank} = r1.rank;
  }

  def nelsonOppenTest2(r: Region{x in self}, x: Int) {
    val rank: Int{self == 1} = r.rank;  // We don't specify r's rank, but since x is in it it must be 1.
  }

}
