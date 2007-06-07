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
namespace x10lib_cws {

class Outlet {
public:
		virtual void run() = 0;
}

enum Status {
				RUNNING,
				SUSPENDED,
				RETURNING,
				READY,
				ABORTING,
				PASSTHROUGH
}
class Closure {
		
	
private:
	void decrementExceptionPointer(Worker *ws);
	void incrementExceptionPointer(Worker *ws);
	void resetExceptionPointer(Worker *ws);
	void signalImmediateException(Worker *ws);
	
	Closure *closureReturn(Worker w);
	Closure *acceptChild(Worker *ws, Closure *child);
	Closure *provablyGoodStealMaybe(Worker *ws, Closure *child); 
	
protected:
	Cache *cache;
	Closure *parent;
	int status;
	Lock *lock;
	Worker *lockOwner;
	list<Closure *> completeInlets;
	list<Closure *> incompleteInlets;
	Worker *ownerReadyQueue;
	Closure *nextReady, *prevReady;
	Outlet *outlet;
	bool done;
	
	int status();
	bool hasChildren();
	void compute(Worker *w, Frame *frame);
	void initialize();
	bool sync(Worker *ws);
	void setupReturn(worker *w);
	void setupGQReturnNoArg(Worker *w);
	void setupGQReturn(Worker *w);
	
	
public:
	Frame *frame;
	int joinCount;

	Closure(Frame *frame);
	~Closure();
	Frame *parentFrame();
	Closure *parent();
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
	bool isDone();
	void completed();
	void setResultInt(int x);
	void accumulateResultInt(int x);
	int resultInt();
		
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
