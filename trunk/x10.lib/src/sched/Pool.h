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
namespace x10lib_cws {
	
enum {RUNNING=0,SHUTDOWN=1,STOP=2,TERMINATED=3};
const int INITIAL_JOBQUEUE_CAPACITY = 64;
const int INITIAL_WORKER_CAPACITY = 64;

class Pool {
private:
	PosixLock *lock;
	pthread_cond_t work;
	pthread_cond_t termination;
	int runningWorkers;
	volatile int runState;
	int activeJobs;
	JobQueue *jobs;
	volatile UncaughtExceptionHandler *ueh;
	void addJob(Job *job);
	
public:
	pthread_t *id;
	vector<Worker *> workers;
	unsigned int num_workers;
	int /*atomic */ joinCount;
  volatile Closure *currentJob;
  ActiveWorkerCount *barrier;
  int /*atomic*/ activeOnJobAtomic;
    
    
	Pool(int numThreads);
	~Pool();
	//UncaughtExceptionHandler *setUncaughtExceptionHandler(UncaughtExceptionHandler *h);
	int getPoolSize() const;
	int activeOnJob() const;
	bool isShutdown() const;
	bool isTerminated() const;
	void shutdown();
	void shutdownNow();
	bool awaitTermination(long timeout) ;
	bool isStopped() const;
	vector<worker *> *getWorkers() const;
	void submit(Job *job) ;
	void invoke(Job *job);
	long getStealCount();
	long getStealAttempts();
	void initFrameGenerator(FrameGenerator *fg);
	void workerTerminated(Worker *r, int index);
	void jobCompleted();
	Closure *getJob();
	
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
		isEmpty();
		void doubleCapacity();
		Job *poll();
		void add(Job *e);
		bool isQuiescent();
};

}
#endif

