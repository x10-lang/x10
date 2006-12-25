/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**


**/

public class ReferenceConsistencyCheck(region(:rank==2&& zeroBased&&rect) R, 
		dist(:region==this.R) D,
		double[:distribution==this.D] A) extends x10Test {
	
	public ReferenceConsistencyCheck(:self.R==R&&self.D==D&&self.A==A)
	(final region(:rank==2&&zeroBased&&rect) R, 
			final dist(:region==R) D, 
			final double[:distribution==D] A) {
		property(R,D,A);
	}
	
	/** Update the submatrix A[k:m-1, k:n-1]*/
	void update(final int k){
		finish ateach (point [i,j]:D) A[i,j]++;
	}

	public boolean run() {
		return true;
	}

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
			
		new ReferenceConsistencyCheck(R, D, A).execute();
	}
}

