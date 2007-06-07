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

class FrameGenerator {
		virtual Frame *make() = 0;
}
const int AWAKE = 0;
const int SLEEPING = -1;
const int WOKEN = 1;
const int SCANS_PER_SLEEP = 1;
const int SLEEP_NANOS_PER_SCAN = 0;

public class Worker {
private:
	bool checkedIn = true;
	Closure *getTask(bool mainLoop);
	Closure *getTaskFromPool(Worker *sleeper);
	void wakeup();

protected:
	Closure *top, *bottom;
	int randNext;
	unsigned int index;
	bool done;
	Closure *closure;
	Pool *pool;
	
	Closure *exceptionHandler();
	void addBottom(Worker *ws, Closure *cl);
	Closure peekBottom(Worker *ws);
	Closure extractBottom(Worker *ws);
	Closure peekTop(Worker *agent, Worker *subject);
	Closure extractTop(Worker *ws);
	Closure steal(Worker *thief, bool retry);
	int rand();
	void setRandSeed(int seed);
	void unlock();
	void lock(Worker *agent);
	
	
public:
	Cache *cache;
	Lock *lock; // dequeue_lock
	Worker *lockOwner; // the thread holding the lock.
	static long stealAttempts = 0; // change them to be visible across objects
	static long steals = 0; // change them to be visible across objects
	static bool reporting = false; 
	volatile /*atomic */ int sleepStatus;

	/*protected static  Worker[] workers; */
	Worker(int index);
	bool sync();
	Closure *scanTasks();
	void run(); // Main execution of the scheduler
	void pushFrame(Frame *frame);
	void popFrame();
	Closure *bottom();
	Closure *interruptCheck();
	void pushIntUpdatingInPlace(int x) ;
	void setFrameGenerator(FrameGenerator *x);
	void checkOutSteal(Closure *cl, Worker *victim);
	void checkIn();
	void checkOut(Closure *cl);
};
}
#endif
