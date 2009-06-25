/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that a safe method can be overridden only by a safe method.
 * @author vj  9/2006
 */
public class SafeOverride2_MustFailCompile extends x10Test {

    class T1 {
      public safe def m(): void = { }
    }
    class T2 extends T1 {
      public local nonblocking def m(): void = { /* should give a compile error. */ }
    }
   
	public def run(): boolean = {
		
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new SafeOverride2_MustFailCompile().execute();
	}

	
}
