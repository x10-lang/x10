package x10.util;

// XXX implements List

// XXX temp - allows ArrayList_PolyRegion;
import x10.array.PolyRegion;
    
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

}
