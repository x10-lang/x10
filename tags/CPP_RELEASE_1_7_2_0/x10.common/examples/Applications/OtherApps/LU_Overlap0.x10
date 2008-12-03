/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
                               LU Factorization with partial pivoting. 

The three steps (pivot, exchange, update) are sequential in LU, although 
there is parallelism in each of them. This implementation overlaps these 
three steps using X10's conditional blocks. The operation of previous update is 
overlapped with the current ones of pivot and exchange. We use flags to 
coordinate the three operations.

The computation is in place, L is represented by the elements of A below the diagonal,
(the diagonal is all-1) and U is represnted by the elements of A above the diagonal.
The permutation information is stored in p so that L*U=A(p,:) in Matlab notation. 
The matrix A can have any distribution. 

The code uses implicit syntax.

Author: Tong Wen, IBM Research
Date:   10/15/06
        11/08/06 clean up comments
        12/24/06 vj -- cosmetic touches to code
        01/23/07 change synchronization style by breaking up the update operation
**/

public class LU_Overlap0(region(:rank==2&& zeroBased&&rect) R, 
		dist(:region==this.R) D,
		double[:distribution==this.D] A) extends x10Test {
	final static double eps=0.00000000001;
	final region(:rank==1 && zeroBased && rect) Rows;
	final int m,n;
	final int [] p;
	final int steps;
	final region Steps;
	
        boolean pivotPlusExchangeDone=true;
	int m_pivotInfo; 
	//array to store the scores of each row
	final int [.] m_rowScore; //Compiler currently does not complain if not putting final here.
	
	
	public LU_Overlap0(:self.R==R&&self.D==D&&self.A==A)
	(final region(:rank==2&&zeroBased&&rect) R, 
			final dist(:region==R) D, 
			final double[:distribution==D] A) {
		property(R,D,A);
		m = R.rank(0).size();
		n = R.rank(1).size();
		steps=Math.min(m,n)-1;
		Steps=[-1:steps-1];
		Rows = [0:m-1];
		// p=new int [steps+1](point[i]) { return i;};
		p=new int [steps+1]; for (point [i] : [0:steps]) {p[i]= i;};
		m_rowScore=new int [Rows] (point [i]) {return n;};
	}
	
	public void lu(){
		final double [] pivots={0.0,0.0};	
		for (point [k]:Steps){	
			when (pivotPlusExchangeDone) {pivotPlusExchangeDone=false;}
			pivots[0]=pivots[1];
			if (pivots[0]!=0) update1(k);
			if (k<steps) async{
				final int K=k+1;
				pivots[1]=pivot(K);
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
		finish ateach (point [i,j]:D|[a_k+1:m-1,a_k+1:a_k+1]) A[i,j]-=A[i,a_k]*A[a_k,j];
	}
	/*unpdate the rest columns of the submatrix A[k:m-1, k:n-1]*/
	void update2(final int a_k){
		for (int i=a_k+1;i<m;i++) atomic m_rowScore[i]=1;
		finish ateach (point [i,j]:D|[a_k+1:m-1,a_k+2:n-1]){
			A[i,j]-=A[i,a_k]*A[a_k,j];
			async (m_rowScore.distribution[i]) atomic m_rowScore[i]++;
		}
	}
	
	/*exchange the k_th row with the row found in pivot()*/ 
	void exchange(final int k){
		final int pivotIdx=m_pivotInfo;
		if (pivotIdx!=k){
			//exchange the row k and m_pivotInfo[0]
			final region(:rank==2) r= row(k);
			finish ateach (point [i,j]: D|r){
				final double temp=A[i,j];
				A[i,j]=A[pivotIdx,j]; 
				A[pivotIdx,j]=temp;
			}
			p[pivotIdx]=p[k];
			p[k]=pivotIdx;
		}
	}
	
	/*Find the maximum abs value of the sub column [k:m-1] and its location
	  The all reduction uses the barrier approach. A point-to-point synchronization 
	  approach using flags can also be used. Note that here we also need the 
	  location of the maximum.
	 */ 
	double pivot(final int k){
		final region(:rank==2) r=[k:m-1,k:k];
		//padding to make the size a power of 2
		final double logSize=Math.ceil(Math.log(r.size())/Math.log(2));
	    	final int factor=pow2((int)logSize);
		final dist(:rank==2) d=(dist(:rank==2))dist.factory.cyclic([k:k+factor-1,k:k]-r);
		final dist(:rank==2) Dr= (dist(:rank==2)) D|r;
		final dist D=Dr||d; 
		
		//defining buffers
		final double [:distribution==D] myA=new double [D], B=new double [D];
		finish ateach(point p: Dr) myA[p]=A[p];
		final int [.] maxLocation=new int [D] (point [i,j]){return i;};
		
		//all reduction to find the value and location of the maximum
		final region Phases = [0:log2(factor)-1];
		finish async {
		  final clock clk=clock.factory.clock();
		  ateach (point [i,j]:D) clocked(clk){
			boolean red=true;
			int Factor=factor;
			int disp=j;
			for (point [l] : Phases){
				int shift=Factor/2;
				final int destProcID=(i-disp+shift)%Factor+(i-disp)/Factor*Factor+disp;
				if (red){
				   double temp=myA[destProcID,j];
				   if (Math.abs(myA[i,j])<Math.abs(temp)){
					B[i,j]=temp;
					maxLocation[i,j]=maxLocation[destProcID,j];
				    } else
					B[i,j]=myA[i,j];
				} else {
				   double temp=B[destProcID,j];
				   if (Math.abs(B[i,j])<Math.abs(temp)){
					myA[i,j]=temp;
					maxLocation[i,j]=maxLocation[destProcID,j];
				    } else
					myA[i,j]=B[i,j];
				}
				next;
				Factor/=2;
				red=!red;	
			}
			if (!red) myA[i,j]=B[i,j];
			}
		}
		
		//scaling
		//remote read. The location of a local element of myA can be computed.
		double temp=myA[k,k]; 
		if(temp!=0){
			finish ateach(point [i,j]:D|r)
				if (i!=maxLocation[i,j] && A[i,j]!=0) 
					A[i,j]/=myA[i,j];
//			remote read. The same as above: a local copy can be found.
			m_pivotInfo=maxLocation[k,k]; 
		}

		return temp;
	}
	/* verify results
	** The first column of L should be equal to array l.
	** The diagonals of U should be equal to array u and its first row are all ones.
	** The content of p should be equal to array permulation
	*/
	public boolean verify() {
		double err1=0; int err2=0;
		final double [] l={1, 0, 1, 0, 1, 0, 1, 0, 1, 0};
		final double [] u={1, -2, 2, -4, 4, -6, 6, -8, 8, 0};
		final int [] permulation={1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
		for (int i=0;i<m;i++){
			err1+=A[i,0]-l[i];
			err1+=A[i,i]-u[i];
			err1+=A[0,i]-1;
			err2+=p[i]-permulation[i];
		}
		return (Math.abs(err1)<eps && err2==0);
	}

	public boolean run() {
		lu();
		return verify();
	}

	public int log2(int a_int){ return (int)(Math.log(a_int)/Math.log(2));}
	public int pow2(int a_int){ return (int)Math.pow(2,a_int); }
	public region(:rank==2) col(region(:rank==1) r, int colid) { return [r,colid:colid];}
	public region(:rank==2) row(int rowid, region(:rank==1) r) { return [rowid:rowid,r];}
	public region(:rank==2) row(int rowid) { return row(rowid,[0:n-1]);}
	
	public static void main(String[] args) {
//		set up a test problem
		final int size=10;
		final region(:rank==2&&zeroBased&&rect) R=[0:size-1,0:size-1];
		final dist(:region==R) D = (dist(:region==R)) dist.factory.cyclic(R);
		final double [:distribution==D] A = 
			(double[:distribution==D]) 
			   new double [D] (point [i,j]){
			      int res=i%2;
			      if (i-1==j) res=i*(res==0?-1:1);
			      return res;
			      };
			
		new LU_Overlap0(R, D, A).execute();
	}
}

