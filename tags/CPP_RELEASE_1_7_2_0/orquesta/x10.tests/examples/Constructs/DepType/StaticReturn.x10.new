/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * The test checks that final fields can be accessed in a depclause.
 *
 * @author vj
 */
public class StaticReturn extends x10Test {
		 
	public def run(): boolean = {		
		var s: dist{rank==2} = starY();
		return true;
	}
	def starY(): dist{rank==2} = {	
		var d: dist{rank==2} = Dist.makeConstant([0..-1, 0..-1], here);	
		return d;
	}
	public static def main(var args: Rail[String]): void = {
		new StaticReturn().execute();
	}
}
