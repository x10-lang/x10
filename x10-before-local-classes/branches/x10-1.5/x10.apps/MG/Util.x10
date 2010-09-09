package NPB3_0_X10;

/*****************************************************************************************
           For X10 implementations of the NPB3.0 Benchmarks 

Constants, flags, and widely used methods are contained in this class.

Author: Tong Wen, IBM Research
Date:   04/04/06
        11/06/06 clean up comments
        
        (C) Copyright IBM Corp. 2006
*****************************************************************************************/
final public class Util {
	/*For performance modeling. The abstract performance metrics are in cycles, that is,
	  relative to clock speed. However, the following parameters are in real measure. So 
	  the conversion has to be done. The formula can be found in MGOP for instance. Or 
	  one can just set those parameters in cycles directly.*/
	public final static boolean PERF_OUTPUT=true;
	public final static double CLOCK_SPEED=375; //MHZ
	public final static double FP_PERCLOCK=4;
	public final static double COMM_LATENCY=1; //us
	public final static double COMM_BANDWIDTH=2000; //MBytes/s
	public final static boolean IN_LOOP=false; //measure cost inside for/foreach loop. Less acurate due to rounding off to int.
	public final static boolean COMP_ONLY=false; //only measure computation cost
	public final static boolean COMM_ONLY=false; //only measure communication cost. if both are ture, then no one is measured.
	public final static int THREADS_PERFOREACH=1;
	
	public final static boolean OVERLAP_COMMANDCOMP=false; //overlap exchange and computation
	// if the above is true, EXCHAGE_AFTER must be set false
	public final static boolean EXCHANGE_AFTER=false; //update of ghost values in MG after or before computation. 
	/* P2SLEVEL is the level below which computation is done solely at place 0. 
	   The level id starts from 0. At level i, the size is 2^ix2^ix2^i. 
	   There is no computation at level 0.
	   P2SLEVEL should smaller than log2(problem size in one dimension).
	*/
	public final static int P2SLEVEL=1;
	/*N_PLACES<=2^(3*max(P2SLEVEL,1)). To make sure each place must have at least one element of the array.*/
	public final static int N_PLACES=place.MAX_PLACES; 
	public final static dist ALLPLACES=dist.factory.unique();
	/*points in the 3x3x3 stencil*/
	public final static point [] DIFF1={[1,0,0],[-1,0,0],[0,1,0],[0,-1,0],[0,0,1],[0,0,-1]};
	public final static point [] DIFF2={[1,1,0],[1,-1,0],[-1,1,0],[-1,-1,0], [1,0,1],[1,0,-1],[-1,0,1],[-1,0,-1],
	[0,1,1],[0,1,-1],[0,-1,1],[0,-1,-1]};
	public final static point [] DIFF3={[1,1,1],[1,1,-1], [1,-1,1],[1,-1,-1],[-1,1,1],[-1,1,-1], [-1,-1,1],[-1,-1,-1]};
	public final static region UNIT_CUBE = [0:1,0:1,0:1];
	public final static region [.] QREGIONS = new region value [UNIT_CUBE] (point p[a,b,c]) { return [0:a,0:b,0:c];};
	/*there are two faces of the problem domain in each space dimension*/
	public final static int LOW=0, HIGH=1;
	
	public static boolean powerOf2(int a_int){
		int i=(int)Math.abs(a_int);
		if (i==0) return false;
		else{
			if (i!=(pow2(log2(i)))) return false;
		}
		return true;
	}
	public static int log2(int a_int){
		return (int)(Math.log(a_int)/Math.log(2));
	}
	public static int pow2(int a_int){
		return (int)Math.pow(2,a_int);
	}
	public static region boundary(region a_R, int a_direction){
		return boundary(a_R, a_direction,[0,0,0]);
	}
	/*compute the boundary  at each face*/
	public static region boundary(region a_R, int a_direction, point a_padSize ){
		/*direction=+/-{1,2,3} for x,y,z directions. The negative value is for the low side*/
		int i=(int)Math.abs(a_direction);
		boolean IsHigh=(a_direction>0);
		point low,high;
		low=[a_R.rank(0).low(),a_R.rank(1).low(),a_R.rank(2).low()];
		high=[a_R.rank(0).high(),a_R.rank(1).high(),a_R.rank(2).high()];
		int r=a_R.rank;
		if (i<=r && r==3){
			if (IsHigh){
				return [(i==1?high[0]+1:low[0])-a_padSize[0]:(i==1?high[0]+1:high[0])+a_padSize[0],
				        (i==2?high[1]+1:low[1])-a_padSize[1]:(i==2?high[1]+1:high[1])+a_padSize[1],
				        (i==3?high[2]+1:low[2])-a_padSize[2]:(i==3?high[2]+1:high[2])+a_padSize[2]];
			}
			else{
				return [(i==1?low[0]-1:low[0])-a_padSize[0]:(i==1?low[0]-1:high[0])+a_padSize[0],
				        (i==2?low[1]-1:low[1])-a_padSize[1]:(i==2?low[1]-1:high[1])+a_padSize[1],
				        (i==3?low[2]-1:low[2])-a_padSize[2]:(i==3?low[2]-1:high[2])+a_padSize[2]];
			}
		}
		else{
			System.out.println("MG3TongValue1.boundary():Warning! invalid inputs!");
			return [0:-1,0:-1,0:-1];
		}
	}
	
	/*It is assumed that the arraycopy methods here are called from the place where a_dest resides.
	  The following methods should be replaced by the built-in array copy method when it is available*/ 
	public static void arraycopy(final double [.] a_dest, final double[.]  a_src){	
      	  final region R = ((region(:rank==a_dest.rank))a_src.region)&&((region(:rank==a_dest.rank))a_dest.region); 
      	  //finish foreach( point p : R){
      	  finish for( point p : R){	  
      	    a_dest[p]= future(a_src.distribution[p]) {a_src[p]}.force();
      	  }	  
      	  //for( point p : R) a_dest[p]=a_src[p]; //implicit syntax
	}
	public static void arraycopy(final double [.] a_dest, final region a_destR, final double [.] a_src){
  	  //final region R = a_dest.region&&a_destR&&a_src.region; 
  	  final region R=a_destR;
  	  //finish foreach( point p : R){
  	  /*finish for( point p : R){	 
  	    a_dest[p]= future(a_src.distribution[p]){a_src[p]}.force();
  	  }*/
  	  for( point p : a_destR) a_dest[p]= a_src[p]; //implicit syntax
	}
	public static void arraycopy(final double  [.] a_dest, final region a_destR, final double  [.] a_src, final point a_trans){
    	  //final region R = a_dest.region&&a_destR; 
    	  final region R=a_destR;
    	  /*using for loop here will result in segmentation fault. 
    	   The situiation is improved after update with CVS. But occasionally, say 1 out of 5 times, 
    	   running MGDriver gives the segmentaiton fault*/
    	  //finish foreach( point p : R){
    	  /*finish for( point p : R){
    		final point pt=p+a_trans;
    		a_dest[p]= future(a_src.distribution[pt]) {a_src[pt]}.force();
    	  }*/
    	  for (point p: R) a_dest[p]=a_src[p+a_trans]; //implicit syntax
	}
	/*It is assumed that the ArrayCopy method here are called from the place where a_src resides.
	  The following methods should be replaced by the built-in array copy method when it is available*/   
	public static void ArrayCopy(final double [.] a_dest, final region a_destR, final double [.] a_src){
	  	  //final region R = a_dest.region&&a_destR&&a_src.region; 
	  	  final region R=a_destR;
	  	  /*finish for( point p : R){
	  		  final double temp=a_src[p];
	  		  async (a_dest.distribution[p]){
	  			  a_dest[p]= temp;
	  		  }
	  	  }*/
    	  	for (point p: R) a_dest[p]=a_src[p]; //implicit syntax			  
	}
	
	public Util(){}
	
	    public static void main(String[] a) {
	    	/*region r=[1:3,1:3,2:4]; System.out.println(r.size());
	    	System.out.println("boundary of "+r+" at direciton 2 is"+Util.boundary(r,2)+" ALLPLACES="+ALLPLACES.toString());
	    	System.out.println("This is Util, finished!");
	    	final LevelData MG=new LevelData(32, true);
	    	MG.set(1);
	    	System.out.println("MG.initialize() finished");
	    	double [.] da=new double [[0:3]](point[i]){return i;};
	    	for (point [i]: da.region) System.out.println("["+i+"]"+da[i]);*/
	    	dist D=[0:0]->place.factory.place(0);
	    	finish ateach(point[i]: D){
	    		x10.lang.perf.addLocalOps(1);
	    	}
	    	finish ateach(point[i]: D){
	    		x10.lang.perf.addLocalOps(1);
	    	}
	    	System.out.println("This is Util");
	    }
}
