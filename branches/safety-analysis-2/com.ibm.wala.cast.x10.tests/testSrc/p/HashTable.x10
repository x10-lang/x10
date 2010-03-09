
package p;

public value HashTable {
  region R = [1:1000];
  dist D =  dist.factory.block(R);

  nullable<Bucket> [.] buckets = new nullable<Bucket> [D] (point p) { return null; } ;

  HashTable() {
    // ateach is not supported in CASTX10 yet, so using array initializer syntax instead
    
    //ateach (point p : buckets.distribution) {
    //   buckets[p] = null;
    //}
  }
  
  public static void main(String[] args) {
    	new HashTable().put(new K(), new V());
  }

  public void put(final K key, final V value) {
    final int hash = key.hashCode()% 1000; // need R.max etc.
    async (D[hash]) {
      nullable<Bucket> b = buckets[hash];
      while (b != null) {
        if (b.k.equals(key)) {
           /* atomic */ { b.v = value; }
           return; // from the async
        }
        b = b.next;
      }
      /* atomic */ { buckets[hash] = new Bucket(key, value, buckets[hash]); }
    };
  }
}

class K {}

class V {}

class Bucket {
    final K k;
    V v;
    nullable<Bucket> next;

    Bucket(K k, V v, nullable<Bucket> next) { 
      this.k = k; 
      this.v = v; 
      this.next = next; 
    }
}
