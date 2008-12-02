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
* Building arrays distributed accross places using the encapsulation approach 
* (2D array of 2D arrays).
* @author Tong
  11/29/2006
*/
public class EncapsulatedArray2D_Dep extends x10Test {
	
	static value Wrapper{
		double [.] m_array;
		Wrapper(double [.] a_array){
			m_array=a_array;
		}
	}
	
        public boolean run() {
        	final int size=5;
        	final region(:rank==2) R=[0:size-1,0:size-1];
        	final dist(:rank==2) D=(dist(:rank==2))dist.factory.cyclic(R); //the casting here is not natural.
        	
        	final Wrapper value [:rank==2] A=new Wrapper value [D];
        	finish ateach(point [i,j]: D) A[i,j]=new Wrapper(new double [R]);
        		
        	//for (int i=0;i<numOfPlaces;i++){	
        	finish ateach (point [i,j]: D){ 
        		final double [:rank==2] temp=(double [:rank==2])A[i,j].m_array; 
        		for (point p: temp) temp[p]=i+j;
        	}
    	
	    return true;
	}
	
	public static void main(String[] args) {
		new EncapsulatedArray2D_Dep().execute();
	}

}

