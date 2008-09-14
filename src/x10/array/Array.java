/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Oct 28, 2004
 */
package x10.array;

import x10.core.Box;
import x10.types.RuntimeType;
import x10.types.Type;


/**
 * Double arrays.
 *
 * @author Christoph von Praun
 * @author Igor Peshansky
 */
public abstract class Array<T> extends BaseRefArray<T> {
	 
	public Array(Type<T> T, Dist d) {
		super(T, d);
	}
	
	// vj: Copied from Box -- TODO: needs to be checked by Nate.
	public static class RTT extends RuntimeType<Array<?>> {
        Type<?> type;

        public RTT(Type<?> type) {
            super(Array.class);
            this.type = type;
        }
        
        @Override
        public boolean instanceof$(Object o) {
            return o instanceof Array && ((Array) o).T.equals(type);
        }
        
        @Override
        public boolean isSubtype(Type<?> o) {
            if (o instanceof Array.RTT) {
                Array.RTT other = (Array.RTT) o;
                return type.isSubtype(other.type);
            }
            return super.isSubtype(o);
        }
    }
}

