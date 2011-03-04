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

package examples;

import x10.compiler.*;
import x10.util.OptionsParser;
import x10.util.Option;
import x10.lang.Math;
import x10.util.Random;
import x10.util.Stack;
import global.lb.*;
public class Ones extends TaskFrame[Int, Int] {
	var c:Int=0;
	public def runTask(t:Int, s:Stack[Int]):void offers Int {
		//	offer t;
		c += t;
		
	}
	public def runRootTask(t:Int, s:Stack[Int]):void offers Int {
		for (var i:Int=0; i < t; i++) 
			s.push(1);
	}
	public static def main (args : Array[String](1)) {
		try {
			val opts = new OptionsParser(args, null, [
			                                          Option("s", "", "Sequential"),
			                                          Option("x", "", "Input")]);
			val seq = opts("-s", 0)==1;
			val x = opts("-x",40);
			Console.OUT.println("Places="+Place.MAX_PLACES 
					+ " x=" + x + " seq=" + seq);
			val reducer = new Reducible[Int]() {
				public def zero()=0;
				public operator this(a:Int, b:Int)=a+b;
			};
			val counts = Rail.make(Place.MAX_PLACES, 
					(i:Int)=> at(Place(i)) GlobalRef[TaskFrame[Int,Int]](new Ones()));
			val runner = seq ? new SeqRunner[Int,Int](new Counts()) as Runner[Int,Int]
			                 : new GlobalRunner[Int, Int](args, ()=> counts(here.id));
			Console.OUT.println("Starting...");
			var time:Long = System.nanoTime();
			val result=runner(x, reducer);
			time = System.nanoTime() - time;
			Console.OUT.println("Finished with result " + result + ".");
			runner.stats(time, true);
			Console.OUT.println("--------");
			for (count in counts) {
				at (count) 
				Console.OUT.println("Ones(" + here.id +")=" + (count() as Ones).c);
			}
		} catch (e:Throwable) {
			e.printStackTrace(Console.ERR);
		}
	}
}

// vim: ts=2:sw=2:et
