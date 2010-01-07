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
public abstract class Future[+T] implements ()=>T {
    /**
     * Wait for the completion of this activity and return the computed value.
     */
    public abstract global def force(): T;

    /**
     * Return true if this activity has completed.
     */
    public abstract global def forced(): boolean;
}
