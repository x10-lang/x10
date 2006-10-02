/*
 * Created on Nov 30, 2004
 */
package polyglot.ext.x10.types;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import polyglot.ext.jl.types.ParsedClassType_c;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.frontend.Source;
import polyglot.main.Report;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.FieldInstance;
import polyglot.types.LazyClassInitializer;
import polyglot.types.MethodInstance;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;

/** 6/2006 Modified so that every type is now potentially generic and dependent.
 * @author vj
 */
public class X10ParsedClassType_c extends ParsedClassType_c
implements X10ParsedClassType
{
	
	public X10ParsedClassType_c() { super();}
	
	/**
	 * @param ts
	 * @param init
	 * @param fromSource
	 */
	public X10ParsedClassType_c(TypeSystem ts,
			LazyClassInitializer init,
			Source fromSource)
	{
		super(ts, init, fromSource);
		
	}
	
	/** An instance of X10ParsedClassType_c obtained by reading a code
	 ** file defining a class C is said to tbe base representation of
	 ** C. X10 supports the creation of variants of the type obtained
	 ** by parsing C; these variants are obtained by instantiating
	 ** type parameters or by supplying dependent clauses. Each
	 ** variant is represented in the type system by an instance of
	 ** X10ClassType_c (with the appropriate fields set). In the
	 ** field baseClass we record the instance of X10ParsedClassType_c
	 ** obtained by reading the code from a file. This instance is
	 ** guaranteed to have a null depClaus and an empty list in the
	 ** typeParameters field.
	 
	 ** Now we can determine if two instances of X10ParsedClassType_c
	 ** correspond to the same base Class by simply == checking their
	 ** baseClass fields.
	 
	 */  
	protected Constraint depClause;
	protected List/*<GenParameterExpr>*/ typeParameters;
	protected X10Type baseType = this;
	public X10Type baseType() { return baseType;}
	public boolean isParametric() { return typeParameters != null && ! typeParameters.isEmpty();}
	public List typeParameters() { return typeParameters;}
	public Constraint depClause() { return depClause; }
	public boolean isConstrained() { return depClause != null && ! depClause.valid();}
	public X10Type makeVariant(Constraint d, List/*<GenParameterExpr>*/ l) { 
		if (d == null && (l == null || l.isEmpty())) return this;
		X10ParsedClassType_c n = (X10ParsedClassType_c) copy();
		n.typeParameters = l;
		n.depClause = d;
		return n;
	}
	public C_Term propVal(String name) {
		return (depClause==null) ? null : depClause.find(name);
	}
	
	public boolean typeEqualsImpl(Type o) {
		return equalsImpl(o);
	}
	public int hashCode() {
		return 
		(baseType == this ? super.hashCode() : baseType.hashCode() ) 
		+ (isConstrained() ? depClause.hashCode() : 0)
		+ (isParametric() ? typeParameters.hashCode() :0);
		
	}

	public boolean isCanonical() {
		boolean result =true;
		if (typeParameters != null) {
			Iterator it = typeParameters.iterator();
			while (it.hasNext() && result) {
				Type t = (Type) it.next();
				result &= t.isCanonical();
			}
		}
		return result;
		
	}    
	private Type translateType(Type t) {
		return t;
	}
	/**
	 * TODO: vj Fix major bug. Cannot make copies of FieldInstances. These are destructively modified in place
	 * to record whether they have constant values or not.
	 * 6/21/06. Hmm. This code should be triggered with what we have in CVS now if we have an array class
	 * with a field of type Parameter1.
	 * @param i
	 * @return
	 */
	private FieldInstance translateTypes(FieldInstance i) {
		return i;
		/* FieldInstance fi = (FieldInstance)i.copy();
		 fi.setType(translateType(i.type()));
		 fi.container(this);
		 return fi;*/
	}
	private MethodInstance translateTypes(MethodInstance i) {
		MethodInstance mi = (MethodInstance)i.copy();
		List formals = mi.formalTypes();
		List nformals = new LinkedList();
		Iterator it = formals.iterator();
		while (it.hasNext())
			nformals.add(translateType((Type)it.next()));
		mi.formalTypes(nformals);
		mi.returnType(translateType(i.returnType()));
		mi.container(this);
		return mi;
	}
	private ConstructorInstance translateTypes(ConstructorInstance i) {
		ConstructorInstance ci = (ConstructorInstance)i.copy();
		List formals = ci.formalTypes();
		List nformals = new LinkedList();
		Iterator it = formals.iterator();
		while (it.hasNext())
			nformals.add(translateType((Type)it.next()));
		ci.formalTypes(nformals);
		ci.container(this);
		return ci;
	}
	
	/* (non-Javadoc)
	 * @see polyglot.types.ReferenceType#fields()
	 */
	/*public List fields() {
	 List il = super.fields();
	 List lo = new LinkedList();
	 Iterator it = il.iterator();
	 while (it.hasNext()) {
	 FieldInstance ci = (FieldInstance) it.next();
	 lo.add(translateTypes(ci));
	 }
	 return lo;
	 }*/
	
	
	/**
	 * The class's constructors.
	 * A list of <code>ConstructorInstance</code>.
	 * @see polyglot.types.ConstructorInstance
	 */
	/* public List constructors() {
	 List il=super.constructors();
	 Iterator it = il.iterator();
	 while (it.hasNext()) {
	 Report.report(2, "[X10ParsedClassType] "  + it.next());
	 
	 }
	 return il;
	 /*List il = super.constructors();
	  List lo = new LinkedList();
	  Iterator it = il.iterator();
	  while (it.hasNext()) {
	  ConstructorInstance ci = (ConstructorInstance) it.next();
	  lo.add(translateTypes(ci));
	  }
	  return lo;}*/
	
	
	/** Get a field by name, or null. */
	public FieldInstance fieldNamed(String name) {
		FieldInstance fi = super.fieldNamed(name);
		if (fi == null)
			return null;
		return translateTypes(fi);
	}
	
	/* (non-Javadoc)
	 * @see polyglot.types.ReferenceType#methods()
	 */
	public List methods() {
		//Report.report(5, "X10ParsedClassTypes_c: methods in | (#" + this + ")|:");
		
		List methods = super.methods();
		List lo = new LinkedList();
		Iterator it = methods.iterator();
		while (it.hasNext()) {
			MethodInstance ci = (MethodInstance) it.next();
			MethodInstance m = translateTypes(ci);
			lo.add(m);
			// Report.report(5, "X10ParsedClassTypes_c: ... |" + m + "|:");
		}
		
		return lo;
	}
	
	/**
	 * The class's member classes.
	 * A list of <code>ClassType</code>.
	 * @see polyglot.types.ClassType
	 */
	public List memberClasses() {
		List bl = super.memberClasses();
		List bo = new LinkedList();
		Iterator it = bl.iterator();
		while (it.hasNext()) {
			X10ClassType ct = 
				(X10ClassType) ((X10ClassType) it.next()).makeVariant(null, typeParameters);
			
			// TODO: Figure out how to set the dependentClause;.
			bo.add(ct);
		}
		return bo;
	}
	
	/**
	 * Returns the member class with the given name, or null.
	 *
	 * Note that I'm not sure that this is what is supposed to
	 * happen in all cases (does a static inner class of a parametric
	 * type inherit the parameters of the outer class? always?
	 * should we check? how to check? what does the parser do?
	 * what about non-static?).  However, this may also just work
	 * perfectly as-is.
	 */
	/*  public ClassType memberClassNamed(String name) {
	 ClassType ct = super.memberClassNamed(name);
	 if (ct == null) return null;
	 X10ClassType result = (X10ClassType) ct.copy();
	 result.setTypeParameters(typeParameters);
	 result.setDepClause(depClause);
	 return result;
	 }*/
	
	/**
	 * This method probably does not currently work correctly if
	 * the we do something like
	 *
	 * class ParametricType<T> extends ParentType<T> {};
	 *
	 * However, that would require some additional support
	 * by the parser, too.
	 *
	 * @see polyglot.types.ReferenceType#superType()
	 */
	public Type superType() {
		// FIXME: parent class may use parameters (extends Base<T>)
		// that we need to instantiate (Base<Foo>)
		//  if (toString().startsWith("x10.lang.GenericReferenceArray"))
		//    Report.report(3, "X10ParsedClassType.superType " + this + "(#" + this.hashCode() + ") " + this.getClass() + " is |" + super.superType() + "|");
		return (baseType == this ? super.superType() : ((X10ParsedClassType_c) baseType).superType());
	}
	
	/**
	 * This method probably does not currently work correctly if
	 * the we do something like
	 *
	 * class ParametricType<T> implements MyInterface<T> {};
	 *
	 * However, that would require some additional support
	 * by the parser, too.
	 *
	 * @see polyglot.types.ReferenceType#interfaces()
	 */
	public List interfaces() {
		// FIXME: interfaces may use parameters (implements List<T>)
		// that we need to instantiate (List<Foo>)
		return super.interfaces();
	}
	
	/**
	 * Simple name of the type object. Anonymous classes do not have names.
	 *
	 * Q: does this have any semantics? Should (must??) we add the
	 * type parameters and dependent expressions here?
	 */
	/*  public String name() {
	 return super.name() + (typeParameters == null || typeParameters.isEmpty() ? "" : "_GENERIC");
	 }*/
	
	// Uncomment the method below for debugging only. The output will confuse the post compiler (javac).
	
	public String toString() { 
		if (false)
			Report.report(5,"X10ParsedClassType: toString |" + super.toString() + "|(#" 
					+ this.hashCode() + this.getClass() + ") typeParameters=|" + typeParameters+"|");
		return  
		((baseType == this) ? super.toString() : ((X10ParsedClassType_c) baseType).toString())
		+ (isParametric() ? "/"+"*" + typeParameters.toString() + "*"+"/"  : "") 
		+ (depClause == null ? "" :  "/"+"*"+"(:" +  depClause.toString() + ")"+"*"+"/");
		//  + "/"+"*"+"(#" + hashCode() + ")"+"*"+"/";
	}
	
	
	public boolean equalsImpl(TypeObject toType) {
		X10Type other = (X10Type) toType;
		X10TypeSystem xts = (X10TypeSystem) ts;
		X10Type tb = this.baseType(), ob = other.baseType();
		boolean result = ((tb==this) ? super.equalsImpl(ob): tb.equalsImpl(ob))
				&& xts.equivClause(this, other);
		return result;
		
	}
	public X10ClassType superClassRoot() {
		X10TypeSystem xt = (X10TypeSystem) typeSystem();
		X10ClassType x10LangObj = (X10ClassType) xt.X10Object();
		X10ClassType result = this;
		X10ClassType next = (X10ClassType) result.superType();
		while (next != null   ) {
			boolean value = xt.equals(x10LangObj, result);
			if (value)
				return x10LangObj;
			result=next;
			next=(X10ClassType) result.superType();
		}
		return result;
	}
	
	public boolean isJavaType() {
		TypeSystem ts = typeSystem();
		return ts.equals(superClassRoot(), ts.Object());
	}
	/**
	 * A parsed class is safe iff it explicitly has a flag saying so.
	 */
	public boolean safe() {
		return X10Flags.toX10Flags(flags()).isSafe();
		
	}
	
	
	public boolean isSubtypeImpl(Type toType ) {
	  
		X10Type other = (X10Type) toType;
		X10TypeSystem xts = (X10TypeSystem) ts;
		X10Type tb = this.baseType(), ob = other.baseType();
		boolean result = (ts.typeEquals(tb,ob) || ts.descendsFrom(tb,ob)) &&
			xts.entailsClause(this, other);
		return result;
	}
	
	public boolean descendsFromImpl(Type toType ) {
		X10Type other = (X10Type) toType;
		X10TypeSystem xts = (X10TypeSystem) ts;
		X10Type tb = this.baseType(), ob = other.baseType();
		boolean result = (tb==this ? super.descendsFromImpl(ob) : tb.descendsFromImpl(ob)) 
		&& xts.entailsClause(this, other);
		return result;
	}
	public boolean isImplicitCastValidImpl(Type toType) {
		boolean result = true;
		
			if (toType.isArray()) return false;
			X10Type targetType = (X10Type) toType;
			NullableType realTarget = targetType.toNullable();
			result = ts.isSubtype( this, targetType);
			
			if (result) return result;
			
			if (realTarget != null) {
				result = ts.isSubtype( this, realTarget.base());
			}
			
			return result;
		
	}
	
	/** Returns true iff a cast from this to <code>toType</code> is valid. */
	public boolean isCastValidImpl(Type toType) {
		X10TypeSystem xts = (X10TypeSystem) ts;
		X10Type targetType = (X10Type) toType;
		
		boolean result = (toType.isPrimitive() && ts.isCastValid(xts.X10Object(), this));
		if (result) return result;
		NullableType type = targetType.toNullable();
		if (type !=null) return isCastValidImpl(type.base());
		
		FutureType f = targetType.toFuture();
		if (f !=null) {
			// If we can cast the Future into this type, we can do the reverse
			return targetType.isCastValidImpl(this);
		}
		result = super.isCastValidImpl(toType);
		return result;
	}
	
	
	
	List properties = null;
	public List properties() {
		if (properties != null) 
			return properties;
		init.canonicalFields();
		FieldInstance fi = fieldNamed("propertyNames$");
		if (fi == null) {
			if (Report.should_report(Report.types, 2))
				Report.report(2, "Type " + name + " has no properties.");
			properties = Collections.EMPTY_LIST;
			return properties;
		}
		String propertyNames = (String) fi.constantValue();
		if (fi == null)
			throw new InternalCompilerError("The synthetic field propertyNames$ " 
					+ " has not been initialized for type " + name); 
		properties = Collections.EMPTY_LIST;
		Scanner s = new Scanner(propertyNames);
		while (s.hasNext()) {
			String propName = s.next();
			FieldInstance prop = fieldNamed(propName);
			if (prop == null) 
				throw new InternalCompilerError("Type " 
						+ name + " has no property named " + propName); 
			properties.add(prop);
		}
		if (superType != null) 
			properties.addAll(((X10Type) superType).properties());
		if (true || Report.should_report(Report.types, 2))
			Report.report(2, "Type " + name + " has properties " + properties +".");
		return properties;
		
	}
	public NullableType toNullable() { return X10Type_c.toNullable(this);}
	public FutureType toFuture() { return X10Type_c.toFuture(this);}
	
}

