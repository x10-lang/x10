
import x10.util.*;
import x10.array.*;
import harness.x10Test;

public class BlockDist2 extends x10Test.BardTest {
  public static def main(Rail[String]){
     val p:x10Test = new BlockDist2();
     p.execute();
  }
  static def str(d:Dist):String {
    var s : String = "";
    for(p in d.region) 
      s += " " + d(p).id;
    return s;
  }

  static def actualSize(R: Region):Int {
    var n : Int = 0;
    for(p in R) n++;
    return n;
  }


  public def test() {
     for( [n] in 10 .. 100 ) {
        val R = 1 .. n;
        val D = Dist.makeBlock(R);
        val M = Place.MAX_PLACES;
        val l : Int = n / M; // Minimum number in a place.
        var prev : Int = -1; 
        for (p in Place.places()) {
          val atP =  D.get(p);
          val np = actualSize(atP);
          eq(np, atP.size(), "Size of " + atP + " really: " + np + " but .size()=" + atP.size());
          
          yes (np == l || np == l+1, 
            "number at p test for p=" + p + " + n=" + n
            + " -- expects l=" + l + " or " + (l+1) 
            + ", but found np=" + np
            + "\n D= " + str(D)
            + "\n atP = " + atP
            );
          if (prev != -1) {
            if (prev == l) yes(np == l, "big blocks before small blocks for p=" + p + ", n=" + n);
          }
          prev = np;
        }
     }
  }
}
