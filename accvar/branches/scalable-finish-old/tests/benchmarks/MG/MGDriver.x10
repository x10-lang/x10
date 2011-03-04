package NPB3_0_X10;

/***************************************************************************************
               An X10 implementation of the NPB3.0 MG 3D Benchmark 

In this implementation, the grid is partitioned across places. It is used to study X10's 
expressiveness rather than to generate performance numbers. Replacing the current arraycopy 
methods with the corresponding built-in ones (to be implemented) will certainly improve 
performance. 

We use X10's runtime support for abstract performance metrics to study the potential performance 
impacts of expressing fined-grained parallelism in the MG algorithm.

Note that the master thread is at the default place 0.
  
Related classes: MGOP, LevelData, Timer, Util 

Date:   05/19/06
        07/31/06 for vs foreach. For unordered loops, make compiler/runtime decides how much 
        parallelism should be applied there?
        11/06/06 Add comments
	12/07/2007 Add the rest of test problems
        
Author: Tong Wen
        IBM Research
        
        (C) Copyright IBM Corp. 2006
*****************************************************************************************/

//import NPB3_0_X10.BMInOut.*;
final public class MGDriver{
	public static final String BMName="MG_3D";
	static final int minusArrayLength = 10;
	static final int plusArrayLength = 10;

	static void populate(final point [] a_minusOnePoints, final point [] a_plusOnePoints, final char a_CLASS){
	    	switch (a_CLASS){
	    	case 'S':
	    	    a_minusOnePoints[0] = [0, 11, 2];
	    	    a_minusOnePoints[1] = [13, 8, 17];
	    	    a_minusOnePoints[2] = [5, 14, 0];
	    	    a_minusOnePoints[3] = [4, 28, 15];
	    	    a_minusOnePoints[4] = [12, 2, 1];
	    	    a_minusOnePoints[5] = [5, 17, 8];
	    	    a_minusOnePoints[6] = [20, 19, 11];
	    	    a_minusOnePoints[7] = [26, 15, 31];
	    	    a_minusOnePoints[8] = [8, 25, 22];
	    	    a_minusOnePoints[9] = [7, 14, 26];
	
	    	    a_plusOnePoints[0] = [7, 1, 20];
	    	    a_plusOnePoints[1] = [19, 29, 31];
	    	    a_plusOnePoints[2] = [2, 0, 3];
	    	    a_plusOnePoints[3] = [4, 22, 3];
	    	    a_plusOnePoints[4] = [1, 16, 21];
	    	    a_plusOnePoints[5] = [21, 31, 6];
	    	    a_plusOnePoints[6] = [12, 15, 12];
	    	    a_plusOnePoints[7] = [30, 4, 25];
	    	    a_plusOnePoints[8] = [28, 0, 28];
	    	    a_plusOnePoints[9] = [17, 26, 17];	    
	    	    break;
	    	case 'W':
	    	    a_minusOnePoints[0] = [38, 60, 51];
	    	    a_minusOnePoints[1] = [50, 15, 23];
	    	    a_minusOnePoints[2] = [18, 45, 36];
	    	    a_minusOnePoints[3] = [25, 14, 36];
	    	    a_minusOnePoints[4] = [26, 25, 25];
	    	    a_minusOnePoints[5] = [32, 37, 0];
	    	    a_minusOnePoints[6] = [29, 62, 54];
	    	    a_minusOnePoints[7] = [39, 49, 57];
	    	    a_minusOnePoints[8] = [12, 29, 28];
	    	    a_minusOnePoints[9] = [63, 46, 25];
	
	    	    a_plusOnePoints[0] = [27, 32, 45];
	    	    a_plusOnePoints[1] = [39, 0, 5];
	    	    a_plusOnePoints[2] = [45, 23, 49];
	    	    a_plusOnePoints[3] = [20, 32, 58];
	    	    a_plusOnePoints[4] = [23, 47, 57];
	    	    a_plusOnePoints[5] = [17, 43, 53];
	    	    a_plusOnePoints[6] = [8, 16, 48];
	    	    a_plusOnePoints[7] = [51, 46, 26];
	    	    a_plusOnePoints[8] = [58, 19, 62];
	    	    a_plusOnePoints[9] = [58, 15, 54];
	 	    break;
	case 'A':  case 'B':// class A and B
	    a_minusOnePoints[0] = [221, 40, 238];
	    a_minusOnePoints[1] = [152, 160, 34];
	    a_minusOnePoints[2] = [80, 182, 253];
	    a_minusOnePoints[3] = [248, 168, 155];
	    a_minusOnePoints[4] = [197, 5, 201];
	    a_minusOnePoints[5] = [90, 61, 203];
	    a_minusOnePoints[6] = [15, 203, 30];
	    a_minusOnePoints[7] = [99, 154, 57];
	    a_minusOnePoints[8] = [100, 136, 110];
	    a_minusOnePoints[9] = [209, 152, 96];

	    a_plusOnePoints[0] = [52, 207, 38];
	    a_plusOnePoints[1] = [241, 170, 12];
	    a_plusOnePoints[2] = [201, 16, 196];
	    a_plusOnePoints[3] = [200, 81, 207];
	    a_plusOnePoints[4] = [113, 121, 205];
	    a_plusOnePoints[5] = [210, 5, 246];
	    a_plusOnePoints[6] = [43, 192, 232];
	    a_plusOnePoints[7] = [174, 244, 162];
	    a_plusOnePoints[8] = [3, 116, 173];
	    a_plusOnePoints[9] = [55, 118, 165];
	    break;
	case 'C': // class C
	    a_minusOnePoints[0] = [397, 310, 198];
	    a_minusOnePoints[1] = [94, 399, 236];
	    a_minusOnePoints[2] = [221, 276, 59];
	    a_minusOnePoints[3] = [342, 137, 166];
	    a_minusOnePoints[4] = [381, 72, 281];
	    a_minusOnePoints[5] = [350, 192, 416];
	    a_minusOnePoints[6] = [16, 19, 455];
	    a_minusOnePoints[7] = [152, 336, 8];
	    a_minusOnePoints[8] = [400, 502, 447];
	    a_minusOnePoints[9] = [72, 0, 105];

	    a_plusOnePoints[0] = [308, 359, 9];
	    a_plusOnePoints[1] = [9, 491, 116];
	    a_plusOnePoints[2] = [449, 268, 441];
	    a_plusOnePoints[3] = [147, 115, 197];
	    a_plusOnePoints[4] = [241, 85, 3];
	    a_plusOnePoints[5] = [507, 41, 125];
	    a_plusOnePoints[6] = [161, 278, 73];
	    a_plusOnePoints[7] = [144, 91, 310];
	    a_plusOnePoints[8] = [201, 8, 49];
	    a_plusOnePoints[9] = [149, 399, 329];
	    break;
	case 'D': // class D
	    a_minusOnePoints[0] = [186, 374, 694];
	    a_minusOnePoints[1] = [773, 345, 474];
	    a_minusOnePoints[2] = [478, 874, 804];
	    a_minusOnePoints[3] = [306, 75, 624];
	    a_minusOnePoints[4] = [397, 667, 49];
	    a_minusOnePoints[5] = [606, 199, 59];
	    a_minusOnePoints[6] = [892, 70, 361];
	    a_minusOnePoints[7] = [844, 261, 252];
	    a_minusOnePoints[8] = [221, 906, 14];
	    a_minusOnePoints[9] = [85, 327, 232];

	    a_plusOnePoints[0] = [739, 879, 781];
	    a_plusOnePoints[1] = [742, 641, 147];
	    a_plusOnePoints[2] = [335, 295, 600];
	    a_plusOnePoints[3] = [982, 944, 696];
	    a_plusOnePoints[4] = [622, 881, 180];
	    a_plusOnePoints[5] = [956, 217, 952];
	    a_plusOnePoints[6] = [777, 453, 706];
	    a_plusOnePoints[7] = [258, 730, 482];
	    a_plusOnePoints[8] = [271, 75, 815];
	    a_plusOnePoints[9] = [78, 276, 250];
	    break;
	    }
	}
    
    public static void main(String[] args) {
    	//BMArgs.ParseCmdLineArgs(a,BMName);
        //char CLSS=BMArgs.CLASS.val;
        char CLASS = 'S';
    	for (int q = 0; q < args.length; ++q) {
			if (args[q].equals("-s") || args[q].equals("-S")) {
				CLASS = 'S';
			}
			if (args[q].equals("-w") || args[q].equals("-W")) {
				CLASS = 'W';
			}
			if (args[q].equals("-a") || args[q].equals("-A")) {
				CLASS = 'A';
			}
			if (args[q].equals("-b") || args[q].equals("-B")) {
				CLASS = 'B';
			}
			if (args[q].equals("-c") || args[q].equals("-C")) {
				CLASS = 'C';
			}
			if (args[q].equals("-c") || args[q].equals("-C")) {
				CLASS = 'D';
			}
	}
    	final point [] minusOnePoints = new point [minusArrayLength];
    	final point [] plusOnePoints = new point [plusArrayLength];
    	final boolean isDistributed=true;
    	
    	int problemSize=0; int numberIterations=0; double reference2Norm=0;
    	switch (CLASS){
    	  case 'S':
    		problemSize=32; numberIterations=4; reference2Norm=5.30770700573E-5;
    		break;
    	  case 'W':
    		problemSize=64; numberIterations=40; reference2Norm=2.50391406439E-18;
    		break;
	case 'A':
	    problemSize=256; numberIterations=4; reference2Norm=2.433365309e-6;
	    break;
	case 'B':
	    problemSize=256;numberIterations = 20;  reference2Norm = 1.80056440132e-6;
	    break;
	case 'C':
	    problemSize=512; numberIterations = 20; reference2Norm = 5.70674826298e-7;
	    break;
	case 'D':
	    problemSize=1024; numberIterations = 50; reference2Norm = 1.58327506043e-10;
	    break;	
    	  default:
    		throw new Error("Class must be one of {S,W,A,B,C,D}!");  
    	  
    	}
	    populate(minusOnePoints, plusOnePoints, CLASS);
	    
	    LevelData u=new LevelData(problemSize, isDistributed); // the unknowns
	    LevelData v=new LevelData(problemSize, isDistributed); // the right-hand side
	    v.initialize(minusOnePoints, plusOnePoints);
	    MGOP MG=new MGOP(problemSize);
	    System.out.println("There are "+Util.N_PLACES+" places. Running problem of class "+CLASS);
	    Timer tmr=new Timer();int count=0;
	    tmr.start(count);
	    double res=0;
	    try{ 
	    	res=MG.MGSolve(u,v, numberIterations);	
		}catch(OutOfMemoryError e){
			//BMArgs.outOfMemoryMessage();
			System.err.println("out Of Memory!");
			System.exit(0);
		}	
	    tmr.stop(count);
	    System.out.println("The reference 2norm is "+reference2Norm+". The difference is "+(res-reference2Norm));
	    System.out.println("Wall-clock time for MGSolve: "+tmr.readTimer(count)+"secs");
    }
}
