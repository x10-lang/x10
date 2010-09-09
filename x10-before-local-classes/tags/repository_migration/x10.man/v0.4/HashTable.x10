/** A distributed hash table.

    @author roca, vj

   7/22 (vj) Should get/put take in and return ^K and ^Object?
 */
package x10.examples;

public value Hashtable { 
    class Bucket {
	Object k;
	Object v;
	nullable Bucket next;
	Bucket(Object k, Object v) { 
	    this(k, v, null);
	} 
	Bucket(Object k, Object v, nullable Bucket next) { 
	    this.k = k; 
	    this.v = v; 
	    this.next = next; 
	}
    }
    distribution D;
    public Hashtable( distribution D) {
        this.D = D;
    }
    Bucket[D]  buckets = new Bucket@current[D];

    public atomic
	void put(Object key, Object value) {
        // compute the hashcode locally.
        int hash = key.hashCode()% D.size;

	// jump to the place containing the bucket
        // and work on the bucket.
        async (D[hash]) {
            for (Bucket@here b = buckets[hash]; b != null; b = b.next) {
                if (b.k.equals(key)) {
		    atomic {
			b.v = value;
		    }
                    return; // from the async
                }
            }
	    atomic {
		buckets[hash] 
                  = new Bucket(key, value, buckets[hash]);
	    }
        };
    }

    public 
	future<Object> get(Object key) {
        int hash = key.hashCode()% D.size;
        return future (D[hash]) {
            for (Bucket@here b = buckets[hash]; b != null; b = b.next) {
                if (b.k.equals(key)) {
                    return b.v;
                }
            }
	    // couldnt find it. send a default value for the given type.
            return new Object();
        }
    }

    public
	int multihashTest() {
	distribution D = [1...1000] block 10;
	_ table = new Hashtable<D, int, int>;
	for (int i : 0.. 1000) 
	    async (?) { 
	    for (int j : 0..1000) table.put(i*j, i);
	}
	return table.get(2500);
    }
}
