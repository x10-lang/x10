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

public class VecChar {
    char[]  data;
    int sz;
    int cap;

    //void     init(int size, T pad);
    void     grow(int min_cap) {
    	 if (min_cap <= cap) return;
    	    if (cap == 0) cap = (min_cap >= 2) ? min_cap : 2;
    	    else          do cap = (cap*3+1) >> 1; while (cap < min_cap);
    	    char[] newData=  new char[cap];
    	    System.arraycopy(data, 0, newData, 0, data.length);
    	    data = newData;
    	    
    }

   /* // Don't allow copying (error prone):
    Vec<T>  operator = (Vec<T> other) { 
    	assert false;
    	
    }*/
    VecChar        (Object other) { 
    	assert false;
    }

    static  int imin(int x, int y) {
        int mask = (x-y) >> (/*sizeof(int)*/1*8-1);
        return (x&mask) + (y&(~mask)); }

    static int imax(int x, int y) {
        int mask = (y-x) >> (/*sizeof(int)*/1*8-1);
        return (x&mask) + (y&(~mask)); }

    // Constructors:
    public VecChar()    { data=null; sz=0; cap=0;   }
    public VecChar(int size)   { data = null; sz=0; cap=0; growTo(size); }
    public VecChar(int size, char pad) { data=null; sz=0; cap=0; growTo(size, pad); }
    public VecChar(char[] array, int size)     {
    	data=array; sz=size; cap=size; 
    	// (takes ownership of array -- will be deallocated with 'free()')
    }
   public void finalize()                                                    { clear(true); }

    // Ownership of underlying array:
    public char[]      release  ()           { char[] ret = data; data = null; sz = 0; cap = 0; return ret; }
   // operator T*       ()           { return data; }     // (unsafe but convenient)
  

    // Size operations:
    public int      size   ()       { return sz; }
    public void     shrink (int nelems)       { 
    	assert(nelems <= sz); 
    	for (int i = 0; i < nelems; i++) {
    		sz--; data[sz]=0;
    	}
    }
    public void     shrink_(int nelems)       { assert(nelems <= sz); sz -= nelems; }
    public void     pop    ()             { sz--; data[sz]=0; }
    public void     growTo (int size) {
    	   if (sz >= size) return;
    	    grow(size);
    	    sz = size; 
    }
    public void     growTo (int size, char pad) {
    	if (sz >= size) return;
    	grow(size);
    	for (int i = sz; i < size; i++) data[i] = pad;
    	sz = size; 
    }

    public void     clear  (boolean dealloc ) {
    	 if (data != null){
    	        for (int i = 0; i < sz; i++) data[i]=0;
    	        sz = 0;
    	        if (dealloc) { data = null; cap = 0; } 
    	 }
    }
    public void clear() { clear(false);}
    public void     capacity (int size) { grow(size); }

    // Stack interface:

    public void     push  () { 
    	if (sz == cap) { 
    		cap = imax(2, (cap*3+1)>>1); 
    		char[] newData = new char[cap];
    		System.arraycopy(data, 0, newData, 0, data.length);
    		data = newData;
    	} 
    	sz++; 
    }
    //void     push  (const T& elem)     { if (sz == cap) { cap = imax(2, (cap*3+1)>>1); data = (T*)realloc(data, cap * sizeof(T)); } new (&data[sz]) T(elem); sz++; }
    public void     push  (char elem)     { 
    	if (sz == cap) { 
    		cap = imax(2, (cap*3+1)>>1); 
    		char[] newData = new char[cap];
    		System.arraycopy(data, 0, newData, 0, data.length);
    		data = newData;
    	} 
    	data[sz++] = elem; 
    }
    public void     push_ (char elem)  { assert(sz < cap); data[sz++] = elem; }


    public int last  () { return data[sz-1]; }

    // Vector interface:
    public int get(int index) { 
    	return data[index];
    }
    public void set(int index, char t) { data[index]=t;}
    
    // Duplicatation (preferred instead):
    //TODO vj: Check direcitonality of copy
    public void copyTo(VecChar copy)  { 
    	copy.clear(); 
    	copy.growTo(sz); 
    	for (int i = 0; i < sz; i++) 
    		data[i]=copy.data[i];
    	}
    public void moveTo(VecChar dest) { 
    	dest.clear(true); 
    	dest.data = data; 
    	dest.sz = sz; 
    	dest.cap = cap; 
    	data = null; 
    	sz = 0; 
    	cap = 0; 
    }
}
