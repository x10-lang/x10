/**************************************************************************
*                                                                         *
*             Java Grande Forum Benchmark Suite - Version 2.0             *
*                                                                         *
*                            produced by                                  *
*                                                                         *
*                  Java Grande Benchmarking Project                       *
*                                                                         *
*                                at                                       *
*                                                                         *
*                Edinburgh Parallel Computing Centre                      *
*                                                                         *
*                email: epcc-javagrande@epcc.ed.ac.uk                     *
*                                                                         *
*            See below for the previous history of this code              *
*                                                                         *
*      This version copyright (c) The University of Edinburgh, 1999.      *
*                         All rights reserved.                            *
*                                                                         *
**************************************************************************/

/*
 Modified 3/3/97 by David M. Doolin (dmd) doolin@cs.utk.edu
 Fixed error in matgen() method. Added some comments.

 Modified 1/22/97 by Paul McMahan mcmahan@cs.utk.edu
 Added more MacOS options to form.

 Optimized by Jonathan Hardwick (jch@cs.cmu.edu), 3/28/96
 Compare to Linkpack.java.
 Optimizations performed:
 - added "final" modifier to performance-critical methods.
 - changed lines of the form "a[i] = a[i] + x" to "a[i] += x".
 - minimized array references using common subexpression elimination.
 - eliminated unused variables.
 - undid an unrolled loop.
 - added temporary 1D arrays to hold frequently-used columns of 2D arrays.
 - wrote my own abs() method
 See http://www.cs.cmu.edu/~jch/java/linpack.html for more details.

 Ported to Java by Reed Wade  (wade@cs.utk.edu) 2/96
 built using JDK 1.0 on solaris
 using "javac -O Linpack.java"

 Translated to C by Bonnie Toy 5/88
 (modified on 2/25/94  to fix a problem with daxpy  for
 unequal increments or equal increments not equal to 1.
 Jack Dongarra)
 */
package lufact;

import jgfutil.*;

/**
 * Ported to X10 March 17 2005
 * @author ahk
 * @author cmd
 */
public class Linpack {

	double[.] a;
	double[.] b;
	double[.] x;
	double ops, total, norma, normx;
	double resid, time;
	double kf;
	int n, i, ntimes, info, lda, ldaa, kflops;
	int ipvt[];

	final double read(final double[.] a, final int i, final int j) {
		return future (a.distribution[i,j]) { a[i,j] }.force();
	}
	final void write(final double[.] a, final int i, final int j, final double val) {
		async (a.distribution[i,j]) atomic a[i,j] = val;
	}
	final void plusWrite(final double[.] a, final int i, final int j, final double val) {
		async (a.distribution[i,j]) atomic a[i,j] += val;
	}
	final void plusWrite(final double[.] a, final int i, final double val) {
		async (a.distribution[i]) atomic a[i] += val;
	}
	final void timesWrite(final double[.] a, final int i, final int j, final double val) {
		async (a.distribution[i,j]) atomic a[i,j] *= val;
	}
	final void timesWrite(final double[.] a, final int i, final double val) {
		async (a.distribution[i]) atomic a[i] *= val;
	}

	final double abs(double d) {
		return (d >= 0) ? d : -d;
	}

	final double matgen(final double[.] a, final int lda, final int n, final double[.] b)
	{
		int init = 1325;
		double norma = 0.0;
		/* Next two for() statements switched.  Solver wants
		   matrix in column order. --dmd 3/3/97
		 */
		for (point [i,j] : a) {
			init = 3125*init % 65536;
			double value = (init - 32768.0)/16384.0;
			finish write(a, i, j, value);
			norma = value > norma ? value : norma;
		}
		finish ateach (point [i,j] : b) b[i,j] = 0.0;
		finish ateach (point [i,j] : a.distribution | [0:n-1,0:n-1]) plusWrite(b, 0, j, a[i,j]);
		return norma;
	}

	int infodgefa = 0;
	/**
	 dgefa factors a double precision matrix by gaussian elimination.

	 dgefa is usually called by dgeco, but it can be called
	 directly with a saving in time if  rcond  is not needed.
	 (time for dgeco) = (1 + 9/n)*(time for dgefa) .

	 on entry

	 a       double precision[n,lda]
	 the matrix to be factored.

	 lda     integer
	 the leading dimension of the array  a .

	 n       integer
	 the order of the matrix  a .

	 on return

	 a       an upper triangular matrix and the multipliers
	 which were used to obtain it.
	 the factorization can be written  a = l*u  where
	 l  is a product of permutation and unit lower
	 triangular matrices and  u  is upper triangular.

	 ipvt    integer[n]
	 an integer vector of pivot indices.

	 info    integer
	 = 0  normal value.
	 = k  if  u[k,k] .eq. 0.0 .  this is not an error
	 condition for this subroutine, but it does
	 indicate that dgesl or dgedi will divide by zero
	 if called.  use  rcond  in dgeco for a reliable
	 indication of singularity.

	 linpack. this version dated 08/14/78.
	 cleve moler, university of new mexico, argonne national lab.

	 functions

	 blas daxpy, dscal, idamax
	 */
	final int dgefa(final double[.] a, final int lda, final int n, final int ipvt[])
	{
		// gaussian elimination with partial pivoting
		infodgefa = 0;
		final int nm1 = n - 1;

		if (nm1 >=  0) {
			// array is distributed along each column,

			for (point [k] : [0:nm1-1]) {
				finish async(a.distribution[k,1]) { // do this on row k
					final int kp1 = k+1;
					// would be nice to do this via an X10 reduction on a sub-array
					final int l = idamax(n - k, a, k, k, 1) + k;
					ipvt[k] = l; // must be distributed same way as a
					// zero pivot implies this column already triangularized
					if (a[k, l] != 0) {
						if (l != k) {
							//    interchange if necessary
							if (l != k) {
								double t = a[k,l];
								a[k,l] = a[k,k];
								a[k,k] = t;
							}
						}
						// compute multipliers
						final double tx = -1.0/a[k,k];

						dscal(n-(kp1), tx, a, k, kp1, 1);

						// row elimination with column indexing
						// daxpy on all columns to the right in parallel
						finish {
							for (point [j] : [kp1:n-1]) {
								async(a.distribution[j,l]) {
									double t = a[j,l];
									if (l != k) {
										a[j,l] = a[j,k];
										a[j,k] = t;
									}
									daxpy(n-(kp1), t, a, k, kp1, 1, a, j, kp1, 1);
								}
							}
						}
					} else {
						infodgefa = k;
					}
				}
			}
		}
		ipvt[n-1] = n-1;
		double val = read(a, n-1, n-1);
		if (val == 0) info = n-1;

		return infodgefa;
	}

	/**
	 dgesl solves the double precision system
	 a * x = b  or  trans(a) * x = b
	 using the factors computed by dgeco or dgefa.

	 on entry

	 a       double precision[n,lda]
	 the output from dgeco or dgefa.

	 lda     integer
	 the leading dimension of the array  a .

	 n       integer
	 the order of the matrix  a .

	 ipvt    integer[n]
	 the pivot vector from dgeco or dgefa.

	 b       double precision[n]
	 the right hand side vector.

	 job     integer
	 = 0         to solve  a*x = b ,
	 = nonzero   to solve  trans(a)*x = b  where
	 trans(a)  is the transpose.

	 on return

	 b       the solution vector  x .

	 error condition

	 a division by zero will occur if the input factor contains a
	 zero on the diagonal.  technically this indicates singularity
	 but it is often caused by improper arguments or improper
	 setting of lda .  it will not occur if the subroutines are
	 called correctly and if dgeco has set rcond .gt. 0.0
	 or dgefa has set info .eq. 0 .

	 to compute  inverse(a) * c  where  c  is a matrix
	 with  p  columns
	 dgeco(a, lda, n, ipvt, rcond, z)
	 if (!rcond is too small) {
	 for (j = 0, j < p, j++)
	 dgesl(a, lda, n, ipvt, c[j,0], 0);
	 }

	 linpack. this version dated 08/14/78 .
	 cleve moler, university of new mexico, argonne national lab.

	 functions

	 blas daxpy, ddot
	 */
	final void dgesl(final double[.] a, final int lda, final int n, final int ipvt[], final double[.] b, final int job)
	{
		final int nm1 = n - 1;
		if (job == 0) {
			// job = 0 , solve  a * x = b.  first solve  l*y = b
			if (nm1 >= 1) {
				for (point [k] : [0:nm1-1]) {
					final int l = ipvt[k];
					double t = b[0,l];
					if (l != k) {
						b[0,l] = b[0,k];
						b[0,k] = t;
					}
					final int kp1 = k + 1;
					daxpy(n-(kp1), t, a, k, kp1, 1, b, 0, kp1, 1); // FIXME: subarrays
				}
			}

			// now solve  u*x = y
			for (point [kb] : [0:n-1]) {
				final int k = n - (kb + 1);
				b[0,k] /= read(a, k, k);
				double t = -b[0,k];
				daxpy(k, t, a, k, 0, 1, b, 0, 0, 1); // FIXME: subarrays
			}
		}
		else {
			// job = nonzero, solve  trans(a) * x = b.  first solve  trans(u)*y = b
			for (point [k] : [0:n-1]) {
				double t = ddot(k, a, k, 0, 1, b, 0, 0, 1); // FIXME: subarrays not yet supported
				b[0,k] = (b[0,k] - t) / a[k,k];
			}

			// now solve trans(l)*x = y
			if (nm1 >= 1) {
				for (point [kb] : [1:nm1-1]) {
					final int k = n - (kb+1);
					final int kp1 = k + 1;
					b[0,k] += ddot(n-(kp1), a, k, kp1, 1, b, 0, kp1, 1); //FIXME: subarrays not yet supported
					final int l = ipvt[k];
					if (l != k) {
						double t = b[0,l];
						b[0,l] = b[0,k];
						b[0,k] = t;
					}
				}
			}
		}
	}

	/**
	 constant times a vector plus a vector.
	 jack dongarra, linpack, 3/11/78.
	 */
	final void daxpy(int n, double da, final double[.] dx, final int dxCol, final int dx_off, int incx,
			double[.] dy, int dyCol, int dy_off, int incy)
	{
		if ((n > 0) && (da != 0)) {
			if (incx != 1 || incy != 1) {
				// code for unequal increments or equal increments not equal to 1
				int ix = 0;
				int iy = 0;
				if (incx < 0) ix = (-n+1)*incx;
				if (incy < 0) iy = (-n+1)*incy;
				for (point [i] : [0:n-1]) {
					// daxpy is called at dy.distribution[dyCol,xx]
					dy[dyCol,iy+dy_off] += da * read(dx, dxCol, ix+dx_off);
					ix += incx;
					iy += incy;
				}
				return;
			}
			// code for both increments equal to 1
			for (point [i] : [0:n-1])
				dy[dyCol,i+dy_off] += da*read(dx, dxCol, i+dx_off);
		}
	}

	/**
	 forms the dot product of two vectors.
	 jack dongarra, linpack, 3/11/78.
	 */
	final double ddot(final int n, final double[.] dx, final int dxCol, final int dx_off, final int incx,
			final double[.] dy, final int dyCol, final int dy_off, final int incy)
	{
		double dtemp = 0;
		if (n > 0) {
			if (incx != 1 || incy != 1) {
				// code for unequal increments or equal increments not equal to 1
				int ix = 0;
				int iy = 0;
				if (incx < 0) ix = (-n+1)*incx;
				if (incy < 0) iy = (-n+1)*incy;
				for (point [i]: [0:n-1]) {
					dtemp += dx[dxCol,ix+dx_off]*dy[dyCol,iy+dy_off];
					ix += incx;
					iy += incy;
				}
			} else {
				// code for both increments equal to 1
				for (point [i]: [0:n-1])
					dtemp += dx[dxCol,i+dx_off]*dy[dyCol,i+dy_off];
			}
		}
		return dtemp;
	}

	/**
	 scales a vector by a constant.
	 jack dongarra, linpack, 3/11/78.
	 */
	final void dscal(int n, double da, double[.] dx, int currentCol, int dx_off, int incx)
	{
		if (n > 0) {
			if (incx != 1) {
				// code for increment not equal to 1
				final int nincx = n*incx;
				for (int i = 0; i < nincx; i += incx)
					dx[currentCol,i+dx_off] *= da;
			} else {
				// code for increment equal to 1
				for (point [i] : [0:n-1])
					dx[currentCol,i+dx_off] *= da;
			}
		}
	}

	/**
	 finds the index of element having max. absolute value.
	 jack dongarra, linpack, 3/11/78.

	 In X10: dx is now a 2 dimensional array (currently don't support
	 sub-sections) so must explicitly address only first element
	 */
	final int idamax(final int n, final double[.] dx, final int dxk, final int dx_off, final int incx)
	{
		if (n < 1) return -1;
		if (n == 1) return 0;
		if (incx != 1) {
			// code for increment not equal to 1
			int itemp = 0;
			double dmax = abs(read(dx, dxk, 0+dx_off));
			int ix = 1 + incx;
			for (point [i] : [1:n-1]) {
				double dtemp = abs(read(dx, dxk, ix+dx_off));
				if (dtemp > dmax)  {
					itemp = i;
					dmax = dtemp;
				}
				ix += incx;
			}
			return itemp;
		}
		// code for increment equal to 1
		int itemp = 0;
		double dmax = abs(read(dx, dxk, 0+dx_off));
		for (point [i] : [1:n-1]) {
			double dtemp = abs(read(dx, dxk, i+dx_off));
			if (dtemp > dmax) {
				itemp = i;
				dmax = dtemp;
			}
		}
		return itemp;
	}

	/**
	 estimate unit roundoff in quantities of size x.

	 this program should function properly on all systems
	 satisfying the following two assumptions,
	 1.  the base used in representing dfloating point
	 numbers is not a power of three.
	 2.  the quantity  a  in statement 10 is represented to
	 the accuracy used in dfloating point variables
	 that are stored in memory.
	 the statement number 10 and the go to 10 are intended to
	 force optimizing compilers to generate code satisfying
	 assumption 2.
	 under these assumptions, it should be true that,
	 a  is not exactly equal to four-thirds,
	 b  has a zero for its last bit or digit,
	 c  is not exactly equal to one,
	 eps  measures the separation of 1.0 from
	 the next larger dfloating point number.
	 the developers of eispack would appreciate being informed
	 about any systems where these assumptions do not hold.

	 *****************************************************************
	 this routine is one of the auxiliary routines used by eispack iii
	 to avoid machine dependencies.
	 *****************************************************************

	 this version dated 4/6/83.
	 */
	final double epslon (final double x)
	{
		final double a = 4.0e0/3.0e0;
		double eps = 0;
		while (eps == 0) {
			double b = a - 1.0;
			double c = b + b + b;
			eps = abs(c-1.0);
		}
		return (eps*abs(x));
	}

	/**
	 Function purpose:
	 multiply matrix m times vector x and add the result to vector y.

	 Function parameters:

	 n1 integer, number of elements in vector y, and number of rows in
	 matrix m

	 y double [n1], vector of length n1 to which is added
	 the product m*x

	 n2 integer, number of elements in vector x, and number of columns
	 in matrix m

	 ldm integer, leading dimension of array m

	 x double [n2], vector of length n2

	 m double [ldm,n2], matrix of n1 rows and n2 columns
	 */
	final void dmxpy (final int n1, final double[.] y, final int n2, final int ldm, final double[.] x, final double[.] m)
	{
		// cleanup odd vector
		for (point [j,i] : [0:n2-1,0:n1-1])
			y[0,i] += x[0,j]*read(m, j, i);
	}
}

