/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.util.Copy;
import polyglot.util.CollectionUtil; import polyglot.util.Position;
import x10.util.CollectionFactory;
import x10.constraint.XFailure;
import x10.constraint.XVar;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10CodeDef;
import x10.types.X10FieldInstance;
import x10.types.X10LocalInstance;
import x10.types.MethodInstance;

import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;
import x10.types.constraints.XConstrainedTerm;
import java.util.*;

import polyglot.main.Reporter;
import polyglot.util.*;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.errors.Errors;
import x10.types.AsyncDef;
import x10.types.AtDef;
import x10.types.ConstrainedType;
import x10.types.EnvironmentCapture;
import x10.types.MacroType;
import x10.types.MethodInstance;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10CodeDef;
import x10.types.X10ConstructorDef;
import x10.types.X10FieldInstance;
import x10.types.X10LocalInstance;
import x10.types.X10MemberDef;
import x10.types.X10MethodDef;
import x10.types.X10ProcedureDef;
import x10.types.XTypeTranslator;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CQualifiedVar;
import x10.types.constraints.ConstraintManager;
import x10.types.constraints.SubtypeConstraint;
import x10.types.constraints.TypeConstraint;
import x10.types.constraints.XConstrainedTerm;
import x10.util.CollectionFactory;

/**
 * This class maintains a context for looking up named variables, types,
 * and methods.
 * It's implemented as a stack of Context objects.  Each Context
 * points to an outer context.  To enter a new scope, call one of the
 * pushXXX methods.  To leave a scope, just follow the outer() pointer.
 * NodeVisitors handle leaving scope automatically.
 * Each context object contains maps from names to variable, type, and
 * method objects declared in that scope.

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
public class Context implements Resolver, Cloneable
{
    /**
     * The prefix for compiler generated variables. No user-specified
     * type or package or parameter name or local variable should begin
     * with this prefix.
     */
    public static String MAGIC_VAR_PREFIX = "x10$__var";
    // Use addVariable to add a PropertyInstance to the context.

    /** Context name table */
    public static String MAGIC_NAME_PREFIX = "X10$";
    static Map<String,Name> contextNameTable = CollectionFactory.newHashMap();
    public static enum Kind {
        BLOCK("block"),
        CLASS("class"),
        CODE("code"),
        ASYNC("async"),
        AT("at"),
        OUTER("outer"),
        SOURCE("source");

        public final String name;
        private Kind(String name) {
            this.name = name;
        }
        @Override public String toString() {
            return name;
        }               
    }
    public enum X10Kind { None, Async, At, Finish; }
    public X10Kind x10Kind = X10Kind.None;
    static protected int varCount = 0;
    static protected int nameCount = 0;
    public static Name getNewVarName() {
        return Name.make(MAGIC_VAR_PREFIX + (varCount++));
    }

    public static Name makeFreshName(String name) {
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
    public static final Kind BLOCK = Kind.BLOCK;
    public static final Kind CLASS = Kind.CLASS;
    public static final Kind CODE = Kind.CODE;
    public static final Kind ASYNC = Kind.ASYNC;
    public static final Kind AT = Kind.AT;
    public static final Kind OUTER = Kind.OUTER;
    public static final Kind SOURCE = Kind.SOURCE;
    private static final Collection<String> TOPICS = 
        CollectionUtil.list(Reporter.types, Reporter.context);

    protected Context outer;
    protected TypeSystem ts;
    protected Reporter reporter;
    protected String name;
    protected boolean inLoopHeader;
    protected boolean inAnnotation;
    protected boolean inAnonObjectScope;
    protected boolean inAssignment;
    protected boolean inClockedFinishScope=false;
 
    protected boolean isClocked=false;
    protected Ref<? extends Type> depType = null;
    protected X10ClassDef inSuperOf = null; // in supertype of this type?
    protected XConstrainedTerm currentFinishPlaceTerm = null;
    protected ImportTable it; // import table for file
    protected Kind kind;
    protected X10ClassType type;
    protected X10ClassDef scope;
    protected X10CodeDef code;
    protected Map<Name,Type> types;
    protected Map<Name,VarInstance<?>> vars;
    protected boolean inCode;
    protected VarDef varWhoseTypeIsBeingElaborated = null;
    protected boolean staticContext; // is the context static?
    protected XConstrainedTerm currentPlaceTerm = null;
    protected CConstraint currentConstraint=null;
    protected Ref<TypeConstraint> currentTypeConstraint=null;
    protected XConstrainedTerm thisPlace = null;
    protected boolean cStackUsedUp=false;
    protected Type currentCollectingFinishType=null;
    protected Type switchType=null;
    protected Stack<Ref<CConstraint>> cStack=null;
    
    protected boolean specialAsQualifiedVar=false;
    
    public Context(TypeSystem ts) {
        this.ts = ts;
        this.outer = null;
        this.kind = OUTER;
        this.reporter = ts.extensionInfo().getOptions().reporter;
    }

    // -------- Section: Query methods --------------------------
    public boolean isBlock() { return kind == BLOCK; }
    public boolean isClass() { return kind == CLASS; }
    public boolean isCode() { return kind == CODE; }
    public boolean isAsync() { return kind == ASYNC; }
    public boolean isAt() { return kind == AT; }
    public boolean isOuter() { return kind == OUTER; }
    public boolean isSource() { return kind == SOURCE; }
    public boolean isClocked() {
        if (isClocked) return true;
        X10CodeDef cd = currentCode();
        if (cd instanceof X10MethodDef) {
            X10MethodDef md = (X10MethodDef) cd;
            return md.flags().isClocked();
        }
        return false;
    }
    public boolean inDepType() { return depType != null; }
    public boolean inLoopHeader() { return inLoopHeader; }
    public boolean inAnnotation() { return inAnnotation; }
    public boolean inAnonObjectScope() { return inAnonObjectScope;}
    public boolean inClockedFinishScope() {
        if (inClockedFinishScope) return true;
        if (outer != null) 
            return ((Context) outer).inClockedFinishScope();
        return false;
    }
  
    public boolean inSuperTypeDeclaration() { return inSuperOf != null; }
    public X10ClassDef supertypeDeclarationType() { return inSuperOf; }
    public boolean isFormalParameter(LocalDef ld) {
        CodeDef thisCode = currentCode();
        if (thisCode instanceof X10ProcedureDef) {
            for (LocalDef fd : ((X10ProcedureDef) thisCode).formalNames())
                if (ld == fd)
                    return true;
        }
        return false;
    }   
    public TypeSystem typeSystem() { return ts; }
    public Type currentDepType() { return  Types.get(depType); }
    public Ref<? extends Type> depTypeRef() { return depType;  }
    public VarDef varWhoseTypeIsBeingElaborated() {
        return varWhoseTypeIsBeingElaborated;
    }
    /** Get import table currently in scope. */
    public ImportTable importTable() { return it; }
    /** The current package, or null if not in a package. */
    public Package package_() { return Types.get(importTable().package_()); }

    Context skipDepType() {
        Context me = this;
        while (me.depType !=null)
            me = me.pop();
        return me; // could be null?
    }

    public Type currentSwitchType() {
        return switchType;
    }

    /** Return the innermost method or constructor in scope. */
    public X10CodeDef currentCode() {
        return skipDepType().code;
    }
    /**
     * Return true if in a method's scope and not in a local class within the
     * innermost method.
     ** Alternatively? Return whether innermost non-block scope is a code scope. */
    public boolean inCode() { return skipDepType().inCode; }
    public boolean inAssignment() { return inAssignment; }
    /** 
     * Returns whether the current context is a static context.
     * A statement of expression occurs in a static context if and only if the
     * inner-most method, constructor, instance initializer, static initializer,
     * field initializer, or explicit constructor statement enclosing the 
     * statement or expressions is a static method, static initializer, the 
     * variable initializer of a static variable, or an explicit constructor 
     * invocation statement. (Java Language Spec, 2nd Edition, 8.1.2)
     */
    public boolean inStaticContext() { return skipDepType().staticContext; }
    
    public boolean specialAsQualifiedVar() {
        return specialAsQualifiedVar || inDepType();
    }

    /**
     * Returns whether the particular symbol is defined locally.  If it isn't
     * in this scope, we ask the parent scope, but don't traverse to enclosing
     * classes.
     */
    public boolean isLocal(Name name) {
        Context me = this;
        while (me != null && me.depType != null) {
            me = me.pop();
        }
        if (me != null) {
            if (isClass()) {
                return false;
            }

            if ((isBlock() || isCode() || isAsync() || isAt()) &&
                    (findVariableInThisScope(name) != null 
                            || findInThisScope(ts.TypeMatcher(name)) != null)) {
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
        return false;
    }
    public boolean localHasAt(Name name) { 
        // is there an "at" until the definition of the local?
        final VarInstance<?> var = findVariableInThisScope(name);
        return var!=null && var instanceof LocalInstance 
        ? false : // no "at" found until the definition of the local
            x10Kind== X10Kind.At ? true :
                outer==null || (isCode() && !isDummyCode(currentCode())) ? false :
                    outer.localHasAt(name);
    }
    // Same as isLocal(), except async and at are considered code scopes
    public boolean isLocalExcludingAsyncAt(Name name) {
        if (isClass()) {
            return false;
        }

        if ((isBlock() || isCode() || isAsync() || isAt()) &&
                (findVariableInThisScope(name) != null 
                        || findInThisScope(ts.TypeMatcher(name)) != null)) {
            return true;
        }

        if (isCode() || isAsync() || isAt()) {
            return false;
        }

        if (outer == null) {
            return false;
        }

        return outer.isLocalExcludingAsyncAt(name);
    }
    public boolean isValInScopeInClass(Name name) {
        if (isClass()) {
            return false;
        }

        if ((isBlock() || isCode() || isAsync() || isAt()) &&
                (findVariableInThisScope(name) != null)) {
            return true;
        }

        if (outer instanceof Context) {
            return  outer.isValInScopeInClass(name);
        }
        return false;
    }
 
    public Type collectingFinishType() {
        if (currentCollectingFinishType != null)
            return currentCollectingFinishType;
        // check if you are in code.
        Context cxt = this;
        CodeDef cc = cxt.currentCode();
        if (cc instanceof X10ProcedureDef) {
            X10ProcedureDef md = (X10ProcedureDef) cc;
            while (md instanceof AtDef || md instanceof AsyncDef) {
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
        return null;
    }
    public static boolean isDummyCode(CodeDef ci) {
        return (ci instanceof AtDef || ci instanceof AsyncDef);
    }
    public boolean inAsyncScope() {
        return x10Kind== X10Kind.Async ? true :
            outer==null || (isCode() && !isDummyCode(currentCode())) ? false :
                outer.inAsyncScope();        
    }
    public boolean isSequentialAccess(boolean isSeqential, Name name) { 
        // there is no async without an enclosing finish   
        if (findVariableInThisScope(name)!=null) return isSeqential;
        if (outer!=null) {
            return
            outer.isSequentialAccess(
                                     x10Kind==X10Kind.Finish ? true :
                                         x10Kind==X10Kind.Async ? false :
                                             isSeqential,name);
        }
        return true; // not in this scope (like a field), so access is ok
    }

    protected String mapsToString() { 
        return (types == null ? "" : "types=|" + types + "|")
                + (vars == null ? "" : "vars=|" + vars + "|"); }
    
    /** Return the innermost class in scope. */
    public X10ClassType currentClass() { return skipDepType().type; }

    /**
     * Gets current class scope
     */ 
    

    CConstraint outerThisEquivalences() {
        CConstraint result = ConstraintManager.getConstraintSystem().makeCConstraint();
        Type curr = currentClass();
        List<X10ClassDef> outers = Types.outerTypes(curr); 
        for (int i=0; i < outers.size(); i++) {
            XVar base = outers.get(i).thisVar();
            for (int j=i+1; j < outers.size(); j++ ) {
                X10ClassDef y = outers.get(j);
                result.addBinding(y.thisVar(), 
                			      ConstraintManager.getConstraintSystem().makeQualifiedVar(y.asType(), base));
        }
        }
        return result;
    }
    public X10ClassDef currentClassDef() { return skipDepType().scope; }
    public XConstrainedTerm currentFinishPlaceTerm() { return currentFinishPlaceTerm;}
    public XConstrainedTerm currentPlaceTerm() { return currentPlaceTerm; }
    public CConstraint currentConstraint() {
        CConstraint result = currentConstraint;
        if (result == null) {
            result = ConstraintManager.getConstraintSystem().makeCConstraint();
            if (! inStaticContext()) {
                result.setThisVar(thisVar());
                CConstraint d = outerThisEquivalences();
               result.addIn(d);
            }
        }
        if (! cStackUsedUp) {
            // this means that cStack needs to be evaluated.
            cStackUsedUp=true;
            if (cStack!= null) {
                for (Object r : cStack.toArray()) {
                    CConstraint top = Types.get((Ref<CConstraint>) r);
                    result.addIn(top);
                }
            }
            // also get the constraint from the outer context.
            if (outer !=null) {
                // call this recursively so the outer also caches its constraint.
              //  System.out.print("^" +outer.hashCode());
                CConstraint c = outer.currentConstraint();
                if (c != null && ! c.valid())
                    result.addIn(c); // do not store it in currentConstraint.
            }
            // cache it.
            currentConstraint = result;
        }
      //  System.out.println("==>" + result);
        return result;
    }
    public TypeConstraint currentTypeConstraint() {
      //  System.out.println("tc: #" + hashCode() + ".");
        TypeConstraint result = null;
        if (currentTypeConstraint == null)
            result=new TypeConstraint();
        else  result=currentTypeConstraint.get(); 
       // System.out.println("tc: #" + hashCode() +  "==> " + result);
        return result;
    }
    public Type findInThisScope(Matcher<Type> matcher) {
        Name name = matcher.name();

        Type t = null;

        if (types != null) {
            t = types.get(name);

            if (t != null) {
                try {
                    t = matcher.instantiate(t);
                }
                catch (SemanticException e) {
                    // todo: we might have this exception:
                    // SemanticException: Call invalid; calling environment does not entail the method guard.
                    // therefore we should report the error or store it.
                    // Because we ignore it, the error message for TypedefConstraint3c_MustFailCompile is horrible!
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
                List<Type> res = ts.classContextResolver(this.currentClass(), this).find(matcher);
                t = SystemResolver.first(res);
            }
            catch (SemanticException e) {
            }
        }

        return t;
    }

    public void addNamedToThisScope(Type type) {
        if (types == null) types = CollectionFactory.<Name, Type>newHashMap();
        types.put(type.name(), type);
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


    public CConstraint constraintProjection(CConstraint... cs)  {
        Map<XTerm, CConstraint> m = CollectionFactory.newHashMap();

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
            if (! r.consistent()) // r is inconsistent, no point going further.
                return r;
        }
        if (r == null) 
            r = ConstraintManager.getConstraintSystem().makeCConstraint();
        // fold in the current constraint
        r.addSigma(currentConstraint(), m);
        r.addSigma(currentPlaceTerm, m);
        PlaceChecker.AddHereEqualsPlaceTerm(r, this);
        r.addSigma(thisPlace, m);
        // fold in the real clause of the base type
        Type selfType = this.currentDepType();
        if (selfType != null) {
            CConstraint selfConstraint = Types.realX(selfType);
            if (selfConstraint != null) {
                r.addIn(selfConstraint.instantiateSelf(r.self()));
            }
        }
        return r;
    }
    // to check if we can call property(...) or assign to final fields
    // FIXME: [IP] I think this is subtly wrong -- what if there is an intervening block?
    public X10ConstructorDef getCtorIgnoringAsync() {
        return inCode() && currentCode() instanceof X10ConstructorDef? (X10ConstructorDef) currentCode() :
            x10Kind==X10Kind.Async ? outer.getCtorIgnoringAsync() :
                null;
    }

    public List<LocalDef> locals() {
        if (vars != null) {
            List<LocalDef> lis = allLocals();
            if (lis.isEmpty())
                return lis;
            Context c =  pop();
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
            Context c =  pop();
            if (c != null)
                lis.addAll(c.allLocals());
            return lis;
        }
        else {
            Context c =  pop();
            if (c != null)
                return c.allLocals();
        }
        return Collections.<LocalDef>emptyList();
    }

  

    /**
     * Looks up a method with name "name" and arguments compatible with
     * "argTypes".
     */
    public MethodInstance findMethod(TypeSystem_c.MethodMatcher matcher) throws SemanticException {
        if (reporter.should_report(TOPICS, 3))
            reporter.report(3, "find-method " + matcher.signature() + " in " + this);
        Context me = this;
        while (me != null && me.depType != null) {
            me = me.pop();
        }
        if (me != null) {
            // Check for any method with the appropriate name.
            // If found, stop the search since it shadows any enclosing
            // classes method of the same name.
            if (this.currentClass() != null &&
                    ts.hasMethodNamed(this.currentClass(), matcher.name())) {
                if (reporter.should_report(TOPICS, 3))
                    reporter.report(3, "find-method " + matcher.signature() + " -> " +
                                    this.currentClass());
                // Found a class that has a method of the right name.
                // Now need to check if the method is of the correct type.
                return ts.findMethod(this.currentClass(), matcher.container(this.currentClass()));
            }

            if (outer != null) {
                return outer.findMethod(matcher);
            }
        }
        throw new SemanticException("Method " + matcher.signature() + " not found.");
    }

    /**
     * Gets a local variable of a particular name.
     */
    public X10LocalInstance findLocal(Name name) throws SemanticException {
        Context me = this;
        while (me != null && me.depType != null)
            me = me.pop();
        if (me != null) {
            VarInstance<?> vi = findVariableSilent(name);
            if (vi instanceof X10LocalInstance) {
                return (X10LocalInstance) vi;
            }
        }
        throw new SemanticException("Local " + name + " not found.");
    }

    /**
     * Gets a field of a particular name. The lookupContext is the context in 
     * which the field is being looked up, this is typically a lower context 
     * than the class context in which the field is defined. This lower context
     * may have additional constraints, such as the type bounds for the 
     * type parameters of the class, and the class invariant pushed in. This
     * information may be necessary to ensure that this field access is 
     * consistent.
     */
    public X10FieldInstance findField(Name name, Context lookupContext) throws SemanticException {
        VarInstance<?> vi = findVariableSilent(name, lookupContext);
        if (vi instanceof FieldInstance) {
            X10FieldInstance fi = (X10FieldInstance) vi;
            if (! ts.isAccessible(fi, this)) {
                throw new SemanticException("Field " + name + " not accessible.");
            }
            if (reporter.should_report(TOPICS, 3))
                reporter.report(3, "find-field " + name + " -> " + fi);
            return fi;
        }
        throw new NoMemberException(NoMemberException.FIELD, 
                                    "Field " + name + " not found.");
    }


    /** Looks up a local variable or field in the current scope. */
    public VarInstance<?> findVariableSilent(Name name) {
        return findVariableSilent(name, this);
    }
    /**
     * See the comment for findField(Name, Context) for a discussion of the
     * importance of lookupContext.
     * @param name
     * @param lookupContext
     * @return
     */
    public VarInstance<?> findVariableSilent(Name name, Context lookupContext) {
        if (reporter.should_report(TOPICS, 3))
            reporter.report(3, "find-var " + name + " in " + this);

        VarInstance<?> vi = findVariableInThisScope(name, lookupContext);

        if (vi != null) {
            if (reporter.should_report(TOPICS, 3))
                reporter.report(3, "find-var " + name + " -> " + vi);
            return vi;
        }

        if (outer != null) {
            return outer.findVariableSilent(name, lookupContext);
        }

        return null;
    }
    public X10ClassType findMethodContainerInThisScope(Name name) {
        if (isClass() && ts.hasMethodNamed(this.currentClass(), name)) {
            return this.type;
        }
        return null;
    }
    /**
     * Finds the class which added a method to the scope. Do not
     * search the current scope if depType !=null, since that does not contribute methods.
     * In fact, it should be an error for this method to be called when
     * deptype is true.
     */
    public X10ClassType findMethodScope(Name name) throws SemanticException {
        X10ClassType result=null;
        if (reporter.should_report(TOPICS, 3))
            reporter.report(3, "find-method-scope " + name + " in " + this);

        if (this.currentClass() != null &&
                ts.hasMethodNamed(this.currentClass(), name)) {
            if (reporter.should_report(TOPICS, 3))
                reporter.report(3, "find-method-scope " + name + " -> " +
                                this.currentClass());
            result= this.currentClass();
            if (result == null) {
                // hack. This is null when this context is in a deptype, and the deptype
                // is not a classtype, and the field belongs to the outer type, e.g.
                // class Foo { int(:v=0) v; }
                ClassType r = type;
                result = pop().type;
            }
            return result;
        } 

        if (outer != null) {
            result= outer.findMethodScope(name);
            if (result == null) {
                // hack. This is null when this context is in a deptype, and the deptype
                // is not a classtype, and the field belongs to the outer type, e.g.
                // class Foo { int(:v=0) v; }
                ClassType r = type;
                result =  pop().type;
            }
            return result;
        }
        throw new SemanticException("Method " + name + " not found.");
    }
    /**
     * Finds the class which added a field to the scope.
     */
    public X10ClassType findFieldScope(Name name) throws SemanticException {
        return findFieldScope(name, this);
    }
    public X10ClassType findFieldScope(Name name, Context lookupContext) throws SemanticException {
        VarInstance<?> vi = findVariableInThisScope(name, lookupContext);
        if (vi instanceof FieldInstance) {
            X10ClassType result = type;
            if (result != null)
                return result;
            if (inDepType())
                result = pop().type;
            if (result != null)
                return result;
            if (supertypeDeclarationType() != null)
                result = supertypeDeclarationType().asType();
            if (result != null)
                return result;
        }
        if (vi == null && pop() != null) {
            return pop().findFieldScope(name, lookupContext);
        }
        throw new SemanticException("Field " + name + " not found.");
    }

    public Type findInThisScope(Name name) {
        if (types != null) {
            Type t = types.get(name);
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
                Type t = findMemberTypeInThisScope(name, container);
                if (t != null) return t;
            }
        }
        if (inDepType()) {
            Type container = currentDepType();
            Type t = findMemberTypeInThisScope(name, container);
            if (t != null) return t;
        }
        return null;
    }
    public Context findEnclosingCapturingScope() {
        Context c = popToCode();
        while (c != null && !(c.currentCode() instanceof EnvironmentCapture)) {
            c = c.pop().popToCode();
        }
        assert (c == null || c.isCode() || c.isAsync() || c.isAt());
        if (c != null && c.currentCode() instanceof EnvironmentCapture)
            return c;
        return null;
    }

    private Type findMemberTypeInThisScope(Name name, Type container) {
        TypeSystem ts = typeSystem();
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
            return t;
        }
        catch (SemanticException e) {
        }
        try {
            return ts.findTypeDef(container, name, Collections.<Type>emptyList(), Collections.<Type>emptyList(), this);
        }
        catch (SemanticException e) {
        }
        return null;
    }
    
    public VarInstance<?> findVariableInThisScope(Name name) {
        return findVariableInThisScope(name, this);
    }

    /**
     * See the comment for findField(Name, Context) for a discussion of the
     * importance of lookupContext.
     * @param name
     * @param lookupContext
     * @return
     */
    public VarInstance<?> findVariableInThisScope(Name name, Context lookupContext) {
        //if (name.startsWith("val")) Report.report(1, "X10Context: searching for |" + name + " in " + this);
        if (depType == null) return superFindVariableInThisScope(name, lookupContext);

        VarInstance<?> vi =  pop().findVariableInThisScope(name, lookupContext);

        if (vi instanceof LocalInstance) return vi;
        // otherwise it is a FieldInstance (might be a PropertyInstance, which is a FieldInstance)
        // See if the currentDepType has a field of this name. If so, that gets priority
        // and should be returned. The receiver must treat it as the reference
        // self.name.
        try {
            if (depType instanceof X10ClassType) {
                X10ClassType dep = (X10ClassType) this.depType;
                TypeSystem ts = typeSystem();
                X10FieldInstance myVi = ts.findField(dep, dep, name, this);
                if (myVi != null) {
                    return myVi;
                }
            }
        } catch (SemanticException e) { }
        return vi;
    }

    VarInstance<?> superFindVariableInThisScope(Name name, Context lookupContext) {
        VarInstance<?> vi = null;

        if (vars != null) {
            vi = (VarInstance<?>) vars.get(name);
        }

        if (vi == null && isClass()) {
            try {
                return ts.findField(this.currentClass(), this.currentClass(), name, lookupContext);
            }
            catch (SemanticException e) {
                return null;// todo: we loose the error message! e.g., "Field XXX is ambiguous; it is defined in both ..." 
            }
        }
        return vi;
    }


    public X10FieldInstance findProperty(Name name) throws SemanticException {
        X10FieldInstance pi = null;
        FieldInstance fi = findField(name, this);
        if (fi instanceof X10FieldInstance) {
            pi = (X10FieldInstance) pi;
        }
        return pi;
    }

    public X10ClassType findPropertyScope(Name name) throws SemanticException {
        return findFieldScope(name, this);
    }
    
    /**
     * Finds the definition of a particular type.
     */
    public List<Type> find(Matcher<Type> matcher) throws SemanticException {
        if (reporter.should_report(TOPICS, 3))
            reporter.report(3, "find-type " + matcher.signature() + " in " + this);

        if (isOuter())
            return ts.systemResolver().find(QName.make(null, matcher.name())); // NOTE: looking up a short name
        if (isSource())
            return it.find(matcher);

        Type type = findInThisScope(matcher);

        if (type != null) {
            if (reporter.should_report(TOPICS, 3))
                reporter.report(3, "find " + matcher.signature() + " -> " + type);
            return CollectionUtil.<Type>list(type);
        }

        if (outer != null) {
            return outer.find(matcher);
        }

        throw new SemanticException("Type " + matcher.signature() + " not found.");
    }
    
    // -------- Section: Pop and Push methods --------------------------
    /** Pop the context. */
    public Context pop() { return outer; }
    protected Context push() {
        Context v = this.shallowCopy();
        v.outer = this;
        v.types = null;
        v.vars = null;
        v.depType = null;
        v.name = null;
        v.currentConstraint=null;
        v.cStack= null;
        v.cStackUsedUp=false;
        //      v.varWhoseTypeIsBeingElaborated = null;
        // Do not set the inXXXCode attributes to false, inherit them from parent.
        
        return v;
    }
    public Context pushClockedContext() {
        Context v = pushBlock();
        v.isClocked = true;
        
        return v;
    }
    /** Enter the scope of a source file. */
    public Context pushSource(ImportTable it) {
        assert (depType == null);
        if (reporter.should_report(TOPICS, 4))
            reporter.report(4, "push source");
        Context v = push();
        v.kind = SOURCE;
        v.it = it;
        v.inCode = false;
        v.staticContext = false;
        
        return v;
    }

    /** Enter a static scope. In general, this is only used for
     * explicit constructor calls; static methods, initializers of static
     * fields and static initializers are generally handled by pushCode().
     */
    public Context pushStatic() {
        assert (depType == null);
        if (reporter.should_report(TOPICS, 4))
            reporter.report(4, "push static");
        Context v;
        if (isCode() || isAsync() || isAt()) {
            v = pushBlock();
        } else {
            v = push();
        }
        v.staticContext = true;
        return v;
    }

    /** Enter the scope of a method, constructor, field initializer, or closure. */
    public Context pushCode(X10CodeDef ci) {
        assert (depType == null);
        if (reporter.should_report(TOPICS, 4))
            reporter.report(4, "push code " + ci.position());
        Context v = push();
        v.kind = CODE;
        v.code = ci;
        v.inCode = true;
        v.staticContext = ci.staticContext();
        
        return v;
    }

    /** Enter the scope of an async. */
    public Context pushAsync(X10CodeDef ci) {
        if (reporter.should_report(TOPICS, 4))
            reporter.report(4, "push async " + ci.position());
        Context v = push();
        v.kind = ASYNC;
        v.code = ci;
        v.inCode = true;
        v.staticContext = ci.staticContext();
        
        return v;
    }

    /** Enter the scope of an at. */
    public Context pushAt(X10CodeDef ci) {
        if (reporter.should_report(TOPICS, 4))
            reporter.report(4, "push at " + ci.position());
        Context v = push();
        v.kind = AT;
        v.code = ci;
        v.inCode = true;
        v.staticContext = ci.staticContext();
        
        return v;
    }


    public Context pushAtomicBlock() {
        assert (depType == null);
        if (reporter.should_report(TOPICS, 4))
            reporter.report(4, "push atomic block");
        Context v = pushBlock();
        return v;
    }

    public Context pushAssignment() {
        if (reporter.should_report(TOPICS, 4))
            reporter.report(4, "push assignment");
        assert (depType == null);
        Context v = pushBlock();
        v.setInAssignment();
        return v;
    }

    public Context pushSwitchType(Type st) {
        if (reporter.should_report(TOPICS, 4))
            reporter.report(4, "push switch type: "+st);
        assert (depType == null);
        Context v = pushBlock();
        v.setSwitchType(st);
        return v;
    }

    /** Enter the scope of a block. */
    public Context pushBlock() {
        if (reporter.should_report(TOPICS, 4))
            reporter.report(4, "push block");
        Context v = push();
        v.kind = BLOCK;
        return v;
    }
    
    /**
     * In a context marked as SpecialAsQualifiedVar a qualified this, 
     * A.this is returned as a 
     * QualifiedVar instead of the this var in the appropriate outer 
     * context.
     */
    
    public Context pushSpecialAsQualifiedVar() {
        Context c = pushBlock();
        c.specialAsQualifiedVar=true;
        return c;
    }
    public Context pushFinishScope(boolean isClocked) {
        if (reporter.should_report(TOPICS, 4))
            reporter.report(4, "push finish scope");
        Context v = pushBlock();
        v.x10Kind = X10Kind.Finish;
        v.inClockedFinishScope = isClocked;
        v.currentFinishPlaceTerm = v.currentPlaceTerm;
        return v;
    }
    /**
     * Pushes on a deptype. Treat this as pushing a class.
     */
    public Context pushDepType(polyglot.types.Ref<? extends polyglot.types.Type> ref) {
        if (reporter.should_report(TOPICS, 4))
            reporter.report(4, "push dep type");
        Context v = pushBlock();
        v.depType = ref;
        v.inCode = false;
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
    public Context pushClass(X10ClassDef classScope, X10ClassType type) {
        assert (depType == null);

        Context result = this;
        // Pushing a nested (non-inner) class should be done in a static context
        if (classScope.isMember() && classScope.flags().isStatic()) {
            result = pushStatic();
        }
        if (reporter.should_report(TOPICS, 4))
            reporter.report(4, "push class " + classScope + " " + classScope.position());
        Context v = result.push();
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
    public Context pushSuperTypeDeclaration(X10ClassDef type) {
        if (reporter.should_report(TOPICS, 4))
            reporter.report(4, "push super type");
        Context v = pushBlock();
        v.inSuperOf = type;
        v.inCode = false;
        
        return v;
    }
    public Context pushAdditionalConstraint(CConstraint env, Position pos)  throws SemanticException {
        // Now push the newly computed Gamma
        Context xc =  pushBlock();
        CConstraint c = xc.currentConstraint();
        if (c == null) {
            c = env;
        } else {
            c = c.copy();
            c.addIn(env);
            // c.addIn(xc.constraintProjection(c));
            if (! c.consistent())
                throw new Errors.InconsistentContext(env, pos);
        }
        xc.setCurrentConstraint(c);
        //            xc.setCurrentTypeConstraint(tenv);
        return xc;
    }
    public Context popToCode() {
        Context c = this;
        while (c != null && !c.isCode() && !c.isAsync() && !c.isAt()) {
            c = c.pop();
        }
        return c;
    }
    
    
    // -------------Section: Setter methods, updating current context--------
    
    public void setName(String n) { name = n;  }
    public void restoreAnonObjectScope(boolean s) { inAnonObjectScope=s;}
    public void setInAssignment() { inAssignment = true;}
    public void setLoopHeader() { inLoopHeader = true; }
    public void setAnnotation() { inAnnotation = true; }
    public void setAnonObjectScope() { inAnonObjectScope = true;}
    public void clearAnnotation() { inAnnotation = false; }
    public void setCurrentTypeConstraint(Ref<TypeConstraint> c) {
        currentTypeConstraint = c;
    }
    public void setTypeConstraintWithContextTerms(TypeConstraint c) {  
        TypeConstraint equals = new TypeConstraint();
        //if (currentTypeConstraint!=null) equals.addIn(currentTypeConstraint.get());
        equals.addIn(c);   
        if (currentTypeConstraint != null) {
            equals.addIn(currentTypeConstraint.get());
        } 
        setCurrentTypeConstraint(Types.ref(equals));
    }
    
    public void setTypeConstraintWithContextTerms(final Ref<TypeConstraint> c) {  
       // System.out.println("Context (#" + hashCode() + ") setting with" + c);
        if (currentTypeConstraint == null) {
            setCurrentTypeConstraint(c);
            return;
        }
        // Else add the lazy conjunction of c and currentTypeConstraint.
            final Ref<TypeConstraint> old = currentTypeConstraint;
            final LazyRef<TypeConstraint> ref 
            = new LazyRef_c<TypeConstraint>(new TypeConstraint());
            Runnable runnable = new Runnable() {
                public void run() {
                    TypeConstraint result = new TypeConstraint();
                    result.addIn(c.get());
                    result.addIn(old.get());
                    ref.update(result);
                }
                public String toString() {
                    return "and("  + old +"," + c + ")";
                }
            };
            ref.setResolver(runnable);
            setCurrentTypeConstraint(ref);
        /*TypeConstraint equals = new TypeConstraint();
        //if (currentTypeConstraint!=null) equals.addIn(currentTypeConstraint.get());
        equals.addIn(c);   
        if (currentTypeConstraint != null) {
            equals.addIn(currentTypeConstraint.get());
        } 
        setCurrentTypeConstraint(Types.ref(equals));*/
    }
    public void setTypeConstraint(TypeConstraint c) {
        setTypeConstraint(c.terms());
    }
    public void setTypeConstraint(Collection<SubtypeConstraint> d) {
        TypeConstraint equals = new TypeConstraint();
        //if (currentTypeConstraint!=null) equals.addIn(currentTypeConstraint.get());
        equals.addTerms(d);   
       // if (currentTypeConstraint != null) {
       //     equals.addIn(currentTypeConstraint.get());
       // } 
        setCurrentTypeConstraint(Types.ref(equals));
    }
    public void setPlace(XConstrainedTerm t) {
        //assert t!= null;
        //X10Context cxt = (X10Context) SUPER_pushBlock();
        currentPlaceTerm = t;
    }
    public void setSwitchType(Type st) {
        switchType = st;
    }
    public void setCollectingFinishScope(Type t) {
        assert t!=null;
        //X10Context cxt = (X10Context) SUPER_pushBlock();
        currentCollectingFinishType =t;
        //return cxt;
    }
    
    public X10CodeDef definingCodeDef(Name name) {
        if ((isBlock() || isCode() || isAsync() || isAt()) &&
                (findVariableInThisScope(name) != null 
                        || findInThisScope(name) != null)) {
            return currentCode();
        }
        return pop().definingCodeDef(name);
    }
    /**
     * Adds a symbol to the current scoping level.
     */
    public void addVariable(VarInstance<?> vi) {
        if (reporter.should_report(TOPICS, 3))
            reporter.report(3, "Adding " + vi.name() + " to context.");
        addVariableToThisScope(vi);
    }

    /**
     * Adds a named type object to the current scoping level.
     */
    public void addNamed(Type t) {
        assert (depType == null);
        if (reporter.should_report(TOPICS, 3))
            reporter.report(3, "Adding type " + t.name() + " to context.");
        addNamedToThisScope(t);
    }

   
    public void addVariableToThisScope(VarInstance<?> var) {
        if (vars == null) vars = CollectionFactory.newHashMap();
        vars.put(var.name(), var);
    }
    public void recordCapturedVariable(VarInstance<? extends VarDef> vi) {
        Context c = findEnclosingCapturingScope();
        if (c == null)
            return;
        VarInstance<?> o = c.pop().findVariableSilent(vi.name());
        if (vi == o || (o != null && vi.def() == o.def()))
            ((EnvironmentCapture) c.currentCode()).addCapturedVariable(vi);
    }




    public void setVarWhoseTypeIsBeingElaborated(VarDef var) {
        varWhoseTypeIsBeingElaborated = var;
    }  
    public XConstrainedTerm currentThisPlace() { return thisPlace; }
    public void addConstraint(Ref<CConstraint> c) {
        if (cStack==null)
            cStack = new Stack<Ref<CConstraint>>();
        cStack.push(c);
    }

    public void setCurrentConstraint(CConstraint c) {
        currentConstraint = c;
    }
    /**
     * Add the real clause for the current class/struct to the current constraint.
     * If force is true, then force the computation of the real clause if it is not 
     * yet known.
     * @param force
     */
 /*   public void addInClassInvariantIfNeeded(boolean force) {
        CodeDef cd = currentCode();
        if (cd !=null && cd instanceof MethodDef) {
            MethodDef md =  (MethodDef) cd;
            if (!md.flags().isStatic()) {
                // this call occurs in the body of an instance method for T.
                // Pick up the real clause for T -- that information is known 
                // statically about "this"
                Ref<? extends ContainerType> container = md.container();
                if (container.known()) { 
                    X10ClassType type = (X10ClassType) Types.get(container);
                    Ref<CConstraint> rc = type.x10Def().realClause();
                    addConstraint(rc);
                    Ref<TypeConstraint> tc = type.x10Def().typeBounds();
                    if (tc != null) {
                        setCurrentTypeConstraint(tc); 
                    }
                    
                        if (rc != null && (force || rc.known())) { // do not trigger prematurely
                            TypeSystem ts = typeSystem();
                            if (! ts.isUnknown(type)) {
                                CConstraint env = rc.get();
                                if (env !=null) {
                                    XVar  containerThis = thisVar();
                                    if (containerThis !=null)
                                        env=env.instantiateSelf(containerThis);
                                    if (! env.valid()) {
                                        if (currentConstraint == null)
                                            currentConstraint=env;
                                        else 
                                            currentConstraint.addIn(env);
                                    }
                                }
                            }
                        }
                     
                }
            }
        }
    }
    */
    
    // todo: we have both freeze and shallowCopy (that used to be called copy). why? get rid of shallowCopy.
    public Context shallowCopy() {
        try {
            Context res =  (Context) super.clone();
            res.x10Kind = X10Kind.None;
            return res;
            // it's a shallow copy cause we do not copy types or vars or deep copy the outer. that's what we do in freeze.
        }
        catch (CloneNotSupportedException e) {
            throw new InternalCompilerError("Java clone() weirdness.");
        }
    }

    /** Deep copy the context so that it can be saved away. */
    public Context freeze() {
        if (true) return this;
        // todo: is freezing actually needed anymore? (the guard in closures might be a problem...)
        Context c =  this.shallowCopy();
        c.types = types != null ? CollectionFactory.newHashMap(types) : null;
        c.vars = vars != null ? CollectionFactory.newHashMap(vars) : null;
        c.outer = outer != null ? outer.freeze() : null;
        return c;
    }

    static <T> String stackToString(Stack<T> stack) {
        String str = "|";
        for (T t : stack) {
            str += t.toString() + " ";
        }
        return str+"|";
    }
    private String indentedString(String prefix) {
        return "(#" + hashCode() + " " + kind + (name ==null ? "" : " " + name) + " "+  mapsToString()
        + (depType == null ? "" : "\n " + prefix + " depType=|" + depType + "|")
        + (cStack == null || cStack.isEmpty() ? "" : ("\n" + prefix + " cStack " 
            + (cStackUsedUp? "(used)" : "") + "=" + stackToString(cStack)))
            + (currentConstraint ==null ? "" : "\n " + prefix + " constraint=|" + currentConstraint+"|")
            + (currentTypeConstraint ==null ? "" : "\n " + prefix + " type constraint=|" + currentTypeConstraint+"|")
            + (currentPlaceTerm == null ? "" : "\n " + prefix + " place=" + currentPlaceTerm)
            + (thisPlace == null? "" : "\n " +  prefix + " home(this)=" + thisPlace.toString())
            + (outer == null? "" : " \n " + prefix + " outer=" + outer.indentedString(prefix + "  ") + ")")
            +")";
    }
    public String toString() {
        return indentedString("");
    }
}
