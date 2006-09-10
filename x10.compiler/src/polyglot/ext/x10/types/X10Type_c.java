/*
 * Created on Nov 30, 2004
 *
 */
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.ext.jl.types.Type_c;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.main.Report;
import polyglot.types.Type;

/** This class is added for the sake of symmetry, but may not be used very much.
 * Most ..ext.x10.type.X10*Type classes actually subclass from corresponding
 * ..ext.jl.type.*Type classes, and manually add the methods to implement X10Type.
 * Only those X10*Type classes which dont may extend ts.
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
    protected X10Type baseType = this;
    public X10Type baseType() { return baseType;}
    public boolean isParametric() { return (typeParameters == null) || ! typeParameters.isEmpty();}
    public List typeParameters() {return typeParameters;}
    public DepParameterExpr depClause() { return depClause();}
    protected X10TypeSystem xts = (X10TypeSystem) ts;
    
    public boolean typeEqualsImpl(Type o) {
        return equalsImpl(o);
    }
    public int hashCode() {
        return 
          (baseType == this ? super.hashCode() : baseType.hashCode() ) 
        + (depClause != null ? depClause.hashCode() : 0)
        + ((typeParameters !=null && ! typeParameters.isEmpty()) ? typeParameters.hashCode() :0);
        
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
        if (ts.isNullable(target)) {
            NullableType toType = toNullable(target);
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
    

    public static NullableType toNullable(X10Type me) {
        X10TypeSystem ts=X10TypeSystem_c.getTypeSystem();
        return ts.isNullable(me) ? (NullableType) me : null;
    }
    public static FutureType toFuture(X10Type me) {
        X10TypeSystem ts=X10TypeSystem_c.getTypeSystem();
        return ts.isFuture(me) ? (FutureType) me : null;
    }
    
    
 
  
    public boolean isNullable() {return false; }
    public boolean isFuture() {return false; }
    public FutureType toFuture() {return toFuture(this); }
    public NullableType toNullable() {return toNullable(this);}
    public boolean isPrimitiveTypeArray() {return xts.isPrimitiveTypeArray(this);}
    public boolean isX10Array() {return xts.isX10Array(this);}
    public boolean isBooleanArray() {return xts.isBooleanArray(this);}
    public boolean isCharArray() {return xts.isCharArray(this);}
    public boolean isByteArray() {return xts.isByteArray(this); }
    public boolean isShortArray() {return xts.isShortArray(this);}
    public boolean isIntArray() {return xts.isIntArray(this); }
    public boolean isLongArray() {return xts.isLongArray(this);}
    public boolean isFloatArray() {return xts.isFloatArray(this);}
    public boolean isDoubleArray() {return xts.isDoubleArray(this);}
    public boolean isClock() {return xts.isClock(this);}
    public boolean isPoint() {return xts.isPoint(this);}
    public boolean isPlace() {return xts.isPlace(this);}
    public boolean isRegion() {return xts.isRegion(this);}
    public boolean isDistribution() {return xts.isDistribution(this);}
    public boolean isValueType() {return xts.isValueType(this);}
  // public boolean isSubtypeImpl(Type other) {return xts.isSubtypeImpl(this, other);}
	
	
}
