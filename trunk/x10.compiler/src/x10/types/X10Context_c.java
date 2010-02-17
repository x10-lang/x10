/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.types;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import polyglot.main.Report;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.CodeDef;
import polyglot.types.Context;
import polyglot.types.Context_c;
import polyglot.types.FieldInstance;
import polyglot.types.ImportTable;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.Matcher;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.Named;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.TypeSystem_c;
import polyglot.types.Types;
import polyglot.types.VarDef;
import polyglot.types.VarInstance;
import polyglot.util.CollectionUtil;
import x10.constraint.XFailure;
import x10.constraint.XField;
import x10.constraint.XFormula;
import x10.constraint.XLit;
import x10.constraint.XLocal;
import x10.constraint.XName;
import x10.constraint.XNameWrapper;
import x10.constraint.XRoot;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraint_c;
import x10.types.constraints.XConstrainedTerm;

public class X10Context_c extends Context_c implements X10Context {


	public X10Context_c(TypeSystem ts) {
		super(ts);
	}

	public List<LocalDef> locals() {
	    if (vars != null) {
	        List<LocalDef> lis = allLocals();
	        if (lis.isEmpty())
	            return lis;
	        X10Context_c c = (X10Context_c) pop();
	        if (c != null)
	            lis.removeAll(c.allLocals());
	        return lis;
	    }
	    return Collections.EMPTY_LIST;
	}

	public List<LocalDef> allLocals() {
	    if (vars != null) {
	        List<LocalDef> lis = new ArrayList<LocalDef>(vars.values().size());
	        for (VarInstance vi : vars.values()) {
	            if (vi instanceof LocalInstance)
	                lis.add(((LocalInstance) vi).def());
	        }
	        X10Context_c c = (X10Context_c) pop();
	        if (c != null)
	            lis.addAll(c.allLocals());
	        return lis;
	    }
	    else {
	        X10Context_c c = (X10Context_c) pop();
	        if (c != null)
	            return c.allLocals();
	    }
	    return Collections.EMPTY_LIST;
	}

	public XRoot thisVar() {
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

	/* sigma(Gamma) restricted to the variables mentioned in c1,c2 */
        public CConstraint constraintProjection(CConstraint... cs) throws XFailure {
            HashMap<XTerm, CConstraint> m = new HashMap<XTerm, CConstraint>();

            // add in the real clause of the type of any var mentioned in the constraint list cs
            CConstraint r = null;

            for (CConstraint ci : cs) {
                CConstraint ri = constraintProjection(ci, m);
                if (r == null)
                    r = ri;
                else
                    r.addIn(ri);
            }

            if (r == null) r = new CConstraint_c();

            // fold in the current constraint
            CConstraint c1 = currentConstraint();
            CConstraint sigma1 = constraintProjection(c1, m);
            r.addIn(c1);
            r.addIn(sigma1);

            // fold in the current place constraint
            CConstraint c2 = currentPlaceTerm == null ? null : currentPlaceTerm.xconstraint();
           r.addIn(c2);
           CConstraint sigma2 = constraintProjection(c2, m);
           r.addIn(sigma2);

           // fold in the current thisPlace constraint
           //System.err.println("X10Context_c: thisPlace is " + thisPlace);
           CConstraint c3 = thisPlace==null ? null : thisPlace.xconstraint();
           r.addIn(c3);
           CConstraint sigma3 = constraintProjection(c3, m);
           r.addIn(sigma3);

            // fold in the real clause of the base type
            Type selfType = this.currentDepType();
            if (selfType != null) {
                CConstraint selfConstraint = X10TypeMixin.realX(selfType);
                if (selfConstraint != null) {
                    r.addIn(selfConstraint.substitute(r.self(), selfConstraint.self()));
                }
            }

            return r;
        }

        /* sigma(Gamma) restricted to the variables mentioned in c */
        private CConstraint constraintProjection(CConstraint c, Map<XTerm,CConstraint> m) throws XFailure {
            CConstraint r = new CConstraint_c();
            if (c != null)
                for (XTerm t : c.constraints()) {
                    CConstraint tc = constraintProjection(t, m);
                    if (tc != null)
                        r.addIn(tc);
                }
            return r;
        }

        private CConstraint constraintProjection(XTerm t, Map<XTerm,CConstraint> m) throws XFailure {
	    X10TypeSystem xts = (X10TypeSystem) this.ts;

	    CConstraint r = m.get(t);
	    if (r != null)
	        return r;

	    // pre-fill the cache to avoid infinite recursion
	    m.put(t, new CConstraint_c());

	    if (t instanceof XLocal) {
	        XLocal v = (XLocal) t;
	        X10LocalDef ld = getLocal(v);
	        if (ld != null) {
	            Type ty = Types.get(ld.type());
	            CConstraint ci = X10TypeMixin.realX(ty);
	            ci = ci.substitute(v, ci.self());
	            r = new CConstraint_c();
	            r.addIn(ci);
	            r.addIn(constraintProjection(ci, m));
	        }
	    }
	    else if (t instanceof XLit) {
	    }
	    else if (t instanceof XField) {
	        XField f = (XField) t;
	        XTerm target = f.receiver();

	        CConstraint rt = constraintProjection(target, m);

	        X10FieldDef fi = getField(f);
	        CConstraint ci = null;

	        if (fi != null) {
	            Type ty = Types.get(fi.type());
	            ci = X10TypeMixin.realX(ty);
	            ci = ci.substitute(f, ci.self());
	            ci = ci.substitute(target, xts.xtypeTranslator().transThisWithoutTypeConstraint());
	            r = new CConstraint_c();
	            r.addIn(ci);
	            r.addIn(constraintProjection(ci, m));
	            if (rt != null) {
	                r.addIn(rt);
	            }
	        }
	        else {
	            r = rt;
	        }
	    }
	    else if (t instanceof XFormula) {
	        XFormula f = (XFormula) t;
	        for (XTerm a : f.arguments()) {
	            CConstraint ca = constraintProjection(a, m);
	            if (ca != null) {
	                if (r == null) {
	                    r = new CConstraint_c();
	                }
	                r.addIn(ca);
	            }
	        }
	    }
	    else {
	        assert false : "unexpected " + t;
	    }

	    if (r != null)
	        m.put(t, r);
	    else
	        m.put(t, new CConstraint_c());
	    return r;
        }

    private X10FieldDef getField(XField f) {
        XName n = f.field();
        if (n instanceof XNameWrapper) {
            XNameWrapper w = (XNameWrapper<?>) n;
            if (w.val() instanceof X10FieldDef) {
                return (X10FieldDef) w.val();
            }
        }
        return null;
    }

    private X10LocalDef getLocal(XLocal f) {
        XName n = f.name();
        if (n instanceof XNameWrapper) {
            XNameWrapper w = (XNameWrapper<?>) n;
            if (w.val() instanceof X10LocalDef) {
                return (X10LocalDef) w.val();
            }
        }
        return null;
    }

    protected Ref<TypeConstraint> currentTypeConstraint;
    public TypeConstraint currentTypeConstraint() {
    	if (currentTypeConstraint == null)
    		return new TypeConstraint_c();
    	return currentTypeConstraint.get(); }
    public void setCurrentTypeConstraint(Ref<TypeConstraint> c) {
    	currentTypeConstraint = c;
    }

    /*
    protected CConstraint currentPlaceConstraint;
    public CConstraint currentPlaceConstraint() {
    	if (currentPlaceConstraint == null)
    		return new CConstraint_c();
    	return currentPlaceConstraint;
    }
   */
    protected XConstrainedTerm currentPlaceTerm = null;
    public XConstrainedTerm currentPlaceTerm() {
    	if (currentPlaceTerm == null) {
    		X10TypeSystem xts = (X10TypeSystem) ts;
    		currentPlaceTerm = xts.xtypeTranslator().firstPlace();
    		assert currentPlaceTerm != null;
    	}

    	return currentPlaceTerm;
    }
    public Context pushPlace(XConstrainedTerm t) {
    	assert t!= null;
    	X10Context_c cxt = (X10Context_c) super.pushBlock();
		cxt.currentPlaceTerm = t;
		return cxt;
    }
    protected XConstrainedTerm thisPlace = null;
    public XConstrainedTerm currentThisPlace() {
    	if (thisPlace == null) {
    		X10TypeSystem xts = (X10TypeSystem) ts;
    		thisPlace = ((X10TypeSystem) ts).xtypeTranslator().firstPlace();
    		assert thisPlace != null;
    	}
    	return thisPlace;
    }

    protected CConstraint currentConstraint;
    // vj: TODO: check if this is the right thing to do.
    public CConstraint currentConstraint() {
    	if (currentConstraint == null) {
    		CConstraint c = new CConstraint_c();
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

	    if (outer instanceof X10Context) {
	        return ((X10Context) outer).definingCodeDef(name);
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

	protected boolean inSafeCode;
	protected boolean inSequentialCode;
	protected boolean inLocalCode;
	protected boolean inNonBlockingCode;
	protected boolean inLoopHeader;
	protected boolean inAnnotation;
	protected boolean inAnonObjectScope;
	protected boolean inAssignment;

	public boolean inSafeCode() { return inSafeCode; }
	public boolean inSequentialCode() { return inSequentialCode; }
	public boolean inNonBlockingCode() { return inNonBlockingCode; }
	public boolean inLocalCode() { return inLocalCode; }
	public boolean inLoopHeader() { return inLoopHeader; }
	public boolean inAnnotation() { return inAnnotation; }
	public boolean inAnonObjectScope() { return inAnonObjectScope;}
	public void restoreAnonObjectScope(boolean s) { inAnonObjectScope=s;}

	public void setInAssignment() { inAssignment = true;}
	public void setSafeCode() { inSafeCode = true; }
	public void setSequentialCode() { inSequentialCode = true; }
	public void setNonBlockingCode() { inNonBlockingCode = true; }
	public void setLocalCode() { inLocalCode = true; }
	public void setLoopHeader() { inLoopHeader = true; }
	public void setAnnotation() { inAnnotation = true; }
	public void setAnonObjectScope() { inAnonObjectScope = true;}
	public void clearAnnotation() { inAnnotation = false; }

	protected Context_c push() {
		X10Context_c v = (X10Context_c) super.push();
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

	private static final Collection<String> TOPICS =
		CollectionUtil.list(Report.types, Report.context);

	/**
	 * Returns whether the particular symbol is defined locally.  If it isn't
	 * in this scope, we ask the parent scope, but don't traverse to enclosing
	 * classes.
	 */
	public boolean isLocal(Name name) {
		return depType == null ? super.isLocal(name) : pop().isLocal(name);
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
	            X10TypeSystem xts = (X10TypeSystem) ts;

	            XRoot thisVar = null;
	            if (XTypeTranslator.THIS_VAR) {
	                CodeDef cd = this.currentCode();
	                if (cd instanceof X10MemberDef) {
	                    thisVar = ((X10MemberDef) cd).thisVar();
	                }
	            }
	            else {
	                //	                  thisVar = xts.xtypeTranslator().transThis(currentClass);
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
		return depType == null ? super.findLocal(name) : pop().findLocal(name);
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
			result = ((X10Context_c) pop()).type();
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
		ClassType result = super.findMethodScope(name);
		if (result == null) {
			// hack. This is null when this context is in a deptype, and the deptype
			// is not a classtype, and the field belongs to the outer type, e.g.
			// class Foo { int(:v=0) v; }
			ClassType r = type;
			result = ((X10Context_c) pop()).type();
		}
		return result;
	}

	/**
	 * Gets a field of a particular name.
	 */
	public FieldInstance findField(Name name) throws SemanticException {
		return super.findField(name);
	}

	/**
	 * Gets a local or field of a particular name.
	 */
	@Override
	public VarInstance<?> findVariable(Name name) throws SemanticException {
		VarInstance<?> vi = super.findVariable(name);
		return vi;
	}

	/**
	 * Gets a local or field of a particular name.
	 */
	public VarInstance<?> findVariableSilent(Name name) {
		return super.findVariableSilent(name);
	}

	/**
	 * Finds the definition of a particular type.
	 */
//	public Named find(String name) throws SemanticException {
////		assert (depType == null);
//		return super.find(name);
//	}

	/**
	 * Push a source file scope.
	 */
	public Context pushSource(ImportTable it) {
		assert (depType == null);
		return super.pushSource(it);
	}


	private boolean inBootLoads(ClassDef classScope) {
		QName q = classScope.fullName();
		return q.equals(QName.make("x10.lang.Place"))
		|| q.equals(QName.make("x10.lang.Int"))
		|| q.equals(QName.make("x10.lang.Boolean"))
			|| q.equals(QName.make("x10.lang.Object"))
				|| q.equals(QName.make("x10.lang.Ref"))
		|| q.equals(QName.make("x10.lang.NativeRuntime"));

	}
	public Context pushClass(ClassDef classScope, ClassType type) {
		//System.err.println("Pushing class " + classScope);
		assert (depType == null);
		XConstrainedTerm currentHere = null;
		if (! (inBootLoads(classScope)) ){
			//System.err.println("X10COntext_c: GOLDEN..determining current place for " + classScope);
			currentHere = currentPlaceTerm();
		}
		//XConstrainedTerm currentHere = currentPlaceTerm();
		X10Context_c result = (X10Context_c) super.pushClass(classScope, type);

		if ( (type.kind() == ClassDef.ANONYMOUS) || ! (
		        type.toString().startsWith("x10.lang.Boolean") ||
                type.toString().startsWith("x10.lang.Object")

		))
			try {
				XTerm thisLoc = ((X10TypeSystem) typeSystem()).homeVar(((X10ClassDef) classScope).thisVar(),
						this);
				if (currentHere != null) {
					CConstraint r = currentHere== null ? null : currentHere.constraint().copy();
					r.addBinding(thisLoc, currentHere.term());
					result.thisPlace = XConstrainedTerm.make(thisLoc, r);
				}
				//instantiate(currentHere.constraint().copy(), thisLoc);

			} catch (XFailure f) {
				throw new InternalError("Unexpected failure when realizing thisPlace constraint" +
						currentHere);
			}

		return result;
	}

	/**
	 * pushes an additional block-scoping level.
	 */
	public Context pushBlock() {
//		assert (depType == null);
		return super.pushBlock();
	}

	public X10Context pushAtomicBlock() {
		assert (depType == null);
		X10Context c = (X10Context) super.pushBlock();
		c.setLocalCode();
		c.setNonBlockingCode();
		c.setSequentialCode();
		return c;
	}

	public X10Context pushAssignment() {
		if (depType != null)
			assert (depType == null);
		X10Context c = (X10Context) super.pushBlock();
		c.setInAssignment();
		return c;
	}

	/**
	 * pushes an additional static scoping level.
	 */
	public Context pushStatic() {
		assert (depType == null);
		return super.pushStatic();
	}

	/**
	 * enters a method
	 */
	public Context pushCode(CodeDef ci) {
		//System.err.println("Pushing code " + ci);
		assert (depType == null);
		return super.pushCode(ci);
	}

	/**
	 * Gets the current method
	 */
	public CodeDef currentCode() {
		return depType == null ? super.currentCode() : pop().currentCode();
	}

	/**
	 * Return true if in a method's scope and not in a local class within the
	 * innermost method.
	 */
	public boolean inCode() {
		return depType == null ? super.inCode() : pop().inCode();
	}

	public boolean inAssignment() {
		return inAssignment;
	}

	public boolean inStaticContext() {
		return depType == null ? super.inStaticContext() : pop().inStaticContext();
	}

	/**
	 * Gets current class
	 */
	public ClassType currentClass() {
		return depType == null ? super.currentClass() : pop().currentClass();
	}

	/**
	 * Gets current class scope
	 */
	public ClassDef currentClassDef() {
		return depType == null ? super.currentClassDef() : pop().currentClassDef();
	}

	/**
	 * Adds a symbol to the current scoping level.
	 */
	public void addVariable(VarInstance vi) {
//		assert (depType == null);
		super.addVariable(vi);
	}

	/**
	 * Adds a named type object to the current scoping level.
	 */
	public void addNamed(Named t) {
		assert (depType == null);
		super.addNamed(t);
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
		X10TypeSystem ts = (X10TypeSystem) this.ts;
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
		    return ts.findTypeDef(container, ts.TypeDefMatcher(container, name, Collections.EMPTY_LIST, Collections.EMPTY_LIST, this), this);
		}
		catch (SemanticException e) {
		}
		return null;
	    }

	public void addNamedToThisScope(Named type) {
//		assert (depType == null);
		super.addNamedToThisScope(type);
	}

	public ClassType findMethodContainerInThisScope(Name name) {
//		assert (depType == null);
		return super.findMethodContainerInThisScope(name);

	}

	public VarInstance<?> findVariableInThisScope(Name name) {
		//if (name.startsWith("val")) Report.report(1, "X10Context_c: searching for |" + name + " in " + this);
		if (depType == null) return super.findVariableInThisScope(name);

		VarInstance<?> vi = ((X10Context_c) pop()).findVariableInThisScope(name);

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
		super.addVariableToThisScope(var);
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
	public X10Context pushDepType(polyglot.types.Ref<? extends polyglot.types.Type> ref) {
		X10Context_c v = (X10Context_c) push();
		v.depType = ref;
		v.inCode = false;
		//Report.report(1, "X10Context_c: Pushing deptype |" + type + "|" + v.hashCode());
		return v;
	}

	public X10Context pushAdditionalConstraint(CConstraint env)	throws SemanticException {
		// Now push the newly computed Gamma
		X10Context xc = (X10Context) pushBlock();
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
	public X10Context pushSuperTypeDeclaration(X10ClassDef type) {

		X10Context_c v = (X10Context_c) push();
		v.inSuperOf = type;
		v.inCode = false;
		//Report.report(1, "X10Context_c: Pushing deptype |" + type + "|" + v.hashCode());
		return v;
	}

	public String toString() {
		return "(" + (depType != null ? "depType " + depType : kind.toString())
		  + (currentConstraint !=null ? " constraint " + currentConstraint : "")
		  + (" place=" + currentPlaceTerm)
		  + (" thisPlace=" + thisPlace)
		  + " "+  mapsToString() + " " + outer + ")";
	}

	static protected int varCount = 0;

	public Name getNewVarName() {
		return Name.make(MAGIC_VAR_PREFIX + (varCount++));
	}
	
	
}
