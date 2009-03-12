/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/*
 !-------------------------------------------------------------------------!
 !                                                                        !
 !       N  A  S     P A R A L L E L     B E N C H M A R K S  3.0         !
 !                                                                        !
 !                      J A V A         V E R S I O N                     !
 !                                                                        !
 !                                  MG                                     !
 !                                                                         !
 !-------------------------------------------------------------------------!
 !                                                                         !
 !    This benchmark is a serial/multithreaded version of the              !
 !    NPB3_0_JAV MG code.                                                  !
 !                                                                         !
 !    Permission to use, copy, distribute and modify this software        !
 !    for any purpose with or without fee is hereby granted.  We          !
 !    request, however, that all derived work reference the NAS           !
 !    Parallel Benchmarks 3.0. This software is provided "as is"          !
 !    without express or implied warranty.                                !
 !                                                                        !
 !    Information on NPB 3.0, including the Technical Report NAS-02-008   !
 !    "Implementation of the NAS Parallel Benchmarks in Java", !
 !    original specifications, source code, results and information       !
 !    on how to submit new results, is available at:                      !
 !                                                                        !
 !          http://www.nas.nasa.gov/Software/NPB/                         !
 !                                                                        !
 !    Send comments or suggestions to  npb@nas.nasa.gov                   !
 !                                                                        !
 !         NAS Parallel Benchmarks Group                                  !
 !         NASA Ames Research Center                                      !
 !         Mail Stop: T27A-1                                              !
 !         Moffett Field, CA   94035-1000                                 !
 !                                                                        !
 !         E-mail:  npb@nas.nasa.gov                                      !
 !         Fax: (650) 604-3957                                        !
 !                                                                        !
 !-------------------------------------------------------------------------!
 ! Authors: E. Barszcz                                                     !
 !          P. Frederickson                                               !
 !          A. Woo                                                        !
 !          M. Yarrow                                                     !
 ! Translation to Java and MultiThreaded Code                             !
 !         M. Frumkin                                                     !
 !         M. Schultz                                                     !
 !-------------------------------------------------------------------------!
 */
package NPB3_0_X10;

import NPB3_0_X10.MGThreads.*;

import NPB3_0_X10.BMInOut.*;
import x10.io.*;

public class MG extends MGBase {
	private val epsilon: double = 1.0E-8;
	private var bid: int = -1;
	private var results: BMResults;
	private var serial: boolean = false;
	private var timeron: boolean = false;

	private var rnmu: double;
	private var n1: int;
        private var n2: int;
        private var n3: int;
	private var verified: int;

	var t_names: Rail[String];
	var is1: int;
        var is2: int;
        var is3: int;
        var ie1: int;
        var ie2: int;
        var ie3: int;

	public def this(var clss: char, var np: int, var ser: boolean): MG = {
		super(clss, np, ser);
		serial = ser;
	}

	public static def entryPoint(var argv: Rail[String]): void = {
		var mg: MG = null;

		BMArgs.ParseCmdLineArgs(argv, BMName);
		var CLSS: char = BMArgs.CLASS.val;
		var np: int = BMArgs.num_threads.val;
		var serial: boolean = BMArgs.serial.val;

		try {
			mg = new MG(CLSS, np, serial);
		} catch (var e: OutOfMemoryError) {
			BMArgs.outOfMemoryMessage();
			throw new Error("Out of memory");
		}
		mg.runBenchMark();
	}

	public def run(): void = { runBenchMark(); }

	public def runBenchMark(): void = {
		BMArgs.Banner(BMName, CLASS, serial, num_threads);

		var niter: int = getInputPars();

		var nsizes: Rail[int] = new Rail[int](3);
		setup(nsizes);
		n1 = nsizes(0);
		n2 = nsizes(1);
		n3 = nsizes(2);

		setTimers();
		timer.resetAllTimers();
		timer.start(T_init);

		zero3(u, 0, n1, n2, n3);
		zran3(v, n1, n2, n3, nx(lt-1), ny(lt-1));

		if (!serial) setupThreads();
		if (serial) resid(u, v, r, 0, n1, n2, n3);
		else residMaster(u, v, r, 0, n1, n2, n3);
//		--------------------------------------------------------------------
//		One iteration for startup
//		--------------------------------------------------------------------
		if (serial) {
			mg3P(u, v, r, n1, n2, n3);
			resid(u, v, r, 0, n1, n2, n3);
		} else {
			mg3Pmaster(u, v, r, n1, n2, n3);
			residMaster(u, v, r, 0, n1, n2, n3);
		}

		zero3(u, 0, n1, n2, n3);
		zran3(v, n1, n2, n3, nx(lt-1), ny(lt-1));

		timer.stop(T_init);
		timer.start(T_bench);

		if (timeron) timer.start(T_resid2);
		if (serial) resid(u, v, r, 0, n1, n2, n3);
		else residMaster(u, v, r, 0, n1, n2, n3);
		if (timeron) timer.stop(T_resid2);
		for (val (it): Point in [1..nit]) {
			if (timeron) timer.start(T_mg3P);
			if (serial) mg3P(u, v, r, n1, n2, n3); else mg3Pmaster(u, v, r, n1, n2, n3);
			if (timeron) timer.stop(T_mg3P);
			if (timeron) timer.start(T_resid2);
			if (serial) resid(u, v, r, 0, n1, n2, n3); else residMaster(u, v, r, 0, n1, n2, n3);
			if (timeron) timer.stop(T_resid2);
		}
		timer.stop(T_bench);

		var tinit: double = timer.readTimer(T_init);
		x10.io.Console.OUT.println(" Initialization time: "+tinit+" seconds");
		var rnm2: double = norm2u3(r, n1, n2, n3, rnmu, nx(lt-1), ny(lt-1), nz(lt-1));
		verified = verify(rnm2);
		var tm: double = timer.readTimer(T_bench);
		results = new BMResults("MG",
				CLASS, nx(lt-1), ny(lt-1), nz(lt-1), nit, tm,
				getMFLOPS(tm, nit), "floating point",
				verified, serial, num_threads, bid);
		results.print();
		if (timeron) printTimers();
		if (verified != 1) throw new Error("Verification failed");
	}

	public def verify(var rnm2: double): int = {
		var verify_value: double = 0.0;

		if (CLASS != 'U') {
			if (CLASS == 'S') {
				verify_value = 0.530770700573E-4;
			} else if (CLASS == 'W') {
				verify_value = 0.250391406439E-17;
			} else if (CLASS == 'A') {
				verify_value = 0.2433365309E-5;
			} else if (CLASS == 'B') {
				verify_value = 0.180056440132E-5;
			} else if (CLASS == 'C') {
				verify_value = 0.570674826298E-6;
			}
			x10.io.Console.OUT.println(" L2 Norm is "+rnm2);
			if (Math.abs(rnm2 - verify_value) < epsilon) {
				verified = 1;
				x10.io.Console.OUT.println(" Deviation is   "+(rnm2 - verify_value));
			} else {
				verified = 0;
				x10.io.Console.OUT.println(" The correct L2 Norm is "+verify_value);
			}
		} else {
			verified = -1;
		}
		BMResults.printVerificationStatus(CLASS, verified, BMName);
		return  verified;
	}

	public def getMFLOPS(var tm: double, var niter: int): double = {
		var mflops: double = 0.0;
		if (tm > 0.0) {
			mflops = 58.0*n1*n2*n3;
			mflops *= niter / (tm*1000000.0);
		}
		return mflops;
	}

	public def getInputPars(): int = {
		var lnx: int = 32;
                var lny: int = 32;
                var lnz: int = 32;
		var f2: File = new File("mg.input");
		if (f2.exists()) {
			x10.io.Console.OUT.println("Reading from input file mg.input");
			try {
				var fis: FileInputStream = new FileInputStream(f2);
				var datafile: DataInputStream = new DataInputStream(fis);
				lt = datafile.readInt();
				if (lt>maxlevel) {
					x10.io.Console.OUT.println("lt="+lt+" Maximum allowable = "+maxlevel);
					System.exit(0);
				}
				lnx = datafile.readInt();
				lny = datafile.readInt();
				lnz = datafile.readInt();
				nit = datafile.readInt();
				fis.close();
			} catch (var e: java.lang.Exception) {
				x10.io.Console.ERR.println("Error reading from file mg.input");
			}
			if (lnx != lny||lnx != lnz) CLASS = 'U';
			else if (lnx == 32&&nit == 4)     CLASS = 'S';
			else if (lnx == 64&&nit == 40)     CLASS = 'W';
			else if (lnx == 256&&nit == 20)CLASS = 'B';
			else if (lnx == 512&&nit == 20)CLASS = 'C';
			else if (lnx == 256&&nit == 4)     CLASS = 'A';
			else CLASS = 'U';
		} else x10.io.Console.OUT.println(" No input file mg.input, Using compiled defaults");

		x10.io.Console.OUT.println(" Size:  "+nx(lt-1)+"x"+ny(lt-1)+"x"+nz(lt-1)+" Iterations:   " +nit);
		return nit;
	}

	public def setTimers(): void = {
		var f1: File = new File("timer.flag");
		if (f1.exists()) {
			timeron = true;
			t_names = new Rail[String](16);
			t_names(T_init) = "init";
			t_names(T_bench) = "benchmark";
			t_names(T_mg3P) = "mg3P";
			t_names(T_psinv) = "psinv";
			t_names(T_resid) = "resid";
			t_names(T_rprj3) = "rprj3";
			t_names(T_interp) = "interp";
			t_names(T_norm2) = "norm2";
		}
	}

	public def printTimers(): void = { //% of the ime should be fixed
		x10.io.Console.OUT.println("  SECTION   Time (secs)");
		var tmax: double = timer.readTimer(T_bench);
		if (tmax == 0.0) tmax = 1.0;
		for (var i: int = T_bench; i <= T_last; i++) {
			var t: double = timer.readTimer(i);
			if (i == T_resid2) {
				t = timer.readTimer(T_resid) - t;
x10.io.Console.OUT.printf("      --> total mg-resid %.3f (%.3f%%)", [t, t*100./tmax]);
			} else {
x10.io.Console.OUT.printf("    %s  %.3f (%.3f%%)", [t_names(i), t, t*100./tmax]);
			}
		}
	}

	public def setup(var nsizes: Rail[int]): void = {
		var size1: int = 3;
                var size2: int = 10;
		var mi: Rail[int] = new Rail[int](size1*size2);
		var ng: Rail[int] = new Rail[int](size1*size2);

		lb = 1;
		ng((lt-1)*size1) = nx(lt-1);
		ng(1+(lt-1)*size1) = ny(lt-1);
		ng(2+(lt-1)*size1) = nz(lt-1);

		for (var ax: int = 0; ax<size1; ax++)
			for (var k: int = lt-2; k >= 0; k--)
				ng(ax+k*size1) = ng(ax+(k+1)*size1)/2;

		for (var k: int = lt-2; k >= 0; k--) {
			nx(k) = ng(k*size1);
			ny(k) = ng(1+k*size1);
			nz(k) = ng(2+k*size1);
		}

		for (var k: int = lt-1; k >= 0; k--) {
			for (var ax: int = 0; ax<size1; ax++) {
				mi(ax+k*size1) = 2 + ng(ax+k*size1);
			}
			m1(k) = mi(k*size1);
			m2(k) = mi(1+k*size1);
			m3(k) = mi(2+k*size1);
		}

		var k: int = lt-1;
		is1 = 2 + ng(k*size1) - ng(k*size1);
		ie1 = 1 + ng(k*size1);
		n1 = nsizes(0) = 3 + ie1 - is1;
		is2 = 2 + ng(1+k*size1) - ng(1+k*size1);
		ie2 = 1 + ng(1+k*size1);
		n2 = nsizes(1) = 3 + ie2 - is2;
		is3 = 2 + ng(2+k*size1) - ng(2+k*size1);
		ie3 = 1 + ng(2+k*size1);
		n3 = nsizes(2) = 3 + ie3 - is3;

		ir(lt-1) = 0;
		for (var j: int = lt-2; j >= 0; j--) {
			ir(j) = ir(j+1)+m1(j+1)*m2(j+1)*m3(j+1);
		}
	}

	public def zero3(var z: Rail[double], var off: int, var n1: int, var n2: int, var n3: int): void = {
		for (val (i3,i2,i1): Point in [0..n3-1, 0..n2-1, 0..n1-1]) z(off+i1+n1*(i2+n2*i3)) = 0.0;
	}

	public def zran3(var z: Rail[double], var n1: int, var n2: int, var n3: int, var nx: int, var ny: int): void = {
//		c---------------------------------------------------------------------
//		c     zran3  loads +1 at ten randomly chosen points,
//		c     loads -1 at a different ten random points,
//		c     and zero elsewhere.
//		c---------------------------------------------------------------------

		var mm: int = 10;
		//double xx, x0, x1;
		var ten: Rail[double] = new Rail[double](mm*2);
		var best: double;
		var j1: Rail[int] = new Rail[int](mm*2),
			var j2: Rail[int] = new Rail[int](mm*2),
			var j3: Rail[int] = new Rail[int](mm*2);
		var jg: Rail[int] = new Rail[int](4*mm*2);
		//int jg_temp = new int[4];

		zero3(z, 0, n1, n2, n3);
		val ii: int = is1-2+nx*(is2-2+ny*(is3-2));

		val d1: int = ie1 - is1 + 1;
		//final int e1 = ie1 - is1 + 2;
		val e2: int = ie2 - is2 + 2;
		val e3: int = ie3 - is3 + 2;

		var seed: double = 314159265.0;
                var a: double = Math.pow(5.0, 13);
		var rng: Random = new Random();
		val a1: double = rng.power(a, nx);
		val a2: double = rng.power(a, nx*ny);
		val ai: double = rng.power(a, ii);
		var x0: double = rng.randlc(seed, ai);
		for (var i3: int = 2; i3 <= e3; i3++) {
			var x1: double = x0;
			for (var i2: int = 2; i2 <= e2; i2++) {
				val xx: double = x1;
				rng.vranlc(d1, xx, a, z, (1+n1*(i2-1+n2*(i3-1))));
				x1 = rng.randlc(x1, a1);
			}
			x0 = rng.randlc(x0, a2);
		}

		for (var i: int = 0; i<mm; i++) {
			ten(i+mm) = 0.0;
			j1(i+mm) = 0;
			j2(i+mm) = 0;
			j3(i+mm) = 0;
			ten(i) = 1.0;
			j1(i) = 0;
			j2(i) = 0;
			j3(i) = 0;
		}

		for (val (i3,i2,i1): Point in [1..n3-2, 1..n2-2, 1..n1-2]) {
			if (z(i1+n1*(i2+n2*i3)) > ten(mm)) {
				ten(mm) = z(i1+n1*(i2+n2*i3));
				j1(mm) = i1;
				j2(mm) = i2;
				j3(mm) = i3;
				bubble(ten, j1, j2, j3, mm, 1);
			}
			if (z(i1+n1*(i2+n2*i3)) < ten(0)) {
				ten(0) = z(i1+n1*(i2+n2*i3));
				j1(0) = i1;
				j2(0) = i2;
				j3(0) = i3;
				bubble(ten, j1, j2, j3, mm, 0);
			}
		}
//		c---------------------------------------------------------------------
//		c     Now which of these are globally best?
//		c---------------------------------------------------------------------
		var i1: int = mm;
		var i0: int = mm;
		for (var i: int = mm-1; i >= 0; i--) {
			best = z(j1(i1-1+mm)+n1*(j2(i1-1+mm)+n2*(j3(i1-1+mm))));
			if (best == z(j1(i1-1+mm)+n1*(j2(i1-1+mm)+n2*(j3(i1-1+mm))))) {
				jg(4*(i+mm)) = 0;
				jg(1+4*(i+mm)) = is1 - 2 + j1(i1-1+mm);
				jg(2+4*(i+mm)) = is2 - 2 + j2(i1-1+mm);
				jg(3+4*(i+mm)) = is3 - 2 + j3(i1-1+mm);
				i1 = i1-1;
			} else {
				jg(4*(i+mm)) = 0;
				jg(1+4*(i+mm)) = 0;
				jg(2+4*(i+mm)) = 0;
				jg(3+4*(i+mm)) = 0;
			}
			ten(i+mm) = best;

			best = z(j1(i0-1)+n1*(j2(i0-1)+n2*(j3(i0-1))));
			if (best == z(j1(i0-1)+n1*(j2(i0-1)+n2*(j3(i0-1))))) {
				jg(4*i) = 0;
				jg(1+4*i) = is1 - 2 + j1(i0-1);
				jg(2+4*i) = is2 - 2 + j2(i0-1);
				jg(3+4*i) = is3 - 2 + j3(i0-1);
				i0 = i0-1;
			} else {
				jg(4*i) = 0;
				jg(1+4*i) = 0;
				jg(2+4*i) = 0;
				jg(3+4*i) = 0;
			}
			ten(i) = best;
		}
		val m1: int = i1+1;
		val m0: int = i0+1;

		for (val (i3,i2,i): Point in [0..n3-1, 0..n2-1, 0..n1-1]) z(i+n1*(i2+n2*i3)) = 0.0;
		for (val (i): Point in [m0..mm]) z(j1(i-1)+n1*(j2(i-1)+n2*(j3(i-1)))) = -1.0;
		for (val (i): Point in [m1..mm]) z(j1(i-1+mm)+n1*(j2(i-1+mm)+n2*(j3(i-1+mm)))) = 1.0;
		comm3(z, 0, n1, n2, n3);
	}

	public def norm2u3(var r: Rail[double], var n1: int, var n2: int, var n3: int, var rnmu: double, var nx: int, var ny: int, var nz: int): double = {
//		c---------------------------------------------------------------------
//		c     norm2u3 evaluates approximations to the L2 norm and the
//		c     uniform (or L-infinity or Chebyshev) norm, under the
//		c     assumption that the boundaries are periodic or zero.  Add the
//		c     boundaries in with half weight (quarter weight on the edges
//		c     and eighth weight at the corners) for inhomogeneous boundaries.
//		c---------------------------------------------------------------------
//		double precision r(n1,n2,n3)
		if (timeron) timer.start(T_norm2);
		rnmu = 0.0;
		var rnm2: double = 0.0;
		for (val (i3,i2,i1): Point in [1..n3-2, 1..n2-2, 1..n1-2]) {
			rnm2 += r(i1+n1*(i2+n2*i3))*r(i1+n1*(i2+n2*i3));
			var a: double = Math.abs(r(i1+n1*(i2+n2*i3)));
			rnmu = dmax1(rnmu, a);
		}

		rnm2 = Math.sqrt(rnm2 / ((nx*ny*nz) as double));
		if (timeron) timer.stop(T_norm2);
		return rnm2;
	}

	public def TestNorm(var r: Rail[double], var n1: int, var n2: int, var n3: int): double = {
		var rnm2: double = 0.0;
		for (val (i3,i2,i1): Point in [1..n3-2, 1..n2-2, 1..n1-2]) rnm2 += r(i1+n1*(i2+n2*i3))*r(i1+n1*(i2+n2*i3));
		rnm2 = Math.sqrt(rnm2 / ((n1*n2*n3) as double));
		x10.io.Console.OUT.println("*****TestNorm  "+rnm2);
		return rnm2;
	}

	public def bubble(var ten: Rail[double], var j1: Rail[int], var j2: Rail[int], var j3: Rail[int], var m: int, var ind: int): void = {
//		c---------------------------------------------------------------------
//		c     bubble        does a bubble sort in direction dir
//		c---------------------------------------------------------------------
		if (ind == 1) {
			for (var i: int = 0; i<m-1; i++) {
				if (ten(i+m*ind) > ten(i+1+m*ind)) {
					var temp: double = ten(i+1+m*ind);
					ten(i+1+m*ind) = ten(i+m*ind);
					ten(i+m*ind) = temp;

					var j_temp: int = j1(i+1+m*ind);
					j1(i+1+m*ind) = j1(i+m*ind);
					j1(i+m*ind) = j_temp;

					j_temp           = j2(i+1+m*ind);
					j2(i+1+m*ind) = j2(i+m*ind);
					j2(i+m*ind) = j_temp;

					j_temp           = j3(i+1+m*ind);
					j3(i+1+m*ind) = j3(i+m*ind);
					j3(i+m*ind) = j_temp;
				} else {
					return;
				}
			}
		} else {
			for (var i: int = 0; i<m-1; i++) {
				if (ten(i+m*ind) < ten(i+1+m*ind)) {
					var temp: double = ten(i+1+m*ind);
					ten(i+1+m*ind) = ten(i+m*ind);
					ten(i+m*ind) = temp;

					var j_temp: int = j1(i+1+m*ind);
					j1(i+1+m*ind) = j1(i+m*ind);
					j1(i+m*ind) = j_temp;

					j_temp           = j2(i+1+m*ind);
					j2(i+1+m*ind) = j2(i+m*ind);
					j2(i+m*ind) = j_temp;

					j_temp           = j3(i+1+m*ind);
					j3(i+1+m*ind) = j3(i+m*ind);
					j3(i+m*ind) = j_temp;
				} else {
					return;
				}
			}
		}
	}

	public def resid(var u: Rail[double], var v: Rail[double], var r: Rail[double], var off: int, var n1: int, var n2: int, var n3: int): void = {
//		c---------------------------------------------------------------------
//		c     resid computes the residual:  r = v - Au
//		c
//		c     This  implementation costs  15A + 4M per result, where
//		c     A and M denote the costs of Addition (or Subtraction) and
//		c     Multiplication, respectively.
//		c     Presuming coefficient a(1) is zero (the NPB assumes this,
//		c     but it is thus not a general case), 3A + 1M may be eliminated,
//		c     resulting in 12A + 3M.
//		c     Note that this vectorizes, and is also fine for cache
//		c     based machines.
//		c---------------------------------------------------------------------
		var u1: Rail[double] = new Rail[double](nm+1);
		var u2: Rail[double] = new Rail[double](nm+1);
		if (timeron) timer.start(T_resid);
		Resid.resid(u, v, r, off, n1, n2, n3, u1, u2, a, 1, n3-1);

//		c---------------------------------------------------------------------
//		c     exchange boundary data
//		c---------------------------------------------------------------------
		comm3(r, off, n1, n2, n3);
		if (timeron) timer.stop(T_resid);
	}

	public def mg3P(var u: Rail[double], var v: Rail[double], var r: Rail[double], var n1: int, var n2: int, var n3: int): void = {
//		c---------------------------------------------------------------------
//		c     multigrid V-cycle routine
//		c---------------------------------------------------------------------
//		double precision u(nr),v(nv),r(nr)

//		c---------------------------------------------------------------------
//		c     down cycle.
//		c     restrict the residual from the find grid to the coarse
//		c---------------------------------------------------------------------
		for (var k: int = lt-1; k >= lb; k--) {
			val j: int = k-1;
			rprj3(r, ir(k), m1(k), m2(k), m3(k), ir(j), m1(j), m2(j), m3(j));
		}
		val kk: int = lb-1;
//		c---------------------------------------------------------------------
//		c     compute an approximate solution on the coarsest grid
//		c---------------------------------------------------------------------
		zero3(u, ir(kk), m1(kk), m2(kk), m3(kk));
		psinv(r, ir(kk), u, ir(kk), m1(kk), m2(kk), m3(kk));
		for (var k: int = lb; k<lt-1; k++) {
			val j: int = k-1;
//			c---------------------------------------------------------------------
//			c        prolongate from level k-1  to k
//			c---------------------------------------------------------------------
			zero3(u, ir(k), m1(k), m2(k), m3(k));
			interp(u, ir(j), m1(j), m2(j), m3(j), ir(k), m1(k), m2(k), m3(k));
//			c---------------------------------------------------------------------
//			c        compute residual for level k
//			c---------------------------------------------------------------------
			resid(u, r, r, ir(k), m1(k), m2(k), m3(k));
//			c---------------------------------------------------------------------
//			c        apply smoother
//			c---------------------------------------------------------------------
			psinv(r, ir(k), u, ir(k), m1(k), m2(k), m3(k));
		}
		val j: int = lt - 2;
                val k: int = lt-1;
		interp(u, ir(j), m1(j), m2(j), m3(j), 0, n1, n2, n3);
		resid(u, v, r, 0, n1, n2, n3);
		psinv(r, 0, u, 0, n1, n2, n3);
	}

	public def mg3Pmaster(var u: Rail[double], var v: Rail[double], var r: Rail[double], var n1: int, var n2: int, var n3: int): void = {
//		c---------------------------------------------------------------------
//		c     multigrid V-cycle routine
//		c---------------------------------------------------------------------
//		double precision u(nr),v(nv),r(nr)

//		c---------------------------------------------------------------------
//		c     down cycle.
//		c     restrict the residual from the find grid to the coarse
//		c---------------------------------------------------------------------
		for (var k: int = lt-1; k >= lb; k--) {
			val j: int = k-1;
			rprj3Master(r, ir(k), m1(k), m2(k), m3(k), ir(j), m1(j), m2(j), m3(j));
		}
		val kk: int = lb-1;
//		c---------------------------------------------------------------------
//		c     compute an approximate solution on the coarsest grid
//		c---------------------------------------------------------------------
		zero3(u, ir(kk), m1(kk), m2(kk), m3(kk));
		psinvMaster(r, ir(kk), u, ir(kk), m1(kk), m2(kk), m3(kk));
		for (var k: int = lb; k<lt-1; k++) {
			val j: int = k-1;
//			c---------------------------------------------------------------------
//			c        prolongate from level k-1  to k
//			c---------------------------------------------------------------------
			zero3(u, ir(k), m1(k), m2(k), m3(k));
			interpMaster(u, ir(j), m1(j), m2(j), m3(j), ir(k), m1(k), m2(k), m3(k));
//			c---------------------------------------------------------------------
//			c        compute residual for level k
//			c---------------------------------------------------------------------
			residMaster(u, r, r, ir(k), m1(k), m2(k), m3(k));
//			c---------------------------------------------------------------------
//			c        apply smoother
//			c---------------------------------------------------------------------
			psinvMaster(r, ir(k), u, ir(k), m1(k), m2(k), m3(k));
		}
		val j: int = lt - 2;
		interpMaster(u, ir(j), m1(j), m2(j), m3(j), 0, n1, n2, n3);
		residMaster(u, v, r, 0, n1, n2, n3);
		psinvMaster(r, 0, u, 0, n1, n2, n3);
	}

	public def rprj3(var r: Rail[double], var roff: int, var m1k: int, var m2k: int, var m3k: int, var zoff: int, var m1j: int, var m2j: int, var m3j: int): void = {
//		c---------------------------------------------------------------------
//		c     rprj3 projects onto the next coarser grid,
//		c     using a trilinear Finite Element projection:  s = r' = P r
//		c
//		c     This  implementation costs  20A + 4M per result, where
//		c     A and M denote the costs of Addition and Multiplication.
//		c     Note that this vectorizes, and is also fine for cache
//		c     based machines.
//		c---------------------------------------------------------------------
//		double precision r(m1k,m2k,m3k), s(m1j,m2j,m3j)
		var x1: Rail[double] = new Rail[double](nm+1);
                var y1: Rail[double] = new Rail[double](nm+1);

		if (timeron) timer.start(T_rprj3);
		Rprj.rprj3(r, roff, m1k, m2k, m3k, zoff, m1j, m2j, m3j, x1, y1, 2, m3j-1);
		comm3(r, zoff, m1j, m2j, m3j);
		if (timeron) timer.stop(T_rprj3);
	}

	public def interp(var u: Rail[double], var zoff: int, var mm1: int, var mm2: int, var mm3: int, var uoff: int, var n1: int, var n2: int, var n3: int): void = {
		val m: int = 535;
		val z1: Rail[double] = new Rail[double](m);
                val z2: Rail[double] = new Rail[double](m);
                val z3: Rail[double] = new Rail[double](m);
		if (timeron) timer.start(T_interp);
		Interp.interp(u, zoff, mm1, mm2, mm3, uoff, n1, n2, n3, z1, z2, z3, 1, mm3);
		if (timeron) timer.stop(T_interp);
	}

	public def psinv(var r: Rail[double], var roff: int, var u: Rail[double], var uoff: int, var n1: int, var n2: int, var n3: int): void = {
		if (timeron) timer.start(T_psinv);
		Psinv.psinv(r, roff, u, uoff, n1, n2, n3, new Rail[double](nm+1), new Rail[double](nm+1), c, 1, n3-1);
//		c---------------------------------------------------------------------
//		c     exchange boundary points
//		c---------------------------------------------------------------------
		comm3(u, uoff, n1, n2, n3);
		if (timeron) timer.stop(T_psinv);
	}

	private var interp: Rail[Interp];
	private var psinv: Rail[Psinv];
	private var rprj: Rail[Rprj];
	private var resid: Rail[Resid];

	private def setupThreads(): void = {
		interp = new Rail[Interp](num_threads);
		psinv = new Rail[Psinv](num_threads);
		rprj = new Rail[Rprj](num_threads);
		resid = new Rail[Resid](num_threads);

		for (val (i): Point in [0..num_threads-1]) {
			interp(i) = new Interp(this, i);
			//interp[i].start();

			psinv(i) = new Psinv(this, i);
			//psinv[i].start();

			rprj(i) = new Rprj(this, i);
			//rprj[i].start();

			resid(i) = new Resid(this, i);
			//resid[i].start();
		}
	}

	public def residMaster(val u: Rail[double], val v: Rail[double], val r: Rail[double], val off: int, val n1: int, val n2: int, val n3: int): void = {
		if (timeron) timer.start(T_resid);
		if (num_threads == 1) resid(u, v, r, off, n1, n2, n3);
		else {
			val visr: boolean = (v == r);
			finish foreach (val (l): Point in [0..num_threads-1]) resid(l).step(false, visr, 1, n3, n1, n2, n3, off);
			comm3(r, off, n1, n2, n3);
		}
		if (timeron) timer.stop(T_resid);
	}

	public def psinvMaster(val r: Rail[double], val roffl: int, val u: Rail[double], val uoffl: int, val n1: int, val n2: int, val n3: int): void = {
		if (timeron) timer.start(T_psinv);
		if (num_threads == 1) psinv(r, roffl, u, uoffl, n1, n2, n3);
		else {
			finish foreach (val (l): Point in [0..num_threads-1]) psinv(l).step(false, 1, n3, n1, n2, n3, roffl, uoffl);
			comm3(u, uoffl, n1, n2, n3);
		}
		if (timeron) timer.stop(T_psinv);
	}

	public def interpMaster(val u: Rail[double], val zoffl: int, val mm1: int, val mm2: int, val mm3: int, val uoffl: int, val n1: int, val n2: int, val n3: int): void = {
		if (timeron) timer.start(T_interp);
		if (num_threads == 1) interp(u, zoffl, mm1, mm2, mm3, uoffl, n1, n2, n3);
		else {
			finish foreach (val (l): Point in [0..num_threads-1]) interp(l).step(false, 1, mm3, mm1, mm2, mm3, n1, n2, n3, zoffl, uoffl);
		}
		if (timeron) timer.stop(T_interp);
	}

	public def rprj3Master(val r: Rail[double], val roffl: int, val m1k: int, val m2k: int, val m3k: int, val zoffl: int, val m1j: int, val m2j: int, val m3j: int): void = {
		if (timeron) timer.start(T_rprj3);
		if (num_threads == 1) rprj3(r, roffl, m1k, m2k, m3k, zoffl, m1j, m2j, m3j);
		else {
			finish foreach (val (l): Point in [0..num_threads-1]) rprj(l).step(false, 2, m3j, m1k, m2k, m3k, m1j, m2j, m3j, roffl, zoffl);
			comm3(r, zoffl, m1j, m2j, m3j);
		}
		if (timeron) timer.stop(T_rprj3);
	}

	public def getTime(): double = { return timer.readTimer(T_bench); }
}
