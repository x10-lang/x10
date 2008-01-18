package x10.runtime.cws;

/**
 * Package-specific class, used by Worker and Closure to cache
 * the frames for the bottom most closure in a worker's ready deque.
 * 
 * The design of this library is based on the Cilk runtime, developed by the Cilk
 * group at MIT.
 * 
 * @author vj 05/18/2007
 *
 */
class Cache {
	public static final int MAXIMUM_CAPACITY = 1 << 30;
	public static final int INITIAL_CAPACITY = 1 << 20; // too high??
	public static final int EXCEPTION_INFINITY = Integer.MAX_VALUE;
	
	public Frame[] stack;
	// these are indices into stack.
	private volatile int head, tail, exception;
	
	protected final Worker owner;
	protected Cache(Worker w) {owner=w;}
	public String toString() { return "Cache("+owner.index+")";}
	 /**
     * Pushes a task. Called only by current thread.
     * TODO: Need to make sure the increment to tail is seen
     * by all processors after the assignment array[tail]=x.
     * Does Java allow an array of volatiles?
     * @param x the task
     */
    final protected void pushFrame(Frame x) {
    	assert x !=null;
    	Frame[] array = stack;
    	if (array != null && tail < array.length - 1) {
    		
    		array[tail]=x;
    		++tail;
    		return;
    	}
    	growAndPushFrame(x);
    }
    protected void pushIntUpdatingInPlace(int x) {
    	
    	Frame[] array = stack;
    	if (array != null && tail < array.length - 1) {
    		if (array[tail] != null) {
    			array[tail].setInt(x);
    		} else {
    			Worker w = (Worker) Thread.currentThread();
    			Frame f = w.fg.make();
    			f.setInt(x);
    			array[tail] = f;
    			
    		}
    		++tail;
    		return;
    	}
    	Worker w = (Worker) Thread.currentThread();
		Frame f = w.fg.make();
		f.setInt(x);
    	growAndPushFrame(f);
    	
    }
 protected void pushObjectUpdatingInPlace(Object x) {
    	
    	Frame[] array = stack;
    	if (array != null && tail < array.length - 1) {
    		if (array[tail] != null) {
    			array[tail].setObject(x);
    		} else {
    			Worker w = (Worker) Thread.currentThread();
    			Frame f = w.fg.make();
    			f.setObject(x);
    			array[tail] = f;
    			
    		}
    		++tail;
    		return;
    	}
    	Worker w = (Worker) Thread.currentThread();
		Frame f = w.fg.make();
		f.setObject(x);
    	growAndPushFrame(f);
    	
    }
	  /*
     * Handles resizing and reinitialization cases for pushFrame
     * @param x the task
     */
    private void growAndPushFrame(Frame x) {
        int oldSize = 0;
        int newSize = 0;
        Frame[] oldArray = stack;
        if (oldArray != null) {
            oldSize = oldArray.length;
            newSize = oldSize << 1;
        }
        if (newSize < INITIAL_CAPACITY)
            newSize = INITIAL_CAPACITY;
        if (newSize > MAXIMUM_CAPACITY)
            throw new Error("Frame stack size exceeded");
        Frame[] newArray = new Frame[newSize];
        if (oldArray != null) {
            for (int i = head; i < tail; ++i) {
                newArray[i] = oldArray[i];
            }
        }
        newArray[tail] = x;
        stack = newArray;
        ++tail;
    }
    
    /**
     * TODO: Check that the write to the volatile variable
     * is visible to every other thread.
     *
     */
    protected void signalImmediateException() {
    	exception = EXCEPTION_INFINITY;
    }
    protected boolean atTopOfStack() {
    	return head+1 == tail;
    }
   
    protected Frame childFrame() {
    	return stack[head+1];
    }
    protected Frame topFrame() {
    	return stack[head];
    }
    public Frame currentFrame() {
    	return stack[tail-1];
    }
    
    public Frame currentFrameIfStackExists() {
    	return (stack !=null && head  < tail)? stack[tail-1] : null;
    }
   
    protected void popFrame() {
		--tail;
	}
    /**
     * The victim's portion of Dekker.
     * @return true iff an exception has been posted against
     *              the current closure.
     */
	protected boolean interrupted() {
		return exception >= tail;
	}
	public String dump() {
		return this.toString() + "(head=" + head + " tail=" + tail + " exception=" + exception + ")";
	}
	public boolean empty() {
		return head >=tail;
	}
	
	public void reset() {
		int t = tail;
		int h = head;
		tail=0; // order is imp.
		head=0;
		exception=0;
		
		/*while (t >= 0) {
			stack[t]=null;
			t--;
		}*/
		
		
		
	}
	public void incHead() {
		++head;
	}
	public boolean exceptionOutstanding() {
		return head <= exception;
	}
	public int head() { return head;}
	public int tail() { return tail;}
	public int exception() { return exception;}
	

	/**
	 * Do the thief part of Dekker's protocol.  Return true upon success,
	 * false otherwise.  The protocol fails when the victim already popped
	 * T so that E=T.
	 * Must be the case that Thread.currentThread()==thief.
	 */
	boolean dekker(Worker thief) {
		assert thief !=owner;
		if (exception != EXCEPTION_INFINITY)
    		++exception;
		if ((head + 1) >= tail) {
			if (exception != EXCEPTION_INFINITY)
	    		--exception;
			return false;
		}
		// so there must be at least two elements in the framestack for a theft.
		if ( Worker.reporting) {
			System.out.println(thief + " has found victim " + owner);
		}
		return true;
	}
    
	public void resetExceptionPointer(Worker w) {
		assert w==owner;
		exception=head;
	}
	/**
	 * A fast way of determining whether the worker has been interrupted.
	 * @param w
	 * @return
	 */
	public Frame popAndReturnFrame(Worker w) {
		assert w==owner;
		try {
			if (head >= tail) return null;
			tail--;
			if (interrupted()) { // there has been a theft -- rare case.
				w.lock(w);
				// need to lock to ensure that we get the right value for head.
				// have to set exception so that the interrupt is acknowledged.
				exception=head;
				w.unlock();
			} 
			
			Frame f = stack[tail];
			stack[tail]=null;
			return f;
			
		} finally {
			assert (head >=0);
			assert (tail >= 0);
		}
	}
}