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
	
	public static def acquireLocksInOrder(locks:List[OrderedLock]):void {
		//first sort the lock, then acquire in the order of index
		locks.sort();
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
	
	static val objectLockMap:Map[Int, OrderedLock] = new HashMap[Int, OrderedLock]();
	static val classLockMap:Map[Int, OrderedLock] = new HashMap[Int, OrderedLock]();
	
	public static def createAndStoreObjectLock() : OrderedLock {
		var lock:OrderedLock = new OrderedLock();
		objectLockMap.put(lock.getIndex(), lock);
		return lock;
	}
	
	public static def createObjectLock() : Int {
		var lock:OrderedLock = new OrderedLock();
		objectLockMap.put(lock.getIndex(), lock);
		return lock.getIndex();
	}
	
	public static def hasObjectLock(lockId:Int) : boolean {
		return objectLockMap.containsKey(lockId);
	}
	
	public static def getObjectLock(lockId:Int) : OrderedLock {
		if(!hasObjectLock(lockId)) {
			Console.OUT.println("Can not find lock id: " + lockId);
		}
		return objectLockMap.getOrThrow(lockId);
	}
	
	//for class lock
	public static def createClassLock() : Int {
		var lock:OrderedLock = new OrderedLock();
		classLockMap.put(lock.getIndex(),lock);
		return lock.getIndex();
	}
	
	public static def hasClassLock(lockId:Int) : boolean {
		return classLockMap.containsKey(lockId);
	}
	
	public static def getClassLock(lockId:Int) : OrderedLock {
		return classLockMap.getOrThrow(lockId);
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