/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Oct 4, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import polyglot.types.PrimitiveType_c;
import polyglot.ast.Expr;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.ast.GenParameterExpr;
import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.types.constr.C_Lit;
import polyglot.ext.x10.types.constr.C_Lit_c;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Special_c;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.ext.x10.types.constr.Failure;
import polyglot.ext.x10.types.constr.Promise;
import polyglot.main.Report;
import polyglot.types.FieldInstance;
import polyglot.types.PrimitiveType;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;

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
	protected List<X10ClassType> annotations;
	
	public List<X10ClassType> annotations() {
		if (annotations == null)
			return Collections.EMPTY_LIST;
//		if (! annotationsSet())
//			throw new MissingDependencyException(((X10Scheduler) typeSystem().extensionInfo().scheduler()).TypeObjectAnnotationsPropagated(this), false);
		return Collections.<X10ClassType>unmodifiableList(annotations);
	}
	public boolean annotationsSet() { return true || annotations != null; }
	
	public void setAnnotations(List<X10ClassType> annotations) {
		if (annotations == null) annotations = null;
		this.annotations = new ArrayList<X10ClassType>(annotations);
	}
	public X10TypeObject annotations(List<X10ClassType> annotations) {
		X10PrimitiveType_c n = (X10PrimitiveType_c) copy();
		n.setAnnotations(annotations);
		return n;
	}
	public List<X10ClassType> annotationMatching(Type t) {
		List<X10ClassType> l = new ArrayList<X10ClassType>();
		for (Iterator<X10ClassType> i = annotations().iterator(); i.hasNext(); ) {
			X10ClassType ct = i.next();
			if (ct.isSubtype(t)) {
				l.add(ct);
			}
		}
		return l;
	}
	
	/** Used for deserializing types. */
	protected X10PrimitiveType_c() { }
	public X10PrimitiveType_c(TypeSystem ts, PrimitiveType.Kind kind) {
		super(ts, kind);
	}
	
	protected Constraint depClause;
	protected List/*<GenParameterExpr>*/ typeParameters;
	protected X10Type rootType = this;
	public X10Type rootType() { return rootType;}
	public boolean isRootType() { return rootType == this;}
	public boolean isParametric() { return typeParameters != null && ! typeParameters.isEmpty();}
	public List typeParameters() { return typeParameters;}
	public Constraint depClause() { return depClause; }
	public Constraint realClause() { return depClause; }
	public boolean propertiesElaborated() { return true; }
	public boolean isConstrained() { return depClause !=null && ! depClause.valid();}
	public void setDepGen(Constraint d, List/*<GenParameterExpr>*/ l) {
		depClause = d;
		typeParameters = l;
	}
	public C_Var selfVar() {
		return depClause==null ? null : depClause.selfVar();
	}
	public void setSelfVar(C_Var v) {
		Constraint c = depClause();
		if (c==null) {
			depClause=new Constraint_c((X10TypeSystem) ts);
		}
		depClause.setSelfVar(v);
	}
	public void addBinding(C_Var t1, C_Var t2) {
		if (depClause == null)
			depClause = new Constraint_c((X10TypeSystem) ts);
		try {
			depClause = depClause.addBinding(t1, t2);
		}
		catch (Failure f) {
			throw new InternalCompilerError("Cannot bind " + t1 + " to " + t2 + ".", f);
		}
	}
	public boolean consistent() {
		return depClause== null || depClause.consistent();
	}
	public X10Type makeDepVariant(Constraint d, List<Type> l) { 
		return makeVariant(d, l);
	}
	protected transient DepParameterExpr dep;
	
	/** Build a variant of the root type, with the constraint expression. */
	public X10Type dep(DepParameterExpr dep) {
		X10PrimitiveType_c n = (X10PrimitiveType_c) copy();
		n.dep = dep;
		return n;
	}
	
	/** Get the type's constraint expression. */
	public DepParameterExpr dep() {
		return dep;
	}
	
	public X10Type makeVariant(Constraint d, List<Type> l) { 
    	// Need to pick up the typeparameters from this
    	// made, and the realClause from the root type.
    	if (d == null && (l == null || l.isEmpty())) return this;
    	X10PrimitiveType_c n = (X10PrimitiveType_c) copy();
    	n.typeParameters = (l==null || l.isEmpty())? typeParameters : l;
    	n.depClause = d;
    	return n;
    }
	
	public X10Type makeNoClauseVariant() {
		X10PrimitiveType_c n = (X10PrimitiveType_c) copy();
		n.depClause = new Constraint_c((X10TypeSystem) ts);
		n.typeParameters = typeParameters;
		n.dep = null;
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
		(rootType == this ? super.hashCode() : rootType.hashCode() ) 
		+ (depClause == null || depClause.valid()? 0 : depClause.hashCode())
		+ ((typeParameters ==null || typeParameters.isEmpty()) ? 0: typeParameters.hashCode());
		
	}
	public boolean equalsImpl(TypeObject o) {
		if (! (o instanceof X10PrimitiveType_c)) return false;
		if (! super.equalsImpl(o)) return false;
		X10PrimitiveType_c other = (X10PrimitiveType_c) o;
		return ((X10TypeSystem) typeSystem()).equivClause(this, other);
	}
	public boolean equalsWithoutClauseImpl(X10Type o) {
		if (! (o instanceof X10PrimitiveType_c)) return false;
		return super.equalsImpl(o);
	}
	
	/**
	 * Every X10 value type descends from X10.lang.Object, the base class,
	 * and implements the ValueType interface.
	 */
	public boolean descendsFromImpl(Type ancestor) {
		X10TypeSystem xts = (X10TypeSystem) ts;
		return ts.equals(ancestor, xts.X10Object()) ||
			ts.equals(ancestor, xts.value());
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
			&& xts.entailsClause(this,other);
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
		|| ts.equals(toType, xts.boxedType((X10PrimitiveType) this.makeVariant(new Constraint_c((X10TypeSystem) ts), null)));
		if (result) return result;
		if (isVoid() || toType.isVoid())
			return result = false;
		if (ts.typeEquals(this, toType))
			return result = true;
		
		// maybe toType is a BoxedPrimitive constrained
		if(xts.isBoxedType(toType)) {
			toType = (X10Type) xts.boxedTypeToPrimitiveType(toType);
		}
		
		if (isNumeric() && toType.isNumeric()) {
			Constraint rc = realClause();
			if (rc !=null) {
				Promise p = rc.lookup(C_Special_c.Self);
				if (p != null) {
					C_Term t = p.term()	;
					if (t!=null) {
						Constraint toRC = toType.realClause();
						if (toRC !=null) {
							Promise t20 = toRC.lookup(C_Special_c.Self);
							if (t20 ==null) return result=true;
							C_Term t2 = t20.term();
							return result = (t2 == null ||  t.equals(t2));
							
						}
					}
				}
			}
			return result = true;
		}
		X10Type base = rootType();
		if (base != this)
			return ((PrimitiveType) base).isCastValidImpl(toType);
		return false;
	}
	
	public void print(CodeWriter w) {
		// [IP] FIXME: is this the right thing to do here?
		w.write(super.toString());
	}

	public String toString() { 
		return toStringForDisplay();
	}

	private static String getStackTrace() {
		StringBuffer sb = new StringBuffer();
		StackTraceElement[] trace = new Throwable().getStackTrace();
		for (int i=2; i < trace.length; i++)
			sb.append("\t").append(trace[i]).append("\n");
		return sb.toString();
	}

	public String toStringForDisplay() { 
		String clause = "";
		if (depClause != null) {
			clause = depClause.toString();
		}
		
		return  
		((rootType == this) ? super.toString() : ((X10PrimitiveType_c) rootType).toString())
		+ (isParametric() ?  typeParameters.toString() : "") 
		+ (depClause == null ? "" : clause);
		
	}
	public String typeName() { 
		return  
		((rootType == this) ? super.toString() : ((X10PrimitiveType_c) rootType).toString());
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
			X10Type tb = xme.rootType();
			if (xme==tb) {
				result = super.numericConversionValidImpl(value);
				return result;
			}
			result = super.numericConversionValidImpl(value);
			if (result==false) return result;
			C_Special self = new C_Special_c(X10Special.SELF, tb);
			C_Lit val = new C_Lit_c(value, tb);
			Constraint c;
			try {
				c = Constraint_c.makeBinding(self, val, ts);
			}
			catch (Failure f) {
				// Adding binding makes real clause inconsistent.
				return result;
			}
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
	public List<FieldInstance> properties() { return Collections.EMPTY_LIST;}
	public List<FieldInstance> definedProperties() { return Collections.EMPTY_LIST;}
	public NullableType toNullable() { return X10Type_c.toNullable(this);}
	public FutureType toFuture() { return X10Type_c.toFuture(this);}
	
	/** All primitive types are safe. */
	
	public boolean safe() { return true; }

	public boolean isValue() { return true; }
	// ----------------------------- end manual mixin code from X10Type_c
}
