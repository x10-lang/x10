//import x10.lang.Object;
//import harness.x10Test;

/**
* Building arrays distributed accross places using the encapsulation approach 
* (2D array of 2D arrays).
* @author Tong 11/29/2006
* Modified by T.W. 11/29/2007: comment out the import statements;
*                              use array initializer. 
*/
public class EncapsulatedArray2D extends x10Test {
	
	static value Wrapper{
		double [.] m_array;
		Wrapper(double [.] a_array){
			m_array=a_array;
		}
	}
	
        public boolean run() {
        	final int size=5;
        	final region R=[0:size-1,0:size-1];
        	final dist D=dist.factory.cyclic(R); 
        	
        	final Wrapper value [.] A=new Wrapper value [D] (point [i,j]) {return new Wrapper(new double [R]);};
        	//finish ateach(point [i,j]: D) A[i,j]=new Wrapper(new double [R]);
        		
        	//for (int i=0;i<numOfPlaces;i++){	
        	finish ateach (point [i,j]: D){ 
        		final double [.] temp=A[i,j].m_array; 
        		for (point p: temp) temp[p]=i+j;
        	}
    	
	    return true;
	}
	
	public static void main(String[] args) {
		new EncapsulatedArray2D().execute();
	}

}

