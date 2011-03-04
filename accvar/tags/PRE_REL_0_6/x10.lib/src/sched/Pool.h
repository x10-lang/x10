/*
============================================================================
 Name        : Pool.h
 Author      : Rajkishore Barik
 Version     :
 Copyright   : IBM Corporation 2007
 Description : Exe source file
============================================================================
*/
#ifndef x10lib_Pool_h
#define x10lib_Pool_h
#include <pthread.h>
#include <stdlib.h>
#include <time.h>
#include <vector>

namespace x10lib_cws {
	
class JobQueue;
class FrameGenerator;
class Job;
class Worker;
class ActiveWorkerCount;
class PosixLock;
class Closure;

const int INITIAL_JOBQUEUE_CAPACITY = 64;
const int INITIAL_WORKER_CAPACITY = 64;

enum {EXECUTING=0,SHUTDOWN=1,STOP=2,TERMINATED=3};

class Pool {
private:
	//anonymous inner class
	friend class anon_ActiveWorkerCount;
private:
	PosixLock *lock_var;
	pthread_cond_t work;
	pthread_cond_t termination;
	int runningWorkers;
	volatile int runState;
	int activeJobs;
	JobQueue *jobs;
	//volatile UncaughtExceptionHandler *ueh;
	void addJob(Job *job);
	
	
public:
	pthread_t *id;
	std::vector<Worker *> workers;
	unsigned int num_workers;
	int /*atomic */ joinCount;
	volatile Job *currentJob;
	ActiveWorkerCount *barrier;
	int /*atomic*/ activeOnJobAtomic;

	class callBackFunc {
	public:
	  Pool *cl;
	  int id;
	  void each_thread(Pool *p,int a);
	  callBackFunc(Pool *_cl, int _id) : cl(_cl), id(_id){}
	};

	static void *each_thread_wrapper (void *);

	Job *getCurrentJob() volatile { return (Job *)currentJob; }
    
	/* @sriramk: Disabling the default constructor. This should
	   get the #threads from elsewhere and call Pool(int). But we
	   need to figure out interaction with the environment.  Until
	   then, we will ignore this.
	 */
/* 	Pool(); */
	Pool(int numThreads);
	~Pool();

	void barrierAction(Closure *c);
	//UncaughtExceptionHandler *setUncaughtExceptionHandler(UncaughtExceptionHandler *h);
	int getPoolSize() const;
	int activeOnJob() const;
	bool isShutdown() volatile;
	bool isTerminated() const;
	void shutdown();
	void shutdownNow();
	bool awaitTermination(const struct timespec *timeout) ;
	bool isStopped() const;
	//vector<Worker *> *getWorkers() const;
	void submit(Job *job) ;
	void invoke(Job *job);
	long getStealCount() const;
	long getStealAttempts() const;
	void initFrameGenerator(FrameGenerator *fg);
	void workerTerminated(Worker *r, int index);
	void jobCompleted();
	Closure *getJob() ;
	void tryTerminate() const;

	/*support for area-specific data */
	void allocateWorkerArea(int size /*in bytes*/); 
};


/**
 * A JobQueue is a simple array-based circular queue.
 * Basically a stripped-down variant of ArrayDeque
 */
class JobQueue {
 private:
  
  std::vector<Job *> elements;
  int head;
  int tail;
 public:
  JobQueue() ;
  ~JobQueue();
  bool isEmpty() const;
  void doubleCapacity();
  Job *poll();
  void add(Job *e);
  bool isQuiescent(std::vector<Worker *> &workers) const;
};

}
#endif

