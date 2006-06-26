/*
 * Created on Nov 30, 2004
 *
 */
package polyglot.ext.x10.types;

import java.util.LinkedList;
import java.util.List;

import polyglot.ext.jl.types.Type_c;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.types.Type;
import polyglot.main.Report;

/** This class is added for the sake of symmetry, but may not be used very much.
 * Most ..ext.x10.type.X10*Type classes actually subclass from corresponding
 * ..ext.jl.type.*Type classes, and manually add the methods to implement X10Type.
 * Only those X10*Type classes which dont may extend X10Type_c.
 * 
 * TODO: Check all the other predicates from Type_c and determine which of them need to 
 * be redefined here.
 * 
 * @author vj
 *
 * 
 */
public abstract class X10Type_c extends Type_c implements X10Type {
	
    protected DepParameterExpr depClause;
    protected List/*<GenParameterExpr>*/ typeParameters;
    public void setTypeParameters(List t) { typeParameters = t; }
    public void setDepClause(DepParameterExpr d) { depClause = d; }
    public boolean isParametric() { return (typeParameters == null) || ! typeParameters.isEmpty();}
    public List typeParameters() {return typeParameters;}
    public DepParameterExpr depClause() { return depClause();}
    
	// TODO: Extend this for other kinds of X10 arrays
	public static boolean isPrimitiveTypeArray(X10Type me) {
		return 
        me.isBooleanArray() || 
        me.isCharArray() || 
        me.isByteArray() || 
        me.isShortArray() || 
        me.isIntArray() || 
        me.isLongArray() || 
        me.isFloatArray() || 
        me.isDoubleArray();
	}
	
	public static boolean isX10Array(X10Type me) { 
        X10TypeSystem ts = X10TypeSystem_c.getTypeSystem();
        boolean result = ts.isSubtype(me, ts.Indexable());        
		return result;
	}
    
    public static boolean isBooleanArray(X10Type me) {
        X10TypeSystem ts = X10TypeSystem_c.getTypeSystem();
        return ts.isSubtype( me, ts.booleanArray()); 
    }
    public static boolean isCharArray(X10Type me) {
        X10TypeSystem ts = X10TypeSystem_c.getTypeSystem();
        return ts.isSubtype( me, ts.charArray()); 
    }
    public static boolean isByteArray(X10Type me) {
        X10TypeSystem xts = X10TypeSystem_c.getTypeSystem();
        return xts.isSubtype( me, xts.byteArray()); 
    }
    public static boolean isShortArray(X10Type me) {
        X10TypeSystem xts = X10TypeSystem_c.getTypeSystem();
        return xts.isSubtype( me, xts.shortArray()); 
    }
    public static boolean isIntArray(X10Type me) {
        X10TypeSystem xts = X10TypeSystem_c.getTypeSystem();
        return xts.isSubtype( me, xts.intArray()); 
    }
    public static boolean isLongArray(X10Type me) {
        X10TypeSystem xts = X10TypeSystem_c.getTypeSystem();
        return xts.isSubtype( me, xts.longArray()); 
    }
    public static boolean isFloatArray(X10Type me) {
        X10TypeSystem xts = X10TypeSystem_c.getTypeSystem();
        return xts.isSubtype( me, xts.floatArray()); 
    }
    public static boolean isDoubleArray(X10Type me) {
        X10TypeSystem xts = X10TypeSystem_c.getTypeSystem();
        return xts.isSubtype( me, xts.doubleArray());
    }
    public static boolean isClock(X10Type me) {
        X10TypeSystem xts = X10TypeSystem_c.getTypeSystem();
        return xts.isSubtype( me, xts.clock());
    }
    public static boolean isPoint(X10Type me) {
        X10TypeSystem xts = X10TypeSystem_c.getTypeSystem();
        return xts.isSubtype( me, xts.point());
    }
    public static boolean isPlace(X10Type me) {
        X10TypeSystem xts = X10TypeSystem_c.getTypeSystem();
        return xts.isSubtype( me, xts.place());
    }
    public static boolean isRegion(X10Type me) {
        X10TypeSystem xts = X10TypeSystem_c.getTypeSystem();
        return xts.isSubtype( me, xts.region());
    }
    public static boolean isDistribution(X10Type me) {
        X10TypeSystem xts = X10TypeSystem_c.getTypeSystem();
        return xts.isSubtype( me, xts.distribution());
    }
    public static boolean isValueType( Type t) {
       // Report.report(5, "[X10Type_c] isValueType " + t + " " + t.getClass());
        X10TypeSystem xts=X10TypeSystem_c.getTypeSystem();
        Type target = xts.value();
        boolean res =  t.isSubtype( target);
        //Report.report(5, "[X10Type_c] isValueType " + res + "( target=" + target+")");
        return res;
    }
    
    public  static boolean isSubtypeImpl( Type me, Type other) {
    
        X10Type target = (X10Type) other;
        X10TypeSystem ts=X10TypeSystem_c.getTypeSystem();
        
        boolean result1 = ts.equals(me, target); 
        boolean result2 = ts.descendsFrom(me, target);
        if (  Report.should_report("debug", 3))
            Report.report( 3, "[X10Type_c] isSubTypeImpl: |" 
                    + me  +  "|(#" + me.hashCode()+")"  +") descends from |" 
                    + target + "(#" + target.hashCode() + ")|? " + result1 );  
        boolean result = result1 || result2;
        if (result) {
            if (  Report.should_report("debug", 3))
                Report.report( 3, "[X10Type_c] isSubTypeImpl: |" 
                        + me  +  "|(#" + me.hashCode()+") is a subtype of |" 
                        + target + "(#" + target.hashCode() + ")|.");  
             
            return result;
        }
        if (target.isNullable()) {
            NullableType toType = target.toNullable();
            Type baseType = toType.base();
            result = me.isSubtypeImpl( baseType );
            if (Report.should_report("debug", 3))
                Report.report( 3, "[X10Type_c] isSubTypeImpl: |" 
                        + me  +  "|(#" + me.hashCode()+") is" + (result ? "" : " not") + " a subtype of |" 
                        + target + "(#" + target.hashCode() + ").");  
               
            return result;
        }
        if ( Report.should_report("debug", 3))
            Report.report( 3, "[X10Type_c] isSubTypeImpl: |"
                    + me  +  "|(#" + me.hashCode()+") is not a subtype of |" 
                    + target + "(#" + target.hashCode() + ")|.");  
        
        return false;
    }
    
    public boolean isNullable() {return false; }
    public boolean isFuture() {return false; }
    public FutureType toFuture() {return null; }
    public NullableType toNullable() {return null;}
    public boolean isPrimitiveTypeArray() {return X10Type_c.isPrimitiveTypeArray(this);}
    public boolean isX10Array() {return X10Type_c.isX10Array(this);}
	public boolean isBooleanArray() {return X10Type_c.isBooleanArray(this);}
    public boolean isCharArray() {return X10Type_c.isCharArray(this);}
    public boolean isByteArray() {return X10Type_c.isByteArray(this); }
    public boolean isShortArray() {return X10Type_c.isShortArray(this);}
    public boolean isIntArray() {return X10Type_c.isIntArray(this); }
	public boolean isLongArray() {return X10Type_c.isLongArray(this);}
    public boolean isFloatArray() {return X10Type_c.isFloatArray(this);}
	public boolean isDoubleArray() {return X10Type_c.isDoubleArray(this);}
	public boolean isClock() {return X10Type_c.isClock(this);}
	public boolean isPoint() {return X10Type_c.isPoint(this);}
	public boolean isPlace() {return X10Type_c.isPlace(this);}
	public boolean isRegion() {return X10Type_c.isRegion(this);}
	public boolean isDistribution() {return X10Type_c.isDistribution(this);}
    public boolean isValueType() {return X10Type_c.isValueType(this);}
	public boolean isSubtypeImpl(Type other) {return X10Type_c.isSubtypeImpl(this, other);}
	
	
	
}
