/*
 * Created on Nov 14, 2004
 *
 */
package x10.base;

/**
 * @author Christoph von Praun
 */
public interface UnsafeContainer {

    public void keepItLive();
    
    public long getUnsafeAddress();
    
}
