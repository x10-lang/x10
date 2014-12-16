package x10.core.concurrent;

@SuppressWarnings("serial")
public class Condition extends java.util.concurrent.locks.ReentrantLock {
    protected boolean unblocked;
    protected java.lang.Thread thread = null;
    
    public Condition(System[] $dummy) {
    }
    
    public final Condition x10$core$concurrent$Condition$$init$S() {
        return this;
    }
    
    public void release() {
        lock();
        unblocked = true;
        if (null != thread) java.util.concurrent.locks.LockSupport.unpark(thread);
        unlock();
    }
    
    public void await() {
        if (!unblocked) {
            lock();
            if (!unblocked) {
                thread = java.lang.Thread.currentThread();
                while (!unblocked) {
                    unlock();
                    java.util.concurrent.locks.LockSupport.park();
                    lock();
                }
            }
            unlock();
        }
    }

    public void await(long timeout) {
        if (!unblocked) {
            lock();
            if (!unblocked) {
                thread = java.lang.Thread.currentThread();
                unlock();
                java.util.concurrent.locks.LockSupport.parkNanos(timeout);
                lock();
            }
            unlock();
        }
    }
    
    public boolean complete$O() {
        return unblocked;
    }
}
