/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: Worker.cc,v 1.28 2007-12-26 07:57:34 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#include <x10/xws/Worker.h>
#include <x10/xws/Lock.h>
#include <x10/xws/Frame.h>
#include <x10/xws/Pool.h>
#include <x10/xws/Closure.h>
#include <x10/xws/Cache.h>
#include <x10/xws/ActiveWorkerCount.h>
#include <x10/xws/StealAbort.h>
#include <x10/xws/Sys.h>
#include <x10/xws/Job.h>
#include <x10/xws/Executable.h>
#include <x10/xassert.h>
#include <cstdlib>
#include <iostream>
#include <sys/sched.h>
#include <unistd.h>

using namespace x10lib_xws;
using namespace std;


bool Worker::reporting = false;

//Worker::Worker() {
//}

// index is the id of the pthread
Worker::Worker(int idx, Pool *p) {
  this->checkedIn = true; 
	this->exception = false;

	this->top = NULL;
	this->bottom = NULL;
	this->randNext = 0;
	this->index = idx;
	this->done = false;
	this->closure = NULL;
	this->pool = p;
	this->lock_var = new PosixLock();
	assert(this->lock_var != NULL);
	this->lockOwner = NULL;

	this->cache = new Cache(this);
	assert(this->cache != NULL);
	this->fg = NULL;
	this->stealCount = 0;
	this->stealAttempts = 0;
	this->reporting = false;
	this->idleScanCount = 0;
	this->sleepStatus = 0;

	this->_area = NULL;
}

Worker::~Worker() { delete lock_var; /*delete cache;*/ }

void Worker::lock(Worker *ws) {
	LOCK(lock_var);
	lockOwner=ws;
}
void Worker::unlock() {
	lockOwner=NULL; // TODO Check
	UNLOCK(lock_var);
}
void Worker::setRandSeed(int seed) {
	randNext = seed;
}
int Worker::rand() {
	randNext = randNext*1103515245l  + 12345;
	int result = randNext >> 16;
	if (result < 0) result = -result;
	return result;
}


/**
 * Called by thief on victim. In the body of this method
 * victim==this and thief==Thread.currentThread().
 * @param thief -- The thread making this invocation.
 * @return
 */
Executable *Worker::steal(Worker *thief, bool retry) {
  if(jobRequiresGlobalQuiescence) {
    return stealFrame(thief, retry);
  }
  
  Closure *res = NULL;
  Worker *victim = this;
  ++stealAttempts;
  /*if (reporting) {
    System.out.println(thief + " attempts to steal from " + victim.index);
    }*/
		
  lock(thief);

  if(pool->getCurrentJob())
    jobRequiresGlobalQuiescence = pool->getCurrentJob()->requiresGlobalQuiescence();
  else {
    /*No current job => so no closure to steal?? Shouldn't we just return*/
  }
  if(jobRequiresGlobalQuiescence) {
    stealAttempts--;
    unlock();
    return stealFrame(thief, retry);
  }
  

		
  Closure *cl=NULL;
  cl = peekTop(thief, victim);
  if (cl == NULL) {
    //lock->lock_signal_posix(); // should it be unlock call directly -- TODO
    unlock();
    return NULL;
  }
  // vj: I believe the victim's ready deque should have only one
  // closure in it.
  Closure *cl1 = peekBottom(thief);
  assert(cl1==cl);
				
  cl->lock(thief);
				
  int status = cl->getstatus();

  //cerr<<thief->index<<"::Worker::steal. Status="<<status<<endl;
		
  assert (status == ABORTING || status == READY || status == RUNNING || status == RETURNING);
		
  switch (status) {
		
  case READY:
			
    res = extractTop(thief);
    assert (res == cl);
    thief->checkOut(res);
    /*if (reporting) {
      System.out.println(thief + " steals ready " + cl + " from "
      + victim);
      }*/
    cl->unlock();
    unlock();
    return res;
    break;
		
  case RUNNING: 
			
    if (cl->dekker(thief)) {
//       cerr<<index<<"::Got a closure to steal"<<endl;
      Closure *child = NULL;
      Closure *res = NULL;

      //      cerr<<index<<"::Stealing frame "<<cl->frame<<endl;

      //cl->copyFrame(thief);			
      //I have work now, so checkout of the barrier.
      child = cl->promoteChild(thief, victim);
      assert(child != NULL);
      res = extractTop(thief);
      assert(res != NULL);
      //cerr<<thief->index<<"::Stealing: victim top="<<res<<" bottom="<<bottom<<endl;
      /*if (reporting)
	System.out.println(thief + " Stealing: victim top=" + res + "bottom=" + bottom);*/
      thief->checkOutSteal(res, victim); //?????????????sriram TODO: checkoutsteal?
      assert(cl==res);

      unlock();
			
      cl->finishPromote(thief, child);

      cl->unlock();
      return res;
    } else { 
//       cerr<<thief->index<<"::Stealing: dekker failed"<<endl;
      cl->unlock(); 
      unlock(); 
      return NULL;
    }
    /*if (reporting) {
      System.out.println(thief + " steals running " + cl + " from "
      + victim);
      }*/
    break;
  case SUSPENDED:
    assert(0); 
    abort();
    break;
  case RETURNING:
  case ABORTING:
    cl->unlock(); 
    unlock();
    return NULL;
    break; 
  default:
    assert(0); 
    abort();
			
  }
  // this path taken when status == RETURNING, 
  // status==ABORTING, or status==RUNNING and dekker failed.
  //unlock();
  //cl->unlock();
  //return res;
  //SHOULD NOT REACH HERE
  assert(0); 
  abort();
  return NULL;
}

#warning "With GQ, frames are always copied when stolen. So user should always de-allocate all allocated frames. This not true without GQ. "

Executable *Worker::stealFrame(Worker *thief, bool retry) {
  Worker *victim = this;
  ++stealAttempts;
  lock(thief);
  
  Frame *frame = NULL;

  bool b=cache->dekker(thief);
  if (b) {
    frame = cache->topFrame();
    assert(frame != NULL);

    assert(thief->cache->empty());
    frame = frame->copy(thief);
    cache->incHead();
    //I have work now, so checkout of the barrier.
    thief->checkOutSteal(frame, victim);
  }

  unlock();
  return frame;
}

/* 
 * Extract the topmost closure in the ready deque of this
 * worker. May return NULL.
 * @aparam ws -- the current thread, i.e. Thread.currentThread()=ws
 */
Closure *Worker::extractTop(Worker *ws) {
	assert (lockOwner==ws);
	Closure *cl = top;
	if (cl == NULL) {
		assert (bottom == NULL);
		return cl;
	}
	top = cl->nextReady;
	if (cl == bottom) {
		assert(cl->nextReady == NULL);
		bottom = NULL;
	} else {
		assert(cl->nextReady != NULL);
		cl->nextReady->prevReady = NULL;
	}
	cl->ownerReadyQueue = NULL;
	return cl;
}
/**
 * Return the top of the closure deque, without removing it.
 * @return top of the closure deque
 * @param ws -- the current thread, i.e. Thread.currentThread()==ws
 */
Closure *Worker::peekTop(Worker *agent, Worker *subject) {
	assert(lockOwner==agent);
	Closure *cl = top;
	if (cl == NULL) {
		assert (bottom==NULL);
		return NULL;
	}
	assert(cl->ownerReadyQueue == subject);
	return cl;
}
/**
 * Return the closure at the bottom fo the deque.
 * Required: ws = Thread.currentThread()
 * @return
 */
Closure *Worker::extractBottom(Worker *ws) {
	assert(lockOwner==ws);
	Closure *cl=bottom;
	if (cl == NULL) {
		assert(top==NULL);
		return NULL;
	}
	assert(cl->ownerReadyQueue == ws);
	bottom = cl->prevReady;
	if (cl == top) {
		assert(cl->prevReady==NULL);
		top=NULL;
	} else {
		assert(cl->prevReady !=NULL);
		cl->prevReady->nextReady = NULL;
	}
	cl->ownerReadyQueue=NULL;
	return cl;
}
/**
 * Peek at the closure at the bottom of the ready deque.
 * @param ws -- The current thread, i.e. ws == Thread.currentThread().
 * @return
 */
Closure *Worker::peekBottom(Worker *ws) {
  	assert(lockOwner==ws);
	Closure *cl = bottom;
	if (cl==NULL) {
		assert (top==NULL);
		return cl;
	}
	assert(cl->ownerReadyQueue != NULL);
	assert(cl->ownerReadyQueue == this);
	//cl->ownerReadyQueue = this;
	return cl;
}
/**
 * Add the given closure to the bottom of the
 * worker's ready deque.
 * @parame ws -- the current thread, i.e. ws==Thread.currentThread().
 *               Note: current thread may not always be a Worker.
 * @param cl -- the closure to be added.
 */
void Worker::addBottom(Worker *ws, Closure *cl) {
		
	assert(lockOwner==ws);
	assert(cl != NULL);
	assert(cl->ownerReadyQueue == NULL);
		
	/*if (reporting)
		System.out.println(ws + " adds " + cl + " to " + this + " bottom.");*/
	cl->prevReady = bottom;
	cl->nextReady = NULL;
	cl->ownerReadyQueue = this;
	bottom = cl;
	if (top == NULL) {
		top = cl;
	} else {
		cl->prevReady->nextReady = cl;
	}
}



/**
 * A slow sync. Must be invoked only by the this Worker, i.e.
 * Thread.currentThread() == this.
 * @return true if the closure has to be suspended, false o.w.
 */
bool Worker::sync(Closure *c) {
	bool res = false;
	//	if(index==0) cerr<<index<<"::slow sync(). H="<<cache->gethead()<<"T="<<cache->gettail()<<"E="<<cache->getexception()<<endl;
	lock(this);

	assert(cache->gethead() == 0);
	assert(cache->gettail() == 1);
	assert(cache->getexception() == 0);

	assert(top == bottom);

	Closure *t = peekBottom(this);
	t->lock(this);
	
	assert(t == c);
	assert(t->getstatus()==RUNNING);
				
	// In slow sync. Therefore must be the case
	// that there is no frame on the stack.
	// i.e. this worker has finished executing any children asyncs. 
	// Other workers may still be working on children.
	assert(t->cache->atTopOfStack());
	// Execute all completed inlets.
	// Note these are being executed in a single-threaded
	// context since the lock is held.
	t->pollInlets(this);
	// pollInlets may change bottom of queue, due to abort processing.
	assert(t == bottom);
			
	if (t->hasChildren()) {
		// Suspend. Now any child that is joining
		// will get to check inlets and if they 
		// are all done, then execute this task
		// in place.
		/*if (reporting) {
			System.out.println(this + " suspends " + t + "(joincount=" + t.joinCount+")");
		}*/
				
		t->suspend(this);
		//  Vj: Added this popFrame. Note sure that Cilk does this. Without this
		// caches are left behind with unpopped frames. This interferes with
		// subsequent execution of other closures.
		popFrame();
		res=true;
	}
	t->unlock();
	unlock();
	return res;
		
}
	

/**
 * Tries to get a non-local task, stealing from other workers and/or the pool
 * submission queue.
 * @param mainLoop -- when true, try mainpool for work if stealing doesnt work.
 * @return -- a task, or NULL if none is available
 */
Executable *Worker::getTask(bool mainLoop) {
  /*if ( reporting) {
    System.out.println(this + " at  " + pool.time() + " looking for work idleScanCount= " + idleScanCount + 
    " checkedIn=" + checkedIn + " job=" + currentJob() + ".");
    System.out.println(this + " " + pool.barrier.numCheckedIn);
    }*/
  Executable *cl = NULL;
  checkIn();
  if (++idleScanCount < 0)
    idleScanCount=1;

  assert(cache->empty());
		
  /* From DL:
   * Scan through workers array twice starting at random
   * index. The first pass skips over those for which
   * stealTask reports contention. The second retries steals
   * even under contention.
   *
   * While traversing, the first failed attempted steal for
   * which the attempted victim is sleeping is recorded. If
   * a task is found in some other queue, and that queue
   * appears to have more tasks, that sleeper is woken up.
   * This propagates wakeups across workers when new work
   * becomes available to a set of otherwise idle threads.
   */
  //vector<Worker *> workers = pool->workers;
  int n = pool->num_workers;
  int idx = rand() % n;
  int origin = idx;
  Worker *sleeper = NULL;
  Worker *thief = this;
  bool retry = false; // first pass skips on contention.
  for (;;) {
			
    Worker *victim = (pool->workers).at(idx);
    if (victim != NULL && victim != thief) {
      //System.out.println(thief +  " at " + pool.time() + ": tries to steal from " + victim + "...");
      cl = (jobRequiresGlobalQuiescence) 
	? victim->stealFrame(thief, retry)
	: victim->steal(thief, retry);
      if (cl == NULL) {
	if ((sleeper == NULL) && // first sleeping worker
	    victim->sleepStatus== SLEEPING)
	  sleeper=victim;
      } else {
	//cerr<<index<<":: stole from "<<victim->index<<endl;
	idleScanCount = -1;
	++stealCount;
	if (sleeper != NULL)
	  sleeper->wakeup();
					
	return cl;
      }
    }
    if (++idx >= n) idx = 0;
    if (idx==origin) {
      if (! retry) 
	retry = true; 
      else 
	break;
				
    }
  }
  //cerr<<index<<":: here. mainLoop="<<mainLoop<<" sleeper="<<sleeper<<endl;
  Executable *x = mainLoop? this->getTaskFromPool(sleeper) : NULL;
  //cerr<<index<<":: here 1. mainLoop="<<mainLoop<<" cl="<<x<<endl;

  return x;
}

void Worker::setJob(Job *currentJob) {
  if(currentJob == NULL)
    currentJob = pool->getCurrentJob();
  if (currentJob==NULL) return;
  assert(currentJob != NULL);

  //cerr<<index<<":: setting info for new job"<<endl;
  this->job = currentJob;
  jobRequiresGlobalQuiescence = currentJob->requiresGlobalQuiescence();
}



    /**
     * Try to get a task from pool. On failure, possibly sleep.
     * On success, try to wake up some other sleeping worker.
     * @param sleeper a worker noticed to be sleeping while scanning
     */
Closure *Worker::getTaskFromPool(Worker *sleeper) {
  //cerr<<index<<":: gtfrompool. here 1"<<endl;
  Closure *job = pool->getJob();
  //cerr<<index<<":: gtfrompool. here 2"<<endl;
  if (job != NULL) {
    //cerr<<index<<":: found non-null job"<<endl;
    idleScanCount = -1;
    if (sleeper != NULL)
      sleeper->wakeup();
    checkOut(job);
    setJob(dynamic_cast<Job *>(job));
   return job;
  }

  //cerr<<"Invoking gettaskfrompool"<<endl;
  setJob(pool->getCurrentJob());

  if (((idleScanCount + 1) & SCANS_PER_SLEEP) == 0) {
    if (sleepStatus == AWAKE) {
      compare_exchange((int *)&sleepStatus, AWAKE, SLEEPING);
      sched_yield(); // TODO RAJ -- wanna give it to someone else
      usleep(idleScanCount * 0.001);
      
      //LockSupport.parkNanos(idleScanCount * SLEEP_NANOS_PER_SCAN);
      
      if (sleepStatus == WOKEN) // reset count on wakeup
	idleScanCount = 1;
    }
    sleepStatus = AWAKE; // TODO RAJ -- write it atomically
  }
  return NULL;
}

    
    
void Worker::checkIn() {
  if (!checkedIn) {
    /*if (reporting)
      System.out.println(this + " at " + pool.time() + " checks in. Gotta find me some work!");*/
    checkedIn = true;
    //cerr<<index<<":: checking in"<<endl;
    pool->barrier->checkIn();  // TODO RAJ
  }
}
void Worker::checkOut(Executable *cl) {
  assert(checkedIn);
  if (! checkedIn) {
    Worker *other = (pool->workers).at(1-index);
    /*if (reporting)
      System.out.println(this + " at " + pool.time() + " tries to check out. checkedIn == false!! other.checkedIn=" 
      + other.checkedIn);*/
    assert(false); // TODO RAJ
  }
  if (checkedIn) {
    /*if (reporting)
      System.out.println(this + " at " + pool.time() + " checks out. Work to do! " + cl);*/
    checkedIn = false;
    //cerr<<index<<":: checking out"<<endl;
    pool->barrier->checkOut();
  }
}
void Worker::checkOutSteal(Executable *cl, Worker *victim) {
    	assert(victim->lockOwner==this);
    	checkOut(cl);
}
void Worker::wakeup() {
        if (sleepStatus == SLEEPING) { 
	  compare_exchange((int *)&sleepStatus, SLEEPING, WOKEN);
        	/*if (reporting)
        	System.out.println(this + " at " + pool.time() + " is being unparked.");*/
          //LockSupport->unpark(this);  // TODO RAJ
        }
}

	
void Worker::run() {
  assert(index >= 0);
  setRandSeed(index*162347);
  Executable *cl = NULL; //frame or closure.
  int yields = 0;
  while (!done) {

    if(pool->currentJob)
      jobRequiresGlobalQuiescence = pool->getCurrentJob()->requiresGlobalQuiescence();

    if (cl == NULL) {
      // Addition for GlobalQuiescence. Keep popping
      // tasks off the deque and executing them until
      // the deque becomes empty.
      if (jobRequiresGlobalQuiescence) {
	Cache *cache = this->cache;
	for(;;) {
	  Frame *f = cache->popAndReturnFrame(this);
	  if (f == NULL) {
	    assert(cache->empty());
	    break;
	  }
	  assert(!hasThrownException());
	  f->compute(this);
	  //Nothing much to do on exception. It just
	  //unwound the stack as we wanted. 
	  catchAllException();
	}
      } else {
	//cerr<<index<<":: no GQ"<<endl;
	//Try geting work from the local queue.
	lock(this);
	cl = extractBottom(this);
	if (((reporting )) && cl !=NULL) {
	  cerr<<index<<":: extract local closure"<<endl;
	}
	unlock();
      }
    }

    if (cl == NULL) {
      //cerr<<"Getting task"<<endl;
      cl = getTask(true);
      //cerr<<"Got task "<<cl<<" reqGQ="<<jobRequiresGlobalQuiescence<<endl;
      if(cl != NULL) {
	assert(!checkedIn);
	//assert(pool->getCurrentJob()->requiresGlobalQuiescence());
	//assert(jobRequiresGlobalQuiescence);
      }
    }
			
    if (cl !=NULL) {
      // Found some work! Execute it.
      idleScanCount=-1;
      assert(cache == NULL || cache->empty());
      /*if ( reporting) {
	System.out.println(this + " executes " + cl +".");
	}*/
      //cache->reset();
      assert(cache->empty());
      cache->reset();
      Executable *cl1 = cl->execute(this);
      /*if ((reporting && bottom == cl)) {
	System.out.println(this + " completes " + cl + " returns " + cl1 +".");
	}*/
      cl=cl1;

      //cerr<<index<<":: H="<<cache->gethead()<<" T="<<cache->gettail()<<endl;
      //assert(cache->empty());
      //cache->reset();

      // vj: This avoids having to implement a wrap around.
      // Otherwise, head might increase on a steal, but would
      // never decrease.
      //assert(jobRequiresGlobalQuiescence);
      if(!jobRequiresGlobalQuiescence) {
 	assert(cache->empty());
	cache->reset();
      }
    } else if(pool->isShutdown()) {
      //cerr<<index<<":: shutting down"<<endl;
    	
      /* sriramk: If pool says shutdown, shutdown. Global
       * termination is someone else's problem*/ 
      return;
    } else {
      yields++;
      sched_yield(); // TODO RAJ
    }
  }
}
	/**
	 * Push a frame onto the stack of the current
	 * closure (the closure at the bottom of the deque).
	 * Called by client code in the body of a procedure
	 * which has a spawn async before the first spawn.
	 * @param frame -- the frame to be pushed.
	 */
void Worker::pushFrame(Frame *frame) {
		cache->pushFrame(frame);
}
	/**
	 * Pop the last frame from the stack.
	 *
	 */
void Worker::popFrame() {
		cache->popFrame();
}
	/*
	public String toString() {
		return "Worker("+index+")";
	}*/
Closure *Worker::getbottom() {
		return bottom;
}
	/**
	 * Called by client code on return from a spawn async
	 * invocation. Performs the victim end of Dekker.
	 * @return  -- non NULL only if the current frame has been stolen
	 *             by a thief since its execution started.
	 *             The value returned is the closure created
	 *             by the invocation of a makeClosure on the child
	 *             frame, provided that it is not NULL. (In this case
	 *             the closure is also at the bottom of the
	 *             worker's ready queue.) If it is NULL, then
	 *             currentJob() is returned. (For in this case
	 *             the worker is running part of a globally
	 *             quiescent computation.) 
	 *   
	 *            
	 */
Closure *Worker::interruptCheck() {
		if (! cache->interrupted()) 
			// fast path
			return NULL;
		
		Closure *result = exceptionHandler();
		/*if (true && reporting)
			System.out.println(this + " at " + pool.time() + " popFrameCheck: discovers a theft and returns " + result
					+ " cache=" + cache.dump());*/
/*#if 0*/ // RAJ -- I have uncommented this section of the code -- please verify
		if (result !=NULL)
			popFrame();
/*#else
#warning "Sriram: Commented some code. Check it!!!"
#endif*/
		return result;
}
	/**
	 * Method to be called by user code after every method invocation that may have
	 * pushed a frame on the frame stack. Detects whether the worker has been mugged. 
	 * If so, throws a StealAbort. This will cause the Java stack to unwind all the way
	 * to the scheduler, which catches and discards this exception.
	 * <p>
	 * If the worker has not been mugged, this method does nothing.
	 * @return true if abort. false otherwise
	 */
bool Worker::abortOnSteal()  /*throw StealAbort */
{
  if(hasThrownException()) return true;
  Closure *c = interruptCheck();
  if (c != NULL) {
    throwException();
    return true;
  }
  return false;
}
	
	/**
	 * Method to be called by user code after every method invocation that may have
	 * pushed a frame on the frame stack, provided that the method returns an int value.
	 * This value should be passed into this call. If the worker has been mugged, this
	 * value will be squirrelled away in the promoted closure for the victim, 
	 * and from there it will make its way to the stolen frame. 
	 * Also, a StealAbort will be thrown. This will cause the Java stack to unwind all the way
	 * to the scheduler, which catches and discards this exception.
	 * 
	 * @throws StealAbort
	 */
bool Worker::abortOnSteal(int x)/* throw StealAbort*/{
  if(hasThrownException())    return true;
  Closure *c = interruptCheck();
  if (c != NULL) {
    //cerr<<index<<":: Setting result int "<<x<<"Throwing an exception"<<endl;
    c->setResultInt(x);
    throwException();
    return true;
  }
  return false;
}
	
	/**
	 * @see abortOnSteal(int x)
	 * @param x
	 * @throws StealAbort
	 */
bool Worker::abortOnSteal(double x) /*throw StealAbort*/{
  if(hasThrownException()) return true;
  Closure *c = interruptCheck();
  if (c != NULL) {
    c->setResultDouble(x);
    throwException();
    return true;
  }
  return false;
}

	/**
	 * @see abortOnSteal(int x)
	 * @param x
	 * @throws StealAbort
	 */
bool Worker::abortOnSteal(float x) /*throw StealAbort*/{
  if(hasThrownException()) return true;
  Closure *c = interruptCheck();
  if (c != NULL) {
    c->setResultFloat(x);
    throwException();
    return true;
  }
  return false;
}
	/**
	 * @see abortOnSteal(int x)
	 * @param x
	 * @throws StealAbort
	 */
bool Worker::abortOnSteal(long x) /*throw StealAbort*/{
  if(hasThrownException()) return true;
  Closure *c = interruptCheck();
  if (c != NULL) {
    c->setResultLong(x);
    throwException();
    return true;
  }
  return false;
}
	/**
	 * @see abortOnSteal(int x)
	 * @param x
	 * @throws StealAbort
	 */
/*
void Worker::abortOnSteal(Object x) throw StealAbort {
Closure *c = interruptCheck();
if (c != NULL || hasThrownException()) {
c->setResultObject(x);
throwException();
return true;
    }
    }
*/

Closure *Worker::exceptionHandler() {
		Closure *res = NULL;
		lock(this);
		Closure *b = bottom;
		assert (b !=NULL);
		b->lock(this);
	/*		b->pollInlets(this);
			Closure *c = bottom;
			assert (c!=NULL);
			if (b != c) {
				b->unlock();
				b=c;
				b->lock(this);
			}*/
		bool result = b->handleException(this);
		res = result?b:NULL;
		b->unlock();
		unlock();
		return res;
}
	

bool Worker::isActive() const { 
        return  (!cache->empty()) || idleScanCount <= 0;
}
Closure *Worker::currentJob() const {
    	return (Closure *)pool->currentJob;
}
void Worker::pushIntUpdatingInPlace(int x) {
		cache->pushIntUpdatingInPlace(pool, index, x);
}
void Worker::setFrameGenerator(FrameGenerator *x) {
		fg=x;
}

