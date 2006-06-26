/*
 * Created on Nov 30, 2004
 */
package polyglot.ext.x10.types;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.ext.jl.types.ReferenceType_c;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.main.Report;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.util.Position;

/** Implements an X10ReferenceType. We have it inherit from ReferenceType_c because
 * there is a lot of code there, and manually "mix-in" the code from X10Type_c.
 * @author vj
 *
 * 
 */
public abstract class X10ReferenceType_c extends ReferenceType_c implements
		X10ReferenceType {
	
    
    protected X10ReferenceType_c() { super();}
    public X10ReferenceType_c(TypeSystem ts) { this(ts, null);}
    public X10ReferenceType_c(TypeSystem ts, Position pos) { super(ts, pos);}
    
    protected DepParameterExpr depClause;
    protected List/*<GenParameterExpr>*/ typeParameters;
    protected X10Type baseType = this;
    public X10Type baseType() { return baseType;}
    public boolean isParametric() { return typeParameters != null && ! typeParameters.isEmpty();}
    public List typeParameters() { return typeParameters;}
    public X10Type makeVariant(DepParameterExpr d, List l) { 
        if (d == null && (l == null || l.isEmpty()))
                return this;
        X10ReferenceType_c n = (X10ReferenceType_c) copy();
        // n.baseType = baseType; // this may not be needed.
        n.typeParameters = l;
        n.depClause = d;
        if (  Report.should_report("debug", 5))
            Report.report(5,"X10ReferenceType_c.makeVariant: " + this + " creates " + n + "|");
        return n;
    }
    
   
    public boolean equalsImpl(TypeObject o) {
        //    Report.report(3,"X10ParsedClassType_c: equals |" + this + "| and |" + o+"|");
        if (o == this) return true;
        if (! (o instanceof X10ReferenceType_c)) return false;
        X10ReferenceType_c other = (X10ReferenceType_c) o;
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
   
    public boolean isCanonical() {
        if (typeParameters != null) {
            Iterator it = typeParameters.iterator();
            while (it.hasNext()) {
                Type t = (Type) it.next();
                if (!t.isCanonical())
                    return false;
            }
        }
        return true;
        
    }    
	// ----------------------------- begin manual mixin code from X10Type_c
    public boolean isNullable() { return false; }
    public boolean isFuture() { return false; }
    public FutureType toFuture() { return null; }
    public NullableType toNullable() { return null;}
    public boolean isPrimitiveTypeArray() { return X10Type_c.isPrimitiveTypeArray(this);}
    public boolean isX10Array() { return X10Type_c.isX10Array(this);}
    public boolean isDistributedArray() { return false;}
    public boolean isBooleanArray() { return X10Type_c.isBooleanArray(this);}
    public boolean isCharArray() { return X10Type_c.isCharArray(this);}
    public boolean isByteArray() { return X10Type_c.isByteArray(this); }
    public boolean isShortArray() { return X10Type_c.isShortArray(this);}
    public boolean isIntArray() { return X10Type_c.isIntArray(this); }
    public boolean isLongArray() { return X10Type_c.isLongArray(this);}
    public boolean isFloatArray() { return X10Type_c.isFloatArray(this);}
    public boolean isDoubleArray() { return X10Type_c.isDoubleArray(this);}
    public boolean isClock() { return X10Type_c.isClock(this);}
    public boolean isPoint() { return X10Type_c.isPoint(this);}
    public boolean isPlace() { return X10Type_c.isPlace(this);}
    public boolean isRegion() { return X10Type_c.isRegion(this);}
    public boolean isDistribution() { return X10Type_c.isDistribution(this);}
    public boolean isValueType() { return X10Type_c.isValueType(this);}
    public boolean isSubtypeImpl(  Type other) { return X10Type_c.isSubtypeImpl(this, other);}
  
    
	public boolean descendsFromImpl(Type ancestor) {
		// Check subtype relation for supertype.
		if (superType() == null) {
			return false;
		}

		if (ts.isSubtype(superType(), ancestor)) {
			return true;
		}

		// Next check default behavior.
		return super.descendsFromImpl(ancestor);
	}

	// ----------------------------- end manual mixin code from X10Type_c
	
}
