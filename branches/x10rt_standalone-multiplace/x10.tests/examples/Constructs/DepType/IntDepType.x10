/**
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that a depclause can be added to primitive types such as int.
 *
 * @author vj
 */
public class IntDepType extends x10Test {
   class Test(i:int, j:int) {
       public def this(i:int, j:int):Test = { this.i=i; this.j=j;}
    }
  
	public def run(): boolean = {
		var i: int{self == 0} = 0;
	   return true;
	}
	public static def main(var args: Rail[String]): void = {
		new IntDepType().execute();
	}
}
