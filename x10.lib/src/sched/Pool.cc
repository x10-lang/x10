/*
============================================================================
 Name        : Pool.cpp
 Author      : Rajkishore Barik
 Version     :
 Copyright   : IBM Corporation 2007
 Description : Exe source file
============================================================================
*/
using namespace x10lib_cws;
using namespace std;
#include <time.h>

Pool::~Pool() {
	pthread_cond_destroy(&work);
	pthread_cond_destroy(&termination);
	delete lock;
	delete barrier;
	free id;
	for(i=0;i<workers.size();i++)
					delete workers[i];
	workers.clear();
}


void Pool::each_thread(int d)
{
	 Worker *ws = new Worker(d, this);
	 workers[d]=ws;
     //barrier->barrier();
     /*if (id == 0)
	  ws->run(invoke_main);
     else
      ws->run((Closure *) NULL);*/
     ws->run();
}

void Pool::barrierAction(Closure *c)
{
	if (c != NULL && c->requiresGlobalQuiescence()) 
			c->completed();
	c = NULL;
	MEM_BARRIER(); // let everyone see it -- TODO RAJ
}

Pool::Pool(int numThreads) {
	
	long i;
	int res;
	pthread_attr_t attr;
	
	if (numThreads <=0) { cout << "Illegal argument"; abort(); }
	
	lock = new PosixLock();

	pthread_cond_init (&work, NULL);
	pthread_cond_init (&termination, NULL);

	barrier = new ActiveWorkerCount(&barrierAction, currentJob);

	num_workers = numThreads;
	runningWorkers = 0;
	activeJobs = 0;
	activeOnJobAtomic = 0;
	jobs =  new JobQueue();
	joinCount = 0;
	
	workers.resize(INITIAL_WORKER_CAPACITY);
	
	  
	id = malloc(num_workers * sizeof(pthread_t));
	

	pthread_attr_init(&attr); 
	pthread_attr_setscope(&attr, PTHREAD_SCOPE_SYSTEM); 
	 
	lock -> lock_wait_posix();
	  
	for (i = 0; i < num_workers; i++)
	{
		  res = pthread_create(id + i, 
				       &attr,
				       (void * (*) (void *)) each_thread, 
				       (void *) i);
		  if (res) {cout << "could not create"; abort(); }
		  runningWorkers++;
		       
	}
	lock -> lock_signal_posix();
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
        const PosixLock *l = this->lock;
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

bool Pool::isShutdown() const {
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
        PosixLock *l = this->lock;
        l->lock_wait_posix();
        
        if (runState < SHUTDOWN) {
                   runState = SHUTDOWN;
									 MEM_BARRIER(); // for volatile update -- TODO RAJ
        }
        l->lock_signal_posix();
}

    /**
     * Attempts to stop all actively executing tasks, and halts the
     * processing of waiting tasks.
     */
void Pool::shutdownNow() {
        PosixLock *l = this->lock;
        l->lock_wait_posix();
        //tryTerminate 
        l->lock_signal_posix();
}

// RAJ:: timeout is used as it is without conversion
bool Pool::awaitTermination(const struct timespec *timeout) {
        
        PosixLock *l = this->lock;
        l->lock_wait_posix();
        
        for (;;) {
                if (runState == TERMINATED)
                    return true;
                if (timeout->tv_nsec <= 0)
                    return false;
                // nanos = termination.awaitNanos(timeout); // TODO wait for timeout nanos RAJ
								pthread_cond_timedwait(&termination,this->lock->posix_lock_var,timeout);

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
vector<Worker *> *Pool::getWorkers() const {
     return &workers;
}
 
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
        PosixLock *l = this->lock;
        bool ok;
        l->lock_wait_posix();
        
        if (ok = (runState == RUNNING)) {
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
long Pool::getStealCount() {
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
long Pool::getStealAttempts() {
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
        PosixLock *l = this->lock;
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
        	/*// TODO Confirm from Vijay
            Worker *replacement = new Worker(this, index);
            if (ueh != NULL)
                    replacement.setUncaughtExceptionHandler(ueh);
                workers[index] = replacement;
                replacement.start();
                */
         }
        
         l->lock_signal_posix();
}

void Pool::jobCompleted() {
        PosixLock *l = this->lock;
        l->lock_wait_posix();
        
        if (--activeJobs <= 0 && runState == SHUTDOWN && jobs->isEmpty())
                tryTerminate();
        
        l->lock_signal_posix();
        
}
void Pool::tryTerminate() const {
    	// do nothing.
}
Closure *Pool::getJob() const {
        
        lock->lock_wait_posix();
        //try {
        	Closure *task = jobs->poll();
        	if (task == NULL && activeJobs == 0) {
        		//work.await(); // TODO RAJ
        		pthread_cond_wait(&work,&(this->lock->posix_lock_var))
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
        lock->lock_signal_posix();
        return task;
}



JobQueue::JobQueue() {
	elements.resize(INITIAL_JOBQUEUE_CAPACITY);
}
JobQuque::~JobQueue() {
				elements.clear();
}

bool JobQueue::isEmpty() {
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
bool JobQueue::isQuiescent() {
        for (int i = 0; i < workers.size(); ++i) {
            Worker *t = workers[i];
            if (t != NULL && t->isActive())
                return false;
        }
        return true;
}

