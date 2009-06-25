/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;
/**
** The partition method of region is called, which should never be. Whether the run time error occurs or not
** depends on the number of places  used (for instance, 3)and the value of k. 
** @author Tong
** @date 10/31/06
**/
public class RegionDifference extends x10Test {

	public def run(): boolean = {
		val size: int = 10;
	    	val k: int = 5;
	    	val factor: int = 8;
	    	val r: region{rank==2} = [k..size-1, k..k];
	    	//System.out.println(([k:k+factor-1,k:k]-r).toString());
	    	val d: dist = dist.makeCyclic([k..k+factor-1, k..k]-r);
	    	
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new RegionDifference().execute();
	}
}
