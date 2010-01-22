/**
 * This is one of a series of programs showing how to express
 * different forms of parallelism in X10.</p>
 *
 * All of the example programs in the series are computing
 * the same thing:  solving a set of 2D partial differential
 * equations that can be expressed as an iterative 4-point
 * stencil operation.  See the X10 2.0 tutorial for
 * for more details and some pictures.</p>
 *
 * This program is illustrating explicit loop chunking 
 * with hierarchical parallelism.  It diverges from the 
 * presentation in the X10 2.0 tutorial slides because 
 * the current X10 2.0 array/distribution library doesn't
 * provide a built-in function to take a (already block-cyclic)
 * distribution and split the points into P sub-regions at each
 * place.  In this sample, this is done fairly inefficiently
 * by the blockIt method, which is actually being invoked in each
 * outer iteration of the main do/while loop.  For better performance,
 * (a) the creation of sub-regions should probably consider locality
 * and (b) the sub-region creation should only be done once at each 
 * place instead of being redone on every loop iteration.<p>
 */
public class HeatTransfer_v3 {
    static type Real=Double;
    const n = 3; 
    const epsilon = 1.0e-5;
    const P = 2;

    const BigD = Dist.makeBlock([0..n+1, 0..n+1], 0);
    const D = BigD | ([1..n, 1..n] as Region);
    const LastRow = [0..0, 1..n] as Region;
    const A = Array.make[Real](BigD,(p:Point)=>{ LastRow.contains(p) ? 1.0 : 0.0 });
    const Temp = Array.make[Real](BigD);

    static def stencil_1((x,y):Point(2)) = (([x-1..x+1,y..y] as Region(2)) || [x..x,y-1..y+1]) - [x..x,y..y];

    static def subtract(a:Array[Real],b:Array[Real]) = Array.make[Real](a.dist, (p:Point)=>a(p as Point(a.rank))-b(p as Point(b.rank)));

    // TODO: This is a really inefficient implementation of this abstraction.
    //       Needs to be done properly and integrated into the Dist/Region/Array
    //       class library in x10.array.
    static def blockIt(d:Dist(2), numProcs:int):ValRail[Iterable[Point(2)]] {
        val ans = ValRail.make(numProcs, (int) => new x10.util.ArrayList[Point{self.rank==d.rank}]());
	var modulo:int = 0;
        for (p in d) {
	    ans(modulo).add(p);
            modulo = (modulo + 1) % numProcs;
        }
	return ans;
    }

    def run() {
	val D_Base = Dist.makeUnique(D.places());
        var delta:Real = 1.0;
        do {
            finish ateach (z in D_Base) {
                val blocks:ValRail[Iterable[Point(2)]] = blockIt(D | here, P);
                foreach ((q) in 0..P-1) {
                    for (p in blocks(q)) {
                        Temp(p) = (A | stencil_1(p)).reduce(Double.+, 0.0)/4;
                    }
                }
            }

            delta = subtract(A|D.region,Temp|D.region).lift(Math.abs.(Double)).reduce(Math.max.(Double,Double), 0.0);
            finish ateach (p in D) A(p) = Temp(p);
        } while (delta > epsilon);
    }
 
   def prettyPrintResult() {
       for ((i) in A.region.projection(0)) {
           for ((j) in A.region.projection(1)) {
                val pt = Point.make(i,j);
                at (BigD(pt)) { 
		    val tmp = A(pt);
                    at (Place.FIRST_PLACE) Console.OUT.printf("%1.4f ", tmp);
                }
            }
            Console.OUT.println();
        }
    }

    public static def main(Rail[String]) {
	Console.OUT.println("HeatTransfer Tutorial example with n="+n+" and epsilon="+epsilon);
	Console.OUT.println("Initializing data structures");
        val s = new HeatTransfer_v3();
	Console.OUT.print("Beginning computation...");
	val start = System.nanoTime();
        s.run();
	val stop = System.nanoTime();
	Console.OUT.printf("...completed in %1.3f seconds.\n", (stop-start as double)/1e9);
	s.prettyPrintResult();
    }
}
