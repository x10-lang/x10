/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: Pool.cc,v 1.21 2007-12-26 12:53:11 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#include <x10/xws/Pool.h>
#include <x10/xws/ActiveWorkerCount.h>
#include <x10/xws/Runnable.h>
#include <x10/xws/Job.h>
#include <x10/xws/Lock.h>
#include <x10/xws/Worker.h>
#include <x10/xws/Sys.h>
#include <x10/xassert.h>
#include <stdlib.h>
#include <strings.h>
#include <iostream>

using namespace x10lib_xws;
using namespace std;


Pool::~Pool() {
	int i;

	for(i=0; i<workers.size(); i++) {
	  if(workers[i] != NULL) {
	    pthread_join(id[i], (void **)NULL);
	    free(workers[i]->area());
	  }
	}
	free(id);

 	for(i=0;i<workers.size();i++)
 	  delete workers[i];
	workers.clear();
	pthread_cond_destroy(&work);
	pthread_cond_destroy(&termination);
	delete lock_var;
	delete barrier;
	delete jobs;
}


void Pool::callBackFunc::each_thread(Pool *p,int d)
{
	 Worker *ws = new Worker(d, p);
	assert(ws != NULL);
	 p->workers[d]=ws;
	 MEM_BARRIER();
     //barrier->barrier();
     /*if (id == 0)
	  ws->run(invoke_main);
     else
      ws->run((Closure *) NULL);*/
    ws->run();
    //cerr<<id<<":: returned from Worker::run()"<<endl;
}

void *Pool::each_thread_wrapper(void *arg) {
  assert(arg != NULL);
	callBackFunc *cbf = (callBackFunc *) arg;
	assert(cbf!=NULL);
	cbf->each_thread(cbf->cl, cbf->id);
	delete cbf;
	return NULL;
}


// Pool::Pool() { }

/*Anonymous innner class argument to ActiveWorkerCount from Java code*/
//class anon_Runnable : public virtual Runnable {
class anon_Runnable : public Runnable {
private:
  Pool *p;
  ~anon_Runnable(){} //cannot inherit
public:
  anon_Runnable(Pool *p) {
    this->p = p;
  }
  //virtual void run() {
  void run() {
    Job *job = (Job *) p->currentJob;
    if (job != NULL && 
	job->requiresGlobalQuiescence()) {
       job->completed();
       job->jobCompleted();
    }
    p->currentJob = NULL;
  }
};

Pool::Pool(int numThreads) {
	
	long i;
	int res;
	pthread_attr_t attr;
	
	if (numThreads <=0) { cout << "Illegal argument"; abort(); }

	currentJob = NULL;
	
	lock_var = new PosixLock();
	assert(lock_var != NULL);

	pthread_cond_init (&work, NULL);
	pthread_cond_init (&termination, NULL);

	barrier = new ActiveWorkerCount(new anon_Runnable(this));
	assert(barrier!= NULL);

	num_workers = numThreads;
	runningWorkers = 0;
	runState = 0;
	activeJobs = 0;
	activeOnJobAtomic = 0;
	jobs =  new JobQueue();
	assert(jobs != NULL);
	joinCount = 0;

	workers.resize(numThreads);
	for(i=0; i<workers.size(); i++) {
	  workers[i] = NULL;
	}

	  
	id = (pthread_t *)malloc(num_workers * sizeof(pthread_t));
	assert(id != NULL);

	pthread_attr_init(&attr); 
	pthread_attr_setscope(&attr, PTHREAD_SCOPE_SYSTEM); 
	 
	lock_var -> lock_wait_posix();

	callBackFunc *ptToFunc;
	  
	for (i = 0; i < num_workers; i++)
	{
	  ptToFunc = new callBackFunc(this, i);
	  assert(ptToFunc != NULL);
// 	  ptToFunc.id = i;
// 	  ptToFunc.cl = this;
	  res = pthread_create(id + i, 
			       &attr,
			       Pool::each_thread_wrapper, 
			       (void *) ptToFunc);
	  if (res) {cout << "could not create"; abort(); }
	  runningWorkers++;
	  
	}
	lock_var -> lock_signal_posix();
}

/*
Pool::shutDown() {

	for (i = 0; i < num_workers; i++)
	{
		  res = pthread_join(id[i], NULL);
		  if (res) cout << "could not join";
	}
}*/


/**
     * Sets the handler for internal worker threads that terminate due
     * to uncaught Errors or other unchecked Throwables encountered
     * while executing tasks. Since such errors are not in generable
     * recoverable, they are not managed by ForkJoinTasks themselves,
     * but instead cause threads to die, invoking the handler, and
     * then being replaced.  In many usage contexts, it is probably a
     * better ideas to install a handler to either shutdown the pool
     * or exit the program when such errors occur.  Unless set, the
     * current default or ThreadGroup handler is used as handler.
     * @param h the new handler
     * @return the old handler, or null if none
     */
/*
UncaughtExceptionHandler *Pool::setUncaughtExceptionHandler(UncaughtExceptionHandler *h) {
        UncaughtExceptionHandler *old = NULL;
				abort(); // TODO RAJ
        const PosixLock *l = this->lock_var;
        l->lock_wait_posix();
        old = ueh;
        ueh = h;
        for (int i = 0; i < workers.size(); ++i) {
        	Worker *w = workers[i];
            if (w != NULL)
                    w.setUncaughtExceptionHandler(h);
            
        }
        l->lock_signal_posix();
        return old;
}
*/

int Pool::getPoolSize() const {
        return workers.size();
}
int Pool::activeOnJob() const {
    	return activeOnJobAtomic;
}

bool Pool::isShutdown() volatile {
        return runState >= SHUTDOWN;
}

bool Pool::isTerminated() const {
        return runState == TERMINATED;
}

void Pool::initFrameGenerator(FrameGenerator *fg) {
        for (int i = 0; i < workers.size(); ++i) {
            Worker *t = workers[i];
            t->setFrameGenerator(fg);
        }
}

   /**
     * Initiates an orderly shutdown in which previously submitted
     * tasks are executed, but no new tasks will be accepted.
     * Invocation has no additional effect if already shut down.
     */
void Pool::shutdown() {
        // todo security checks??
  PosixLock *l = this->lock_var;
  l->lock_wait_posix();

  if (runState < SHUTDOWN) {
    runState = SHUTDOWN;
	  MEM_BARRIER(); // for volatile update -- TODO RAJ
  }
  l->lock_signal_posix();
	// TODO Would you like to join all the ptrheads
	//cerr<<"Waiting for threads to join"<<endl;
    	for(int i=0; i<workers.size(); i++) {
	  if(workers[i] != NULL) {
	    lock_var->lock_wait_posix();
	    pthread_cond_broadcast(&work);
	    lock_var->lock_signal_posix();
	    int res = pthread_join(id[i], NULL);
	    if(res) cerr<<"Could not join with thread!"<<endl;
	  }
	  workers[i] = NULL;
	}
}

    /**
     * Attempts to stop all actively executing tasks, and halts the
     * processing of waiting tasks.
     */
void Pool::shutdownNow() {
        PosixLock *l = this->lock_var;
        l->lock_wait_posix();
        //tryTerminate 
        l->lock_signal_posix();
				// TODO Would you like to join all the ptrheads
}

// RAJ:: timeout is used as it is without conversion
bool Pool::awaitTermination(const struct timespec *timeout) {
        
        PosixLock *l = this->lock_var;
        l->lock_wait_posix();
        
        for (;;) {
                if (runState == TERMINATED)
                    return true;
                if (timeout->tv_nsec <= 0)
                    return false;
                // nanos = termination.awaitNanos(timeout); // TODO wait for timeout nanos RAJ
								pthread_cond_timedwait(&termination,&this->lock_var->posix_lock_var,timeout);

            }
        l->lock_signal_posix();
}
// Internal methods that may be invoked by workers

bool Pool::isStopped() const {
     return runState >= STOP;
}

 /**
  * Return the workers array; needed for random-steal by Workers.
  */
/*
vector<Worker *> &Pool::getWorkers() const {
     return &workers;
}
*/
 
void Pool::submit(Job *job) {
    	//startTime = System.nanoTime();
        addJob(job);
}
void Pool::invoke(Job *job) {
    	submit(job);
    	//try {
    	job->waitForCompletion();
		//} catch (InterruptedException z) {} // TODO
}

/**
     * Enqueue an externally submitted task
     */
void Pool::addJob(Job *job) {
        PosixLock *l = this->lock_var;
        bool ok;
        l->lock_wait_posix();
        
        if (ok = (runState == EXECUTING)) {
                jobs->add(job);
                //work.signalAll();
                pthread_cond_broadcast(&work);
        }
        
        l->lock_signal_posix();
        
        if (!ok) { abort(); }
            //throw RejectedExecutionException();
}

/**
     * Returns the total number of tasks stolen from one thread's work
     * queue by another. This value is only an approximation,
     * obtained by iterating across all threads in the pool, but may
     * be useful for monitoring and tuning fork/join programs.
     * @return the number of steals.
     */
long Pool::getStealCount() const {
        long sum = 0;
        for (int i = 0; i < workers.size(); ++i) {
            Worker *t = workers[i];
            if (t != NULL) 
                sum += t->stealCount;
        }
        return sum;
}
    /**
     * Returns the total number of steal attempts made by all workers.
     * This value is only an approximation,
     * obtained by iterating across all threads in the pool, but may
     * be useful for monitoring and tuning fork/join programs.
     * @return the number of steal attempts.
     */
long Pool::getStealAttempts() const {
    	long sum = 0;
      for (int i = 0; i < workers.size(); ++i) {
            Worker *t = workers[i];
            if (t != NULL) 
                sum += t->stealAttempts;
      }
      return sum;
}
    /**
     * Termination callback from dying worker.
     */
void Pool::workerTerminated(Worker *r, int index) {
  /*sriramk: Not thought through the semantics in C++. The new in this
    function does not have a corresponding delete either. So
    aborting. */
  assert(0); 

        PosixLock *l = this->lock_var;
        l->lock_wait_posix();
        
        if (runState >= STOP) {
        	if (--runningWorkers <= 0) {
        		runState = TERMINATED;
						MEM_BARRIER(); // TODO RAj
                    //termination.signalAll();
        		pthread_cond_broadcast(&termination);
            }
        }
        else if (index >= 0 && index < workers.size() &&
                     workers[index] == r) {
        	abort();
        	/* // TODO Confirm from Vijay
            Worker *replacement = new Worker(this, index);
			assert(replacement != NULL);
            if (ueh != NULL)
                    replacement.setUncaughtExceptionHandler(ueh);
                workers[index] = replacement;
                replacement.start();
                */
         }
        
         l->lock_signal_posix();
}

void Pool::jobCompleted() {
        PosixLock *l = this->lock_var;
        l->lock_wait_posix();
        
        if (--activeJobs <= 0 && runState == SHUTDOWN && jobs->isEmpty())
                tryTerminate();
        
        l->lock_signal_posix();
        
}
void Pool::tryTerminate() const {
    	// do nothing.
}
Closure *Pool::getJob() {
        
  lock_var->lock_wait_posix();

  Job *task = jobs->poll();
  if (task == NULL && activeJobs == 0) {
    //work.await(); // TODO RAJ
    //cerr<<"waiting on condition"<<endl;
    pthread_cond_wait(&work,&(this->lock_var->posix_lock_var));
    //cerr<<"woke up from condition"<<endl;
    task=jobs->poll();
  }
  if (task != NULL) {
    ++ activeJobs;
    currentJob = task;
    MEM_BARRIER(); // Let the update be visible to everyone -- TODO RAJ
    //work.signalAll(); // TODO RAJ
    pthread_cond_broadcast(&work);
  }
  //} catch(InterruptedException e) { return NULL; }
  lock_var->lock_signal_posix();
  return task;
}

/*support for area-specific data */
void Pool::allocateWorkerArea(int size /*in bytes*/) {
  for(int i=0; i<workers.size(); i++) {
    while(workers[i] == NULL)
      sched_yield();
    assert(workers[i] != NULL);
    if(workers[i] != NULL) {
      void *area = malloc(size);
      assert(area != NULL);
      bzero(area, size);
      workers[i]->setArea(area);
      //cerr<<"Pool. set area="<<area<<" for worker id="<<i<<endl;
    }
  }
  MEM_BARRIER();
}

/*-------------------JobQueue--------------------*/

JobQueue::JobQueue() {
	elements.resize(INITIAL_JOBQUEUE_CAPACITY);
	head = tail = 0;
}
JobQueue::~JobQueue() {
  elements.clear();
}

bool JobQueue::isEmpty() const {
	return head == tail;
}

void JobQueue::add(Job *e) {
        elements[tail] = e;
        if ( (tail = (tail + 1) & (elements.size() - 1)) == head)
            doubleCapacity();
}

Job *JobQueue::poll() {
        int h = head;
        Job *result = elements[h]; 
        if (result != NULL) {
            elements[h] = NULL;
            head = (h + 1) & (elements.size() - 1);
        }
        return result;
}
   
void JobQueue::doubleCapacity() {
				int i;
        int p = head;
        int n = elements.size();
        int r = n - p; 
        int newCapacity = n << 1;
        if (newCapacity < 0) { abort();} // TODO throw exception if needed }
        elements.resize(newCapacity); 

				// TODO RAJ: please optimize this code later
				vector<Job *> tmp(INITIAL_JOBQUEUE_CAPACITY);
        for(i=p; i<n; i++) tmp[i-p]=elements[i];
				for(i=0;i<p;i++) tmp[r+i] = elements[i];
				for(i=0;i<n;i++) elements[i] = tmp[i];
				tmp.clear();
        head = 0;
        tail = n;
}
bool JobQueue::isQuiescent(vector<Worker *> &workers) const{
        for (int i = 0; i < workers.size(); ++i) {
            Worker *t = workers[i];
            if (t != NULL && t->isActive())
                return false;
        }
        return true;
}



