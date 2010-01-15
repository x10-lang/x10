/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.core;

import x10.rtt.Equality;
import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.rtt.Types;

public class BoxInt extends Box<Integer> {
    public BoxInt(int v) {
        super(Types.INT, v);
    }

    public int hashCode() {
        return value.hashCode();
    }
    
    public String toString() {
        return value.toString();
    }
}
