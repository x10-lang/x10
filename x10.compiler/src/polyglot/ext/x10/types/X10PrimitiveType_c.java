/*
 * Created on Oct 4, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package polyglot.ext.x10.types;

import java.util.LinkedList;
import java.util.List;

import polyglot.ext.jl.types.PrimitiveType_c;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.main.Report;
import polyglot.types.Type;
import polyglot.types.TypeSystem;

/** X10 has no primitive types. Types such as int etc are all value class types. 
 * However, this particular X10 implementation uses Java primitive types to implement some of
 * X10's value class types, namely, char, boolean, byte, int etc etc. It implements other
 * value class types as Java classes.
 * 
 * Thus this class represents one of specially implemented X10 value class types.
 * @author praun
 * @author vj
 */
public class X10PrimitiveType_c extends PrimitiveType_c implements X10PrimitiveType {

	/** Used for deserializing types. */
    protected X10PrimitiveType_c() { }

    public X10PrimitiveType_c(TypeSystem ts, Kind kind) {
        super(ts, kind);
    }
    
    protected DepParameterExpr depClause;
    protected List/*<GenParameterExpr>*/ typeParameters;
    protected X10Type baseType = this;
    public X10Type baseType() { return baseType;}
    public boolean isParametric() { return typeParameters != null && ! typeParameters.isEmpty();}
    public List typeParameters() { return typeParameters;}
    
    public X10Type makeVariant(DepParameterExpr d, List/*<GenParameterExpr>*/ l) { 
        if (d == null && (l == null || l.isEmpty()))
            return this;
        X10PrimitiveType_c n = (X10PrimitiveType_c) copy();
        // n.baseType = baseType; // this may not be needed.
        n.typeParameters = l;
        n.depClause = d;
        if (  Report.should_report("debug", 5))
            Report.report(5,"X10PrimitiveType_c.makeVariant: " + this + " creates |" + n + "|");
        return n;
    }

    
    public boolean equalsImpl(Object o) {
        if (! (o instanceof X10PrimitiveType_c)) return false;
        X10PrimitiveType_c other = (X10PrimitiveType_c) o;
        if (! super.equals(o)) return false;
        if (depClause == null && other.depClause != null) return false;
        if (depClause != null && ! depClause.equals(other.depClause)) return false;
        
        if (typeParameters == null) return other.typeParameters == null;
        if (typeParameters.isEmpty()) return other.typeParameters == null || other.typeParameters.isEmpty();
        return typeParameters.equals(other.typeParameters);
    }
       
    /** Every X10 value type descends from X10.lang.Object, the base class.
     *
     */
    public boolean descendsFromImpl(Type ancestor) {
    	X10TypeSystem xts = (X10TypeSystem) ts;
        return ts.equals(ancestor, xts.X10Object());
    }

    /** Return true if this type can be assigned to <code>toType</code>. */
    public boolean isImplicitCastValidImpl(Type toType) {
        // System.out.println( "[PrimitiveType_c] isImplicitCastValid |" + this + "| to |" + toType + "|?");
    	X10TypeSystem xts = (X10TypeSystem) ts;
        return ts.equals(toType, xts.X10Object()) ||
               super.isImplicitCastValidImpl(toType);
    }

    /** Returns true iff a cast from this to <code>toType</code> is valid. */
    public boolean isCastValidImpl(Type toType) {
    	X10TypeSystem xts = (X10TypeSystem) ts;
        return ts.equals(toType, xts.Object()) || super.isCastValidImpl(toType);
    }
    
 /*   public String toString() { 
        //Report.report(5,"X10ParsedClassType: toString |" + super.toString() + "|(#" 
        //        + this.hashCode() + this.getClass() + ") typeParameters=|" + typeParameters+"|");
        return  
        ((baseType == this) ? super.toString() : ((X10PrimitiveType_c) baseType).toString())
        + (isParametric() ? typeParameters.toString()  : "") 
        + (depClause == null ? "" :  depClause.toString())
        + "(#" + hashCode() + ")";
    }*/
    
//	 ----------------------------- begin manual mixin code from X10Type_c
	public boolean isNullable() { return false; }
	public boolean isFuture() { return false; }
	public NullableType toNullable() { return null;}
	public FutureType toFuture() { return null; }
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
    
    /**
     * Note that this (general) mix-in code correctly takes care of ensuring that
     * int is a subtype of nullable int as well as x10.lang.X10Object.
     */
    public boolean isSubtypeImpl(  Type other) { return X10Type_c.isSubtypeImpl(this, other);}
    public boolean isValueType() { return X10Type_c.isValueType(this); }
	// ----------------------------- end manual mixin code from X10Type_c
}
