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
!                               B M A R G S                               !
!                                                                         !
!-------------------------------------------------------------------------!
!                                                                         !
!    BMArgs implements Command Line Benchmark Arguments class             !
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
Translation to X10
Vijay Saraswat
*/
package NPB3_0_X10.BMInOut;
import java.io.*;
import java.text.*;

public class BMArgs implements Serializable {

	public const CLASS: BoxedChar = new BoxedChar('U');
	public const num_threads: BoxedInt = new BoxedInt(4);
	public const serial: BoxedBoolean = new BoxedBoolean(true);

	public def this(): BMArgs = {
		CLASS.val = 'U';
		num_threads.val = 4;
		serial.val = true;
	}

	public static def ParseCmdLineArgs(argv: Rail[String], BMName: String): void = {
		for (var i: int = 0; i<argv.length; i++) {
			if (argv(i).equals("SERIAL")
					|| argv(i).equals("serial")
					|| argv(i).equals("-serial")
					|| argv(i).equals("-SERIAL")) {
				serial.val = true;
			} else if (argv(i).startsWith("class=")
					|| argv(i).startsWith("CLASS=")
					|| argv(i).startsWith("-class")
					|| argv(i).startsWith("-CLASS"))
			{
				if (argv(i).length()>6)
					CLASS.val = argv(i)(6).toUpperCase();
				if (CLASS.val != 'A' && CLASS.val != 'B' && CLASS.val != 'C' && CLASS.val != 'S' && CLASS.val != 'W') {
					x10.io.Console.OUT.println("classes allowed are A,B,C,W and S.");
					commandLineError(BMName);
				}
			} else if (argv(i).startsWith("np=")
					|| argv(i).startsWith("NP=")
					|| argv(i).startsWith("-NP")
					|| argv(i).startsWith("-np"))
			{
				try {
					if (argv(i).length()>3)
						num_threads.val = Int.parseInt(argv(i).substring(3));
					serial.val = false;
				} catch (e: Exception) {
					x10.io.Console.OUT.println("argument to " + argv(i).substring(0, 3)
							+" must be an integer.");
					commandLineError(BMName);
				}
			}
		}
	}

	public static def commandLineError(BMName: String): void = {
		x10.io.Console.OUT.println("synopsis: java "+BMName
				+" CLASS=[ABCWS] -serial [-NPnnn]");
		x10.io.Console.OUT.println("[ABCWS] is the size class \n"
				+"-serial specifies the serial version and\n"
				+"-NP specifies number of threads where nnn "
				+"is an integer");
		System.exit(1);
	}

	public static def outOfMemoryMessage(): void = {
		x10.io.Console.OUT.println("The java maximum heap size is "
				+"to small to run this benchmark class");
		x10.io.Console.OUT.println("To allocate more memory, use the -mxn option"
				+" where n is the number of bytes to be allocated");
	}

	public static def Banner(BMName: String, clss: char, serial: boolean, np: int): void = {
		x10.io.Console.OUT.println(" NAS Parallel Benchmarks X10 version (NPB3_0_X10)");
		if (serial) x10.io.Console.OUT.println(" Serial Version "+BMName+"."+clss);
		else x10.io.Console.OUT.println(" Multithreaded Version "+BMName+"."+clss+" np="+np);
	}
}
