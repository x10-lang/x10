/*
 * Created on Apr 18, 2005
 */
package polyglot.ext.x10.types;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ext.jl.types.ArrayType_c;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.util.Position;

/**
 * @author Christian Grothoff
 */
public class X10ArrayType_c extends ArrayType_c implements X10ReferenceType {

    protected DepParameterExpr depClause;
    protected List/*<GenParameterExpr>*/ typeParameters;
    protected X10Type baseType = this;
    public X10Type baseType() { return baseType;}
    public boolean isParametric() { return typeParameters != null && !  typeParameters.isEmpty();}
    public List typeParameters() { return typeParameters;}
    public X10Type makeVariant(DepParameterExpr d, List l) { 
        if (d == null && (l == null || l.isEmpty()))
                return this;
        X10ArrayType_c n = (X10ArrayType_c) copy();
        // n.baseType = baseType; // this may not be needed.
        n.typeParameters = l;
        n.depClause = d;
        return n;
    }
    public DepParameterExpr depClause() { return depClause; }
    
    public boolean equalsImpl(TypeObject o) {
        //Report.report(3,"X10ArrayType_c: equals |" + this + "| and |" + o+"|");
        if (o == this) return true;
        if (! (o instanceof X10ArrayType_c)) return false;
        X10ArrayType_c other = (X10ArrayType_c) o;
        
        if ( ! base.equalsImpl(other.base)) return false;
        
        if (depClause == null && other.depClause != null) return false;
        if (depClause != null && ! depClause.equals(other.depClause)) return false;
        
        if (typeParameters == null) return other.typeParameters == null;
        if (typeParameters.isEmpty()) return other.typeParameters == null || other.typeParameters.isEmpty();
        if (typeParameters.size() != other.typeParameters.size()) return false;
        Iterator it1 = typeParameters.iterator();
        Iterator it2 = other.typeParameters.iterator();
        while (it1.hasNext()) {
            Type t1 = (Type) it1.next();
            Type t2 = (Type) it2.next();
            if (!t1.equals(t2)) return false;
        }
        return true;
    }
    
	/**
	 * 
	 */
	public X10ArrayType_c() {super();}
    public X10ArrayType_c(TypeSystem ts, Position pos, Type base) {
        super(ts, pos, base);
    }
    
    public boolean isArray() {return true;}
    public boolean isX10Array() {return true;}
	public boolean isNullable() {return false; }
	public boolean isFuture() {return false;}
	public NullableType toNullable() {
	     return X10TypeSystem_c.getFactory().createNullableType(Position.COMPILER_GENERATED, this);
	}
	public FutureType toFuture() {
		return X10TypeSystem_c.getFactory().createFutureType(Position.COMPILER_GENERATED, this);
	}
	public boolean isBooleanArray() {return base.isBoolean();}
	public boolean isCharArray() {return base.isChar();}
	public boolean isByteArray() {return base.isByte();}
	public boolean isShortArray() {return base.isShort();}
	public boolean isLongArray() {return base.isLong();}
	public boolean isIntArray() {return base.isInt();}
	public boolean isFloatArray() {return  base.isFloat();	}
	public boolean isDoubleArray() {return base.isDouble();}
	public boolean isPrimitiveTypeArray() {return base.isPrimitive();}
    //  vj TODO: check whether the depclause says so 
	public boolean isDistributedArray() {return true; } 
	public boolean isPoint() {return false;}
	public boolean isPlace() {return false;}

   //FIXME: should the array be identified with its distribution?
    // vj: NO. A distribution is indexable, so itself an array.
	public boolean isDistribution() {return false; }

//   FIXME: should the array be identified with its region?
    // vj: No.
	public boolean isRegion() {return false; }
	public boolean isClock() {return false; }

    // FIXME: Its a value type if the array declaration says so.
    
	public boolean isValueType() {
	   return false;
		//throw new RuntimeException("Internal compiler error: isValueType() on " + this);
	}
    // TODO: Remove this class.
    public List properties() { return Collections.EMPTY_LIST;}
   
}
