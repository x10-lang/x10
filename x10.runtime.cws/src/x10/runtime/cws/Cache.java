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
 public class Cache {
	public static final int MAXIMUM_CAPACITY = 1 << 30;
	public static final int INITIAL_CAPACITY = 1 << 13; // too high??
	public static final int EXCEPTION_INFINITY = Integer.MAX_VALUE;
	
	protected Frame[] stack;
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
    protected void resetExceptionPointer() {
    	exception = head;
    }
    /**
     * TODO: Ensure that a fence is not needed after the write to exception.
     *
     */
    protected void incrementExceptionPointer() {
    	if (exception != EXCEPTION_INFINITY)
    		++exception;
    	
    }
    protected void decrementExceptionPointer() {
    	if (exception != EXCEPTION_INFINITY)
    		--exception;
    	
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
    protected void popFrame() {
		--tail;
	}
    /**
     * The victim's portion of Dekker.
     * @return true iff an exception has been posted against
     *              the current closure.
     */
    protected int lastException;
	protected boolean popCheck() {
		int t = tail;
		int e = exception;
		lastException = e;
		return e >= t;
	}
	public String dump() {
		return this.toString() + "(head=" + head + " tail=" + tail + " exception=" + exception + ")";
	}
	protected boolean empty() {
		return head >=tail;
	}
	public boolean headAheadOfTail() {
		return head==tail+1;
	}
	public boolean notEmpty() {
		return head < tail;
	}
	
	protected void reset() {
		head=tail=exception=0;
	}
	public void incHead() {
		++head;
	}
	public boolean exceptionOutstanding() {
		return head <= exception;
	}
	public int head() { return head;}
	public int tail() { return tail;}
	
}