/*
 * Created on Nov 14, 2004
 */
package x10.base;

/**
 * @author Christoph von Praun
 */
public abstract class MemoryBlockSafe extends MemoryBlock {

    public void keepItLive() {}
    
    public long getUnsafeAddress() {
        throw new Error("MemoryBlockSafe: no unsafe address for safe memory.");
    }
}
