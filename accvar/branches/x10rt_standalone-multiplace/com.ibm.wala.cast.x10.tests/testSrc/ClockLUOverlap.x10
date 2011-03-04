/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */

/**************************************************************************************************************
                               LU Factorization with partial pivoting. 

The three steps (pivot, exchange, update) are sequential in LUPartial, although there is parallelism in each of them. 
This implementation overlaps these three steps using X10's conditional blocks. The operation of previous update is 
overlapped with the current ones of pivot and exchange. We use the flag approach to coordinate the three operations.

The computation is in place, where the part below the diagonal of A is the corresponding part of L (whose diagnals are 
all ones), and the rest is the corresponding part of U. The permutation information is stored in m_p so that L*U=A(m_p,:)
in Matlab notation. The matrix A can have any distribution. Note that the code is in the implicit syntax.

Author: Tong Wen, IBM Research
Date:   10/15/06
        11/08/06 clean up comments
        01/12/07 break up update to reduce the usage of flags and when clause
        01/22/07 propagate the bug fixes of LU_Overlap
******************************************************************************************************************/

public class ClockLUOverlap{
	final static double eps=0.00000000001;
	double [.] m_A; //Can this array be referred to globally? Right now, it can be (error?).
	region m_R;
	dist m_D;
	int m,n;
	int m_pivotInfo; 
	int [] m_p; //Can be also be referred to globally. It looks like an error.
	//flags for coordinating activities
	boolean pivotPlusExchangeDone;
	//array to store the scores of each row
	int [.] m_rowScore; 
	
	public void Clock(){}
	
	public void lu(final double [.] a_A){
		assert a_A.rank==2;
		m_A=a_A;
		m_R=a_A.region;
		m_D=a_A.distribution;
		m=m_R.rank(0).size(); n=m_R.rank(1).size();
		final int steps=Math.min(m,n)-1;
		final region STEPS=[-1:steps-1];
		m_p=new int [steps+1]; for (int i=0;i<steps+1;i++) {m_p[i]= i;};
		m_rowScore=new int [[0:m-1]];
		pivotPlusExchangeDone=true; //It is true when pivot() and exchange() are finished
		
		final ClockLUOverlap hack = this;
		
		final double [] pivots={0.0,0.0};	
		//for(int k=0;k<steps;k++){
		for (point [k]:STEPS){	
			when (pivotPlusExchangeDone) {pivotPlusExchangeDone=false;}
			pivots[0]=pivots[1];
			if (pivots[0]!=0) update1(k);
			if (k<steps) async{
				final int K=k+1;
				pivots[1]= hack.pivot(K);
				final int score=n-K;
				await ((m_rowScore[K]==score) && (m_rowScore[m_pivotInfo]==score));
				exchange(K);
				atomic pivotPlusExchangeDone=true;
			}
			if (pivots[0]!=0) 
				update2(k);
			else 
				for (int i=k+1;i<m;i++) atomic m_rowScore[i]=n-k-1;
		}
	}
	/*update the first column of the submatrix A[k:m-1, k:n-1]*/
	void update1(final int a_k){
		finish ateach (point [i,j]:m_D|[a_k+1:m-1,a_k+1:a_k+1]) m_A[i,j]-=m_A[i,a_k]*m_A[a_k,j];
	}
	/*unpdate the rest columns of the submatrix A[k:m-1, k:n-1]*/
	void update2(final int a_k){
		for (int i=a_k+1;i<m;i++) atomic m_rowScore[i]=1;
		finish ateach (point [i,j]:m_D|[a_k+1:m-1,a_k+2:n-1]){
			m_A[i,j]-=m_A[i,a_k]*m_A[a_k,j];
			async (m_rowScore.distribution[i]) atomic m_rowScore[i]++;
		}
	}
	/*exchange the k_th row with the row found in pivot()*/ 
	void exchange(final int a_k){
		final int pivotIdx=m_pivotInfo;
		if (pivotIdx!=a_k){
			//exchange the row a_k and m_pivotInfo[0]
			final region r=[a_k:a_k,0:n-1];
			finish ateach (point [i,j]: m_D|r){
				final double temp=m_A[i,j];
				m_A[i,j]=m_A[pivotIdx,j]; 
				m_A[pivotIdx,j]=temp;
			}
			m_p[pivotIdx]=m_p[a_k];m_p[a_k]=pivotIdx;
		}
	}
	/*Find the maximum abs value of the sub column [k:m-1] and its location
	  The all reduction uses the barrier approach. A point-to-point synchronization approach using flags can 
	  also be used. Note that here we also need the location of the maximum.
	 */ 
	double pivot(final int a_k){
		final region(:rank==2) r=[a_k:m-1,a_k:a_k];
		//padding to make the size a power of 2
		double logSize=Math.log(r.size())/Math.log(2);
	    	logSize=Math.ceil(logSize);
	    	final int factor=pow2((int)logSize);
		final dist(:rank==2) d=(dist(:rank==2))dist.factory.cyclic([a_k:a_k+factor-1,a_k:a_k]-r);
		final dist(:rank==2) Dr=(dist(:rank==2))(m_D|r);
		final dist D=Dr||d; //can't put casting in this statement. Have to do it separately
		//defining buffers
		final double [.] A=new double [D];
		final double [.] B=new double [D];
		finish ateach(point p: D|r) A[p]=m_A[p];
		final int [.] maxLocation=new int [D] (point [i,j]){return i;};
		//all reduction to find the value and location of the maximum
		final int phases=log2(factor);
		finish async{
			  final clock clk=clock.factory.clock();
			  ateach (point [i,j]:D) clocked(clk){
				boolean red=true;
				int Factor=factor;
				int shift;
				int disp=j;
				double temp;
				for (int l=0;l<phases;l++){
					shift=Factor/2;
					final int destProcID=(i-disp+shift)%Factor+(i-disp)/Factor*Factor+disp;
					if (red){
						temp=A[destProcID,j];
						if (Math.abs(A[i,j])<Math.abs(temp)){
							B[i,j]=temp;
							maxLocation[i,j]=maxLocation[destProcID,j];
						}
						else
							B[i,j]=A[i,j];
					}
					else{
						temp=B[destProcID,j];
						if (Math.abs(B[i,j])<Math.abs(temp)){
							A[i,j]=temp;
							maxLocation[i,j]=maxLocation[destProcID,j];
						}
						else
							A[i,j]=B[i,j];
					}
					clk.doNext();
					Factor/=2;
					red=!red;
					
				}
				if (!red) A[i,j]=B[i,j];
				
			  }
		}
		
		//scaling
		double temp=A[a_k,a_k]; //remote read. The location of a local element of A can be computed.
		if(temp!=0){
			finish ateach(point [i,j]:m_D|r){
				if (i!=maxLocation[i,j] && m_A[i,j]!=0) m_A[i,j]/=A[i,j];
			}
			m_pivotInfo=maxLocation[a_k,a_k]; //remote read. The same as above: a local copy can be found.
		}

		return temp;
	}
	public int log2(int a_int){ return (int)(Math.log(a_int)/Math.log(2));}
	public int pow2(int a_int){ return (int)Math.pow(2,a_int); }
	
	public boolean run() {
		//set up a test problem
		final int size=10;
		final region R=[0:size-1,0:size-1];
		final double [.] A=new double [dist.factory.cyclic(R)] (point [i,j]){
			int res=i%2;
			if (i-1==j) res=i*(res==0?-1:1);
			return res;};
			
		//compute LU factorization of A
		lu(A);
		/* verify results
		** The first column of L should be equal to array l.
		** The diagonals of U should be equal to array u and its first row are all ones.
		** The content of p should be equal to array permulation
		*/
		double err1=0; int err2=0;
		final double [] l={1, 0, 1, 0, 1, 0, 1, 0, 1, 0};
		final double [] u={1, -2, 2, -4, 4, -6, 6, -8, 8, 0};
		final int [] permulation={1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
		
		for (int i=0;i<size;i++){
			err1+=A[i,0]-l[i];
			err1+=A[i,i]-u[i];
			err1+=A[0,i]-1;
			err2+=m_p[i]-permulation[i];
		}
		return (Math.abs(err1)<eps && err2==0);
		
	}
	public static void main(String[] a) {
	
		finish async {
		new ClockLUOverlap().run();
		
		}
	}
	
}
