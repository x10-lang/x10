/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Dynamic widening cast from Object to X10 array.
 * @author vcave
 **/
public class ArrayDynamicCast extends x10Test {

	public boolean run() {
		// constrained distribution
		dist(:rank==2&&rect&&zeroBased) d1 = [0:10,0:10]->here;

		// array creation
		int[.] x10array = new int[d1];
		x10.lang.Object obj = x10array;
		
		// widening cast 
		int[:rank==2&&rect] dynCast =  (int[:rank==2&&rect]) obj;
		return true;
	}

	public static void main(String[] args) {
		new ArrayDynamicCast().execute();
	}

}
 