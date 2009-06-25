/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
 
import harness.x10Test;

/**
 *
 * Check that a field can be declared at a deptype.
 *
 */
public class FieldDepType extends x10Test {
    var f: Array[double](0..10)
	= Array.make[double](0..10, (i: Point)=> (10-i(0)) as double);
	
	def m(a: Array[double]{rank==1&&rect&&zeroBased}): void = {
	}
	public def run(): boolean = {
		m(f);
		return f(0)==10.0D;
	}
	public static def main(Rail[String]): void = {
		new FieldDepType().execute();
	}
}
