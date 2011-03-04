/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
Test that a deptype for a method arg that depends on a previous arg is handled correctly.
@author vj
**/
public class DepTypeInMethodArgDependsOnArg extends x10Test {

  public static def arraycopy(val a_dest: Array[double], 
			      val a_src: Array[double]{rank==a_dest.rank}): void = {	
			      }
	public def run(): boolean = {
		val buffDest: Array[double]{rank==2} 
                    = Array.make[double]([1..10, 1..10]->here);
		val buffSrc = buffDest;
		arraycopy(buffDest,  buffSrc);
		return true;
	}
	
	public static def main(var args: Rail[String]): void = {
		new DepTypeInMethodArgDependsOnArg().execute();
	}
}
