/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks dynamic cast from object to X10 array works properly.
 * Issue: Object does not meet rank constraint of targeted type.
 * int[:rect&&rank==3] <-- int[:rect&&rank==2&&zerobased]
 * @author vcave
 **/
public class ArrayDynamicCastFromObject extends x10Test {

	public boolean run() {
		try {
			// array creation
			region e = region.factory.region(0, 10);
			dist(:rank==2&&rect&&zeroBased) d1 = [0:10,0:10]->here;
			int[.] x10array = new int[d1];
			// upcast
			x10.lang.Object obj = x10array;
			//invalid downcast
			int[:rank==3&&rect] dynCast =  (int[:rect&&rank==3]) obj;		
		} catch (ClassCastException e) {
			return true;
		}

		return false;
	}

	public static void main(String[] args) {
		new ArrayDynamicCastFromObject().execute();
	}

}
 