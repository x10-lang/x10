/*
 * Created on Nov 14, 2004
 */
package x10.base;

/**
 * @author Christoph von Praun
 */

public abstract class MemoryBlock implements UnsafeContainer {

    private static final String MSG00_ = "MemoryBlock: Implementation deferred.";
    
    public abstract long size();
    
    public abstract int count(); 
    
    /**
     * @param other
     * @return true if these objects are value-equals
     */
    public abstract boolean valueEquals(MemoryBlock other);
    
    public boolean deepEquals(MemoryBlock other) {
        throw new UnsupportedOperationException("MemoryBlock::deepEquals");
    }
    
    public boolean setBoolean(boolean val, int d0) {
        throw new Error(MSG00_);
    }

    public boolean getBoolean(int d0) {
        throw new Error(MSG00_);
    }

    public byte setByte(byte val, int d0) {
        throw new Error(MSG00_);
    }

    public byte getByte(int d0) {
        throw new Error(MSG00_);
    }

    public char setChar(char val, int d0) {
        throw new Error(MSG00_);
    }

    public char getChar(int d0) {
        throw new Error(MSG00_);
    }

    public short setShort(short val, int d0) {
        throw new Error(MSG00_);
    }

    public short getShort(int d0) {
        throw new Error(MSG00_);
    }

    public int setInt(int val, int d0) {
        throw new Error(MSG00_);
    }

    public int getInt(int d0) {
        throw new Error(MSG00_);
    }

    public long setLong(long val, int d0) {
        throw new Error(MSG00_);
    }

    public long getLong(int d0) {
        throw new Error(MSG00_);
    }

    public float setFloat(float val, int d0) {
        throw new Error(MSG00_);
    }

    public float getFloat(int d0) {
        throw new Error(MSG00_);
    }

    public double setDouble(double val, int d0) {
        throw new Error(MSG00_);
    }

    public double getDouble(int d0) {
        throw new Error(MSG00_);
    }

    public Object set(Object val, int d0) {
        throw new Error(MSG00_);
    }

    public Object get(int d0) {
        throw new Error(MSG00_);
    }
}