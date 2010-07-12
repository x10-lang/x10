package NPB3_0_X10;

/******************************************************************************************
             An X10 implementation of the NPB3.0 MG 3D Benchmark 

This class implements the distributed array for one level of refinement in the MG algorithm. 
It is implemented using the encapsulation approach rather than through a distribution. The 
reason is that each sub-array overlapps with each other because of the adding of a one-deep 
layer of ghost cells. The method exchange is used to update these ghost cells and also to 
apply the periodic physical boundary condtion.

For experimental purposes, a methods in this class may have different implementations. The 
choice of whether updating the ghost cells after each operation that modifies the values in 
inner region or only when the update is needed can be made by setting the corresponding flag 
in class Util. To fully express the concurrency in the algorithm, foreach loop may be used 
to iterate over local arrays at each place when the order of the iteration does not matter. 
In the current implementation, doing so may not be optimal (too many acitivities) compared 
with using for loop. However, the production version of X10 compiler and runtime should be 
responsible for optimizing foreach loops based on the available hardware resources.

Note that the default place is 0.

Related classes: MGOP, MGDriver, Util

Author: Tong Wen, IBM Research

Date:   04/04/06
Modified: 05/19/06
          Add methods PRINT() and getShrinkedRegion()
          Remove qualifier final before local array?
          08/08/06
          remove final qulifiers for the member declaration
          08/20/06
          add the new exchange method which can be used in parallel with computation
          11/06/06
          clean up comments
          12/07/2007 Change  m_boundaries to be an X10 2D array
          (C) Copyright IBM Corp. 2006
*******************************************************************************************/

//final public class LevelData {
public value LevelData {	
	
	value Wrapper{
		double [.] m_array;
		Wrapper(final double [.] a_array){
			m_array=a_array;
		}
	}
	
	/*The abstract peformance metrics are in cycles. 
	  An integer operation is assumed to be as half expensive as a Flop.*/
	public final static boolean PERF_On=Util.PERF_OUTPUT;
	public final static double PERF_Comp=1.0/Util.FP_PERCLOCK;
	public final static double  PERF_CommL=Util.COMM_LATENCY*Util.CLOCK_SPEED;
	public final static double PERF_CommB=((double)Util.CLOCK_SPEED/(double)Util.COMM_BANDWIDTH)*8.0;
	public final static boolean PERF_InLoop=Util.IN_LOOP;
	public final static boolean PERF_CompOnly=Util.COMP_ONLY;
	public final static boolean PERF_CommOnly=Util.COMM_ONLY;
	//public final static boolean EXCHANGE_After=(Util.OVERLAP_COMMANDCOMP?false:Util.EXCHANGE_AFTER);//complained by dep type
	public final  boolean EXCHANGE_After;
	
	int N_PLACES=Util.N_PLACES;
	int HIGH=Util.HIGH, LOW=Util.LOW;
	dist ALLPLACES=Util.ALLPLACES;
	
	int SIZE; /*problem size*/
	region P_DOMAIN;/*problem domain*/
	boolean ISPARALLEL;/*distributed or not*/
	
	region[] m_regions;/* decomposition of the problem domain, one subdomain for each place */
	region[] m_REGIONs;/* with ghost cells or boundaries*/
	//region [] [] m_boundaries; /*boundary at each face*/
	region [.] m_boundaries;
	
	dist  m_places;
	int m_numPlaces;
	
	region m_placeGrid; /*3D index of each subregion*/
	point m_low, m_hi, m_size, m_cut, m_block, m_blockSize;
	dist [] m_dist, m_DIST;
	Wrapper value [.] m_u; 
	/*The following java array works, that is, it provides the same functionality as the above value array.*/
	//Wrapper [] m_u;
	
	final dist getPlaces(){return m_places;}
	final double [.] getArray(final int a_idx){
		assert (a_idx>=0 && a_idx<m_numPlaces);
		return m_u[a_idx].m_array;
	}
	final region(:rank==3) getInnerRegion(final int a_idx){
		assert (a_idx>=0 && a_idx<m_numPlaces);
		return (region(:rank==3))m_regions[a_idx];
	}
	final region(:rank==3) getINNERRegion(final int a_idx){
		assert (a_idx>=0 && a_idx<m_numPlaces);
		region R=m_regions[a_idx];
		return (region(:rank==3))[R.rank(0).low()+1:R.rank(0).high()-1,R.rank(1).low()+1:R.rank(1).high()-1,R.rank(2).low()+1:R.rank(2).high()-1];
	}
	final region(:rank==3) getShrinkedRegion(final int a_idx){
		assert (a_idx>=0 && a_idx<m_numPlaces);
		region R=m_REGIONs[a_idx];
		return (region(:rank==3))[R.rank(0).low():R.rank(0).high()-1,R.rank(1).low():R.rank(1).high()-1,R.rank(2).low():R.rank(2).high()-1];
	}
	
	final region getRegion(final int a_idx){
		assert (a_idx>=0 && a_idx<m_numPlaces);
		return m_REGIONs[a_idx];
	}
	/*for test purpose*/
	final void print(){
		finish ateach(point [i]:m_places){
			double res=0;
			double [.] temp=m_u[i].m_array; //remove final
			for (point p:m_REGIONs[i]) {res+=Math.abs(temp[p]);}
			System.out.println("block at place "+i+" : "+temp.region+" sum of m_u="+res);
		}
	}
	/*for test purpose*/
	final void PRINT(){
		finish ateach(point [i]:m_places){
			System.out.println("block at place "+i+" : ");
			double [.] temp=m_u[i].m_array; //remove final
			int counter=0;
			for (point p:m_regions[i]) {
				System.out.print (" ["+p+"]= "+temp[p]); counter++;
				if (counter==4){ System.out.print("\n"); counter=0;}
			}
			System.out.flush();System.out.println();
		}
	}
	final double norm2(){
		final double [.] results=new double [m_places];
		finish ateach(point [i]:m_places){
			double res=0;
			double [.] temp=m_u[i].m_array; 
			for (point p:m_regions[i]) {res+=temp[p]*temp[p];}
			results[i]=res;
			if (PERF_On && (!PERF_CommOnly)) x10.lang.perf.addLocalOps((long)(m_regions[i].size()*PERF_Comp));
		}
		double temp=results.reduce(results.add,0)/(SIZE*SIZE*SIZE);
		/*The cost is very small here, so we just ignore it*/
		return Math.sqrt(temp);
	}
	final void initialize(final point [] a_minusOnePoints, final point [] a_plusOnePoints){
		/*setting the inner value to 1*/
		finish ateach(point [i]:m_places){
			double [.] temp=m_u[i].m_array; 
			region R=m_regions[i]; 
			//System.out.println(a_minusOnePoints.length);
			for (int j=0;j<a_minusOnePoints.length;j++){
				if (R.contains(a_minusOnePoints[j])) temp[a_minusOnePoints[j]]=-1; 
			}
			for (int j=0;j<a_plusOnePoints.length;j++){
				if (R.contains(a_plusOnePoints[j])) temp[a_plusOnePoints[j]]=1; 
			}
		}
		if (EXCHANGE_After) finish exchange();
	}
	final void set(final double a_db){
		/*setting the inner value to 1*/
		finish ateach(point [i]:m_places){
			final double [.] temp=m_u[i].m_array; //remove final
			for (point p:m_regions[i]) temp[p]=a_db; //foreach becomes for
		}
		if (EXCHANGE_After) finish exchange();
	}
	final void set(final LevelData a_LD){
		/*setting the inner value to 1*/
		finish ateach(point [i]:m_places){
			final double [.] u=m_u[i].m_array; //remove final
			final double [.] temp=a_LD.getArray(i);
			for (point p:m_regions[i]) u[p]=temp[p]; //foreach becomes for
		}
		if (EXCHANGE_After) finish exchange();
	}
	
	final void add(final LevelData a_LD){
		/*setting the inner value to 1*/
		finish ateach(point [i]:m_places){
			final double [.] u=m_u[i].m_array; //remove final
			final double [.] temp=a_LD.getArray(i);
			for (point p:m_regions[i]){//foreach becomes for
				u[p]+=temp[p]; 
				if (PERF_On && (!PERF_CommOnly) && PERF_InLoop) x10.lang.perf.addLocalOps((long)(1.0*PERF_Comp));
			}
			if (PERF_On && (!PERF_CommOnly) && (!PERF_InLoop)) x10.lang.perf.addLocalOps((long)(m_regions[i].size()*PERF_Comp));
		}
		if (EXCHANGE_After) finish exchange();
	}
	/*filling the boundary regions. Periodic boundary condition is used for the physical boundary*/
	/*this version can't be used in parallel with the stencil operation in the inner region*/
	final void Exchange(){
		for (int j=0;j<3;j++){ //for each space dimension and has to be done sequentially
			final int jj=j;
			finish ateach(point [i]:m_places){//at each place in parallel
				final point dest=m_placeGrid.coord(i); 
				final int ii=i; 
				//foreach (point [p]:[LOW:HIGH]){ //for each face in parallel
				for (point [p]:[LOW:HIGH]){	
				    point disp, source,trans;
				    int sourceID;
				    int k;
					if (p==LOW){ 
						k=jj*2;
						disp=[(jj==0?1:0),(jj==1?1:0),(jj==2?1:0)];
					}
					else{
						k=jj*2+1;
						disp=[(jj==0?-1:0),(jj==1?-1:0),(jj==2?-1:0)];
					}	
					source=dest-disp;
					//got Eclipse internal error if insert if (m_numPlace>1) here
					if ((m_numPlaces>1)&&PERF_On && (!PERF_CompOnly)) x10.lang.perf.addLocalOps((long)(PERF_CommL+PERF_CommB*m_boundaries[k,ii].size()));
					if (m_placeGrid.contains(source)){
						sourceID=m_placeGrid.ordinal(source);
						Util.arraycopy(m_u[ii].m_array,m_boundaries[k,ii],m_u[sourceID].m_array);
					}
					else{
						source=dest+disp*(m_block[jj]-1);
						sourceID=m_placeGrid.ordinal(source);
						trans=disp*m_size[jj];
						Util.arraycopy(m_u[ii].m_array,m_boundaries[k,ii],m_u[sourceID].m_array, trans);	
					}
					
				}
			}
		}
	}
	/*it seems more efficient to put for loop inside the ateach loop*/
	final void exchange(){
	  async{
		final clock clk=clock.factory.clock();	
		ateach(point [i]:m_places) clocked(clk){/*at each place in parallel*/
			for (int j=0;j<3;j++){ /*for each space dimension and has to be done sequentially*/
				final int jj=j;
				final point dest=m_placeGrid.coord(i); 
				final int ii=i; 
				finish foreach (point [p]:[LOW:HIGH]){ /*for each face in parallel*/
				//finish for (point [p]:[LOW:HIGH]){	
				    point disp, source,trans;
				    int sourceID;
				    int k;
					if (p==LOW){ 
						k=jj*2;
						disp=[(jj==0?1:0),(jj==1?1:0),(jj==2?1:0)];
					}
					else{
						k=jj*2+1;
						disp=[(jj==0?-1:0),(jj==1?-1:0),(jj==2?-1:0)];
					}	
					source=dest-disp;
					if ((m_numPlaces>1) && PERF_On && (!PERF_CompOnly)) 
						x10.lang.perf.addLocalOps((long)(PERF_CommL+PERF_CommB*m_boundaries[k,ii].size()));
					if (m_placeGrid.contains(source)){
						sourceID=m_placeGrid.ordinal(source);
						Util.arraycopy(m_u[ii].m_array,m_boundaries[k,ii],m_u[sourceID].m_array);
					}
					else{
						source=dest+disp*(m_block[jj]-1);
						sourceID=m_placeGrid.ordinal(source);
						trans=disp*m_size[jj];
						Util.arraycopy(m_u[ii].m_array,m_boundaries[k,ii],m_u[sourceID].m_array, trans);	
					}
					
				}
				next;
			}
		}
	  }	
	}
	public LevelData(final int a_problemDomainSize, final boolean a_isParallel){
		if (Util.OVERLAP_COMMANDCOMP)
			EXCHANGE_After=false;
		else
			EXCHANGE_After=Util.EXCHANGE_AFTER;
		
		SIZE=a_problemDomainSize;
		ISPARALLEL=a_isParallel;
		assert Util.powerOf2(SIZE);
		assert Util.powerOf2(N_PLACES);
		P_DOMAIN=[0:SIZE-1,0:SIZE-1,0:SIZE-1];
		//System.out.println("num of places: "+place.MAX_PLACES+" problem domain: "+P_DOMAIN);
		
		final int numCuts;
		if (ISPARALLEL){
			m_places=ALLPLACES;m_numPlaces=N_PLACES; 
			numCuts=Util.log2(N_PLACES);
		}
		else{
			m_places=ALLPLACES|[0:0];m_numPlaces=1;
			numCuts=0;
		}
		
		m_regions=new region [m_numPlaces];
		m_REGIONs=new region [m_numPlaces];
		//m_boundaries=new region [numFaces][m_numPlaces];
		m_boundaries =  new region [[0:5, 0:m_numPlaces-1]];
		m_dist=new dist [m_numPlaces];
		m_DIST=new dist [m_numPlaces];
		m_u=new Wrapper value [ALLPLACES];//must put value here!
		//m_u=new Wrapper [m_numPlaces];
		/*To be general*/
		m_low=[P_DOMAIN.rank(0).low(), P_DOMAIN.rank(1).low(), P_DOMAIN.rank(2).low()]; 
		m_hi=[P_DOMAIN.rank(0).high(), P_DOMAIN.rank(1).high(), P_DOMAIN.rank(2).high()]; 
		m_size=[m_hi[0]-m_low[0]+1, m_hi[1]-m_low[1]+1, m_hi[2]-m_low[2]+1];
		
		/*compute the cut of problem domain into N_PLACES subdomains*/
		int a=numCuts/3;
		int b=numCuts%3;
		m_cut=[a+(b>0?1:0),a+(b>1?1:0),a];
		m_block=[Util.pow2(m_cut[0]), Util.pow2(m_cut[1]),Util.pow2(m_cut[2])];
		m_blockSize=[m_size[0]/m_block[0],m_size[1]/m_block[1],m_size[2]/m_block[2]];
		m_placeGrid=[0:m_block[0]-1, 0:m_block[1]-1, 0:m_block[2]-1];
		int i,j,k,l;
		i=0;
		/*compute the global data layout*/
		for (point p: m_placeGrid){
			/*compute the subregions, one for each place*/
			m_regions[i]=[m_blockSize[0]*p[0]+m_low[0]: m_blockSize[0]*(p[0]+1)+m_low[0]-1, 
			              m_blockSize[1]*p[1]+m_low[1]: m_blockSize[1]*(p[1]+1)+m_low[1]-1,
			              m_blockSize[2]*p[2]+m_low[2]: m_blockSize[2]*(p[2]+1)+m_low[2]-1];
			m_REGIONs[i]=[m_blockSize[0]*p[0]+m_low[0]-1: m_blockSize[0]*(p[0]+1)+m_low[0], 
			              m_blockSize[1]*p[1]+m_low[1]-1: m_blockSize[1]*(p[1]+1)+m_low[1],
			              m_blockSize[2]*p[2]+m_low[2]-1: m_blockSize[2]*(p[2]+1)+m_low[2]];
			m_dist[i]=m_regions[i]->place.factory.place(i);
			m_DIST[i]=m_REGIONs[i]->place.factory.place(i);
			m_u[i]=new Wrapper(new double [m_DIST[i]]);
			//System.out.println(" "+i+": "+m_regions[i]+" 3d id="+m_placeGrid.coord(i));
			/*compute the boundary faces, the order of the faces are lowX,highX,lowY,highY,lowZ,highZ*/
			for (l=0;l<3;l++){
				j=l*2;
				m_boundaries[j,i]=Util.boundary(m_regions[i],-(l+1),[(l>0?1:0),(l>1?1:0),0]);
				m_boundaries[j+1,i]=Util.boundary(m_regions[i],(l+1),[(l>0?1:0),(l>1?1:0),0]);
				//System.out.println("   direction: "+l+" "+m_boundaries[j][i]+" "+m_boundaries[j+1][i]);
			}
			i++;//i=m_placeGrid.ordinal(p);
		}
		
	}
	
	
    public static void main(String[] a) {
    	/*throw new RuntimeException();*/
    	
    	final LevelData MG=new LevelData(32, true);//MG.location=place(0);
    	MG.set(1);
    	System.out.println("MG.initialize() finished");
    	MG.print();
    	MG.Exchange();
    	MG.print();
    	System.out.println("The 2norm is "+MG.norm2());
    	
    	System.out.println("This is LevelData, finished!");
    }
	
}
