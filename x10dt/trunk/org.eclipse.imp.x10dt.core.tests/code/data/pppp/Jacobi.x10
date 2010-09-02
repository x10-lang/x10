/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Jacobi iteration.
 *
 * At each step of the iteration, replace the value of a cell with
 * the average of its adjacent cells in the (i,j) dimensions.
 * Compute the error at each iteration as the sum of the changes
 * in value across the whole array. Continue the iteration until
 * the error falls below a given bound.
 *
 * This program is safe.
 *
 * @author vj
 * @author cvp
 * @author kemal
 */
public class Jacobi {
	const N = 6;
	const epsilon = 0.002D;
	const epsilon2 = 0.000000001D;
	val R: Region(2) = [0..(N+1), 0..(N+1)];
	val R_inner: Region(2) = [1..N, 1..N];
	val D:Dist(2) = Dist.makeBlock(R, 0);
	val D_inner = D.restriction(R_inner);
	val D_Boundary  = D.difference(D_inner.region);
	const EXPECTED_ITERS: int = 97;
	const EXPECTED_ERR: double = 0.0018673382039402497;
	public  incomplete static def read[T](x:T):Effects;
	public  incomplete static def write[T](x:T):Effects;
	public  incomplete static def touch[T](x:T):Effects;

	public def run() { 
		@fun {
			val initializer: Box[(Point)=>double] 
			    = ((i,j):Point) => (i==0||i==N+1||j==0||j==N+1)? (N-1)/2 
			    		: (i-1)*N+(j-1) as double;
			    val a = Array.make[double](D, initializer), 
			    b = Array.make[double](D, (Point)=>0.0D);
			    val error = Array.make[double](Dist.makeUnique(D.places()), (Point)=>0.0D);
			    var iters:int=0;
			    @fun(touch(a).and(touch(b))) 
			    while (true) {
			    	if (step(a, b, error) < epsilon) 
			    		break;
			    	iters++;
			    	if (step(b, a, error) < epsilon) 
			    		break;
			    	iters++;
			    }
			    Console.OUT.println("Error= " + error + " iters=" + iters);
			    return iters == EXPECTED_ITERS;
		}}

	public def step(red: Array[double](2), black: Array[double](red.dist), 
			error: Array[double](1)) {
		@fun(read(red).and(touch(black)).and(touch(error))) {
			@fun(read(red).and(write(black)).and(write(error)))
			finish {
				@parfun(read(red).and(write(black)).and(write(error)))
				for (p in red.dist.places())
					@parfun(read(red).and(write(black|p)).and(write(error|p)))
					async(p) {
					val local = D_inner | p;
					var err: double = 0.0D;
					for ((i,j) in local) {
						black(i,j) = (red(i+1, j)+red(i-1, j)
								+red(i, j-1)+red(i, j+1))/4.0;
						err = Math.max(Math.abs(black(i,j)-red(i,j)), err);
					}
					error(p.id)=err;
				}
			}
			return error.reduce(double.+, double.MAX_VALUE);
		}}
	public static def main(Rail[String]) {
		new Jacobi().run();
	}
}