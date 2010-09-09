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


/**
 * Ported to X10 March 17 2005
 * @author ahk
 * @author cmd
 */
class Linpack {

	var a: Array[double](2);
	var b: Array[double](2);
	var x: Array[double](2);
	var ops: double;
	var total: double;
	var norma: double;
	var normx: double;
	var resid: double;
	var time: double;
	var kf: double;
	var n: int;
	var i: int;
	var ntimes: int;
	var info: int;
	var lda: int;
	var ldaa: int;
	var kflops: int;
	var ipvt: Array[int](1);

	final def read(val a: Array[double](2), val i: int, val j: int): double = {
		return a(i, j);
	}
	final def write(val a: Array[double](2), val i: int, val j: int, val val: double): void = {
		 atomic a(i, j) = val;
	}
	final def plusWrite(val a: Array[double](2), val i: int, val j: int, val val: double): void = {
		atomic a(i, j) += val;
	}
	final def plusWrite(val a: Array[double](1), val i: int, val val: double): void = {
		 atomic a(i) += val;
	}
	final def timesWrite(val a: Array[double](2), val i: int, val j: int, val val: double): void = {
		 atomic a(i, j) *= val;
	}
	final def timesWrite(val a: Array[double](1), val i: int, val val: double): void = {
		 atomic a(i) *= val;
	}

	final def abs(var d: double): double = {
		return (d >= 0) ? d : -d;
	}

	final def matgen(val a: Array[double](2), val lda: int, val n: int, val b: Array[double](2)): double = {
		var init: int = 1325;
		var norma: double = 0.0;
		/* Next two for() statements switched.  Solver wants
		   matrix in column order. --dmd 3/3/97
		 */
		for (val (i,j): Point in a) {
			init = 3125*init % 65536;
			var value: double = (init - 32768.0)/16384.0;
			finish write(a, i, j, value);
			norma = value > norma ? value : norma;
		}
		finish foreach (val (i,j): Point in b) b(i, j) = 0.0;
		finish foreach ((i,j): Point in [0..n-1, 0..n-1]) plusWrite(b, 0, j, a(i, j));
		return norma;
	}

	var infodgefa: int = 0;
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
	final def dgefa(val a: Array[double](2), val lda: int, val n: int, val ipvt: Array[int](1)): int = {
		// gaussian elimination with partial pivoting
		infodgefa = 0;
		val nm1: int = n - 1;

		if (nm1 >=  0) {
			// array is distributed along each column,

			for (val (k): Point in [0..nm1-1]) {
			   { // do this on row k
					val kp1: int = k+1;
					// would be nice to do this via an X10 reduction on a sub-array
					val l: int = this.idamax(n - k, a, k, k, 1) + k;
					ipvt(k) = l; // must be distributed same way as a
					// zero pivot implies this column already triangularized
					if (a(k, l) != 0) {
						if (l != k) {
							//    interchange if necessary
							if (l != k) {
								var t: double = a(k, l);
								a(k, l) = a(k, k);
								a(k, k) = t;
							}
						}
						// compute multipliers
						val tx: double = -1.0/a(k, k);

						dscal(n-(kp1), tx, a, k, kp1, 1);

						// row elimination with column indexing
						// daxpy on all columns to the right in parallel
val c = new Clock();
						{
							for (val (j): Point in [kp1..n-1]) {
								async clocked (c) {
									var t: double = a(j, l);
									if (l != k) {
										a(j, l) = a(j, k);
										a(j, k) = t;
									}
									daxpy(n-(kp1), t, a, k, kp1, 1, a, j, kp1, 1);
								}
							}
						}
next;
					} else {
						infodgefa = k;
					}
				}
			}
		}
		ipvt(n-1) = n-1;
		var val: double = read(a, n-1, n-1);
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
	final def dgesl(val a: Array[double](2), val lda: int, val n: int, val ipvt: Array[int](1), val b: Array[double](2), val job: int): void = {
		val nm1: int = n - 1;
		if (job == 0) {
			// job = 0 , solve  a * x = b.  first solve  l*y = b
			if (nm1 >= 1) {
				for (val (k): Point in [0..nm1-1]) {
					val l: int = ipvt(k);
					var t: double = b(0, l);
					if (l != k) {
						b(0, l) = b(0, k);
						b(0, k) = t;
					}
					val kp1: int = k + 1;
					daxpy(n-(kp1), t, a, k, kp1, 1, b, 0, kp1, 1); // FIXME: subarrays
				}
			}

			// now solve  u*x = y
			for (val (kb): Point in [0..n-1]) {
				val k: int = n - (kb + 1);
				b(0, k) /= read(a, k, k);
				var t: double = -b(0, k);
				daxpy(k, t, a, k, 0, 1, b, 0, 0, 1); // FIXME: subarrays
			}
		}
		else {
			// job = nonzero, solve  trans(a) * x = b.  first solve  trans(u)*y = b
			for (val (k): Point in [0..n-1]) {
				var t: double = ddot(k, a, k, 0, 1, b, 0, 0, 1); // FIXME: subarrays not yet supported
				b(0, k) = (b(0, k) - t) / a(k, k);
			}

			// now solve trans(l)*x = y
			if (nm1 >= 1) {
				for (val (kb): Point in [1..nm1-1]) {
					val k: int = n - (kb+1);
					val kp1: int = k + 1;
					b(0, k) += ddot(n-(kp1), a, k, kp1, 1, b, 0, kp1, 1); //FIXME: subarrays not yet supported
					val l: int = ipvt(k);
					if (l != k) {
						var t: double = b(0, l);
						b(0, l) = b(0, k);
						b(0, k) = t;
					}
				}
			}
		}
	}

	/**
	 constant times a vector plus a vector.
	 jack dongarra, linpack, 3/11/78.
	 */
	final def daxpy(var n: int, var da: double, val dx: Array[double](2), val dxCol: int, val dx_off: int, var incx: int, var dy: Array[double](2), var dyCol: int, var dy_off: int, var incy: int): void = {
		if ((n > 0) && (da != 0)) {
			if (incx != 1 || incy != 1) {
				// code for unequal increments or equal increments not equal to 1
				var ix: int = 0;
				var iy: int = 0;
				if (incx < 0) ix = (-n+1)*incx;
				if (incy < 0) iy = (-n+1)*incy;
				for (val (i): Point in [0..n-1]) {
					// daxpy is called at dy.dist[dyCol,xx]
					dy(dyCol, iy+dy_off) += da * read(dx, dxCol, ix+dx_off);
					ix += incx;
					iy += incy;
				}
				return;
			}
			// code for both increments equal to 1
			for (val (i): Point in [0..n-1]) dy(dyCol, i+dy_off) += da*read(dx, dxCol, i+dx_off);
		}
	}

	/**
	 forms the dot product of two vectors.
	 jack dongarra, linpack, 3/11/78.
	 */
	final def ddot(val n: int, val dx: Array[double](2), val dxCol: int, val dx_off: int, val incx: int, val dy: Array[double](2), val dyCol: int, val dy_off: int, val incy: int): double = {
		var dtemp: double = 0;
		if (n > 0) {
			if (incx != 1 || incy != 1) {
				// code for unequal increments or equal increments not equal to 1
				var ix: int = 0;
				var iy: int = 0;
				if (incx < 0) ix = (-n+1)*incx;
				if (incy < 0) iy = (-n+1)*incy;
				for (val (i): Point in [0..n-1]) {
					dtemp += dx(dxCol, ix+dx_off)*dy(dyCol, iy+dy_off);
					ix += incx;
					iy += incy;
				}
			} else {
				// code for both increments equal to 1
				for (val (i): Point in [0..n-1]) dtemp += dx(dxCol, i+dx_off)*dy(dyCol, i+dy_off);
			}
		}
		return dtemp;
	}

	/**
	 scales a vector by a constant.
	 jack dongarra, linpack, 3/11/78.
	 */
	final def dscal(var n: int, var da: double, var dx: Array[double](2), var currentCol: int, var dx_off: int, var incx: int): void = {
		if (n > 0) {
			if (incx != 1) {
				// code for increment not equal to 1
				val nincx: int = n*incx;
				for (var i: int = 0; i < nincx; i += incx)
					dx(currentCol, i+dx_off) *= da;
			} else {
				// code for increment equal to 1
				for (val (i): Point in [0..n-1]) dx(currentCol, i+dx_off) *= da;
			}
		}
	}

	/**
	 finds the index of element having max. absolute value.
	 jack dongarra, linpack, 3/11/78.

	 In X10: dx is now a 2 dimensional array (currently don't support
	 sub-sections) so must explicitly address only first element
	 */
	final def idamax(val n: int, val dx: Array[double](2), val dxk: int, val dx_off: int, val incx: int): int = {
		if (n < 1) return -1;
		if (n == 1) return 0;
		if (incx != 1) {
			// code for increment not equal to 1
			var itemp: int = 0;
			var dmax: double = abs(read(dx, dxk, 0+dx_off));
			var ix: int = 1 + incx;
			for (val (i): Point in [1..n-1]) {
				var dtemp: double = abs(read(dx, dxk, ix+dx_off));
				if (dtemp > dmax)  {
					itemp = i;
					dmax = dtemp;
				}
				ix += incx;
			}
			return itemp;
		}
		// code for increment equal to 1
		var itemp: int = 0;
		var dmax: double = abs(read(dx, dxk, 0+dx_off));
		for (val (i): Point in [1..n-1]) {
			var dtemp: double = abs(read(dx, dxk, i+dx_off));
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
	 1.  the base used in representing dfloating Point
	 numbers is not a power of three.
	 2.  the quantity  a  in statement 10 is represented to
	 the accuracy used in dfloating Point variables
	 that are stored in memory.
	 the statement number 10 and the go to 10 are intended to
	 force optimizing compilers to generate code satisfying
	 assumption 2.
	 under these assumptions, it should be true that,
	 a  is not exactly equal to four-thirds,
	 b  has a zero for its last bit or digit,
	 c  is not exactly equal to one,
	 eps  measures the separation of 1.0 from
	 the next larger dfloating Point number.
	 the developers of eispack would appreciate being informed
	 about any systems where these assumptions do not hold.

	 *****************************************************************
	 this routine is one of the auxiliary routines used by eispack iii
	 to avoid machine dependencies.
	 *****************************************************************

	 this version dated 4/6/83.
	 */
	final def epslon(val x: double): double = {
		val a: double = 4.0e0/3.0e0;
		var eps: double = 0;
		while (eps == 0) {
			var b: double = a - 1.0;
			var c: double = b + b + b;
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
	final def dmxpy(val n1: int, val y: Array[double](2), val n2: int, val ldm: int, val x: Array[double](2), val m: Array[double](2)): void = {
		// cleanup odd vector
		for (val (j,i): Point in [0..n2-1, 0..n1-1]) y(0, i) += x(0, j)*read(m, j, i);
	}
}


public class LUFactOrig extends Linpack {

	private var size: int;
	//private int datasizes[] = { 150, 1000, 2000 };
	private var datasizes: ValRail[int] = [ 50, 1000, 2000 ];
	public def JGFsetsize(var size: int): void = {
		this.size = size;
	}

	public def JGFinitialise(): void = {
		n = datasizes(size);
		//Console.OUT.println("ATTENTION: Running with smaller size (" + n + " instead of 500)");
		ldaa = n;
		lda = ldaa + 1;

		var vectorRegion: Region = [0..ldaa];
		var rectangularRegion: Region = [0..ldaa, 0..lda];
		var slimRegion: Region = [0..0, 0..lda]; //fake out because we don't support array sections
		

		a = new Array[double]([0..ldaa, 0..lda]);
		b = new Array[double]([0..0, 0..lda]);
		x = new Array[double]([0..0, 0..lda]);
		ipvt = new Array[int](0..ldaa);

		var nl: long =  n;   //avoid integer overflow
		ops = (2.0*(nl*nl*nl))/3.0 + 2.0*(nl*nl);

		norma = matgen(a, lda, n, b);
	}

	public def JGFkernel(): void = {
		//JGFInstrumentor.startTimer("Section2:LUFact:Kernel");
		info = dgefa(a, lda, n, ipvt);
		dgesl(a, lda, n, ipvt, b, 0);
		//JGFInstrumentor.stopTimer("Section2:LUFact:Kernel");
	}

	public def JGFvalidate(): void = {
		var i: int;
		var eps: double;
		var residn: double;
		val ref: ValRail[double] = [ 6.0, 12.0, 20.0 ];

		for (i = 0; i < n; i++) {
			x(0, i) = b(0, i);
		}
		norma = matgen(a, lda, n, b);
		for (i = 0; i < n; i++) {
			b(0, i) = -b(0, i);
		}

		dmxpy(n, b, n, lda, x, a);
		resid = 0.0;
		normx = 0.0;
		for (i = 0; i < n; i++) {
			resid = (resid > abs(b(0, i))) ? resid : abs(b(0, i));
			normx = (normx > abs(x(0, i))) ? normx : abs(x(0, i));
		}

		eps =  epslon(1.0);
		residn = resid/( n*norma*normx*eps );

		if (residn > ref(size)) {
			Console.OUT.println("Validation failed");
			Console.OUT.println("Computed Norm Res = " + residn);
			Console.OUT.println("Reference Norm Res = " + ref(size));
			throw new Error("Validation failed");
		}
	}

	public def JGFtidyup(): void = {
		// Make sure large arrays are gc'd.

		/* CMD
		* this causes problems in X10, and strictly spreaking, is
		* unnecessary

		a = null;
		b = null;
		x = null;
		ipvt = null;
		System.gc();
		*/
	}

	public def JGFrun(var size: int): void = {
		//JGFInstrumentor.addTimer("Section2:LUFact:Kernel", "Mflops", size);

		JGFsetsize(size);
		JGFinitialise();
		JGFkernel();
		JGFvalidate();
		JGFtidyup();

		//JGFInstrumentor.addOpsToTimer("Section2:LUFact:Kernel", ops/1.0e06);
		//JGFInstrumentor.printTimer("Section2:LUFact:Kernel");
	}
	
	 public static def main(args:Rail[String]!)= {
		
		new LUFactOrig (). JGFrun(0);
		
	}
}
