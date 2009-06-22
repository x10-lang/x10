/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: Job.cc,v 1.21 2007-12-26 07:57:34 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#include <x10/xws/Job.h>
#include <x10/xws/Frame.h>
#include <x10/xws/Lock.h>
#include <x10/xws/Closure.h>
#include <x10/xws/Cache.h>
#include <x10/xws/Worker.h>
#include <x10/xws/Pool.h>
#include <x10/xws/Sys.h>
#include <x10/xassert.h>
#include <pthread.h>
#include <iostream>

using namespace std;
using namespace x10lib_xws;

/*-----------------JobFrame----------------------*/

Closure *JobFrame::makeClosure() {
	assert(0);
	return NULL;
}
void JobFrame::setOutletOn(Closure *c) {
  /*Outlets are deleted by the closures to which they are attached*/
  ResultOutlet *t = new ResultOutlet(c, this);
  assert(t != NULL);
  c->setOutlet(t);
}
		
JobFrame *JobFrame::copy() { return new JobFrame(*this); }

/*-------------------Outlet------------------------*/

#if defined(MEM_DEBUG) && (MEM_DEBUG!=0)
volatile int Outlet::nCons=0;
volatile int Outlet::nDestruct=0;
#endif

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

void GFrame::setOutletOn(Closure *c) { /*assert(0); abort(); */}
Closure *GFrame::makeClosure() { /*assert(0); abort(); */return NULL;}
GFrame::GFrame():JobFrame(), PC(0) { }
GFrame *GFrame::copy() { return new GFrame(*this); }

/*---------------------Job---------------------------*/

// Job::Job() {}
// Job::Job(Pool *pool) : Job::Job(new JobFrame(), pool) {}

Job::Job(Pool *pool, Frame *f) : Closure(f), jobDone(false) {
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
			if(w->abortOnSteal(x)) {
			  return ;
			}
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
	pthread_cond_broadcast(&cond_done);
	pool->jobCompleted();
}
void Job::waitForCompletion() {
		while (!isDone()) { pthread_cond_wait(&cond_done,NULL); }
}
    
bool Job::isCancelled() const { return false;}
    
bool Job::cancel(bool b) const { return false;}

int Job::getInt() {
//#warning "Need to check sched_yield() and locks, etc."
//   while(!isDone()) 
//     sched_yield();
  while(!isJobDone()) 
    sched_yield();
  return resultInt();
}

void Job::jobCompleted() {
  jobDone = true;
}

bool Job::isJobDone() volatile { return jobDone; }

/*--------------------GloballyQuiescentJob-------------------*/

bool GloballyQuiescentJob::requiresGlobalQuiescence() { return true;}	
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



/*------------------------GloballyQuiescentVoidJob----------------*/

/*virtual*/ void 
GloballyQuiescentVoidJob::compute(Worker *w, Frame *frame) { 
  assert(!w->hasThrownException());
  frame->compute(w);

  if(w->cache->interrupted()) {
    w->throwException();
    return;
  }
  //w->cache->reset();
  // The completion of the job might leave behind work (frames).
  // Do not pop the framestack.
  setupGQReturnNoArgNoPop(w);
  //setupGQReturnNoArg(w);
}
