/*
 * Created on Apr 18, 2005
 */
package polyglot.ext.x10.types;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ext.jl.types.ArrayType_c;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.types.Named;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.util.Position;

/** This is simply the X10 representation for a Java ([]) array.
 * @author Christian Grothoff
 */
public class X10ArrayType_c extends ArrayType_c implements X10ArrayType {
	
	protected Constraint depClause;
	protected List/*<GenParameterExpr>*/ typeParameters;
	protected X10NamedType baseType = this;
	public X10Type baseType() { return baseType;}
	public boolean isParametric() { return typeParameters != null && !  typeParameters.isEmpty();}
	public List typeParameters() { return typeParameters;}
	public Constraint depClause() { return depClause; }
	public Constraint realClause() { return depClause; }
	public boolean isConstrained() { return depClause !=null && ! depClause.valid();}
	public void setDepGen(Constraint d, List/*<GenParameterExpr>*/ l) {
		depClause = d;
		typeParameters = l;
	}
	public void addBinding(C_Term t1, C_Term t2) {
		if (depClause == null)
			depClause = new Constraint_c();
		depClause = depClause.addBinding(t1, t2);
	}
	public boolean consistent() {
		return depClause== null || depClause.consistent();
	}
	public C_Var selfVar() { return depClause()==null ? null : depClause().selfVar();}
	public X10Type makeVariant(Constraint d, List l) { 
		if (d == null && (l == null || l.isEmpty()))
			return this;
		X10ArrayType_c n = (X10ArrayType_c) copy();
		// n.baseType = baseType; // this may not be needed.
		n.typeParameters = (l==null || l.isEmpty())? typeParameters : l;
		n.depClause = (d==null) ? depClause.copy() : d;
		return n;
	}
	public String name() { return ((Named) base).name();}
	public String fullName() { return ((Named) base).fullName();}
	public C_Term propVal(String name) {
		return (depClause==null) ? null : depClause.find(name);
	}
	public boolean typeEqualsImpl(Type o) {
		return equalsImpl(o);
	}
	public int hashCode() {
		return 
		(baseType == this ? base.hashCode() : baseType.hashCode() ) 
		+ (isConstrained() ? depClause.hashCode() : 0)
		+ (isParametric() ? typeParameters.hashCode() :0);
		
	}
	
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
	  public boolean equalsWithoutClauseImpl(X10Type o) {
//		Report.report(3,"X10ArrayType_c: equals |" + this + "| and |" + o+"|");
			if (o == this) return true;
			if (! (o instanceof X10ArrayType_c)) return false;
			X10ArrayType_c other = (X10ArrayType_c) o;
			
			if ( ! base.equalsImpl(other.base)) return false;
			
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
	
	/*public boolean isImplicitCastValidImpl(Type toType) {
	 
	 if (! (toType instanceof X10ArrayType_c)) {
	 // toType is not an array, but this is.  Check if the array
	  // is a subtype of the toType.  This happens when toType
	   // is java.lang.Object.
	    
	    return ts.isSubtype(this,toType);
	    }
	    X10ArrayType_c other = (X10ArrayType_c) toType;
	    return ts.typeEquals(base(), other.base());
	    
	    }*/
	/**
	 * 
	 */
	public X10ArrayType_c() {super();}
	public X10ArrayType_c(TypeSystem ts, Position pos, Type base) {
		super(ts, pos, base);
	}
	
	
	// TODO: Remove this class.
	public List properties() { return Collections.EMPTY_LIST;}
	public NullableType toNullable() { return X10Type_c.toNullable(this);}
	public FutureType toFuture() { return X10Type_c.toFuture(this);}
	public boolean safe() { return true;}
	
}
