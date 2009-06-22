/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Nov 14, 2004
 *
 */
package x10.base;

/**
 * This interface has to be implemented by any data structure that 
 * is passed to a native method (structs and arrays).
 * 
 * @author Christoph von Praun
 */
public interface UnsafeContainer {
    public int[] getDescriptor();
}
