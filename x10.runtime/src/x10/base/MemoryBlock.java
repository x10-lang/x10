/*
 * Created on Nov 14, 2004
 */
package x10.base;

/**
 * @author Christoph von Praun
 */

public abstract class MemoryBlock implements UnsafeContainer {

    private static final String MSG_ = "MemoryBlock: Implementation deferred.";

    public abstract long size();
    
    public abstract int count(); 
    
    public boolean setBoolean(boolean val, int d0) {
        throw new Error(MSG_);
    }

    public boolean getBoolean(int d0) {
        throw new Error(MSG_);
    }

    public byte setByte(byte val, int d0) {
        throw new Error(MSG_);
    }

    public byte getByte(int d0) {
        throw new Error(MSG_);
    }

    public char setChar(char val, int d0) {
        throw new Error(MSG_);
    }

    public char getChar(int d0) {
        throw new Error(MSG_);
    }

    public short setShort(short val, int d0) {
        throw new Error(MSG_);
    }

    public short getShort(int d0) {
        throw new Error(MSG_);
    }

    public int setInt(int val, int d0) {
        throw new Error(MSG_);
    }

    public int getInt(int d0) {
        throw new Error(MSG_);
    }

    public long setLong(long val, int d0) {
        throw new Error(MSG_);
    }

    public long getLong(int d0) {
        throw new Error(MSG_);
    }

    public float setFloat(float val, int d0) {
        throw new Error(MSG_);
    }

    public float getFloat(int d0) {
        throw new Error(MSG_);
    }

    public double setDouble(double val, int d0) {
        throw new Error(MSG_);
    }

    public double getDouble(int d0) {
        throw new Error(MSG_);
    }
}