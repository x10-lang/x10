import x10.util.DistributedRail;
import x10.util.Random;
import x10.lang.Reducible;

/**
 * @author Mohsen Vakilian
 */
public class MinK {
  public static def main(argv:Rail[String]!) {
    val len = 20;
    val maxVal = 100;
    val k = 4;
    val a = new Array[Int](1..len, ((i) : Point) => i % k == 0 ? i / k : Math.abs(new Random().nextInt()) % maxVal + k + 1);
    val b = DistArray.make[Int](Dist.makeBlock(1..len, 0), ((i) : Point) => a(i));

    printPlaces(a, b);
    printDistArrayInfo(b);

    val mins = computeMinK(a, k);
    x10.io.Console.OUT.print("Sequential mins = ");
    printArray(mins);

    val localMins = computeLocalMins(b, k);
    val globalMins = computeGlobalMins(localMins, k);
    x10.io.Console.OUT.print("Global mins = ");
    printArray(globalMins);
  }

  public static def printDistArrayInfo(a : DistArray[Int]) {
    x10.io.Console.OUT.println("a = " + a);
    x10.io.Console.OUT.println("a.rank = " + a.rank);
    x10.io.Console.OUT.println("a.dist = " + a.dist);
  }

  public static def printPlaces(a : Array[Int], b : DistArray[Int]) {
    x10.io.Console.OUT.println("Place.ALL_PLACES = " + Place.ALL_PLACES);
    x10.io.Console.OUT.println("here = " + here);
    x10.io.Console.OUT.println("a.home = " + a.home);
    x10.io.Console.OUT.println("b.home = " + b.home);
    x10.io.Console.OUT.println("b.dist.places()=" + b.dist.places());
  }

  public static def computeLocalMins(b : DistArray[Int], k : Int) {
    val localMins = DistArray.make[Array[Int]{self.rank == 1}](Dist.makeUnique(), ((i) : Point) => new Array[Int](1..k, ((i) : Point) => Int.MAX_VALUE));
    val minKOp = new MinKOp(k);
    
    finish for (p in b.dist.places())
      at (p) {
    	foreach (pt in b.dist | here) {
          minKOp.accum(localMins(p.id), b(pt));
          //x10.io.Console.OUT.println("at " + p + ", b(" + i + ") is " + b(i));
    	}
      }

    return localMins;
  }

  public static def computeGlobalMins(localMins : DistArray[Array[Int]{self.rank == 1}]{self.rank == 1}, k : Int) {
    val globalSum = finish (new MinKOp(k)) {
      foreach (p in localMins.dist.places()) {
        at (p) {
          offer localMins.apply(Point.make(p.id));
    	}
      }
    }; 

    return globalSum;
  }

  public static def printArray(a : Array[Int]) = {
    for (i in a) {
      x10.io.Console.OUT.print(a(i) + " ");
    }
    x10.io.Console.OUT.println("");
  }

  public static def accum(mins : Array[Int]!{self.rank == 1}, v : Int) = {
    if (v < mins(1)) {
      mins(1) = v;
    }

    for ((i) in 2..mins.size()) {
      if (mins(i - 1) < mins(i)) {
        var temp : Int = mins(i);
        mins(i) = mins(i - 1);
        mins(i - 1) = temp;
      } 
    }
  }

  public static def computeMinK(a : Array[Int], k : Int) = {
    val mins = new Array[Int](1..k, ((i) : Point) => Int.MAX_VALUE);
    for (i in a) {
      accum(mins, a(i));
    }
    return mins;
  }

}

class MinKOp(k : Int) implements Reducible[Array[Int]{self.rank == 1}] {

  public def this(k : Int) : MinKOp{self.k == k} = {
    property(k);
  }

  public global safe def accum(mins : Array[Int]{self.rank == 1}, v : Int) = {
    if (v < mins(1)) {
      mins(1) = v;
    }

    for ((i) in 2..k) {
      if (mins(i - 1) < mins(i)) {
        var temp : Int = mins(i);
        mins(i) = mins(i - 1);
        mins(i - 1) = temp;
      } 
    }
  }

  public global safe def zero() : Array[Int]{self.rank == 1} = new Array[Int](1..k, ((i) : Point) => Int.MAX_VALUE);

  public global safe def apply(u : Array[Int]{self.rank == 1}, v : Array[Int]{self.rank == 1}) : Array[Int]{self.rank == 1} = {
    for (i in v) {
      accum(u, v(i));
    }
    return u;
  }

}

