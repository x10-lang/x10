

/**
 * (c) IBM Corporation 2007
 * Author: Guojing Cong
 * Tong Wen
 * Vijay Saraswat
 */
import java.util.concurrent.atomic.AtomicInteger;
import x10.runtime.cws.*;



public class SpanC {
	
	class V {
		public int parent;
		public int degree;
		public int [] neighbors;
		public V(){}
	}
	
	
	class E{
		public int v1,v2;
		public boolean in_tree;
		public E(int u1, int u2){ v1=u1;v2=u2;in_tree=false;}
	}
	public static int N=100000,M=400000;
	int m;
	V[] G;
	E[] El;
	E[] El1;
	AtomicInteger [] color;
	int ncomps=0;
	
	static int[] Ns = new int[] {10*1000, 50*1000, 100*1000, 
		500*1000, 1000*1000, 5*1000*1000,10*1000*1000};
	
	
	public SpanC (){
		
		/*constructing edges*/
		El = new E [M];
		double[] seeds = new double[] {
				0.21699440277541715, 0.9099040926714322, 0.5586793832519766,
				0.15656203486110076, 0.3716929310972751, 0.6327328452004045,
				0.9854204833301402, 0.8671652950975213, 0.1079976151083556,
				0.5993517714916581
		};
		El = new E [M];
		for(int i=0;i<M;i++){
			El[i]=new 
			//E ((int) (Math.random()*N)%N, (int) (Math.random()*N)%N);
			E ((int) (seeds[i%10]*(N+i))%N, (int) (seeds[(i+1)%10]*(N+i))%N);
		}
		
		int[] D = new int [N];
		/* D[i] is the degree of vertex i (duplicate edges are counted).*/
		for(int i=0;i<M;i++){
			D[El[i].v1]++;
			D[El[i].v2]++;
		}
		
		int[][] NB = new int[N][];/*NB[i][j] stores the jth neighbor of vertex i*/
		// leave room for making connected graph by +2
		for(int i=0;i<N;i++) NB[i]=new int [D[i]+2]; 
		
		/*Now D[i] is the index for storing the neighbors of vertex i
		 into NB[i] NB[i][D[i]] is the current neighbor*/
		for(int i=0;i<N;i++) D[i]=0;
		
		m=0;
		for(int i=0;i<M;i++) {
			boolean r=false;;  
			/* filtering out repeated edges*/
			for(int j=0;j<D[El[i].v1] && !r ;j++){ 
				if(El[i].v2==NB[El[i].v1][j]) r=true;
			}
			if(r){
				El[i].v1=El[i].v2=-1; /*mark as repeat*/
			} else {
				m++;
				NB[El[i].v1][D[El[i].v1]]=El[i].v2;
				NB[El[i].v2][D[El[i].v2]]=El[i].v1;
				D[El[i].v1]++;
				D[El[i].v2]++;
			}
		}  
		
		/* now make the graph connected*/
		/* first we find all the connected comps*/
		
		color = new AtomicInteger [N];
		for (int i=0; i < color.length; i++) color[i] = new AtomicInteger();
		int[] stack = new int [N]; 
		int[] connected_comps  = new int [N]; 
		
		int top=-1;
		ncomps=0;
		
		for(int i=0;i<N && color[i].get() !=1;i++) {
			connected_comps[ncomps++]=i;
			stack[++top]=i;
			color[i].set(1);
			while(top!=-1) {
				int v = stack[top];
				top--;
				
				for(int j=0;j<D[v];j++)
					if(color[NB[v][j]].get()==0){
						top++;
						stack[top]=NB[v][j];
						color[NB[v][j]].set(1);
					}
			}
		}
		
		//System.out.println("ncomps="+ncomps);
		El1 = new E [m+ncomps-1]; 
		for(int i=0;i<N;i++) color[i].set(0);
		int j=0;
		//    Remove duplicated edges
		for(int i=0;i<M;i++) if(El[i].v1!=-1) El1[j++]=El[i]; 
		
		//if(j!=m) System.out.println("Remove duplicates failed");
		//else System.out.println("Remove duplicates succeeded,j=m="+j);
		
		/*add edges between neighboring connected comps*/
		for(int i=0;i<ncomps-1;i++) {
			NB[connected_comps[i]][D[connected_comps[i]]++]=connected_comps[i+1];
			NB[connected_comps[i+1]][D[connected_comps[i+1]]++]=connected_comps[i];
			El1[i+m]=new E (connected_comps[i], connected_comps[i+1]);
		}
		
		G = new V[N];
		for(int i=0;i<N;i++) {
			G[i]=new V();
			G[i].degree=D[i];
			G[i].neighbors=new int [D[i]];
			for(j=0;j<D[i];j++)
				G[i].neighbors[j]=NB[i][j];
		}     
	}
	
	public static Traverser makeTraverser(SpanC s, int u) {
		return new Traverser(new TFrame(s,u));
	}
	public static class TFrame extends Frame {
		final SpanC s;
		final int u; // vertex
		int k;
		public TFrame(SpanC s, int u) {
			this.s=s;
			this.u=u;
		}
		public void setOutlet(final Closure c) {
			c.setOutlet(null);
		}
	}
	public static final int LABEL_0 = 0, LABEL_1 = 1, LABEL_2=2;
	static void traverse(final Worker ws, final SpanC s, final int u) throws StealAbort {
		TFrame frame = new TFrame(s, u);
		frame.k=1;
		frame.PC=LABEL_0;
		ws.pushFrame(frame);
		int k=0;
		while (k < s.G[u].degree) {
			int v=s.G[u].neighbors[k];
			boolean result = s.color[v].compareAndSet(0,1);
			if (result) {
				s.G[v].parent=u;
				traverse(ws,s,v);
				Closure c = ws.popFrameCheck();
				if (c!=null) throw new StealAbort();
			}
			++k;
			frame.k=k;
			frame.PC=LABEL_0; // to publish the f.k assignment.
		}
		ws.popFrame();
		return;
	}
	public static class Traverser extends Closure {
		public Traverser(TFrame t) { 
			super(t);
		}
		
		public void compute(Worker ws, Frame frame) {
			TFrame f = (TFrame) frame;
			final int u = f.u;
			final SpanC s = f.s;
			switch (f.PC) {
			case LABEL_0: 
				int k = f.k;
				while (k < s.G[u].degree) {
					int v=s.G[u].neighbors[k];
					boolean result = s.color[v].compareAndSet(0,1);
					if (result) {
						s.G[v].parent=u;
						try {
							traverse(ws,s,v);
						} catch(StealAbort z) {
							return;
						}
						Closure c = ws.popFrameCheck();
						if (c!=null) 
							return;
					}
					++k;
					f.k=k;
					f.PC=LABEL_0; // to publish the f.k assignment.
				}
				f.PC = LABEL_1;
				if (sync(ws)) 
					return;
				
				// There should be a way of signaling returning with no value.
			case LABEL_1: 
				setupReturn();
			}
			return;
		}
	}
	boolean verifyTraverse(int root) {
		int[] D = new int [N];
		for(int i=0;i<N;i++) D[i]=G[i].parent;
		for(int i=0;i<N;i++) while(D[i]!=D[D[i]]) D[i]=D[D[i]];
		for(int i=0;i<N;i++)
			if(D[i]!=root)  
				return false;
		return true;
	}
	
	public static void main(String[] args) {
		int procs;
		try {
			procs = Integer.parseInt(args[0]);
			System.out.println("Number of procs=" + procs);
		}
		catch (Exception e) {
			System.out.println("Usage: java SpanT <threads> <N>");
			return;
		}
		Pool g = new Pool(procs);
		for (int i=0; i < Ns.length; i++) {
			
			N = Ns[i]; M = 3*N/5;
			final SpanC graph = new SpanC();
			long s = System.nanoTime();
			Job task = new Job(g) {
				public int spawnTask(Worker ws) throws StealAbort { 
					makeTraverser(graph,0);
					return 0;
				}
			};
			g.submit(task);
			long t = System.nanoTime();
			System.out.println( "N=" + N + " " + graph.verifyTraverse(0) + " Time: "  + (t-s)/1000000 );
			System.gc();
			System.out.println("Finished gc.");
		}   
		g.shutdown(); 
	}
}

