/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Test for arrays, regions and dists.
 * Based on original arraycopy1 by vj.
 *
 * @author kemal 1/2005
 */
public class ArrayCopy1 extends x10Test {

	/**
	 * Does not throw an error iff A[i] == B[i] for all points i.
	 * A and B can have differing dists
	 * whose regions are equal.
	 */
	public def arrayEqual(val A: Array[int], val B: Array[int]): void = {
		val D = A.dist;
		val E = B.dist;
		// Spawn an activity for each index to
		// fetch the B[i] value
		// Then compare it to the A[i] value
		finish
			ateach (val p in D){
			 val fp:Int = (future (E(p)) B(p)).force();
			
			         if (A(p) != fp) {
			           throw new Error("****Error: A(" + p + ")= " + A(p) 
                                   + ", B(" + p + ")=" + B(p) + " fp= " + fp);
			         }  
			         chk(A(p)==fp); 
			         chk(A(p)==(future (E(p)) B(p)).force());
			                              
           }
	}

	/**
	 * Set A[i] = B[i] for all points i.
	 * A and B can have different dists whose
	 * regions are equal.
	 * Throws an error iff some assertion failed.
	 */
	public def arrayCopy(val A: Array[int], val B: Array[int]): void = {
		val D = A.dist;
		val E = B.dist;
		// Spawn an activity for each index to
		// fetch and copy the value
		finish
			ateach (val p in D) {
				chk(D(p) == here);
				async(E(p)) chk(E(p) == here);
				A(p) = (future(E(p)) B(p)).force();
			}
	}

	const N = 3;

	/**
	 * For all combinations of dists of arrays B and A,
	 * do an array copy from B to A, and verify.
	 */
	public def run(): boolean = {
	try {
		val R: region = [0..N-1, 0..N-1, 0..N-1, 0..N-1];
		val TestDists: region = [0..dist2.N_DIST_TYPES-1, 0..dist2.N_DIST_TYPES-1];

		for (val distP(dX,dY): point in TestDists) {
			val D = dist2.getDist(dX, R);
			val E = dist2.getDist(dY, R);
			chk(D.region.equals(E.region) && D.region.equals(R));
			val A = Array.make[int](D,(point)=>0);
			val B = Array.make[int](E,
			 (p(i,j,k,l): point): int => { 
			     var x: int = ((i*N+j)*N+k)*N+l; 
			     return x*x+1; 
			     }
			     );
			arrayCopy(A, B);
			arrayEqual(A, B);
		}
		return true;
		} catch (e:Error) {
		   e.printStackTrace();
		   return false;
		}
	}

	public static def main(var args: Rail[String]): void = {
		new ArrayCopy1().execute();
	}

	/**
	 * utility for creating a dist from a
	 * a dist type int value and a region
	 */
	static class dist2 {
		const BLOCK: int = 0;
		const CYCLIC: int = 1;
		const BLOCKCYCLIC: int = 2;
		const CONSTANT: int = 3;
		const RANDOM: int = 4;
		const ARBITRARY: int = 5;
		const N_DIST_TYPES: int = 6;

		/**
		 * Return a dist with region r, of type disttype
		 */
		public static def getDist(distType: Int, r: Region): Dist(r) = {
			switch(distType) {
				case BLOCK: return dist.makeBlock(r);
				case CYCLIC: return dist.makeCyclic(r);
				case BLOCKCYCLIC: return dist.makeBlockCyclic(r, 3);
				case CONSTANT: return r->here;
				case RANDOM: return dist.makeRandom(r);
				case ARBITRARY:return dist.makeArbitrary(r);
				default: throw new Error();
			}
		}
	}
}
