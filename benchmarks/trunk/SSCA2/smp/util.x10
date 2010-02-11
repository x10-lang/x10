package ssca2;

import x10.util.*;

class util {
  public def this () { }

  public static def get_seconds() { return x10.util.Timer.nanoTime()*1e-9; }
  public static val thisInstance = new util();
  val nthreads: Int;
  public static def x10_set_num_threads(THREADS: int) {
    thisInstance.nthreads = THREADS;
  }
  public static def x10_get_num_threads(): Int {
    return thisInstance.nthreads;
  }

  public static def prefix_sums(input:Rail[types.LONG_T], result:Rail[types.LONG_T], p: Rail[types.LONG_T], val n: types.LONG_T) {

    val r = n/nthreads;

    result(0) = 0;


    for ((i) in [max(1,tid*chunkSize)..(tid+1)*chunkSize-1]) {
        result(i) = input(i-1);
    }

    val start =  tid*r + 1;
    val end   = (tid+1)*r;

    if (tid == nthreads-1)
        end = n+1;

    for (j=start; j<end; j++)
        result(j) = input(j-1) + result(j-1);

    p(tid) = result(end-1);

    /* #pragma x10 barrier */

    if (tid == 0) {
        for (j=1; j<nthreads; j++)
            p(j) += p(j-1);
    }

    /* #pragma x10 barrier */

    if (tid>0) {
       add_value=p(tid-1);
        for (j=start-1; j<end; j++)
            result(j) += add_value;
    }

    /* #pragma x10 barrier */
}


  public static def BinarySearchEdgeListL(array:Rail[types.LONG_T], m:types.LONG_T, i:types.LONG_T) : types.LONG_T {

    if (i==0) return 0;

    val h = m;
    val l = 0;

    while ((h-l) > 0) {
        val md = (h+l)/2;
        if ((array(md) <= i) && (array(md+1) > i)) {
            return md;
        }

        if (array(md) > i) {
            h = md;
        }

        if ((array(md) <= i) && (array(md+1) <= i)) {
            l = md+1;
        }
    }

    return (md-1);
}

public static def dynArraySortedInsert (l: GrowableRail[types.LONG_T]!, elem: types.LONG_T, pos: types.LONG_T) {

    /* shift all the values to the right */
    for (var i: Int= l.length()-1; i>=pos; i--) {
        l(i+1) = l(i);
    }

    /* insert in correct position */
    l(pos) = elem;
}

public static def dynArraySortedInsertPos(l: GrowableRail[types.LONG_T]!, elem: types.LONG_T): Int {

    if (l.length()==0) {
        return 0;
    }

    for (i=0; i<l.length(); i++) {
        if (l(i)== elem)
            return -1;
        if (l(i) > elem)
            return i;
    }

    return l.length();
}

public static def dynArrayInsert(l: GrowableRail[types.LONG_T]!, elem: types.LONG_T) {
    l(l.length()-1) = elem;
}
};
