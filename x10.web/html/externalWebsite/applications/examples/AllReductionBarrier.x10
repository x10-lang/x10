//import harness.x10Test;

/***************************************************************************************
An example of implementing all reduction using X10's clock 

In this implementation, a barrier is needed after each phase which is implemented using 
X10's clock. The length of the array on which the all reduction is performed is equal to
the number of places used to run this code. For simplicity, it is assumed that the number
of places is a power of 2.

Also, the explicit and implicit syntax for accessing a remote array element are presented.


Date:   11/09/06

Author: Tong Wen @IBM Research

Modified by T.W. 11/29/2007: comment out the import statements;
                             use dist.UNIQUE. 
*****************************************************************************************/

public class  AllReductionBarrier extends x10Test {

	public boolean run() {
		final dist ALLPLACES=dist.UNIQUE;//the size must be a power of 2
	    	final int numPlaces=place.MAX_PLACES;
	    	assert powerOf2(numPlaces);
	    	final double [.] A=new double [ALLPLACES] (point[i]){return 1;};
	    	
	    	/*using the buffer approach to avoid dependence between each pair*/
	    	final double [.] B=new double [ALLPLACES] (point[i]){return 0;};
	    	final int factor=numPlaces;
	    	final int phases=log2(factor);
	    	
		finish async{
			  final clock clk=clock.factory.clock();
			  ateach (point [i]:ALLPLACES) clocked(clk){
				boolean red=true;
				int Factor=factor;
				int shift;
				
				for (int j=0;j<phases;j++){
					shift=Factor/2;
					final int destProcID=(i+shift)%Factor+i/Factor*Factor;
					if (red){
						//B[i]=future(A.distribution[destProcID]){A[destProcID]}.force();
						//B[i]+=A[i];
						B[i]=A[i]+A[destProcID];
					}
					else{
						//A[i]=future(ALLPLACES[destProcID]){B[destProcID]}.force();
						//A[i]+=B[i];
						A[i]=B[i]+B[destProcID];
					}
					next;
					Factor/=2;
					red=!red;
				}
				if (!red) A[i]=B[i];
			  }
		}
		return (A[0]==numPlaces);
	}

	
	public static void main(String[] args) {
		new AllReductionBarrier().execute();
	}
}

