package NPB3_0_X10;

/***************************************************************************************
               An X10 implementation of the NPB3.0 MG 3D Benchmark 

In this implementation, the grid is partitioned across places. It is used to study X10's 
expressiveness rather than to generate performance numbers. We use X10 VM's support for 
abstract performance metrics to study the potential performance impacts of fully expressing
the parallelism in the MG algorithm explicitly in X10.

Only the test problems of class S and W are defined here. However, others can be easily 
added. 

Note that the master thread is at the default place 0.
  
Related classes: MGOP, LevelData, Util, NPB3_0_X10.BMInOut.BMArgs (It is  
                 the X10 translation of the class BMInOut.BMArgs in Java NPB3.0.)

Date:   05/19/06
        07/31/06 for vs foreach. For unordered loops, make compiler/runtime decides how much 
        parallelism should be applied there?
        11/06/06 Add comments
        
Author: Tong Wen
        IBM Research
*****************************************************************************************/

import NPB3_0_X10.BMInOut.*;
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
	 
	    	}
	}
    
    public static void main(String[] a) {
    	BMArgs.ParseCmdLineArgs(a,BMName);
        char CLSS=BMArgs.CLASS.val;
        
    	final point [] minusOnePoints = new point [minusArrayLength];
    	final point [] plusOnePoints = new point [plusArrayLength];
    	final boolean isDistributed=true;
    	
    	int problemSize=0; int numberIterations=0; double reference2Norm=0;
    	switch (CLSS){
    	  case 'S':
    		problemSize=32; numberIterations=4; reference2Norm=5.30770700573E-5;
    		break;
    	  case 'W':
    		problemSize=64; numberIterations=40; reference2Norm=2.50391406439E-18;
    		break;
    	  default:
    		throw new Error("Class must be one of SW");  
    	  
    	}
	    populate(minusOnePoints, plusOnePoints, CLSS);
	    
	    LevelData u=new LevelData(problemSize, isDistributed); // the unknowns
	    LevelData v=new LevelData(problemSize, isDistributed); // the right-hand side
	    v.initialize(minusOnePoints, plusOnePoints);
	    MGOP MG=new MGOP(problemSize);
	    System.out.println("There are "+Util.N_PLACES+" places. Running problem of class "+CLSS);
	    Timer tmr=new Timer();int count=0;
	    tmr.start(count);
	    double res=0;
	    try{ 
	    	res=MG.MGSolve(u,v, numberIterations);	
		}catch(OutOfMemoryError e){
			BMArgs.outOfMemoryMessage();
			System.exit(0);
		}	
	    tmr.stop(count);
	    System.out.println("The reference 2norm is "+reference2Norm+". The difference is "+(res-reference2Norm));
	    System.out.println("Wall-clock time for MGSolve: "+tmr.readTimer(count)+"secs");
    }
}
