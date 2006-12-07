/*
 * Created on Oct 4, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package polyglot.ext.x10.types;

import java.util.Collections;
import java.util.List;

import polyglot.types.PrimitiveType_c;
import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.types.constr.C_Lit;
import polyglot.ext.x10.types.constr.C_Lit_c;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Special_c;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.ext.x10.types.constr.Promise;
import polyglot.main.Report;
import polyglot.types.PrimitiveType;
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
	public X10PrimitiveType_c(TypeSystem ts, PrimitiveType.Kind kind) {
		super(ts, kind);
	}
	
	protected Constraint depClause;
	protected List/*<GenParameterExpr>*/ typeParameters;
	protected X10Type baseType = this;
	public X10Type baseType() { return baseType;}
	public boolean isParametric() { return typeParameters != null && ! typeParameters.isEmpty();}
	public List typeParameters() { return typeParameters;}
	public Constraint depClause() { return depClause; }
	public Constraint realClause() { return depClause; }
	
	public boolean isConstrained() { return depClause !=null && ! depClause.valid();}
	public void setDepGen(Constraint d, List/*<GenParameterExpr>*/ l) {
		depClause = d;
		typeParameters = l;
	}
	public C_Var selfVar() {
		return depClause==null ? null : depClause.selfVar();
	}
	  public void addBinding(C_Term t1, C_Term t2) {
			if (depClause == null)
				depClause = new Constraint_c();
			depClause = depClause.addBinding(t1, t2);
		}
	    public boolean consistent() {
	    	return depClause== null || depClause.consistent();
	    }
	public X10Type makeVariant(Constraint d, List/*<GenParameterExpr>*/ l) { 
		if (d == null && (l == null || l.isEmpty())) return this;
		X10PrimitiveType_c n = (X10PrimitiveType_c) copy();
		n.typeParameters = (l==null || l.isEmpty())? typeParameters : l;
		n.depClause = (d==null) ? depClause : d;
		if (  Report.should_report("debug", 5))
			Report.report(5,"X10PrimitiveType_c.makeVariant: " 
					+ this + " creates |" + n + "| d=|" + d+"|");
		return n;
	}
	public C_Term propVal(String name) {
		return (depClause==null) ? null : depClause.find(name);
	}
	
	
	public boolean typeEqualsImpl(Type o) {
		boolean result = equalsImpl(o) && 
		((X10TypeSystem) typeSystem()).equivClause((X10Type) this, (X10Type) o);
		return result;
	}
	public int hashCode() {
		return 
		(baseType == this ? super.hashCode() : baseType.hashCode() ) 
		+ (depClause == null || depClause.valid()? 0 : depClause.hashCode())
		+ ((typeParameters ==null || typeParameters.isEmpty()) ? 0: typeParameters.hashCode());
		
	}
	public boolean equalsImpl(Object o) {
		if (! (o instanceof X10PrimitiveType_c)) return false;
		if (! super.equals(o)) return false;
		X10PrimitiveType_c other = (X10PrimitiveType_c) o;
		return ((X10TypeSystem) typeSystem()).equivClause(this, other);
	}
	public boolean equalsWithoutClauseImpl(X10Type o) {
		if (! (o instanceof X10PrimitiveType_c)) return false;
		return super.equals(o);
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
		if (false)
			Report.report(1, "PrimitiveType: is implicitcast valid this="+this+ " to " + toType);
		X10Type targetType = (X10Type) toType;
		boolean result = false;
		try {
			if (toType.isArray()) return false;
			//X10Type tb = this.baseType(), ob = ((X10Type) toType).baseType();
			result = ts.isSubtype( this, targetType);
			if (result) return result;
			
			NullableType realTarget = targetType.toNullable();
			if (realTarget != null) {
				result = ts.isSubtype( this, realTarget.base());
			}
			if (result) return result;
			X10TypeSystem xts = (X10TypeSystem) this.typeSystem();
			X10Type other = (X10Type) toType;
			result = super.isImplicitCastValidImpl(toType) 
			&& xts.entailsClause(this,other );
			return result;
		} finally {
			if (false)
				Report.report(1, "PrimitiveType: is implicitcast valid ? " + result);
		}
		
	}
	
	
	/** Returns true iff a cast from this to <code>origType</code> is valid. 
	 * Note that a cast from int to x10.compilergenerated.BoxedInteger is valid.
	 * */
	public boolean isCastValidImpl(Type origType) {
		//Report.report(1, "X10PrimitiveType_c.isCastValidImpl: " + this + " " + origType);
		X10TypeSystem xts = (X10TypeSystem) ts;
		X10Type toType = (X10Type) origType;
		NullableType nullType = toType.toNullable();
		if (nullType != null) 
			toType = nullType.base();
		boolean result = ts.equals(toType, xts.Object()) 
		|| ts.equals(toType, xts.X10Object())
		|| ts.equals(toType, xts.boxedType((X10PrimitiveType) this.makeVariant(new Constraint_c(), null)));
		if (result) return result;
		if (isVoid() || toType.isVoid())
			return result = false;
		 if (ts.typeEquals(this, toType))
			 return result = true;
		 
		 if (isNumeric() && toType.isNumeric()) {
			 Constraint rc = realClause();
			 if (rc !=null) {
				 Promise p = rc.lookup(C_Special_c.Self);
				 if (p != null) {
					 C_Term t = p.term()	;
					 if (t!=null) {
						 Constraint toRC = toType.realClause();
						 if (toRC !=null) {
							 C_Term t2 = toRC.lookup(C_Special_c.Self).term();
							 return result = (t2 == null ||  t.equals(t2));
							 
						 }
					 }
				 }
			 }
			 return result = true;
		 }
		 X10Type base = baseType();
		 if (base != this)
			 return ((PrimitiveType) base).isCastValidImpl(toType);
		 return false;
	}
	
	public String toString() { 
		if (false)
			Report.report(5,"X10PrimitiveType_c: toString |" + super.toString() + "|(#" 
					+ this.hashCode() + this.getClass() + ") typeParameters=|" + typeParameters+"|");
		return  
		((baseType == this) ? super.toString() : ((X10PrimitiveType_c) baseType).toString())
		+ (isParametric() ? "/"+"*" + typeParameters.toString() + "*"+"/"  : "") 
		+ (depClause == null ? "" :  "/"+"*"+"(:" +  depClause.toString() + ")"+"*"+"/");
		//  + "/"+"*"+"(#" + hashCode() + ")"+"*"+"/";
	}
	public String typeName() { 
		return  
		((baseType == this) ? super.toString() : ((X10PrimitiveType_c) baseType).toString());
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
	
	public boolean numericConversionValidImpl(Object value) {
		
		X10Type xme = (X10Type) this;
		X10TypeSystem ts= (X10TypeSystem) xme.typeSystem();
		boolean result = false;
		try {
			X10Type tb = xme.baseType();
			if (xme==tb) {
				result = super.numericConversionValidImpl(value);
				return result;
			}
			result = super.numericConversionValidImpl(value);
			if (result==false) return result;
			C_Special self = new C_Special_c(X10Special.SELF, tb);
			C_Lit val = new C_Lit_c(value, tb);
			Constraint c = Constraint_c.makeBinding(self,val);
			result = ts.entailsClause(xme.realClause(), c);
			return result;
		} finally {
			
		}
		
	}
	  
	/**
	 * Note that this (general) mix-in code correctly takes care of ensuring that
	 * int is a subtype of nullable int as well as x10.lang.X10Object.
	 */
	public boolean isSubtypeImpl(  Type other) { return X10Type_c.isSubtypeImpl(this, other);}
	public boolean isValueType() { return ((X10TypeSystem) typeSystem()).isValueType(this); }
	public List properties() { return Collections.EMPTY_LIST;}
	public NullableType toNullable() { return X10Type_c.toNullable(this);}
	public FutureType toFuture() { return X10Type_c.toFuture(this);}
	
	/** All primitive types are safe. */
	
	public boolean safe() { return true; }
	// ----------------------------- end manual mixin code from X10Type_c
}
