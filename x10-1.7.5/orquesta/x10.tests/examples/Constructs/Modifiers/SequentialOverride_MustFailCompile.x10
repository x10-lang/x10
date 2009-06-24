/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Check that a sequential method can be overridden only by a sequential method.
 * @author vj  9/2006
 */
public class SequentialOverride_MustFailCompile extends x10Test {

    class T1 {
      public sequential def m(): void = {
      }
    }
    class T2 extends T1 {
      public def m(): void = { /* should give a compile error. */ }
    }
   
	public def run(): boolean = {
		
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new SequentialOverride_MustFailCompile().execute();
	}

	
}
