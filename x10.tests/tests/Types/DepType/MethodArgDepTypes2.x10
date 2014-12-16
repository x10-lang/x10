/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;
import x10.util.concurrent.Future;
import x10.regionarray.*;

/**
 * Check that a method arg can have a deptype and it is propagated into the body.
 *
 * @author vj
 */
public class MethodArgDepTypes2 extends x10Test {
	public static def arraycopy(val a_dest: DistArray[double], 
	                            val a_src: DistArray[double]{rank==a_dest.rank}): void = {	
    	  val R: Region{rank==a_dest.rank} = a_src.region&& a_dest.region; 
    	  finish for (val p: Point{rank==a_dest.rank} in R) async {
    	  //finish for( point p : R){	  
    	    a_dest(p)= Future.make(() => at(a_src.dist(p)) { return a_src(p);}).force();
    	  }	  
    	  //for( point p : R) a_dest[p]=a_src[p]; //implicit syntax
	}
  
	public def m(val a: DistArray[double]): void = {
		var result: DistArray[double] = a;
		val buffDest: DistArray[double] = a;
		
		val buffSrc =  buffDest;
		arraycopy(buffDest, buffSrc);
	
	}
	public def run()=true;
	
	public static def main(var args: Rail[String]): void = {
		new MethodArgDepTypes2().execute();
	}
}
