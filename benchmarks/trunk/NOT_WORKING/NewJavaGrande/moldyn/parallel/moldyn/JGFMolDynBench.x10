/**************************************************************************
*                                                                         *
*             Java Grande Forum Benchmark Suite - MPJ Version 1.0         *
*                                                                         *
*                            produced by                                  *
*                                                                         *
*                  Java Grande Benchmarking Project                       *
*                                                                         *
*                                at                                       *
*                                                                         *
*                Edinburgh Parallel Computing Centre                      *
*                                                                         *
*                email: epcc-javagrande@epcc.ed.ac.uk                     *
*                                                                         *
*                                                                         *
*      This version copyright (c) The University of Edinburgh, 2001.      *
*                         All rights reserved.                            *
*                                                                         *
**************************************************************************/
package moldyn;

import jgfutil.*;

import x10.lang.Clock;

/**
 * Moldyn ported to x10. Single place with multiple activities.
 *
 * @author kemal 3/2005
 */
public class JGFMolDynBench implements JGFSection3 {
  public static val NTHREADS: Int = 4;
  val size: Int;

  public static val P = 
    ValRail.make[md!](NTHREADS, (i: Int) => new md(i, NTHREADS));

  public def this(size: Int) {
    this.size = size;
  }

  // not called any more
  public def JGFsetsize(size: Int) {
    // this.size = size; // error: cannot assign to final field size 
  }

  public def JGFinitialise() {
    finish foreach ((j) in 0..P.length-1) (P(j)).initialise();
  }

  public def JGFapplication() {
    JGFInstrumentor.startTimer("Section3:MolDyn:Run");
    finish async {
      val C: Clock = Clock.make();
      foreach ((j) in 0..P.length-1) clocked(C) P(j).runiters(C);
    }
    JGFInstrumentor.stopTimer("Section3:MolDyn:Run");
  }

  public def JGFvalidate() {
    finish foreach ((j) in 0..P.length-1) {
      val myNode = P(j);

      val refval: ValRail[Double] = [ 275.97175611773514, 7397.392307839352 ];

      val dev: Double = Math.abs(myNode.ek - refval(size));

      if (dev > 1.0e-10) {
        Console.OUT.println("Validation failed at thread "+j);
        Console.OUT.println("Kinetic Energy = " + myNode.ek + "  " + 
                            dev + "  " + refval(size));
        throw new Error("Validation failed");
      }
    }
  }

  public def JGFtidyup() {
    // System.gc();
  }

  // immutable instance field size is initialized in the constructor, so
  // argument size is not used in the body of JGFrun; it exists because
  // JGFMolDynBench implements JGFSection3 which contains JGFrun(size: Int): void
  public def JGFrun(size: Int) {
    JGFInstrumentor.addTimer("Section3:MolDyn:Total", "Solutions", size);
    JGFInstrumentor.addTimer("Section3:MolDyn:Run", "Interactions", size);

    // JGFsetsize(size);

    JGFInstrumentor.startTimer("Section3:MolDyn:Total");

    JGFinitialise();
    JGFapplication();
    JGFvalidate();
    JGFtidyup();

    JGFInstrumentor.stopTimer("Section3:MolDyn:Total");

    val interactions = 0;
    JGFInstrumentor.addOpsToTimer("Section3:MolDyn:Run", interactions as Double);
    JGFInstrumentor.addOpsToTimer("Section3:MolDyn:Total", 1);

    JGFInstrumentor.printTimer("Section3:MolDyn:Run");
    JGFInstrumentor.printTimer("Section3:MolDyn:Total");
  }
}
