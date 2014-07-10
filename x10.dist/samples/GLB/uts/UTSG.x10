

import x10.compiler.*;
import x10.util.Option;
import x10.util.OptionsParser;
import x10.util.Random;

import x10.glb.GLB;
import x10.glb.GLBResult;
import x10.glb.GLBParameters;
public final class UTSG {

    public static def compute(args:Rail[String]):Rail[Long]{
	   val opts = new OptionsParser(args, new Rail[Option](), [
                                                                Option("b", "", "Branching factor"),
                                                                Option("r", "", "Seed (0 <= r < 2^31"),
                                                                Option("d", "", "Tree depth"),
                                                                Option("n", "", "Number of nodes to process before probing. Default 200."),
                                                                Option("w", "", "Number of thieves to send out. Default 1."),
                                                                Option("l", "", "Base of the lifeline"),
                                                                Option("m", "", "Max potential victims"),
                                                                Option("v", "", "Verbose. Default 0 (no).")]);

        val b = opts("-b", 4n);
        val r = opts("-r", 19n);
        val d = opts("-d", 13n);
        val n = opts("-n", 511n);
        val l = opts("-l", 32n);
        val m = opts("-m", 1024n);
        val verbose = opts("-v", GLBParameters.SHOW_RESULT_FLAG);

        val P = Place.numPlaces();

        var z0:Int = 1n;
        var zz:Int = l;
        while (zz < P) {
            z0++;
            zz *= l;
        }
        val z = z0;

        val w = opts("-w", z);

        Console.OUT.println("places=" + P +
                "   b=" + b +
                        "   r=" + r +
                                "   d=" + d +
                                        "   w=" + w +
                                                "   n=" + n +
                                                        "   l=" + l + 
                                                                "   m=" + m + 
                                                                        "   z=" + z);
        val init = ()=>{ return new Queue(b); };
        //val glb = new GLB[Queue](init, n, w, l, z, m, true);
        val glb = new GLB[Queue,Long](init, GLBParameters(n, w, l, z, m,verbose), true);
       
       
        val start = ()=>{ (glb.taskQueue()).init(r, d); };
        val result:Rail[Long] = glb.run(start);
	return result;

    }
    public static def main(args:Rail[String]) {
	compute(args);
    }

    public static def mainTest(args:Rail[String]):Rail[Long]{
	val result:Rail[Long] = compute(args);
	Console.OUT.println("Result is: " + result(0));
	return result;
    }
}
