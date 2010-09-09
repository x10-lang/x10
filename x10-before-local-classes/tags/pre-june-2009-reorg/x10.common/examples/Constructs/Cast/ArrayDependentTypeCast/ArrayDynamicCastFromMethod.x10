/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks dynamic cast works for method invocation that returns an X10 array.
 * Issue: Returned array does not meet rank constraint of targeted type.
 * int[:rect&&rank==3] <-- int[:rect&&rank==2&&zerobased]
 * @author vcave
 **/
public class ArrayDynamicCastFromMethod extends x10Test {

	public boolean run() {
		//invalid downcast
		try {
			int[:rank==3&&rect] dynCast =  (int[:rect&&rank==3]) this.createArray();			
		} catch(ClassCastException e) {
			return true;
		}
		return false;
	}
	
	public x10.lang.Object createArray() {
		region e = region.factory.region(0, 10);
		dist(:rank==2&&rect&&zeroBased) d1 = [0:10,0:10]->here;
		int[.] x10array = new int[d1];
		return x10array;
	}

	public static void main(String[] args) {
		new ArrayDynamicCastFromMethod().execute();
	}

}
 