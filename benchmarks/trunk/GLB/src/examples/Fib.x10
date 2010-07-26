/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import x10.compiler.*;
import x10.util.OptionsParser;
import x10.util.Option;
import x10.lang.Math;
import x10.util.Random;
import x10.util.Stack;

public class Fib {

	static def fib(n:UInt) = n < 2 ? n : fib(n-1)+fib(n-2);
	
	class Fib2 implements TaskFrame[UInt, UInt] {
		public def runTask(t:UInt, s:Stack[UInt]!) offers UInt {
			if (t < 20) 
				offer fib(t);
			else {
				s.push(t-1);
				s.push(t-2);
			}
		}
		public def runRootTask(t:UInt, s:Stack[UInt]!) offers UInt {
			runTask(t, s);
		}
	}
	public static def main (args : Rail[String]!) {
		try {
			val opts = new OptionsParser(args, null,
					[
					 Option("s", "", "Sequential"),
					 Option("x", "", "Input"),
					 Option("e", "", "Event logs, default 0 (no)."),
					 Option("k", "", "Number of items to steal; default 0. If 0, steal half. "),
					 Option("v", "", "Verbose, default 0 (no)."),
					 Option("n", "", "Number of nodes to process before probing."),
					 Option("w", "", "Number of thieves to send out, less 1. (Default 0, so 1 thief will be sent out."),
                     Option("z", "", "Dimension of the sparse hypercube")
					 ]);

	
			val seq = opts("-s", 0);
			val verbose = opts("-v",0)==1;
			val nu:Int = opts("-n",200);
			val w:Int = opts("-w", 0);
			val e = opts("-e", 0)==1;
			val x = opts("-x", 40);

			Console.OUT.println("--------");
			Console.OUT.println("Places="+Place.MAX_PLACES);
			val reducer = new Reducible[UInt]() {
				global safe public def zero()=0;
				global safe public def apply(a:UInt, b:UInt)=a+b;
			};
			if (seq != 0) {
				Console.OUT.println("Starting...");
				var time:Long = System.nanoTime();

				val runner = new SeqRunner[UInt UInt](new Fib2());
				val result=runner.run(x, reducer);

				time = System.nanoTime() - time;
				Console.OUT.println("Finished with result " + result + ".");
				Console.OUT.println("Performance = "+result+"/"+(time/1E9)+"="+ (nodes/(time/1E3)) + "M nodes/s");
			} else {
				val g = new GlobalRunner[UInt, UInt](nu, w, e, l, z, Dist.makeUnique(), 
						():TaskFrame[UInt, UInt] => new Fib2());
				Console.OUT.println("Starting...");
				var time:Long = System.nanoTime();
				val result = g.run(x, reducer);
				time = System.nanoTime() - time;
				Console.OUT.println("Finished with result " + result + ".");
				g.stats(time, verbose);
			}
			Console.OUT.println("--------");

		} catch (e:Throwable) {
			e.printStackTrace(Console.ERR);
		}
	}
}

// vim: ts=2:sw=2:et
