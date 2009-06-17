/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that the variable being defined can be used in the depclause of its type.
 *
 * @author vj
 */
public class VarBeingDefinedInType extends x10Test {
    
	public boolean run() {
		int(:v ==0) v = 0;
		
	    return v==0;
	}
	public static void main(String[] args) {
		new VarBeingDefinedInType().execute();
	}
}
