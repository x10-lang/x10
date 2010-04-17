import x10.lang.Double;

/**
 * X10 port of SOROrigCore benchmark from Section 2 of Java Grande Forum Benchmark Suite.
 *
 *  SERIAL VERSION
 *
 * @author Vivek Sarkar (vsarkar@us.ibm.com)
 */
 
 
 import x10.util.Random;;

  
class SOROrigCore {
    var gTotal:Double = 0.0D;
    final public def SOROrigCorerun(omega: Double, G: Array[Double](2)!, numIter: Int) {
	val M = G.region().max(0);
	val N = G.region().max(1);
	
	val omega_over_four  = omega * 0.25;
	val one_minus_omega = 1.0 - omega;

	 // update interior Points
	 //
	 //JGFInstrumentor.startTimer("Section2:SOROrigCore:Kernel");

	 for ((p) in 1..numIter) 
	     for ((o) in 0..1) 
		 finish foreach ((ii) in 0..(((M-2-(1+o))/2))) {
		 val i = 2 * ii + 1 + o;
		 for ((j) in 1..N-2) 
		     G(i, j) = omega_over_four * (G(i-1, j) + G(i+1, j) + G(i, j-1)
						  + G(i, j+1)) + one_minus_omega * G(i, j);
	     }

	 //JGFInstrumentor.stopTimer("Section2:SOROrigCore:Kernel");
	 gTotal = G.reduce(Double.+, 0);
    }
}





/*
*
* (C) Copyright IBM Corporation 2006
*
*  This file is part of X10 Test.
*
*/




/**
* X10 port of SOROrigCore benchmark from Section 2 of Java Grande Forum Benchmark Suite.
*
*  PARALLEL VERSION
*
* @author Vivek Sarkar (vsarkar@us.ibm.com)
* @author vj (ported to X10 v1.7)
*/
public class SOROrig extends SOROrigCore {

   private var size: Int;
   private val datasizes =   Array.make[int](0..2, ((i):Point)=> i == 0? 10: (i==1? 1500 :2500));
   private const JACOBI_NUM_ITER = 10000;
   private const RANDOM_SEED  = 10101010L;

   val R  = new Random(RANDOM_SEED);

   public def JGFsetsize(size: Int) {
	this.size = size;
   }

   public def JGFinitialise() {
   }

   public def JGFkernel() {
	val G =  RandomMatrix(datasizes(size), datasizes(size), R) as Array[Double](2)!;
	SOROrigCorerun(1.25, G, JACOBI_NUM_ITER);
   }

   public def JGFvalidate() {
	//val refval = [ 0.0012191583622038237D, 1.123010681492097D, 1.9967774998523777D ];
	val refval = [ 4.306821467221954E-5, 1.123010681492097D, 1.9967774998523777D ];
	val dev = Math.abs(gTotal - refval(size));
	if (dev > 1.0e-12) {
	    Console.OUT.println("Validation failed");
	    Console.OUT.println("gTotal = " + gTotal + "  " + dev + "  " + size);
	    throw new Error("Validation failed");
	}
	Console.OUT.println("Validated");
   }

   public def JGFtidyup() {
   }

   public def JGFrun(size: Int) {
	//JGFInstrumentor.addTimer("Section2:SOROrigCore:Kernel", "Iterations", size);

	JGFsetsize(size);
	JGFinitialise();
	JGFkernel();
	JGFvalidate();
	JGFtidyup();

	//JGFInstrumentor.addOpsToTimer("Section2:SOROrigCore:Kernel", (JACOBI_NUM_ITER) as Double);
	//JGFInstrumentor.printTimer("Section2:SOROrigCore:Kernel");
   }

   private static def RandomMatrix(M: int, N: int, R: Random!): Array[Double] {
	val t = Array.make[Double]([0..M-1, 0..N-1]);
   for ((i,j) in t.region) 
	t(i, j) = R.nextDouble() * 1e-6;
   return t;
   }
   
   public static def main(args:Rail[String]!)= {
		
		new SOROrig (). JGFrun(0);
		
	}
   
}

