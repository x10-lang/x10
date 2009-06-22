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
import moldyn.*;
import jgfutil.*;
import harness.x10Test;

/**
 * Moldyn ported to x10. Sequential version.
 *
 * @author kemal 3/2005
 */
public class JGFMolDynBenchSizeA extends x10Test {

	public boolean run() {
		JGFInstrumentor.printHeader(3, 0);
		JGFMolDynBench mold = new JGFMolDynBench();
		mold.JGFrun(0);
		return true;
	}

	public static void main(String[] args) {
		new JGFMolDynBenchSizeA().execute();
	}
}

