/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
package NPB3_0_X10.MGThreads;

import NPB3_0_X10.MG;

abstract public class MGWorker {
	final int id;
	final protected double u[], v[], r[], a[], c[];
	final protected int nm, num_threads;
	final protected MG mg;

	protected int start;
	protected int end;
	protected int work;

	public int wend, wstart;

	protected MGWorker(MG mg0, int id0) {
		mg = mg0;
		id = id0;
		//initialize shared data
		num_threads = mg.num_threads;
		r = mg.r;
		u = mg.u;
		c = mg.c;
		nm = mg.nm;
		v = mg.v;
		a = mg.a;
	}

	protected void getWork(int wstart, int wend) {
		int workpt = (wend-wstart)/num_threads;
		int remainder = wend-wstart-workpt*num_threads;
		if (workpt == 0) {
			if (id<wend-wstart) {
				work = 1;
				start = end = wstart+id;
			} else {
				work = 0;
			}
		} else {
			if (id<remainder) {
				workpt++;
				start = wstart+workpt*id;
			} else {
				start = wstart+remainder+workpt*id;
			}
			end = start+workpt-1;
			work = workpt;
		}
	}
}

