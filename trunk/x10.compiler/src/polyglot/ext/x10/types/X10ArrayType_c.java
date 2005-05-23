/*
 * Created on Apr 18, 2005
 */
package polyglot.ext.x10.types;

import polyglot.ext.jl.types.ArrayType_c;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;

/**
 * @author Christian Grothoff
 */
public class X10ArrayType_c extends ArrayType_c implements X10ReferenceType {

	/**
	 * 
	 */
	public X10ArrayType_c() {
		super();
	}
	
    public boolean isArray() {
        return true;
    }
    
    public boolean isX10Array() {
        return true;
    }

	/**
	 * @param ts
	 * @param pos
	 * @param base
	 */
	public X10ArrayType_c(TypeSystem ts, Position pos, Type base) {
		super(ts, pos, base);
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.X10Type#isNullable()
	 */
	public boolean isNullable() {
		return false;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.X10Type#isFuture()
	 */
	public boolean isFuture() {
		return false;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.X10Type#toNullable()
	 */
	public NullableType toNullable() {
	     return X10TypeSystem_c.getFactory().createNullableType(Position.COMPILER_GENERATED, this);
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.X10Type#toFuture()
	 */
	public FutureType toFuture() {
		return X10TypeSystem_c.getFactory().createFutureType(Position.COMPILER_GENERATED, this);
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.X10Type#isBooleanArray()
	 */	
	public boolean isBooleanArray() {
		return base.isBoolean();
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.X10Type#isCharArray()
	 */
	public boolean isCharArray() {
		return base.isChar();
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.X10Type#isByteArray()
	 */
	public boolean isByteArray() {
		return base.isByte();
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.X10Type#isShortArray()
	 */
	public boolean isShortArray() {
		return base.isShort();
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.X10Type#isLongArray()
	 */
	public boolean isLongArray() {
		return base.isLong();
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.X10Type#isIntArray()
	 */
	public boolean isIntArray() {
		return base.isInt();
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.X10Type#isFloatArray()
	 */
	public boolean isFloatArray() {
		return base.isFloat();
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.X10Type#isDoubleArray()
	 */
	public boolean isDoubleArray() {
		return base.isDouble();
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.X10Type#isPrimitiveTypeArray()
	 */
	public boolean isPrimitiveTypeArray() {
		return base.isPrimitive();
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.X10Type#isDistributedArray()
	 */
	public boolean isDistributedArray() {
		return true; // FIXME: how to tell?  
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.X10Type#isPoint()
	 */
	public boolean isPoint() {
		return false;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.X10Type#isPlace()
	 */
	public boolean isPlace() {
		return false;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.X10Type#isDistribution()
	 */
	public boolean isDistribution() {
		return false; // FIXME: should the array be identified with its distribution?
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.X10Type#isRegion()
	 */
	public boolean isRegion() {
		return false; // FIXME: should the array be identified with its region?
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.X10Type#isClock()
	 */
	public boolean isClock() {
		return false;
	}

	public boolean isValueType() {
		throw new RuntimeException("Internal compiler error: isValueType() on " + this);
	}
}
