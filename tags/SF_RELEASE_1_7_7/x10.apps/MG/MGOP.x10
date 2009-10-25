package NPB3_0_X10;

/***************************************************************************************
                 An X10 implementation of the NPB3.0 MG 3D Benchmark 

This class implements the Poisson solver using the multigrid algorithm. For experimental 
purposes, different versions of those stencil operators are presented. The choice for 
overlapping computations and communications can be made by setting the corresponding flag 
in class Util. To fully express the concurrency in the algorithm, foreach loop should be 
used to iterate over local arrays at each place. In the prototype implementation of x10, 
doing so might not be optimal (too many threads) compared with using for loop. However, the
production version of X10 compiler and runtime should be responsible for optimizing foreach 
loops based on the available resources.

Abstract performance metrics (in terms of cycles) are inserted. Flags in class Util can be set 
to determine which part of them are output.

Related classes: MGDriver, LevelData, Util

Author: Tong Wen, IBM Research

Date:   04/04/06
        05/19/06 Correction bugs are fixed in applyOpP and applyOpQ
        11/06/06 Clean up comments
        
        (C) Copyright IBM Corp. 2006
**************************************************************************************/

final public class MGOP {
	/*The abstract peformance metrics are in cycles. 
	  An integer operation is assumed to be as half expensive as a Flop.*/
	public final static boolean PERF_On=Util.PERF_OUTPUT;
	public final static double PERF_Comp=1.0/Util.FP_PERCLOCK;
	public final static double PERF_CommL=Util.COMM_LATENCY*Util.CLOCK_SPEED;
	public final static double PERF_CommB=((double)Util.CLOCK_SPEED/(double)Util.COMM_BANDWIDTH)*8.0;
	public final static boolean PERF_InLoop=Util.IN_LOOP;
	public final static boolean PERF_CompOnly=Util.COMP_ONLY;
	public final static boolean PERF_CommOnly=Util.COMM_ONLY;
	public final static boolean OVERLAPPED=Util.OVERLAP_COMMANDCOMP;
	//public final static boolean EXCHANGE_After=(OVERLAPPED?false:Util.EXCHANGE_AFTER); //complained by dep type
	public final  boolean EXCHANGE_After;
	/*coefficients of the stencil operators*/
	public final static double [] Ac={-8.0/3.0,0.0,1.0/6.0,1.0/12.0};
	public final static double [] Sac={-3.0/8.0, 1.0/32.0, -1.0/64.0, 0};
	public final static double [] Pc={1.0/2.0, 1.0/4.0, 1.0/8.0, 1.0/16.0};
	/* the level below which all computation is done solely at place 0. 
	   Data structures are duplicated at place 0 when necessary. */
	final int P2SLEVEL=Util.P2SLEVEL-1;
	
	final int m_size;
	final region m_problemDomain;
	final int m_levels;
	final LevelData [] m_r,m_z;
	LevelData m_tempLD;
	
	
	public MGOP(final int a_size){
		if (Util.OVERLAP_COMMANDCOMP)
			EXCHANGE_After=false;
		else
			EXCHANGE_After=Util.EXCHANGE_AFTER;
		
		assert Util.powerOf2(a_size);
		int i,j,k;
		
		m_size=a_size;
		m_problemDomain=[0:m_size-1,0:m_size-1,0:m_size-1];
		m_levels=Util.log2(a_size);

		if (P2SLEVEL>0){
			i=Util.pow2(P2SLEVEL);j=i*i*i;
			assert (j>=Util.N_PLACES && P2SLEVEL<m_levels-1); //make sure each place has at least one array element
		}
		else{
			assert Util.N_PLACES<=8; //the base size is 2 by 2 by 2.
		}
		/* building distributed arrays at each refinement level */ 
		m_r=new LevelData [m_levels];m_z=new LevelData [m_levels];
		j=m_size;
		boolean isParallel;
		for (i=m_levels-1;i>-1;i--){//The level number starts with m_levels-1 and ends with 0. At level 0, the size is 2.
			if (i>=P2SLEVEL) isParallel=true; else isParallel=false;
			m_r[i]=new LevelData(j,isParallel);
			m_z[i]=new LevelData(j,isParallel);
			j/=2;
		}
		if (P2SLEVEL>0) m_tempLD=new LevelData(Util.pow2(P2SLEVEL),true);
		System.out.println("Overlapping communication and computation? "+OVERLAPPED+". If not, doing exchange after computation? "+EXCHANGE_After);
	}
	
	/*a_res=a_res-A(a_arg)*/

	final void computeResidual(final LevelData RES, final LevelData a_arg){
		
		finish ateach(point [i]:RES.getPlaces()){ /*at each place.*/
			final double [.] res=RES.getArray(i);
		        final double [.] arg=a_arg.getArray(i);
			/*apply A on the inner region*/
			region R=RES.getInnerRegion(i); //removed final
			//foreach(point p:R){ // 23 addition+3 multiplication	
			for(point p:R){  
				double d0,d1,d2,d3;
				d0=arg[p];
				//d1=0; for(int j=0;j<Util.DIFF1.length;j++) d1+=arg[p+Util.DIFF1[j]];
				d2=0; for(int j=0;j<Util.DIFF2.length;j++) d2+=arg[p+Util.DIFF2[j]];
				d3=0; for(int j=0;j<Util.DIFF3.length;j++) d3+=arg[p+Util.DIFF3[j]];
				res[p]-=(Ac[0]*d0+Ac[2]*d2+Ac[3]*d3);
				if (PERF_On && (!PERF_CommOnly)&&PERF_InLoop) x10.lang.perf.addLocalOps((long)(26*PERF_Comp));
			}
			if (PERF_On && (!PERF_CommOnly)&&(!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(26*PERF_Comp*R.size()));
			//if (PERF_On && (!PERF_CommOnly)&&(!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(26*PERF_Comp));
		}
		if (EXCHANGE_After) finish RES.exchange();
	}
	
	/*a_res=a_arg1-A(a_arg2)*/
	final void computeResidual(final LevelData a_res, final LevelData a_arg1, final LevelData a_arg2){
		if (OVERLAPPED) 
			ComputeResidual_Overlapped(a_res, a_arg1, a_arg2);
		else
			ComputeResidual(a_res, a_arg1,a_arg2);
	}
	
	final void ComputeResidual(final LevelData a_res, final LevelData a_arg1, final LevelData a_arg2){
		if (!EXCHANGE_After) finish a_arg2.exchange();
		finish ateach(point [i]:a_res.getPlaces()){ /*at each place.*/
			final double [.] res=a_res.getArray(i);
		        final double [.] arg1=a_arg1.getArray(i);
		        final double [.] arg2=a_arg2.getArray(i);
			/*apply A on the inner region*/
			region R=a_res.getInnerRegion(i); // removed final
			//foreach(point p:R){  // 23 addition+3 multiplication
			for (point p:R){	
				double d0,d1,d2,d3;
				d0=arg2[p];
				//d1=0; for(int j=0;j<Util.DIFF1.length;j++) d1+=arg2[p+Util.DIFF1[j]];
				d2=0; for(int j=0;j<Util.DIFF2.length;j++) d2+=arg2[p+Util.DIFF2[j]];
				d3=0; for(int j=0;j<Util.DIFF3.length;j++) d3+=arg2[p+Util.DIFF3[j]];
				res[p]=arg1[p]-Ac[0]*d0-Ac[2]*d2-Ac[3]*d3;
				if (PERF_On && (!PERF_CommOnly) && PERF_InLoop) x10.lang.perf.addLocalOps((long)(26*PERF_Comp));
			}
			if (PERF_On && (!PERF_CommOnly)&&(!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(26*PERF_Comp*R.size()));
			//if (PERF_On && (!PERF_CommOnly)&&(!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(26*PERF_Comp));
		}
		if (EXCHANGE_After) finish a_res.exchange();
	}
	/*Updating the  outer boundary values and Computing on the inner region are overlapped.*/	
	final void ComputeResidual_Overlapped1(final LevelData a_res, final LevelData a_arg1, final LevelData a_arg2){	
	  finish{	
		a_arg2.exchange();
		ateach(point [i]:a_res.getPlaces()){ /*at each place.*/
			final double [.] res=a_res.getArray(i);
		        final double [.] arg1=a_arg1.getArray(i);
		        final double [.] arg2=a_arg2.getArray(i);
			/*apply A on the inner region*/
			region R=a_res.getINNERRegion(i); //remove final
			//foreach(point p:R){  
			for (point p:R){//23 addition+3 multiplication	
				double d0,d1,d2,d3;
				d0=arg2[p];
				//d1=0; for(int j=0;j<Util.DIFF1.length;j++) d1+=arg2[p+Util.DIFF1[j]];
				d2=0; for(int j=0;j<Util.DIFF2.length;j++) d2+=arg2[p+Util.DIFF2[j]];
				d3=0; for(int j=0;j<Util.DIFF3.length;j++) d3+=arg2[p+Util.DIFF3[j]];
				res[p]=arg1[p]-Ac[0]*d0-Ac[2]*d2-Ac[3]*d3;
				if (PERF_On && (!PERF_CommOnly) && PERF_InLoop) x10.lang.perf.addLocalOps((long)(26*PERF_Comp));
			}
			if (PERF_On && (!PERF_CommOnly)&&(!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(26*PERF_Comp*R.size()));
			//if (PERF_On && (!PERF_CommOnly)&&(!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(26*PERF_Comp));
		}
	  }
		finish ateach(point [i]:a_res.getPlaces()){ /*at each place.*/
			final double [.] res=a_res.getArray(i);
		        final double [.] arg1=a_arg1.getArray(i);
		        final double [.] arg2=a_arg2.getArray(i);
			/*apply A on the inner region*/
			region R=a_res.getInnerRegion(i)-a_res.getINNERRegion(i); //remove final
			//foreach(point p:R){  
			for (point p:R){//23 addition+3 multiplication	
				double d0,d1,d2,d3;
				d0=arg2[p];
				d2=0; for(int j=0;j<Util.DIFF2.length;j++) d2+=arg2[p+Util.DIFF2[j]];
				d3=0; for(int j=0;j<Util.DIFF3.length;j++) d3+=arg2[p+Util.DIFF3[j]];
				res[p]=arg1[p]-Ac[0]*d0-Ac[2]*d2-Ac[3]*d3;
				if (PERF_On && (!PERF_CommOnly) && PERF_InLoop) x10.lang.perf.addLocalOps((long)(26*PERF_Comp));
			}
			if (PERF_On && (!PERF_CommOnly)&&(!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(26*PERF_Comp*R.size()));
			//if (PERF_On && (!PERF_CommOnly)&&(!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(26*PERF_Comp));
		}
	}
	/*fully overlapped version*/	
	final void ComputeResidual_Overlapped(final LevelData a_res, final LevelData a_arg1, final LevelData a_arg2){
		  finish async{	
			ateach(point [i]:a_res.getPlaces()){ /*at each place.*/
				final double [.] res=a_res.getArray(i);
			        final double [.] arg1=a_arg1.getArray(i);
			        final double [.] arg2=a_arg2.getArray(i);
				/*apply A on the inner region*/
				region R=a_res.getINNERRegion(i); //removed final
				//foreach(point p:R){  
				for (point p:R){//23 addition+3 multiplication	
					double d0,d1,d2,d3;
					d0=arg2[p];
					//d1=0; for(int j=0;j<Util.DIFF1.length;j++) d1+=arg2[p+Util.DIFF1[j]];
					d2=0; for(int j=0;j<Util.DIFF2.length;j++) d2+=arg2[p+Util.DIFF2[j]];
					d3=0; for(int j=0;j<Util.DIFF3.length;j++) d3+=arg2[p+Util.DIFF3[j]];
					res[p]=arg1[p]-Ac[0]*d0-Ac[2]*d2-Ac[3]*d3;
					if (PERF_On && (!PERF_CommOnly) && PERF_InLoop) x10.lang.perf.addLocalOps((long)(26*PERF_Comp));
				}
				if (PERF_On && (!PERF_CommOnly)&&(!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(26*PERF_Comp*R.size()));
				//if (PERF_On && (!PERF_CommOnly)&&(!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(26*PERF_Comp));
			}
			
			finish a_arg2.exchange(); //If change a_arg2 to a wrong one such as a_arg, compiler can't tell the name of this file.
				
			ateach(point [i]:a_res.getPlaces()){ /*at each place.*/
				final double [.] res=a_res.getArray(i);
			        final double [.] arg1=a_arg1.getArray(i);
			        final double [.] arg2=a_arg2.getArray(i);
				/*apply A on the inner region*/
				region R=a_res.getInnerRegion(i)-a_res.getINNERRegion(i); //removed final
				//foreach(point p:R){  
				for (point p:R){//23 addition+3 multiplication	
					double d0,d1,d2,d3;
					d0=arg2[p];
					d2=0; for(int j=0;j<Util.DIFF2.length;j++) d2+=arg2[p+Util.DIFF2[j]];
					d3=0; for(int j=0;j<Util.DIFF3.length;j++) d3+=arg2[p+Util.DIFF3[j]];
					res[p]=arg1[p]-Ac[0]*d0-Ac[2]*d2-Ac[3]*d3;
					if (PERF_On && (!PERF_CommOnly) && PERF_InLoop) x10.lang.perf.addLocalOps((long)(26*PERF_Comp));
				}
				if (PERF_On && (!PERF_CommOnly)&&(!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(26*PERF_Comp*R.size()));
				//if (PERF_On && (!PERF_CommOnly)&&(!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(26*PERF_Comp));
			}	
		}
	}
			
	/*a_res=a_res+S(a_arg)*/
	final void smooth(final LevelData a_res, final LevelData a_arg, final boolean a_isBottom){
		if (OVERLAPPED) 
			Smooth_Overlapped(a_res, a_arg, a_isBottom);
		else
			Smooth(a_res, a_arg,a_isBottom);
	}
	final void Smooth(final LevelData a_res, final LevelData a_arg, final boolean a_isBottom){
		if (!EXCHANGE_After) finish a_arg.exchange();
		//final double [.] Sc=Sac; //this statement will make compiler (TypeChecker) in infite loops.
		finish ateach(point [i]:a_res.getPlaces()){ /*at each place.*/
			final double [.] res=a_res.getArray(i);
		        final double [.] arg=a_arg.getArray(i);
			/*applyOpS is on the inner region*/
			region R=a_res.getInnerRegion(i); //removed final
			//foreach(point p:R){
			for(point p:R){//21 addition 3 multiplication	
				double d0,d1,d2,d3;
				d0=arg[p];
				d1=0; for(int j=0;j<Util.DIFF1.length;j++) d1+=arg[p+Util.DIFF1[j]];
				d2=0; for(int j=0;j<Util.DIFF2.length;j++) d2+=arg[p+Util.DIFF2[j]];
				//d3=0; for(int j=0;j<Util.DIFF3.length;j++) d3+=arg2[p+Util.DIFF3[j]];
				//if (a_isBottom)
					//res[p]=Sc[0]*d0+Sc[1]*d1+Sc[2]*d2;
				//else
					res[p]+=Sac[0]*d0+Sac[1]*d1+Sac[2]*d2;
				if (PERF_On && (!PERF_CommOnly) && PERF_InLoop) x10.lang.perf.addLocalOps((long)(24*PERF_Comp));
			}
			if (PERF_On && (!PERF_CommOnly) && (!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(24*PERF_Comp*R.size()));
			//if (PERF_On && (!PERF_CommOnly) && (!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(24*PERF_Comp));
		}	
		if (EXCHANGE_After) finish a_res.exchange();
	}
	/*Updating the  outer boundary values and Computing on the inner region are overlapped.*/	
	final void Smooth_Overlapped1(final LevelData a_res, final LevelData a_arg, final boolean a_isBottom){
	  finish{
		a_arg.exchange();
		ateach(point [i]:a_res.getPlaces()){ /*at each place.*/
			final double [.] res=a_res.getArray(i);
		        final double [.] arg=a_arg.getArray(i);
			/*applyOpS is on the inner region*/
			region R=a_res.getINNERRegion(i); //removed final
			//foreach(point p:R){
			for(point p:R){//21 addition 3 multiplication	
				double d0,d1,d2,d3;
				d0=arg[p];
				d1=0; for(int j=0;j<Util.DIFF1.length;j++) d1+=arg[p+Util.DIFF1[j]];
				d2=0; for(int j=0;j<Util.DIFF2.length;j++) d2+=arg[p+Util.DIFF2[j]];
				res[p]+=Sac[0]*d0+Sac[1]*d1+Sac[2]*d2;
				if (PERF_On && (!PERF_CommOnly) && PERF_InLoop) x10.lang.perf.addLocalOps((long)(24*PERF_Comp));
			}
			if (PERF_On && (!PERF_CommOnly) && (!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(24*PERF_Comp*R.size()));
			//if (PERF_On && (!PERF_CommOnly) && (!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(24*PERF_Comp));
		}
	  }	
		finish ateach(point [i]:a_res.getPlaces()){ /*at each place.*/
			final double [.] res=a_res.getArray(i);
		        final double [.] arg=a_arg.getArray(i);
			/*applyOpS is on the inner region*/
			region R=a_res.getInnerRegion(i)-a_res.getINNERRegion(i); //removed final
			//foreach(point p:R){
			for(point p:R){//21 addition 3 multiplication	
				double d0,d1,d2,d3;
				d0=arg[p];
				d1=0; for(int j=0;j<Util.DIFF1.length;j++) d1+=arg[p+Util.DIFF1[j]];
				d2=0; for(int j=0;j<Util.DIFF2.length;j++) d2+=arg[p+Util.DIFF2[j]];
				res[p]+=Sac[0]*d0+Sac[1]*d1+Sac[2]*d2;
				if (PERF_On && (!PERF_CommOnly) && PERF_InLoop) x10.lang.perf.addLocalOps((long)(24*PERF_Comp));
			}
			if (PERF_On && (!PERF_CommOnly) && (!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(24*PERF_Comp*R.size()));
			//if (PERF_On && (!PERF_CommOnly) && (!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(24*PERF_Comp));
		}
	}
	/*fully overlapped version*/	
	final void Smooth_Overlapped(final LevelData a_res, final LevelData a_arg, final boolean a_isBottom){
		  finish async{
			ateach(point [i]:a_res.getPlaces()){ /*at each place.*/
				final double [.] res=a_res.getArray(i);
			        final double [.] arg=a_arg.getArray(i);
				/*applyOpS is on the inner region*/
				region R=a_res.getINNERRegion(i); //removed final
				//foreach(point p:R){
				for(point p:R){//21 addition 3 multiplication	
					double d0,d1,d2,d3;
					d0=arg[p];
					d1=0; for(int j=0;j<Util.DIFF1.length;j++) d1+=arg[p+Util.DIFF1[j]];
					d2=0; for(int j=0;j<Util.DIFF2.length;j++) d2+=arg[p+Util.DIFF2[j]];
					res[p]+=Sac[0]*d0+Sac[1]*d1+Sac[2]*d2;
					if (PERF_On && (!PERF_CommOnly) && PERF_InLoop) x10.lang.perf.addLocalOps((long)(24*PERF_Comp));
				}
				if (PERF_On && (!PERF_CommOnly) && (!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(24*PERF_Comp*R.size()));
				//if (PERF_On && (!PERF_CommOnly) && (!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(24*PERF_Comp));
			}
			
			finish a_arg.exchange();
			ateach(point [i]:a_res.getPlaces()){ /*at each place.*/
				final double [.] res=a_res.getArray(i);
			        final double [.] arg=a_arg.getArray(i);
				/*applyOpS is on the inner region*/
				region R=a_res.getInnerRegion(i)-a_res.getINNERRegion(i); //remove final
				//foreach(point p:R){
				for(point p:R){//21 addition 3 multiplication	
					double d0,d1,d2,d3;
					d0=arg[p];
					d1=0; for(int j=0;j<Util.DIFF1.length;j++) d1+=arg[p+Util.DIFF1[j]];
					d2=0; for(int j=0;j<Util.DIFF2.length;j++) d2+=arg[p+Util.DIFF2[j]];
					res[p]+=Sac[0]*d0+Sac[1]*d1+Sac[2]*d2;
					if (PERF_On && (!PERF_CommOnly) && PERF_InLoop) x10.lang.perf.addLocalOps((long)(24*PERF_Comp));
				}
				if (PERF_On && (!PERF_CommOnly) && (!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(24*PERF_Comp*R.size()));
					//if (PERF_On && (!PERF_CommOnly) && (!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(24*PERF_Comp));
				}	
		}		
	}
			
	/*a_res=P(a_arg)*/
	final void applyOpP(final LevelData a_res, final LevelData a_arg, final int a_level){
		if (a_level==P2SLEVEL){ //a_level is the leve id of a_arg
			applyOpP(m_tempLD, a_arg);
			/* copy m_tempLD to a_res */
				final double [.] res=a_res.getArray(0);
				//foreach(point [i]:m_tempLD.getPlaces().region){
				//for(point [i]:m_tempLD.getPlaces().region){	
				finish ateach(point [i]:m_tempLD.getPlaces()){		
					double [.] temp=m_tempLD.getArray(i); //removed final
					Util.arraycopy(res, m_tempLD.getInnerRegion(i), temp);
					if (i!=0)
						if (PERF_On && (!PERF_CompOnly)) 
							x10.lang.perf.addLocalOps((long)(PERF_CommL+PERF_CommB*m_tempLD.getInnerRegion(i).size()));
				}
		}	
		else
			applyOpP(a_res,a_arg);
		if (EXCHANGE_After) finish a_res.exchange();
	}
	final void applyOpP(final LevelData a_res, final LevelData a_arg){
		if (OVERLAPPED) 
			ApplyOpP_Overlapped(a_res, a_arg);
		else
			ApplyOpP(a_res, a_arg);
	}
	final void ApplyOpP(final LevelData a_res, final LevelData a_arg){
		if (!EXCHANGE_After) finish a_arg.exchange();
		finish ateach(point [i]:a_res.getPlaces()){ /*at each place.*/
			final double [.] arg=a_arg.getArray(i);
		        final double [.] res=a_res.getArray(i);
			/*applyOpS is on the inner region*/
			region R=a_res.getInnerRegion(i); //removed final
			//foreach(point pp:R){ 
			for(point pp:R){//flops:29 addition+4 multiplication; integer: 1 addition 1 mult. 	
				double d0,d1,d2,d3;
				point p=pp*2+[1,1,1]; //This is the formula to use. But no shift is better
				d0=arg[p];
				d1=0; for(int j=0;j<Util.DIFF1.length;j++) d1+=arg[p+Util.DIFF1[j]];
				d2=0; for(int j=0;j<Util.DIFF2.length;j++) d2+=arg[p+Util.DIFF2[j]];
				d3=0; for(int j=0;j<Util.DIFF3.length;j++) d3+=arg[p+Util.DIFF3[j]];
				res[pp]=Pc[0]*d0+Pc[1]*d1+Pc[2]*d2+Pc[3]*d3;
				if (PERF_On && (!PERF_CommOnly) && PERF_InLoop) x10.lang.perf.addLocalOps((long)(34*PERF_Comp));
			}
			if (PERF_On && (!PERF_CommOnly) && (!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(34*PERF_Comp*R.size()));
		}	
	}
	/*Updating the  outer boundary values and Computing on the inner region are overlapped.*/	
	final void ApplyOpP_Overlapped1(final LevelData a_res, final LevelData a_arg){
	  finish{	
		a_arg.exchange();
		ateach(point [i]:a_res.getPlaces()){ /*at each place.*/
			final double [.] arg=a_arg.getArray(i);
		        final double [.] res=a_res.getArray(i);
			/*applyOpS is on the inner region*/
			region R=a_res.getINNERRegion(i); //removed final
			//foreach(point pp:R){ 
			for(point pp:R){//flops:29 addition+4 multiplication; integer: 1 addition 1 mult. 	
				double d0,d1,d2,d3;
				point p=pp*2+[1,1,1]; //This is the formula to use. But no shift is better
				d0=arg[p];
				d1=0; for(int j=0;j<Util.DIFF1.length;j++) d1+=arg[p+Util.DIFF1[j]];
				d2=0; for(int j=0;j<Util.DIFF2.length;j++) d2+=arg[p+Util.DIFF2[j]];
				d3=0; for(int j=0;j<Util.DIFF3.length;j++) d3+=arg[p+Util.DIFF3[j]];
				res[pp]=Pc[0]*d0+Pc[1]*d1+Pc[2]*d2+Pc[3]*d3;
				if (PERF_On && (!PERF_CommOnly) && PERF_InLoop) x10.lang.perf.addLocalOps((long)(34*PERF_Comp));
			}
			if (PERF_On && (!PERF_CommOnly) && (!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(34*PERF_Comp*R.size()));
		}
	  }
	  finish ateach(point [i]:a_res.getPlaces()){ /*at each place.*/
			final double [.] arg=a_arg.getArray(i);
		        final double [.] res=a_res.getArray(i);
			/*applyOpS is on the inner region*/
			region R=a_res.getInnerRegion(i)-a_res.getINNERRegion(i); //removed final
			//foreach(point pp:R){ 
			for(point pp:R){//flops:29 addition+4 multiplication; integer: 1 addition 1 mult. 	
				double d0,d1,d2,d3;
				point p=pp*2+[1,1,1]; //This is the formula to use. But no shift is better
				d0=arg[p];
				d1=0; for(int j=0;j<Util.DIFF1.length;j++) d1+=arg[p+Util.DIFF1[j]];
				d2=0; for(int j=0;j<Util.DIFF2.length;j++) d2+=arg[p+Util.DIFF2[j]];
				d3=0; for(int j=0;j<Util.DIFF3.length;j++) d3+=arg[p+Util.DIFF3[j]];
				res[pp]=Pc[0]*d0+Pc[1]*d1+Pc[2]*d2+Pc[3]*d3;
				if (PERF_On && (!PERF_CommOnly) && PERF_InLoop) x10.lang.perf.addLocalOps((long)(34*PERF_Comp));
			}
			if (PERF_On && (!PERF_CommOnly) && (!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(34*PERF_Comp*R.size()));
		}
	}
	/*fully overlapped version*/  
	final void ApplyOpP_Overlapped(final LevelData a_res, final LevelData a_arg){
		  finish async {	
				ateach(point [i]:a_res.getPlaces()){ /*at each place.*/
					final double [.] arg=a_arg.getArray(i);
				        final double [.] res=a_res.getArray(i);
					/*applyOpS is on the inner region*/
					region R=a_res.getINNERRegion(i); //removed final
					//foreach(point pp:R){ 
					for(point pp:R){//flops:29 addition+4 multiplication; integer: 1 addition 1 mult. 	
						double d0,d1,d2,d3;
						point p=pp*2+[1,1,1]; //This is the formula to use. But no shift is better
						d0=arg[p];
						d1=0; for(int j=0;j<Util.DIFF1.length;j++) d1+=arg[p+Util.DIFF1[j]];
						d2=0; for(int j=0;j<Util.DIFF2.length;j++) d2+=arg[p+Util.DIFF2[j]];
						d3=0; for(int j=0;j<Util.DIFF3.length;j++) d3+=arg[p+Util.DIFF3[j]];
						res[pp]=Pc[0]*d0+Pc[1]*d1+Pc[2]*d2+Pc[3]*d3;
						if (PERF_On && (!PERF_CommOnly) && PERF_InLoop) x10.lang.perf.addLocalOps((long)(34*PERF_Comp));
					}
					if (PERF_On && (!PERF_CommOnly) && (!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(34*PERF_Comp*R.size()));
					//if (PERF_On && (!PERF_CommOnly) && (!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(34*PERF_Comp));
				}
			
				finish a_arg.exchange();
			
				ateach(point [i]:a_res.getPlaces()){ /*at each place.*/
					final double [.] arg=a_arg.getArray(i);
				        final double [.] res=a_res.getArray(i);
					/*applyOpS is on the inner region*/
					region R=a_res.getInnerRegion(i)-a_res.getINNERRegion(i); //removed final
					//foreach(point pp:R){ 
					for(point pp:R){//flops:29 addition+4 multiplication; integer: 1 addition 1 mult. 	
						double d0,d1,d2,d3;
						point p=pp*2+[1,1,1]; //This is the formula to use. But no shift is better
						d0=arg[p];
						d1=0; for(int j=0;j<Util.DIFF1.length;j++) d1+=arg[p+Util.DIFF1[j]];
						d2=0; for(int j=0;j<Util.DIFF2.length;j++) d2+=arg[p+Util.DIFF2[j]];
						d3=0; for(int j=0;j<Util.DIFF3.length;j++) d3+=arg[p+Util.DIFF3[j]];
						res[pp]=Pc[0]*d0+Pc[1]*d1+Pc[2]*d2+Pc[3]*d3;
						if (PERF_On && (!PERF_CommOnly) && PERF_InLoop) x10.lang.perf.addLocalOps((long)(34*PERF_Comp));
					}
					if (PERF_On && (!PERF_CommOnly) && (!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(34*PERF_Comp*R.size()));
					//if (PERF_On && (!PERF_CommOnly) && (!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(34*PERF_Comp));
				}
			
		}		
	}
				
	/*a_res=Q(a_arg)*/
	final void applyOpQ(final LevelData a_res, final LevelData a_arg, final int a_level){
		if (a_level==P2SLEVEL-1){//a_level is the level id of a_arg
			/* copy a_arg to m_tempLD */
			final double [.] arg=a_arg.getArray(0);
			finish ateach(point [i]:m_tempLD.getPlaces()){	
				double [.] temp=m_tempLD.getArray(i); //removed final
				Util.arraycopy(temp, temp.region, arg);
				if (i!=0)
					if (PERF_On && (!PERF_CompOnly)) 
						x10.lang.perf.addLocalOps((long)(PERF_CommL+PERF_CommB*temp.region.size()));
			}
			applyOpQ(a_res, m_tempLD);
		}	
		else
			applyOpQ(a_res,a_arg);
	}
	final void applyOpQ(final LevelData a_res, final LevelData a_arg){
		if (OVERLAPPED) 
			ApplyOpQ_Overlapped(a_res, a_arg);
		else
			ApplyOpQ(a_res, a_arg);
	}
	final void ApplyOpQ(final LevelData a_res, final LevelData a_arg){
		if (!EXCHANGE_After) finish a_arg.exchange();
		finish ateach(point [i]:a_res.getPlaces()){ /*at each place.*/
			final double [.] arg=a_arg.getArray(i);
		        final double [.] res=a_res.getArray(i);
			/*applyOpQ is on the shrinked region*/
			region R=a_arg.getShrinkedRegion(i); //this is the right formula to use
			//foreach(point p:R){
			for(point p:R){//flop: 35 addition+8 multiplication; integer: 36 addition+1 mult
				double d;
				point pp=p*2+[1,1,1]; 
				for (point o: Util.UNIT_CUBE){
					d=0;
					for (point q: Util.QREGIONS[o]) d+=arg[p+q];
					res[pp+o]=d/Util.QREGIONS[o].size();
				}
				if (PERF_On && (!PERF_CommOnly) && PERF_InLoop) x10.lang.perf.addLocalOps((long)(61.5*PERF_Comp));
			}
			if (PERF_On && (!PERF_CommOnly) && (!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(61.5*PERF_Comp*R.size()));
		}	
	}
	/*Updating the  outer boundary values and Computing on the inner region are overlapped.*/	
	final void ApplyOpQ_Overlapped1(final LevelData a_res, final LevelData a_arg){
	  finish{	
		a_arg.exchange();
		ateach(point [i]:a_res.getPlaces()){ /*at each place.*/
			final double [.] arg=a_arg.getArray(i);
		        final double [.] res=a_res.getArray(i);
			/*applyOpQ is on the shrinked region*/
			region R=a_arg.getINNERRegion(i); //this is the right formula to use
			//foreach(point p:R){
			for(point p:R){//flop: 35 addition+8 multiplication; integer: 36 addition+1 mult
				double d;
				point pp=p*2+[1,1,1]; 
				for (point o: Util.UNIT_CUBE){
					d=0;
					for (point q: Util.QREGIONS[o]) d+=arg[p+q];
					res[pp+o]=d/Util.QREGIONS[o].size();
				}
				if (PERF_On && (!PERF_CommOnly) && PERF_InLoop) x10.lang.perf.addLocalOps((long)(61.5*PERF_Comp));
			}
			if (PERF_On && (!PERF_CommOnly) && (!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(61.5*PERF_Comp*R.size()));
		}	
	  }
		finish ateach(point [i]:a_res.getPlaces()){ /*at each place.*/
			final double [.] arg=a_arg.getArray(i);
		        final double [.] res=a_res.getArray(i);
			/*applyOpQ is on the shrinked region*/
			region R=a_arg.getShrinkedRegion(i)-a_arg.getINNERRegion(i); //this is the right formula to use
			//foreach(point p:R){
			for(point p:R){//flop: 35 addition+8 multiplication; integer: 36 addition+1 mult
				double d;
				point pp=p*2+[1,1,1]; 
				for (point o: Util.UNIT_CUBE){
					d=0;
					for (point q: Util.QREGIONS[o]) d+=arg[p+q];
					res[pp+o]=d/Util.QREGIONS[o].size();
				}
				if (PERF_On && (!PERF_CommOnly) && PERF_InLoop) x10.lang.perf.addLocalOps((long)(61.5*PERF_Comp));
			}
			if (PERF_On && (!PERF_CommOnly) && (!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(61.5*PERF_Comp*R.size()));
		}	
	}
	/*fully overlapped version*/	
	final void ApplyOpQ_Overlapped(final LevelData a_res, final LevelData a_arg){
		  finish async {	
				ateach(point [i]:a_res.getPlaces()){ /*at each place.*/
					final double [.] arg=a_arg.getArray(i);
				        final double [.] res=a_res.getArray(i);
					/*applyOpQ is on the shrinked region*/
					region R=a_arg.getINNERRegion(i); //this is the right formula to use
					//foreach(point p:R){
					for(point p:R){//flop: 35 addition+8 multiplication; integer: 36 addition+1 mult
						double d;
						point pp=p*2+[1,1,1]; 
						for (point o: Util.UNIT_CUBE){
							d=0;
							for (point q: Util.QREGIONS[o]) d+=arg[p+q];
							res[pp+o]=d/Util.QREGIONS[o].size();
						}
						if (PERF_On && (!PERF_CommOnly) && PERF_InLoop) x10.lang.perf.addLocalOps((long)(61.5*PERF_Comp));
					}
					if (PERF_On && (!PERF_CommOnly) && (!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(61.5*PERF_Comp*R.size()));
					
				}	
			
				finish a_arg.exchange();
				
				ateach(point [i]:a_res.getPlaces()){ /*at each place.*/
					final double [.] arg=a_arg.getArray(i);
				        final double [.] res=a_res.getArray(i);
					/*applyOpQ is on the shrinked region*/
					region R=a_arg.getShrinkedRegion(i)-a_arg.getINNERRegion(i); //this is the right formula to use
					//foreach(point p:R){
					for(point p:R){//flop: 35 addition+8 multiplication; integer: 36 addition+1 mult
						double d;
						point pp=p*2+[1,1,1]; 
						for (point o: Util.UNIT_CUBE){
							d=0;
							for (point q: Util.QREGIONS[o]) d+=arg[p+q];
							res[pp+o]=d/Util.QREGIONS[o].size();
						}
						if (PERF_On && (!PERF_CommOnly) && PERF_InLoop) x10.lang.perf.addLocalOps((long)(61.5*PERF_Comp));
					}
					if (PERF_On && (!PERF_CommOnly) && (!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(61.5*PERF_Comp*R.size()));
				}
		}		
	}
				
	public final double MGSolve(final LevelData a_u, final LevelData a_v, final int a_its){
		int l=m_levels-1; 
		m_r[l].set(a_v);System.out.println("The init residual norm is "+m_r[l].norm2());
		double res=0.0;
		for (int i=0;i<a_its;i++){
			System.out.println("Iteration "+i);
			MG();
			a_u.add(m_z[l]);
			computeResidual(m_r[l],a_v,a_u);
			res=m_r[l].norm2();
			System.out.println("The residual norm is "+res);
		}
		return res;
	}
	
	/*the multigrid algorithm (V-Cycle)*/
	final void MG(){
		for (int i=m_levels-1;i>0;i--) applyOpP(m_r[i-1], m_r[i],i);
		m_z[0].set(0);smooth(m_z[0],m_r[0], true); 
		//System.out.println("z[0]="+m_z[0].norm2());
		for (int i=1;i<m_levels;i++){
			applyOpQ(m_z[i], m_z[i-1], i-1);
			computeResidual(m_r[i], m_z[i]);
			smooth(m_z[i], m_r[i], false);
		}	
	}
	
	final double residualNorm(){
		int l=m_levels-1;
		return m_r[l].norm2(); 
	}
	
	public static void main(String[] a) {
	    /*	
	        final MGOP MG=new MGOP(32);
	    	System.out.println("number of levels= "+MG.m_levels);
	    	final int lv=MG.m_levels;
	    	MG.m_z[1].set(1); MG.m_z[1].print();
	    	//MG.smooth(MG.m_r[lv-1], MG.m_z[lv-1], false);
	    	//MG.applyOpP(MG.m_z[0],MG.m_z[1], 1);
	    	MG.applyOpQ(MG.m_z[2],MG.m_z[1], 1);
	    	MG.m_z[2].print();
	    */	
    	
    	System.out.println("This is MGOP, finished!");
    
    }
}
