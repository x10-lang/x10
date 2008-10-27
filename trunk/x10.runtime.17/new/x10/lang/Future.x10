/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

/**
 * @author Christian Grothoff
 * @author Christoph von Praun
 * @author tardieu
 */
public interface Future[T] extends ()=>T {
    /**
     * Wait for the completion of this activity and return the computed value.
     */
    public def force(): T;

    /**
     * Return true if this activity has completed.
     */
    public def forced(): boolean;
}
