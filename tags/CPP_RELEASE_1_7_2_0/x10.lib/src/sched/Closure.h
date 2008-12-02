/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: Closure.h,v 1.16 2007-12-26 07:57:34 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_XWS_CLOSURE_H
#define __X10_XWS_CLOSURE_H

//#include <x10/xws/Lock.h>
//#include <x10/xws/Worker.h>
#include <x10/xws/Sys.h>
#include <x10/xws/Executable.h>
#include <list>

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib_xws {

class PosixLock;
class Frame;
class Worker;
class Cache;
class Closure;
class Outlet;



enum { RUNNING, SUSPENDED, RETURNING, READY, ABORTING, PASSTHROUGH };

class Closure : public Executable {
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
	std::list<Closure *> completeInlets;
	std::list<Closure *> incompleteInlets;
	volatile bool done;
	
	virtual void compute(Worker *w, Frame *frame) ;
	void initialize();
	bool sync(Worker *ws);
	
 public: //for now
	Outlet *outlet;
	
public:
	Frame *frame;
	Cache *cache;
	Closure *parent;
	volatile int joinCount;
	Closure *nextReady, *prevReady;
	Worker *ownerReadyQueue;
	int status;

 	//Closure(); 
	Closure(Frame *frame);
	virtual ~Closure();
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
	void pollInlets(Worker *ws, Closure *causingChild=NULL);
	Closure *returnValue(Worker *ws);
	Closure *execute(Worker *w);
	void executeAsInlet();
	void setOutlet(Outlet *o);
	void copyFrame(Worker *w);
	bool isDone() volatile;

	virtual void completed() volatile;

	virtual void setResultInt(int x);
	virtual void accumulateResultInt(int x);
	virtual int resultInt();
	
	virtual void setupReturn(Worker *w);
	virtual void setupGQReturnNoArg(Worker *w);
	virtual void setupGQReturn(Worker *w);
	virtual void setupGQReturnNoArgNoPop(Worker *w);
		
	virtual void setResultFloat(float x);
	virtual void accumulateResultFloat(float x);
	virtual float resultFloat();
		
	virtual void setResultLong(long x);
	virtual void accumulateResultLong(long x);
	virtual long resultLong();
		
	virtual void setResultDouble(double x);
	virtual void accumulateResultDouble(double x);
	virtual double resultDouble();
		
	virtual void setResultObject(void *x);
	virtual void *resultObject();
	virtual bool requiresGlobalQuiescence() /*volatile*/;

 public:
#if defined(MEM_DEBUG) && (MEM_DEBUG!=0)
  static volatile int nCons, nDestruct;
#endif
  inline static void incCons() {
# if defined(MEM_DEBUG) && (MEM_DEBUG!=0)
    atomic_add(&nCons, 1);
# endif
  }
  inline static void incDestruct() {
# if defined(MEM_DEBUG) && (MEM_DEBUG!=0)
    atomic_add(&nDestruct, 1);
# endif
  }
	
};

} /* closing brace for namespace x10lib_xws */
#endif

#endif /* __X10_XWS_CLOSURE_H */
