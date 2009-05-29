/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that array deptypes are properly processed.
 *
 * @author vj
 */
public class DepTypeRef extends x10Test {
	public def run(): boolean = {
  	  var R: Region{rect} = [1..2, 1..2];
	  var a: Array[double]{rect} = Array.make[double](R, (p: Point) => 1.0);
	   return true;
	}
	public static def main(var args: Rail[String]): void = {
		new DepTypeRef().execute();
	}
}
