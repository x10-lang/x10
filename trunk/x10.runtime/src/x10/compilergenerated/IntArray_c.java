/*
 * Created on Oct 20, 2004
 */
package x10.compilergenerated;

import x10.lang.Array;
import x10.lang.Distribution;
import x10.lang.IntArray;
import x10.runtime.Array_c;

/**
 * @author Christian Grothoff
 */
public class IntArray_c extends Array_c implements IntArray {

    private final int[] arr_;
    
    public IntArray_c(Distribution d) {
        super(d);
        this.arr_ = new int[d.size()];
    }
    
    /* (non-Javadoc)
     * @see x10.lang.IntArray#set(int, int[])
     */
    public void set(int v, int[] pos) {
        arr_[this.d.ordinal(pos)] = v;
    }

    /* (non-Javadoc)
     * @see x10.lang.IntArray#get(int[])
     */
    public int get(int[] pos) {
        return arr_[this.d.ordinal(pos)];
    }

    /* (non-Javadoc)
     * @see x10.lang.Array#padd(x10.lang.Array)
     */
    public Array padd(Array arg) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see x10.lang.Array#psub(x10.lang.Array)
     */
    public Array psub(Array arg) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see x10.lang.Array#pmult(x10.lang.Array)
     */
    public Array pmult(Array arg) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see x10.lang.Array#mult(x10.lang.Array)
     */
    public Array mult(Array arg) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see x10.lang.Array#circshift(int[])
     */
    public Array circshift(int[] arg) {
        // TODO Auto-generated method stub
        return null;
    }

}
