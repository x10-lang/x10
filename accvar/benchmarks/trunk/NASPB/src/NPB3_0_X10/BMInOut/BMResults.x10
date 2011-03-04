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
!                           B M R E S A L T S                             !
!                                                                         !
!-------------------------------------------------------------------------!
!                                                                         !
!    BMResults implements Benchmark Result class                          !
!									  !
!    Permission to use, copy, distribute and modify this software	  !
!    for any purpose with or without fee is hereby granted.  We 	  !
!    request, however, that all derived work reference the NAS  	  !
!    Parallel Benchmarks 3.0. This software is provided "as is" 	  !
!    without express or implied warranty.				  !
!									  !
!    Information on NPB 3.0, including the Technical Report NAS-02-008	  !
!    "Implementation of the NAS Parallel Benchmarks in Java",		  !
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
Ported to X10 by Vijay Saraswat
*/
package NPB3_0_X10.BMInOut;

import x10.io.*;
import x10.util.StringBuilder;


public class BMResults  {
	public var name: String;
	public var MachineName: String;
	public var PrLang: String;
	public var clss: char;
	public var n1: int;
	public var n2: int;
	public var n3: int;
	public var niter: int;
	public var time: double;
	public var acctime: double;
	public var wctime: double;
	public var mops: double;
	public var tmSent: double = 0.0;
	public var tmReceived: double = 0.0;
	public var RecArrSize: int = 0;
	public var optype: String;
	public var numthreads: int;
	public var serial: boolean;
	public var pid: int;
	public var verified: int;
	public var out: Printer = null;

	public def this(): BMResults = { }

	public def this(bid: int): BMResults = {
		pid = bid;
		clss = 'S';
		optype = "floating point";
	}

	public def this(bname: String, CLASS: char, bn1: int, bn2: int, bn3: int, bniter: int, 
			btime: double, bmops: double, boptype: String, 
			passed_verification: int, bserial: boolean, num_threads: int, 
			bid: int): BMResults = {
		pid = bid;
		name = bname;
		clss = CLASS;
		n1 = bn1;
		n2 = bn2;
		n3 = bn3;
		niter = bniter;
		time = btime;
		mops = bmops;
		optype = boptype;
		verified = passed_verification;
		serial = bserial;
		numthreads = num_threads;
	}

	public def print(): void = {
		val fmt = "%.3f";
		var outbuf:StringBuilder = new StringBuilder().add("                                "
				+"                               *");
		var len: int = outbuf.length();
		var outline: String = new String("***** NAS Parallel Benchmarks"+
				" X10 version (NPB3_0_X10) "+name+" ****");
		outbuf.insert(0, outline);
		;
		outbuf.insert(len-1,"*");
		x10.io.Console.OUT.println(outbuf.toString());

		outbuf = new StringBuilder().add("                          "
				+"                         *");
		outline = new String("* Class             = "+clss);
		outbuf.insert(0, outline);
		
		outbuf.insert(len-1,"*");
		x10.io.Console.OUT.println(outbuf.toString());

		outbuf = new StringBuilder().add("                          "
				+"                         *");
		if (n2 == 0 && n3 == 0) {
			outline = new String("* Size              = "+n1);
		} else {
			outline = new String("* Size              = "+n1+" X "+n2+" X "+n3);
		}
		outbuf.insert(0, outline);
		
		outbuf.insert(len-1,"*");
		x10.io.Console.OUT.println(outbuf.toString());

		outbuf = new StringBuilder().add("                          "
				+"                         *");
		outline = new String("* Iterations        = "+niter);
		outbuf.insert(0, outline);
		
		outbuf.insert(len-1,"*");
		x10.io.Console.OUT.println(outbuf.toString());

		outbuf = new StringBuilder().add("                          "
				+"                         *");
		outline = new String("* Time in seconds   = "+String.format(fmt, new Array[Any][time]));
		outbuf.insert(0, outline);
		
		outbuf.insert(len-1,"*");
		x10.io.Console.OUT.println(outbuf.toString());

		outbuf = new StringBuilder().add("                          "
				+"                         *");
		outline = new String("* ACCTime           = "+String.format(fmt, new Array[Any][acctime]));
		outbuf.insert(0, outline);
		
		outbuf.insert(len-1,"*");
		x10.io.Console.OUT.println(outbuf.toString());

		outbuf = new StringBuilder().add("                          "
				+"                         *");
		outline = new String("* Mops total        = "+String.format(fmt, new Array[Any][mops]));
		outbuf.insert(0, outline);
		
		outbuf.insert(len-1,"*");
		x10.io.Console.OUT.println(outbuf.toString());

		outbuf = new StringBuilder().add("                          "
				+"                         *");
		outline = new String("* Operation type    = "+optype);
		outbuf.insert(0, outline);
		
		outbuf.insert(len-1,"*");
		x10.io.Console.OUT.println(outbuf.toString());

		outbuf = new StringBuilder().add("                          "
				+"                         *");
		if (verified == 1)outline = new String("* Verification      = Successful");
		else if (verified == 0)outline = new String("* Verification      = Failed");
		else outline = new String("* Verification      = Not Performed");
		outbuf.insert(0, outline);
		
		outbuf.insert(len-1,"*");
		x10.io.Console.OUT.println(outbuf.toString());

		if (!serial) {
			outbuf = new StringBuilder().add("                          "
					+"                         *");
			outline = "* Threads requested = "+numthreads;
			outbuf.insert(0, outline);
			
			outbuf.insert(len-1,"*");
			x10.io.Console.OUT.println(outbuf.toString());
		}

		outbuf = new StringBuilder().add("*                               "
				+"                               *");
		x10.io.Console.OUT.println(outbuf.toString());

		outbuf = new StringBuilder().add("                          "
				+"                         *");
		outline = "* Please send all errors/feedbacks to:";
		outbuf.insert(0, outline);
		
		outbuf.insert(len-1,"*");
		x10.io.Console.OUT.println(outbuf.toString());

		outbuf = new StringBuilder().add("                          "
				+"                         *");
		outline = "* NPB Working Team";
		outbuf.insert(0, outline);
		
		outbuf.insert(len-1,"*");
		x10.io.Console.OUT.println(outbuf.toString());

		outbuf = new StringBuilder().add("                          "
				+"                         *");
		outline = "* x10-dev@cs.purdue.edu";
		outbuf.insert(0, outline);
		
		outbuf.insert(len-1,"*");
		x10.io.Console.OUT.println(outbuf.toString());

		outbuf = new StringBuilder().add("                          "
				+"                         *");
		outline = new String("********************************"
				+"*******************************");
		outbuf.insert(0, outline);
		
		outbuf.insert(len-1,"*");
		x10.io.Console.OUT.println(outbuf.toString());

		if (out != null) {
			try {
				outline = "***** NAS Parallel Benchmarks X10 version (NPB3_0_X10) "
					+name+" Report *****";
				out.println(outline);
			
				outline = "Class           = "+String.valueOf(clss);
				out.println(outline);
				
				if (n2 == 0 && n3 == 0) {
					outline = "Size            = "+String.valueOf(n1);
				} else {
					outline = "Size            = "+String.valueOf(n1)+" X "+
						String.valueOf(n2)+" X "+
						String.valueOf(n3);
				}
				out.println(outline);
			
				outline = "Iterations      = "+String.valueOf(niter);
				out.println(outline);
				
				outline = "Time in seconds = "+String.format(fmt, new Array[Any][time]);
				out.println(outline);
			
				outline = "ACCTime         = "+String.format(fmt, new Array[Any][acctime]);
				out.println(outline);
			
				outline = "Mops total      = "+String.format(fmt, new Array[Any][mops]);
				out.println(outline);
			
				outline = "Operation type  = "+String.valueOf(optype);
				out.println(outline);
				
				if (verified == 1)      outline = "Verification    = Successful";
				else if (verified == 0) outline = "Verification Failed";
				else                 outline = "Verification Not Performed";
				out.println(outline);
			

				outline = "\n Please send all errors/feedbacks to:";
				out.println(outline);
				
				outline = " NPB Working Team";
				out.println(outline );
			
				outline = " npb@nas.nasa.gov\n";
				out.println(outline);
				
				out.flush();
			} catch (e: Exception) {
				x10.io.Console.ERR.println("Res.print: write file: "+e.toString());
			}
		}
	}

	public def getFromFile(filename: String): int = {
		var inp: FileReader = null;
		verified =-1;
		try {
			inp = new FileReader(new File(filename));
		} catch (e: Exception) {
			x10.io.Console.ERR.println("BMResults.getFromFile: filename "+e.toString());
			return 0;
		}
		var line: String = "";
		var keyword: String;
		var idx1: int;
		try {
			while ((line = inp.readLine()) != null) {
				if (line.indexOf("Time in seconds =") >= 0) {
					keyword = new String("Time in seconds =");
					idx1 = line.indexOf(keyword);
					idx1 += keyword.length();
					var dbl: Double = Double.parseDouble(line.substring(idx1));
					acctime = wctime = dbl;
				} else if (line.indexOf("Verification    =") >= 0) {
					verified = 0;
					if (line.indexOf("successful") >= 0&&line.indexOf("successful")<0
							||line.indexOf("SUCCESSFUL") >= 0&&line.indexOf("UNSUCCESSFUL")<0)
						verified = 1;
				} else if (line.indexOf("Mop/s total     =") >= 0) {
					keyword = new String("Mop/s total     =");
					idx1 = line.indexOf(keyword);
					idx1 += keyword.length();
					var dbl: Double = Double.parseDouble(line.substring(idx1));
					mops = dbl;
				}
			}
		} catch (var e: Exception) {
			x10.io.Console.ERR.println("BMResults.getFromFile: "+e.toString());
			return 0;
		}
		//print();
		return 1;
	}

	public static def printVerificationStatus(var clss: char, var verified: int, var BMName: String): void = {
		if (clss == 'U'||verified == -1) {
			verified = -1;
			x10.io.Console.OUT.println(" Problem size unknown");
			x10.io.Console.OUT.println(BMName+"."+clss+": Verification Not Performed");
		} else if (verified == 1) {
			x10.io.Console.OUT.println(BMName+"."+clss+": Verification Successful");
		} else {
			x10.io.Console.OUT.println(BMName+"."+clss+": Verification Failed");
		}
	}

	public static def printComparisonStatus(var clss: char, var verified: int, var epsilon: double, var xcr: Rail[double], var xcrref: Rail[double], var xcrdif: Rail[double]): int = {
		for (var m: int = 0; m < xcr.length; m++) {
			if (clss == 'U') {
				x10.io.Console.OUT.println(m+". "+xcr(m));
			} else {
				if (xcrdif(m) <= epsilon) {
					if (verified ==-1) verified = 1;
				} else {
					verified = 0;
					x10.io.Console.OUT.print("FAILURE: ");
				}
				x10.io.Console.OUT.println(m+". "+xcr(m)+" "+xcrref(m)+" "+xcrdif(m));
			}
		}
		return verified;
	}

	public static def printComparisonStatus(clss: char, var verified: int, epsilon: double, xcr: double, xcrref: double, xcrdif: double): int = {
		if (clss == 'U') {
			x10.io.Console.OUT.println(" "+xcr);
		} else {
			if (xcrdif <= epsilon) {
				if (verified ==-1) verified = 1;
			} else {
				verified = 0;
				x10.io.Console.OUT.print("FAILURE: ");
			}
			x10.io.Console.OUT.println(xcr+" "+xcrref+" "+xcrdif);
		}
		return verified;
	}
}
