/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/*
!-------------------------------------------------------------------------!
!									  !
!	 N  A  S     P A R A L L E L	 B E N C H M A R K S  3.0	  !
!									  !
!			J A V A 	V E R S I O N			  !
!									  !
!                                  C G                                    !
!									  !
!-------------------------------------------------------------------------!
!									  !
!    This benchmark is a serial/multithreaded version of the              !
!    NPB3_0_JAV CG code.                                                  !
!									  !
!    Permission to use, copy, distribute and modify this software	  !
!    for any purpose with or without fee is hereby granted.  We 	  !
!    request, however, that all derived work reference the NAS  	  !
!    Parallel Benchmarks 3.0. This software is provided "as is" 	  !
!    without express or implied warranty.				  !
!									  !
!    Information on NPB 3.0, including the Technical Report NAS-02-008	  !
!    "Implementation of the NAS Parallel Benchmarks in Java",	!
!    original specifications, source code, results and information	  !
!    on how to submit new results, is available at:			  !
!									  !
!	    http://www.nas.nasa.gov/Software/NPB/			  !
!									  !
!    Send comments or suggestions to  npb@nas.nasa.gov  		  !
!									  !
!	   NAS Parallel Benchmarks Group				  !
!	   NASA Ames Research Center					  !
!	   Mail Stop: T27A-1						  !
!	   Moffett Field, CA   94035-1000				  !
!									  !
!	   E-mail:  npb@nas.nasa.gov					  !
!	   Fax: (650) 604-3957					  !
!									  !
!-------------------------------------------------------------------------!
! Authors: M. Yarrow                                                      !
!          C. Kuszmaul					                  !
! Translation to Java and to MultiThreaded Code				  !
!	   M. Frumkin							  !
!	   M. Schultz							  !
!-------------------------------------------------------------------------!
Translated to X10 by vj 08/2005

*/
package NPB3_0_X10;

import NPB3_0_X10.CGThreads.*;
import NPB3_0_X10.BMInOut.*;
import x10.io.*;

public class CG {
	public const BMName: String = "CG";
	public const cgitmax: int = 25;
	var CLASS: char = 'S';

	var na: int;
        var nonzer: int;
        var niter: int;
	var shift: double;
        var rcond: double;
        var zeta_verify_value: double;
	var nz: int;
	val firstrow: int;
        val lastrow: int;
        val firstcol: int;
        val lastcol: int;

	var colidx: Rail[int];
        var rowstr: Rail[int];
        var iv: Rail[int];
        var arow: Rail[int];
        var acol: Rail[int];
	var v: Rail[double];
        var aelt: Rail[double];
        var a: Rail[double];
	var p: Rail[double];
        var q: Rail[double];
        var r: Rail[double];
        var z: Rail[double];
        var x: Rail[double];

	var t_names: Rail[String];
	var timer_read: double;
	var timeron: boolean;
	public const t_init: int = 1;
        public const t_bench: int = 2;
        public const t_conj_grad: int = 3;
        public const t_last: int = 3;
	public const timer: Timer = new Timer();

	var num_threads: int;
	var dmaster: Rail[double];
        var rhomaster: Rail[double];
        var rnormmaster: Rail[double];

	var bid: int = -1;
	var results: BMResults;
	var serial: boolean = true;
	var rng: Random = new Random();
	var zeta: double = rng.randlc(CGGen.amult);

	var worker: Rail[CGWorker];
	var master: CG;
	val THREADS: Region;

	public def this(var CLSS: char, var np: int, var ser: boolean): CG = {
		CLASS = CLSS;
		num_threads = np;
		switch (CLASS) {
			case 'S':
				na = 1400;
				nonzer = 7;
				shift = 10;
				niter = 15;
				rcond = .1;
				zeta_verify_value = 8.5971775078648;
				break;
			case 'W':
				na = 7000;
				nonzer = 8;
				shift = 12;
				niter = 15;
				rcond = .1;
				zeta_verify_value = 10.362595087124;
				break;
			case 'A':
				na = 14000;
				nonzer = 11;
				shift = 20;
				niter = 15;
				rcond = .1;
				zeta_verify_value = 17.130235054029;
				break;
			case 'B':
				na = 75000;
				nonzer = 13;
				shift = 60;
				niter = 75;
				rcond = .1;
				zeta_verify_value = 22.712745482631;
				break;
			case 'C':
				na = 150000;
				nonzer = 15;
				shift = 110;
				niter = 75;
				rcond = .1;
				zeta_verify_value = 28.973605592845;
				break;
		}
		t_names = new Rail[String](t_last+1);

		nz = (na*(nonzer+1)*(nonzer+1)+ na*(nonzer+2));
		colidx = new Rail[int](nz +1);
		rowstr = new Rail[int](na+2);

		a = new Rail[double](nz+1);
		p = new Rail[double](na+3);
		q = new Rail[double](na+3);
		r = new Rail[double](na+3);
		x = new Rail[double](na+3);
		z = new Rail[double](na+3);

		serial = ser;

		dmaster = new Rail[double](num_threads);
		rhomaster = new Rail[double](num_threads);
		rnormmaster = new Rail[double](num_threads);

		firstrow = 1;
		lastrow  = na;
		firstcol = 1;
		lastcol  = na;

		var cggen: CGGen = new CGGen(firstrow, lastrow, firstcol, lastcol, rng);
		cggen.makea(na, nz, a, colidx, rowstr, nonzer, rcond, shift);

		THREADS = [0..num_threads-1];
	}

	public static def entryPoint(var argv: Rail[String]): void = {
		var cg: CG = null;

		BMArgs.ParseCmdLineArgs(argv, BMName);
		var CLSS: char = BMArgs.CLASS.val;
		var np: int = BMArgs.num_threads.val;
		np = 8;
		var serial: boolean = BMArgs.serial.val;
		try {
			cg = new CG(CLSS, np, serial);
		} catch (var e: OutOfMemoryError) {
			BMArgs.outOfMemoryMessage();
			throw new Error("Out of memory");
		}
		cg.runBenchMark();
	}

	public def run(): void = { runBenchMark(); }

	public def runBenchMark(): void = {
		var i: int;
                var j: int;
                var k: int;
                var it: int;
		var zeta: double;
		var rnorm: double = 0;

		BMArgs.Banner(BMName, CLASS, serial, num_threads);
		x10.io.Console.OUT.println(" Size: " + na +" Iterations: " + niter);

		timer.resetAllTimers();
		setTimers();

		timer.start(t_init);

		//---------------------------------------------------------------------
		//  Inialize random number generator
		//---------------------------------------------------------------------

		if (!serial) setupThreads();

		//---------------------------------------------------------------------
		//  Note: as a result of the above call to makea:
		//        values of j used in indexing rowstr go from 1 --> lastrow-firstrow+1
		//        values of colidx which are col indexes go from firstcol --> lastcol
		//        So:
		//        Shift the col index vals from actual (firstcol --> lastcol)
		//        to local, i.e., (1 --> lastcol-firstcol+1)
		//---------------------------------------------------------------------
		for (j = 1; j <= lastrow-firstrow+1; j++) {
			for (k = rowstr(j); k <= rowstr(j+1)-1; k++) {
				colidx(k) = colidx(k) - firstcol + 1;
			}
		}

		//---------------------------------------------------------------------
		//  set starting vector to (1, 1, .... 1)
		//---------------------------------------------------------------------
		for (i = 1; i <= na+1; i++) x(i) = 1.0;
		zeta  = 0.0;
		//---------------------------------------------------------------------
		//  Do one iteration untimed to init all code and data page tables
		//----> (then reinit, start timing, to niter its)
		//---------------------------------------------------------------------
		// 	rnorm = conj_grad(colidx, rowstr, x, z, a, p, q, r, rnorm);

		//---------------------------------------------------------------------
		//  zeta = shift + 1/(x.z)
		//  So, first: (x.z)
		//  Also, find norm of z
		//  So, first: (z.z)
		//---------------------------------------------------------------------
		var tnorm1: double = 0.0;
		var tnorm2: double = 0.0;
		for (j = 1; j <= lastcol-firstcol+1; j++) {
			tnorm1 += x(j)*z(j);
			tnorm2 += z(j)*z(j);
		}
		tnorm2 = 1.0/ Math.sqrt(tnorm2);

		//---------------------------------------------------------------------
		//  Normalize z to obtain x
		//---------------------------------------------------------------------
		for (j = 1; j <= lastcol-firstcol+1; j++) {
			x(j) = tnorm1*z(j);
		}
		//---------------------------------------------------------------------
		//  set starting vector to (1, 1, .... 1)
		//---------------------------------------------------------------------
		for (i = 1; i <= na+1; i++) x(i) = 1.0;
		zeta  = 0.0;
		timer.stop(t_init);
		timer.start(t_bench);
		//---------------------------------------------------------------------
		//  Main Iteration for inverse power method
		//---------------------------------------------------------------------

		for (val (itt): Point in [1..niter]) {
			if (timeron)timer.start(t_conj_grad);
			if (serial) {
				rnorm = conj_grad(colidx, rowstr, x, z, a, p, q, r, rnorm);
			} else {
				finish foreach (val (p): Point in THREADS) worker(p).step3();
				var rho: double = 0.0;
				for (val (p): Point in THREADS) rho += rhomaster(p);
				for (val (ii): Point in [0..cgitmax]) {
					finish foreach (val (p): Point in THREADS) worker(p).step0();
					var dcff: double = 0.0; for (val (m): Point in THREADS) dcff += dmaster(m);
					val rho0: double = rho;
					val alpha: double = rho/dcff;
					finish foreach (val (p): Point in THREADS) worker(p).step1(alpha);
					rho = 0.0; for (val (m): Point in THREADS) rho += rhomaster(m);
					val beta: double = rho/rho0;
					finish foreach (val (p): Point in THREADS) worker(p).step2(beta);
				}
				finish foreach (val (p): Point in THREADS) worker(p).endWork();
				rnorm = 0.0; for (val (m): Point in THREADS) rnorm += rnormmaster(m);
				rnorm = Math.sqrt(rnorm);
			}
			if (timeron) timer.stop(t_conj_grad);
			//---------------------------------------------------------------------
			//  zeta = shift + 1/(x.z)
			//  So, first: (x.z)
			//  Also, find norm of z
			//  So, first: (z.z)
			//---------------------------------------------------------------------
			tnorm1 = 0.0;
			tnorm2 = 0.0;
			for (j = 1; j <= lastcol-firstcol+1; j++) {
				tnorm1 += x(j)*z(j);
				tnorm2 += z(j)*z(j);
			}
			tnorm2 = 1.0 / Math.sqrt(tnorm2);
			zeta = shift + 1.0 /tnorm1;
			x10.io.Console.OUT.println("    "+ itt + "       " + rnorm +" " + zeta);
			//---------------------------------------------------------------------
			//  Normalize z to obtain x
			//---------------------------------------------------------------------
			for (j = 1; j <= lastcol-firstcol+1; j++) x(j) = tnorm2*z(j);
		}
		timer.stop(t_bench);

		//---------------------------------------------------------------------
		//  End of timed section
		//---------------------------------------------------------------------

		var verified: int = verify(zeta);
		var time: double = timer.readTimer(t_bench);
		results = new BMResults(BMName,
				CLASS,
				na,
				0,
				0,
				niter,
				time,
				getMFLOPS(time, niter),
				"floating point",
				verified,
				serial,
				num_threads,
				bid);
		results.print();
		if (timeron) PrintTimers();
		if (verified != 1) throw new Error("Verification failed");
	}
	def setTimers(): void = {
		var fp: File = new File("timer.flag");
		if (fp.exists()) {
			timeron = true;
			t_names(t_init) = new String("init");
			t_names(t_bench) = new String("benchmark");
			t_names(t_conj_grad) = new String("conjugate gradient");
		} else {
			timeron = false;
		}
	}

	public def getMFLOPS(var total_time: double, var niter: int): double = {
		var mflops: double = 0.0;
		if (total_time != 0.0) {
			mflops = (float)(2*na)*
				(3.+(float)(nonzer*(nonzer+1))
				  + 25.*(5.+(float)(nonzer*(nonzer+1)))+ 3.) ;
			mflops *= niter / (total_time*1000000.0);
		}
		return mflops;
	}

	public def verify(var zeta: double): int = {
		var verified: int = 0;
		var epsilon: double = 1.0E-10;
		if (CLASS != 'U') {
			x10.io.Console.OUT.println(" Zeta is   " + zeta);
			if (Math.abs(zeta - zeta_verify_value) <= epsilon) {
				verified = 1;
				x10.io.Console.OUT.println(" Deviation is   " + (zeta-zeta_verify_value));
			} else {
				verified = 0;
				x10.io.Console.OUT.println(" The correct zeta is " + zeta_verify_value);
			}
		} else {
			verified = -1;
		}
		BMResults.printVerificationStatus(CLASS, verified, BMName);
		return verified;
	}

	public def conj_grad(var colidx: Rail[int], var rowstr: Rail[int], var x: Rail[double], var z: Rail[double], var a: Rail[double], var p: Rail[double], var q: Rail[double], var r: Rail[double], var rnorm: double): double = {
		//---------------------------------------------------------------------
		//  Floating point arrays here are named as in NPB1 spec discussion of
		//  CG algorithm
		//---------------------------------------------------------------------

		var i: int;
                var j: int;
                var k: int;
		var cgit: int;
		var d: double;
                var sum: double;
                var rho: double;
                var rho0: double;

		//---------------------------------------------------------------------
		//  Initialize the CG algorithm:
		//---------------------------------------------------------------------
		for (j = 1; j <= na+1; j++) {
			q(j) = 0.0;
			z(j) = 0.0;
			r(j) = x(j);
			p(j) = r(j);
		}
		//---------------------------------------------------------------------
		//  rho = r.r
		//  Now, obtain the norm of r: First, sum squares of r elements
		// locally...
		//---------------------------------------------------------------------
		rho = 0.0;
		for (j = 1; j <= lastcol-firstcol+1; j++) rho += r(j)*r(j);
		var alpha: double = 0.0;
                var beta: double = 0.0;
		//---------------------------------------------------------------------
		//  The conj grad iteration loop
		//---------------------------------------------------------------------
		for (cgit = 1; cgit <= cgitmax; cgit++) {
			//---------------------------------------------------------------------
			//  q = A.p
			//  The partition submatrix-vector multiply: use workspace w
			//---------------------------------------------------------------------
			//
			//  NOTE: this version of the multiply is actually (slightly: maybe
			// %5)
			//        faster on the sp2 on 16 nodes than is the unrolled-by-2 version
			//        below. On the Cray t3d, the reverse is true, i.e., the
			//        unrolled-by-two version is some 10% faster.
			//        The unrolled-by-8 version below is significantly faster
			//        on the Cray t3d - overall speed of code is 1.5 times faster.
			//
			for (j = 1; j <= lastrow-firstrow+1; j++) {
				sum = 0.0;
				for (k = rowstr(j); k <= rowstr(j+1)-1; k++) {
					sum += a(k)*p(colidx(k));
				}
				q(j) = sum;
			}
			//---------------------------------------------------------------------
			//  Obtain p.q
			//---------------------------------------------------------------------
			d = 0.0;
			for (j = 1; j <= lastcol-firstcol+1; j++) d += p(j)*q(j);
			//---------------------------------------------------------------------
			//  Obtain alpha = rho / (p.q)
			//---------------------------------------------------------------------
			alpha = rho / d;
			//---------------------------------------------------------------------
			//  Obtain z = z + alpha*p
			//  and r = r - alpha*q
			//---------------------------------------------------------------------
			for (j = 1; j <= lastcol-firstcol+1; j++) {
				z(j) = z(j) + alpha*p(j);
				r(j) = r(j) - alpha*q(j);
			}
			//---------------------------------------------------------------------
			//  rho = r.r
			//  Obtain the norm of r: First, sum squares of r elements locally...
			//---------------------------------------------------------------------
			rho0 = rho;
			rho = 0.0;
			for (j = 1; j <= lastcol-firstcol+1; j++) rho += r(j)*r(j);
			beta = rho / rho0;
			//---------------------------------------------------------------------
			//  p = r + beta*p
			//---------------------------------------------------------------------
			for (j = 1; j <= lastcol-firstcol+1; j++) {
				p(j) = r(j) + beta*p(j);
			}
		}
		//---------------------------------------------------------------------
		//  Compute residual norm explicitly: ||r|| = ||x - A.z||
		//  First, form A.z
		//  The partition submatrix-vector multiply
		//---------------------------------------------------------------------
		for (j = 1; j <= lastrow-firstrow+1; j++) {
			sum = 0.0;
			for (k = rowstr(j); k <= rowstr(j+1)-1; k++) {
				sum += a(k)*z(colidx(k));
			}
			r(j) = sum;
		}
		//---------------------------------------------------------------------
		//  At this point, r contains A.z
		//---------------------------------------------------------------------
		sum = 0.0;
		for (j = 1; j <= lastcol-firstcol+1; j++) sum += (x(j)-r(j))*(x(j)-r(j));
		return Math.sqrt(sum);
	}

	public def endWork(): double = {
		var sum: double;
		//---------------------------------------------------------------------
		//  Compute residual norm explicitly: ||r|| = ||x - A.z||
		//  First, form A.z
		//  The partition submatrix-vector multiply
		//---------------------------------------------------------------------
		for (var j: int = 1; j <= lastrow-firstrow+1; j++) {
			sum = 0.0;
			for (var k: int = rowstr(j); k <= rowstr(j+1)-1; k++) {
				sum += a(k)*z(colidx(k));
			}
			r(j) = sum;
		}
		//---------------------------------------------------------------------
		//  At this point, r contains A.z
		//---------------------------------------------------------------------
		sum = 0.0;
		for (var j: int = 1; j <= lastcol-firstcol+1; j++) sum += (x(j)-r(j))*(x(j)-r(j));
		return Math.sqrt(sum);
	}

	private def PrintTimers(): void = {
		x10.io.Console.OUT.println("  SECTION   Time (secs)");
		var ttot: double = timer.readTimer(t_bench);
		if (ttot == 0.0) ttot = 1.0;
		for (var i: int = 1; i <= t_last; i++) {
			var tm: double = timer.readTimer(i);
			if (i == t_init) {
				x10.io.Console.OUT.printf("  %s:%.3f", [t_names(i), tm]);
			} else {
				x10.io.Console.OUT.printf("  %s:%.3f (%.3f%%)", [t_names(i), tm, tm*100.0/ttot]);
				if (i == t_conj_grad) {
					tm = ttot - tm;
					x10.io.Console.OUT.printf("    --> total rest :%.3f (%.3f%%)", [tm, tm*100.0/ttot]);
				}
			}
		}
	}

	public def getTime(): double = { return timer.readTimer(t_bench); }

	public def setupThreads(): void = {
		worker = new Rail[CGWorker](num_threads);

		var div: int = na/num_threads;
		var rem: int = na%num_threads;
		var start: int = 1;
                var end: int = 0;

		for (var i: int = 0; i<num_threads; i++) {
			end += div;
			if (rem != 0) {
				rem--;
				end++;
			}
			worker(i) = new CGWorker(i, start, end, a, colidx, rowstr,
					x, z, p, q, r,
					dmaster, rhomaster, rnormmaster);
			start = end+1;
		}
	}
}
