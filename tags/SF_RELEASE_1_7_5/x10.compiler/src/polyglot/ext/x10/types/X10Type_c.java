/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Nov 30, 2004
 *
 */
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.types.TypeSystem;
import polyglot.types.Type_c;
import polyglot.ast.ClassDecl;
import polyglot.ast.CodeDecl;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Special;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.main.Report;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;

/** This class is added for the sake of symmetry, but may not be used very much.
 * Most ..ext.x10.type.X10*Type classes actually subclass from corresponding
 * ..ext.jl.type.*Type classes, and manually add the methods to implement X10Type.
 * Only those X10*Type classes which dont may extend ts.
 * 
 * @author vj
 *
 * 
 */
public abstract class X10Type_c extends Type_c implements X10Type {
	
    protected Constraint depClause;
    protected List/*<GenParameterExpr>*/ typeParameters;
    public void setTypeParameters(List t) { typeParameters = t; }
    public void setDepClause(Constraint d) { depClause = d; }
    protected X10Type rootType = this;
    public X10Type rootType() { return rootType;}
    public boolean isParametric() { return (typeParameters == null) || ! typeParameters.isEmpty();}
    public List typeParameters() {return typeParameters;}
    public Constraint depClause() { return depClause;}
    protected X10TypeSystem xts = (X10TypeSystem) ts;
    
    private X10Type_c() { super(); }
    
    private X10Type_c(X10TypeSystem ts, Position pos) {
    	super(ts, pos);
    }
    
    // t[e/this]
    public static X10Type substituteForThis(X10Type t, final Expr e) {
    	DepParameterExpr dep = t.dep();
    	DepParameterExpr dep2 = (DepParameterExpr) dep.visit(new NodeVisitor() {
    		public Node override(Node n) {
    			if (n instanceof ClassDecl || n instanceof CodeDecl) {
    				return n;
    			}
    			if (n instanceof Special && ((Special) n).kind() == Special.THIS) {
    				return e;
    			}
    			return null;
    		}
    	});
    	return t.dep(dep2);
    }
    
    public boolean typeEqualsImpl(Type o) {
        return equalsImpl(o);
    }
    public int hashCode() {
        return 
          (rootType == this ? super.hashCode() : rootType.hashCode() ) 
        + (depClause != null ? depClause.hashCode() : 0)
        + ((typeParameters !=null && ! typeParameters.isEmpty()) ? typeParameters.hashCode() :0);
        
    }
    public  static boolean isSubtypeImpl( Type me, Type other) {
        X10Type target = (X10Type) other;
        X10Type xme = (X10Type) me;
        X10TypeSystem ts= (X10TypeSystem) xme.typeSystem();
        X10Type tb = xme.rootType(), ob = target.rootType();
        boolean result = false;
        
        result = (ts.equals(tb, ob) || ts.descendsFrom(tb, ob)) 
        && ts.entailsClause(xme, target);
        if (result) return result;
        
        if (ts.isNullable(target)) {
        	NullableType toType = toNullable(target);
        	Type baseType = toType.base();
        	result = me.isSubtypeImpl( baseType );
        	return result;
        }
        return result;
       
    }
    

    public static NullableType toNullable(X10Type me) {
        X10TypeSystem ts = (X10TypeSystem) me.typeSystem();
        return ts.isNullable(me) ? (NullableType) me : null;
    }
    public static FutureType toFuture(X10Type me) {
        X10TypeSystem ts = (X10TypeSystem) me.typeSystem();
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
  // public boolean isSubtypeImpl(Type other) {return xts.isSubtypeImpl(this, other);}
	
	
}
