//import harness.x10Test;

/***************************************************************************************
         An example of implementing all reduction using X10's conditional blocks

In this implementation, we use the point-to-point approach to reduce the synchronization
overhead of the barrier one (shown in ReductionBarrier). The point-to-point synchronization
is implemented using flags, where X10's conditional blocks are used. However, in X10 the 
issue of which one is faster under what circumstance is not clear at this moment. 

The length of the input array on which the all reduction is performed is equal to the number
of places used to run this code. For simplicity, it is assumed that the number of places is 
a power of 2.

Also, the implicit syntax for accessing a remote array element is used here.

Date:   11/09/06

Author: Tong Wen @IBM Research

Modified by T.W. 11/29/2007: comment out the import statements;
                             use dist.UNIQUE. 
*****************************************************************************************/

public class  AllReductionP2P extends x10Test {

	public boolean run() {
		final dist ALLPLACES=dist.UNIQUE;//the size must be a power of 2
	    	final int numPlaces=place.MAX_PLACES;
	    	assert powerOf2(numPlaces);
	    	
	    	final double [.] A=new double [ALLPLACES] (point[i]){return 1;};
	    	
	        /*define the buffers*/
		final double [.] B=new double [ALLPLACES] (point[i]){return 0;};
	        /*Flag1 is used to align the phase of each pair, 
	          while Flag2 is used to coordinate the exchange of values between them.
	        */  
		final int [.] Flag1=new int [ALLPLACES] (point [i]){return -1;};
		//final int [.] Flag2=new int [ALLPLACES] (point [i]){return -1;}; //use only Flg1 for synchronization
		final int factor=numPlaces;
		final int phases=log2(factor);
		  
		finish  ateach (point [i]:ALLPLACES){
			int Factor=factor;
			int shift;
			for (int j=0;j<phases;j++){
				shift=Factor/2;
				final int destProcID=(i+shift)%Factor+i/Factor*Factor;
				//when (Flag1[destProcID]==(j-1)) {}
				//when (Flag1[destProcID]==(2*j-1)) {}
				await (Flag1[destProcID]==(2*j-1));
					B[i]=A[destProcID]; 
					//async (ALLPLACES[destProcID]) atomic Flag2[destProcID]++;
					async (ALLPLACES[destProcID]) atomic Flag1[destProcID]++;
				//await (Flag2[i]==j);
				await (Flag1[i]==2*j);
					A[i]+=B[i];
					//Flag1[i]++;
					atomic Flag1[i]++;
				
				Factor/=2;
			}
		}
		return (A[0]==numPlaces);
	}

	
	public static void main(String[] args) {
		new AllReductionP2P().execute();
	}
}

