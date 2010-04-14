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
	val id: int;
	protected val u: Rail[double];
        protected val v: Rail[double];
        protected val r: Rail[double];
        protected val a: Rail[double];
        protected val c: Rail[double];
	protected val nm: int;
        protected val num_threads: int;
	protected val mg: MG;

	protected var start: int;
	protected var end: int;
	protected var work: int;

	public var wend: int;
        public var wstart: int;

	protected def this(var mg0: MG, var id0: int): MGWorker = {
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

	protected def getWork(var wstart: int, var wend: int): void = {
		var workpt: int = (wend-wstart)/num_threads;
		var remainder: int = wend-wstart-workpt*num_threads;
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
