package msat.mtl;
/******************************************************************************************[Heap.h]
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
// A heap implementation with support for decrease/increase key.


//template<class Comp>
public class Heap {
	public static interface Comp {
		public boolean apply(int x, int y);
	}
    Comp     lt;
    VecInt heap;     // heap of ints
    VecInt indices;  // int -> index in heap

    // Index "traversal" functions
    static int left  (int i) { return i*2+1; }
    static int right (int i) { return (i+1)*2; }
    static  int parent(int i) { return (i-1) >> 1; }


    void percolateUp(int i)
    {
        int x = heap.get(i);
        while (i != 0 && lt.apply(x, heap.get(parent(i)))){
            heap.set(i,         heap.get(parent(i)));
            indices.set(heap.get(i),i);
            i                = parent(i);
        }
        heap.set(i,x);
        indices.set(x,i);
    }


    void percolateDown(int i)
    {
        int x = heap.get(i);
        while (left(i) < heap.size()){
            int child = right(i) < heap.size() && 
            lt.apply(heap.get(right(i)), heap.get(left(i))) ? right(i) : left(i);
            if (!lt.apply(heap.get(child), x)) break;
            heap.set(i, heap.get(child));
            indices.set(heap.get(i), i);
            i                = child;
        }
        heap.set(i,x);
        indices.set(x,i);
    }


    boolean heapProperty (int i)  {
        return i >= heap.size()
            || ((i == 0 || 
            		!lt.apply(heap.get(i), heap.get(parent(i)))) && heapProperty(left(i)) && heapProperty(right(i))); }

   public Heap(Comp c) { lt =c;}

   public int  size      ()           { return heap.size(); }
   public  boolean empty     ()          { return heap.size() == 0; }
   public boolean inHeap    (int n)      { return n < indices.size() && indices.get(n) >= 0; }
   public int get(int index) { assert(index < heap.size()); return heap.get(index); }

   public void decrease  (int n) { 
	   assert(inHeap(n)); 
	   percolateUp(indices.get(n)); }

    // RENAME WHEN THE DEPRECATED INCREASE IS REMOVED.
   public void increase_ (int n) { assert(inHeap(n)); percolateDown(indices.get(n)); }


   public void insert(int n)
    {
        indices.growTo(n+1, -1);
        assert(!inHeap(n));

        indices.set(n,heap.size());
        heap.push(n);
        percolateUp(indices.get(n)); 
    }


   public  int  removeMin()
    {
        int x            = heap.get(0);
        heap.set(0, heap.last());
        indices.set(heap.get(0), 0);
        indices.set(x, -1);
        heap.pop();
        if (heap.size() > 1) percolateDown(0);
        return x; 
    }

   public void clear() { clear(false);}
   public void clear(boolean dealloc) 
    { 
        for (int i = 0; i < heap.size(); i++)
            indices.set(heap.get(i),-1);
//#ifdef NDEBUG
//        for (int i = 0; i < indices.size(); i++)
//            assert(indices[i] == -1);
//#endif
        heap.clear(dealloc); 
    }


    // Fool proof variant of insert/decrease/increase
   public void update (int n)
    {
        if (!inHeap(n))
            insert(n);
        else {
            percolateUp(indices.get(n));
            percolateDown(indices.get(n));
        }
    }

/*
    // Delete elements from the heap using a given filter function (-object).
    // *** this could probaly be replaced with a more general "buildHeap(vec<int>&)" method ***
   // template <class F>
    public <F> void filter(F filt) {
        int i,j;
        for (i = j = 0; i < heap.size(); i++)
            if (filt(heap.get(i))){
                heap.set(j,heap.get(i));
                indices.set(heap.get(i), j++);
            }else
                indices.set(heap.get(i), -1);

        heap.shrink(i - j);
        for ( i = heap.size() / 2 - 1; i >= 0; i--)
            percolateDown(i);

        assert(heapProperty());
    }

*/
    // DEBUG: consistency checking
    public boolean heapProperty() {
        return heapProperty(1); }


    // COMPAT: should be removed
    public void setBounds (int n) { }
    public void increase  (int n) { decrease(n); }
    public int  getmin    ()      { return removeMin(); }

}

