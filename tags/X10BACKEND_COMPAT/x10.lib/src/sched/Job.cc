/*
============================================================================
 Name        : Job.cpp
 Author      : Rajkishore Barik
 Version     :
 Copyright   : IBM Corporation 2007
 Description : Exe source file
============================================================================
*/



#include "Job.h"
#include "Frame.h"
#include "Lock.h"
#include "Closure.h"
#include "Worker.h"
#include "Pool.h"
#include "Sys.h"
#include <pthread.h>
#include <assert.h>
#include <iostream>

using namespace std;
using namespace x10lib_cws;

/*-----------------JobFrame----------------------*/

JobFrame::JobFrame():Frame(), PC(0), x(0) {}
JobFrame::~JobFrame() {}
Closure *JobFrame::makeClosure() {
	assert(0);
	return NULL;
}
void JobFrame::setOutletOn(Closure *c) {
#warning "Object created here. Should deallocate somewhere!"
	ResultOutlet *t = new ResultOutlet(c, this);
	assert(t != NULL);
	c->setOutlet(t);
}
		
JobFrame *JobFrame::copy() { return new JobFrame(*this); }

/*------------------ResultOutlet-------------------*/

// ResultOutlet::ResultOutlet(): Outlet() { }
ResultOutlet::ResultOutlet(Closure *c, JobFrame *f) 
  : Outlet() {
  closure = c;
  jframe = f;
}
void ResultOutlet::run() {
  //  cerr<<"ResultOutlet::run. closure="<<closure<<endl;
  jframe->x = closure->resultInt();
}



/*----------------------GFrame------------------*/	

void GFrame::setOutletOn(Closure *c) { assert(0); abort(); }
Closure *GFrame::makeClosure() { assert(0); abort(); return NULL;}
GFrame::GFrame():JobFrame(), PC(0) { }
GFrame *GFrame::copy() { return new GFrame(*this); }

/*---------------------Job---------------------------*/

// Job::Job() {}
// Job::Job(Pool *pool) : Job::Job(new JobFrame(), pool) {}

Job::Job(Pool *pool, Frame *f) : Closure(f) {
		this->pool=pool;
		parent = NULL;
		joinCount=0;
		status = READY;
		pthread_cond_init (&cond_done, NULL);
}
Job::~Job() {
  pthread_cond_destroy(&cond_done);
}
void Job::compute(Worker *w, Frame *frame) {
		int x;
		JobFrame *f = (JobFrame *) frame;
		switch (f->PC) {
		case LABEL_0: 
			f->PC=LABEL_1;
			MEM_BARRIER(); // TODO RAJ
			// spawning
			x = spawnTask(w);
			if(w->abortOnSteal(x)) 
			  return ;
			f->x=x;
		case LABEL_1: 
			f->PC=LABEL_2;
			MEM_BARRIER(); // TODO RAJ
			if (sync(w)) return;
		case LABEL_2: 
			setResultInt(f->x);
			setupReturn(w);
		default: return;
		}
		return;
}
int Job::spawnTask(Worker *ws) { assert(0); abort(); return 0; } // TODO RAJ child must provide an imple for this
void Job::completed() {
	Closure::completed();
	/*	if ( Worker.reporting)
			System.out.println(Thread.currentThread() + " completed. result=" + resultInt());*/
		/*
		synchronized(this) {
			notifyAll();
		}*/
		pthread_cond_broadcast(&cond_done);
		pool->jobCompleted();
}
void Job::waitForCompletion() {
		while (!isDone()) pthread_cond_wait(&cond_done,NULL);
}
    
bool Job::isCancelled() const { return false;}
    
bool Job::cancel(bool b) const { return false;}

int Job::getInt() {
#warning "Need to check sched_yield() and locks, etc."
  while(!isDone()) 
    sched_yield();
  return resultInt();
}


/*--------------------GloballyQuiescentJob-------------------*/

bool GloballyQuiescentJob::requiresGlobalQuiescence() const { return true;}	
// GloballyQuiescentJob::GloballyQuiescentJob(Pool *pool) 
//   : GloballyQuiescentJob(pool, new GFrame()) {}

GloballyQuiescentJob::GloballyQuiescentJob(Pool *pool, Frame *f) 
  : Job(pool, f) {
  parent = NULL;
  joinCount=0;
  status = READY;
}

void GloballyQuiescentJob::compute(Worker *w, Frame *frame) {
			GFrame *f = (GFrame *) frame;
			int PC = f->PC;
			f->PC=LABEL_1;
			MEM_BARRIER(); // TODO Raj
			if (PC==0) {
				// spawning
				int x = spawnTask(w);
				if(w->abortOnSteal(x))
				  return ;
				f->x=x;
				// Accumulate into result.
				int old = resultInt();
				accumulateResultInt(f->x);
				//if (Worker.reporting)
				//System.out.println( w + " " + this + " adds " + f.x + " to move " + old + " --> " + resultInt());
			}
			setupGQReturn(w);
}

