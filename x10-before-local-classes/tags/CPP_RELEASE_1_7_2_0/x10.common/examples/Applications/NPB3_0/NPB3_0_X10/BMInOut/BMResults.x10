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

import java.io.*;
import java.text.*;
import java.lang.Exception;
import java.lang.Double;

public class BMResults implements Serializable {
	public String name;
	public String MachineName;
	public String PrLang;
	public char clss;
	public int n1, n2, n3, niter;
	public double time, acctime, wctime, mops;
	public double tmSent = 0.0, tmReceived = 0.0;
	public int RecArrSize = 0;
	public String optype;
	public int numthreads;
	public boolean serial;
	public int pid;
	public int verified;
	public nullable<BufferedWriter> out = null;

	public BMResults() { }

	public BMResults(int bid) {
		pid = bid;
		clss = 'S';
		optype = "floating point";
	}

	public BMResults(String bname,
			char   CLASS,
			int    bn1,
			int    bn2,
			int    bn3,
			int    bniter,
			double btime,
			double bmops,
			String boptype,
			int    passed_verification,
			boolean bserial,
			int num_threads,
			int bid)
	{
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

	public void print() {
		DecimalFormat fmt = new DecimalFormat("0.000");
		StringBuffer outbuf = new StringBuffer("                                "
				+"                               *");
		int len = outbuf.length();
		String outline = new String("***** NAS Parallel Benchmarks"+
				" X10 version (NPB3_0_X10) "+name+" ****");
		outbuf.insert(0, outline);
		outbuf.setLength(len);
		outbuf.insert(len-1,"*");
		System.out.println(outbuf.toString());

		outbuf = new StringBuffer("                          "
				+"                         *");
		outline = new String("* Class             = "+clss);
		outbuf.insert(0, outline);
		outbuf.setLength(len);
		outbuf.insert(len-1,"*");
		System.out.println(outbuf.toString());

		outbuf = new StringBuffer("                          "
				+"                         *");
		if (n2 == 0 && n3 == 0) {
			outline = new String("* Size              = "+n1);
		} else {
			outline = new String("* Size              = "+n1+" X "+n2+" X "+n3);
		}
		outbuf.insert(0, outline);
		outbuf.setLength(len);
		outbuf.insert(len-1,"*");
		System.out.println(outbuf.toString());

		outbuf = new StringBuffer("                          "
				+"                         *");
		outline = new String("* Iterations        = "+niter);
		outbuf.insert(0, outline);
		outbuf.setLength(len);
		outbuf.insert(len-1,"*");
		System.out.println(outbuf.toString());

		outbuf = new StringBuffer("                          "
				+"                         *");
		outline = new String("* Time in seconds   = "+fmt.format(time));
		outbuf.insert(0, outline);
		outbuf.setLength(len);
		outbuf.insert(len-1,"*");
		System.out.println(outbuf.toString());

		outbuf = new StringBuffer("                          "
				+"                         *");
		outline = new String("* ACCTime           = "+fmt.format(acctime));
		outbuf.insert(0, outline);
		outbuf.setLength(len);
		outbuf.insert(len-1,"*");
		System.out.println(outbuf.toString());

		outbuf = new StringBuffer("                          "
				+"                         *");
		outline = new String("* Mops total        = "+fmt.format(mops));
		outbuf.insert(0, outline);
		outbuf.setLength(len);
		outbuf.insert(len-1,"*");
		System.out.println(outbuf.toString());

		outbuf = new StringBuffer("                          "
				+"                         *");
		outline = new String("* Operation type    = "+optype);
		outbuf.insert(0, outline);
		outbuf.setLength(len);
		outbuf.insert(len-1,"*");
		System.out.println(outbuf.toString());

		outbuf = new StringBuffer("                          "
				+"                         *");
		if (verified == 1)outline = new String("* Verification      = Successful");
		else if (verified == 0)outline = new String("* Verification      = Failed");
		else outline = new String("* Verification      = Not Performed");
		outbuf.insert(0, outline);
		outbuf.setLength(len);
		outbuf.insert(len-1,"*");
		System.out.println(outbuf.toString());

		if (!serial) {
			outbuf = new StringBuffer("                          "
					+"                         *");
			outline = new String("* Threads requested = "+numthreads);
			outbuf.insert(0, outline);
			outbuf.setLength(len);
			outbuf.insert(len-1,"*");
			System.out.println(outbuf.toString());
		}

		outbuf = new StringBuffer("*                               "
				+"                               *");
		System.out.println(outbuf.toString());

		outbuf = new StringBuffer("                          "
				+"                         *");
		outline = new String("* Please send all errors/feedbacks to:");
		outbuf.insert(0, outline);
		outbuf.setLength(len);
		outbuf.insert(len-1,"*");
		System.out.println(outbuf.toString());

		outbuf = new StringBuffer("                          "
				+"                         *");
		outline = new String("* NPB Working Team");
		outbuf.insert(0, outline);
		outbuf.setLength(len);
		outbuf.insert(len-1,"*");
		System.out.println(outbuf.toString());

		outbuf = new StringBuffer("                          "
				+"                         *");
		outline = new String("* x10-dev@cs.purdue.edu");
		outbuf.insert(0, outline);
		outbuf.setLength(len);
		outbuf.insert(len-1,"*");
		System.out.println(outbuf.toString());

		outbuf = new StringBuffer("                          "
				+"                         *");
		outline = new String("********************************"
				+"*******************************");
		outbuf.insert(0, outline);
		outbuf.setLength(len);
		outbuf.insert(len-1,"*");
		System.out.println(outbuf.toString());

		if (out != null) {
			try {
				outline = "***** NAS Parallel Benchmarks X10 version (NPB3_0_X10) "
					+name+" Report *****";
				out.write(outline, 0, outline.length());
				out.newLine();
				outline = "Class           = "+String.valueOf(clss);
				out.write(outline, 0, outline.length());
				out.newLine();
				if (n2 == 0 && n3 == 0) {
					outline = "Size            = "+String.valueOf(n1);
				} else {
					outline = "Size            = "+String.valueOf(n1)+" X "+
						String.valueOf(n2)+" X "+
						String.valueOf(n3);
				}
				out.write(outline, 0, outline.length());
				out.newLine();
				outline = "Iterations      = "+String.valueOf(niter);
				out.write(outline, 0, outline.length());
				out.newLine();
				outline = "Time in seconds = "+String.valueOf(fmt.format(time));
				out.write(outline, 0, outline.length());
				out.newLine();
				outline = "ACCTime         = "+String.valueOf(fmt.format(acctime));
				out.write(outline, 0, outline.length());
				out.newLine();
				outline = "Mops total      = "+String.valueOf(fmt.format(mops));
				out.write(outline, 0, outline.length());
				out.newLine();
				outline = "Operation type  = "+String.valueOf(optype);
				out.write(outline, 0, outline.length());
				out.newLine();
				if (verified == 1)      outline = "Verification    = Successful";
				else if (verified == 0) outline = "Verification Failed";
				else                 outline = "Verification Not Performed";
				out.write(outline, 0, outline.length());
				out.newLine();

				outline = "\n Please send all errors/feedbacks to:";
				out.write(outline, 0, outline.length());
				out.newLine();
				outline = " NPB Working Team";
				out.write(outline, 0, outline.length());
				out.newLine();
				outline = " npb@nas.nasa.gov\n";
				out.write(outline, 0, outline.length());
				out.newLine();
				out.flush();
			} catch (Exception e) {
				System.err.println("Res.print: write file: "+e.toString());
			}
		}
	}

	public int getFromFile(String filename) {
		nullable<BufferedReader> in = null;
		verified =-1;
		try {
			in = new BufferedReader(new FileReader(filename));
		} catch (Exception e) {
			System.err.println("BMResults.getFromFile: filename "+e.toString());
			return 0;
		}
		nullable<String> line = new String();
		String keyword;
		int idx1;
		try {
			while ((line = in.readLine()) != null) {
				if (line.indexOf("Time in seconds =") >= 0) {
					keyword = new String("Time in seconds =");
					idx1 = line.indexOf(keyword);
					idx1 += keyword.length();
					Double dbl = new Double(line.substring(idx1));
					acctime = wctime = dbl.doubleValue();
				} else if (line.indexOf("Verification    =") >= 0) {
					verified = 0;
					if (line.indexOf("successful") >= 0&&line.indexOf("successful")<0
							||line.indexOf("SUCCESSFUL") >= 0&&line.indexOf("UNSUCCESSFUL")<0)
						verified = 1;
				} else if (line.indexOf("Mop/s total     =") >= 0) {
					keyword = new String("Mop/s total     =");
					idx1 = line.indexOf(keyword);
					idx1 += keyword.length();
					Double dbl = new Double(line.substring(idx1));
					mops = dbl.doubleValue();
				}
			}
		} catch (Exception e) {
			System.err.println("BMResults.getFromFile: "+e.toString());
			return 0;
		}
		//print();
		return 1;
	}

	public static void printVerificationStatus(char clss, int verified, String BMName) {
		if (clss == 'U'||verified == -1) {
			verified = -1;
			System.out.println(" Problem size unknown");
			System.out.println(BMName+"."+clss+": Verification Not Performed");
		} else if (verified == 1) {
			System.out.println(BMName+"."+clss+": Verification Successful");
		} else {
			System.out.println(BMName+"."+clss+": Verification Failed");
		}
	}

	public static int printComparisonStatus(char clss, int verified, double epsilon,
											double xcr[], double xcrref[], double xcrdif[])
	{
		for (int m = 0; m < xcr.length; m++) {
			if (clss == 'U') {
				System.out.println(m+". "+xcr[m]);
			} else {
				if (xcrdif[m] <= epsilon) {
					if (verified ==-1) verified = 1;
				} else {
					verified = 0;
					System.out.print("FAILURE: ");
				}
				System.out.println(m+". "+xcr[m]+" "+xcrref[m]+" "+xcrdif[m]);
			}
		}
		return verified;
	}

	public static int printComparisonStatus(char clss, int verified, double epsilon,
											double xcr, double xcrref, double xcrdif)
	{
		if (clss == 'U') {
			System.out.println(" "+xcr);
		} else {
			if (xcrdif <= epsilon) {
				if (verified ==-1) verified = 1;
			} else {
				verified = 0;
				System.out.print("FAILURE: ");
			}
			System.out.println(xcr+" "+xcrref+" "+xcrdif);
		}
		return verified;
	}
}

