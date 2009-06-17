/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;


public class ConstDist extends x10Test {

public boolean run() {
	
	region(:rank==2&&zeroBased) R =  [0:9,0:9];
	dist(:rank==2&&onePlace==here) D = R->here;
	double[:rank==2&&onePlace==here&&zeroBased] a =  new double[R->here];
	double[:rank==2&&onePlace==here&&zeroBased] b  =  new double[R->here];
		
	System.out.println("results are " + a + " " + b);
	return true;
}

public static void main(String[] args) {
	new ConstDist().execute();
}
}

