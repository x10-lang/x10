/*
============================================================================
 Name        : Job.h
 Author      : Rajkishore Barik
 Version     :
 Copyright   : IBM Corporation 2007
 Description : Exe source file
============================================================================
*/
#ifndef x10lib_Job_h
#define x10lib_Job_h
#include "Frame.h"
#include "Lock.h"
#include "Closure.h"
#include "Worker.h"
#include "Pool.h"
#include <pthread.h>


namespace x10lib_cws {

class Outlet;
class Closure;
class Worker;
class Pool;
class JobFrame;
class Frame;
class ResultOutlet;
class GFrame;
class Job;

enum { LABEL_0=0,LABEL_1=1, LABEL_2=2, LABEL_3=3};
class JobFrame : public Frame {
public :
	volatile int PC;
	int x;
	JobFrame();
	~JobFrame();
	Closure *makeClosure();
	void setOutletOn(Closure *c);
};

class Outlet {
public:
		virtual void run() {};
};

class ResultOutlet : public Outlet {
public:
	Closure *closure;
	JobFrame *jframe;
	void run();
	ResultOutlet();
	ResultOutlet(Closure *c, JobFrame *f);
};
class GFrame : public JobFrame {
public:
	volatile int PC;
	void setOutletOn(Closure *c);
	Closure *makeClosure();
	GFrame();
};

class Job : public Closure {
	
protected:
	void compute(Worker *w, Frame *frame) ;
	
public:
	Pool *pool;
	
	void completed();
	void waitForCompletion();
	bool isCancelled() const ;
	bool cancel(bool b) const;
	pthread_cond_t cond_done;

	Job();
	Job(Pool *pool);
	Job(Frame *f, Pool *pool);
	~Job();
	int spawnTask(Worker *ws) ;
};

class GloballyQuiescentJob : public Job {
public:
	GloballyQuiescentJob(Pool *pool);
	GloballyQuiescentJob(Pool *pool, Frame *f);
	bool requiresGlobalQuiescence() const;
	void compute(Worker *w, Frame *frame) ;
};


}
#endif
