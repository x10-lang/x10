/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * 
 * @author vj
 */
public class ArrayTypeCheck_MustFailCompile extends x10Test {

	public def run(): boolean = {
		var a1: Array[int]{rank==3} = new Array[int](Dist.makeConstant([0..2, 0..3], here), (var p: point[i]): int => { return i; });
		
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ArrayTypeCheck_MustFailCompile().execute();
	}
}
