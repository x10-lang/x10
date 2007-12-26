/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: Closure.cc,v 1.27 2007-12-26 07:57:34 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#include <x10/xws/Closure.h>
#include <x10/xws/Worker.h>
#include <x10/xws/Lock.h>
#include <x10/xws/Cache.h>
#include <x10/xws/Job.h>
#include <x10/xws/Frame.h>
#include <x10/xws/Sys.h>
#include <x10/xws/StealAbort.h>
#include <x10/xws/Executable.h>
#include <x10/xassert.h>
#include <iostream>

using namespace std;
using namespace x10lib_xws;

#if defined(MEM_DEBUG) && (MEM_DEBUG!=0)
volatile int Closure::nCons = 0;
volatile int Closure::nDestruct = 0;
#endif

int Closure::getstatus() const { return status;}
Frame *Closure::parentFrame() const { return parent->frame; }
Closure *Closure::getparent() const { return parent; }

//Closure::Closure() { }

Closure::Closure(Frame *frame) {
	/*In place to check the defalut parameter memory allocation does not 
	 * 	return NULL. We need to fix this if there is ever a need to create 
	 * a Closure with a NULL frame.
	 */
	assert(frame != NULL);

	done = false;
	this->frame = frame;
	lock_var = new PosixLock();
	assert(lock_var != NULL);

	//public members to be init
	cache = NULL;
	parent = NULL;
	joinCount = 0;
	nextReady = prevReady = NULL;
	ownerReadyQueue = NULL;
	status = -1; //It is to be set to a valid value by someone; Not me!

	//protected members to be init
	lockOwner = NULL;
	outlet = NULL;
	done = false;

	initialize();

	incCons();
}

Closure::~Closure() { 
  incDestruct();
  assert(lockOwner==NULL);
  assert(joinCount==0);
	delete lock_var; 
	completeInlets.clear(); 
	incompleteInlets.clear();
	//NOTE: Do not delete frame here -- leave choice to derived classes
	//delete frame; 
	delete outlet; 
	/*Every outlet is attached to a Closure, and it
			 destroyed by that Closure. */
}

bool Closure::hasChildren() const {
	return joinCount > 0;
}
void Closure::lock(Worker *agent) { 
	LOCK(lock_var);
	lockOwner = agent;
}
void Closure::unlock() { 
	lockOwner=NULL;
	UNLOCK(lock_var);
}
void Closure::addCompletedInlet(Closure *child) {
  /*if (completeInlets == NULL)
    completeInlets = new list<Closure>();*/
  assert(lockOwner != NULL);
  completeInlets.push_back(child);
}
	
void Closure::removeChild(Closure *child) {
  //if (incompleteInlets != NULL) 
  incompleteInlets.remove(child);
  // for (Inlet i : incompleteInlets) {
  //	if (i.target == child) return i;
  //}
  //return NULL;
}
	
	/**
	 * This code is executed by the thief on the parent while holding 
	 * the lock on the parent and on the victim.
	 * @param thief  -- The worker performing the steal.
	 * @param victim -- The worker from who work has been stolen.
	 * @return the child closure
	 */
Closure *Closure::promoteChild(Worker *thief, Worker *victim) {
		assert(lockOwner == thief);
		assert(status == RUNNING);
		assert(ownerReadyQueue == victim);
		assert(victim->lockOwner == thief);
		assert(nextReady==NULL);
		assert(cache->exceptionOutstanding());

// 		Frame *pFrame = cache->topFrame();
// 		assert(pFrame != NULL);
		Frame *pFrame = this->frame;
		
		Frame *childFrame = /*dynamic_cast<Frame *>*/(cache->childFrame());
		assert(childFrame!=NULL);
		Closure *child = /*dynamic_cast<Closure *>*/(childFrame->makeClosure());
		
		cache->childFrame() = child->frame;
		
		pFrame->setOutletOn(child);
		
		// Leave the parent link in there.
		// It will not be used by globally quiescent computations.
		child->parent = this;
		child->joinCount = 0;
		child->cache = cache;
		child->status = RUNNING;
		child->ownerReadyQueue = NULL;
		cache->incHead();
		
		victim->addBottom(thief, child);
		return child;
}
	/**
	 * This code is executed by the thief on the parent while holding 
	 * the lock on the parent. The lock on the victim has been given up.
	 * Therefore other thiefs may be hitting upon the victim simultaneously.
	 * 
	 * @param thief  -- The worker performing the steal.
	 * @param child -- The child closure being promoted.
	 */
void Closure::finishPromote(Worker *thief, Closure *child) {
		
		assert(lockOwner == thief);
		assert(child == NULL || child->lockOwner != thief);
		
		/* Add the child to the parent. */
		if (! child->requiresGlobalQuiescence())
			++joinCount;
		// No need to add child to parent's incomplete inlets
		// unless aborts are being propagated.
		//if (incompleteInlets == NULL)
		//	incompleteInlets = new ArrayList<Closure>();
		//incompleteInlets.push_back(child);
		
		/* Set the parent's cache to NULL and its status to READY */
		status=READY;
		cache=NULL;
}

	/**
	 * Do the thief part of Dekker's protocol.  Return true upon success,
	 * false otherwise.  The protocol fails when the victim already popped
	 * T so that E=T.
	 * Must be the case that Thread.currentThread()==thief.
	 */
bool Closure::dekker(Worker *thief) {
		assert(lockOwner==thief);
		assert(status==RUNNING);
		return cache->dekker(thief);
}
    
/*
void Closure::decrementExceptionPointer(Worker *ws) {
    	assert(lockOwner == ws);
    	assert(status == RUNNING);
    	cache->decrementExceptionPointer();
}
    
void Closure::incrementExceptionPointer(Worker *ws) {
    	assert(lockOwner == ws);
    	assert(status == RUNNING);
    	
    	cache->incrementExceptionPointer();
}
void Closure::resetExceptionPointer(Worker *ws) {
    	 assert(lockOwner==ws);
    	 
    	 cache->resetExceptionPointer();
}
    
*/
bool Closure::handleException(Worker *ws) {
    	cache->resetExceptionPointer(ws);
    	
    	assert (status == RUNNING || status == RETURNING);
    	if (cache->empty()) {
    		assert(joinCount==0);
    		status = RETURNING;
        	return true;
    	}
    	return false;
    	
}
  
void Closure::signalImmediateException(Worker *ws) {
    	assert(lockOwner == ws);
    	assert(status == RUNNING);
    	cache->signalImmediateException();
}



 /**
     * Return protocol. The closure has completed its computation. Its return value
     * is now propagated to the parent. The closure to be executed next, possibly NULL,
     * is returned.  
     * This closure must not be locked (by this worker??) and must not be in any deque.
     * Required that ws==Thread.currentThread().
     * @return the parent, if this is the last child joining.
     */
Closure *Closure::closureReturn(Worker *w) {
    	
    	// Short circuit for globally quiescent computations.
    	if (requiresGlobalQuiescence() && parent != NULL) {
    		w->currentJob()->accumulateResultInt(resultInt());
    		return NULL;
    	}
    	assert (joinCount==0);
    	assert (ownerReadyQueue==NULL);
    	assert (lockOwner != w);
    	
    	if (! requiresGlobalQuiescence())
    		completed();
    	Closure *parent = this->parent;
    	if (parent == NULL) {
    		// Must be a top level closure.
    		return NULL;
    	}
    	return parent->acceptChild(w, this);
}
     
Closure *Closure::acceptChild(Worker *ws, Closure *child) {
		Closure *res;
    	lock(ws);
    	assert(status != RETURNING);
    	assert(frame != NULL);
    		//removeChild(child);
    	assert (joinCount > 0); // ADDED by RAJ;
    	MEM_BARRIER();
    	--joinCount;
    	MEM_BARRIER();
    	child->lock(ws);
    	assert (lockOwner == ws);
    	addCompletedInlet(child);
    	res = provablyGoodStealMaybe(ws, child);
    	child->unlock();
	if(res) {
	  /*The child can be deleted once we have unwound the stack
	    and we have no more reference to this object.*/
	  assert(child->parent == res);
	  assert(res->joinCount==0);
	  assert(res->completeInlets.size() == 0);
	}
    	unlock();
    	return res;
}
    
    /**
     * Suspend this closure. Called during execution of slow sync
     * when there is at least one outstanding child.
     * ws must be the worker executing suspend.
     * Assume: ws=Thread.currentThread();
     */
void Closure::suspend(Worker *ws) {
    	assert(lockOwner == ws);
    	assert(status == RUNNING);
    	
    	status = SUSPENDED;

    	// throw away the bottommost closure on the worker.
    	// the only references left to this closure are from
    	// its children.
    	Closure *cl = ws->extractBottom(ws);
    	assert(cl==this);
    	
//    	Setting ownedReadyQueue to NULL even though Cilk does not do it.
	assert(cl->ownerReadyQueue == NULL);
//    	cl->ownerReadyQueue=NULL;
}
    
    /**
     * Return the parent provided that its joinCount is zero 
     * and it is suspended. The parent should now be considered
     * stolen by the worker that just finished returning the
     * value of a child
     * @return parent or NULL
     */
Closure *Closure::provablyGoodStealMaybe(Worker *ws, Closure *child) {
    	assert(child->lockOwner==ws);
    	//assert parent != NULL;
    	Closure *result = NULL;
    	
    	if (joinCount==0 && status == SUSPENDED) {
    		result = this;
    		pollInlets(ws, child);
		assert(ownerReadyQueue == NULL);
		  //ownerReadyQueue = NULL;
    		status=READY;
    		cache=NULL;
    		/*if (Worker.reporting) {
    			System.out.println(ws + " awakens " + this);
    		}*/
    	} 
    	return result;
    }
	
   
	/**
	 * Run all completed inlets.
	 * TODO: Figure out why pollInlets are being run incrementally
	 * and not just once, after joinCount==0. Perhaps because
	 * this method is supposed to perform abort processing.
	 * This methods always invoked because some child completed
	 * and is try to see if the parent can be stolen. This is done
	 * with the child being locked. All inlet closures, other than
	 * this child, can be delete here. The child causing this
	 * invocation is probably locked in the enclosing call sequence
	 * and should be deleted elsewhere. 
	 * We make sure random pollInlets still works, by giving a
	 * default NULL parameter to causingChild (see declaration). 
	 */
void Closure::pollInlets(Worker *ws, Closure *causingChild) {
  list<Closure *>::iterator i;
  int flag;
  assert(lockOwner==ws);
  
  
  if (status==RUNNING && ! cache->atTopOfStack()) {
    assert(ws->lockOwner == ws);
  }
  if (!completeInlets.empty())
    flag=0;
    /* TODO LIST TRAVERSAL -- RAJ*/
    for (i=completeInlets.begin(); i != completeInlets.end(); ++i) {
      assert((*i)->parent == this);
      assert((*i)->status == RETURNING);
      assert((*i)->ownerReadyQueue == NULL);
      (*i)->executeAsInlet();
      if((*i) == causingChild)
	flag+=1;
      if((*i) != causingChild)
	delete (*i);
    }
    assert(causingChild==NULL || (!completeInlets.empty()  && flag==1));
  completeInlets.clear();
}

	
	 /**
     * Must be called by every slow procedure after it sets the 
     * return value but before it returns.
     * This method ensures that the closure's value is returned
     * to the parent. If this is the last child joining the parent,
     * and the parent is suspended, return the parent (this is a
     * provably good steal).
     * @return
     */
   
Closure *Closure::returnValue(Worker *ws) {
    	assert(status==RETURNING);
    	
    	return closureReturn(ws);
}
	
	/**
	 * Execute this closure. Performed after the closure has been
	 * stolen from a victim. Will eventually invoke compute(frame),
	 * after setting things up.
	 * @param w -- the current thread, must be equal to Thread.currentThread()
	 */
Closure *Closure::execute(Worker *w) {
		Closure *res = NULL;
		assert(lockOwner != w);

		lock(w);
		
		Frame *f = frame;
		assert(f != NULL);
		switch (status) {
		case READY:
			status = RUNNING;
		    // load the cache from the worker's state.
		    cache = w->cache;
		    assert(cache!=NULL);
		    //cache->reset();
		    cache->pushFrame(frame);
		    cache->resetExceptionPointer(w);
			assert(f != NULL);
			pollInlets(w);
			
			unlock();
			
			w->lock(w);
			w->addBottom(w, this);
			w->unlock();
			
			compute(w, f); 
			//Nothing much to do on exception. It just
			//unwound the stack as we wanted. 
 			w->catchAllException();

			res = NULL;
			break;
		
		case RETURNING:
		  unlock();
		  res = returnValue(w);
		  
		  if(res) {
		    assert(parent == res);
		    delete this;
		    break;
		  }
		  assert(isDone());
		  
		  if(parent==NULL && !requiresGlobalQuiescence()) {
		    Job *job = dynamic_cast<Job *>(this);
		    assert(job != NULL);
		    job->jobCompleted();
		    /*deleted by user -- creator of jobs*/
		    break;
		  }
		  break;
		default:
		  assert(0);
		  abort(); // TODO RAJ
		  //throw new Error(w + "executes " + status + " " + this + ": error!");
		}
		return res;
}

	/**
	 * Return your value to the parent closure. Typically the
	 * closure will be created with information about where
	 * to deposit its result.
	 */
void Closure::executeAsInlet() {
  assert(status == RETURNING);
  assert(ownerReadyQueue == NULL);
  if (outlet != NULL) {
    outlet->run();
  }
}
//	=============== The methods intended to be called by client code =======
//	=============== that subclasses Closure.========
	
	/**
	 * Slow execution entry point for the scheduler. Invoked by the thief
	 * running in the scheduler after it has stolen the closure from a victim.
	 * @param w -- The thread invoking the compute, i.e. w == Thread.currentThread()
	 * @param frame -- frame within which to execute
	 */
void Closure::compute(Worker *w, Frame *frame) {assert(0); abort();}
    
	/**
	 * Subclasses should override this as appropriate. 
	 * But they should alwas first call super.initialize();
	 *
	 */
void Closure::initialize() {
		// need to handle abort processing.
}

    /* Public method intended to be invoked from within 
     * slow methods of client code.
     * @return true -- iff the closure must suspend because not 
     *                 all children have returned
     */
bool Closure::sync(Worker *ws) {
		return ws->sync(this);
}
	
	
	/**
	 * Invoked by client code before a return from slow code. It will
	 * mark the current closure as RETURNING so it wont be stolen. It will
	 * pop the current frame. 
	 * Before invoking this call, client code is responsible for setting the appropriate state
	 * on the closure so that the value to be returned is known.
	 * 
	 */
void Closure::setupReturn(Worker *w) {
		// Do not trust client code to pass this parameter in.
		//Worker *w = (Worker) Thread.currentThread(); // TODO how to handle this RAJ
		done = true;
		w->lock(w);
		
		if (requiresGlobalQuiescence()) {
			w->extractBottom(w);
			// speed the result on its way.
			if (parent != NULL)
			w->currentJob()->accumulateResultInt(resultInt());
			return;
		}
		Closure *t = w->peekBottom(w);
		assert(t==this);
		lock(w);
			
		assert(status==RUNNING);
		status=RETURNING;
		//frame=NULL;
		w->popFrame();
			
		unlock();
			
		w->unlock();
}

void Closure::setupGQReturnNoArg(Worker *w) {
		// Do not trust client code to pass this parameter in.
		//Worker *w = (Worker) Thread.currentThread(); //TODO RAJ
		w->lock(w); 
		w->extractBottom(w);
		w->popFrame();
		w->unlock();
		
}

void Closure::setupGQReturn(Worker *w) {
		// Do not trust client code to pass this parameter in.
		//Worker *w = (Worker) Thread.currentThread(); // TODO RAJ
		// do not set done to true. This will be 
		// done when global quiescence is recognized.
		w->lock(w);
		Closure *t = w->peekBottom(w);
		assert(t==this);
		lock(w);
		assert(status==RUNNING);
		status=RETURNING;
		//frame=NULL;
		w->popFrame();
		unlock();
		w->unlock();
		
}

void Closure::setupGQReturnNoArgNoPop(Worker *w) {
  w->lock(w);
  w->extractBottom(w);
  assert(status == RUNNING);
  status = RETURNING;
  w->unlock();
}

	
void Closure::setOutlet(Outlet *o) { outlet=o;}
/** Replace the frame by a copy. 
	 * 
	 * <p> Called during
	 * a steal to ensure that frames are not shared between the caches
	 * of two different workers. Must be called with the current thread,
	 * w, holding the lock on the victim. This ensures that the 
	 * victim is not running freely, modifying the frame being
	 * copied. Why? Because the Theft Protocol ensures that the
	 * victim cannot return to processing the frame until it has checked
	 * whether this frame has been stolen. If the frame has been stolen,
	 * the victim must grab the lock on itself. So therefore we must
	 * ensure that this method is called by the thief before it releases 
	 * the lock on the victim. 
	 * 
	 * Note that the victim is by definition the owner of this closure.
	 * ownerReadyQueue must not be null.
	 *
	 */
void Closure::copyFrame(Worker *w) {
		assert(ownerReadyQueue->lockOwner==w);
		frame = frame->copy();
}	

bool Closure::isDone() volatile { return done;}
	
//RuntimeException Closure::getException() { return NULL;} // TODO RAJ
	
	/**
	 * Invoked on completion of the computation associated with this closure. 
	 * May be overridden by client code. Note: This method may be invoked more than once
	 * by the scheduler.
	 *
	 */
void Closure::completed() volatile{
		done = true;
}
	
	// At most one of the following pairs of methods should be overridden by subclassing
	// closures. No pair may be overridden if the closure does not have an associated
	// return value. These methods are not abstract so that Closure can be used directly
	// when there is no reason to subclass it.
void Closure::setResultInt(int x) { assert(0); abort(); }
void Closure::accumulateResultInt(int x) { assert(0); abort();}
int Closure::resultInt() { assert(0); abort(); return 0;}
	
void Closure::setResultFloat(float x) {assert(0); abort();}
void Closure::accumulateResultFloat(float x) { assert(0); abort();}
float Closure::resultFloat() { assert(0); abort(); return 0.0;}
	
void Closure::setResultLong(long x) {assert(0); abort();}
void Closure::accumulateResultLong(long x) { assert(0); abort();}
long Closure::resultLong() { assert(0); abort(); return 0l;}
	
void Closure::setResultDouble(double x) {assert(0); abort();}
void Closure::accumulateResultDouble(double x) { assert(0); abort();}
double Closure::resultDouble() { assert(0); abort(); return 0.0;}
	
void Closure::setResultObject(void *x) {assert(0); abort();}
void *Closure::resultObject() { assert(0); abort(); return NULL; }
bool Closure::requiresGlobalQuiescence() /*volatile*/ {  
  if(dynamic_cast<GloballyQuiescentJob*>(this)) return true;
	if (dynamic_cast<Closure *>(this)) return false;
	else if (dynamic_cast<Job *>(this)) return false;
	// if its a GloballyQuiescentjob -- TODO later on -- make it more appropriate
	else return true;
}

