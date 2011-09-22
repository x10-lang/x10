package x10.util.concurrent;

import x10.util.List;
import x10.util.ListIterator;
import x10.util.Map;
import x10.util.HashMap;

public class OrderedLock implements Comparable[OrderedLock] {
	
	// //keep a global lock allocation count
	// private static val array:Array[Int] = new Array[Int](1);
	
	private var lock:Lock = null;
	private var index:Int = -1;
	
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
	
	//some shortcut to void use the list
	//in most cases, there will be only 1 - 3 locks needed
	public static def acquireSingleLock(that:OrderedLock) : void {
		that.lock();
	}
	
	public static def releaseSingleLock(that:OrderedLock) : void {
		that.unlock();
	}
	
	//2 locks
	public static def acquireTwoLocks(that1:OrderedLock, that2:OrderedLock) : void {
		that1.lock();
		that2.lock();
	}
	
	public static def releaseTwoLocks(that1:OrderedLock, that2:OrderedLock) : void {
		that1.unlock();
		that2.unlock();
	}
	
	//3 locks
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
	
	//the operation for acquiring locks
	public static def acquireLocksInOrder(locks:List[OrderedLock]):void {
		//first sort the lock, then acquire in the order of index
		//locks.sort(); //no need to sort
		//iterate through the locks
		var it:ListIterator[OrderedLock] = locks.iterator();
		while(it.hasNext()) {
			var l:OrderedLock = it.next();
			l.lock();
		}
	}
	
	public static def releaseLocks(locks:List[OrderedLock]):void {
		var it:ListIterator[OrderedLock] = locks.iterator();
		while(it.hasNext()) {
			var l:OrderedLock = it.next();
			l.unlock();
		}
	}

	//the data structure to keep the lock map
	static val lockMap:Map[Int, OrderedLock] = new HashMap[Int, OrderedLock]();
	
	static val mlock:Lock = new Lock();
	
	//it returns a lock
	public static def createAndStoreObjectLock() : OrderedLock {
		try {
			mlock.lock();
			var lock:OrderedLock = new OrderedLock();
		    lockMap.put(lock.getIndex(), lock);
		    //Console.OUT.println("Put object lock id: " + lock.getIndex());
		    return lock;
		} finally {
			mlock.unlock();
		}
	}
	
	//it returns the lock id
	public static def createObjectLock() : Int {
		try {
			mlock.lock();
			var lock:OrderedLock = new OrderedLock();
		    lockMap.put(lock.getIndex(), lock);
		    //Console.OUT.println("Put object lock id: " + lock.getIndex());
		    return lock.getIndex();
		} finally {
			mlock.unlock();
		}
	}
	
	public static def hasObjectLock(lockId:Int) : boolean {
		try {
			mlock.lock();
		    return lockMap.containsKey(lockId);
		} finally {
			mlock.unlock();
		}
	}
	
	public static def getObjectLock(lockId:Int) : OrderedLock {
		// if(!hasObjectLock(lockId)) {
		// 	Console.OUT.println("Can not find obj lock id: " + lockId);
		// }
		try {
			mlock.lock();
		    return lockMap.getOrThrow(lockId);
		} finally {
			mlock.unlock();
		}
	}
	
	//for class lock
	public static def createClassLock() : Int {
		try {
		  mlock.lock();
		  var lock:OrderedLock = new OrderedLock();
		  lockMap.put(lock.getIndex(),lock);
		  //Console.OUT.println("Put class lock id: " + lock.getIndex());
		  return lock.getIndex();
		} finally {
			mlock.unlock();
		}
	}
	
	public static def hasClassLock(lockId:Int) : boolean {
		return lockMap.containsKey(lockId);
	}
	
	public static def getClassLock(lockId:Int) : OrderedLock {
		return lockMap.getOrThrow(lockId);
	}
}

//ugly hack
class Counter {
	
	private var num:Int = -1;
	
	public def getCount():Int {
		num = num + 1;
		return num;
	}
	
	public static val counter:Counter = new Counter();
	
	public static def newInstance() : Counter {
		return counter;
	}
	
	public static def getIndex() : Int {
		return counter.getCount();
	}
}