/*
 * Created on Nov 14, 2004
 */
package x10.base;

/**
 * @author Christoph von Praun
 */

public abstract class MemoryBlock {

    private static final String MSG_ = "MemoryBlock: Implementation deferred.";

    public abstract long size();
    
    public abstract int count(); 
    
    public void setBoolean(boolean val, int d0) {
        throw new Error(MSG_);
    }

    public boolean getBoolean(int d0) {
        throw new Error(MSG_);
    }

    public void setByte(byte val, int d0) {
        throw new Error(MSG_);
    }

    public byte getByte(int d0) {
        throw new Error(MSG_);
    }

    public void setChar(char val, int d0) {
        throw new Error(MSG_);
    }

    public char getChar(int d0) {
        throw new Error(MSG_);
    }

    public void setShort(short val, int d0) {
        throw new Error(MSG_);
    }

    public short getShort(int d0) {
        throw new Error(MSG_);
    }

    public void setInt(int val, int d0) {
        throw new Error(MSG_);
    }

    public int getInt(int d0) {
        throw new Error(MSG_);
    }

    public void setLong(long val, int d0) {
        throw new Error(MSG_);
    }

    public long getLong(int d0) {
        throw new Error(MSG_);
    }

    public void setFloat(float val, int d0) {
        throw new Error(MSG_);
    }

    public float getFloat(int d0) {
        throw new Error(MSG_);
    }

    public void setDouble(double val, int d0) {
        throw new Error(MSG_);
    }

    public double getDouble(int d0) {
        throw new Error(MSG_);
    }
}