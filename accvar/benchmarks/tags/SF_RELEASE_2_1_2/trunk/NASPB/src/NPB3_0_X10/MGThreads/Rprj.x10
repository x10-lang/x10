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
 !                               R p r j                                   !
 !                                                                         !
 !-------------------------------------------------------------------------!
 !                                                                         !
 !    Rprj implements thread for Rprj subroutine of MG benchmark.          !
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

public class Rprj extends MGWorker {
	private val x1: Array[Double](1){rail};
        private val y1: Array[Double](1){rail};

	public def this( mg: MG,  i: int): Rprj = {
		super(mg, i);
		x1 = new Array[double](nm+1);
		y1 = new Array[double](nm+1);
	}

	public def step( done: boolean,  wstart: int,  wend: int, 
			 m1k: int,  m2k: int,  m3k: int, 
			 m1j: int,  m2j: int,  m3j: int, 
			 roff: int,  zoff: int): void = {
		getWork(wstart, wend); // not wend?? vj,m3j?
		if (work == 0) return;
		 r: Array[Double](1){rail} = mg.r;
		rprj3(r, roff, m1k, m2k, m3k, zoff, m1j, m2j, m3j, x1, y1, start, end);

		for ([j3,j2] in (start-1..(end-1))*(1..(m2j-1))) {
			r(zoff+m1j*(j2+m2j*j3)) = r(zoff+m1j-2+m1j*(j2+m2j*j3));
			r(zoff+m1j-1+m1j*(j2+m2j*j3)) = r(zoff+1+m1j*(j2+m2j*j3));
		}

		for ([j3,j1] in (start-1..(end-1))*(0..m1j)) {
			r(zoff+j1+m1j*m2j*j3) = r(zoff+j1+m1j*(m2j-2+m2j*j3));
			r(zoff+j1+m1j*(m2j-1+m2j*j3)) = r(zoff+j1+m1j*(1+m2j*j3));
		}
	}
	public static def rprj3( r: Array[Double](1){rail}, 
			 roff: int,  m1k: int,  m2k: int, 
			 m3k: int, var zoff: int, var m1j: int, 
			var m2j: int, var m3j: int, 
			var x1: Array[Double](1){rail}, 
			var y1: Array[Double](1){rail}, var start: int, 
			var end: int): void = {
		val d1: int = (m1k == 3) ? 2 : 1;
		val d2: int = (m2k == 3) ? 2 : 1;
		val d3: int = (m3k == 3) ? 2 : 1;

		for ([j3] in start..end) {
			val i3 = 2*j3-d3-1;
			for ([j2] in 2..(m2j-1)) {
				val i2: int = 2*j2-d2-1;
				for ([j1] in 2..m1j) {
					val i1: int = 2*j1-d1-1;
					x1(i1-1) = r(roff+i1-1+m1k*(i2-1+m2k*i3))
						+ r(roff+i1-1+m1k*(i2+1+m2k*i3))
						+ r(roff+i1-1+m1k*(i2+m2k*(i3-1)))
						+ r(roff+i1-1+m1k*(i2+m2k*(i3+1)));
					y1(i1-1) = r(roff+i1-1+m1k*(i2-1+m2k*(i3-1)))
						+ r(roff+i1-1+m1k*(i2-1+m2k*(i3+1)))
						+ r(roff+i1-1+m1k*(i2+1+m2k*(i3-1)))
						+ r(roff+i1-1+m1k*(i2+1+m2k*(i3+1)));
				}

				for ([j1] in (2..(m1j-1))) {
					val i1: int = 2*j1-d1-1;
					var y2: double = r(roff+i1+m1k*(i2-1+m2k*(i3-1)))
						+r(roff+i1+m1k*(i2-1+m2k*(i3+1)))
						+ r(roff+i1+m1k*(i2+1+m2k*(i3-1)))
						+r(roff+i1+m1k*(i2+1+m2k*(i3+1)));
					var x2: double = r(roff+i1+m1k*(i2-1+m2k*i3))
						+ r(roff+i1+m1k*(i2+1+m2k*i3))
						+ r(roff+i1+m1k*(i2+m2k*(i3-1)))
						+ r(roff+i1+m1k*(i2+m2k*(i3+1)));
					r(zoff+j1-1+m1j*(j2-1+m2j*(j3-1))) =
						0.5 * r(roff+i1+m1k*(i2+m2k*i3))
						+ 0.25 * (r(roff+i1-1+m1k*(i2+m2k*i3))+r(roff+i1+1+m1k*(i2+m2k*i3))+x2)
						+ 0.125 * (x1(i1-1) + x1(i1+1) + y2)
						+ 0.0625 * (y1(i1-1) + y1(i1+1));
				}
			}
		}
	}
}
