/*
 * Created on Oct 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package x10.runtime;

import x10.lang.Array;
import x10.lang.Distribution;
import x10.lang.X10Object;

/**
 * @author praun
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Array_c extends Array {

    private final Distribution_c d;
    
    Array_c(Distribution_c d) {
        super (d);
        this.d = d;
    }
	
	/* (non-Javadoc)
	 * @see x10.lang.Array#getDistribution()
	 */
	public Distribution getDistribution() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see x10.lang.Array#set(java.lang.Object, int[])
	 */
	public void set(X10Object v, int[] pos) {
	    d.setValueAt(this, pos, v);
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see x10.lang.Array#get(int[])
	 */
	public X10Object get(int[] pos) {
	    return d.getValueAt(this,pos);
	}
    
    X10Object getInternal(int[] pos) {
        return null; // FIXME Christoph!
    }
    void setInternal(int[] pos, X10Object value) {
        // FIXME Christoph!
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
