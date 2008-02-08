package msat.mtl;
/*******************************************************************************************[Map.h]
MiniSat -- Copyright (c) 2003-2006, Niklas Een, Niklas Sorensson

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or
substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT
OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
**************************************************************************************************/

//=================================================================================================
// Default hash/equals functions
//

//template<class K> struct Hash  { uint32_t operator()(const K& k)               const { return hash(k);  } };
//template<class K> struct Equal { bool     operator()(const K& k1, const K& k2) const { return k1 == k2; } };

//template<class K> struct DeepHash  { int operator()(const K* k)               const { return hash(*k);  } };
//template<class K> struct DeepEqual { bool     operator()(const K* k1, const K* k2) const { return *k1 == *k2; } };

//=================================================================================================
// Some primes
//


//=================================================================================================
// Hash table implementation of Maps
//

//template<class K, class D, class H = Hash<K>, class E = Equal<K> >
class Map <K,D>{
	static  final int nprimes          = 25;
	static final int primes [] = { 31, 73, 151, 313, 643, 1291, 2593, 5233, 10501, 21013, 42073, 84181, 168451, 337219, 674701, 1349473, 2699299, 5398891, 10798093, 21596719, 43193641, 86387383, 172775299, 345550609, 691101253 };

    class Pair { K key; D data; };
    class Hash { int apply(K k) { return k.hashCode();}}
    class Equal { boolean apply(K k1, K k2) { return k1==k2;}}
    Hash         hash;
    Equal         equals;

    Vec<Pair>[] table;
    int        cap;
    int        size;

    // Don't allow copying (error prone):
   // Map<K,D>&  operator = (Map<K,D,H,E>& other) { assert(0); }
     //              Map        (Map<K,D,H,E>& other) { assert(0); }

    int index  (K k)  { return hash.apply(k) % cap; }
    void   _insert (K k, D d) { 
    	table[index(k)].push(); 
    	table[index(k)].last().key = k; 
    	table[index(k)].last().data = d; 
    }
    void    rehash () {
        final Vec<Pair>[] old = table;
        int newsize = primes[0];
        for (int i = 1; newsize <= cap && i < nprimes; i++)
           newsize = primes[i];
        table =  new Vec[newsize];
        for (int i = 0; i < cap; i++){
            for (int j = 0; j < old[i].size(); j++){
                _insert(old[i].get(j).key, old[i].get(j).data); }}
        //delete [] old;
        cap = newsize;
    }

    
    public Map () { table=null; cap=0; size=0;}
     Map (Hash h, Equal e){ 
    	 this(); hash=h; equals=e;
     }
   // ~Map () { delete [] table; }

    public void insert (K k, D d) { if (size+1 > cap / 2) rehash(); _insert(k, d); size++; }
    boolean peek   (K k, D d) {
        if (size == 0) return false;
        Vec<Pair> ps = table[index(k)];
        for (int i = 0; i < ps.size(); i++)
            if (ps.get(i).key== k){
                d = ps.get(i).data;
                return true; } 
        return false;
    }

    public void remove (final K k) {
        assert(table != null);
        Vec<Pair> ps = table[index(k)];
        int j = 0;
        for (; j < ps.size() && ps.get(j).key != k; j++);
        assert(j < ps.size());
        ps.set(j,ps.last());
        ps.pop();
    }

    public void clear  () {
        cap = size = 0;
        //delete [] table;
        table = null;
    }
}
