/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
Test that methods whose return types are deptypes are handled correctly when
they are defined in another class, in a different source unit from the class
which references them. This program should not give a compile-time error -- the
compiler should be able to establish that the arguments for - are regions of
rank==3.
@author vj
**/
public class TypeElaborationAcrossCompilationUnits extends x10Test {

	
	public boolean run() {
		Temp t = new Temp();
		region(:rank==3) b = t.m([1:10,1:10,1:10]);
		return true;
	}
	
	public static void main(String args[]) {
		new TypeElaborationAcrossCompilationUnits().execute();
	}
}

