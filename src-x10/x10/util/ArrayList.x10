// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.util;

import x10.lang.Iterator;


public class ArrayList[T] implements (nat)=>T {

    private var a: Rail[T];
    private var n: int = 0;

    public def add(v: T): void {
        if (n==a.length) {
            var b: Rail[T] = Rail.makeVar[T](a.length*2);
            for (var i: int = 0; i<n; i++)
                b(i) = a(i);
            a = b;
        }
        a(n++) = v;
    }
            
    public def apply(i: nat) = a(i);

    public def get(i: int): T = a(i);

    public def size(): int = n;

    public def toArray(): Rail[T] {
        var b: Rail[T] = Rail.makeVar[T](n);
        for (var i: int = 0; i<n; i++)
            b(i) = a(i);
        return b;
    }

    public def this() {
        a = Rail.makeVar[T](10); // here, not in decl - XTENLANG-13
    }


    //
    // iterator
    //

    private class It implements Iterator[T] {
        
        private var i: int = 0;
        
        public def hasNext(): boolean {
            return i</*al.*/n;
        }
        
        public def next(): T {
            return /*al.*/a(i++);
        }

        incomplete public def remove(): void;
    }

    public def iterator(): Iterator[T] {
        return new It();
    }



    //
    // quick&dirty sort
    //

    public def sort() {T <: Comparable[T]} = qsort(a, 0, n-1);

    private def qsort(a: Rail[T], lo: int, hi: int) {T<:Comparable[T]} {
        if (hi <= lo) return;
        var l: int = lo - 1;
        var h: int = hi;
        while (true) {
            while ((a(++l) as Comparable[T]).compareTo(a(hi))<0);
            while ((a(hi) as Comparable[T]).compareTo(a(--h))<0 && h>lo);
            if (l >= h) break;
            exch(a, l, h);
        }
        exch(a, l, hi);
        qsort(a, lo, l-1);
        qsort(a, l+1, hi);
    }

    private def exch(a: Rail[T], i: int, j: int): void {
        val temp = a(i);
        a(i) = a(j);
        a(j) = temp;

    }
}
