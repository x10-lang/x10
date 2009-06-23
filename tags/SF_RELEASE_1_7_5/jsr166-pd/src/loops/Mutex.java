package loops;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

 class Mutex implements Lock, java.io.Serializable {

    // Our internal helper class
    private static class Sync extends AbstractQueuedSynchronizer {
      // Report whether in locked state
      protected boolean isHeldExclusively() {
        return getState() == 1;
      }

      // Acquire the lock if state is zero
      public boolean tryAcquire(int acquires) {
        assert acquires == 1; // Otherwise unused
        return compareAndSetState(0, 1);
      }

      // Release the lock by setting state to zero
      protected boolean tryRelease(int releases) {
        assert releases == 1; // Otherwise unused
        if (getState() == 0) throw new IllegalMonitorStateException();
        setState(0);
        return true;
      }

      // Provide a Condition
      Condition newCondition() { return new ConditionObject(); }

      // Deserialize properly
      private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        setState(0); // reset to unlocked state
      }
    }

    // The sync object does all the hard work. We just forward to it.
    private final Sync sync = new Sync();

    public void lock()                { sync.acquire(1); }
    public boolean tryLock()          { return sync.tryAcquire(1); }
    public void unlock()              { sync.release(1); }
    public Condition newCondition()   { return sync.newCondition(); }
    public boolean isLocked()         { return sync.isHeldExclusively(); }
    public boolean hasQueuedThreads() { return sync.hasQueuedThreads(); }
    public void lockInterruptibly() throws InterruptedException {
      sync.acquireInterruptibly(1);
    }
    public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
      return sync.tryAcquireNanos(1, unit.toNanos(timeout));
    }
 }
