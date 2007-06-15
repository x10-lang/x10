
package testSrc;

//import testSrc.K;
//import testSrc.V;

public value HashTable {
  class K {}
  class V {}
  region R = [1:1000];
  dist D = dist.factory.block(R);

  class Bucket {
    final K k;
    V v;
    nullable<Bucket> next;
    
    Bucket(K k, V v) { this(k, v, null); } 

    Bucket(K k, V v, nullable<Bucket> next) { 
      this.k = k; 
      this.v = v; 
      this.next = next; 
    }
  }

  nullable<Bucket> [.] buckets = new nullable<Bucket> [D];

  HashTable() {
    ateach (point p : buckets.distribution) {
       buckets[p] = null;
    }
  }

  public void put(K key, V value) {
    int hash = key.hashCode()% 1000; // need R.max etc.
    async (D[hash]) {
      nullable<Bucket> b = buckets[hash];
      while (b != null) {
        if (b.k.equals(key)) {
           atomic { b.v = value; }
           return; // from the async
        }
        b = b.next;
      }
      atomic {
        buckets[hash] = 
          new Bucket(key, value, buckets[hash]);
      }
    };
  }
}