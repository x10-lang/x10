import x10.lang.Double;
import clocked.*;

/**
 * X10 port of SORCore benchmark from Section 2 of Java Grande Forum Benchmark Suite.
 *
 *  SERIAL VERSION
 *
 * @author Vivek Sarkar (vsarkar@us.ibm.com)
 */
 
 
 import x10.util.Random;;

  
class SORCore {
    var gTotal:Double = 0.0D;
    //static val op = Double.+ ;
    static val op = Math.noOp.(Double, Double);
    final public def SORCorerun(c: Clock, omega: Double, G: Array[Double @ Clocked[int](c, op, 0.0)](2)!, numIter: Int) @ ClockedM(c) {
    finish {
	val M = G.region().max(0);
	val N = G.region().max(1);
	
	val omega_over_four  = omega * 0.25;
	val one_minus_omega = 1.0 - omega;
	///Console.OUT.println(G.reduce(Double.+, 0.0));
	//Console.OUT.println(G(1,1));
	val t  = omega_over_four * (G(1-1, 1) + G(1+1, 1) + G(1, 1-1)
						  + G(1, 1+1)) + one_minus_omega * G(1, 1);
	//Console.OUT.println(t);
	 // update interior Points
	 //
	 //JGFInstrumentor.startTimer("Section2:SORCore:Kernel");

	 for ((p) in 1..numIter) { 
	     for ((o) in 0..1) {
		 for ((ii) in 0..(((M-2-(1+o))/2))) async clocked(c) {
		   val i = 2 * ii + 1 + o;
		   for ((j) in 1..N-2) { 
		   	G(i, j) = omega_over_four * (G(i-1, j) + G(i+1, j) + G(i, j-1)
						  + G(i, j+1)) + one_minus_omega * G(i, j);
		  }	
	         }
	      next;
	      // Console.OUT.println(G.reduce(Double.+, 0.0));
	      //Console.OUT.println(G(1,1));
	   
	   }
	 }
	 //JGFInstrumentor.stopTimer("Section2:SORCore:Kernel");
	 gTotal = G.reduce(Double.+, 0.0);
    }
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
* X10 port of SORCore benchmark from Section 2 of Java Grande Forum Benchmark Suite.
*
*  PARALLEL VERSION
*
* @author Vivek Sarkar (vsarkar@us.ibm.com)
* @author vj (ported to X10 v1.7)
*/
public class SOR extends SORCore {

   private var size: Int;
   private val datasizes =   new Array[int](0..2, ((i):Point)=> i == 0? 64: (i==1? 1500 :2500));
   private const JACOBI_NUM_ITER = 80;
   private const RANDOM_SEED  = 10101010L;

   val R  = new Random(RANDOM_SEED);

   public def JGFsetsize(size: Int) {
	this.size = size;
   }

   public def JGFinitialise() {
   }

   public def JGFkernel(c: Clock) @ClockedM(c) {
	val G =  RandomMatrix(c, datasizes(size), datasizes(size), R) as Array[Double](2)!;
	SORCorerun(c, 1.25, G, JACOBI_NUM_ITER);
   }

   public def JGFvalidate(c: Clock) {
	//val refval = [0.002033751571686669, 1.123010681492097D, 1.9967774998523777D ];
	val refval = [0.0020221537289802217, 0.0019480487017237507 , 1.9967774998523777D ];
	val dev = Math.abs(gTotal - refval(size));
	if (dev > 1.0e-4) {
	    Console.OUT.println("Validation failed");
	    Console.OUT.println("gTotal = " + gTotal + "  " + dev + "  " + size);
	    throw new Error("Validation failed");
	}
	Console.OUT.println("Validated");
   }

   public def JGFtidyup() {
   }

   public def JGFrun(size: Int) {
	//JGFInstrumentor.addTimer("Section2:SORCore:Kernel", "Iterations", size);
	val c = Clock.make();
	JGFsetsize(size);
	JGFinitialise();
	JGFkernel(c);
	JGFvalidate(c);
	JGFtidyup();

	//JGFInstrumentor.addOpsToTimer("Section2:SORCore:Kernel", (JACOBI_NUM_ITER) as Double);
	//JGFInstrumentor.printTimer("Section2:SORCore:Kernel");
   }

   private static def RandomMatrix(c: Clock, M: int, N: int, R: Random!): Array[Double] = @ ClockedM(c) {
	val t = new Array[Double @ Clocked[Double](c, op, 0.0)]([0..M-1, 0..N-1], (p: Point) => R.nextDouble() * 1e-6);
   	return t;
   }
   
   public static def main(args:Rail[String]!)= {
		
    		val start_time = System.currentTimeMillis(); 
		new SOR (). JGFrun(0);
    		val compute_time = (System.currentTimeMillis() - start_time);
    		Console.OUT.println( compute_time + " ");
		
	}
   
}

