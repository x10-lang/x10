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
Translated to X10 by vj 08/05

*/
package NPB3_0_X10;

import NPB3_0_X10.CGThreads.*;
import NPB3_0_X10.BMInOut.*;
import java.io.*;
import java.text.*;

public class CGGen {
	const double amult = 1220703125.0;
	final Random rng;
	final int firstrow, lastrow, firstcol, lastcol;

	public CGGen(int firstrow1, int lastrow1, int firstcol1, int lastcol1, Random rng1) {
		firstrow = firstrow1;
		lastrow = lastrow1;
		firstcol = firstcol1;
		lastcol = lastcol1;
		rng = rng1;
	}

	public void makea(int n, int nz, double[] a, int[] colidx, int[] rowstr,
					  int nonzer, double rcond, double shift)
	{
		int[] iv = new int[2*n+2];
		double[] v = new double[n+2];
		double[] aelt = new double[nz+1];
		int[] arow = new int[nz+1];
		int[] acol = new int[nz+1];

		if (false)
			System.out.println("na=" + n + " nz=" + nz + " a.length=" + a.length + " colidx.length="  + colidx.length
					+ " rowstr.length=" + rowstr.length + " nonzer=" + nonzer + " rcond=" + rcond
					+ " arow.length=" + arow.length + " acol.length=" + acol.length + " aelt.length=" + aelt.length
					+ " v.length=" + v.length + " iv.length=" + iv.length + " shift=" + shift
					+ " firstrow=" + firstrow + " lastrow=" + lastrow + " firstcol=" + firstcol + " lastcol=" + lastcol
					+ " rng=" + rng);
		if (false) {
			for (int i = 0; i < a.length; i++) if (a[i] != 0) System.out.println("a[" + i + "]=" + a[i]);
			for (int i = 0; i < colidx.length; i++) if (colidx[i] != 0) System.out.println("colidx[" + i + "]=" + colidx[i]);
			for (int i = 0; i < rowstr.length; i++) if (rowstr[i] != 0) System.out.println("rowstr[" + i + "]=" + rowstr[i]);
			for (int i = 0; i < arow.length; i++) if (arow[i] != 0) System.out.println("arow[" + i + "]=" + arow[i]);
			for (int i = 0; i < acol.length; i++) if (acol[i] != 0) System.out.println("acol[" + i + "]=" + acol[i]);
			for (int i = 0; i < aelt.length; i++) if (aelt[i] != 0) System.out.println("aelt[" + i + "] = " + aelt[i]);
			for (int i = 0; i < v.length; i++) if (v[i] != 0) System.out.println("v[" + i + "]=" + v[i]);
			for (int i = 0; i < iv.length; i++) if (iv[i] != 0) System.out.println("iv[" + i + "]=" + iv[i]);
		}

		//---------------------------------------------------------------------
		//       generate the test problem for benchmark 6
		//       makea generates a sparse matrix with a
		//       prescribed sparsity distribution
		//
		//       parameter    type        usage
		//
		//       input
		//
		//       n            i           number of cols/rows of matrix
		//       nz           i           nonzeros as declared array size
		//       rcond        r*8         condition number
		//       shift        r*8         main diagonal shift
		//
		//       output
		//
		//       a            r*8         array for nonzeros
		//       colidx       i           col indices
		//       rowstr       i           row pointers
		//
		//       workspace
		//
		//       iv, arow, acol i
		//       v, aelt        r*8
		//---------------------------------------------------------------------

		int irow, nzv, jcol;

		//---------------------------------------------------------------------
		//      nonzer is approximately (int(sqrt(nnza /n)));
		//---------------------------------------------------------------------

		double scale;

		double size = 1.0;
		double ratio = Math.pow(rcond , (1.0/(float)n));
		int nnza = 0;

		//---------------------------------------------------------------------
		//  Initialize colidx(n+1 .. 2n) to zero.
		//  Used by sprnvc to mark nonzero positions
		//---------------------------------------------------------------------

		for (int i = 1; i <= n; i++) {
			colidx[n+i] = 0;
		}
		for (int iouter = 1; iouter <= n; iouter++) {
			nzv = nonzer;
			sprnvc(n, nzv, v, iv, colidx, 0, colidx, n);
			nzv = vecset(n, v, iv, nzv, iouter, .5);

			for (int ivelt = 1; ivelt <= nzv; ivelt++) {
				jcol = iv[ivelt];
				if (jcol >= firstcol && jcol <= lastcol) {
					scale = size * v[ivelt];
					for (int ivelt1 = 1; ivelt1 <= nzv; ivelt1++) {
						irow = iv[ivelt1];
						if (irow >= firstrow && irow <= lastrow) {
							nnza = nnza + 1;
							if (nnza > nz) {
								System.out.println("Space for matrix elements exceeded in makea");
								System.out.println("nnza, nzmax = " + nnza +", " + nz);
								System.out.println(" iouter = " + iouter);
								System.exit(0);
							}
							acol[nnza] = jcol;
							arow[nnza] = irow;
							aelt[nnza] = v[ivelt1] * scale;
						}
					}
				}
			}
			size = size * ratio;
		}

		//---------------------------------------------------------------------
		//       ... add the identity * rcond to the generated matrix to bound
		//           the smallest eigenvalue from below by rcond
		//---------------------------------------------------------------------
		for (int i = firstrow; i <= lastrow; i++) {
			if (i >= firstcol && i <= lastcol) {
				int iouter = n + i;
				nnza = nnza + 1;
				if (nnza > nz) {
					System.out.println("Space for matrix elements exceeded in makea");
					System.out.println("nnza, nzmax = " + nnza +", " + nz);
					System.out.println(" iouter = " + iouter);
					System.exit(0);
				}
				acol[nnza] = i;
				arow[nnza] = i;
				aelt[nnza] = rcond - shift;
			}
		}

		//---------------------------------------------------------------------
		//       ... make the sparse matrix from list of elements with duplicates
		// (v and iv are used as  workspace)
		//---------------------------------------------------------------------

		sparse(a, colidx, rowstr, n, arow, acol, aelt,
				v, iv, 0 , iv, n, nnza);

		/*
		for (int ii = 0; ii < rowstr.length; ii++) {
			System.out.print(" rowstr[" + ii + "]="+rowstr[ii]);
			if (ii % 10 == 0) System.out.println();
		}
		*/
		return;
	}

	public void sprnvc(int n, int nz, double v[], int iv[], int nzloc[],
					   int nzloc_offst, int mark[], int mark_offst)
	{
		int nzrow = 0, nzv = 0, idx;
		int nn1 = 1;

		while (true) {
			nn1 = 2 * nn1;
			if (nn1 >= n) break;
		}

		while (true) {
			if (nzv >= nz) {
				for (int ii = 1; ii <= nzrow; ii++) {
					idx = nzloc[ii+nzloc_offst];
					mark[idx+mark_offst] = 0;
				}
				return;
			}
			double vecelt = rng.randlc(amult);
			double vecloc = rng.randlc(amult);
			//	System.out.println("vecelt="  + vecelt + " vecloc = " + vecloc);
			idx = (int) (vecloc*nn1)+1;
			if (idx > n || idx == 0) continue; // vj .. added idx = 0

			if (mark[idx+mark_offst] == 0) {
				mark[idx+mark_offst] = 1;
				nzrow = nzrow + 1;
				nzloc[nzrow + nzloc_offst] = idx;
				nzv = nzv + 1;
				v[nzv] = vecelt;
				iv[nzv] = idx;
			}
		}
	}

	public int vecset(int n, double v[], int iv[], int nzv, int ival, double val) {
		boolean set = false;
		for (int k = 1; k <= nzv; k++) {
			if (iv[k] == ival) {
				v[k] = val;
				set = true;
			}
		}
		if (!set) {
			nzv     = nzv + 1;
			v[nzv]  = val;
			iv[nzv] = ival;
		}
		return nzv;
	}

	public void sparse(double a[], int colidx[], int rowstr[],
					   int n, int arow[], int acol[],
					   double aelt[],
					   double x[], int mark[],
					   int mark_offst, int nzloc[], int nzloc_offst,
					   int nnza)
	{
		//---------------------------------------------------------------------
		//       rows range from firstrow to lastrow
		//       the rowstr pointers are defined for nrows = lastrow-firstrow+1 values
		//---------------------------------------------------------------------
		int nrows;
		//---------------------------------------------------
		//       generate a sparse matrix from a list of
		//       [col, row, element] tri
		//---------------------------------------------------
		int i, j, jajp1, nza, k, nzrow;
		double xi;
		//---------------------------------------------------------------------
		//    how many rows of result
		//---------------------------------------------------------------------
		nrows = lastrow - firstrow + 1;

		//---------------------------------------------------------------------
		//     ...count the number of triples in each row
		//---------------------------------------------------------------------
		for (j = 1; j <= n; j++) {
			rowstr[j] = 0;
			mark[j+mark_offst] = 0;
		}
		rowstr[n+1] = 0;

		for (nza = 1; nza <= nnza; nza++) {
			j = (arow[nza] - firstrow + 1)+1;
			rowstr[j] = rowstr[j] + 1;
		}

		rowstr[1] = 1;
		for (j = 2; j <= nrows+1; j++) {
			rowstr[j] = rowstr[j] + rowstr[j-1];
		}
		//---------------------------------------------------------------------
		//     ... rowstr(j) now is the location of the first nonzero
		//           of row j of a
		//---------------------------------------------------------------------

		//---------------------------------------------------------------------
		//     ... do a bucket sort of the triples on the row index
		//---------------------------------------------------------------------
		for (nza = 1; nza <= nnza; nza++) {
			j = arow[nza] - firstrow + 1;
			k = rowstr[j];
			a[k] = aelt[nza];
			colidx[k] = acol[nza];
			rowstr[j] = rowstr[j] + 1;
		}

		//---------------------------------------------------------------------
		//       ... rowstr(j) now points to the first element of row j+1
		//---------------------------------------------------------------------
		for (j = nrows-1; j >= 0; j--) {
			rowstr[j+1] = rowstr[j];
		}
		rowstr[1] = 1;
		//---------------------------------------------------------------------
		//       ... generate the actual output rows by adding elements
		//---------------------------------------------------------------------
		nza = 0;
		for (i = 1; i <= n; i++) {
			x[i] = 0.0;
			mark[i+mark_offst] = 0;
		}

		jajp1 = rowstr[1];

		for (j = 1; j <= nrows; j++) {
			nzrow = 0;

			//---------------------------------------------------------------------
			//          ...loop over the jth row of a
			//---------------------------------------------------------------------
			for (k = jajp1; k <= (rowstr[j+1]-1); k++) {
				i = colidx[k];
				x[i] = x[i] + a[k];
				if ((mark[i+mark_offst] == 0) && (x[i] != 0)) {
					mark[i+mark_offst] = 1;
					nzrow = nzrow + 1;
					nzloc[nzrow+nzloc_offst] = i;
				}
			}

			//---------------------------------------------------------------------
			//          ... extract the nonzeros of this row
			//---------------------------------------------------------------------
			for (k = 1; k <= nzrow; k++) {
				i = nzloc[k+nzloc_offst];
				mark[i+mark_offst] = 0;
				xi = x[i];
				x[i] = 0;
				if (xi != 0) {
					nza = nza + 1;
					a[nza] = xi;
					colidx[nza] = i;
				}
			}
			jajp1 = rowstr[j+1];
			rowstr[j+1] = nza + rowstr[1];
		}
		return;
	}
}

