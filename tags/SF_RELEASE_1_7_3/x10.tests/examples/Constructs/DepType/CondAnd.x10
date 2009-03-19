/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that properly typed arguments are accepted by &&
 *
 * @author vj
 */
public class CondAnd extends x10Test {
   
   
	public def run(): boolean = {
	   val r1 = 0..100;
	   val r2 = 2..99;
	   val r3 = r1 && r2;
	   return true;
	}
	public static def main(var args: Rail[String]): void = {
		new CondAnd().execute();
	}
}
