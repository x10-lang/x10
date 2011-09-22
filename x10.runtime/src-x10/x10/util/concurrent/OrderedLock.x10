package x10.util.concurrent;

import x10.util.List;
import x10.util.ListIterator;
import x10.util.Map;
import x10.util.HashMap;

//The lock class used to transforming atomic sections to use
//locking.
public class OrderedLock implements Comparable[OrderedLock] {
	//the real lock to acquire, each OrderedLock object is associated
	//with a unique lock id (as ordering).
	private var lock:Lock = null;
	private var index:Int = -1;
	
	//create the lock and assign a unique id
	public def this() {
		lock = new Lock();
		index = Counter.getIndex();
	}
	
	public def lock():void {
		lock.lock();
	}

	public def tryLock():Boolean {
		return lock.tryLock();
	}

	public def unlock():void {
		lock.unlock();
	}

	public def getHoldCount():Int { // only supported on some platforms
		return lock.getHoldCount();
	}
	
	public def getIndex():Int {
		return index;
	}
	
	public def compareTo(that:OrderedLock):Int {
		if(this.index == that.getIndex()) {
			return 0;
		} else if (this.index > that.getIndex()) {
			return 1;
		} else {
			return -1;
		}
	}
	
	//a few shortcut methods to acquire/release 1 -- 3 locks
	//this is much faster for cases that few locks are need to
	//acquire/release frequently than using a list to store all locks (below)
	public static def acquireSingleLock(that:OrderedLock) : void {
		that.lock();
	}
	public static def releaseSingleLock(that:OrderedLock) : void {
		that.unlock();
	}
	public static def acquireTwoLocks(that1:OrderedLock, that2:OrderedLock) : void {
		that1.lock();
		that2.lock();
	}
	public static def releaseTwoLocks(that1:OrderedLock, that2:OrderedLock) : void {
		that1.unlock();
		that2.unlock();
	}
	public static def acquireThreeLocks(that1:OrderedLock, that2:OrderedLock, that3:OrderedLock) : void {
		that1.lock();
		that2.lock();
		that3.lock();
	}
	public static def releaseThreeLocks(that1:OrderedLock, that2:OrderedLock, that3:OrderedLock) : void {
		that1.unlock();
		that2.unlock();
		that3.unlock();
	}
	
	//Acquire a list of locks
	public static def acquireLocks(locks:List[OrderedLock]):void {
		//locks.sort(); //do not need sort, can not avoid deadlock anyway
		//iterate through the locks, and acquire it one by one
		var it:ListIterator[OrderedLock] = locks.iterator();
		while(it.hasNext()) {
			var l:OrderedLock = it.next();
			l.lock();
		}
	}
	
	//Release a list of locks
	public static def releaseLocks(locks:List[OrderedLock]):void {
		var it:ListIterator[OrderedLock] = locks.iterator();
		while(it.hasNext()) {
			var l:OrderedLock = it.next();
			l.unlock();
		}
	}

	//the global map to store locks
	static val lockMap:Map[Int, OrderedLock] = new HashMap[Int, OrderedLock]();
	//the lock to protect accessing of the global lock map
	static val mlock:Lock = new Lock();
	
	//create a new lock, put it into the global lock map, and return it
	public static def createNewLock() : OrderedLock {
		try {
			mlock.lock();
			var lock:OrderedLock = new OrderedLock();
		    lockMap.put(lock.getIndex(), lock);
		    return lock;
		} finally {
			mlock.unlock();
		}
	}
	
	//create a new lock, put it into the global lock map, and return its id
	public static def createNewLockID() : Int {
		try {
			mlock.lock();
			var lock:OrderedLock = new OrderedLock();
		    lockMap.put(lock.getIndex(), lock);
		    return lock.getIndex();
		} finally {
			mlock.unlock();
		}
	}
	
	//get the lock from the global lock map by id
	public static def getLock(lockId:Int) : OrderedLock {
		try {
			mlock.lock();
		    return lockMap.getOrThrow(lockId);
		} finally {
			mlock.unlock();
		}
	}
}


//FIXME ugly hack:
//a unique id generator to return an increasing id everytime when called
class Counter {
	
	private var num:Int = -1;
	
	public def getCount():Int {
		num = num + 1;
		return num;
	}
	//the singleton counter object
	public static val counter:Counter = new Counter();
	
	public static def newInstance() : Counter {
		return counter;
	}
	
	public static def getIndex() : Int {
		return counter.getCount();
	}
}