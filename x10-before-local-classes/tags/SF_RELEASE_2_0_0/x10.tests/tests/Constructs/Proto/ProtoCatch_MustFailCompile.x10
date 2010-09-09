/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Throw/Catch types cannot be proto types.
 * @author vj
 */
public class ProtoCatch_MustFailCompile extends x10Test {

	value A extends Throwable {}
    
	def m() throws A {
		
	}
    public def run() {
    	try {
    		m();
    	} catch (z: proto A) {
    		
    	}
    }

    public static def main(Rail[String])  {
	new ProtoCatch_MustFailCompile().execute();
    }
}
