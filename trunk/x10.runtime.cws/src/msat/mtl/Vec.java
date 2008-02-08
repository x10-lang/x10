package msat.mtl;
/*******************************************************************************************[Vec.h]
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


/**
 * Automatically resizable arrays.
 */


public class Vec<T> {
    T[]  data;
    int sz;
    int cap;

    //void     init(int size, T pad);
    void     grow(int min_cap) {
    	 if (min_cap <= cap) return;
    	    if (cap == 0) cap = (min_cap >= 2) ? min_cap : 2;
    	    else          do cap = (cap*3+1) >> 1; while (cap < min_cap);
    	    T[] newData= (T[]) new Object[cap];
    	    System.arraycopy(data, 0, newData, 0, data.length);
    	    data = newData;
    	    
    }

   /* // Don't allow copying (error prone):
    Vec<T>  operator = (Vec<T> other) { 
    	assert false;
    	
    }*/
    Vec        (Vec<T> other) { 
    	assert false;
    }

    static  int imin(int x, int y) {
        int mask = (x-y) >> (/*sizeof(int)*/1*8-1);
        return (x&mask) + (y&(~mask)); }

    static int imax(int x, int y) {
        int mask = (y-x) >> (/*sizeof(int)*/1*8-1);
        return (x&mask) + (y&(~mask)); }

    // Constructors:
    public Vec()    { data=null; sz=0; cap=0;   }
    public Vec(int size)   { data = null; sz=0; cap=0; growTo(size); }
    public Vec(int size, T pad) { data=null; sz=0; cap=0; growTo(size, pad); }
    public Vec(T[] array, int size)     {
    	data=array; sz=size; cap=size; 
    	// (takes ownership of array -- will be deallocated with 'free()')
    }
   public void finalize()                                                    { clear(true); }

    // Ownership of underlying array:
    public T[]      release  ()           { T[] ret = data; data = null; sz = 0; cap = 0; return ret; }
   // operator T*       ()           { return data; }     // (unsafe but convenient)
  

    // Size operations:
    public int      size   ()       { return sz; }
    public void     shrink (int nelems)       { 
    	assert(nelems <= sz); 
    	for (int i = 0; i < nelems; i++) {
    		sz--; data[sz]=null;
    	}
    }
    public void     shrink_(int nelems)       { assert(nelems <= sz); sz -= nelems; }
    public void     pop    ()             { sz--; data[sz]=null; }
    public void     growTo (int size) {
    	   if (sz >= size) return;
    	    grow(size);
    	    //for (int i = sz; i < size; i++) new (&data[i]) T();
    	    sz = size; 
    }
    public void     growTo (int size, T pad) {
    	if (sz >= size) return;
    	grow(size);
    	//for (int i = sz; i < size; i++)  		new (&data[i]) T(pad);
    	sz = size; 
    }

    public void     clear  (boolean dealloc ) {
    	 if (data != null){
    	        for (int i = 0; i < sz; i++) data[i]=null;
    	        sz = 0;
    	        if (dealloc) {  data = null; cap = 0; } 
    	 }
    }
    public void clear() { clear(false);}
    public void     capacity (int size) { grow(size); }

    // Stack interface:

    public void     push  () { 
    	if (sz == cap) { 
    		cap = imax(2, (cap*3+1)>>1); 
    		T[] newData = (T[]) new Object[cap];
    		System.arraycopy(data, 0, newData, 0, data.length);
    		data = newData;
    	} 
    	//new (&data[sz]) T(); 
    	sz++; 
    }
    //void     push  (const T& elem)     { if (sz == cap) { cap = imax(2, (cap*3+1)>>1); data = (T*)realloc(data, cap * sizeof(T)); } new (&data[sz]) T(elem); sz++; }
    public void     push  (T elem)     { 
    	if (sz == cap) { 
    		cap = imax(2, (cap*3+1)>>1); 
    		T[] newData = (T[]) new Object[cap];
    		System.arraycopy(data, 0, newData, 0, data.length);
    		data = newData;
    		//data = realloc(data, cap * 1 /*sizeof(T)*/); 
    	} 
    	data[sz++] = elem; 
    }
    public void     push_ (T elem)  { assert(sz < cap); data[sz++] = elem; }


    public T last  () { return data[sz-1]; }

    // Vector interface:
    public T get(int index) { 
    	return data[index];
    }
    public void set(int index, T t) { data[index]=t;}
    
    // Duplicatation (preferred instead):
    public void copyTo(Vec<T> copy)  { 
    	copy.clear(); 
    	copy.growTo(sz); 
    	for (int i = 0; i < sz; i++) 
    		data[i] = copy.data[i];
    		//new (&copy[i]) T(data[i]); 
    	}
    public void moveTo(Vec<T> dest) { 
    	dest.clear(true); 
    	dest.data = data; 
    	dest.sz = sz; 
    	dest.cap = cap; 
    	data = null; 
    	sz = 0; 
    	cap = 0; 
    }
}
