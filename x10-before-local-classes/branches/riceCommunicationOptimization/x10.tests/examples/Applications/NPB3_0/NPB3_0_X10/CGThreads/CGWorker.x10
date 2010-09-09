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
 !                           C G W o r k e r                               !
 !									  !
 !-------------------------------------------------------------------------!
 !									  !
 !    CGworker implements thread for sparse subroutine of CG benchmark.    !
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
 ! Translation to Java and to MultiThreaded Code				  !
 !	   M. Frumkin							  !
 !	   M. Schultz							  !
 !-------------------------------------------------------------------------!
 Translated to X10 by Vijay Saraswat
 */
package NPB3_0_X10.CGThreads;

import NPB3_0_X10.CG;

public class CGWorker {

	var id: int;

	var start1: int;
        var end1: int;

	var colidx: Rail[int];
        var rowstr: Rail[int];
	var a: Rail[double];
        var x: Rail[double];
        var z: Rail[double];
        var p: Rail[double];
        var q: Rail[double];
        var r: Rail[double];
	var dmaster: Rail[double];
        var rhomaster: Rail[double];
        var rnormmaster: Rail[double];

	public def this(var i: int, var st: int, var end: int, var ca: Rail[double], var ccol: Rail[int], var crow: Rail[int], var cx: Rail[double], var cz: Rail[double], var cp: Rail[double], var cq: Rail[double], var cr: Rail[double], var cdmaster: Rail[double], var crhomaster: Rail[double], var crnormmaster: Rail[double]): CGWorker = {
		dmaster = cdmaster;
		rhomaster = crhomaster;
		rnormmaster = crnormmaster;
		colidx = ccol;
		rowstr = crow;
		a = ca;
		p = cp;
		q = cq;
		r = cr;
		x = cx;
		z = cz;
		id = i;
		start1 = st;
		end1 = end;
	}

	public def step0(): void = {
		for (var j: int = start1; j <= end1; j++) {
			var sum: double = 0.0;
			for (var k: int = rowstr(j); k<rowstr(j+1); k++) {
				sum = sum + a(k)*p(colidx(k));
				if (colidx(k) == 0 && p(0) != 0.0) x10.io.Console.OUT.println("Phantom contribution!" + p(0));
			}
			q(j) = sum;
		}
		var sum: double = 0.0;
		for (var j: int = start1; j <= end1; j++) sum += p(j)*q(j);
		dmaster(id) = sum;
	}

	public def step1(var alpha: double): void = {
		for (var j: int = start1; j <= end1; j++) {
			z(j) = z(j) + alpha*p(j);
			r(j) = r(j) - alpha*q(j);
		}
		//---------------------------------------------------------------------
		//  rho = r.r
		//  Now, obtain the norm of r: First, sum squares of r elements locally...
		//---------------------------------------------------------------------
		var rho: double = 0.0;
		for (var j: int = start1; j <= end1; j++) rho += r(j)*r(j);
		// x10.io.Console.OUT.println("step1: rhomaster[" + id + "]=" + rho);
		rhomaster(id) = rho;
	}

	public def step2(var beta: double): void = {
		for (var j: int = start1; j <= end1; j++) p(j) = r(j)+beta*p(j);
	}

	public def step3(): void = {
		var rho: double = 0.0;
		for (var j: int = start1; j <= end1; j++) {
			q(j) = 0.0;
			z(j) = 0.0;
			r(j) = x(j);
			p(j) = x(j);
			rho += x(j)*x(j);
		}

		rhomaster(id) = rho;
	}

	public def endWork(): void = {
		//---------------------------------------------------------------------
		//  Compute residual norm explicitly:  ||r|| = ||x - A.z||
		//  First, form A.z
		//  The partition submatrix-vector multiply
		//---------------------------------------------------------------------
		for (var j: int = start1; j <= end1; j++) {
			var sum: double = 0.0;
			for (var k: int = rowstr(j); k <= rowstr(j+1)-1; k++) {
				sum += a(k)*z(colidx(k));
				if (colidx(k) == 0 && z(0) != 0.0)
					x10.io.Console.OUT.println("z[colidx[k]=0] = " + z(colidx(k)));
			}
			r(j) = sum;
		}
		//---------------------------------------------------------------------
		//  At this point, r contains A.z
		//---------------------------------------------------------------------
		var sum: double = 0.0;
		for (var j: int = start1; j <= end1; j++) sum += (x(j)-r(j))*(x(j)-r(j));
		rnormmaster(id) = sum;
	}
}
