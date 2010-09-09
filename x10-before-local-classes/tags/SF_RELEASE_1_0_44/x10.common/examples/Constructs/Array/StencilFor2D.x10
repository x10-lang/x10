/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import x10.lang.Object;
import harness.x10Test;

/**
*  Implementing a 5-point stencil operation using for loop
* @author Tong
  11/29/2006
*/
public class StencilFor2D extends x10Test {
	
        public boolean run() {
        	final region R=[-1:256,-1:256], r=[0:255,0:255];
        	final point north=[0,1], south=[0,-1], west=[-1,0], east=[1,0];
        	final double [.] A=new double [R];
        	final double h=0.1;
        	
        	for(point p: r) A[p]=(A[p+north]+A[p+south]+A[p+west]+A[p+east]-4*A[p])*h;
        	
	    return true;
	}
	
	public static void main(String[] args) {
		new StencilFor2D().execute();
	}

}

