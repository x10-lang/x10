/*
 * Created on Sep 30, 2004
 */
package x10.runtime;

import x10.lang.Future;
import x10.lang.Object;

/**
 * This class encapsulates the return value of a local async
 * call and allows the client to wait for the completion of the
 * async call (force future).
 * 
 * @author Christian Grothoff
 * @author Christoph von PRaun
 */
final class Future_c extends Future {

    private boolean haveResult_;
    
    private Object result_;
    private char resultUnsigned_;
    private boolean resultBoolean_;
    private long resultSigned_;
    private double resultDouble_;
    
    /**
     * Set the result value returned by this async call.
     * 
     * @param result
     */
    public synchronized void setResult(Object result) {
        assert (! haveResult_); 
        this.result_ = result;
        this.haveResult_ = true;
        this.notifyAll(); // wake up 'force' if waiting
    }
    
    public synchronized void setResult(boolean result) {
        assert (! haveResult_); 
        this.resultBoolean_ = result;
        this.haveResult_ = true;
        this.notifyAll(); // wake up 'force' if waiting
    }
    
    public synchronized void setResult(long result) {
        assert (! haveResult_); 
        this.resultSigned_ = result;
        this.haveResult_ = true;
        this.notifyAll(); // wake up 'force' if waiting
    }
    
    public synchronized void setResult(char result) {
        assert (! haveResult_); 
        this.resultUnsigned_ = result;
        this.haveResult_ = true;
        this.notifyAll(); // wake up 'force' if waiting
    }
    
    public synchronized void setResult(double result) {
        assert (! haveResult_); 
        this.resultDouble_ = result;
        this.haveResult_ = true;
        this.notifyAll(); // wake up 'force' if waiting
    }
    
    /*
     * @see x10.runtime.Activity.Result#force()
     */
    public synchronized Object force() {
        while (! haveResult_) {
            try {
                LoadMonitored.blocked();
                this.wait();
            } catch (InterruptedException ie) {
                throw new Error(ie); // this should never happen...
            } finally {
                LoadMonitored.unblocked();
            }
        }
        return result_;
    }
	
	public boolean forceBoolean() {
	    return resultBoolean_;
	}
	
	public byte forceByte() {
	    return (byte) resultSigned_;
	}
	
	public char forceChar() {
	    return (char) resultUnsigned_;
	}
	
	public short forceShort() {
	    return (short) resultSigned_;
	}
	
	public int forceInt() {
	    return (int) resultSigned_;
	}
	
	public long forceLong() {
	    return resultUnsigned_;
	}
	
	public float forceFloat() {
	    return (float) resultDouble_;
	}
	
	public double forceDouble() {
	    return resultDouble_;
	}

}
