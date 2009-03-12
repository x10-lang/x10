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
 !                               M G B a s e                               !
 !                                                                         !
 !-------------------------------------------------------------------------!
 !                                                                         !
 !    MGBase implements base class for MG benchmark.                       !
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
 ! Translation to X10 by Vijay Saraswat
 ! No change needed from Java.
 */
package NPB3_0_X10.MGThreads;

import NPB3_0_X10.*;

public class MGBase {
	public const BMName: String = "MG";
	public const maxlevel: int = 11;
	public var nit: int;

	protected val nx_default: int;
        protected val ny_default: int;
        protected val nz_default: int;
	protected val nit_default: int;
        protected val lm: int;
        protected val lt_default: int;
	protected val ndim1: int;
        protected val ndim2: int;
        protected val ndim3: int;
	protected val nm: int;
        protected val nv: int;
        protected val nr: int;
        protected val nm2: int;
	protected val timer: Timer = new Timer();
	protected val nx: Rail[int] = new Rail[int](maxlevel);
        protected val ny: Rail[int] = new Rail[int](maxlevel);
        protected val nz: Rail[int] = new Rail[int](maxlevel);
        protected val ir: Rail[int] = new Rail[int](maxlevel);
        protected val m1: Rail[int] = new Rail[int](maxlevel);
        protected val m2: Rail[int] = new Rail[int](maxlevel);
        protected val m3: Rail[int] = new Rail[int](maxlevel);

	public var lt: int;
        public var lb: int;

	protected val u: Rail[double];
        protected val v: Rail[double];
        protected val r: Rail[double];
        protected val a: Rail[double];
        protected val c: Rail[double];

	protected const T_total: int = 0;
        protected const T_init: int = 1;
        protected const T_bench: int = 2;
        protected const T_mg3P: int = 3;
        protected const T_psinv: int = 4;
        protected const T_resid: int = 5;
        protected const T_resid2: int = 6;
        protected const T_rprj3: int = 7;
        protected const T_interp: int = 8;
        protected const T_norm2: int = 9;
        protected const T_last: int = 9;

	protected var timeron: boolean = false;
	protected var CLASS: char;

	public def this(var clss: char, var np: int, var serial: boolean): MGBase = {
		CLASS = clss;
		num_threads = np;
		switch (CLASS) {
			case 'S':case 'S':
				nx_default = ny_default = nz_default = 32;
				nit_default = 4;
				lm = 5;
				lt = lt_default = 5;
				ndim1 = ndim2 = ndim3 = 5;
				nit = nit_default;
				break;
			case 'W':case 'W':
				nx_default = ny_default = nz_default = 64;
				nit_default = 40; lm = 6;
				lt_default = 6;
				ndim1 = ndim2 = ndim3 = 6;
				break;
			case 'A':case 'A':
				nx_default = ny_default = nz_default = 256;
				nit_default = 4; lm = 8;
				lt_default = 8;
				ndim1 = ndim2 = ndim3 = 8;
				break;
			case 'B':case 'B':
				nx_default = ny_default = nz_default = 256;
				nit_default = 20; lm = 8;
				lt_default = 8;
				ndim1 = ndim2 = ndim3 = 8;
				break;
			case 'C':case 'C':
				nx_default = ny_default = nz_default = 512;
				nit_default = 20; lm = 9;
				lt_default = 9;
				ndim1 = ndim2 = ndim3 = 9;
				break;
			default:default:
				throw new Error("Class must be one of SWABC");
		}
		lt = lt_default;
		nit = nit_default;
		nx(lt-1) = nx_default;
		ny(lt-1) = ny_default;
		nz(lt-1) = nz_default;
		nm = 2+(1<<lm);
		nv = (2+(1<<ndim1))*(2+(1<<ndim2))*(2+(1<<ndim3));
		nr = (8*(nv+nm*nm+5*nm+7*lm))/7;
		nm2 = 2*nm*nm;

//		x10.io.Console.OUT.println(" Allocation of grids: nr="+nr+" words nv="+nv+" words");
//		x10.io.Console.OUT.print(" r="+(nr*8/(1024*1024))+" MB...");
		r = new Rail[double](nr);
//		x10.io.Console.OUT.println(" OK.");
//		x10.io.Console.OUT.print(" v="+(nv*8/(1024*1024))+" MB...");
		v = new Rail[double](nv);
//		x10.io.Console.OUT.println(" OK.");
//		x10.io.Console.OUT.print(" u="+(nr*8/(1024*1024))+" MB...");
		u = new Rail[double](nr);
//		x10.io.Console.OUT.println(" OK.");

		a = [ -8.0/3.0, 0.0, 1.0/6.0, 1.0/12.0 ];

		c = (CLASS == 'A'||CLASS == 'S'||CLASS == 'W')
			? [ -3.0/8.0, 1.0/32.0, -1.0/64.0, 0.0 ]
			: [ -3.0/17.0, 1.0/33.0, -1.0/61.0, 0.0 ];
	}

	public var num_threads: int = 0;
	public static def checksum(var arr: Rail[int], var name: String, var stop: boolean): void = {
		var csum: double = 0;
		for (var i: int = 0; i<arr.length; i++) csum += arr(i);
		x10.io.Console.OUT.println(name + " checksum MG " + csum);
		if (stop) System.exit(0);
	}
	public static def dmax1(var a: double, var b: double): double = { return (a < b) ? b : a; }

	public static def comm3(var u: Rail[double], var off: int, var n1: int, var n2: int, var n3: int): void = {
//		c---------------------------------------------------------------------
//		c     comm3 organizes the communication on all borders
//		c---------------------------------------------------------------------
//		double precision u(n1,n2,n3)

		for (val (i3,i2): Point in [1..n3-2, 1..n2-2]) {
			u(off+n1*(i2+n2*i3)) = u(off+n1-2+n1*(i2+n2*i3));
			u(off+n1-1+n1*(i2+n2*i3)) = u(off+1+n1*(i2+n2*i3));
		}

		for (val (i3,i1): Point in [1..n3-2, 0..n1-1]) {
			u(off+i1+n1*n2*i3) = u(off+i1+n1*(n2-2+n2*i3));
			u(off+i1+n1*(n2-1+n2*i3)) = u(off+i1+n1*(1+n2*i3));
		}

		for (val (i2,i1): Point in [0..n2-1, 0..n1-1]) {
			u(off+i1+n1*i2) = u(off+i1+n1*(i2+n2*(n3-2)));
			u(off+i1+n1*(i2+n2*(n3-1))) = u(off+i1+n1*(i2+n2));
		}
	}
}
