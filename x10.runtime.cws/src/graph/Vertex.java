package graph;



import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import x10.runtime.cws.Frame;
public abstract class Vertex extends Frame {
	final static AtomicIntegerFieldUpdater<Vertex> UPDATER 
	= AtomicIntegerFieldUpdater.newUpdater(Vertex.class, "level");
	public static boolean reporting;
	int level() { return level;}
	int index() { return index;}
	abstract void makeRoot();
	abstract void addEdge(Vertex v);
	abstract void initNeighbors(int k);
	
	public final int index;
	public volatile int level;
	//int parent;
	abstract Vertex parent();
	abstract void reset();
	public final boolean tryColor() {
		return level == 0 && UPDATER.compareAndSet(this,0,1);
	}
	
	
	public Vertex(int i) { index=i;}
	public boolean verify(Vertex root, boolean[] reachesRoot, Vertex[] G) {
		int N=G.length; Vertex p = parent(), oldP=null;
		int count=0;
		try { 
			while (! (p==null || reachesRoot[p.index()] || p ==this || p == root|| count == N)) {
				oldP=p;
				p = p.parent();
				count++;
			}
			boolean result = (count < N && p != null && ( p==root || reachesRoot[p.index()]));
			reachesRoot[index()]=result;
			if (result) {
				p = parent(); 
				while (! (p==root || reachesRoot[p.index()])) {
					reachesRoot[p.index()] = true;
					p=p.parent();
				}
			}
			return result;
		} finally {
			if (true || reporting) {
				if (count > N-10) {
					System.out.println(Thread.currentThread() + " finds possibly bad guy " + this +
							"count=" + count + " p=" + p );
				}
				if (! reachesRoot[index()])
					System.out.println(Thread.currentThread() + " finds bad guy " + this +
							"count=" + count + " p=" + p  + " oldP=" + oldP);
			}
		}
	}
	
	

}
