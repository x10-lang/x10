/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks a constraint composed of a shortcut property is translated as a runtime constraint.
 * Issue: The array tested is not zeroBased.
 * @author vcave
 **/
public class ArrayDynamicCastFromMethod2 extends x10Test {

	public boolean run() {
		//invalid downcast
		try {
			int[:zeroBased] dynCast =  (int[:zeroBased]) this.createArray();			
		} catch(ClassCastException e) {
			return true;
		}
		return false;
	}
	
	public x10.lang.Object createArray() {
		dist d1 = [1:10,1:10]->here;
		int[.] x10array = new int[d1];
		return x10array;
	}

	public static void main(String[] args) {
		new ArrayDynamicCastFromMethod2().execute();
	}

}
 