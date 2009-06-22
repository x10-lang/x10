/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/* An X10 implementation of ConcurrentHashMap
 * 
 * One important note about this adaptation to X10 is that most of the classes in this
 * file have had X10 added somewhere to their name (e.g., ConcurrentX10HashMap). If these
 * are removed and the dependence on x10.lang.Object is removed, it should be compatible
 * with the normal usage of java.util.ConcurrentHashMap.
 *
 * UPDATE: The above no longer holds true. If non-X10 objects are used in the HashMap,
 * errors will be generated as they do not have a location field. This could potentially
 * be fixed in a future update to X10.
 * 
 * @author Shane Markstrum
 * @version 08/02/06
 * @see java.util.concurrent.ConcurrentHashMap
 */
package x10.concurrent.util;
// package support seems a little broken in X10 currently
 
import x10.lang.Object;
import x10.util.*;
//import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.lang.IllegalArgumentException;
 
public class ConcurrentX10HashMap extends x10.lang.Object {
    /* ---------------- Constants -------------- */

    /**
     * The default initial number of table slots for this table.
     * Used when not otherwise specified in constructor.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 6;

    /**
     * The maximum capacity, used if a higher value is implicitly
     * specified by either of the constructors with arguments.  MUST
     * be a power of two <= 1<<30 to ensure that entries are indexible
     * using ints.
     */
    static final int MAXIMUM_CAPACITY = 1 << 30; 

    /**
     * The default number of concurrency control segments.
     **/
    static final int DEFAULT_SEGMENTS = 16;

    /**
     * The maximum number of segments to allow; used to bound
     * constructor arguments.
     */
    static final int MAX_SEGMENTS = 1 << 16; // slightly conservative

	
    // Declarations to mirror ConcurrentHashMap
	
    int SegmentMask;
    int SegmentShift;
    int numSegments;
    Set keySet;
    Set entrySet;
    Collection values;
    
    // Changed the array to an X10 array to support the ability to distribute the
    // segments over multiple places.
    
    X10Segment[.] Segments;
	
	
    /** Borrowed wholesale from ConcurrentHashMap.java
     *
     * Returns a hash code for non-null Object x.
     * Uses the same hash code spreader as most other java.util hash tables.
     * @param x the object serving as a key
     * @return the hash code
     */
    static int hash(final Object x) {
    	final nullable<Object> nn_x = (nullable<Object>)x;
        int h = future(nn_x){nn_x.hashCode()}.force();
        h += ~(h << 9);
        h ^=  (h >>> 14);
        h +=  (h << 4);
        h ^=  (h >>> 10);
        return h;
     }
	
    /** Returns a reference to the segment which contains objects that map
     * to the hashcode.
     * @param hash the hash code of an entry to lookup
     * @return the segment which contains entries for that hashcode
     */
    final X10Segment segmentFor(final int hash) {
        return future (Segments.distribution[((hash >>> SegmentShift) & SegmentMask)+1]) {
        	Segments[((hash >>> SegmentShift) & SegmentMask)+1] }.force();
    }

    /* ------------ Helper Classes ------------------ */

    /** X10HashMapValue is a box for containing a mutable reference to an object
     * representing a value in the HashMap. Such a box class is required for
     * mutable state in value classes in X10.
     */
    static class X10HashMapValue {
    	
    	 // Since X10HashMapValue is a reference class, value is a mutable
    	 // field which may hold any Object (including null).
        public nullable<Object> value;
    	
    	 // Constructor simply assigns a nullable<Object> to the value field
        X10HashMapValue(final nullable<Object> value){
        	this.value = value;
        }
        
    }
	
    /** X10HashEntry is declared as an X10 value class. As a result, any entry can
     * be passed between any number of places. In this way, resizing, shuffling,
     * or migrating the hash map (or its segments) should be simplified.
     *
     * Due to the current limited Java generics support, all generics code has been
     * reverted to Objects.
     */
    static value class X10HashEntry{
		
    	// Key, Hash, and Next correspond to their counterparts in HashEntry.
    	// vbox takes the place of value as it must be unboxed to gain access to
    	// the mutable data.
    	
    	Object Key;
    	int Hash;
    	X10HashMapValue vbox;
    	nullable<X10HashEntry> Next;
    	
    	/** Basic constructor. The key difference here from the original is the creation of 
    	 * the box for housing the value.
    	 */
    	X10HashEntry(Object key, int hash,final nullable<Object> value, nullable<X10HashEntry> next){
    		this.Key = key;
    		this.Hash = hash;
    		this.vbox = new X10HashMapValue(value);
    		this.Next = next;
    	}		
    	
    	/** updateValue replaces the object in the value box with the supplied argument
    	 * @param value Object with which to replace current value
    	 */
    	void updateValue(final nullable<Object> value) {
    		/*nullable<Object> fvalue = future (value) { value }.force();*/
    		atomic {
    			this.vbox.value = /*f*/value;
    		}
    	}
    	
    	/** getValue unboxes the value element of this entry
    	 * @return a nullable<Object> reference corresponding to the value element
    	 */
    	atomic nullable<Object> getValue(){
    		return this.vbox.value;
    	}
    }
	
    /** X10Segment is a helper class for ConcurrentX10HashMap which is meant to
     * act as a distributed segment of the total hash map.
     *
     * On each X10Segment, operations such as get, put, and clear can be run
     * concurrently.
     *
     * At this moment, resizing support has been removed, but it may be replaced
     * in the near future.
     */
    
    static final class X10Segment{
    	
    	int count;
    	int modCount;
    	int size;
    	final Semaphore seg_sem;
    	int seg_id;
    	nullable<X10HashEntry>[.] table;
    	
    	/** Default constructor.
    	 * If no distribution is given, it is assumed that the array should be
    	 * defined at the place where the constructor is called.
    	 * param initCapacity the initial capacity of the X10Segment's table
    	 */    	
    	X10Segment(final int initCapacity, final int seg_id){
    		seg_sem = new Semaphore();
    		this.size = initCapacity;
    		this.seg_id = seg_id;
    		this.table = new (nullable<X10HashEntry>)[[0:initCapacity-1]];
    	}
    	
    	/** Constructor with distribution.
    	 * If an appropriately shaped distribution is provided (i.e., it has a
    	 * linear shaped region with at least as many points as the size of
    	 * initCapacity), then use that distribution when creating the array.
    	 * param initCapacity the initial capacity of the X10Segment's table
    	 * param D the distribution to use for table
    	 */
    	X10Segment(final int initCapacity, final dist D){
    		seg_sem = new Semaphore();
    		if ( (((region(:self.rank==1)) D.region) && [0:initCapacity-1]).size() != initCapacity )
    			throw new IllegalArgumentException();
    		this.size = initCapacity;
    		this.table = new (nullable<X10HashEntry>)[D|[0:initCapacity-1]];
    	}

    	/* Rudimentary semaphores with conditional atomics are
    	   required for proper synchronization of a truly 
    	   distributed and concurrent data structure. */
    	
    	/* p_aux returns true if the semaphore is grabbed,
    	   otherwise it returns false. */
    	   
/*    	private boolean p_aux(){
    		int tmp;
    		when(num_access > 0){
    			tmp = --num_access;
    		}
    		if(tmp != 0){
    			v();
    			return false;
    		}    		
    		return true;
    	}
    	
    	/* p calls p_aux until the semaphore grants access. */
    	
    	void p(){
    		seg_sem.p();
    	}
    	
    	/* v atomically increments the semaphore. */
    	void v(){
	    seg_sem.v();
    	}
    	
    	/** a method which returns the appropriate first element in the linked
    	 * list associated with hash.
    	 * param hash the hash code to lookup.
    	 */
    	nullable<X10HashEntry> getFirst(final int hash){
    		final point phash = [hash & (size-1)];
    		return future (table.distribution[phash]) { table[phash] }.force();
    	}
    	
    	/** Lookup the value associated with the particular key and hash.
    	 * @param key the key for the entry
    	 * @param hash the hash code for the entry
    	 * @return a nullable object corresponding to the value for the entry
    	 */
    	nullable<Object> get(final Object key, final int hash) {
    	      if (count != 0) { // read-volatile
    	    	  nullable<X10HashEntry> e = getFirst(hash);
    	          while (e != null) {
    	        	  final nullable<X10HashEntry> fe = e;
    	        	  final Object eKey = future (e){fe.Key}.force();
    	        	  if (future(e){fe.Hash}.force() == hash && future (key) {key.equals(eKey)}.force())
    	        		  return future (e){fe.getValue()}.force();
    	        	  e = future(e){fe.Next}.force();
    	          }
    	      }
    	      return null;
    	}
    	
    	/** Determines whether a particular object is used as a key in
    	 * this segment of the hash map.
    	 * @param key the key object
    	 * @param hash the hash code for the key
    	 * @return a boolean corresponding to whether key is in the hash map
    	 */
    	boolean containsKey(final Object key, final int hash) {
    	      if (count != 0) { // read-volatile
    	    	  nullable<X10HashEntry> e = getFirst(hash);
    	          while (e != null) {
    	        	  final nullable<X10HashEntry> fe = e;
    	        	  final Object eKey = future(e) {fe.Key}.force();
    	        	  if (future(e){fe.Hash}.force() == hash && future (key) {key.equals(eKey)}.force())
    	        		  return true;
    	        	  e = future(e){fe.Next}.force();
    	          }    		
    	      }
    	      return false;
    	}
	       
    	/** Determines whether an object is used as an entry value in
    	 * this segment of the hash map.
    	 * @param value the object to seek in the segment
    	 * @return a boolean corresponding to whether the value was found
    	 */
    	boolean containsValue(final Object value) {
    		if (count != 0) { // read-volatile
    		    for ( point p : table ) {
    			for (nullable<X10HashEntry> e = future (table.distribution[p]) { table[p] }.force(); 
    			     e != null ; ) { 
    				final nullable<X10HashEntry> fe = e;
    				final nullable<Object> v = future(e){fe.getValue()}.force();
    				if (future (value) {value.equals(v)}.force())
    					return true;
    				e = future(e){fe.Next}.force();
    			}    			
    		    }
    		}
    		return false;
    	}
	  
    	/** Replaces the value of entry corresponding to the key, hash, and object if it
    	 * is in this segment.
    	 * @param key the key for the entry
    	 * @param hash the hash code for the entry
    	 * @param oldValue the current value for the entry
    	 * @param newValue the value to replace the current value
    	 * @return a boolean corresponding to whether a replace occured
    	 */
    	boolean replace(final Object key, final int hash, final Object oldValue, final Object newValue) {
    		 p();
    		nullable<X10HashEntry> e = getFirst(hash);
    		while (e != null){
    			final nullable<X10HashEntry> fe = e;
    			final Object eKey = future (e){fe.Key}.force();
    			if (future(e){fe.Hash}.force() == hash && (future (key) {key.equals(eKey)}.force()))
    				break;
    			e = future(e){fe.Next}.force();
    		}
    		
    		final nullable<X10HashEntry> fe = e;
    		boolean replaced = false;

    		if (e != null){
        		final nullable<Object> currValue = future(e){fe.getValue()}.force();    			
    			if (future (oldValue) {oldValue.equals(currValue)}.force()) {
    				replaced = true;
    				finish async(e){fe.updateValue(newValue);}
    			}
    		}
    		v();
    		return replaced;
    	}
	
    	/** Replaces the value of entry corresponding to the key, and hash if it
    	 * is in this segment.
    	 * @param key the key for the entry
    	 * @param hash the hash code for the entry
    	 * @param newValue the value to replace the current value
    	 * @return the current value from the table which is replaced
    	 */
    	nullable<Object> replace(final Object key, final int hash, final Object newValue) {
    		 p();
    		nullable<X10HashEntry> e = getFirst(hash);
    		while (e != null){
    			final nullable<X10HashEntry> fe = e;
    			final Object eKey = future (e){fe.Key}.force();
    			if (future(e){fe.Hash}.force() == hash && (future(key){key.equals(eKey)}.force()))
    				break;
    			e = future(e){fe.Next}.force();
    		}
    		
    		final nullable<X10HashEntry> fe = e;
    		nullable<Object> oldValue = null;
    		if (e != null) {
    			oldValue = future(e){fe.getValue()}.force();
    			finish async(e) {fe.updateValue(newValue);}
    		}
    		v();
    		return oldValue;
    	}

    	/** Inserts a new entry corresponding to the key, value, and hash into this
    	 * segment. If the flag is set to true, insertion will only occur if there is
    	 * no other entry which corresponds to the key and hash code combination.
    	 * @param key the key for the entry
    	 * @param hash the hash code for the entry
    	 * @param value the value for the entry
    	 * @param onlyIfAbsent a flag dictating insertion only if no matching entry
    	 * @return the value from the table which was (potentially) replaced
    	 */
    	nullable<Object> put(final Object key, final int hash, final Object value, final boolean onlyIfAbsent) {
     		final point index = [hash & (size - 1)];
     		nullable<Object> oldValue = null;

     		p();

     		try{
    		final nullable<X10HashEntry> first = getFirst(hash);
    		nullable<X10HashEntry> e = first;
    		while (e != null){ 
    			final nullable<X10HashEntry> fe = e;
    			final Object eKey = future(e){fe.Key}.force();
    			if (future(e){fe.Hash}.force() == hash && (future(key){key.equals(eKey)}.force()))
    				break;
    			e = future(e){fe.Next}.force();
    		}
    		
    		final nullable<X10HashEntry> fe = e;
    		if (e != null) {
    			oldValue = future(e){fe.getValue()}.force();
    			if (!onlyIfAbsent)
    				finish async(e){fe.updateValue(value);}
    		}    		
    		else {
    			++modCount;
    			finish async (table.distribution[index]) {table[index] = new X10HashEntry(key, hash, value, first);}
    			count++; // write-volatile
    		}}
    		finally{v();}
    		return oldValue;
    	}

    	/** Removes the entry corresponding to the key, object, and hash if it
    	 * is in this segment.
    	 * @param key the key for the entry
    	 * @param hash the hash code for the entry
    	 * @param value the value for the entry
    	 * @return the (potentially) removed value
    	 */
    	nullable<Object> remove(final Object key,final int hash,final nullable<Object> value) {
    		final point index = [hash & (size - 1)];
    		p();
    		final int c = count - 1;
    		final nullable<X10HashEntry> first = getFirst(hash);
    		nullable<X10HashEntry> e = first;
    		while (e != null){ 
    			final nullable<X10HashEntry> fe = e;
    			final Object eKey = future(e){fe.Key}.force();
    			if (future(e){fe.Hash}.force() == hash && (future(key){key.equals(eKey)}.force()))
    				break;
    			e = future(e){fe.Next}.force();
    		}

    		nullable<Object> oldValue = null;
    		final nullable<X10HashEntry> fe = e;
    		if (e != null) {
    			final nullable<Object> v = future(e){fe.getValue()}.force();
    			if (value == null || future(value){value.equals(v)}.force()) {
    				oldValue = v;
    				// All entries following removed node can stay
    				// in list, but all preceding ones need to be
    				// cloned.
    				++modCount;
    				nullable<X10HashEntry> newFirst = future(e){fe.Next}.force();
    				for (nullable<X10HashEntry> p = first; p != e; ){
    					final nullable<X10HashEntry> newFirst2 = newFirst;
    					final nullable<X10HashEntry> fp = p;
    					newFirst = future(p)
    					{new X10HashEntry(fp.Key, fp.Hash,  
    							fp.vbox, newFirst2)}.force();
    					p = future(p){fp.Next}.force();
    				}
    				final nullable<X10HashEntry> f_newFirst = newFirst;
    				finish async (table.distribution[index]){ table[index] = f_newFirst; }
    				count = c; // write-volatile
    			}
    		}
    		v();
    		return oldValue;
    	}
      
    	void clear() {
            if (count != 0) {
            	p();
            	finish ateach ( point p : table )
            		table[p] = null;
            	++modCount;
            	count = 0; // write-volatile
            	v();
            }
        }

    }
    
    public ConcurrentX10HashMap(int initCapacity, dist D) {
    	int concurrencyLevel = D.region.size();
    	if (initCapacity < 0)
    		throw new IllegalArgumentException();
    	
    	int sshift = 0;
        int ssize = 1;
        while (ssize < concurrencyLevel) {
            ++sshift;
            ssize <<= 1;
        }
        SegmentShift = 32 - sshift;
    	SegmentMask = ssize - 1;
    	numSegments = concurrencyLevel;
    	this.Segments = new X10Segment[D];

    	if (initCapacity > MAXIMUM_CAPACITY)
    		initCapacity = MAXIMUM_CAPACITY;
    	int c = initCapacity / ssize;
    	if (c * ssize < initCapacity)
    		++c;
    	int cap = 1;
    	while (cap < c)
    		cap <<= 1;
    	final int capf = cap;
    	
    	final int value[.] seg_IDs = new int value[D](point[i]){return i;};
    	finish ateach ( point p : Segments )
    	    Segments[p] = new X10Segment(capf,seg_IDs[p]);
    	
    }

    public ConcurrentX10HashMap(int initcapacity, int concurrencyLevel){
   	this(initcapacity, [1:concurrencyLevel]->here);
    }

    public ConcurrentX10HashMap(dist D) {
    	this(DEFAULT_INITIAL_CAPACITY,D);
    }
    
    public ConcurrentX10HashMap() {
    	this(DEFAULT_INITIAL_CAPACITY,dist.factory.cyclic([1:DEFAULT_SEGMENTS]));
    }
    
    // inherit Map javadoc
    public boolean isEmpty() {
        final X10Segment[.] segments = this.Segments;
        /*
         * We keep track of per-segment modCounts to avoid ABA
         * problems in which an element in one segment was added and
         * in another removed during traversal, in which case the
         * table was never actually empty at any point. Note the
         * similar use of modCounts in the size() and containsValue()
         * methods, which are the only other methods also susceptible
         * to ABA problems.
         */
        future<int>[.] mcf = new future<int>[segments.distribution.region->here];
        int[.] mc = new int[segments.distribution.region->here];
        int mcsum = 0;
        future<boolean>[.] countzero = new future<boolean>[segments.distribution.region->here];
        for (point p : segments) {
        	countzero[p] = future (segments.distribution[p]) { segments[p].count != 0 };
        	mcf[p] = future (segments.distribution[p]) { segments[p].modCount };
        }
        for (point p : segments) {
            if (countzero[p].force())
                return false;
            else 
                mcsum += mc[p] = mcf[p].force();
        }
        // If mcsum is not zero, then get atomic access to the segments
        // and make sure that there is no modifications.
        if (mcsum != 0) {
            boolean isempty = true;
            finish ateach (point p : segments) 
            	segments[p].p();
            for (point p : segments)
                if (future(segments.distribution[p]){segments[p].count}.force() != 0 ||
                    mc[p] != future(segments.distribution[p]){segments[p].modCount}.force()) {
                    isempty = false;
                    break;
                }
            finish ateach (point p:segments)
                segments[p].v();
            return isempty;
        }
        return true;
    }
    
    
    public int size() {
        final X10Segment[.] segments = this.Segments;
        long sum = 0;
        long check = 0;
        future<long>[.] sumAsync = new future<long>[segments.distribution.region->here];
        future<long>[.] checkAsync = new future<long>[segments.distribution.region->here];
        future<int>[.] mcf = new future<int>[segments.distribution.region->here];
        int[.] mc = new int[segments.distribution.region->here];
        // Try a few times to get accurate count. On failure due to
        // continuous async changes in table, resort to locking.
        for (int k = 0; k < 2; ++k) {
            check = 0;
            sum = 0;
            int mcsum = 0;
            
            for (point p:segments){
            	sumAsync[p] = future (segments.distribution[p]) { segments[p].count };
            	mcf[p] = future (segments.distribution[p]) { segments[p].modCount };
            	checkAsync [p] = future (segments.distribution[p]) { segments[p].count };
            }
            for (point p:segments) {
                sum += sumAsync[p].force();
                mcsum += mc[p] = mcf[p].force();
            }
            if (mcsum != 0) {
                for (point p:segments) {
                    if (mc[p] != (future (segments.distribution[p]) { segments[p].modCount }).force()) {
                        check = -1; // force retry
                        break;
                    }
                    check += checkAsync[p].force();
                }
            }
            if (check == sum) 
                break;
        }
        if (check != sum) { // Resort to locking all segments
            sum = 0;
            finish ateach (point p:segments)
                segments[p].p();
            for (point p:segments) 
                sum += future(segments.distribution[p]){segments[p].count}.force();
            finish ateach (point p:segments)
                segments[p].v();
        }
        if (sum > java.lang.Integer.MAX_VALUE)
            return java.lang.Integer.MAX_VALUE;
        else
            return (int)sum;
    }
    
    /**
     * Returns the value to which the specified key is mapped in this table.
     *
     * @param   key   a key in the table.
     * @return  a future for the value to which the key is mapped;
     *          The future will be forced to <tt>null</tt> if the key is 
     *          not mapped to any value in this table.
     */
    public future<nullable<Object>> get(final Object key) {
        final int hash = hash(key); // throws NullPointerException if key null
        final X10Segment segmentfor = segmentFor(hash);
        return future (segmentfor) {segmentfor.get(key, hash)};
    }
    
    /**
     * Tests if the specified object is a key in this table.
     *
     * @param   key   possible key.
     * @return  A future whose forced value corresponds to
     *          <tt>true</tt> if and only if the specified object
     *          is a key in this table, as determined by the
     *          <tt>equals</tt> method; <tt>false</tt> otherwise.
     */
    public boolean containsKey(final Object key) {
        final int hash = hash(key); // throws NullPointerException if key null
        final X10Segment segmentfor = segmentFor(hash);
        return future(segmentfor){segmentfor.containsKey(key, hash)}.force();
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the
     * specified value. Note: This method requires a full internal
     * traversal of the hash table, and so is much slower than
     * method <tt>containsKey</tt>.
     *
     * @param value value whose presence in this map is to be tested.
     * @return <tt>true</tt> if this map maps one or more keys to the
     * specified value.
     */
    public boolean containsValue(final Object value) {
        
        // See explanation of modCount use above

        final X10Segment[.] segments = this.Segments;
        future<int>[.] mcf = new future<int>[segments.distribution.region->here];
        int[.] mc = new int[segments.distribution.region->here];
        future<boolean>[.] vals = new future<boolean>[segments.distribution.region->here];

        // Try a few times without locking
        for (int k = 0; k < 2; ++k) {
            int mcsum = 0;
            for (point p:segments) {
            	mcf[p] = future (segments.distribution[p]) { segments[p].modCount };
            	vals[p] = future (segments.distribution[p]) { segments[p].containsValue(value) };
            }
            for (point p:segments) {
                if (vals[p].force())
                    return true;
                mcsum += mc[p] = mcf[p].force();
            }
            boolean cleanSweep = true;
            if (mcsum != 0) {
                for (point p:segments) {
                    if (mc[p] != (future (segments.distribution[p]) { segments[p].modCount }).force()) {
                        cleanSweep = false;
                        break;
                    }
                }
            }
            if (cleanSweep)
                return false;
        }
        // Resort to locking all segments
        boolean found = false;
        
        finish ateach (point p:segments)
            segments[p].p();
        
        for (point p:segments) {
            if (future(segments.distribution[p]){segments[p].containsValue(value)}.force()) {
                    found = true;
                    break;
            }
        }
     
        finish ateach (point p:segments)
            segments[p].v();
        
        return found;
    }

    public boolean contains(Object value) {
        return containsValue(value);
    }

    /**
     * Maps the specified <tt>key</tt> to the specified
     * <tt>value</tt> in this table. Neither the key nor the
     * value can be <tt>null</tt>. 
     *
     * <p> The value can be retrieved by calling the <tt>get</tt> method
     * with a key that is equal to the original key and forcing the
     * evaluation of the resulting future.
     *
     * @param      key     the table key.
     * @param      value   the value.
     * @return     a future for the previous value of the specified key 
     *             in this table which will be forced to <tt>null</tt> 
     *             if it did not have one.
     */
    public future<nullable<Object>> put(final Object key, final Object value) {
        final int hash = hash(key);
        final X10Segment segmentfor = segmentFor(hash);
        return future (segmentfor) { segmentfor.put(key, hash, value, false) };
    }
    
    /**
     * If the specified key is not already associated
     * with a value, associate it with the given value.
     * 
     * @param key key with which the specified value is to be associated.
     * @param value value to be associated with the specified key.
     * @return future for previous value associated with specified key.
     */
    public future<nullable<Object>> putIfAbsent(final Object key, final Object value) {
        final int hash = hash(key);
        final X10Segment segmentfor = segmentFor(hash);
        return future (segmentfor) { segmentfor.put(key, hash, value, true) };
    }

    /**
     * Copies all of the mappings from the specified map to this one.
     *
     * These mappings replace any mappings that this map had for any of the
     * keys currently in the specified Map.
     *
     * @param t Mappings to be stored in this map.
     */
     
    public void putAll(final ConcurrentX10HashMap t) {
        finish for (Iterator it = t.entrySet().iterator(); it.hasNext(); ) {
            final Map.Entry e = (Map.Entry) it.next();
            final Object eKey = e.getKey();
            final nullable<Object> eValue = e.getValue();
            // I thought I could make this async, but cannot be done due to race conditions 
            if (eValue != null) async {
               nullable<Object> foo = put(eKey, (Object)eValue).force();}
        }
    }
 
    /**
     * Removes the key (and its corresponding value) from this
     * table. This method does nothing if the key is not in the table.
     *
     * @param   key   the key that needs to be removed.
     * @return  a future for the value to which the key had been mapped in this table,
     *          or <tt>null</tt> if the key did not have a mapping.
     */
    public future<nullable<Object>> remove(final Object key) {
        final int hash = hash(key);
        final X10Segment segmentfor = segmentFor(hash);
        return future (segmentfor) { segmentfor.remove(key, hash, null) };
    }

    /**
     * Remove entry for key only if currently mapped to given value.
     *
     * @param key key with which the specified value is associated.
     * @param value value associated with the specified key.
     * @return true if the value was removed
     */
    public future<boolean> remove(final Object key,final nullable<Object> value) {
        final int hash = hash(key);
        final X10Segment segmentfor = segmentFor(hash);
        return future (segmentfor) { segmentfor.remove(key, hash, value) != null };
    }
    
    /**
     * Replace entry for key only if currently mapped to given value.
     * @param key key with which the specified value is associated.
     * @param oldValue value expected to be associated with the specified key.
     * @param newValue value to be associated with the specified key.
     * @return true if the value was replaced
     */
    public future<boolean> replace(final Object key, final Object oldValue, final Object newValue) {
        final int hash = hash(key);
        final X10Segment segmentfor = segmentFor(hash);
        return future (segmentfor) { segmentfor.replace(key, hash, oldValue, newValue) };
    }

    /**
     * Replace entry for key only if currently mapped to some value.
     * @param key key with which the specified value is associated.
     * @param value value to be associated with the specified key.
     * @return future for previous value associated with specified key, or <tt>null</tt>
     *         if there was no mapping for key.  
     */
    public future<nullable<Object>> replace(final Object key, final Object value) {
        final int hash = hash(key);
        final X10Segment segmentfor = segmentFor(hash);
        return future (segmentfor) { segmentfor.replace(key, hash, value) };
    }

    /**
     * Removes all mappings from this map.
     */
    public void clear() {
        finish ateach (point p:Segments) {
            Segments[p].clear();
        }
    }

/********** Stuff to be implemented after the Iterator setion ******/

    /**
     * Returns a set view of the keys contained in this map.  The set is
     * backed by the map, so changes to the map are reflected in the set, and
     * vice-versa.  The set supports element removal, which removes the
     * corresponding mapping from this map, via the <tt>Iterator.remove</tt>,
     * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt>, and
     * <tt>clear</tt> operations.  It does not support the <tt>add</tt> or
     * <tt>addAll</tt> operations.
     * The view's returned <tt>iterator</tt> is a "weakly consistent" iterator that
     * will never throw {@link java.util.ConcurrentModificationException},
     * and guarantees to traverse elements as they existed upon
     * construction of the iterator, and may (but is not guaranteed to)
     * reflect any modifications subsequent to construction.
     *
     * @return a set view of the keys contained in this map.
     */
    public Set keySet() {
        nullable<Set> ks = keySet;
        return (ks != null) ? (Set) ks : (keySet = new KeySet());
    }


    /**
     * Returns a collection view of the values contained in this map.  The
     * collection is backed by the map, so changes to the map are reflected in
     * the collection, and vice-versa.  The collection supports element
     * removal, which removes the corresponding mapping from this map, via the
     * <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt> operations.
     * It does not support the <tt>add</tt> or <tt>addAll</tt> operations.
     * The view's returned <tt>iterator</tt> is a "weakly consistent" iterator that
     * will never throw {@link java.util.ConcurrentModificationException},
     * and guarantees to traverse elements as they existed upon
     * construction of the iterator, and may (but is not guaranteed to)
     * reflect any modifications subsequent to construction.
     *
     * @return a collection view of the values contained in this map.
     */
    public Collection values() {
        nullable<Collection> vs = values;
        return (vs != null) ? (Collection) vs : (Collection) (values = new Values());
    }


    /**
     * Returns a collection view of the mappings contained in this map.  Each
     * element in the returned collection is a <tt>Map.Entry</tt>.  The
     * collection is backed by the map, so changes to the map are reflected in
     * the collection, and vice-versa.  The collection supports element
     * removal, which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt> operations.
     * It does not support the <tt>add</tt> or <tt>addAll</tt> operations.
     * The view's returned <tt>iterator</tt> is a "weakly consistent" iterator that
     * will never throw {@link java.util.ConcurrentModificationException},
     * and guarantees to traverse elements as they existed upon
     * construction of the iterator, and may (but is not guaranteed to)
     * reflect any modifications subsequent to construction.
     *
     * @return a collection view of the mappings contained in this map.
     */
public Set entrySet() {
        nullable<Set> es = entrySet;
        return (es != null) ? (Set) es : (Set) (entrySet = new EntrySet());
    }


    /**
     * Returns an enumeration of the keys in this table.
     *
     * @return  an enumeration of the keys in this table.
     * @see     #keySet
     */
    public Enumeration keys() {
        return new KeyIterator();
    }

    /**
     * Returns an enumeration of the values in this table.
     *
     * @return  an enumeration of the values in this table.
     * @see     #values
     */
   public Enumeration elements() {
        return new ValueIterator();
    }


    /* ---------------- Iterator Support -------------- */

    abstract class HashIterator extends x10.lang.Object {
        int nextSegmentIndex;
        int nextTableIndex;
        nullable<X10HashEntry>[.] currentTable;
        nullable<X10HashEntry> nextEntry;
        nullable<X10HashEntry> lastReturned;
        future<boolean> advancedEntry;

        HashIterator() {
            nextSegmentIndex = numSegments;
            nextTableIndex = -1;
            advancedEntry = future { advance() };
        }

        public boolean hasMoreElements() { return hasNext(); }

        final boolean advance() {
            if (nextEntry != null && (nextEntry = nextEntry.Next) != null)
                return nextEntry != null;

            while (nextTableIndex >= 0) {
            	final int fnextTableIndex = nextTableIndex--;
                if ( (nextEntry = future(currentTable.distribution[fnextTableIndex]){currentTable[fnextTableIndex]}.force()) != null)
                    return nextEntry != null;
            }

            while (nextSegmentIndex >= 1) {
            	final int fnextSegmentIndex = nextSegmentIndex--;
                final X10Segment seg = future(Segments.distribution[fnextSegmentIndex]){Segments[fnextSegmentIndex]}.force();
                if (future(seg){seg.count != 0}.force()) {
                    currentTable = future(seg){seg.table}.force();
                    for (int j = future(seg){seg.size-1}.force(); j >= 0; --j) {
                    	final int fj = j;
                        if ( (nextEntry = future(currentTable.distribution[fj]){currentTable[fj]}.force()) != null) {
                            nextTableIndex = j - 1;
                            return nextEntry != null;
                        }
                    }
                }
            }
            
            return nextEntry != null;
        }

        public boolean hasNext() { return advancedEntry.force(); }

        nullable<X10HashEntry> nextEntry() {
            if (!advancedEntry.force())
                throw new NoSuchElementException();
            lastReturned = nextEntry;
            advancedEntry = future { advance() };
            return lastReturned;
        }

        public void remove() {
            if (lastReturned == null)
                throw new IllegalStateException();
            ConcurrentX10HashMap.this.remove(lastReturned.Key);
            lastReturned = null;
        }
    }

    final class KeyIterator extends HashIterator implements Iterator, Enumeration {
        public nullable<Object> next() { return super.nextEntry().Key; }
        public nullable<Object> nextElement() { return next(); }
    }

    final class ValueIterator extends HashIterator implements Iterator, Enumeration {
        public nullable<Object> next() { return super.nextEntry().getValue(); }
        public nullable<Object> nextElement() { return next(); }
    }

    final class EntryIterator extends HashIterator implements Map.Entry, Iterator {
        public nullable<Object> next() {
            nextEntry();
            return this;
        }

        public Object getKey() {
            if (lastReturned == null)
                throw new IllegalStateException("Entry was removed");
           return lastReturned.Key;
        }

        public nullable<Object> getValue() {
            if (lastReturned == null)
                throw new IllegalStateException("Entry was removed");
            return ConcurrentX10HashMap.this.get(lastReturned.Key).force();
        }

        public nullable<Object> setValue(nullable<Object> value) {
            if (lastReturned == null)
                throw new IllegalStateException("Entry was removed");
            if (value == null)
            	throw new UnsupportedOperationException();
            return ConcurrentX10HashMap.this.put(lastReturned.Key, (Object)value).force();
        }

        public boolean equals(Object o) {
            // If not acting as entry, just use default.
            if (lastReturned == null)
                return super.equals(o);
            if (!(o instanceof Map.Entry))
                return false;
            final Map.Entry e = (Map.Entry)o;
            return eq(getKey(), future(o){e.getKey()}.force()) && eq(getValue(), future(o){e.getValue()}.force());
        }

        public int hashCode() {
            // If not acting as entry, just use default.
            if (lastReturned == null)
                return super.hashCode();

            final nullable<Object> k = getKey();
            final nullable<Object> v = getValue();
            return ((k == null) ? 0 : future(k){k.hashCode()}.force()) ^
                   ((v == null) ? 0 : future(v){v.hashCode()}.force());
        }

        public String toString() {
            /* Removed until better support for Strings in X10
            // If not acting as entry, just use default.
            if (lastReturned == null)
                return super.toString();
            else {
            	final Object k = getKey();
            	String keyString = future(k){k.toString()}.force();
            	nullable<Object> v = getValue();
            	String valueString;
            	if (v == null) 
            		valueString = "NULL";
            	else {
            		final Object v2 = (Object)v;
            		valueString = future(v2){v2.toString()}.force();
            	}
                return keyString + "=" + valueString;
            }
            */
            return "";
        }

        boolean eq(final nullable<Object> o1, final nullable<Object> o2) {
            return (o1 == null ? o2 == null : future(o1){o1.equals(o2)}.force());
        }

    }

 final class KeySet extends AbstractSet {
        public Iterator iterator() {
            return new KeyIterator();
        }
        public int size() {
            return ConcurrentX10HashMap.this.size();
        }
        public boolean contains(Object o) {
            return ConcurrentX10HashMap.this.containsKey(o);
        }
        public boolean remove(Object o) {
            return ConcurrentX10HashMap.this.remove(o).force() != null;
        }
        public void clear() {
            ConcurrentX10HashMap.this.clear();
        }
        public nullable<Object>[.] toArray() {
            Collection c = new ArrayList();
            for (Iterator i = iterator(); i.hasNext(); )
                c.add((Object) i.next());
            return c.toArray();
        }
        public nullable<Object>[.] toArray(nullable<Object>[.] a) {
            Collection c = new ArrayList();
            for (Iterator i = iterator(); i.hasNext(); )
                c.add(i.next());
            return c.toArray(a);
        }
    }
 
 final class Values extends AbstractCollection {
     public Iterator iterator() {
         return new ValueIterator();
     }
     public int size() {
         return ConcurrentX10HashMap.this.size();
     }
     public boolean contains(Object o) {
         return ConcurrentX10HashMap.this.containsValue(o);
     }
     public void clear() {
         ConcurrentX10HashMap.this.clear();
     }
     public nullable<Object>[.] toArray() {
         Collection c = new ArrayList();
         for (Iterator i = iterator(); i.hasNext(); )
             c.add(i.next());
         return c.toArray();
     }
     public nullable<Object>[.] toArray(nullable<Object>[.] a) {
         Collection c = new ArrayList();
         for (Iterator i = iterator(); i.hasNext(); )
             c.add(i.next());
         return c.toArray(a);
     }
     public boolean equals(nullable<Object> o){
    	 return (o != null) && (o == this);
     }
 }

 final class EntrySet extends AbstractSet {
     public Iterator iterator() {
         return new EntryIterator();
     }
     public boolean contains(Object o) {
         if (!(o instanceof Map.Entry))
             return false;
         final Map.Entry e = (Map.Entry)o;
         final nullable<Object> v = ConcurrentX10HashMap.this.get(future(o){e.getKey()}.force());
         final nullable<Object> eValue = future(o){e.getValue()}.force();
         return v != null && future(v){v.equals(eValue)}.force();
     }
     public boolean remove(Object o) {
         if (!(o instanceof Map.Entry))
             return false;
         final Map.Entry e = (Map.Entry)o;
         return ConcurrentX10HashMap.this.remove(future(o){e.getKey()}.force(), future(o){e.getValue()}.force()).force();
     }
     public int size() {
         return ConcurrentX10HashMap.this.size();
     }
     public void clear() {
         ConcurrentX10HashMap.this.clear();
     }
     public nullable<Object>[.] toArray() {
         // Since we don't ordinarily have distinct Entry objects, we
         // must pack elements using exportable SimpleEntry
         Collection c = new ArrayList(size());
         for (Iterator i = iterator(); i.hasNext(); )
             c.add(new SimpleEntry(i.next()));
         return c.toArray();
     }
     public nullable<Object>[.] toArray(nullable<Object>[.] a) {
         Collection c = new ArrayList(size());
         for (Iterator i = iterator(); i.hasNext(); )
             c.add(new SimpleEntry(i.next()));
         return c.toArray(a);
     }

 }
 /**
  * This duplicates java.util.AbstractMap.SimpleEntry until this class
  * is made accessible.
  */
 static final class SimpleEntry extends x10.lang.Object implements Map.Entry {
     Object key;
     nullable<Object> value;

     public SimpleEntry(Object key, nullable<Object> value) {
         this.key   = key;
         this.value = value;
     }

     public SimpleEntry(final nullable<Object> e) {
    	 if(!(e instanceof Map.Entry))
    		 throw new IllegalArgumentException();
         this.key   = future(e){((Map.Entry)e).getKey()}.force();
         this.value = future(e){((Map.Entry)e).getValue()}.force();
     }

     public Object getKey() {
         return key;
     }

     public nullable<Object> getValue() {
         return value;
     }

     public nullable<Object> setValue(nullable<Object> value) {
         nullable<Object> oldValue = this.value;
         this.value = value;
         return oldValue;
     }

     public boolean equals(Object o) {
         if (!(o instanceof Map.Entry))
             return false;
         final Map.Entry e = (Map.Entry)o;
         return eq(key, future(o){e.getKey()}.force()) && eq(value, future(o){e.getValue()}.force());
     }

     public int hashCode() {
    	 final Object fkey = key;
    	 final nullable<Object> fvalue = value;
         return future(key){fkey.hashCode()}.force() ^
                ((value == null)?0:future(value){((Object)fvalue).hashCode()}.force());
     }

     public String toString() {
    	 /* Removed until there is X10 support for Strings
	final Object k = key;
     	String keyString = future(k){k.toString()}.force();
     	nullable<Object> v = value;
     	String valueString;
     	if (v == null) 
     		valueString = "NULL";
     	else {
     		final Object v2 = (Object)v;
     		valueString = future(v2){v2.toString()}.force();
     	}
         return keyString + "=" + valueString;
         */
         return "";
     }

     static boolean eq(final nullable<Object> o1, final nullable<Object> o2) {
         return (o1 == null ? o2 == null : future(o1){o1.equals(o2)}.force());
     }
 }

}
