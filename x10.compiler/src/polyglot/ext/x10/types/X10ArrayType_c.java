/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Apr 18, 2005
 */
package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.ext.x10.types.constr.Failure;
import polyglot.types.ArrayType_c;
import polyglot.types.Named;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;

/** This is simply the X10 representation for a Java ([]) array.
 * @author Christian Grothoff
 */
public class X10ArrayType_c extends ArrayType_c implements X10ArrayType {
	
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
		if (annotations == null) annotations = Collections.EMPTY_LIST;
		this.annotations = new ArrayList<X10ClassType>(annotations);
	}
	public X10TypeObject annotations(List<X10ClassType> annotations) {
		X10TypeObject n = (X10TypeObject) copy();
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
	
	protected Constraint depClause;
	protected List/*<GenParameterExpr>*/ typeParameters;
	protected X10NamedType rootType = this;
	public X10Type rootType() { return rootType;}
	public boolean isRootType() { return rootType==this;}
	public boolean isParametric() { return typeParameters != null && !  typeParameters.isEmpty();}
	public List typeParameters() { return typeParameters;}
	public Constraint depClause() { return depClause; }
	public Constraint realClause() { return depClause; }
	public boolean isConstrained() { return depClause !=null && ! depClause.valid();}
	public void setDepGen(Constraint d, List/*<GenParameterExpr>*/ l) {
		depClause = d;
		typeParameters = l;
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
	public boolean propertiesElaborated() { return true; }
	public C_Var selfVar() { return depClause()==null ? null : depClause().selfVar();}
	public void setSelfVar(C_Var v) {
		Constraint c = depClause();
		if (c==null) {
			depClause=new Constraint_c((X10TypeSystem) ts);
		}
		depClause.setSelfVar(v);
	}
	public X10Type makeDepVariant(Constraint d, List<Type> l) { 
		return makeVariant(d, l);
	}
	
	protected transient DepParameterExpr dep;
	
	/** Build a variant of the root type, with the constraint expression. */
	public X10Type dep(DepParameterExpr dep) {
		X10ArrayType_c n = (X10ArrayType_c) copy();
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
	    	X10ArrayType_c n = (X10ArrayType_c) copy();
	    	n.typeParameters = (l==null || l.isEmpty())? typeParameters : l;
	    	n.depClause = d;
	    	return n;
	    }
	public X10Type makeNoClauseVariant() {
  		X10ArrayType_c n = (X10ArrayType_c) copy();
  		n.depClause = new Constraint_c((X10TypeSystem) ts);
  		n.typeParameters = typeParameters;
  		n.dep = null;
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
		(rootType == this ? base.hashCode() : rootType.hashCode() ) 
		+ (isConstrained() ? depClause.hashCode() : 0)
		+ (isParametric() ? typeParameters.hashCode() :0);
		
	}
	
	public boolean equalsImpl(TypeObject o) {
		//Report.report(3,"X10ArrayType_c: equals |" + this + "| and |" + o+"|");
		if (o == this) return true;
		if (! (o instanceof X10ArrayType_c)) return false;
		X10ArrayType_c other = (X10ArrayType_c) o;
		
		if ( ! base.equalsImpl(other.base)) return false;
		
		if (depClause == null && other.depClause != null && ! other.depClause.valid()) return false;
		if (depClause != null && other.depClause == null && ! depClause.valid()) return false;
		
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
	
	
	
	public List properties() { return Collections.EMPTY_LIST;}
	public List definedProperties() { return Collections.EMPTY_LIST;}
	public NullableType toNullable() { return X10Type_c.toNullable(this);}
	public FutureType toFuture() { return X10Type_c.toFuture(this);}
	public boolean safe() { return true;}
	public String toStringForDisplay() {
		return toString();
	}

	public boolean isValue() {
		return false;
	}
}
