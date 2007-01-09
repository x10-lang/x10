/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/***************************************************************************************
An example of implementing all reduction using X10's clock 

In this implementation, a barrier is needed after each phase which is implemented using 
X10's clock. The length of the array on which the all reduction is performed is equal to
the number of places used to run this code. For simplicity, it is assumed that the number
of places is a power of 2.

Also, the explicit and implicit syntax for accessing a remote array element are presented.


Date:   11/09/06

Author: Tong Wen @IBM Research
*****************************************************************************************/

public class  AllReductionBarrier_Dep extends x10Test {
	public boolean powerOf2(int a_int){
		assert a_int>0;
		int i=(int)Math.abs(a_int);
		if (i==0) return false;
		else{
			if (i!=(pow2(log2(i)))) return false;
		}
		return true;
	}
	public int log2(int a_int){
		return (int)(Math.log(a_int)/Math.log(2));
	}
	public int pow2(int a_int){
		return (int)Math.pow(2,a_int);
	}
	public boolean run() {
		final dist(:rank==1) ALLPLACES=(dist(:rank==1))dist.factory.unique();//the size must be a power of 2
	    	final int numPlaces=place.MAX_PLACES;
	    	assert powerOf2(numPlaces);
	    	final double [:rank==1] A=new double [ALLPLACES] (point[i]){return 1;};
	    	
	    	/*using the buffer approach to avoid dependence between each pair*/
	    	final double [:rank==1] B=new double [ALLPLACES];
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
					double [:rank==1] result, buffDest,buffSrc;
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
		new AllReductionBarrier_Dep().execute();
	}
}

