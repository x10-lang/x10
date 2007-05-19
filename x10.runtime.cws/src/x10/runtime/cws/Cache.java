package x10.runtime.cws;

public  class Cache {
	public static final int MAXIMUM_CAPACITY = 1 << 30;
	public static final int INITIAL_CAPACITY = 1 << 13; // too high??
	public static final int EXCEPTION_INFINITY = Integer.MAX_VALUE;
	// these are indices into a table of Frames.
	volatile int head;
	volatile int tail;
	volatile int exception;
	Frame[] stack;
	public Cache() {
		
	}
	 /**
     * Pushes a task. Called only by current thread.
     * @param x the task
     */
    final void pushFrame(Frame x) {
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
        
        newArray[tail] = x;
        stack = newArray;
        ++tail;
    }
    public void resetExceptionPointer() {
    	exception = head;
    }
    /**
     * TODO: Ensure that a fence is not needed after the write to exception.
     *
     */
    public void incrementExceptionPointer() {
    	if (exception != EXCEPTION_INFINITY)
    		++exception;
    	
    }
    public void decrementExceptionPointer() {
    	if (exception != EXCEPTION_INFINITY)
    		--exception;
    	
    }
    /**
     * TODO: Check that the write to the volatile variable
     * is visible to every other thread.
     *
     */
    public void signalImmediateException() {
    	exception = EXCEPTION_INFINITY;
    }
    public boolean atTopOfStack() {
    	return head+1 == tail;
    }
    public Frame childFrame() {
    	return stack[head+1];
    }
    public void popFrame() {
		--tail;
	}
	public boolean popCheck() {
		int t = tail;
		// need a store load fence.
		return exception >= t;
	}
	
}