/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package x10.types;

import java.util.*;

import polyglot.main.Report;
import polyglot.util.*;
import polyglot.util.Enum;
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;
import x10.types.constraints.XConstrainedTerm;

/**
 * This class maintains a context for looking up named variables, types,
 * and methods.
 * It's implemented as a stack of Context objects.  Each Context
 * points to an outer context.  To enter a new scope, call one of the
 * pushXXX methods.  To leave a scope, just follow the outer() pointer.
 * NodeVisitors handle leaving scope automatically.
 * Each context object contains maps from names to variable, type, and
 * method objects declared in that scope.
 */
/**
 * This class extends the jl notion of Context to keep track of
 * the current deptype, if any, and its set of properties. These
 * properties can be referenced inside a deptype, i.e. in
 * the depClause in  [[Foo(: depClause)]].
 *
 * We implement as follows. Since we want to reuse the mechanism for pushing
 * popping scopes as we enter a depClause, we shall implement pushDepType
 * as a pushing of a context, rather than as adding extra structure to
 * the current context.
 *
 * To push a deptype we push a class. However
 * we delegate certain methods, such as currentClass() to outer, since
 * pushing a deptype does not change the meaning of "this", only introduces
 * a meaning for "self". Thus jl code should continue to work -- it does not "see"
 * the deptype pushed onto the context.
 *
 * While processing depClause the only variables of the surrounding scope
 * that are visible are the final variables. Inside depClause no new scopes
 * can be entered, e.g. inner classes, or method declarations or even depTypes.
 * This is a property of the X10 language.
 *
 * Certain methods should not be called if depType is set, e.g. methods to add names,
 * push scopes etc. These throw an assertion error.
 *
 * Certain methods can be called within a deptype, but the result should be as if they
 * are called on the outer context. So this is easily dealt with using the pattern
 * depType == null ? super.Foo(..) : pop.Foo(...)
 * That is, if this context is not deptype context, run the usual code.  Otherwise
 * delegate to the outer context.
 *
 * @author nystrom
 * @see Context
 */
public class Context_c implements Context
{
    protected Context outer;
    protected TypeSystem ts;

    public static class Kind extends Enum {
        private static final long serialVersionUID = 711415714966041239L;
        public Kind(String name) {
            super(name);
        }
    }
    
    public static final Kind BLOCK = new Kind("block");
    public static final Kind CLASS = new Kind("class");
    public static final Kind CODE = new Kind("code");
    public static final Kind OUTER = new Kind("outer");
    public static final Kind SOURCE = new Kind("source");
    
    public Context_c(TypeSystem ts) {
        this.ts = ts;
        this.outer = null;
        this.kind = OUTER;
    }
    
    public boolean isBlock() { return kind == BLOCK; }
    public boolean isClass() { return kind == CLASS; }
    public boolean isCode() { return kind == CODE; }
    public boolean isOuter() { return kind == OUTER; }
    public boolean isSource() { return kind == SOURCE; }

    public TypeSystem typeSystem() {
        return ts;
    }

    private Object SUPERcopy() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalCompilerError("Java clone() weirdness.");
        }
    }
    
    public Context freeze() {
        Context_c c = (Context_c) this.copy();
        c.types = types != null ? new HashMap<Name, Named>(types) : null;
        c.vars = vars != null ? new HashMap<Name, VarInstance<?>>(vars) : null;
        return c;
    }

    private Context_c SUPERpush() {
        Context_c v = (Context_c) this.copy();
        v.outer = this;
        v.types = null;
        v.vars = null;
        return v;
    }

    /**
     * The import table for the file
     */
    protected ImportTable it;
    protected Kind kind;
    protected ClassType type;
    protected ClassDef scope;
    protected CodeDef code;
    protected Map<Name,Named> types;
    protected Map<Name,VarInstance<?>> vars;
    protected boolean inCode;

    /**
     * Is the context static?
     */
    protected boolean staticContext;

    public ImportTable importTable() {
        return it;
    }

    /** The current package, or null if not in a package. */
    public Package package_() {
        return Types.get(importTable().package_());
    }

    /**
     * Returns whether the particular symbol is defined locally.  If it isn't
     * in this scope, we ask the parent scope, but don't traverse to enclosing
     * classes.
     */
    private boolean SUPERisLocal(Name name) {
        if (isClass()) {
            return false;
        }
        
        if ((isBlock() || isCode()) &&
            (findVariableInThisScope(name) != null || findInThisScope(ts.TypeMatcher(name)) != null)) {
            return true;
        }

        if (isCode()) {
            return false;
        }

        if (outer == null) {
            return false;
        }

        return outer.isLocal(name);
    }
     
    /**
     * Looks up a method with name "name" and arguments compatible with
     * "argTypes".
     */
    private MethodInstance SUPERfindMethod(TypeSystem_c.MethodMatcher matcher) throws SemanticException {
        if (Report.should_report(TOPICS, 3))
          Report.report(3, "find-method " + matcher.signature() + " in " + this);

        // Check for any method with the appropriate name.
        // If found, stop the search since it shadows any enclosing
        // classes method of the same name.
        if (this.currentClass() != null &&
            ts.hasMethodNamed(this.currentClass(), matcher.name())) {
            if (Report.should_report(TOPICS, 3))
              Report.report(3, "find-method " + matcher.signature() + " -> " +
                                this.currentClass());

            // Found a class that has a method of the right name.
            // Now need to check if the method is of the correct type.
            return ts.findMethod(this.currentClass(), matcher.container(this.currentClass()));
        }

        if (outer != null) {
            return outer.findMethod(matcher);
        }

        throw new SemanticException("Method " + matcher.signature() + " not found.");
    }

    /**
     * Gets a local of a particular name.
     */
    private LocalInstance SUPERfindLocal(Name name) throws SemanticException {
	VarInstance<?> vi = findVariableSilent(name);

	if (vi instanceof LocalInstance) {
	    return (LocalInstance) vi;
	}

        throw new SemanticException("Local " + name + " not found.");
    }

    /**
     * Finds the class which added a field to the scope.
     */
    private ClassType SUPERfindFieldScope(Name name) throws SemanticException {
        if (Report.should_report(TOPICS, 3))
          Report.report(3, "find-field-scope " + name + " in " + this);

        VarInstance<?> vi = findVariableInThisScope(name);

        if (vi instanceof FieldInstance) {
            if (Report.should_report(TOPICS, 3))
              Report.report(3, "find-field-scope " + name + " in " + vi);
            return type;
        }

        if (vi == null && outer != null) {
            return outer.findFieldScope(name);
        }

        throw new SemanticException("Field " + name + " not found.");
    }

    /** Finds the class which added a method to the scope.
     */
    private ClassType SUPERfindMethodScope(Name name) throws SemanticException {
        if (Report.should_report(TOPICS, 3))
          Report.report(3, "find-method-scope " + name + " in " + this);

        if (this.currentClass() != null &&
            ts.hasMethodNamed(this.currentClass(), name)) {
            if (Report.should_report(TOPICS, 3))
              Report.report(3, "find-method-scope " + name + " -> " +
                                this.currentClass());
            return this.currentClass();
        }

        if (outer != null) {
            return outer.findMethodScope(name);
        }

        throw new SemanticException("Method " + name + " not found.");
    }

    /**
     * Gets a field of a particular name.
     */
    private FieldInstance SUPERfindField(Name name) throws SemanticException {
	VarInstance<?> vi = findVariableSilent(name);

	if (vi instanceof FieldInstance) {
	    FieldInstance fi = (FieldInstance) vi;

	    if (! ts.isAccessible(fi, this)) {
                throw new SemanticException("Field " + name + " not accessible.");
	    }

            if (Report.should_report(TOPICS, 3))
              Report.report(3, "find-field " + name + " -> " + fi);
	    return fi;
	}

        throw new NoMemberException(NoMemberException.FIELD, "Field " + name + " not found.");
    }

    /**
     * Gets a local or field of a particular name.
     */
    private VarInstance<?> SUPERfindVariable(Name name) throws SemanticException {
        VarInstance<?> vi = findVariableSilent(name);

	if (vi != null) {
            if (Report.should_report(TOPICS, 3))
              Report.report(3, "find-var " + name + " -> " + vi);
            return vi;
	}

        throw new SemanticException("Variable " + name + " not found.");
    }

    /**
     * Gets a local or field of a particular name.
     */
    private VarInstance<?> SUPERfindVariableSilent(Name name) {
        if (Report.should_report(TOPICS, 3))
          Report.report(3, "find-var " + name + " in " + this);

        VarInstance<?> vi = findVariableInThisScope(name);

        if (vi != null) {
            if (Report.should_report(TOPICS, 3))
              Report.report(3, "find-var " + name + " -> " + vi);
            return vi;
        }

        if (outer != null) {
            return outer.findVariableSilent(name);
        }

        return null;
    }

    protected String mapsToString() {
        return "types=" + types + " vars=" + vars;
    }

    private String SUPERtoString() {
        return "(" + kind + " " + mapsToString() + " " + outer + ")";
    }

    public Context pop() {
        return outer;
    }

    /**
     * Finds the definition of a particular type.
     */
    public Named find(Matcher<Named> matcher) throws SemanticException {
	if (Report.should_report(TOPICS, 3))
            Report.report(3, "find-type " + matcher.signature() + " in " + this);

        if (isOuter())
            return ts.systemResolver().find(QName.make(null, matcher.name())); // NOTE: looking up a short name
        if (isSource())
            return it.find(matcher);

        Named type = findInThisScope(matcher);

        if (type != null) {
            if (Report.should_report(TOPICS, 3))
              Report.report(3, "find " + matcher.signature() + " -> " + type);
            return type;
        }

        if (outer != null) {
            return outer.find(matcher);
        }

        throw new SemanticException("Type " + matcher.signature() + " not found.");
    }

    /**
     * Push a source file scope.
     */
    private Context SUPERpushSource(ImportTable it) {
        Context_c v = push();
        v.kind = SOURCE;
        v.it = it;
        v.inCode = false;
        v.staticContext = false;
        return v;
    }

    /**
     * Pushes on a class scoping.
     * @param classScope The class whose scope is being entered.  This is
     * the object associated with the class declaration and is returned by
     * currentClassScope.  This is a mutable class type since for some
     * passes (e.g., addMembers), the object returned by currentClassScope
     * is modified.
     * @param type The type to be returned by currentClass().  For JL, this
     * type is the same as classScope.  For other languages, it may differ
     * since currentClassScope might not represent a type.
     * @return A new context with a new scope and which maps the short name
     * of type to type.
     */
    private Context SUPERpushClass(ClassDef classScope, ClassType type) {
        if (Report.should_report(TOPICS, 4))
          Report.report(4, "push class " + classScope + " " + classScope.position());
        Context_c v = push();
        v.kind = CLASS;
        v.scope = classScope;
        v.type = type;
        v.inCode = false;
        v.staticContext = false;

        if (! type.isAnonymous()) {
            v.addNamed(type);
        }

        return v;
    }

    /**
     * pushes an additional block-scoping level.
     */
    private Context SUPERpushBlock() {
        if (Report.should_report(TOPICS, 4))
          Report.report(4, "push block");
        Context_c v = push();
        v.kind = BLOCK;
        return v;
    }

    /**
     * pushes an additional static scoping level.
     */
    private Context SUPERpushStatic() {
        if (Report.should_report(TOPICS, 4))
          Report.report(4, "push static");
        Context_c v = push();
        v.staticContext = true;
        return v;
    }

    /**
     * enters a method
     */
    private Context SUPERpushCode(CodeDef ci) {
        if (Report.should_report(TOPICS, 4))
          Report.report(4, "push code " + ci.position());
        Context_c v = push();
        v.kind = CODE;
        v.code = ci;
        v.inCode = true;
        v.staticContext = ci instanceof MemberDef && ((MemberDef) ci).flags().isStatic();
        return v;
    }

    /**
     * Gets the current method
     */
    private CodeDef SUPERcurrentCode() {
        return code;
    }

    /**
     * Return true if in a method's scope and not in a local class within the
     * innermost method.
     */
    private boolean SUPERinCode() {
        return inCode;
    }

    
    /** 
     * Returns whether the current context is a static context.
     * A statement of expression occurs in a static context if and only if the
     * inner-most method, constructor, instance initializer, static initializer,
     * field initializer, or explicit constructor statement enclosing the 
     * statement or expressions is a static method, static initializer, the 
     * variable initializer of a static variable, or an explicity constructor 
     * invocation statment. (Java Language Spec, 2nd Edition, 8.1.2)
     */
    private boolean SUPERinStaticContext() {
        return staticContext;
    }

    /**
     * Gets current class
     */
    private ClassType SUPERcurrentClass() {
        return type;
    }

    /**
     * Gets current class
     */
    private ClassDef SUPERcurrentClassDef() {
        return scope;
    }

    /**
     * Adds a symbol to the current scoping level.
     */
    private void SUPERaddVariable(VarInstance<?> vi) {
        if (Report.should_report(TOPICS, 3))
          Report.report(3, "Adding " + vi.name() + " to context.");
        addVariableToThisScope(vi);
    }

    /**
     * Adds a named type object to the current scoping level.
     */
    private void SUPERaddNamed(Named t) {
        if (Report.should_report(TOPICS, 3))
          Report.report(3, "Adding type " + t.name() + " to context.");
        addNamedToThisScope(t);
    }

    public Named findInThisScope(Matcher<Named> matcher) {
	Name name = matcher.name();
	
        Named t = null;
        
        if (types != null) {
            t = (Named) types.get(name);

            if (t != null) {
        	try {
        	    t = matcher.instantiate(t);
        	}
        	catch (SemanticException e) {
        	    t = null;
        	}
            }
        }
        
        if (t == null && isClass()) {
            if (this.type != null && ! this.type.isAnonymous()) {
        	try {
        	    t = matcher.instantiate(this.type);
        	}
        	catch (SemanticException e) {
        	}
            }
        }
        
        if (t == null && isClass()) {
            try {
        	t = ts.classContextResolver(this.currentClass(), this).find(matcher);
            }
            catch (SemanticException e) {
            }
        }
        
        return t;
    }

    private void SUPERaddNamedToThisScope(Named type) {
        if (types == null) types = new HashMap<Name, Named>();
        types.put(type.name(), type);
    }

    private ClassType SUPERfindMethodContainerInThisScope(Name name) {
        if (isClass() && ts.hasMethodNamed(this.currentClass(), name)) {
            return this.type;
        }
        return null;
    }

    private VarInstance<?> SUPERfindVariableInThisScope(Name name) {
        VarInstance<?> vi = null;
        
        if (vars != null) {
            vi = (VarInstance<?>) vars.get(name);
        }
        
        if (vi == null && isClass()) {
            try {
                return ts.findField(this.currentClass(), ts.FieldMatcher(this.currentClass(), name, this));
            }
            catch (SemanticException e) {
                return null;// todo: we loose the error message! e.g., "Field XXX is ambiguous; it is defined in both ..." 
            }
        }
        return vi;
    }

    private void SUPERaddVariableToThisScope(VarInstance<?> var) {
        if (vars == null) vars = new HashMap<Name,VarInstance<?>>();
        vars.put(var.name(), var);
    }

    private static final Collection<String> TOPICS = 
                CollectionUtil.list(Report.types, Report.context);

	public List<LocalDef> locals() {
	    if (vars != null) {
	        List<LocalDef> lis = allLocals();
	        if (lis.isEmpty())
	            return lis;
	        Context_c c = (Context_c) pop();
	        if (c != null)
	            lis.removeAll(c.allLocals());
	        return lis;
	    }
	    return Collections.<LocalDef>emptyList();
	}

	public List<LocalDef> allLocals() {
	    if (vars != null) {
	        List<LocalDef> lis = new ArrayList<LocalDef>(vars.values().size());
	        for (VarInstance<?> vi : vars.values()) {
	            if (vi instanceof LocalInstance)
	                lis.add(((LocalInstance) vi).def());
	        }
	        Context_c c = (Context_c) pop();
	        if (c != null)
	            lis.addAll(c.allLocals());
	        return lis;
	    }
	    else {
	        Context_c c = (Context_c) pop();
	        if (c != null)
	            return c.allLocals();
	    }
	    return Collections.<LocalDef>emptyList();
	}

	public XVar thisVar() {
	    if (this.inSuperTypeDeclaration()) {
	        X10ClassDef t = this.supertypeDeclarationType();
	        return t.thisVar();
	    }
	    CodeDef cd = this.currentCode();
	    if (cd instanceof X10MemberDef) {
	        return ((X10MemberDef) cd).thisVar();
	    }
	    X10ClassDef t = (X10ClassDef) this.currentClassDef();
	    if (t != null) {
	        return t.thisVar();
	    }
	    return null;
	}
	

	public CConstraint constraintProjection(CConstraint... cs) throws XFailure {
		 HashMap<XTerm, CConstraint> m = new HashMap<XTerm, CConstraint>();

		 // add in the real clause of the type of any var mentioned in the constraint list cs
		 CConstraint r = null;
		 for (CConstraint ci : cs) {
			 if (ci == null)
				 continue;
			 CConstraint ri = ci.constraintProjection(m);
			 if (r == null)
				 r = ri;
			 else
				 r.addIn(ri);
		 }

		 if (r == null) 
			 r = new CConstraint();

		 // fold in the current constraint
		 r.addSigma(currentConstraint(), m);
		 r.addSigma(currentPlaceTerm, m);
		 PlaceChecker.AddHereEqualsPlaceTerm(r, this);

		 r.addSigma(thisPlace, m);

		 // fold in the real clause of the base type
		 Type selfType = this.currentDepType();
		 if (selfType != null) {
			 CConstraint selfConstraint = X10TypeMixin.realX(selfType);
			 if (selfConstraint != null) {
				 r.addIn(selfConstraint.instantiateSelf(r.self()));
			 }
		 }

		 return r;
	 }


    protected Ref<TypeConstraint> currentTypeConstraint;
    public TypeConstraint currentTypeConstraint() {
    	if (currentTypeConstraint == null)
    		return new TypeConstraint();
    	return currentTypeConstraint.get(); }
    public void setCurrentTypeConstraint(Ref<TypeConstraint> c) {
    	currentTypeConstraint = c;
    }

    /*
    protected CConstraint currentPlaceConstraint;
    public CConstraint currentPlaceConstraint() {
    	if (currentPlaceConstraint == null)
    		return new CConstraint();
    	return currentPlaceConstraint;
    }
   */
    protected XConstrainedTerm currentPlaceTerm = null;
    public XConstrainedTerm currentPlaceTerm() {
    	/*Context_c cxt = this;
    	XConstrainedTerm result = cxt.currentPlaceTerm;
    	// skip dummy async places
    	for ( ;
    	     cxt != null && result != null && result.term().toString().contains("$dummyAsync#");
    	     cxt = (Context_c) cxt.pop())
    	{
    		result = cxt.currentPlaceTerm;
    	}*/

    	return currentPlaceTerm;
    }
    public Context pushPlace(XConstrainedTerm t) {
    	//assert t!= null;
    	Context_c cxt = (Context_c) SUPERpushBlock();
		cxt.currentPlaceTerm = t;
		return cxt;
    }

    protected boolean inClockedFinishScope=false;
    public Context pushFinishScope(boolean isClocked) {
    	Context_c cxt = (Context_c) SUPERpushBlock();
		cxt.x10Kind = X10Kind.Finish;
		cxt.inClockedFinishScope = isClocked;
		return cxt;
    }
    public boolean inClockedFinishScope() {
    	if (inClockedFinishScope)
    		return true;
    	if (outer != null) 
    		return ((Context) outer).inClockedFinishScope();
    	return false;
    }
    Type currentCollectingFinishType=null;
    public Context pushCollectingFinishScope(Type t) {
    	assert t!=null;
    	Context_c cxt = (Context_c) SUPERpushBlock();
    	cxt.currentCollectingFinishType =t;
    	return cxt;
    }
    public Type collectingFinishType() {
    	if (currentCollectingFinishType != null)
    	return currentCollectingFinishType;
    	// check if you are in code.
    	Context cxt = this;
    	CodeDef cc = cxt.currentCode();
    	if (cc != null) {
    		if (cc instanceof X10MethodDef) {
    			X10MethodDef md = (X10MethodDef) cc;
    			while (md.name().toString().contains("$dummyAsync")) {
    				cxt = cxt.pop();
    				if (cxt == null)
    					break;
    				cc = cxt.currentCode();
    				if (cc instanceof X10MethodDef)
    					md = (X10MethodDef) cc;
    			}
    			if (md != null)
    				return Types.get(md.offerType());
    		}
    	}
    	return null;
    }
    protected XConstrainedTerm thisPlace = null;
    public XConstrainedTerm currentThisPlace() {
    	/*if (thisPlace == null) {
    		TypeSystem xts = (TypeSystem) ts;
    		thisPlace = ((TypeSystem) ts).xtypeTranslator().firstPlace();
    		assert thisPlace != null;
    	}
    	*/
    	return thisPlace;
    }

    protected CConstraint currentConstraint;
    // vj: TODO: check if this is the right thing to do.
    public CConstraint currentConstraint() {
    	if (currentConstraint == null) {
    		CConstraint c = new CConstraint();
    		if (! inStaticContext()) {
    			c.setThisVar(thisVar());
    		}
    		return c;
    	}
    	return currentConstraint;
    }
    public void setCurrentConstraint(CConstraint c) {
    	currentConstraint = c;
    }

	public CodeDef definingCodeDef(Name name) {
	    if ((isBlock() || isCode()) &&
	            (findVariableInThisScope(name) != null || findInThisScope(name) != null)) {
	        return currentCode();
	    }

	    if (outer instanceof Context) {
	        return ((Context) outer).definingCodeDef(name);
	    }
	    return null;
	}

	// Set if we are in a supertype declaration of this type.
	protected X10ClassDef inSuperOf = null;
	public boolean inSuperTypeDeclaration() { return inSuperOf != null; }
	public X10ClassDef supertypeDeclarationType() { return inSuperOf; }

	// Invariant: isDepType => outer != null.

	protected Ref<? extends Type> depType = null;
	protected VarDef varWhoseTypeIsBeingElaborated = null;
	public boolean inDepType() { return depType != null; }

	protected boolean inLoopHeader;
	protected boolean inAnnotation;
	protected boolean inAnonObjectScope;
	protected boolean inAssignment;
	boolean isClocked=false;
    public Context pushClockedContext() {
    	Context_c cxt = (Context_c) SUPERpushBlock();
		cxt.isClocked = true;
		return cxt;
    }
    public boolean isClocked() {
    	if (isClocked)
    		return true;
    	CodeDef cd = currentCode();
    	if (cd instanceof MethodDef) {
    		MethodDef md = (MethodDef) cd;
    		return X10Flags.toX10Flags(md.flags()).isClocked();
    	}
    	return false;
    }
	public boolean inLoopHeader() { return inLoopHeader; }
	public boolean inAnnotation() { return inAnnotation; }
	public boolean inAnonObjectScope() { return inAnonObjectScope;}
	public void restoreAnonObjectScope(boolean s) { inAnonObjectScope=s;}

	public void setInAssignment() { inAssignment = true;}
	public void setLoopHeader() { inLoopHeader = true; }
	public void setAnnotation() { inAnnotation = true; }
	public void setAnonObjectScope() { inAnonObjectScope = true;}
	public void clearAnnotation() { inAnnotation = false; }

	protected Context_c push() {
		Context_c v = (Context_c) SUPERpush();
		v.depType = null;
//		v.varWhoseTypeIsBeingElaborated = null;
		// Do not set the inXXXCode attributes to false, inherit them from parent.
		return v;
	}

	public X10NamedType currentDepType() {
		return (X10NamedType) Types.get(depType);
	}

	public Ref<? extends Type> depTypeRef() {
		return depType;
	}

	public VarDef varWhoseTypeIsBeingElaborated() {
		return varWhoseTypeIsBeingElaborated;
	}

	/**
	 * Returns whether the particular symbol is defined locally.  If it isn't
	 * in this scope, we ask the parent scope, but don't traverse to enclosing
	 * classes.
	 */
	public boolean isLocal(Name name) {
		return depType == null ? SUPERisLocal(name) : pop().isLocal(name);
	}
	public boolean isLocalIncludingAsyncAt(Name name) {
        if (isLocal(name)) return true;
        if (outer!=null && isDummyCode(currentCode())) return ((Context_c)outer).isLocalIncludingAsyncAt(name);
        return false;
    }
    public static boolean isDummyCode(CodeDef ci) {
        return (ci != null)
				&& (ci instanceof MethodDef)
				&& ((MethodDef) ci).name().toString().equals(TypeSystem_c.DUMMY_AT_ASYNC);
    }
    public boolean inAsyncScope() {
        return x10Kind== X10Kind.Async ? true :
                outer==null || (isCode() && !isDummyCode(currentCode())) ? false :
                ((Context_c)outer).inAsyncScope();        
    }
	public boolean isSequentialAccess(boolean isSeqential, Name name) { // there is no async without an enclosing finish   
        if (findVariableInThisScope(name)!=null) return isSeqential;
        if (outer!=null) {
            Context_c o = (Context_c)outer;
            return
                o.isSequentialAccess(
                        x10Kind==X10Kind.Finish ? true :
                        x10Kind==X10Kind.Async ? false :
                        isSeqential,name);
        }
        return true; // not in this scope (like a field), so access is ok
    }



	public boolean isValInScopeInClass(Name name) {
	    if (isClass()) {
	        return false;
	    }

	    if ((isBlock() || isCode()) &&
	            (findVariableInThisScope(name) != null)) {
	        return true;
	    }

	    if (outer instanceof Context) {
	        return ((Context_c) outer).isValInScopeInClass(name);
	    }
	    return false;
	}

        /**
	     * Looks up a method with name "name" and arguments compatible with
	     * "argTypes".
	     */
	    public MethodInstance superFindMethod(TypeSystem_c.MethodMatcher matcher) throws SemanticException {
	        if (Report.should_report(TOPICS, 3))
	          Report.report(3, "find-method " + matcher.signature() + " in " + this);

	        // Check for any method with the appropriate name.
	        // If found, stop the search since it shadows any enclosing
	        // classes method of the same name.
	        ClassType currentClass = this.currentClass();
		if (currentClass != null &&
	            ts.hasMethodNamed(currentClass, matcher.name())) {
	            if (Report.should_report(TOPICS, 3))
	              Report.report(3, "find-method " + matcher.signature() + " -> " +
	                                currentClass);

	            // Override to change the type from C to C{self==this}.
	            Type t = currentClass;
	            TypeSystem xts = (TypeSystem) ts;

	            XVar thisVar = null;
	            if (XTypeTranslator.THIS_VAR) {
	                CodeDef cd = this.currentCode();
	                if (cd instanceof X10MemberDef) {
	                    thisVar = ((X10MemberDef) cd).thisVar();
	                }
	            }
	            else {
	                //thisVar = xts.xtypeTranslator().transThis(currentClass);
	                thisVar = xts.xtypeTranslator().transThisWithoutTypeConstraint();
	            }

	            if (thisVar != null)
	                t = X10TypeMixin.setSelfVar(t, thisVar);

	            // Found a class that has a method of the right name.
	            // Now need to check if the method is of the correct type.
	            return ts.findMethod(t, matcher.container(t));
	        }

	        if (outer != null) {
	            return outer.findMethod(matcher);
	        }

	        throw new SemanticException("Method " + matcher.signature() + " not found.");
	    }

	/**
	 * Looks up a method with name "name" and arguments compatible with
	 * "argTypes".
	 */
	public MethodInstance findMethod(TypeSystem_c.MethodMatcher matcher) throws SemanticException {
		MethodInstance result = depType == null ? superFindMethod(matcher) : pop().findMethod(matcher);
		return result;
	}

	/**
	 * Gets a local variable of a particular name.
	 */
	public LocalInstance findLocal(Name name) throws SemanticException {
		return depType == null ? SUPERfindLocal(name) : pop().findLocal(name);
	}

	public ClassType type() { return type; }

	/**
	 * Finds the class which added a field to the scope.
	 */
	public ClassType findFieldScope(Name name) throws SemanticException {
		VarInstance<?> vi = findVariableInThisScope(name);

		if (vi instanceof FieldInstance) {
		    ClassType result = type;
		    if (result != null)
			return result;
		    if (inDepType())
			result = ((Context_c) pop()).type();
		    if (result != null)
			return result;
		    if (supertypeDeclarationType() != null)
			result = supertypeDeclarationType().asType();
		    if (result != null)
			return result;
		}

		if (vi == null && outer != null) {
		    return outer.findFieldScope(name);
		}

		throw new SemanticException("Field " + name + " not found.");
	}

	/**
	 * Finds the class which added a method to the scope. Do not
	 * search the current scope if depType !=null, since that does not contribute methods.
	 * In fact, it should be an error for this method to be called when
	 * deptype is true.
	 */
	public ClassType findMethodScope(Name name) throws SemanticException {
		ClassType result = SUPERfindMethodScope(name);
		if (result == null) {
			// hack. This is null when this context is in a deptype, and the deptype
			// is not a classtype, and the field belongs to the outer type, e.g.
			// class Foo { int(:v=0) v; }
			ClassType r = type;
			result = ((Context_c) pop()).type();
		}
		return result;
	}

	/**
	 * Gets a field of a particular name.
	 */
	public FieldInstance findField(Name name) throws SemanticException {
		return SUPERfindField(name);
	}

	/**
	 * Gets a local or field of a particular name.
	 */
	public VarInstance<?> findVariable(Name name) throws SemanticException {
		VarInstance<?> vi = SUPERfindVariable(name);
		return vi;
	}

	/**
	 * Gets a local or field of a particular name.
	 */
	public VarInstance<?> findVariableSilent(Name name) {
		return SUPERfindVariableSilent(name);
	}

	/**
	 * Finds the definition of a particular type.
	 */
//	public Named find(String name) throws SemanticException {
////		assert (depType == null);
//		return SUPERfind(name);
//	}

	/**
	 * Push a source file scope.
	 */
	public Context pushSource(ImportTable it) {
		assert (depType == null);
		return SUPERpushSource(it);
	}


/*
	private boolean inBootLoads(ClassDef classScope) {
		QName q = classScope.fullName();
		return q.equals(QName.make("x10.lang.Place"))
		|| q.equals(QName.make("x10.lang.Int"))
		|| q.equals(QName.make("x10.lang.Boolean"))
			|| q.equals(QName.make("x10.lang.Object"))
				|| q.equals(QName.make("x10.lang.Ref"))
		|| q.equals(QName.make("x10.lang.NativeRuntime"));

	}
*/
	private Context_c superPushClass(ClassDef classScope, ClassType type) {
	    return (Context_c) SUPERpushClass(classScope, type);
	}
	public Context pushClass(ClassDef classScope, ClassType type) {
		//System.err.println("Pushing class " + classScope);
		assert (depType == null);
/*
		XConstrainedTerm currentHere = null;
		if (! (inBootLoads(classScope)) ){
			currentHere = currentPlaceTerm();
		}
		//XConstrainedTerm currentHere = currentPlaceTerm();
*/
		Context_c result = this;
		// Pushing a nested (non-inner) class should be done in a static context
		if (classScope.isMember() && classScope.flags().isStatic()) {
		    result = (Context_c) pushStatic();
		}
		result = result.superPushClass(classScope, type);
/*
		if ( (type.kind() == ClassDef.ANONYMOUS) || ! (
		        type.toString().startsWith("x10.lang.Boolean") ||
                type.toString().startsWith("x10.lang.Object")

		))
			try {
				XTerm thisLoc = ((TypeSystem) typeSystem()).homeVar(((X10ClassDef) classScope).thisVar(),
						this);
				if (currentHere != null) {
					CConstraint r = currentHere.constraint().copy();
					r.addBinding(thisLoc, currentHere.term());
					result.thisPlace = XConstrainedTerm.make(thisLoc, r);
				}
				//instantiate(currentHere.constraint().copy(), thisLoc);

			} catch (XFailure f) {
				throw new InternalError("Unexpected failure when realizing thisPlace constraint" +
						currentHere);
			}
			*/

		return result;
	}

	/**
	 * pushes an additional block-scoping level.
	 */
	public Context pushBlock() {
//		assert (depType == null);
		return SUPERpushBlock();
	}

	public Context pushAtomicBlock() {
		assert (depType == null);
		Context c = (Context) SUPERpushBlock();
		return c;
	}

	public Context pushAssignment() {
		if (depType != null)
			assert (depType == null);
		Context c = (Context) SUPERpushBlock();
		c.setInAssignment();
		return c;
	}

	/**
	 * pushes an additional static scoping level.
	 */
	public Context pushStatic() {
		assert (depType == null);
		return SUPERpushStatic();
	}

	/**
	 * enters a method
	 */
    public X10Kind x10Kind = X10Kind.None;

    public void x10Kind(X10Kind x10Kind) {
    	this.x10Kind = x10Kind;
    }

	public Object copy() {
		Context_c res = (Context_c) SUPERcopy();
        res.x10Kind = X10Kind.None;
        return res;
    }
    
	public Context pushCode(CodeDef ci) {
		//System.err.println("Pushing code " + ci);
		assert (depType == null);
		Context_c result = (Context_c) SUPERpushCode(ci);
		// For closures, propagate the static context of the outer scope
		if (ci instanceof ClosureDef)
		    result.staticContext = staticContext;
		return result;
	}

	/**
	 * Gets the current method
	 */
	public CodeDef currentCode() {
		return depType == null ? SUPERcurrentCode() : pop().currentCode();
	}

	/**
	 * Return true if in a method's scope and not in a local class within the
	 * innermost method.
	 */
	public boolean inCode() {
		return depType == null ? SUPERinCode() : pop().inCode();
	}

	public boolean inAssignment() {
		return inAssignment;
	}

	public boolean inStaticContext() {
		return depType == null ? SUPERinStaticContext() : pop().inStaticContext();
	}

	/**
	 * Gets current class
	 */
	public ClassType currentClass() {
		return depType == null ? SUPERcurrentClass() : pop().currentClass();
	}

	/**
	 * Gets current class scope
	 */
	public ClassDef currentClassDef() {
		return depType == null ? SUPERcurrentClassDef() : pop().currentClassDef();
	}

	/**
	 * Adds a symbol to the current scoping level.
	 */
	public void addVariable(VarInstance<?> vi) {
//		assert (depType == null);
		SUPERaddVariable(vi);
	}

	/**
	 * Adds a named type object to the current scoping level.
	 */
	public void addNamed(Named t) {
		assert (depType == null);
		SUPERaddNamed(t);
	}

	    public Named findInThisScope(Name name) {
	        if (types != null) {
	            Named t = (Named) types.get(name);
	            if (t != null)
	        	return t;
	        }
	        if (isClass()) {
	            if (! this.type.isAnonymous() &&
	                this.type.name().equals(name)) {
	                return this.type;
	            }
	            else {
	                ClassType container = this.currentClass();
			Named t = findMemberTypeInThisScope(name, container);
			if (t != null) return t;
	            }
	        }
	        if (inDepType()) {
	            Type container = currentDepType();
	            Named t = findMemberTypeInThisScope(name, container);
	            if (t != null) return t;
	        }
//	        if (supertypeDeclarationType() != null) {
//	            ClassType container = supertypeDeclarationType().asType();
//	            Named t = findMemberTypeInThisScope(name, container);
//	            if (t != null) return t;
//	        }
	        return null;
	    }

	    private Named findMemberTypeInThisScope(Name name, Type container) {
		TypeSystem ts = (TypeSystem) this.ts;
		ClassDef currentClassDef = this.currentClassDef();
		if (container instanceof MacroType) {
		    MacroType mt = (MacroType) container;
		    return findMemberTypeInThisScope(name, mt.definedType());
		}
		if (container instanceof ConstrainedType) {
		    ConstrainedType mt = (ConstrainedType) container;
		    return findMemberTypeInThisScope(name, mt.baseType().get());
		}
		try {
		    Type t = ts.findMemberType(container, name, this);
		    if (t instanceof Named) return (Named) t;
		}
		catch (SemanticException e) {
		}
		try {
		    return ts.findTypeDef(container, ts.TypeDefMatcher(container, name, Collections.<Type>emptyList(), Collections.<Type>emptyList(), this), this);
		}
		catch (SemanticException e) {
		}
		return null;
	    }

	public void addNamedToThisScope(Named type) {
//		assert (depType == null);
		SUPERaddNamedToThisScope(type);
	}

	public ClassType findMethodContainerInThisScope(Name name) {
//		assert (depType == null);
		return SUPERfindMethodContainerInThisScope(name);

	}

	public VarInstance<?> findVariableInThisScope(Name name) {
		//if (name.startsWith("val")) Report.report(1, "Context_c: searching for |" + name + " in " + this);
		if (depType == null) return SUPERfindVariableInThisScope(name);

		VarInstance<?> vi = ((Context_c) pop()).findVariableInThisScope(name);

		if (vi instanceof LocalInstance) return vi;
		// otherwise it is a FieldInstance (might be a PropertyInstance, which is a FieldInstance)
		// See if the currentDepType has a field of this name. If so, that gets priority
		// and should be returned. The receiver must treat it as the reference
		// self.name.
		try {
			if (depType instanceof X10ClassType) {
				X10ClassType dep = (X10ClassType) this.depType;
				FieldInstance myVi = ts.findField(dep, ts.FieldMatcher(dep, name, this));
				if (myVi != null) {
					return myVi;
				}
			}
		} catch (SemanticException e) { }
		return vi;
	}

	public void addVariableToThisScope(VarInstance<?> var) {
//		assert (depType == null);
		SUPERaddVariableToThisScope(var);
	}

	public void setVarWhoseTypeIsBeingElaborated(VarDef var) {
		varWhoseTypeIsBeingElaborated = var;
	}

	// New lookup methods added for deptypes.

	public X10FieldInstance findProperty(Name name) throws SemanticException {
		X10FieldInstance pi = null;
		FieldInstance fi = findField(name);
		if (fi instanceof X10FieldInstance) {
			pi = (X10FieldInstance) pi;
		}
		return pi;
	}

	public ClassType findPropertyScope(Name name) throws SemanticException {
		return findFieldScope(name);
	}

	/**
	 * Pushes on a deptype. Treat this as pushing a class.
	 */
	public Context pushDepType(x10.types.Ref<? extends x10.types.Type> ref) {
		Context_c v = (Context_c) push();
		v.depType = ref;
		v.inCode = false;
		//Report.report(1, "Context_c: Pushing deptype |" + type + "|" + v.hashCode());
		return v;
	}

	public Context pushAdditionalConstraint(CConstraint env)	throws SemanticException {
		// Now push the newly computed Gamma
		Context xc = (Context) pushBlock();
		CConstraint c = xc.currentConstraint();
		if (c == null) {
			c = env;
		} else {
			try {
				c = c.copy().addIn(env);
				// c.addIn(xc.constraintProjection(c));
			}
			catch (XFailure e) {
				throw new SemanticException("Call invalid; calling environment is inconsistent.");
			}
		}
		xc.setCurrentConstraint(c);
		//            xc.setCurrentTypeConstraint(tenv);
		return xc;
	}
	public Context pushSuperTypeDeclaration(X10ClassDef type) {

		Context_c v = (Context_c) push();
		v.inSuperOf = type;
		v.inCode = false;
		//Report.report(1, "Context_c: Pushing deptype |" + type + "|" + v.hashCode());
		return v;
	}

	public String toString() {
		return "(" + (depType != null ? "depType " + depType : kind.toString())
		  + (currentConstraint ==null ? "" : " constraint= " + currentConstraint)
		  + (currentPlaceTerm == null ? "" : " place=" + currentPlaceTerm)
		  + (thisPlace == null? "" : " this.home=" + thisPlace.toString())
		  + " "+  mapsToString() + " " + outer + ")";
	}

	static protected int varCount = 0;

	public Name getNewVarName() {
		return Name.make(MAGIC_VAR_PREFIX + (varCount++));
	}
	
	static protected int nameCount = 0;
	
	public Name makeFreshName(String name) {
		synchronized (contextNameTable) {
			Name n = contextNameTable.get(name);
			if (n == null) {
				String fresh = MAGIC_NAME_PREFIX + name + (nameCount++);
				n = Name.make(fresh);
				contextNameTable.put(name,n);
			}
			return n;
		}
	}
}
