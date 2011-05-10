/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import java.util.*;

import polyglot.main.Reporter;
import polyglot.util.*;
import x10.types.MethodInstance;
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
 */
public abstract class Context_c implements Context
{
    protected Context outer;
    protected TypeSystem ts;
    protected Reporter reporter;

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
    
    public static final Kind BLOCK = Kind.BLOCK;
    public static final Kind CLASS = Kind.CLASS;
    public static final Kind CODE = Kind.CODE;
    public static final Kind ASYNC = Kind.ASYNC;
    public static final Kind AT = Kind.AT;
    public static final Kind OUTER = Kind.OUTER;
    public static final Kind SOURCE = Kind.SOURCE;
    
    public Context_c(TypeSystem ts) {
        this.ts = ts;
        this.outer = null;
        this.kind = OUTER;
        this.reporter = ts.extensionInfo().getOptions().reporter;
    }
    
    public boolean isBlock() { return kind == BLOCK; }
    public boolean isClass() { return kind == CLASS; }
    public boolean isCode() { return kind == CODE; }
    public boolean isAsync() { return kind == ASYNC; }
    public boolean isAt() { return kind == AT; }
    public boolean isOuter() { return kind == OUTER; }
    public boolean isSource() { return kind == SOURCE; }

    public TypeSystem typeSystem() {
        return ts;
    }

    public Context_c shallowCopy() {
        try {
            return (Context_c) super.clone();
            // it's a shallow copy cause we do not copy types or vars or deep copy the outer. that's what we do in freeze.
        }
        catch (CloneNotSupportedException e) {
            throw new InternalCompilerError("Java clone() weirdness.");
        }
    }
    
    public Context freeze() {
        if (true) return this;
        // todo: is freezing actually needed anymore? (the guard in closures might be a problem...)
        Context_c c =  this.shallowCopy();
        c.types = types != null ? CollectionFactory.newHashMap(types) : null;
        c.vars = vars != null ? CollectionFactory.newHashMap(vars) : null;
        c.outer = outer != null ? outer.freeze() : null;
        return c;
    }

    protected Context_c push() {
        Context_c v = this.shallowCopy();
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
    protected Map<Name,Type> types;
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
    public boolean isLocal(Name name) {
        if (isClass()) {
            return false;
        }
        
        if ((isBlock() || isCode() || isAsync() || isAt()) &&
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
    public MethodInstance SUPER_findMethod(TypeSystem_c.MethodMatcher matcher) throws SemanticException {
        if (reporter.should_report(TOPICS, 3))
          reporter.report(3, "find-method " + matcher.signature() + " in " + this);

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

        throw new SemanticException("Method " + matcher.signature() + " not found.");
    }

    /**
     * Gets a local of a particular name.
     */
    public LocalInstance SUPER_findLocal(Name name) throws SemanticException {
	VarInstance<?> vi = findVariableSilent(name);

	if (vi instanceof LocalInstance) {
	    return (LocalInstance) vi;
	}

        throw new SemanticException("Local " + name + " not found.");
    }

    /**
     * Finds the class which added a field to the scope.
     */
    public ClassType SUPER_findFieldScope(Name name) throws SemanticException {
        if (reporter.should_report(TOPICS, 3))
          reporter.report(3, "find-field-scope " + name + " in " + this);

        VarInstance<?> vi = findVariableInThisScope(name);

        if (vi instanceof FieldInstance) {
            if (reporter.should_report(TOPICS, 3))
              reporter.report(3, "find-field-scope " + name + " in " + vi);
            return type;
        }

        if (vi == null && outer != null) {
            return outer.findFieldScope(name);
        }

        throw new SemanticException("Field " + name + " not found.");
    }

    /** Finds the class which added a method to the scope.
     */
    public ClassType SUPER_findMethodScope(Name name) throws SemanticException {
        if (reporter.should_report(TOPICS, 3))
          reporter.report(3, "find-method-scope " + name + " in " + this);

        if (this.currentClass() != null &&
            ts.hasMethodNamed(this.currentClass(), name)) {
            if (reporter.should_report(TOPICS, 3))
              reporter.report(3, "find-method-scope " + name + " -> " +
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
    public FieldInstance SUPER_findField(Name name) throws SemanticException {
	VarInstance<?> vi = findVariableSilent(name);

	if (vi instanceof FieldInstance) {
	    FieldInstance fi = (FieldInstance) vi;

	    if (! ts.isAccessible(fi, this)) {
                throw new SemanticException("Field " + name + " not accessible.");
	    }

            if (reporter.should_report(TOPICS, 3))
              reporter.report(3, "find-field " + name + " -> " + fi);
	    return fi;
	}

        throw new NoMemberException(NoMemberException.FIELD, "Field " + name + " not found.");
    }


    /**
     * Gets a local or field of a particular name.
     */
    public VarInstance<?> findVariableSilent(Name name) {
        if (reporter.should_report(TOPICS, 3))
          reporter.report(3, "find-var " + name + " in " + this);

        VarInstance<?> vi = findVariableInThisScope(name);

        if (vi != null) {
            if (reporter.should_report(TOPICS, 3))
              reporter.report(3, "find-var " + name + " -> " + vi);
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

    public String toString() {
        return "(" + kind + " " + mapsToString() + " " + outer + ")";
    }

    public Context SUPER_pop() {
        return outer;
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

    /**
     * Push a source file scope.
     */
    public Context pushSource(ImportTable it) {
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
    public Context SUPER_pushClass(ClassDef classScope, ClassType type) {
        if (reporter.should_report(TOPICS, 4))
          reporter.report(4, "push class " + classScope + " " + classScope.position());
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
    public Context SUPER_pushBlock() {
        if (reporter.should_report(TOPICS, 4))
          reporter.report(4, "push block");
        Context_c v = push();
        v.kind = BLOCK;
        return v;
    }

    /**
     * pushes an additional static scoping level.
     */
    public Context SUPER_pushStatic() {
        if (reporter.should_report(TOPICS, 4))
          reporter.report(4, "push static");
        Context_c v = push();
        v.staticContext = true;
        return v;
    }

    /**
     * enters a method, constructor, field initializer, or closure
     */
    public Context pushCode(CodeDef ci) {
        if (reporter.should_report(TOPICS, 4))
          reporter.report(4, "push code " + ci.position());
        Context_c v = push();
        v.kind = CODE;
        v.code = ci;
        v.inCode = true;
        v.staticContext = ci.staticContext();
        return v;
    }

    /**
     * enters an async
     */
    public Context pushAsync(CodeDef ci) {
        if (reporter.should_report(TOPICS, 4))
            reporter.report(4, "push async " + ci.position());
        Context_c v = push();
        v.kind = ASYNC;
        v.code = ci;
        v.inCode = true;
        v.staticContext = ci.staticContext();
        return v;
    }
    
    /**
     * enters an at block
     */
    public Context pushAt(CodeDef ci) {
        if (reporter.should_report(TOPICS, 4))
            reporter.report(4, "push at " + ci.position());
        Context_c v = push();
        v.kind = AT;
        v.code = ci;
        v.inCode = true;
        v.staticContext = ci.staticContext();
        return v;
    }
    
    /**
     * Gets the current method
     */
    public CodeDef currentCode() {
        return code;
    }

    /**
     * Return true if in a method's scope and not in a local class within the
     * innermost method.
     */
    public boolean inCode() {
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
    public boolean inStaticContext() {
        return staticContext;
    }

    /**
     * Gets current class
     */
    public ClassType SUPER_currentClass() {
        return type;
    }

    /**
     * Gets current class
     */
    public ClassDef SUPER_currentClassDef() {
        return scope;
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
        if (reporter.should_report(TOPICS, 3))
          reporter.report(3, "Adding type " + t.name() + " to context.");
        addNamedToThisScope(t);
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

    public ClassType findMethodContainerInThisScope(Name name) {
        if (isClass() && ts.hasMethodNamed(this.currentClass(), name)) {
            return this.type;
        }
        return null;
    }

    public VarInstance<?> findVariableInThisScope(Name name) {
        VarInstance<?> vi = null;
        
        if (vars != null) {
            vi = (VarInstance<?>) vars.get(name);
        }
        
        if (vi == null && isClass()) {
            try {
                return ts.findField(this.currentClass(), this.currentClass(), name, this);
            }
            catch (SemanticException e) {
                return null;// todo: we loose the error message! e.g., "Field XXX is ambiguous; it is defined in both ..." 
            }
        }
        return vi;
    }

    public void addVariableToThisScope(VarInstance<?> var) {
        if (vars == null) vars = CollectionFactory.newHashMap();
        vars.put(var.name(), var);
    }

    private static final Collection<String> TOPICS = 
                CollectionUtil.list(Reporter.types, Reporter.context);

}
