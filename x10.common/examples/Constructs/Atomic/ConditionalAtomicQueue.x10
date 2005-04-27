import x10.lang.*;

/**
 * When test
 * Using producer-consumer paradigm
 * @author kemal, 12/2004
 */

/**
 * T is the type of the item that is being produced and consumed
 */
class T {
	
	static final int N=/*3*/2;
	
	int val; //  the id of the item
	
	T (int i, int j) { // produce a T
		val= N*i+j;
	}
	
	public void consume() { // consume a T
	}
	
	public int getval() {return val;}
	
}


/**
 * circular buffer implementation of a FIFO queue
 */

public class ConditionalAtomicQueue {
	
	final private int siz=3/*10*/;
	private final T[] Q;      // The circular buffer
	private int nelems; // number of items in buffer Q
	private int tail; // next free slot to insert incoming items
	// at tail of queue
	private int head; // pointer to item to remove from the front
	
	public ConditionalAtomicQueue() {
		Q= new T[siz];
		nelems=0;
		tail=0;
		head=0;
	}
	
	/**
	 * insert i at the tail end of fifo queue.
	 */
	void insert(T i) {
		Q[tail]=i;
		tail=inc(tail,siz);
		nelems++;
	}
	
	/**
	 * remove an item from the queue
	 */
	
	T remove() {
		T t=Q[head];
		head=inc(head,siz);
		nelems--;
		return t;
	}
	/**
	 * increment x modulo n
	 */
	static int inc(int x,int n) {
		int y=x+1;
		return y==n?0:y;
	}
	
	/**
	 * true iff queue is empty 
	 */
	boolean empty() {
		chk(nelems> -1);
		return nelems<=0;
	}
	
	/**
	 * true iff queue is full
	 */
	boolean full() {
		chk(nelems < siz+1);
		return nelems>=siz;
	}

	void chk(boolean b) {
		if(!b) throw new Error();
	}
	
	
	public boolean run() {
		final int N= T.N;
		final int NP= place.MAX_PLACES;
		final distribution D2= Dist.val(N*NP);
		final int[.] received = new int[D2];
		
		finish {
			// spawn producer activities on each place
			async( here )
			ateach(point [i]:Dist.unique()) {
				for(point [j]: 0:(N-1)) { 
					final T t= new T(i,j); // produce a T
					async(this.location) {
						when(!full()) {insert(t);} 
					} 
				}
			}
			// spawn a single consumer activity in place P0
			async( here ) {
				for(point p: D2) {
					T t;
					when(!empty()) { t=remove(); }
					final T t1=t;
					async(t1.location) {t1.consume();}// consume the T
					final int m=future( t1.location ){t1.getval()}.force();
					received[m]+=1;
                    // remember how many times 
					// we received this item
				}
			}
		}
		
		// Ensure all messages were received exactly once
		for(point p: D2) {
			if (received[p]!=1) return false;
		}
		
		// Ensure the FIFO queue is empty now
		if (!empty()) return false;
		
		return true;
	}
	public static void main(String args[]) {
		boolean b= (new ConditionalAtomicQueue()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
	
}
/**
 * Utility routines to create simple common distributions
 */
class Dist {
    /**
     * create a simple 1D blocked distribution
     */
   static distribution block (int arraySize) {
       return distribution.factory.block(0:(arraySize-1));
    }
    /**
     * create a unique distribution (mapping each i to place i)
     */
    
    static distribution unique () {
        return distribution.factory.unique(place.places);
    }
    
    /**
     * create a constant-Here distribution 
     */
    static distribution val(int arraySize) {
        return  (0:(arraySize-1))->here;
    }
}
