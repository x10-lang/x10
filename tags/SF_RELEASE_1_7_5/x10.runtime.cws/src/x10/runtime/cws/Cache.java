package x10.runtime.cws;

/**
 * Package-specific class, used by Worker and Closure to cache
 * the frames for the bottom most closure in a worker's ready deque.
 * 
 * The design of this library is based on the Cilk runtime, developed by the Cilk
 * group at MIT.
 * 
 * @author vj 05/18/2007
 */
class Cache {
	private static final int CHUNK_SIZE_LOG = 14; /* 16K */
	private static final int CHUNK_SIZE = 1 << CHUNK_SIZE_LOG;
	private static final int MAXIMUM_CAPACITY = 1 << 30;
	private static final int INITIAL_CAPACITY = 4 * CHUNK_SIZE;
	
	public static final int EXCEPTION_INFINITY = Integer.MAX_VALUE;
	/*
	 * Logically a Frame[], but maintained as a chunked Frame[][] to
	 * bound the costs of incrementally growing the array without requiring
	 * pre-allocation of large arrays most of which will not be used.
	 */
	private final Frame[][] stack;
	private volatile int head, tail, exception; // these are indices into stack.
	protected final Worker owner;

	protected Cache(Worker w) {
		owner=w;
		stack = new Frame[MAXIMUM_CAPACITY/CHUNK_SIZE][];
		for (int i=0; i<(INITIAL_CAPACITY/CHUNK_SIZE)+1; i++) {
			stack[i] = new Frame[CHUNK_SIZE];
		}	
	}

	public String toString() { return "Cache("+owner.index+")";}
	
	private final int chunkIndex(int i) { assert i >= 0; return i >> CHUNK_SIZE_LOG; }
	private final int chunkOffset(int i) { assert i >= 0; return i & (CHUNK_SIZE-1); }
	private final Frame getFrame(int i) { return stack[chunkIndex(i)][chunkOffset(i)]; }
	
	 /**
     * Pushes a task. Called only by current thread.
     * TODO: Need to make sure the increment to tail is seen
     * by all processors after the assignment array[tail]=x.
     * Does Java allow an array of volatiles?
     * @param x the task
     */
    protected final void pushFrame(Frame x) {
    	assert x != null;
    	int localTail = tail;
    	Frame[] chunk = getChunk(localTail);
    	chunk[chunkOffset(localTail)] = x;
    	tail++;
    }
    protected void pushIntUpdatingInPlace(int x) {
    	int localTail = tail;
    	Frame[] chunk = getChunk(localTail);
    	Frame f = chunk[chunkOffset(localTail)];
    	if (f != null) {
    		f.setInt(x);
    	} else {
			Worker w = (Worker) Thread.currentThread();
			f = w.fg.make();
			f.setInt(x);
			chunk[chunkOffset(localTail)] = f;
    	}
    	tail++;
    }
    protected void pushObjectUpdatingInPlace(Object x) {
    	int localTail = tail;
    	Frame[] chunk = getChunk(localTail);
    	Frame f = chunk[chunkOffset(localTail)];
    	if (f != null) {
    		f.setObject(x);
    	} else {
			Worker w = (Worker) Thread.currentThread();
			f = w.fg.make();
			f.setObject(x);
			chunk[chunkOffset(localTail)] = f;
    	}
    	tail++;
    }
 
 	private final Frame[] getChunk(int i) { 		
    	Frame[] chunk = stack[chunkIndex(i)];
    	if (chunk == null) {
    		chunk = new Frame[CHUNK_SIZE];
    		stack[chunkIndex(i)] = chunk;
    	}
    	return chunk;
 	}
 	
    protected void signalImmediateException() { exception = EXCEPTION_INFINITY; }
    protected boolean atTopOfStack() { 	return head+1 == tail; }
    protected Frame childFrame() { return getFrame(head+1);  }
    protected Frame topFrame() {   return getFrame(head);   }
    public Frame currentFrame() {  return getFrame(tail-1); }
    public void incHead() { ++head; }
	public boolean exceptionOutstanding() { return head <= exception; }
	public int head() { return head;}
	public int tail() { return tail;}
	public int exception() { return exception;}
    public Frame currentFrameIfStackExists() {
    	return (head < tail)? getFrame(tail-1) : null;
    }
	public boolean empty()    { return head >=tail; }
	// TODO: DAVE.  pop should null out stack entry; we're retaining garbage here!
    protected void popFrame() { --tail; assert tail >=0; }
    /**
     * The victim's portion of Dekker.
     * @return true iff an exception has been posted against
     *              the current closure.
     */
	protected boolean interrupted() { return exception >= tail; }
	public String dump() {
		return this.toString() + "(head=" + head + " tail=" + tail + " exception=" + exception + ")";
	}
	// TODO: DAVE.  need to null out old data to avoid retaining garbage.
	public void reset() {
		tail=0; // order is imp.
		head=0;
		exception=0;
		/*while (t >= 0) {
			stack[t]=null;
			t--;
		}*/
	}
	
	/**
	 * Do the thief part of Dekker's protocol.  Return true upon success,
	 * false otherwise.  The protocol fails when the victim already popped
	 * T so that E=T.
	 * Must be the case that Thread.currentThread()==thief.
	 */
	boolean dekker(Worker thief) {
		assert thief !=owner;
		if (exception != EXCEPTION_INFINITY) ++exception;
		if ((head + 1) >= tail) {
			if (exception != EXCEPTION_INFINITY) --exception;
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

	public int queueSize() {
		return tail-head;
	}
	/**
	 * A fast way of determining whether the worker has been interrupted.
	 * @param w -- the current worker, potentially a victim
	 * @return
	 */
    public void popFrameAndReset(Worker w) {
	popAndReturnFrame(w);
    }
	public Frame popAndReturnFrame(Worker w) {
		assert w==owner;
		try {
			if (head >= tail) { return null;}
			tail--; 
			if (interrupted()) { // there has been a theft -- rare case.
				w.lock(w);
				// need to lock to ensure that we get the right value for head.
				// have to set exception so that the interrupt is acknowledged.
				exception=head;
				
				w.unlock();
			}
			int localTail = tail;
			Frame[] chunk = stack[chunkIndex(localTail)];
			Frame f = chunk[chunkOffset(localTail)];
			chunk[chunkOffset(localTail)] = null;
			return f;	
		} finally {
			assert (head >=0);
			assert (tail >= 0);
		}
	}
}
