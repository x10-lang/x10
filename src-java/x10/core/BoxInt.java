/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.core;

import x10.types.Equality;
import x10.types.RuntimeType;
import x10.types.Type;
import x10.types.Types;

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
