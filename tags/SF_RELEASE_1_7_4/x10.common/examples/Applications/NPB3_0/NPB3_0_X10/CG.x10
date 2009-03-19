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
import java.io.*;
import java.text.*;

public class CG {
	const String BMName = "CG";
	const int cgitmax = 25;
	char CLASS = 'S';

	int na, nonzer, niter;
	double shift, rcond, zeta_verify_value;
	int nz;
	final int firstrow, lastrow, firstcol, lastcol;

	int colidx[], rowstr[], iv[], arow[], acol[];
	double v[], aelt[], a[];
	double[] p, q, r, z, x;

	String t_names[];
	double timer_read;
	boolean timeron;
	const int t_init = 1, t_bench = 2, t_conj_grad = 3, t_last = 3;
	const Timer timer = new Timer();

	int num_threads;
	double[] dmaster, rhomaster, rnormmaster;

	int bid =-1;
	BMResults results;
	boolean serial = true;
	Random rng = new Random();
	double zeta = rng.randlc(CGGen.amult);

	CGWorker[] worker;
	CG master;
	final region THREADS;

	public CG(char CLSS, int np, boolean ser) {
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
		t_names = new String[t_last+1];

		nz = (na*(nonzer+1)*(nonzer+1)+ na*(nonzer+2));
		colidx = new int[nz +1];
		rowstr = new int[na+2];

		a = new double[nz+1];
		p = new double[na+3];
		q = new double[na+3];
		r = new double[na+3];
		x = new double[na+3];
		z = new double[na+3];

		serial = ser;

		dmaster = new double[num_threads];
		rhomaster = new double[num_threads];
		rnormmaster = new double[num_threads];

		firstrow = 1;
		lastrow  = na;
		firstcol = 1;
		lastcol  = na;

		CGGen cggen = new CGGen(firstrow, lastrow, firstcol, lastcol, rng);
		cggen.makea(na, nz, a, colidx, rowstr, nonzer, rcond, shift);

		THREADS = [0:num_threads-1];
	}

	public static void entryPoint(String argv[]) {
		nullable<CG>cg = null;

		BMArgs.ParseCmdLineArgs(argv, BMName);
		char CLSS = BMArgs.CLASS.val;
		int np = BMArgs.num_threads.val;
		np = 8;
		boolean serial = BMArgs.serial.val;
		try {
			cg = new CG(CLSS, np, serial);
		} catch (OutOfMemoryError e) {
			BMArgs.outOfMemoryMessage();
			throw new Error("Out of memory");
		}
		cg.runBenchMark();
	}

	public void run() { runBenchMark(); }

	public void runBenchMark() {
		int i, j, k, it;
		double zeta;
		double rnorm = 0;

		BMArgs.Banner(BMName, CLASS, serial, num_threads);
		System.out.println(" Size: " + na +" Iterations: " + niter);

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
			for (k = rowstr[j]; k <= rowstr[j+1]-1; k++) {
				colidx[k] = colidx[k] - firstcol + 1;
			}
		}

		//---------------------------------------------------------------------
		//  set starting vector to (1, 1, .... 1)
		//---------------------------------------------------------------------
		for (i = 1; i <= na+1; i++) x[i] = 1.0;
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
		double tnorm1 = 0.0;
		double tnorm2 = 0.0;
		for (j = 1; j <= lastcol-firstcol+1; j++) {
			tnorm1 += x[j]*z[j];
			tnorm2 += z[j]*z[j];
		}
		tnorm2 = 1.0/ Math.sqrt(tnorm2);

		//---------------------------------------------------------------------
		//  Normalize z to obtain x
		//---------------------------------------------------------------------
		for (j = 1; j <= lastcol-firstcol+1; j++) {
			x[j] = tnorm1*z[j];
		}
		//---------------------------------------------------------------------
		//  set starting vector to (1, 1, .... 1)
		//---------------------------------------------------------------------
		for (i = 1; i <= na+1; i++) x[i] = 1.0;
		zeta  = 0.0;
		timer.stop(t_init);
		timer.start(t_bench);
		//---------------------------------------------------------------------
		//  Main Iteration for inverse power method
		//---------------------------------------------------------------------

		for (point [itt] : [1:niter]) {
			if (timeron)timer.start(t_conj_grad);
			if (serial) {
				rnorm = conj_grad(colidx, rowstr, x, z, a, p, q, r, rnorm);
			} else {
				finish foreach (point [p]: THREADS) worker[p].step3();
				double rho = 0.0;
				for (point [p]: THREADS) rho += rhomaster[p];
				for (point [ii]: [0:cgitmax]) {
					finish foreach (point [p]: THREADS) worker[p].step0();
					double dcff = 0.0; for (point [m]: THREADS) dcff += dmaster[m];
					final double rho0 = rho;
					final double alpha = rho/dcff;
					finish foreach (point [p]: THREADS) worker[p].step1(alpha);
					rho = 0.0; for (point [m]: THREADS) rho += rhomaster[m];
					final double beta = rho/rho0;
					finish foreach (point [p]: THREADS) worker[p].step2(beta);
				}
				finish foreach (point [p]: THREADS) worker[p].endWork();
				rnorm = 0.0; for (point [m] : THREADS) rnorm += rnormmaster[m];
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
				tnorm1 += x[j]*z[j];
				tnorm2 += z[j]*z[j];
			}
			tnorm2 = 1.0 / Math.sqrt(tnorm2);
			zeta = shift + 1.0 /tnorm1;
			System.out.println("    "+ itt + "       " + rnorm +" " + zeta);
			//---------------------------------------------------------------------
			//  Normalize z to obtain x
			//---------------------------------------------------------------------
			for (j = 1; j <= lastcol-firstcol+1; j++) x[j] = tnorm2*z[j];
		}
		timer.stop(t_bench);

		//---------------------------------------------------------------------
		//  End of timed section
		//---------------------------------------------------------------------

		int verified = verify(zeta);
		double time = timer.readTimer(t_bench);
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
	void setTimers() {
		File fp = new File("timer.flag");
		if (fp.exists()) {
			timeron = true;
			t_names[t_init] = new String("init");
			t_names[t_bench] = new String("benchmark");
			t_names[t_conj_grad] = new String("conjugate gradient");
		} else {
			timeron = false;
		}
	}

	public double getMFLOPS(double total_time, int niter) {
		double mflops = 0.0;
		if (total_time != 0.0) {
			mflops = (float)(2*na)*
				(3.+(float)(nonzer*(nonzer+1))
				  + 25.*(5.+(float)(nonzer*(nonzer+1)))+ 3.) ;
			mflops *= niter / (total_time*1000000.0);
		}
		return mflops;
	}

	public int verify(double zeta) {
		int verified = 0;
		double epsilon = 1.0E-10;
		if (CLASS != 'U') {
			System.out.println(" Zeta is   " + zeta);
			if (Math.abs(zeta - zeta_verify_value) <= epsilon) {
				verified = 1;
				System.out.println(" Deviation is   " + (zeta-zeta_verify_value));
			} else {
				verified = 0;
				System.out.println(" The correct zeta is " + zeta_verify_value);
			}
		} else {
			verified = -1;
		}
		BMResults.printVerificationStatus(CLASS, verified, BMName);
		return verified;
	}

	public double conj_grad(int colidx[], int rowstr[],
							double x[], double z[], double a[],
							double p[], double q[], double r[],
							double rnorm)
	{
		//---------------------------------------------------------------------
		//  Floating point arrays here are named as in NPB1 spec discussion of
		//  CG algorithm
		//---------------------------------------------------------------------

		int i, j, k;
		int cgit;
		double d, sum, rho, rho0;

		//---------------------------------------------------------------------
		//  Initialize the CG algorithm:
		//---------------------------------------------------------------------
		for (j = 1; j <= na+1; j++) {
			q[j] = 0.0;
			z[j] = 0.0;
			r[j] = x[j];
			p[j] = r[j];
		}
		//---------------------------------------------------------------------
		//  rho = r.r
		//  Now, obtain the norm of r: First, sum squares of r elements
		// locally...
		//---------------------------------------------------------------------
		rho = 0.0;
		for (j = 1; j <= lastcol-firstcol+1; j++) rho += r[j]*r[j];
		double alpha = 0.0, beta = 0.0;
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
				for (k = rowstr[j]; k <= rowstr[j+1]-1; k++) {
					sum += a[k]*p[colidx[k]];
				}
				q[j] = sum;
			}
			//---------------------------------------------------------------------
			//  Obtain p.q
			//---------------------------------------------------------------------
			d = 0.0;
			for (j = 1; j <= lastcol-firstcol+1; j++) d += p[j]*q[j];
			//---------------------------------------------------------------------
			//  Obtain alpha = rho / (p.q)
			//---------------------------------------------------------------------
			alpha = rho / d;
			//---------------------------------------------------------------------
			//  Obtain z = z + alpha*p
			//  and r = r - alpha*q
			//---------------------------------------------------------------------
			for (j = 1; j <= lastcol-firstcol+1; j++) {
				z[j] = z[j] + alpha*p[j];
				r[j] = r[j] - alpha*q[j];
			}
			//---------------------------------------------------------------------
			//  rho = r.r
			//  Obtain the norm of r: First, sum squares of r elements locally...
			//---------------------------------------------------------------------
			rho0 = rho;
			rho = 0.0;
			for (j = 1; j <= lastcol-firstcol+1; j++) rho += r[j]*r[j];
			beta = rho / rho0;
			//---------------------------------------------------------------------
			//  p = r + beta*p
			//---------------------------------------------------------------------
			for (j = 1; j <= lastcol-firstcol+1; j++) {
				p[j] = r[j] + beta*p[j];
			}
		}
		//---------------------------------------------------------------------
		//  Compute residual norm explicitly: ||r|| = ||x - A.z||
		//  First, form A.z
		//  The partition submatrix-vector multiply
		//---------------------------------------------------------------------
		for (j = 1; j <= lastrow-firstrow+1; j++) {
			sum = 0.0;
			for (k = rowstr[j]; k <= rowstr[j+1]-1; k++) {
				sum += a[k]*z[colidx[k]];
			}
			r[j] = sum;
		}
		//---------------------------------------------------------------------
		//  At this point, r contains A.z
		//---------------------------------------------------------------------
		sum = 0.0;
		for (j = 1; j <= lastcol-firstcol+1; j++) sum += (x[j]-r[j])*(x[j]-r[j]);
		return Math.sqrt(sum);
	}

	public double endWork() {
		double sum;
		//---------------------------------------------------------------------
		//  Compute residual norm explicitly: ||r|| = ||x - A.z||
		//  First, form A.z
		//  The partition submatrix-vector multiply
		//---------------------------------------------------------------------
		for (int j = 1; j <= lastrow-firstrow+1; j++) {
			sum = 0.0;
			for (int k = rowstr[j]; k <= rowstr[j+1]-1; k++) {
				sum += a[k]*z[colidx[k]];
			}
			r[j] = sum;
		}
		//---------------------------------------------------------------------
		//  At this point, r contains A.z
		//---------------------------------------------------------------------
		sum = 0.0;
		for (int j = 1; j <= lastcol-firstcol+1; j++) sum += (x[j]-r[j])*(x[j]-r[j]);
		return Math.sqrt(sum);
	}

	private void PrintTimers() {
		DecimalFormat fmt = new DecimalFormat("0.000");
		System.out.println("  SECTION   Time (secs)");
		double ttot = timer.readTimer(t_bench);
		if (ttot == 0.0) ttot = 1.0;
		for (int i = 1; i <= t_last; i++) {
			double tm = timer.readTimer(i);
			if (i == t_init) {
				System.out.println("  "+t_names[i]+":"+fmt.format(tm));
			} else {
				System.out.println("  "+t_names[i]+":"+fmt.format(tm)
						+" ("+fmt.format(tm*100.0/ttot)+"%)");
				if (i == t_conj_grad) {
					tm = ttot - tm;
					System.out.println("    --> total rest :" + fmt.format(tm)
							+" ("+fmt.format(tm*100.0/ttot)+"%)");
				}
			}
		}
	}

	public double getTime() { return timer.readTimer(t_bench); }

	public void setupThreads() {
		worker = new CGWorker[num_threads];

		int div = na/num_threads;
		int rem = na%num_threads;
		int start = 1, end = 0;

		for (int i = 0; i<num_threads; i++) {
			end += div;
			if (rem != 0) {
				rem--;
				end++;
			}
			worker[i] = new CGWorker(i, start, end, a, colidx, rowstr,
					x, z, p, q, r,
					dmaster, rhomaster, rnormmaster);
			start = end+1;
		}
	}
}

