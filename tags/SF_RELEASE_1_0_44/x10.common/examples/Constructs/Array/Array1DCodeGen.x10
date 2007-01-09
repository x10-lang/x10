/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

public class Array1DCodeGen extends x10Test {

	final double matgen(final double[.] a,  final double[.] b)
	{
		int n=a.region.rank(0).high();
		int init = 1325;
		double norma = 0.0;
		/* Next two for() statements switched.  Solver wants
		   matrix in column order. --dmd 3/3/97
		 */
		for (point [i,j,k] : a) {
			init = 3125*init % 65536;
			double value = (init - 32768.0)/16384.0;
			finish write(a, i, j, k, value);
			norma = value > norma ? value : norma;
		}
		finish ateach (point [i,j,k] : b) b[i,j,k] = 0.0;
		finish ateach (point [i,j,k] : a.distribution | [0:n-1,0:n-1,0:n-1]) 
		            plusWrite(b, 0, j, k, a[i,j,k]);
		return norma;
	}
	final void write(final double[.] a, final int i, final int j, final int k, final double val) {
		async (a.distribution[i,j,k]) atomic a[i,j,k] = val;
	}
	final void plusWrite(final double[.] a, final int i, final int j, final int k, final double val) {
		async (a.distribution[i,j,k]) atomic a[i,j,k] += val;
	}
	
	
	public boolean run() {
		region(:rank==3&&zeroBased&&rect) R =  [0:9,0:9,0:9];
		double[.] a =  new double[R->here];
		double[.] b  =  new double[R->here];
		System.out.println("runtime type of 3dZeroBasedRect array is " + a.getClass());
		double result = matgen(a,b);
		region S =  [0:9,0:9,0:9];
		a =  new double[S->here];
		 b  =  new double[S->here];
		 double result1 = matgen(a,b);
		 System.out.println("runtime type of unknown array is " + a.getClass());
		System.out.println("results are " + result + " " + result1);
		double diff = result-result1;
		return diff < 0 ? diff > -0.001 : diff < 0.001;
	}

	public static void main(String[] args) {
		new Array1DCodeGen().execute();
	}
}

