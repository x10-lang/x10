/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;

import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.types.ClassDef;
import polyglot.types.ClassDef_c;
import polyglot.types.ClassType;
import polyglot.types.CodeInstance;
import polyglot.types.ConstructorDef;
import polyglot.types.ContainerType;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.LazyRef_c;
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.Package;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.FilteringList;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.Predicate;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import polyglot.util.TypedList;
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.types.constraints.CConstraint;
import x10.types.constraints.ConstraintManager;
import x10.types.constraints.TypeConstraint;
import x10.types.constraints.XConstrainedTerm;


public class X10ClassDef_c extends ClassDef_c implements X10ClassDef {
    protected transient Source fromSource;

    protected Ref<? extends Type> superType;
    protected List<Ref<? extends Type>> interfaces;
    protected List<MethodDef> methods;
    protected List<FieldDef> fields;
    protected List<ConstructorDef> constructors;
    protected Ref<? extends Package> package_;
    protected Flags flags;
    protected Kind kind;
    protected Name name;
    protected Ref<X10ClassDef> outer;
    protected List<Ref<? extends ClassType>> memberClasses;
    protected transient ClassType asType;
    
	private static final long serialVersionUID = -4644427081636650171L;

    protected List<ParameterType.Variance> variances;
    
    public X10ClassDef_c(TypeSystem ts, Source fromSource) {
        super(ts, fromSource);
        this.fromSource = fromSource;
        this.interfaces = new ArrayList<Ref<? extends Type>>();
        this.methods = new ArrayList<MethodDef>();
        this.fields = new ArrayList<FieldDef>();
        this.constructors = new ArrayList<ConstructorDef>();
        this.memberClasses = new ArrayList<Ref<? extends ClassType>>();
        
        sourceKind = fromSource != null ? SourceKind.SOURCE : SourceKind.UNKNOWN;
        
        this.variances = new ArrayList<ParameterType.Variance>();
        this.typeParameters = new ArrayList<ParameterType>();
        this.typeMembers = new ArrayList<TypeDef>();
        this.thisDef = null;
    }
    
    public XVar thisVar() {
        if (this.thisDef != null)
            return this.thisDef.thisVar();
        return ConstraintManager.getConstraintSystem().makeThis(); // Why #this instead of this?
    }

    ThisDef thisDef;

    public ThisDef thisDef() {
        return this.thisDef;
    }

    public void setThisDef(ThisDef thisDef) {
        this.thisDef = thisDef;
    }

    // BEGIN ANNOTATION MIXIN
    List<Ref<? extends Type>> annotations;

    public List<Ref<? extends Type>> defAnnotations() {
	if (annotations == null)
	    return Collections.<Ref<? extends Type>>emptyList();
        return Collections.unmodifiableList(annotations);
    }
    
    public void setDefAnnotations(List<Ref<? extends Type>> annotations) {
        this.annotations = TypedList.<Ref<? extends Type>>copyAndCheck(annotations, Ref.class, true);
    }
    
    public List<Type> annotations() {
        return X10TypeObjectMixin.annotations(this);
    }
    
    public List<Type> annotationsMatching(Type t) {
        return X10TypeObjectMixin.annotationsMatching(this, t);
    }
    
    public List<Type> annotationsNamed(QName fullName) {
        return X10TypeObjectMixin.annotationsNamed(this, fullName);
    }
    
    // END ANNOTATION MIXIN

    Ref<TypeConstraint> typeBounds;
    
    public Ref<TypeConstraint> typeBounds() {
        return typeBounds;
    }

    public void setTypeBounds(Ref<TypeConstraint> c) {
        this.typeBounds = c;
    }

    protected Ref<CConstraint> classInvariant; 

    public void setClassInvariant(Ref<CConstraint> c) {
        this.classInvariant = c;
    }

    /**
     * Return the explicitly declared class invariant. If you want to get
     * all the statically known information about instances of this class, then
     * use the getRealClause().
     */
    public Ref<CConstraint> classInvariant() {
        return classInvariant;
    }
    
    // Cached realClause of the root type.
    protected SemanticException realClauseInvalid;
    public void checkRealClause() throws SemanticException {
    	getRealClause(); // force the computation.
    	if (realClauseInvalid != null)
    		throw realClauseInvalid;
    }
    
    // Cached realClause of the root type.
    Ref<CConstraint> realClause = setRealClause();
    Ref<CConstraint> realClauseWithThis = setRealClauseWithThis();
    
    private Ref<CConstraint> setRealClauseWithThis() {
        final LazyRef<CConstraint> ref = new LazyRef_c<CConstraint>(ConstraintManager.getConstraintSystem().makeCConstraint());
        final Runnable runnable = new Runnable() {
            public void run() {
                CConstraint c = X10ClassDef_c.this.realClause.get();
                c = c.instantiateSelf(thisVar());
                ref.update(c);
            }
            public String toString() {
                return "realClauseWithThis for " + X10ClassDef_c.this;
            }
        };
        ref.setResolver(runnable);
        return ref;
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
     */
    private Ref<CConstraint> setRealClause() {
    	final LazyRef<CConstraint> ref = new LazyRef_c<CConstraint>(ConstraintManager.getConstraintSystem().makeCConstraint());
    	Runnable runnable = new Runnable() {
    		boolean computing = false;
    		public void run() {
    			if (computing) {
    				return;
    		    }
    		    computing = true;
    		    CConstraint result = ConstraintManager.getConstraintSystem().makeCConstraint();
    		    try {
    			    List<X10FieldDef> properties = properties();
    			    XVar oldThis = thisVar(); // xts.xtypeTranslator().translateThisWithoutTypeConstraint();
    			    
    			    try {
    				    // Add in constraints from the supertypes.  This is
    				    // no need to change self, and no occurrence of this is possible in 
    				    // a type's base constraint.
    			    	// vj: 08/12/09. Incorrect. this can occur in a type's base constraint.
    				    {
    					    Type type = Types.get(superType());
    					    if (type != null) {
    						CConstraint rs = Types.realX(type);
    						if (rs != null && ! rs.valid()) {
    							if (rs.thisVar() != null)
    								rs = rs.substitute(oldThis, (XVar) rs.thisVar());
    						    result.addIn(rs);
    						}
    					    }
    				    }

    				    // Add in constraints from the interfaces.
    				    for (Iterator<Ref<? extends Type>> i = interfaces().iterator(); i.hasNext(); ) {
    					    Ref<? extends Type> it = (Ref<? extends Type>) i.next();
    					    CConstraint rs = Types.realX(it.get());
    					    // no need to change self, and no occurrence of this is possible in 
    					    // a type's base constraint.
    					    if (rs != null && ! rs.valid()) {
//    						    rs = rs.substitute(rs.self(), oldThis);
    						    result.addIn(rs);
    					    }
    				    }
    				    
    				    // add in the bindings from the property declarations.
    				    for (X10FieldDef fi : properties) {
    					    Type type = fi.asInstance().type();   // ### check for recursive call here
    					    XVar fiThis = fi.thisVar();
    					    CConstraint rs = Types.realX(type);
    					    if (rs != null) {
    						    // Given: f:C{c}
    						    // Add in: c[self.f/self,self/this]
    						    XTerm newSelf = ts.xtypeTranslator().translate(result.self(), fi.asInstance());
    						    CConstraint rs1 = rs.instantiateSelf(newSelf);
    						    CConstraint rs2;
    						    if (fiThis != null)
    						        rs2 = rs1.substitute(result.self(), fiThis);
    						    else
    						        rs2 = rs1;
    						    result.addIn(rs2);
    					    }
    				    }

    				    // Finally, add in the class invariant.
    				    // It is important to do this last since we need avoid type-checking constraints
    				    // until after the base type of the supertypes are resolved.
    				    XVar thisVar = thisVar();
    				    CConstraint ci = Types.get(classInvariant);
    				    if (ci != null && ! ci.valid()) {
    				    	ci = ci.substitute(ci.self(), oldThis);
    				    	result.addIn(ci);
    				    }
    				    
    			    }
    			    catch (XFailure f) {
    				    result.setInconsistent();
    				    X10ClassDef_c.this.realClauseInvalid = 
    				    	new SemanticException("The real clause for " 
    				    			+ this + " is inconsistent.", position());
    			    }
    			    
    			    // Now verify that the root clause entails the assertions of the properties.
    			    // We need to set the root clause first, to avoid a spurious report of a cyclic dependency.
    			    // This can happen when one of the properties is subtype of this type:
    			    // class Ref(home: Place) { }
    			    // class Place extends Ref { ... }
    			    
    			    // Disable this for now since it can cause an infinite loop.
    			    // TODO: vj 08/12/09 Revisit this.
    			    /*if (false && result.consistent()) {
    				    // Verify that the realclause, as it stands, entails the assertions of the 
    				    // property.
    				    for (X10FieldDef fi : properties) {
    					    Type ftype = fi.asInstance().type();
    					    
    					    CConstraint c = X10TypeMixin.realX(ftype);
    					    XTerm newSelf = xts.xtypeTranslator().trans(c, c.self(), fi.asInstance());
    					    c = c.substitute(newSelf, c.self());
    					    
    					    if (! result.entails(c, ((X10Context) ts.emptyContext()).constraintProjection(result, c))) {
    						    this.rootClause = Types.ref(result);
    						    this.rootClauseInvalid = 
    							    new SemanticException("The real clause, " + result + ", does not satisfy constraints from " + fi + ".", position());
    					    }
    				    }
    			    }*/
    		    }
    		  /*  catch (XFailure e) {
    		    	CConstraint result = ConstraintManager.getConstraintSystem().makeCConstraint();
    			    result.setInconsistent();
    			    this.rootClause = Types.ref(result);
    			    this.rootClauseInvalid = new SemanticException(e.getMessage(), position());
    		    }*/
    		    finally {
    		    	ref.update(result);
    			    this.computing = false;
    		    }
    		}
    		public String toString() {
    		    return "realClause for " + X10ClassDef_c.this;
    		}
    	};
    	ref.setResolver(runnable);
    	return ref;
    }
  
    public Ref<CConstraint> realClause() {
    	return realClause;
    }
    public Ref<CConstraint> realClauseWithThis() {
        return realClauseWithThis;
    }
    public CConstraint getRealClause() {
    	return realClause.get();
    }
	
    public boolean isJavaType() {
        return fromJavaClassFile();
    }

    public List<X10FieldDef> properties() {
	List<X10FieldDef> x10fields = new TransformingList<FieldDef, X10FieldDef>(fields(),
	    new Transformation<FieldDef,X10FieldDef>() {
	        public X10FieldDef transform(FieldDef o) {
	            return (X10FieldDef) o;
	        }
	});
        return new FilteringList<X10FieldDef>(x10fields, new Predicate<X10FieldDef>() {
            public boolean isTrue(X10FieldDef o) {
                return o.isProperty();
            }
        });
    }
    
    List<ParameterType> typeParameters;
    
    public List<ParameterType> typeParameters() {
	return Collections.unmodifiableList(typeParameters);
    }
    
    public List<ParameterType.Variance> variances() {
	return Collections.unmodifiableList(variances);
    }
    public ParameterType.Variance getVariance(ParameterType t) {
        int index = typeParameters.indexOf(t);
        assert index!=-1 : "Param "+t+" not found in "+typeParameters;
        return variances.get(index);
    }
    
    public void addTypeParameter(ParameterType p, ParameterType.Variance v) {
	typeParameters = new ArrayList<ParameterType>(typeParameters);
	typeParameters.add(p);
	variances = new ArrayList<ParameterType.Variance>(variances);
	variances.add(v);
    }
    
    public void replaceTypeParameter(int i, ParameterType p, ParameterType.Variance v) {
	assert (typeParameters.size() == variances.size());
	typeParameters = new ArrayList<ParameterType>(typeParameters);
	typeParameters.set(i, p);
	variances = new ArrayList<ParameterType.Variance>(variances);
	variances.set(i, v);
    }
    
    TypeParamSubst subst = null;
    public TypeParamSubst subst() { return this.subst; }
    public void setSubst(TypeParamSubst subst) { this.subst = subst; }

    List<TypeDef> typeMembers;

    // TODO:
    // Add .class property
    // Add class <: C to class invariant
    // Add code to not complain that it's not initialized; ignore when disambiguating C[T]
    public List<TypeDef> memberTypes() {
    	return Collections.unmodifiableList(typeMembers);
    }

    public void addMemberType(TypeDef t) {
    	typeMembers.add(t);
    }
    public Ref<TypeConstraint> typeGuard() {
    	return new LazyRef_c<TypeConstraint>(Types.parameterBounds(asType()));
    }
    public boolean isStruct() {
    	return flags().isStruct();
    }
    // This is overridden by the synthetic Fun_** classes created in X10TypeSystem_c.
    public boolean isFunction() {
    	return false;
    }
    private String typeParameterString() {
    	return ""; //(typeParameters.isEmpty() ? "" : typeParameters.toString());
    }
    public String toString() {
        Name name = name();
        
        if (kind() == null) {
            return "<unknown class " + name + typeParameterString() + ">";
        }
    
        if (kind() == ANONYMOUS) {
            if (interfaces != null && ! interfaces.isEmpty()) {
                return isFunction() ? "" + interfaces.get(0) : "<anonymous subtype of " + interfaces.get(0) + typeParameters() + ">";
            }
            if (superType != null) {
                return "<anonymous subclass of " + superType + ">" + typeParameterString();
            }
        }
    
        if (kind() == TOP_LEVEL) {
            Package p = Types.get(package_());
            return (p != null ? p.toString() + "." : "") + name + typeParameterString();
        }
        else if (kind() == MEMBER) {
            ClassDef outer = Types.get(outer());
            return (outer != null ? outer.toString() + "." : "") + name + typeParameterString();
        }
        else {
            return name.toString() + typeParameterString();
        }
    }
    
    /** Get the class's flags. */
    /** Override the implementation for Java in ClassDef_c which ignores the flag if the class is anonymous.
     * In the current X10 implementation closure types are final anonymous classes.
     */
    @Override
    public Flags flags() {
       if (kind() == ANONYMOUS)
            return Flags.FINAL;
        return flags;
    }

    public X10ClassType asType() {
    	
    	if (asType == null) {
    		asType = ts.createClassType(position(), errorPosition(), Types.ref(this));
    	}
    	return (X10ClassType) asType;
    }
    
    public boolean hasDeserializationConstructor(Context context) {
        for (ConstructorDef cd: constructors()) {
            if (cd.formalTypes().size() == 1) {
                Type type = cd.formalTypes().get(0).get();
                if (type.isSubtype(type.typeSystem().Deserializer(), context)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /** Was the class declared in a static context? */
    protected boolean inStaticContext = false;
    
    /** Whether we need to serialize this class. */
    protected boolean needSerialization = true;

    public boolean isMember() {
        return kind == MEMBER;
    }

    public boolean isTopLevel() {
        return kind == TOP_LEVEL;
    }

    public boolean isLocal() {
        return kind == LOCAL;
    }

    public boolean isAnonymous() {
        return kind == ANONYMOUS;
    }

    public boolean isNested() {
        return !isTopLevel();
    }

    public boolean isInnerClass() {
        return !flags().isInterface() && isNested() && !flags().isStatic()
                && !inStaticContext();
    }
    
    public Source sourceFile() {
        return fromSource;
    }

    protected enum SourceKind { SOURCE, JAVA, ENCODED, UNKNOWN };
    protected SourceKind sourceKind;
    
    public boolean fromSourceFile() {
        return sourceKind == SourceKind.SOURCE;
    }

    public void setFromSourceFile() {
        sourceKind = SourceKind.SOURCE;
    }

    public boolean fromEncodedClassFile() {
        return sourceKind == SourceKind.ENCODED;
    }

    public void setFromEncodedClassFile() {
        sourceKind = SourceKind.ENCODED;
    }

    public boolean fromJavaClassFile() {
        return sourceKind == SourceKind.JAVA;
    }

    public void setFromJavaClassFile() {
        sourceKind = SourceKind.JAVA;
    }

    Job job;
    
    public Job job() {
        return job;
    }
    
    public void setJob(Job job) {
        this.job = job;
    }
    
    public Kind kind() {
        return kind;
    }

    public void inStaticContext(boolean inStaticContext) {
        this.inStaticContext = inStaticContext;
    }

    public boolean inStaticContext() {
        return inStaticContext;
    }

    public Ref<? extends X10ClassDef> outer() {
        if (kind() == TOP_LEVEL)
            return null;
        if (outer == null)
            throw new InternalCompilerError("Nested class " + this + " must have outer classes.");
        return outer;
    }

    public Name name() {
        return name;
    }
    
    public void setContainer(Ref<? extends ContainerType> container) {
        throw new InternalCompilerError("Call outer(container.def()) instead.");
    }

    /** Get the class's super type. */
    public Ref<? extends Type> superType() {
        return this.superType;
    }

    /** Get the class's package. */
    public Ref<? extends Package> package_() {
        return package_;
    }
    
    public void setFlags(Flags flags) {
        this.flags = flags;
    }

    public void flags(Flags flags) {
	this.flags = flags;
    }

    public void kind(Kind kind) {
        this.kind = kind;
    }

    public void outer(Ref<X10ClassDef> outer) {
        if (outer != null && kind() == TOP_LEVEL)
            throw new InternalCompilerError("Top-level classes cannot have outer classes.");
        this.outer = outer;
    }
    
    public Ref<? extends ContainerType> container() {
        return Types.<ClassType>ref(ts.createClassType(position(), errorPosition(), this.outer));
    }
    
    public void name(Name name) {
        if (kind() == ANONYMOUS)
            throw new InternalCompilerError("Anonymous classes cannot have names.");
        this.name = name;
    }

    public void position(Position pos) {
	this.position = pos;
    }
    
    public void errorPosition(Position pos) {
    	this.errorPosition = pos;
        }

    public void setPackage(Ref<? extends Package> p) {
	this.package_ = p;
    }

    public void superType(Ref<? extends Type> t) {
	this.superType = t;
    }

    public void addInterface(Ref<? extends Type> t) {
	interfaces.add(t);
    }

    public void addMethod(MethodDef mi) {
	methods.add(mi);
    }

    public void addConstructor(ConstructorDef ci) {
	constructors.add(ci);
    }

    public void addField(FieldDef fi) {
	fields.add(fi);
    }

    public void addMemberClass(Ref<? extends ClassType> t) {
	memberClasses.add(t);
    }

    public void setConstructors(List<? extends ConstructorDef> l) {
        this.constructors = new ArrayList<ConstructorDef>(l);
    }

    public void setFields(List<? extends FieldDef> l) {
        this.fields = new ArrayList<FieldDef>(l);
    }

    public void setInterfaces(List<Ref<? extends Type>> l) {
        this.interfaces = new ArrayList<Ref<? extends Type>>(l);
    }

    public void setMemberClasses(List<Ref<? extends ClassType>> l) {
        this.memberClasses = new ArrayList<Ref<? extends ClassType>>(l);
    }

    public void setMethods(List<? extends MethodDef> l) {
        this.methods = new ArrayList<MethodDef>(l);
    }
    
    /** Return an immutable list of constructors */
    public List<ConstructorDef> constructors() {
        return Collections.unmodifiableList(constructors);
    }

    /** Return an immutable list of member classes */
    public List<Ref<? extends ClassType>> memberClasses() {
        return Collections.<Ref<? extends ClassType>>unmodifiableList(memberClasses);
    }

    /** Return an immutable list of methods. */
    public List<MethodDef> methods() {
        return Collections.unmodifiableList(methods);
    }

    /** Return an immutable list of fields */
    public List<FieldDef> fields() {
        return Collections.unmodifiableList(fields);
    }
    
    /** Return an immutable list of interfaces */
    public List<Ref<? extends Type>> interfaces() {
        return Collections.<Ref<? extends Type>>unmodifiableList(interfaces);
    }
    
    public void needSerialization(boolean b) {
        needSerialization = b;
    }
    
    public boolean needSerialization() {
        return needSerialization;
    }

    /** Get the full name of the class, if possible. */
    public QName fullName() {
        Name name = name();
        
        if (kind() == TOP_LEVEL) {
            Package p = Types.get(package_());
            return QName.make(p != null ? p.fullName() : null, name);
        }
        else if (kind() == MEMBER) {
            ClassDef outer = Types.get(outer());
            return QName.make(outer != null ? outer.fullName() : null, name);
        }
        else if (kind() == LOCAL) {
            return QName.make(null, name);
        }
        else if (kind() == ANONYMOUS) {
            return QName.make(null, Name.make("<anonymous class>"));
        }
        else {
            return QName.make(null, Name.make("<unknown class>"));
        }
    }

    private static void addTypeToWorkList(ArrayList<ClassDef> worklist, Ref<? extends Type> ref) {
        Type sup = Types.get(ref);
        sup = Types.baseType(sup);
        // can be a parameter type
        if (sup instanceof ParameterType) return;
        if (! (sup instanceof X10ParsedClassType))
        assert sup instanceof X10ParsedClassType;
        worklist.add( ((X10ParsedClassType)sup).def() );
    }
    private static void addToWorkList(ClassDef def, ArrayList<ClassDef> worklist) {
        final Ref<? extends Type> ref = def.superType();
        if (ref!=null) {
            addTypeToWorkList(worklist, ref);
        }
        final List<X10FieldDef> props = def.properties();
        for (X10FieldDef p : props) {
            addTypeToWorkList(worklist, p.type());
        }
    }
    public boolean hasCircularProperty() {
        ArrayList<ClassDef> worklist = new ArrayList<ClassDef>();
        HashSet<ClassDef> smaller = new HashSet<ClassDef>();
        addToWorkList(this, worklist);
        while (worklist.size()>0) {
            ClassDef cur = worklist.remove(worklist.size()-1);
            if (smaller.contains(cur)) continue;
            smaller.add(cur);
            addToWorkList(cur, worklist);
        }
        return smaller.contains(this);

    }

    private boolean wasInner = false;
    public boolean wasInner() {
        return wasInner;
    }
    public void setWasInner(boolean v) {
        wasInner = v;
    }

    
    // [DC] the following 5 functions added to allow ClassDef to be a CodeDef
    // which I wanted to do so that the code reference stored in a context can understand
    // when an expression is within an inner class scope (e.g. invariant of a local class)
	@Override
	public CodeInstance<?> asInstance() {
		return asType();
	}

	@Override
	public void setTypeParameters(List<ParameterType> typeParameters) {
		this.typeParameters = typeParameters;
		
	}

	@Override
	public boolean staticContext() {
		// [DC] A class guard is never in a static context
		return false;
	}

	// [DC] assuming this is the correct thing to do, have to implement the following methods so do it as a setter/getter...
	XConstrainedTerm placeTerm;

	@Override
	public XConstrainedTerm placeTerm() {
		return placeTerm;
	}

	@Override
	public void setPlaceTerm(XConstrainedTerm xt) {
		placeTerm = xt;
		
	}
}
