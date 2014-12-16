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

/**
 * Test Fibonacci GLB version
 */
import harness.x10Test;
import x10.util.ArrayList;
import x10.io.File;

// SOURCEPATH: x10.dist/samples/GLB/bcg
// NUM_PLACES: 4
public class BCGTest extends x10Test{
	
	
	public def prepareGroundTruth(fileName:String):ArrayList[String]{
		btmList:ArrayList[String] = new ArrayList[String]();
		
		val I  = new File(fileName);
		for (line in I.lines()) {
			btmList.add(line);
		}
		return btmList;
	
	}
	
	/**
	 * substring helper function
	 */
	public static def sub(str:String, start:Int, end:Int) = str.substring(start, Math.min(end, str.length()));
	
	/**
	 * Dump the betweenness map.
	 * @param numDigit number of digits to print
	 */
	public final def printBetweennessMap(betweennessMap:Rail[Double]):ArrayList[String] {
		val N = betweennessMap.size;
		btmList:ArrayList[String] = new ArrayList[String]();
		for(var i:Int=0n; i<N; ++i) {
			
			if(betweennessMap(i) != 0.0) {
				entry:String = "(" + i + ") -> " + sub(""+betweennessMap(i), 0n, 6n);
			btmList.add(entry);
			
			}
		}
		return btmList;
	}


    /**
     * Per runTest.sh, X10_TEST_DIR is set as $X10_HOME/x10.tests"
     */
    public def run():boolean {
	val filename:String = pathCombine(["tests", "Samples", "GLB", "bcg"], "bc_n14.txt");
	gt:ArrayList[String] = prepareGroundTruth(filename);
	chk(gt.size() == 13318L); // creepy, i know
	val args = new Rail[String](2L);
	args(0) = "-n";
	args(1) = "14";
	val result:Rail[Double] = BCG.mainTest(args);
	resultL:ArrayList[String] = printBetweennessMap(result);
	chk(resultL.size() == 13318L);
	return (mychk(gt, resultL));
	
    }


    public def mychk(l1: ArrayList[String], l2:ArrayList[String]):Boolean{
		if(l1.size() != l2.size()) return false;
		r1: Rail[String] = l1.toRail();
		r2:Rail[String] = l2.toRail();
		var idx:Long = 0L;
		val max:Long = l1.size();
		for(idx = 0L; idx < max; idx++){
			if(!r1(idx).equals(r2(idx))){
				return false;
			}
		}
		return true;
		
   }
    
    public static def main(args:Rail[String]) {
	new BCGTest().execute();
    }
}
