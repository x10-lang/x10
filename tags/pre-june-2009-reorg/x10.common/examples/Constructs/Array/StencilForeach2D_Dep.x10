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
*  Implementing a 5-point stencil operation using foreach loop
* @author Tong
  11/29/2006
*/
public class StencilForeach2D_Dep extends x10Test {
	
        public boolean run() {
        	final region(:rank==2) R=[-1:256,-1:256], r=[0:255,0:255];
        	final point north=[0,1], south=[0,-1], west=[-1,0], east=[1,0]; //rank is not a property of point!
        	final double [:rank==2] A=(double [:rank==2])new double [R]; //casting is not natrual here!
        	final double h=0.1;
        	
        	finish foreach (point p: r) A[p]=(A[p+north]+A[p+south]+A[p+west]+A[p+east]-4*A[p])*h;
        	
	    return true;
	}
	
	public static void main(String[] args) {
		new StencilForeach2D_Dep().execute();
	}

}

