/**
 * @author Mohsen Vakilian
 */
import x10.util.DistributedRail;
import x10.lang.Reducible;

public class ArrayReductions {
  public static def main(argv:Rail[String]!) {
    val a = new Array[Int](1..8, ((i) : Point) => i * i);
    x10.io.Console.OUT.println("here = " + here);
    x10.io.Console.OUT.println("a.home = " + a.home);
    val b = DistArray.make[Int](Dist.makeBlock(1..8, 0), ((i) : Point) => i * i);
    val localSums = computeLocalSums(b);
    x10.io.Console.OUT.println("sum(a) = " + sum(a));
    x10.io.Console.OUT.println("a.reduce(Int.+, 0) = " + a.reduce(Int.+, 0));
    x10.io.Console.OUT.println("b.reduce(Int.+, 0) = " + b.reduce(Int.+, 0));

    //x10.io.Console.OUT.println("Place.ALL_PLACES=" + Place.ALL_PLACES);
    //x10.io.Console.OUT.println("b.rank=" + b.rank);
    //x10.io.Console.OUT.println("b.dist" + b.dist);
    //x10.io.Console.OUT.println("b.dist.places()=" + b.dist.places());
    
    x10.io.Console.OUT.println("Global sum = " + computeGlobalSum(localSums));
  }

  public static def computeLocalSums(b : DistArray[Int]) {
    x10.io.Console.OUT.println("b.type = " + b.typeName());
    val localSums = DistArray.make[Int](Dist.makeUnique(), ((i) : Point) => 0);
    /*
    for (p in b.dist.places())
      at (p) {
        for ((i) in b.dist | here) {
          x10.io.Console.OUT.println("at " + p + ", b(" + i + ") is " + b(i));
      	}
      }
    */
    finish for (p in b.dist.places())
      at (p) {
    	foreach (pt in b.dist | here) {
          localSums(p.id) += b(pt);
          //x10.io.Console.OUT.println("at " + p + ", b(" + i + ") is " + b(i));
    	}
      }

    for (p in b.dist.places())
      at (p) {
    	x10.io.Console.OUT.println("Local sum at " + p + " is " + localSums(p.id));
      }
    return localSums;
  }

  public static def computeGlobalSum(localSums : DistArray[Int]{self.dist.region.rank == 1}) {
    val globalSum = finish (new Add()) {
      foreach (p in localSums.dist.places()) {
        at (p) {
          offer localSums.apply(Point.make(p.id));
    	}
      }
    }; 

    return globalSum;
  }

  public static def sum(a : Array[Int]) : Int = {
    var s : Int = 0;
    for (i in a) {
      s += a(i);
    }
    return s;
  }

}

class Add implements Reducible[Int] {

  public global safe def zero() : Int = 0;

  public global safe def apply(u : Int, v : Int) : Int = u + v;

}
