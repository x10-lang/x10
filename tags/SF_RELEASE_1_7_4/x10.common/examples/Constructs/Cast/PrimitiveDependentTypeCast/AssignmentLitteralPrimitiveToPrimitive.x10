/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks assignment of primitive to constrained primitives.
 * @author vcave
 **/
public class AssignmentLitteralPrimitiveToPrimitive extends x10Test {

	 public boolean run() {
		byte(:self==1) bb = 1;
		short(:self==10) ss = 10;
		int(:self==20) ii = 20;
		int(:self==-2) iii = -2;
		long(:self==30) ll = 30;
		float(:self==0.001F) ff = 0.001F;
		double (: self == 0.001) i = 0.001;
		char(:self=='c') cc = 'c';
		
		return true;
	}

	public static void main(String[] args) {
		new AssignmentLitteralPrimitiveToPrimitive().execute();
	}
}
 

 