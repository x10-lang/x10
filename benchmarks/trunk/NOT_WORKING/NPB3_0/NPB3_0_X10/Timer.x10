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
!                               T I M E R                                 !
!                                                                         !
!-------------------------------------------------------------------------!
!                                                                         !
!    This benchmark is a serial version of the NPB3_0_JAV  Timer code.    !      !									  !
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
!     Mathew Schultz	   					          !
!-------------------------------------------------------------------------!
*/
package NPB3_0_X10;

public class Timer {
	public const max_counters: int = 64;
	var start_time: Rail[double] = new Rail[double](max_counters);
	var elapsed_time: Rail[double] = new Rail[double](max_counters);
	var total_time: Rail[double] = new Rail[double](max_counters);

	public def Timer(): void = {
		for (var i: int = 0; i < max_counters; i++) {
			start_time(i) = 0;
			elapsed_time(i) = 0;
			total_time(i) = 0;
		}
	}

	public def start(var n: int): void = {
		start_time(n) = System.currentTimeMillis();
	}

	public def stop(var n: int): void = {
		elapsed_time(n) = System.currentTimeMillis()-start_time(n);
		elapsed_time(n) /= 1000;
		total_time(n) += elapsed_time(n);
	}

	public def readTimer(var n: int): double = {
		return total_time(n);
	}

	public def resetTimer(var n: int): void = {
		elapsed_time(n) = start_time(n) = total_time(n) = 0;
	}

	public def resetAllTimers(): void = {
		for (var i: int = 0; i < max_counters; i++) resetTimer(i);
	}
}
