import x10.lang.*;
import x10.util.*;

/**
 * @author Mohsen Vakilian
 */
public class Reduction {
  public static def main(args: Rail[String]!) {
  }
}

abstract class Adder[S, T]{T <: Reducible[S]} {
  
  public abstract def getT() : T;

  public def computeGlobalSum(localSums : DistArray[Int]{self.dist.region.rank == 1}) {
    val globalSum = finish (getT()) {
      foreach (p in localSums.dist.places()) {
        at (p) {
          offer localSums.apply(Point.make(p.id));
        }
      }
    };
  
    return globalSum;
  }
}

