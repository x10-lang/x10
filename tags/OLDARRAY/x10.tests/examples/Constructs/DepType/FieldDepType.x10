/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
 
 /**
 *
 * Check that a field can be declared at a deptype.
 *
 */
 
import harness.x10Test;;

public class FieldDepType extends x10Test {
	var f: Array[double]{rank==1 && rect &&zeroBased} 
	= Array.make[double](0..10, (i: point)=> (10-i(0)) to double);
	
	def m(var a: Array[double]{rank==1&&rect&&zeroBased}): void = {
		
	}
	public def run(): boolean = {
		m(f);
		System.out.println("f[0] = " + f(0));
		return true;
	}
	public static def main(var a: Rail[String]): void = {
		new FieldDepType().execute();
	}
}
