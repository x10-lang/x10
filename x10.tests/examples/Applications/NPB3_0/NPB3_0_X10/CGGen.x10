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
import x10.io.*;

public class CGGen {
	public const amult: double = 1220703125.0;
	val rng: Random;
	val firstrow: int;
        val lastrow: int;
        val firstcol: int;
        val lastcol: int;

	public def this(var firstrow1: int, var lastrow1: int, var firstcol1: int, var lastcol1: int, var rng1: Random): CGGen = {
		firstrow = firstrow1;
		lastrow = lastrow1;
		firstcol = firstcol1;
		lastcol = lastcol1;
		rng = rng1;
	}

	public def makea(var n: int, var nz: int, var a: Rail[double], var colidx: Rail[int], var rowstr: Rail[int], var nonzer: int, var rcond: double, var shift: double): void = {
		var iv: Rail[int] = new Rail[int](2*n+2);
		var v: Rail[double] = new Rail[double](n+2);
		var aelt: Rail[double] = new Rail[double](nz+1);
		var arow: Rail[int] = new Rail[int](nz+1);
		var acol: Rail[int] = new Rail[int](nz+1);

		if (false)
			x10.io.Console.OUT.println("na=" + n + " nz=" + nz + " a.length=" + a.length + " colidx.length="  + colidx.length
					+ " rowstr.length=" + rowstr.length + " nonzer=" + nonzer + " rcond=" + rcond
					+ " arow.length=" + arow.length + " acol.length=" + acol.length + " aelt.length=" + aelt.length
					+ " v.length=" + v.length + " iv.length=" + iv.length + " shift=" + shift
					+ " firstrow=" + firstrow + " lastrow=" + lastrow + " firstcol=" + firstcol + " lastcol=" + lastcol
					+ " rng=" + rng);
		if (false) {
			for (var i: int = 0; i < a.length; i++) if (a(i) != 0) x10.io.Console.OUT.println("a[" + i + "]=" + a(i));
			for (var i: int = 0; i < colidx.length; i++) if (colidx(i) != 0) x10.io.Console.OUT.println("colidx[" + i + "]=" + colidx(i));
			for (var i: int = 0; i < rowstr.length; i++) if (rowstr(i) != 0) x10.io.Console.OUT.println("rowstr[" + i + "]=" + rowstr(i));
			for (var i: int = 0; i < arow.length; i++) if (arow(i) != 0) x10.io.Console.OUT.println("arow[" + i + "]=" + arow(i));
			for (var i: int = 0; i < acol.length; i++) if (acol(i) != 0) x10.io.Console.OUT.println("acol[" + i + "]=" + acol(i));
			for (var i: int = 0; i < aelt.length; i++) if (aelt(i) != 0) x10.io.Console.OUT.println("aelt[" + i + "] = " + aelt(i));
			for (var i: int = 0; i < v.length; i++) if (v(i) != 0) x10.io.Console.OUT.println("v[" + i + "]=" + v(i));
			for (var i: int = 0; i < iv.length; i++) if (iv(i) != 0) x10.io.Console.OUT.println("iv[" + i + "]=" + iv(i));
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

		var irow: int;
                var nzv: int;
                var jcol: int;

		//---------------------------------------------------------------------
		//      nonzer is approximately (int(sqrt(nnza /n)));
		//---------------------------------------------------------------------

		var scale: double;

		var size: double = 1.0;
		var ratio: double = Math.pow(rcond , (1.0/(float)n));
		var nnza: int = 0;

		//---------------------------------------------------------------------
		//  Initialize colidx(n+1 .. 2n) to zero.
		//  Used by sprnvc to mark nonzero positions
		//---------------------------------------------------------------------

		for (var i: int = 1; i <= n; i++) {
			colidx(n+i) = 0;
		}
		for (var iouter: int = 1; iouter <= n; iouter++) {
			nzv = nonzer;
			sprnvc(n, nzv, v, iv, colidx, 0, colidx, n);
			nzv = vecset(n, v, iv, nzv, iouter, .5);

			for (var ivelt: int = 1; ivelt <= nzv; ivelt++) {
				jcol = iv(ivelt);
				if (jcol >= firstcol && jcol <= lastcol) {
					scale = size * v(ivelt);
					for (var ivelt1: int = 1; ivelt1 <= nzv; ivelt1++) {
						irow = iv(ivelt1);
						if (irow >= firstrow && irow <= lastrow) {
							nnza = nnza + 1;
							if (nnza > nz) {
								x10.io.Console.OUT.println("Space for matrix elements exceeded in makea");
								x10.io.Console.OUT.println("nnza, nzmax = " + nnza +", " + nz);
								x10.io.Console.OUT.println(" iouter = " + iouter);
								System.exit(0);
							}
							acol(nnza) = jcol;
							arow(nnza) = irow;
							aelt(nnza) = v(ivelt1) * scale;
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
		for (var i: int = firstrow; i <= lastrow; i++) {
			if (i >= firstcol && i <= lastcol) {
				var iouter: int = n + i;
				nnza = nnza + 1;
				if (nnza > nz) {
					x10.io.Console.OUT.println("Space for matrix elements exceeded in makea");
					x10.io.Console.OUT.println("nnza, nzmax = " + nnza +", " + nz);
					x10.io.Console.OUT.println(" iouter = " + iouter);
					System.exit(0);
				}
				acol(nnza) = i;
				arow(nnza) = i;
				aelt(nnza) = rcond - shift;
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
			x10.io.Console.OUT.print(" rowstr[" + ii + "]="+rowstr[ii]);
			if (ii % 10 == 0) x10.io.Console.OUT.println();
		}
		*/
		return;
	}

	public def sprnvc(var n: int, var nz: int, var v: Rail[double], var iv: Rail[int], var nzloc: Rail[int], var nzloc_offst: int, var mark: Rail[int], var mark_offst: int): void = {
		var nzrow: int = 0;
                var nzv: int = 0;
                var idx: int;
		var nn1: int = 1;

		while (true) {
			nn1 = 2 * nn1;
			if (nn1 >= n) break;
		}

		while (true) {
			if (nzv >= nz) {
				for (var ii: int = 1; ii <= nzrow; ii++) {
					idx = nzloc(ii+nzloc_offst);
					mark(idx+mark_offst) = 0;
				}
				return;
			}
			var vecelt: double = rng.randlc(amult);
			var vecloc: double = rng.randlc(amult);
			//	x10.io.Console.OUT.println("vecelt="  + vecelt + " vecloc = " + vecloc);
			idx = (int) (vecloc*nn1)+1;
			if (idx > n || idx == 0) continue; // vj .. added idx = 0

			if (mark(idx+mark_offst) == 0) {
				mark(idx+mark_offst) = 1;
				nzrow = nzrow + 1;
				nzloc(nzrow + nzloc_offst) = idx;
				nzv = nzv + 1;
				v(nzv) = vecelt;
				iv(nzv) = idx;
			}
		}
	}

	public def vecset(var n: int, var v: Rail[double], var iv: Rail[int], var nzv: int, var ival: int, var val: double): int = {
		var set: boolean = false;
		for (var k: int = 1; k <= nzv; k++) {
			if (iv(k) == ival) {
				v(k) = val;
				set = true;
			}
		}
		if (!set) {
			nzv     = nzv + 1;
			v(nzv)  = val;
			iv(nzv) = ival;
		}
		return nzv;
	}

	public def sparse(var a: Rail[double], var colidx: Rail[int], var rowstr: Rail[int], var n: int, var arow: Rail[int], var acol: Rail[int], var aelt: Rail[double], var x: Rail[double], var mark: Rail[int], var mark_offst: int, var nzloc: Rail[int], var nzloc_offst: int, var nnza: int): void = {
		//---------------------------------------------------------------------
		//       rows range from firstrow to lastrow
		//       the rowstr pointers are defined for nrows = lastrow-firstrow+1 values
		//---------------------------------------------------------------------
		var nrows: int;
		//---------------------------------------------------
		//       generate a sparse matrix from a list of
		//       [col, row, element] tri
		//---------------------------------------------------
		var i: int;
                var j: int;
                var jajp1: int;
                var nza: int;
                var k: int;
                var nzrow: int;
		var xi: double;
		//---------------------------------------------------------------------
		//    how many rows of result
		//---------------------------------------------------------------------
		nrows = lastrow - firstrow + 1;

		//---------------------------------------------------------------------
		//     ...count the number of triples in each row
		//---------------------------------------------------------------------
		for (j = 1; j <= n; j++) {
			rowstr(j) = 0;
			mark(j+mark_offst) = 0;
		}
		rowstr(n+1) = 0;

		for (nza = 1; nza <= nnza; nza++) {
			j = (arow(nza) - firstrow + 1)+1;
			rowstr(j) = rowstr(j) + 1;
		}

		rowstr(1) = 1;
		for (j = 2; j <= nrows+1; j++) {
			rowstr(j) = rowstr(j) + rowstr(j-1);
		}
		//---------------------------------------------------------------------
		//     ... rowstr(j) now is the location of the first nonzero
		//           of row j of a
		//---------------------------------------------------------------------

		//---------------------------------------------------------------------
		//     ... do a bucket sort of the triples on the row index
		//---------------------------------------------------------------------
		for (nza = 1; nza <= nnza; nza++) {
			j = arow(nza) - firstrow + 1;
			k = rowstr(j);
			a(k) = aelt(nza);
			colidx(k) = acol(nza);
			rowstr(j) = rowstr(j) + 1;
		}

		//---------------------------------------------------------------------
		//       ... rowstr(j) now points to the first element of row j+1
		//---------------------------------------------------------------------
		for (j = nrows-1; j >= 0; j--) {
			rowstr(j+1) = rowstr(j);
		}
		rowstr(1) = 1;
		//---------------------------------------------------------------------
		//       ... generate the actual output rows by adding elements
		//---------------------------------------------------------------------
		nza = 0;
		for (i = 1; i <= n; i++) {
			x(i) = 0.0;
			mark(i+mark_offst) = 0;
		}

		jajp1 = rowstr(1);

		for (j = 1; j <= nrows; j++) {
			nzrow = 0;

			//---------------------------------------------------------------------
			//          ...loop over the jth row of a
			//---------------------------------------------------------------------
			for (k = jajp1; k <= (rowstr(j+1)-1); k++) {
				i = colidx(k);
				x(i) = x(i) + a(k);
				if ((mark(i+mark_offst) == 0) && (x(i) != 0)) {
					mark(i+mark_offst) = 1;
					nzrow = nzrow + 1;
					nzloc(nzrow+nzloc_offst) = i;
				}
			}

			//---------------------------------------------------------------------
			//          ... extract the nonzeros of this row
			//---------------------------------------------------------------------
			for (k = 1; k <= nzrow; k++) {
				i = nzloc(k+nzloc_offst);
				mark(i+mark_offst) = 0;
				xi = x(i);
				x(i) = 0;
				if (xi != 0) {
					nza = nza + 1;
					a(nza) = xi;
					colidx(nza) = i;
				}
			}
			jajp1 = rowstr(j+1);
			rowstr(j+1) = nza + rowstr(1);
		}
		return;
	}
}
