/*
============================================================================
 Name        : Closure.h
 Author      : Rajkishore Barik
 Version     :
 Copyright   : IBM Corporation 2007
 Description : Exe source file
============================================================================
*/
#ifndef x10lib_Closure_h
#define x10lib_Closure_h
//#include "Lock.h"
#include "Worker.h"
#include <list.h>

namespace x10lib_cws {

class Frame;
class Worker;
class Cache;
class Closure;
class Outlet;



enum { RUNNING, SUSPENDED, RETURNING, READY, ABORTING, PASSTHROUGH };

class Closure {
	
private:
	/*void decrementExceptionPointer(Worker *ws);
	void incrementExceptionPointer(Worker *ws);
	void resetExceptionPointer(Worker *ws);*/
	void signalImmediateException(Worker *ws);
	
	Closure *closureReturn(Worker *w);
	Closure *acceptChild(Worker *ws, Closure *child);
	Closure *provablyGoodStealMaybe(Worker *ws, Closure *child); 
	
protected:
	PosixLock *lock_var;
	Worker *lockOwner;
	list<Closure *> completeInlets;
	list<Closure *> incompleteInlets;
	Outlet *outlet;
	bool done;
	
	void compute(Worker *w, Frame *frame) ;
	void initialize() const ;
	bool sync(Worker *ws);
	
	
public:
	Frame *frame;
	Cache *cache;
	Closure *parent;
	int joinCount;
	Closure *nextReady, *prevReady;
	Worker *ownerReadyQueue;
	int status;

	Closure();
	Closure(Frame *frame);
	~Closure();
	bool hasChildren() const ;
	int getstatus() const ;
	Frame *parentFrame() const ;
	Closure *getparent() const ;
	void lock(Worker *agent);
	void unlock();
	void addCompletedInlet(Closure *child);
	void removeChild(Closure *child);
	
	Closure *promoteChild(Worker *thief, Worker *victim);
	void finishPromote(Worker *thief, Closure *child);
	bool dekker(Worker *thief);
	bool handleException(Worker *ws);
	void suspend(Worker *ws);
	void pollInlets(Worker *ws);
	Closure *returnValue(Worker *ws);
	Closure *execute(Worker *w);
	void executeAsInlet();
	void setOutlet(Outlet *o);
	void copyFrame(Worker *w);
	bool isDone() const;
	void completed() ;
	void setResultInt(int x);
	void accumulateResultInt(int x);
	int resultInt();
	void setupReturn(Worker *w);
	void setupGQReturnNoArg(Worker *w);
	void setupGQReturn(Worker *w);
		
	void setResultFloat(float x);
	void accumulateResultFloat(float x);
	float resultFloat();
		
	void setResultLong(long x);
	void accumulateResultLong(long x);
	long resultLong();
		
	void setResultDouble(double x);
	void accumulateResultDouble(double x);
	double resultDouble();
		
	void setResultObject(void *x);
	void *resultObject();
	bool requiresGlobalQuiescence();
	
};
}
#endif
