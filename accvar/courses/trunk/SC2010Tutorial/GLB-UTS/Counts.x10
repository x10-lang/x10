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


public class Counts extends TaskFrame[UInt, UInt] {
    var c:UInt=0;
    public def runTask(t:UInt, s:Stack[UInt]):Void offers UInt {
	   offer t;
	   c += t;
    }
    public def runRootTask(t:UInt, s:Stack[UInt]):Void offers UInt {
	   for (var i:UInt=0u; i < t; i++) 
	   s.push(2);
    }
    public static def main (args: Array[String](1)) {
        try {
	        val opts = new OptionsParser(args, null, [
			      Option("s", "", "Sequential"),
			      Option("x", "", "Input")]);
	        val seq = opts("-s", 0)==1;
	        val x:UInt = opts("-x",40);
            Console.OUT.println("Places="+Place.MAX_PLACES 
				+ " x=" + x + " seq=" + seq);
            val reducer = new Reducible[UInt]() {
                public def zero()=0u;
                public def apply(a:UInt, b:UInt)=a+b;
            };
	        val counts = Rail.make(Place.MAX_PLACES, 
			      (i:Int)=> at(Place(i)) GlobalRef[TaskFrame[UInt,UInt]](new Counts()));
	        val runner = seq ? new SeqRunner[UInt,UInt](new Counts()) as Runner[UInt,UInt]
		                     : new GlobalRunner[UInt, UInt](args, ()=> counts(here.id));
	        Console.OUT.println("Starting...");
	        var time:Long = System.nanoTime();
	        val result=runner(x, reducer);
	        time = System.nanoTime() - time;
	        Console.OUT.println("Finished with result " + result + ".");
	        runner.stats(time, false);
            Console.OUT.println("--------");
	        for (count in counts) {
		        at (count) 
		          Console.OUT.println("Count(" + here.id +")=" + (count() as Counts).c);
	        }
        } catch (e:Throwable) {
            e.printStackTrace(Console.ERR);
        }
    }
}

// vim: ts=2:sw=2:et
