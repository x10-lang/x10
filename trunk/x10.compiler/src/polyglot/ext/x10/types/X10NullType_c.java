/*
 * Created on Nov 28, 2004
 *
 */
package polyglot.ext.x10.types;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.ext.jl.types.NullType_c;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;

/** Every X10 term must have a type. This is the type of the X10 term null.
 * Note that there is no X10 type called Null; only the term null.
 * @author vj
 *
 */
public class X10NullType_c extends NullType_c implements X10NullType {
	 /** Used for deserializing types. */
    protected X10NullType_c() {}
    public X10NullType_c( TypeSystem ts ) {super(ts);}
   
    protected DepParameterExpr depClause;
    protected List/*<GenParameterExpr>*/ typeParameters;
    protected X10Type baseType = this;
    public X10Type baseType() { return baseType;}
    public boolean isParametric() { return typeParameters != null && ! typeParameters.isEmpty();}
    public List typeParameters() { return typeParameters;}
    public X10Type makeVariant(DepParameterExpr d, List l) { 
        if (d == null && (l == null || l.isEmpty()))
                return this;
        X10NullType_c n = (X10NullType_c) copy();
        // n.baseType = baseType; // this may not be needed.
        n.typeParameters = l;
        n.depClause = d;
        return n;
    }
    
    
    public boolean equalsImpl(TypeObject o) {
        if (o == this) return true;
        if (! (o instanceof X10NullType_c)) return false;
        X10NullType_c other = (X10NullType_c) o;
        if (baseType != other.baseType) return false;
        
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
     * This is different from the definition of jl.Nullable. X10 does not 
     * assume that every reference type contains null. The type must be nullable
     * for it to contain null.
     * TODO: Check if the result should be just: targetType.isNullable().
     */
    public boolean isImplicitCastValidImpl(Type toType) {
    	X10Type targetType = (X10Type) toType;
    	return toType.isNull() || targetType.isNullable();
    }	

    /** 
     * TODO: vj -- check if this implementation is correct.
     * The definition of descendsFrom in TypeSystem is
     * Returns true iff child is not ancestor, but child descends from ancestor. 
     * In the X10 type system, the Null type should not descend from any type.
     */
    public boolean descendsFromImpl(Type ancestor) {
    	return ts.equals(ancestor, ts.Object());
        // if (ancestor.isNull()) return false;
        // if (ancestor.isReference()) return true;
        // return false;
    }

    /**
     * Same as isImplicitCastValidImpl.
     **/
    public boolean isCastValidImpl(Type toType) {
    	X10Type targetType = (X10Type) toType;
        return toType.isNull() || targetType.isNullable();
    }

//	 ----------------------------- begin manual mixin code from X10Type_c
	public boolean isNullable() { return false;}
	public boolean isFuture() { return false;}
	public NullableType toNullable() { return null;}
	public FutureType toFuture() { return null;}
	public boolean isDistribution() { return false; }
	public boolean isDistributedArray() { return false; }
	public boolean isPrimitiveTypeArray() { return false; }
	public boolean isBooleanArray() { return false; }
    public boolean isCharArray() { return false; }
    public boolean isByteArray() { return false; }
    public boolean isShortArray() { return false; }
    public boolean isLongArray() { return false; }
    public boolean isIntArray() { return false; }
    public boolean isFloatArray() { return false; }
    public boolean isDoubleArray() { return false; }
	public boolean isClock() { return false; }
	public boolean isRegion() { return false; }
	public boolean isPlace() { return false;}
	public boolean isPoint() { return false; }
	public boolean isX10Array() { return false; }
    public boolean isSubtypeImpl(  Type other) { return X10Type_c.isSubtypeImpl(this, other);}
    public boolean isValueType() { return true;}
  
	// ----------------------------- end manual mixin code from X10Type_c
	
	
}
