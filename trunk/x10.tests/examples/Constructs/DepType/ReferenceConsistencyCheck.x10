/**
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**


**/

public class ReferenceConsistencyCheck(R: region{rank==2, zeroBased, rect}, 
		D:dist{region==this.R},
		A: Array[double]{dist==this.D}) extends x10Test {
	
	public def this(R: region{rank==2&&zeroBased&&rect}, 
			D: dist{region==R}, 
			A: Array[double]{dist==D}): 
			ReferenceConsistencyCheck{self.R==R, self.D==D, self.A==A}
			 {
		property(R,D,A);
	}
	
	/** Update the submatrix A[k:m-1, k:n-1]*/
	def update(k:int):void {
		finish ateach (var (i,j) in D) A(i,j)++;
	}

	public def run()=true; 

	public static def main(Rail[String]) = {
//		set up a test problem
		val size=10;
		val R:region =[0..size-1,0..size-1];
		var D:dist{region==R} =dist.factory.cyclic(R);
		val A:Array[double]{dist==D} = 
			   Array.make(D, (x:point) => 
			      { val res=i%2;
			        if (i-1==j) res=i*(res==0?-1:1);
			        res to double
			      });
			
		new ReferenceConsistencyCheck(R, D, A).execute();
	}
}
