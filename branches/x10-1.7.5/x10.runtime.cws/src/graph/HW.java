package graph;

/**
 * (c) IBM Corporation 2007
 * Author: Vijay Saraswat
 * 
 * 
 */

import x10.runtime.deal.*;

public class HW implements Executable {
	
	Executable next;
	public void setNext(Executable n) { next=n;}
	public Executable next() { return next;}
	public HW (){}
	public void compute(Worker w) {
		int phase = w.phaseNum();
		System.err.println(w + ": Hello World! (In phase " + phase+".)");
		if (phase< 50) 
			w.dealTask(this);
	}
	public String toString() { return "HW";}
	
	public static void main(String[] args) {

		final HW hw = new HW();
		Pool g = new Pool(2);

		Job job = new Job(g) {
			public void compute(Worker w) {
				hw.compute(w);
			}
		};
		g.invoke(job);
		System.err.println("HW main thread is Done.");

	}

	
}

