/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
Test that given

final double[:rank==2] buffDest = ...

the local variable declaration

double[:rank==buffDest.rank] buffSrc = new double[[1:10,1:10]->here];

does not give an error. That is, the compiler can make the inference that
(:rank==2) entails (:rank==buffDest.rank)
if buffDest is of type (:rank==2).

@author vj
**/
public class Transitivity extends x10Test {

	
	public boolean run() {
		final double[:rank==2] buffDest = new double[[1:10,1:10]->here];
		double[:rank==buffDest.rank] buffSrc = new double[[1:10,1:10]->here];
		
		return true;
	}
	
	public static void main(String args[]) {
		new Transitivity().execute();
	}
}

