package x10.core.concurrent;

@SuppressWarnings("serial")
public class Condition extends java.util.concurrent.locks.ReentrantLock {
    protected Boolean blocked = true;
    protected java.lang.Thread thread = null;
    
    public Condition(System[] $dummy) {
    }
    
    public final Condition x10$core$concurrent$Condition$$init$S() {
        return this;
    }
    
    public void release() {
        lock();
        blocked = false;
        if (null != thread) java.util.concurrent.locks.LockSupport.unpark(thread);
        unlock();
    }
    
    public void await() {
        if (blocked) {
            lock();
            if (blocked) {
                thread = java.lang.Thread.currentThread();
                while (blocked) {
                    unlock();
                    java.util.concurrent.locks.LockSupport.park();
                    lock();
                }
            }
            unlock();
        }
    }
}
