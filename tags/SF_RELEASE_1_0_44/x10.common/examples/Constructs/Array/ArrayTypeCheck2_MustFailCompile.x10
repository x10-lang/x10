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
 *
 * The expected result is that compilation must fail.
 *
 *
 * @author kemal 4/2005
 */
//LIMITATION:
//This test case will not meet expectations. It is a limitation of the current release.
// In trying to determine if two final variables of type dist are equa, 
// the compiler does not currently try to check statically whether there values are compile-time constants
// and are equal.

public class ArrayTypeCheck2_MustFailCompile extends x10Test {

	public boolean run() {
		final dist O = [0:2,0:3]->here;
		int [:distribution==O] a1 = new int[O](point p[i]){ return i; };
		System.out.println("1");
		final dist E = [-1:-2]->here;
		int [:distribution==E] a2 = (int[:distribution==E])a1;
		System.out.println("2");
		final dist D = dist.factory.unique();
		int [.] a3 = (int[:distribution == D])a2;
		System.out.println("3");
		int i = 1;
		int j = 2;
		int k = 0;
		point p = [i,j,k];
		point q = [i,j];
		point r = [i];
		if (p == q) return false;
		System.out.println("4");
		if (a1[q] + a3[q] != 2) return false;

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
		new ArrayTypeCheck2_MustFailCompile().execute();
	}
}

