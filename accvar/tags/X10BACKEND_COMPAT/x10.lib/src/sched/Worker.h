/*
============================================================================
 Name        : Worker.h
 Author      : Rajkishore Barik
 Version     :
 Copyright   : IBM Corporation 2007
 Description : Exe source file
============================================================================
*/
#ifndef x10lib_Worker_h
#define x10lib_Worker_h


namespace x10lib_cws {


#define LOCK(lock)  lock->lock_wait_posix()
#define UNLOCK(lock)  lock->lock_signal_posix()


class Cache;
class Frame;
class Pool;
class Closure;
class PosixLock;

class FrameGenerator {
public:
		virtual Frame *make() = 0;
};

const int AWAKE = 0;
const int SLEEPING = -1;
const int WOKEN = 1;
const int SCANS_PER_SLEEP = 1;
const int SLEEP_NANOS_PER_SCAN = 0;

class Worker {
private:
	bool checkedIn;
	Closure *getTask(bool mainLoop);
	Closure *getTaskFromPool(Worker *sleeper);
	void wakeup();

	/*Managing exceptions. A simple mechanism to handle StealAbort
	 * and stack unwinding for now*/
	bool exception;
	bool hasThrownException() const { return exception; }
	void throwException() { exception = true; }

protected:
	Closure *top, *bottom;
	int randNext;
	bool done;
	Closure *closure;
	Pool *pool;
	PosixLock *lock_var; // dequeue_lock
	
	int rand();
	void setRandSeed(int seed);
	
	
public:
	Cache *cache;
	FrameGenerator *fg;

	unsigned int index; //TODO. move ot protected later

	Worker *lockOwner; /* the thread holding the lock. Unlike
			      Java, there is no package access. So
			      moving from protected to public*/ 
	

	void catchAllException() { exception = false; }

	void unlock();
	void lock(Worker *agent);
	long stealAttempts; // change them to be visible across objects
	long stealCount; // change them to be visible across objects
	static bool reporting; 
	int idleScanCount;
	volatile /*atomic */ int sleepStatus;

	/*protected static  Worker[] workers; */
//	Worker();
	Worker(int index, Pool *p);
	~Worker();

	bool sync(Closure *c);
	Closure *scanTasks();
	void run(); // Main execution of the scheduler
	void pushFrame(Frame *frame);
	void popFrame();
	Closure *getbottom();
	Closure *interruptCheck();
	bool abortOnSteal() /*throw StealAbort*/;
	bool abortOnSteal(int x) /*throw StealAbort*/;
	bool abortOnSteal(double x) /*throw StealAbort*/;
	bool abortOnSteal(float x) /*throw StealAbort*/;
	bool abortOnSteal(long x) /*throw StealAbort*/;
	//void abortOnSteal(Object x) throw StealAbort;
	void pushIntUpdatingInPlace(int x) ;
	void setFrameGenerator(FrameGenerator *x);
	void checkOutSteal(Closure *cl, Worker *victim);
	void checkIn();
	void checkOut(Closure *cl);
	bool isActive() const;
	Closure *currentJob() const;
	void addBottom(Worker *ws, Closure *cl);
	Closure *exceptionHandler();
	Closure *peekBottom(Worker *ws);
	Closure *extractBottom(Worker *ws);
	Closure *peekTop(Worker *agent, Worker *subject);
	Closure *extractTop(Worker *ws);
	Closure *steal(Worker *thief, bool retry);

};
}
#endif
