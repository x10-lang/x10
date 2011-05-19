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
    
    public IntegralValue(TypeSystem ts, long n, boolean unsigned) {
        super(n, unsigned ? ts.ULong() : ts.Long());
    }
    
    public IntegralValue(TypeSystem ts, long n) {
        this(ts, n, false);
    }
    
    public IntegralValue(TypeSystem ts, int n, boolean unsigned) {
        super(unsigned ? 0xFFFFFFFF & (long) n : (long) n, unsigned ? ts.UInt() : ts.Int());
    }

    public IntegralValue(TypeSystem ts, int n) {
        this(ts, n, false);
    }

    public IntegralValue(TypeSystem ts, short n, boolean unsigned) {
        super(unsigned ? 0xFFFF & (long) n : (long) n, unsigned ? ts.UShort() : ts.Short());
    }
    
    public IntegralValue(TypeSystem ts, short n) {
        this(ts, n, false);
    }
    
    public IntegralValue(TypeSystem ts, byte n, boolean unsigned) {
        super(unsigned ? 0xFF & (long) n : (long) n, unsigned ? ts.UByte() : ts.Byte());
    }

    public IntegralValue(TypeSystem ts, byte n) {
        this(ts, n, false);
    }

	@Override
	public Long value() {
		return (Long) value;
	}

    public byte asByte() {
    	return (byte) (long) (Long) value;
    }

    public short asShort() {
    	return (short) (long) (Long) value;
    }

    public char asChar() {
    	return (char) (long) (Long) value;
    }

    public int asInt() {
    	return (int) (long) (Long) value;
    }

    public long asLong() {
    	return (long) (Long) value;
    }

}
