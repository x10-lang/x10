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
 !                               P s i n v                                 !
 !                                                                         !
 !-------------------------------------------------------------------------!
 !                                                                         !
 !    Psinv implements thread for Psinv subroutine of MG benchmark.        !
 !                                                                        !
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
 ! Translation to Java and MultiThreaded Code                             !
 !         M. Frumkin                                                     !
 !         M. Schultz                                                     !
 !-------------------------------------------------------------------------!
 */
package NPB3_0_X10.MGThreads;

import NPB3_0_X10.MG;

public class Psinv extends MGWorker {
	val r1: Rail[double];
        val r2: Rail[double];

	public def this(var mg: MG, var i: int): Psinv = {
		super(mg, i);
		r1 = new Rail[double](nm+1);
		r2 = new Rail[double](nm+1);
	}

	public def step(var done: boolean, var wstart: int, var wend: int, var n1: int, var n2: int, var n3: int, var roff: int, var uoff: int): void = {

		getWork(wstart, wend); //1,n3?? or maybe 1,n3-1?
		if (work == 0) return;
		psinv(r, roff, u, uoff, n1, n2, n3, r1, r2, c, start, end+1);

		//		c---------------------------------------------------------------------
		//		c     exchange boundary points
		//		c---------------------------------------------------------------------
		//		x10.io.Console.OUT.println(id+" "+start+" "+end);
		for (var i3: int = start; i3 <= end; i3++)
			for (var i2: int = 1; i2<n2-1; i2++) {
				u(uoff+n1*(i2+n2*i3)) = u(uoff+n1-2+n1*(i2+n2*i3));
				u(uoff+n1-1+n1*(i2+n2*i3)) = u(uoff+1+n1*(i2+n2*i3));
			}

		for (var i3: int = start; i3 <= end; i3++)
			for (var i1: int = 0; i1<n1; i1++) {
				u(uoff+i1+n1*n2*i3) = u(uoff+i1+n1*(n2-2+n2*i3));
				u(uoff+i1+n1*(n2-1+n2*i3)) = u(uoff+i1+n1*(1+n2*i3));
			}
	}
	public static def psinv(var r: Rail[double], var roff: int, var u: Rail[double], var uoff: int, var n1: int, var n2: int, var n3: int, var r1: Rail[double], var r2: Rail[double], var c: Rail[double], var start: int, var end: int): void = {
		//		c---------------------------------------------------------------------
		//		c     psinv applies an approximate inverse as smoother:  u = u + Cr
		//		c
		//		c     This  implementation costs  15A + 4M per result, where
		//		c     A and M denote the costs of Addition and Multiplication.
		//		c     Presuming coefficient c(3) is zero (the NPB assumes this,
		//		c     but it is thus not a general case), 2A + 1M may be eliminated,
		//		c     resulting in 13A + 3M.
		//		c     Note that this vectorizes, and is also fine for cache
		//		c     based machines.
		//		c---------------------------------------------------------------------
		//		double precision u(n1,n2,n3),r(n1,n2,n3),c(0:3)

		for (val (i3,i2): Point in [start..end-1, 1..n2-2]) {
			for (val (i1): Point in [0..n1-1]) {
				r1(i1) = r(roff+i1+n1*(i2-1+n2*i3))
					+ r(roff+i1+n1*(i2+1+n2*i3))
					+ r(roff+i1+n1*(i2+n2*(i3-1)))
					+ r(roff+i1+n1*(i2+n2*(i3+1)));
				r2(i1) = r(roff+i1+n1*(i2-1+n2*(i3-1)))
					+ r(roff+i1+n1*(i2+1+n2*(i3-1)))
					+ r(roff+i1+n1*(i2-1+n2*(i3+1)))
					+ r(roff+i1+n1*(i2+1+n2*(i3+1)));
			}
			for (val (i1): Point in [1..n1-2]) {
				u(uoff+i1+n1*(i2+n2*i3)) +=
					c(0) * r(roff+i1+n1*(i2+n2*i3))
					+ c(1) * (r(roff+i1-1+n1*(i2+n2*i3))
							+ r(roff+i1+1+n1*(i2+n2*i3))
							+ r1(i1))
					+ c(2) * (r2(i1) + r1(i1-1) + r1(i1+1));
				//					c---------------------------------------------------------------------
				//					c  Assume c(3) = 0 (Enable line below if c(3) not = 0)
				//					c---------------------------------------------------------------------
				//					c    >                     + c(3) * (r2(i1-1) + r2(i1+1))
				//					c---------------------------------------------------------------------
			}
		}
	}

	//
}
