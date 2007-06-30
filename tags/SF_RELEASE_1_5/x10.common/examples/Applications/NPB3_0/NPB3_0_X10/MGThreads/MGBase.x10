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
	public static final String BMName = "MG";
	public static final int maxlevel = 11;
	public int nit;

	final protected int nx_default, ny_default, nz_default;
	final protected int nit_default, lm, lt_default;
	final protected int ndim1, ndim2, ndim3;
	final protected int nm, nv, nr, nm2;
	final protected Timer timer = new Timer();
	final protected int[]
		nx = new int[maxlevel],
		   ny = new int[maxlevel],
		   nz = new int[maxlevel],
		   ir = new int[maxlevel],
		   m1 = new int[maxlevel],
		   m2 = new int[maxlevel],
		   m3 = new int[maxlevel];

	public int lt, lb;

	final protected double u[], v[], r[], a[], c[];

	final protected static  int
		T_total = 0, T_init = 1, T_bench = 2, T_mg3P = 3,
		T_psinv = 4, T_resid = 5, T_resid2 = 6, T_rprj3 = 7,
		T_interp = 8, T_norm2 = 9, T_last = 9;

	protected boolean timeron = false;
	protected char CLASS;

	public MGBase(char clss, int np, boolean serial) {
		CLASS = clss;
		num_threads = np;
		switch (CLASS) {
			case 'S':
				nx_default = ny_default = nz_default = 32;
				nit_default = 4;
				lm = 5;
				lt = lt_default = 5;
				ndim1 = ndim2 = ndim3 = 5;
				nit = nit_default;
				break;
			case 'W':
				nx_default = ny_default = nz_default = 64;
				nit_default = 40; lm = 6;
				lt_default = 6;
				ndim1 = ndim2 = ndim3 = 6;
				break;
			case 'A':
				nx_default = ny_default = nz_default = 256;
				nit_default = 4; lm = 8;
				lt_default = 8;
				ndim1 = ndim2 = ndim3 = 8;
				break;
			case 'B':
				nx_default = ny_default = nz_default = 256;
				nit_default = 20; lm = 8;
				lt_default = 8;
				ndim1 = ndim2 = ndim3 = 8;
				break;
			case 'C':
				nx_default = ny_default = nz_default = 512;
				nit_default = 20; lm = 9;
				lt_default = 9;
				ndim1 = ndim2 = ndim3 = 9;
				break;
			default:
				throw new Error("Class must be one of SWABC");
		}
		lt = lt_default;
		nit = nit_default;
		nx[lt-1] = nx_default;
		ny[lt-1] = ny_default;
		nz[lt-1] = nz_default;
		nm = 2+(1<<lm);
		nv = (2+(1<<ndim1))*(2+(1<<ndim2))*(2+(1<<ndim3));
		nr = (8*(nv+nm*nm+5*nm+7*lm))/7;
		nm2 = 2*nm*nm;

//		System.out.println(" Allocation of grids: nr="+nr+" words nv="+nv+" words");
//		System.out.print(" r="+(nr*8/(1024*1024))+" MB...");
		r = new double[nr];
//		System.out.println(" OK.");
//		System.out.print(" v="+(nv*8/(1024*1024))+" MB...");
		v = new double[nv];
//		System.out.println(" OK.");
//		System.out.print(" u="+(nr*8/(1024*1024))+" MB...");
		u = new double[nr];
//		System.out.println(" OK.");

		a = new double[] { -8.0/3.0, 0.0, 1.0/6.0, 1.0/12.0 };

		c = (CLASS == 'A'||CLASS == 'S'||CLASS == 'W')
			? new double[] { -3.0/8.0, 1.0/32.0, -1.0/64.0, 0.0 }
			: new double[] { -3.0/17.0, 1.0/33.0, -1.0/61.0, 0.0 };
	}

	public int num_threads = 0;
	public static void checksum(int arr[], String name, boolean stop) {
		double csum = 0;
		for (int i = 0; i<arr.length; i++) csum += arr[i];
		System.out.println(name + " checksum MG " + csum);
		if (stop) System.exit(0);
	}
	public static double dmax1(double a, double b) { return (a < b) ? b : a; }

	public static void comm3(double u[], int off, int n1, int n2, int n3) {
//		c---------------------------------------------------------------------
//		c     comm3 organizes the communication on all borders
//		c---------------------------------------------------------------------
//		double precision u(n1,n2,n3)

		for (point [i3,i2]: [1:n3-2,1:n2-2]) {
			u[off+n1*(i2+n2*i3)] = u[off+n1-2+n1*(i2+n2*i3)];
			u[off+n1-1+n1*(i2+n2*i3)] = u[off+1+n1*(i2+n2*i3)];
		}

		for (point [i3,i1]: [1:n3-2,0:n1-1]) {
			u[off+i1+n1*n2*i3] = u[off+i1+n1*(n2-2+n2*i3)];
			u[off+i1+n1*(n2-1+n2*i3)] = u[off+i1+n1*(1+n2*i3)];
		}

		for (point [i2,i1]: [0:n2-1,0:n1-1]) {
			u[off+i1+n1*i2] = u[off+i1+n1*(i2+n2*(n3-2))];
			u[off+i1+n1*(i2+n2*(n3-1))] = u[off+i1+n1*(i2+n2)];
		}
	}
}

