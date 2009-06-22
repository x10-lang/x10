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
 * Modified by T.W. 11/29/2007: comment out the import statements.
 */
public class LU extends x10Test {
	 const double epsilon2 = 0.000000000001;
	 public void lu(final double [.] a_A, final double [.] a_L, final double [.] a_U, final int [.] a_p){
			assert a_A.rank==2;
			final region R=a_A.region;
			final int n=R.rank(0).size();
			assert n==R.rank(1).size();
			
			final double [.] A=new double [R] (point [i,j]){return a_A[i,j];};
			//a_p=new int [[0:n-1]] (point [i]) {return i;};
			finish foreach (point [i]:a_p.region) a_p[i]=i;
			
			double r; int maxIdx;
			for(int K=0;K<n-1;K++){
				final int k=K;
				// Find index of largest element below diagonal in k-th column
				r=A[k,k]; maxIdx=k;
				for (point [i]: [k+1:n-1]){
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
						finish foreach (point [j]: [0:n-1]){
							double temp=A[k,j];
							A[k,j]=A[m,j]; 
							A[m,j]=temp;
						}
						a_p[m]=a_p[k];a_p[k]=m;
					}
					
					// Compute multipliers
					finish foreach (point [i]: [k+1:n-1]){
						A[i,k]/=A[k,k];
					}
					
					// Update the remainder of the matrix
					finish foreach (point [i,j]:[k+1:n-1,k+1:n-1]){
						A[i,j]-=A[i,k]*A[k,j];
					}
				}
			}
			
			//Separate result
			//a_L=new double [R];
			finish foreach (point [i,j]:R) if (i>j) a_L[i,j]=A[i,j]; else if (i==j) a_L[i,j]=1;
			//a_U=new double [R];
			finish foreach (point [i,j]:R) if (i<=j) a_U[i,j]=A[i,j];
		}

	public boolean run() {
		//set up a test problem
		final int size=10;
		final region R=[0:size-1,0:size-1];
		final double [.] A=new double [R] (point [i,j]){
			int res=i%2;
			if (i-1==j) res=i*(res==0?-1:1);
			return res;};
			
		double [.]  L= new double [R];
		double [.] U=new double [R];
		int [.] p=new int [[0:size-1]];
		//compute LU factorization of A
		lu(A, L, U, p);
		//verify result
		double temp1=0; int temp2=0;
		double [] UDiag={1, -2, 2, -4, 4, -6, 6, -8, 8, 0};
		int [] P={1,2,3,4,5,6,7,8,9,0};
		for (int i=0;i<size;i++){
			temp1+=UDiag[i]-U[i,i];
		}
		for (point [i]:p.region) temp2+=p[i]-P[i];
		//System.out.println("temp1= "+temp1+", temp2= "+temp2);
		return temp1 < epsilon2 && temp2==0;
	}

	
	public static void main(String[] args) {
		new LU().execute();
	}
}

