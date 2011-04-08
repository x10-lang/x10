/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2010.
 */
package x10.types.constants;

import polyglot.types.TypeSystem;

/**
 * @author Bowen Alpern
 *
 */
public final class IntegralValue extends ConstantValue {
    
    IntegralValue(TypeSystem ts, long n, boolean unsigned) {
        super(n, unsigned ? ts.ULong() : ts.Long());
    }
    
    IntegralValue(TypeSystem ts, long n) {
        this(ts, n, false);
    }
    
    IntegralValue(TypeSystem ts, int n, boolean unsigned) {
        super(n, unsigned ? ts.UInt() : ts.Int());
    }

    IntegralValue(TypeSystem ts, int n) {
        this(ts, n, false);
    }

    IntegralValue(TypeSystem ts, short n, boolean unsigned) {
        super(n, unsigned ? ts.UShort() : ts.Short());
    }
    
    IntegralValue(TypeSystem ts, short n) {
        this(ts, n, false);
    }
    
    IntegralValue(TypeSystem ts, byte n, boolean unsigned) {
        super(n, unsigned ? ts.UByte() : ts.Byte());
    }

    IntegralValue(TypeSystem ts, byte n) {
        this(ts, n, false);
    }
    
}
