package x10.util;

// XXX implements List

// XXX temp - allows ArrayList_PolyRegion;
import x10.array.PolyRegion;
import x10.array.Halfspace;
    
public class ArrayList_T {

    private T [] a = new T[10];
    private int n = 0;

    public void add(T v) {
        if (n==a.length) {
            T [] b = new T[a.length*2];
            for (int i=0; i<n; i++)
                b[i] = a[i];
            a = b;
        }
        a[n++] = v;
    }
            
    public T get(int i) {
        return a[i];
    }

    public int size() {
        return n;
    }

    public T [] toArray() {
        T [] b = new T[n];
        for (int i=0; i<n; i++)
            b[i] = a[i];
        return b;
    }


    //
    // iterator
    //

    private class Iterator implements Iterator_T {
        
        private int i = 0;
        
        public boolean hasNext() {
            return i<n;
        }
        
        public T next() {
            return a[i++];
        }
    }

    public Iterator_T iterator() {
        return new Iterator();
    }


    //
    // quick&dirty sort
    // XXX guarded by T implements Comparable
    //

    public void sort() {
        qsort((Comparable[])a, 0, n-1);
    }

    private void qsort(Comparable [] a, int lo, int hi) {
        if (hi <= lo) return;
        int l = lo - 1;
        int h = hi;
        while (true) {
            while (a[++l].compareTo(a[hi])<0);
            while (a[hi].compareTo(a[--h])<0 && h>lo);
            if (l >= h) break;
            exch(a, l, h);
        }
        exch(a, l, hi);
        qsort(a, lo, l-1);
        qsort(a, l+1, hi);
    }

    private void exch(Comparable [] a, int i, int j) {
        Comparable temp = a[i];
        a[i] = a[j];
        a[j] = temp;

    }
}
