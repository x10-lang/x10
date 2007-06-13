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
#include "ActiveWorkerCount.h"
#include "Job.h"
#include <pthread.h>
#include <stdlib.h>
#include <time.h>
#include <vector.h>

namespace x10lib_cws {
	
class JobQueue;
class FrameGenerator;
class Job;
class Worker;
class ActiveWorkerCount;



const int INITIAL_JOBQUEUE_CAPACITY = 64;
const int INITIAL_WORKER_CAPACITY = 64;

enum {EXECUTING=0,SHUTDOWN=1,STOP=2,TERMINATED=3};

class Pool {
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
	vector<Worker *> workers;
	unsigned int num_workers;
	int /*atomic */ joinCount;
  volatile Closure *currentJob;
  ActiveWorkerCount *barrier;
  int /*atomic*/ activeOnJobAtomic;

	typedef struct {
		Pool *cl;
		int id;
		void each_thread(Pool *p,int a);
	} callBackFunc;

	callBackFunc ptToFunc;
	static void *each_thread_wrapper (void *);

    
    
	Pool();
	Pool(int numThreads);
	~Pool();
	void barrierAction(Closure *c);
	//UncaughtExceptionHandler *setUncaughtExceptionHandler(UncaughtExceptionHandler *h);
	int getPoolSize() const;
	int activeOnJob() const;
	bool isShutdown() const;
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
	
};


/**
 * A JobQueue is a simple array-based circular queue.
 * Basically a stripped-down variant of ArrayDeque
 */
class JobQueue {
	private:
		
		vector<Job *> elements;
    	int head;
    	int tail;
	public:
		JobQueue() ;
		~JobQueue();
		bool isEmpty() const;
		void doubleCapacity();
		Job *poll();
		void add(Job *e);
		bool isQuiescent(vector<Worker *> &workers) const;
};

}
#endif

