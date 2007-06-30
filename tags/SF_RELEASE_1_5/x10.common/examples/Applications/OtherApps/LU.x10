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
 */
public class LU(dist(:(rank==2)&&rect) D, 
                double[:distribution==this.D] A, 
                double[:distribution==this.D] L,
                double[:distribution==this.D] U,
                int[:rank==1&&rect] P)  extends x10Test {

    const double epsilon2 = 0.000000000001;
    final int n;
    public  LU(final dist D, 
               final double[:distribution==D] A, 
               final double[:distribution==D] L,
               final double[:distribution==D] U,
	       final int[:(rank==1)&&rect] P) {
	property(D,A,L,U,P);
        n=D.region.rank(0).size();
    }
    region(:rank==1) afterK(int k) { return [k:n-1];}
    region(:rank==2) afterKK(int k) { return [k:n-1,k:n-1];}

    public void lu() {
	final double [.] A=new double [afterKK(0)] (point [i,j]){return LU.this.A[i,j];};
	finish foreach (point [i]:P) P[i]=i;
            
	for(point [k] : [0:n-2]){
	    // Find index of largest element below diagonal in k-th column
	    double max=A[k,k]; 
            int maxIdx=k;
	    for (point [i]: afterK(k+1))
		if (Math.abs(A[i,k])>Math.abs(max)){
		    max=A[i,k]; maxIdx=i;
		}

	    final int m=maxIdx;
	    // Skip elimination if column is zero
	    if (max!=0) {
                    // Swap pivot row
                    if (m!=k){
                        finish foreach (point [j]: afterK(0)){
                            double temp=A[k,j];
                            A[k,j]=A[m,j]; 
                            A[m,j]=temp;
                        }
                        P[m]=P[k];
                        P[k]=m;
                    }
                    
                    // Compute multipliers
                    finish foreach (point [i]: afterK(k+1)) A[i,k]/=A[k,k];
                    
                    // Update the remainder of the matrix
                    finish foreach (point [i,j]: afterKK(k+1))
                        A[i,j]-=A[i,k]*A[k,j];
                }
            }
            
            //Separate result
  	    finish foreach (point [i,j]: afterKK(0)) {
               if (i>j) L[i,j]=A[i,j]; 
               else if (i==j) {U[i,j]=A[i,j]; L[i,j]=1;}
                    else U[i,j]=A[i,j];
	    }
     }

     public static LU initialize() {
         final int size=10;
	 final region(:rank==1 && rect) Size = [0:size-1];
	 final region(:rank==2) R=[Size,Size];
	 final dist(:rank==2&&rect) D = (dist(:rank==2&&rect)) (R -> here);
	 final double [:distribution==D] 
	     A =  new double [D] (point [i,j]){
		 int res=i%2;
		 if (i-1==j) res=i*(res==0?-1:1);
		 return res;
	     };
                
	 double [:distribution==D]  L=  new double [D], U=  new double [D];
	 int [:rank==1&&rect] p= (int [:rank==1&&rect]) new int [Size];
            
	 return new LU(D,A,L,U,p);
     }
     public boolean verify() {
        double temp1=0; int temp2=0;
        double [] UDiag={1, -2, 2, -4, 4, -6, 6, -8, 8, 0};
        int [] PP={1,2,3,4,5,6,7,8,9,0};
        for (point [i] : afterK(0)) {
        	temp1+=UDiag[i]-U[i,i];
        	temp2+=P[i]-PP[i];
        }
        return temp1 < epsilon2 && temp2==0;
     }
     public boolean run() {
	 lu();
         return verify();
     }
    
    public static void main(String[] args) {
        LU lu = initialize();
        lu.execute();
    }
}

