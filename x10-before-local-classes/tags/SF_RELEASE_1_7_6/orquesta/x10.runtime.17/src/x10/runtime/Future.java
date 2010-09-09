/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Oct 3, 2004
 */
package x10.runtime;

import x10.core.fun.Fun_0_0;

/**
 * @author Christian Grothoff
 * @author Christoph von Praun
 */
public interface Future<T> extends Fun_0_0<T> {

    /**
     * Wait for the completion of this activity and return the return value.
     */
    public T apply();

    /**
     * Wait for the completion of this activity and return the return value.
     */
    public abstract T force();

    /**
     * Return true if this activity has completed.
     */
    public abstract boolean forced();
}
