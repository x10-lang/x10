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
!                              R A N D O M                                !
!                                                                         !
!-------------------------------------------------------------------------!
!                                                                         !
!    This benchmark is a serial version of the NPB3_0_JAV Random number   !
!    generating code.                                                     !
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
!     Translation to Java and to MultiThreaded Code:			  !
!     Michael A. Frumkin					          !
!-------------------------------------------------------------------------!
*/
package NPB3_0_X10;

public class Random {

	//default seed
	public var tran: double = 314159265.0;   //First 9 digits of PI
	//Random Number Multiplier
	public var amult: double = 1220703125.0; //= Math.pow(5.0, 13);
	public var KS: int = 0;
	public var R23: double;
        public var R46: double;
        public var T23: double;
        public var T46: double;
	//constants
	public const d2m46: double = Math.pow(0.5, 46);
	protected const i246m1: long = (long)Math.pow(2, 46)-1;

	public def this(): Random = { }
	public def this(var sd: double): Random = { seed = sd; }

	//Random number generator with an external seed
	public def randlc(var x: double, var a: double): double = {
		var y: Array[double];
                var r23: double;
                var r46: double;
                var t23: double;
                var t46: double;
                var t1: double;
                var t2: double;
                var t3: double;
                var t4: double;
                var a1: double;
                var a2: double;
                var x1: double;
                var x2: double;
                var z: double;
		r23 = Math.pow(0.5, 23);
		r46 = Math.pow(r23, 2);
		t23 = Math.pow(2.0, 23);
		t46 = Math.pow(t23, 2);
		//---------------------------------------------------------------------
		//   Break A into two parts such that A = 2^23 * A1 + A2.
		//---------------------------------------------------------------------
		t1 = r23 * a;
		a1 = (int) t1;
		a2 = a - t23 * a1;
		//---------------------------------------------------------------------
		//   Break X into two parts such that X = 2^23 * X1 + X2, compute
		//   Z = A1 * X2 + A2 * X1 (mod 2^23), and then
		//   X = 2^23 * Z + A2 * X2 (mod 2^46).
		//---------------------------------------------------------------------
		t1 = r23 * x;
		x1 = (int) t1;
		x2 = x - t23 * x1;
		t1 = a1 * x2 + a2 * x1;
		t2 = (int) (r23 * t1);
		z = t1 - t23 * t2;
		t3 = t23 * z + a2 * x2;
		t4 = (int) (r46 * t3);
		x = t3 - t46 * t4;
		return x;
	}

	//Random number generator with an internal seed
	public def randlc(var a: double): double = {
		var y: Array[double];
                var r23: double;
                var r46: double;
                var t23: double;
                var t46: double;
                var t1: double;
                var t2: double;
                var t3: double;
                var t4: double;
                var a1: double;
                var a2: double;
                var x1: double;
                var x2: double;
                var z: double;
		r23 = Math.pow(0.5, 23);
		r46 = Math.pow(r23, 2);
		t23 = Math.pow(2.0, 23);
		t46 = Math.pow(t23, 2);
		//---------------------------------------------------------------------
		//   Break A into two parts such that A = 2^23 * A1 + A2.
		//---------------------------------------------------------------------
		t1 = r23 * a;
		a1 = (int) t1;
		a2 = a - t23 * a1;
		//---------------------------------------------------------------------
		//   Break X into two parts such that X = 2^23 * X1 + X2, compute
		//   Z = A1 * X2 + A2 * X1 (mod 2^23), and then
		//   X = 2^23 * Z + A2 * X2 (mod 2^46).
		//---------------------------------------------------------------------
		t1 = r23 * tran;
		x1 = (int) t1;
		x2 = tran - t23 * x1;
		t1 = a1 * x2 + a2 * x1;
		t2 = (int) (r23 * t1);
		z = t1 - t23 * t2;
		t3 = t23 * z + a2 * x2;
		t4 = (int) (r46 * t3);
		tran = t3 - t46 * t4;
		return (r46 * tran);
	}

	public def vranlc(var n: double, var x: double, var a: double, var y: Array[double], var offset: int): double = {
		var Lx: long = (long)x;
		var La: long = (long)a;

		for (var i: int = 0; i<n; i++) {
			Lx   = (Lx*La) & (i246m1);
			y(offset+i) = (double)(d2m46* Lx);
		}
		return (double) Lx;
	}

	public var seed: double;
	public def ipow46(var a: double, var exponent: int): double = {
		var n: int;
                var n2: int;
		var q: double;
                var r: double;
		//---------------------------------------------------------------------
		// Use
		//   a^n = a^(n/2)*a^(n/2) if n even else
		//   a^n = a*a^(n-1)       if n odd
		//---------------------------------------------------------------------
		if (exponent == 0) return seed;
		q = a;
		r = 1;
		n = exponent;

		while (n>1) {
			n2 = n/2;
			if (n2*2 == n) {
				seed = randlc(q, q);
				q = seed;
				n = n2;
			} else {
				seed = randlc(r, q);
				r = seed;
				n = n-1;
			}
		}
		seed = randlc(r, q);
		return seed;
	}

	public def power(var a: double, var n: int): double = {
		//c---------------------------------------------------------------------
		//c     power  raises an integer, disguised as a double
		//c     precision real, to an integer power
		//c---------------------------------------------------------------------
		var aj: double;
                var ajj: double;
                var pow: double;
		var nj: int;

		pow = 1.0;
		nj = n;
		aj = a;
		while (nj != 0) {
			if (nj%2 == 1) {
				seed = randlc(pow, aj);
				pow = seed;
			}
			ajj = aj;
			seed = randlc(aj, ajj);
			aj = seed;
			nj = nj/2;
		}
		return pow;
	}
}
