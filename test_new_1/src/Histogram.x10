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

import x10.util.Random;
import x10.io.Console;
import x10.compiler.Transaction;

public class Histogram {

	/**
    * Compute the histogram of the array a in the rail b.
    */
    public static def run(a:Array[int](1), b:Array[int](1), nThreads:int, isSTM:boolean) {
    	
	    val chunk_size = (a.size / nThreads);
	    finish for (var k:int=0; k < nThreads; k++)
	    {
	    	
	    	async {
	    		Runtime.initTMThread();
	    		
	    		val uniq_id = Runtime.getTMThreadUniqId();
	    		val start_index = uniq_id * chunk_size;
	    		
	    		Console.OUT.println("WorkerID = " + uniq_id);
	    		
	    		
	    		var bin:int;
	    		for (var i:int=0; i < chunk_size; i++)
	    		{
	    			bin = a(start_index + i) % b.size;
	    			if (isSTM)
	    			{
	    				@Transaction
	    				atomic b(bin)++;
	    			} else {
	    				atomic b(bin)++;
	    			}	
	    		}
	    		
	    		Runtime.finishTMThread();
	    	}
	    }
    }

    public static def compute(N:int, S:int, nThreads:int, isSTM:boolean):boolean {
		val a = new Array[int](N, (i:int)=> i);
		val b = new Array[int](S);
		
		
		run(a, b, nThreads, isSTM);
		
		Console.OUT.println("------------");
		for ([x] in b) 
		{
			Console.OUT.println("b["+x+"]="+b(x));
		}
		Console.OUT.println("------------");
		val v = b(0);
	    var ok:boolean = true;
		for ([x] in b) ok &= (b(x)==v);
	    return ok;
    }

    public static def main(args:Array[String](1)) {
		if (args.size != 4) {
		    Console.OUT.println("Usage: Histogram SizeOfArray Buckets NThreads IsStm");
		    return;
	        }
		val N = int.parse(args(0));
		val S = int.parse(args(1));
		val N_THREADS = int.parse(args(2));
		val isSTM = (int.parse(args(3)) == 1)? true : false;
	    
		Runtime.initTMSystem();
		
		val ok = compute(N, S, N_THREADS, isSTM);
	        
		if (ok) {
		    Console.OUT.println("Test ok.");
		} else {
		    Console.OUT.println("Test failed.");
		}
		
		Runtime.finishTMSystem();
    }
}
