//import harness.x10Test;

/**
LU Factorization with partial pivoting. 
A literal translation of the following Matlab code:
		function [L,U,p] = lutx(A)
		%LUTX  Triangular factorization, textbook version
		%   [L,U,p] = lutx(A) produces a unit lower triangular matrix L,
		%   an upper triangular matrix U, and a permutation vector p,
		%   so that L*U = A(p,:)

		[n,n] = size(A);
		p = (1:n)';

		for k = 1:n-1

		   % Find index of largest element below diagonal in k-th column
		   [r,m] = max(abs(A(k:n,k)));
		   m = m+k-1;

		   % Skip elimination if column is zero
		   if (A(m,k) ~= 0)
		   
		      % Swap pivot row
		      if (m ~= k)
		         A([k m],:) = A([m k],:);
		         p([k m]) = p([m k]);
		      end

		      % Compute multipliers
		      i = k+1:n;
		      A(i,k) = A(i,k)/A(k,k);

		      % Update the remainder of the matrix
		      j = k+1:n;
		      A(i,j) = A(i,j) - A(i,k)*A(k,j); 
		   end
		end

		% Separate result
		L = tril(A,-1) + eye(n,n);
		U = triu(A);
*
*
* @author Tong
*  Modified by T.W. 11/29/2007: comment out the import statements;
*                              add more property. 
**/
public class LU_Dep extends x10Test {
	 const double eps = 0.000000000001;
	 public static void lu(final double [:rank==2&&rect] a_A, final double [:rank==2&&rect] a_L, final double [:rank==2&&rect] a_U,  final int [:rank==1&&rect] a_p){
			final region(:rank==2&&rect) R=a_A.region;
			final dist(:rank==2&&rect) D=a_A.distribution;
			final int n=R.rank(0).size();
			assert n==R.rank(1).size();
			//precompute the index sets
			final region(:rank==1&&rect) N = [0:n-1];
			final region(:rank==1&&rect) NLess1 = [0:n-2];
			final region(:rank==1) value [:rank==1&&rect] NCurrent 
			    = new region value [NLess1] (point [k]) { return [k+1:n-1];};
			    
			final double [:rank==2&&rect] A=new double [D] (point [i,j]){return a_A[i,j];}; //In Matlab, A is passed by value.
			finish foreach (point [i]:a_p) a_p[i]=i;
			
			double r; int maxIdx;
			for(point [k]:NLess1){	
				// Find index of largest element below diagonal in the k-th column
				r=A[k,k]; maxIdx=k;
				for (point [i]: NCurrent[k]){
					if (Math.abs(A[i,k])>Math.abs(r)){
						r=A[i,k]; maxIdx=i;
					}
				}
				final int m=maxIdx;
				// Skip elimination if column is zero
				if (r!=0)
				{
					// Swap pivot row
					if (m!=k){
						finish foreach (point [j]: N){
							double temp=A[k,j];
							A[k,j]=A[m,j]; 
							A[m,j]=temp;
						}
						a_p[m]=a_p[k];a_p[k]=m;
					}
					
					// Compute multipliers
					finish foreach (point [i]: NCurrent[k]) A[i,k]/=A[k,k];
					
					// Update the remainder of the matrix
					finish foreach (point [i,j]:[(region(:rank==1))NCurrent[k],(region(:rank==1))NCurrent[k]]) //R[k] returns a general region?
					//finish foreach (point [i,j]:[NCurrent[k],NCurrent[k]]) //this line should pass compilation
					A[i,j]-=A[i,k]*A[k,j];
				}
			}
			
			//Separate result
			finish foreach (point [i,j]:R) 
			if (i>j) 
				a_L[i,j]=A[i,j]; 
			else{ 
				if (i==j) a_L[i,j]=1; 
				a_U[i,j]=A[i,j];
			}
		}

	public boolean run() {
		//set up a test problem
		final int size=10;
		final region(:rank==2&&rect) R=[0:size-1,0:size-1];
		final double [:rank==2&&rect] A=new double [R] (point [i,j]){
			int res=i%2;
			if (i-1==j) res=i*(res==0?-1:1);
			return res;};
			
		final double [:rank==2&&rect]  L= new double [R];
		final double [:rank==2&&rect] U=new double [R];
		final int [:rank==1&&rect] p=new int [[0:size-1]];
		
		//compute LU factorization of A
		lu(A, L, U, p);
		
		
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
			err1+=L[i,0]-l[i];
			err1+=U[i,i]-u[i];
			err1+=U[0,i]-1;
			err2+=p[i]-permulation[i];
		}
		return (Math.abs(err1)<eps && err2==0);
	}

	
	public static void main(String[] args) {
		new LU_Dep().execute();
	}
}

