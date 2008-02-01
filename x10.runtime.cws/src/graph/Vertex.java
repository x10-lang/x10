package graph;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import x10.runtime.cws.Frame;


public abstract class Vertex extends Frame {
	final static AtomicIntegerFieldUpdater<Vertex> UPDATER 
	= AtomicIntegerFieldUpdater.newUpdater(Vertex.class, "level");
	public static boolean reporting;
	public final int index;
	public volatile int level;
	abstract void makeRoot();
	abstract void addEdge(Vertex v);
	abstract void initNeighbors(int k);
	int parent;
	abstract void reset();
	public Vertex(int i) { this.index=i;}
	public boolean tryLabeling() {
		return level == 0 && UPDATER.compareAndSet(this,0,1);
	}
	public boolean verify(Vertex root, boolean[] reachesRoot, Vertex[] G) {
		int N=G.length, p = parent, oldP=-1;
		int count=0;
		try { 
			while (! (p==-1 || reachesRoot[p] || p ==this.index || p == root.index || count == N)) {
				oldP=p;
				p = G[G[p].parent].index;
				count++;
			}
			boolean result = (count < N && p != -1 && ( p==root.index || reachesRoot[p]));
			reachesRoot[index]=result;
			return result;
		} finally {
			if (reporting) {
				if (count > N-10) {
					System.err.println(Thread.currentThread() + " finds possibly bad guy " + this +
							"count=" + count + " p=" + p );
				}
				if (! reachesRoot[index])
					System.err.println(Thread.currentThread() + " finds bad guy " + this +
							"count=" + count + " p=" + p  + " oldP=" + oldP);
			}
		}
	}
	
	

}
