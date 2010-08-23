/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import java.util.*;

import polyglot.main.Report;
import polyglot.util.*;
import polyglot.util.Enum;

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
public class Context_c implements Context
{
    protected Context outer;
    protected TypeSystem ts;

    public static class Kind extends Enum {
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

    public Object copy() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalCompilerError("Java clone() weirdness.");
        }
    }
    
    public Context freeze() {
        Context_c c = (Context_c) this.copy();
        c.types = types != null ? new HashMap(types) : null;
        c.vars = vars != null ? new HashMap(vars) : null;
        return c;
    }

    protected Context_c push() {
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
    public boolean isLocal(Name name) {
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
    public MethodInstance findMethod(TypeSystem_c.MethodMatcher matcher) throws SemanticException {
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
    public LocalInstance findLocal(Name name) throws SemanticException {
	VarInstance vi = findVariableSilent(name);

	if (vi instanceof LocalInstance) {
	    return (LocalInstance) vi;
	}

        throw new SemanticException("Local " + name + " not found.");
    }

    /**
     * Finds the class which added a field to the scope.
     */
    public ClassType findFieldScope(Name name) throws SemanticException {
        if (Report.should_report(TOPICS, 3))
          Report.report(3, "find-field-scope " + name + " in " + this);

	VarInstance vi = findVariableInThisScope(name);

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
    public ClassType findMethodScope(Name name) throws SemanticException {
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
    public FieldInstance findField(Name name) throws SemanticException {
	VarInstance vi = findVariableSilent(name);

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
    public VarInstance<?> findVariable(Name name) throws SemanticException {
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
    public VarInstance<?> findVariableSilent(Name name) {
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

    public String toString() {
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
    public Context pushClass(ClassDef classScope, ClassType type) {
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
    public Context pushBlock() {
        if (Report.should_report(TOPICS, 4))
          Report.report(4, "push block");
        Context_c v = push();
        v.kind = BLOCK;
        return v;
    }

    /**
     * pushes an additional static scoping level.
     */
    public Context pushStatic() {
        if (Report.should_report(TOPICS, 4))
          Report.report(4, "push static");
        Context_c v = push();
        v.staticContext = true;
        return v;
    }

    /**
     * enters a method
     */
    public Context pushCode(CodeDef ci) {
        if (Report.should_report(TOPICS, 4))
          Report.report(4, "push code " + ci + " " + ci.position());
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
    public ClassType currentClass() {
        return type;
    }

    /**
     * Gets current class
     */
    public ClassDef currentClassDef() {
        return scope;
    }

    /**
     * Adds a symbol to the current scoping level.
     */
    public void addVariable(VarInstance vi) {
        if (Report.should_report(TOPICS, 3))
          Report.report(3, "Adding " + vi + " to context.");
        addVariableToThisScope(vi);
    }

    /**
     * Adds a named type object to the current scoping level.
     */
    public void addNamed(Named t) {
        if (Report.should_report(TOPICS, 3))
          Report.report(3, "Adding type " + t + " to context.");
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

    public void addNamedToThisScope(Named type) {
        if (types == null) types = new HashMap<Name, Named>();
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
                return ts.findField(this.currentClass(), ts.FieldMatcher(this.currentClass(), name, this));
            }
            catch (SemanticException e) {
                return null;
            }
        }
        return vi;
    }

    public void addVariableToThisScope(VarInstance<?> var) {
        if (vars == null) vars = new HashMap<Name,VarInstance<?>>();
        vars.put(var.name(), var);
    }

    private static final Collection TOPICS = 
                CollectionUtil.list(Report.types, Report.context);

}
