/*
 * Created on Nov 30, 2004
 */
package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import polyglot.types.ParsedClassType_c;
import polyglot.ext.x10.types.constr.C_Field_c;
import polyglot.ext.x10.types.constr.C_Here_c;
import polyglot.ext.x10.types.constr.C_Lit;
import polyglot.ext.x10.types.constr.C_Special_c;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.ext.x10.types.constr.Failure;
import polyglot.frontend.Source;
import polyglot.main.Report;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.FieldInstance;
import polyglot.types.LazyClassInitializer;
import polyglot.types.MethodInstance;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
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
	/**
	 * The realClause is the conjunction of the depClause and the depClause
	 * for the underlying base class. Each instance of this class satisfies
	 * the realClause. When checking that A(:c) is a subtype of B(:d), check
	 * that A is a subtype of B, and that the real clause of A(:c) implies d. 
	 * (The real clause of A(:c), i.e. the depClause for A, will always imply
	 * the depClause for B if A is a subtype of B.)
	 */
	protected Constraint realClause; 
	protected boolean realClauseSet = false;
	protected SemanticException realClauseInvalid= null;
	protected List/*<GenParameterExpr>*/ typeParameters;
	public void checkRealClause() throws SemanticException {
		if (realClauseInvalid!=null)
			throw realClauseInvalid;
	}
	/** Initially this points to itself. When a copy is made
	 * the value is not touched. So it continues to point to the
	 * object from which the variant was made.
	 */
	protected X10Type baseType = this;
	public X10Type baseType() { return baseType;}
	public boolean isParametric() { return typeParameters != null && ! typeParameters.isEmpty();}
	public List typeParameters() { return typeParameters;}
	public Constraint depClause() { return depClause; }
	public Constraint realClause() {
		if (! realClauseSet)
			initRealClause();
		return realClause;
		
	}
	public C_Var selfVar() {
		return depClause()==null ? null : depClause().selfVar();
	}
	private void ensureClauses() {
		Constraint rc = realClause(); // forces it to be initialized.
		if (rc == null) {
			realClause = new Constraint_c();
		}
		if (depClause == null) 
			depClause = new Constraint_c();
	}
	private boolean aPropertyIsRecursive() {
		boolean isRecursive = false;
		for (Iterator<FieldInstance> it = properties.iterator(); (!isRecursive) && it.hasNext();) {
			FieldInstance fi =  it.next();
			X10Type type = ((X10Type) fi.type());
			isRecursive = ((X10TypeSystem) ts).equalsWithoutClause(type, this) || ts.descendsFrom(type, this);
		}
		return isRecursive;
	}
	 /**
     * Set the realClause for this type. The realClause is the conjunction of the
     * depClause and the baseClause for the type -- it represents all the constraints
     * that are satisfied by an instance of this type. The baseClause is the invariant for
     * the base type. If the base type C has defined properties P1 p1, ..., Pk pk, 
     * and inherits from type B, then the baseClause for C is the baseClause for B
     * conjoined with r1[self.p1/self, self/this] && ... && rk[self.pk/self, self/this]
     * where ri is the realClause for Pi.
     * 
     * @return
     */
	private void initRealClause()  {
		// Force properties to be initialized.
		properties();
		Type type = superType();
		HashMap result = new HashMap();
		
		if (type instanceof X10Type && type != null) {
			X10Type xType = (X10Type) type;
			Constraint rs = xType.realClause();
			// no need to change self, and no occurrence of this is possible in 
			// a type's base constraint.
			if (rs != null)
				result = rs.constraints(result); 
		}
		C_Term newThis = C_Special.Self;
		boolean aPropertyIsRecursive = aPropertyIsRecursive();
		if (! aPropertyIsRecursive) {
			// add in the bindings from the property declarations.
			for (Iterator it = properties.iterator(); it.hasNext();) {
				FieldInstance fi = (FieldInstance) it.next();
				type = fi.type();
				if (type instanceof X10Type) {
					X10Type xType = (X10Type) type;
					Constraint rs = xType.realClause();
					if (rs !=null) {
						C_Term newSelf = new C_Field_c(fi, C_Special.Self);
						result = rs.constraints(result, newSelf, newThis); 
					}
				}
			}
		}
		if (! result.isEmpty()) {
			if (realClause==null) realClause = new Constraint_c();
			realClause = realClause.addBindings(result);
		}
		if (aPropertyIsRecursive) {
			Report.report(1, "X10ParsedClassType_c: This type has a recursive property.");
			// Verify that the realclause, as it stands entails the assertions of the 
			// property.
			for (Iterator<FieldInstance> it = properties.iterator();  it.hasNext();) {
				FieldInstance fi =  it.next();
				C_Var var = new C_Field_c(fi, C_Special.Self);
				if (! realClause.entailsType(var)) {
					realClauseInvalid = 
					 new SemanticException("The real clause," + realClause 
							+ " does not satisfy constraints from the property declaration " 
							+ var.type() + " " + var + ".", position());
				}
			}
		}
		if (depClause !=null) {
			if (realClause==null) realClause = new Constraint_c();
			realClause = realClause.addIn(depClause);
		}
		realClauseSet = true;
	}
	public void addBinding(C_Term t1, C_Term t2) {
		ensureClauses();
		depClause = depClause.addBinding(t1, t2);
		realClause = realClause.addBinding(t1,t2);
	}
	public boolean isConstrained() { 
		Constraint rc = realClause();
		return rc != null && ! rc.valid();}
	public void setDepGen(Constraint d, List/*<GenParameterExpr>*/ l) {
		//Report.report(1, "X10ParsedClassType_c: settingDepGen on "  + this  + "(# + this.hashCode() " +
		//		" to " + d + " " + l);
		depClause = d;
		Constraint rc = realClause();
		rc = (rc==null) ? d : rc.addIn(d);
		typeParameters = l;
	}
	public boolean consistent() {
		return realClause().consistent();
	}
	public X10ParsedClassType makeVariant() {
		return (X10ParsedClassType) makeVariant(new Constraint_c(), null);
	}
	public X10ParsedClassType makeVariant(Constraint c) {
		return (X10ParsedClassType) makeVariant(c, null);
	}
	
	public X10Type makeVariant(Constraint d, List/*<GenParameterExpr>*/ l) { 
		//assert (d!=null || (l !=null && ! l.isEmpty()));
		 if (d == null && (l == null || l.isEmpty())) return this;
		X10ParsedClassType_c n = (X10ParsedClassType_c) copy();
		n.typeParameters = (l==null || l.isEmpty())? typeParameters : l;
		if (d == null) {
			n.depClause = depClause==null ? depClause : depClause.copy();
			Constraint rc = realClause();
			n.realClause = rc==null? null : rc.copy();
			n.realClauseSet = true;
		} else {
			n.depClause =  d;
			Constraint rc = baseType.realClause();
			n.realClause = rc==null ? n.depClause : rc.copy().addIn(d);
			n.realClauseSet = true;
		}
	
		// hack, forced by decision to explicitly represent dist/region/array type logic.
		n.isDistSet = n.isRankSet = n.isOnePlaceSet = n.isRailSet = n.isSelfSet
		= n.isX10ArraySet = n.isZeroBasedSet = false;
		
		return n;
	}
	public C_Term propVal(String name) {
		return (realClause==null) ? null : realClause.find(name);
	}
	
	public boolean typeEqualsImpl(Type o) {
		return equalsImpl(o);
	}
	public int hashCode() {
		return 
		(baseType == this ? super.hashCode() : baseType.hashCode() ) 
	//	+ (isConstrained() ? depClause.hashCode() : 0)
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
			//Report.report(5, "X10ParsedClassTypes_c: ... |" + m + "|:");
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
		return (baseType == this ? mySuperType()
				: ((X10ParsedClassType_c) baseType).superType());
	}
	
	public Type mySuperType() {
		
		init.initSuperclass();
		X10TypeSystem xts = (X10TypeSystem) typeSystem();
		if (xts.equals(superType, xts.Object()) 
				&& ! (xts.equals(this, xts.X10Object()))
				&& ! flags().isInterface()
				&& ! toString().startsWith("java.")
				&& ! toString().equals("x10.compilergenerated.Parameter1")) {
			//Report.report(1, "X10ParsedClass: setting supertype of |" +  this + "| to  x10.lang.Object.");
			superType=xts.X10Object();
		}
		
		return superType;
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
	
	public String toStringUnused() { 
		if (false)
			Report.report(5,"X10ParsedClassType: toString |" + super.toString() + "|(#" 
					+ this.hashCode() + ") baseType = " + ( baseType.toString()) + " dep=" + depClause);
		return  
		((baseType == this) ? super.toString() : ((X10ParsedClassType_c) baseType).toString())
		+ (isParametric() ? "/"+"*T" + typeParameters.toString() + "*"+"/"  : "") 
		
		+ (depClause == null ? "" :  "/"+"*"+"(:" +  depClause.toString() + ")"+"*"+"/");
		//  + "/"+"*"+"(#" + hashCode() + ")"+"*"+"/";
	}
	public String toString() { 
		
		return  
		((baseType == this) ? super.toString() : ((X10ParsedClassType_c) baseType).toString())
		// vj: this causes problems. a type parameter may be nullable which produces a commented string.
		//+ (isParametric() ? "/"+"*T"+ typeParameters.toString() +"*"+"/" : "") 
		+ (depClause == null ? "" : "/"+"*"+"(:" +  depClause.toString() + ")"+"*"+"/");
	}
	
	
	public boolean equalsImpl(TypeObject toType) {
		X10Type other = (X10Type) toType;
		X10TypeSystem xts = (X10TypeSystem) ts;
		X10Type tb = this.baseType(), ob = other.baseType();
		boolean result = ((tb==this) ? super.equalsImpl(ob): tb.equalsImpl(ob))
				&& xts.equivClause(this, other);
		return result;
		
	}
	public boolean equalsWithoutClauseImpl(X10Type other) {
		X10TypeSystem xts = (X10TypeSystem) ts;
		X10Type tb = this.baseType(), ob = other.baseType();
		boolean result = ((tb==this) ? super.equalsImpl(ob): tb.equalsWithoutClauseImpl(ob));
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
	
	List<FieldInstance> properties = null;
	
	public List properties() {
		//Report.report(1, "X10ParsedClassType_c entering properties() on "  + this);
		if (properties != null) 
			return properties;
		init.canonicalFields();
		init.initSuperclass();
		FieldInstance fi = fieldNamed(X10FieldInstance.MAGIC_PROPERTY_NAME);
		//Report.report(1, "X10ParsedClassType_c found " + fi+ " for " + this);
	 
		if (fi == null) {
			if ( Report.should_report(Report.types, 2))
				Report.report(2, "Type " + name + " has no properties.");
			properties = Collections.EMPTY_LIST;
			return properties;
		}
		
		String propertyNames = (String) fi.constantValue();
		
		properties = new ArrayList();
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
		if (  Report.should_report(Report.types, 2))
			Report.report(2, "Type " + name + " has properties " + properties +".");
		return properties;
		
	}
	public NullableType toNullable() { return X10Type_c.toNullable(this);}
	public FutureType toFuture() { return X10Type_c.toFuture(this);}
	
	boolean isX10Array;
	boolean isX10ArraySet;
	public boolean isX10Array() {
		if (isX10ArraySet) return isX10Array;
		isX10ArraySet = true;
		
		return isX10Array=((X10TypeSystem) typeSystem()).isX10Array(this);
	}
	
	boolean isRect;
	boolean isRectSet;
	public boolean isRect() {
		if (isRectSet) return isRect;
		isRectSet = true;
		Constraint c = realClause();
		return isRect= c==null ? false : amIProperty("rect");
	}
	public void setRect() {
		setProperty("rect");
		isRect = isRectSet = true;
	}
	
	C_Term onePlace;
	boolean isOnePlaceSet;
	public C_Term onePlace() {
		if (isOnePlaceSet) return onePlace;
		isOnePlaceSet = true;
		Constraint c = realClause();
		return onePlace= c==null ? null : c.find("onePlace");
	}
	public void setOnePlace(C_Term onePlace) {
		isOnePlaceSet=true;
		this.onePlace = onePlace;
		setProperty("onePlace", onePlace);
		//Report.report(1, "X10ParsedClassType " + depClause);
	}
	public boolean hasLocalProperty() {
		C_Term onePlace = onePlace();
		return onePlace instanceof C_Here_c;
	}
	
	boolean isZeroBased;
	boolean isZeroBasedSet;
	public boolean isZeroBased() {
		//Report.report(1, "X10ParsedClassType_c: isZerobased" + isZeroBasedSet + " " + isZeroBased);
		if (isZeroBasedSet) return isZeroBased;
		
		Constraint c = realClause();
		isZeroBased= c==null ? false : amIProperty("zeroBased");
		//Report.report(1, "X10ParsedClassType_c: isZerobased set to " + isZeroBased);
		isZeroBasedSet = true;
		return isZeroBased;
		
	}
	public void setZeroBased() {
		setProperty("zeroBased");
		isZeroBased=isZeroBasedSet = true;
	}
	
	boolean isRail;
	boolean isRailSet;
	public boolean isRail() {
		if (isRailSet) return isRail;
		isRailSet = true;
		Constraint c = realClause();
		return isRail=c == null? false : isX10Array() && amIProperty("zeroBased");
	}
	public void setRail() {
		setProperty("rail");
		isRail = isRailSet = true;
	}
	
	boolean isRankSet;
	C_Term rank;
	public C_Term rank() {
		
		if (isRankSet) return rank;
		
		Constraint c = realClause();
		rank = c==null? null : c.find("rank");
		/*if (c == null) {
			isRankSet = true;
			return rank = null;
		}
		rank = c.find("rank");*/
		if (rank == null && c!= null) {
			// build the synthetic term.
			C_Var var = c.selfVar();
			if (var !=null) {
				FieldInstance fi = definedFieldNamed("rank");
				//Report.report(1, "X10ParsedClassType: rank is " + rank + " var.type is " + var.type());
				rank = new C_Field_c(fi, var);
			}
		}
		//Report.report(1, "X1ParsedClassType rank of " + this + " is " + rank);
		isRankSet = true;
		return rank;
	}
	public void setRank(C_Term rank) {
		assert(rank !=null);
		setProperty("rank", rank);
		isRankSet=true;
		this.rank = rank;
	}
	
	public boolean isRankOne() {
		return C_Lit.ONE.equals(rank());
	}
	public boolean isRankTwo() {
		return C_Lit.TWO.equals(rank());
	}
	public boolean isRankThree() {
		return C_Lit.THREE.equals(rank());
	}
	boolean isDistSet;
	C_Term dist;
	public C_Term distribution() {
		if (isDistSet) return dist;
		
		Constraint c = realClause();
		if (c == null)
			return dist = null;
		dist = c.find("distribution");
		if (dist == null) {
			// build the synthetic term.
			C_Var var = c.selfVar();
			if (var !=null) {
				FieldInstance fi = definedFieldNamed("distribution");
				
				dist = new C_Field_c(fi, var);
			}
		}
		isDistSet = true;
		//Report.report(1, "X1ParsedClassType dist is " + rank);
		C_Term result = dist;
		return result;
	}
	public void setDistribution(C_Term dist) {
		setProperty("distribution", dist);
		isDistSet=true;
		this.dist = dist;
	}
	C_Term self;
	boolean isSelfSet;
	public C_Term self() {
		if (isSelfSet) return self;
		Constraint c = realClause();
		if (c == null) return self=null;
		self = c.find("self");
		if (self == null) {
			// build the synthetic term.
			self = c.selfVar();
		}
		return self;
	}
	/** Set the value of this property on the constraints of this type. Should be called
	 * only within code that is transferring properties to this type from 
	 * properties of referenced types, e.g. an array is zeroBased if its region is. 
	 * 
	 * */
	protected void setProperty(String propName) {
		setProperty(propName, C_Lit.TRUE);
	}
	
	protected void setProperty(String propName, C_Term val)  {
		X10FieldInstance fi = definedFieldNamed(propName);
		//Report.report(1, "X10Parsedclass.setting property " + propName + " on " + this + "found fi=" + fi);
		if (fi != null &&  fi.isProperty()) {
			C_Var var = new C_Field_c(fi, C_Special.Self);
			addBinding(var, val);
		}
	}
	
	protected X10FieldInstance definedFieldNamed(String name) {
		ReferenceType x = this;
		
		X10FieldInstance fi = (X10FieldInstance) fieldNamed(name);
		while (fi == null && ! (x.equals(ts.Object()))) {
			x = (ReferenceType) x.superType();
			fi = (X10FieldInstance) x.fieldNamed(name);
		}
		return fi;
	}
	protected boolean amIProperty(String propName) {
		boolean result = false;
		try {
			X10FieldInstance fi = (X10FieldInstance) definedFieldNamed(propName);
			if (fi != null &&  fi.isProperty()) {
				C_Term term = new C_Field_c(fi, C_Special.Self);
				Constraint c = new Constraint_c();
				c.addTerm(term);
				return result = realClause().entails(c);
			}
			
		} catch (SemanticException z) {}
		
		return result;
	}
	boolean fieldsInitialized = false;
	public List fields() {
		// Report.report(1, "***X10ParsedClassTypes fields() invoked on " + this + " " + init.getClass());
		if (fieldsInitialized) 
			return fields;
		try {
			init.initFields();
			init.canonicalFields();
			// Report.report(1, "X10ParsedClassTypes " + this + ".fields() returning " + fields);
			fieldsInitialized=true;
			return Collections.unmodifiableList(fields);
		} catch (Throwable z) {
			//Report.report(1, "X10ParsedClassTypes caught  " + z);
		}
		return  Collections.unmodifiableList(fields);
	}
	
	
}

