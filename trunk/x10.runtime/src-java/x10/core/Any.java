/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.core;

import x10.rtt.RuntimeType;
import x10.rtt.Type;

// Base interface of all X10 entities.
public interface Any {
    public static class RTT extends RuntimeType<Any> {
    	public static final RTT it = new RTT();

    	public RTT() {
            super(Any.class);
        }

        @Override
        public boolean instanceof$(Object o) {
            return true;
        }
    }
}
