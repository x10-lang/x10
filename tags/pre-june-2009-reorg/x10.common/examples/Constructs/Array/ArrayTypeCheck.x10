/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Array operations and points must be type checked.

 * @author kemal 4/2005
 */
public class ArrayTypeCheck extends x10Test {

	public boolean run() {
		int [.] a1 = new int[[0:2,0:3]->here](point p[i]){ return i; };
		System.out.println("1");
		final dist E = [-1:-2]->here;
		try {
		int [.] a2 = (int[:distribution==E])a1;
		return false;
		} catch (ClassCastException z) {
		System.out.println("2");
		}
		try {
		  final dist D = dist.factory.unique();
		  int [.] a3 = (int[:distribution == D])a1;
		  return false;
		} catch (ClassCastException z) {
			System.out.println("3");
		}
		
		int i = 1;
		int j = 2;
		int k = 0;
		point p = [i,j,k];
		point q = [i,j];
		point r = [i];
		if (p == q) return false;
		

		boolean gotException;
		System.out.println("5");
		try {
			return a1[i] == a1[i,j,k];
		} catch (RankMismatchException e) {
			gotException = true;
			System.out.println("Caught "+e);
		}
		if (!gotException) return false;

		return true;
	}

	public static void main(String[] args) {
		new ArrayTypeCheck().execute();
	}
}

