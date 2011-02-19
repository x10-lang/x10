/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
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

/**
 * A context represents a stack of scopes used for looking up types, methods,
 * and variables.  To push a new scope call one of the <code>push*</code>
 * methods to return a new context.  The old context may still be used
 * and may be accessed directly through a call to <code>pop()</code>.
 * While the stack of scopes is treated functionally, each individual
 * scope is updated in place.  Names added to the context are added
 * in the current scope.
 */
public interface Context extends Resolver, Cloneable
{
    /** Deep copy the context so that it can be saved away. */
    Context freeze();
    Context shallowCopy(); // todo: we have both freeze and shallowCopy (that used to be called copy). why? get rid of shallowCopy.
    
    /** The type system. */
    TypeSystem typeSystem();

    /** Add a variable to the current scope. */
    void addVariable(VarInstance<?> vi);

    /** Add a named type object to the current scope. */
    void addNamed(Type t);

    /** Looks up a local variable or field in the current scope. */
    VarInstance<?> findVariable(Name name) throws SemanticException;

    /** Looks up a local variable or field in the current scope. */
    VarInstance<?> findVariableSilent(Name name);

    /** Get import table currently in scope. */
    ImportTable importTable();

    /** Enter the scope of a source file. */
    Context pushSource(ImportTable it);

    /** Enter the scope of a class. */
    Context pushClass(ClassDef classScope, ClassType type);

    /** Enter the scope of a method or constructor. */
    Context pushCode(CodeDef f);

    /** Enter the scope of a block. */
    Context pushBlock();

    /** Enter a static scope. In general, this is only used for
     * explicit constructor calls; static methods, initializers of static
     * fields and static initializers are generally handled by pushCode().
     */
    Context pushStatic();

    /** Pop the context. */
    Context pop();

    /** Return whether innermost non-block scope is a code scope. */
    boolean inCode();

    /** Returns whether the symbol is defined within the current method. */
    boolean isLocal(Name name);

    /** 
     * Returns whether the current context is a static context.
     * A statement of expression occurs in a static context if and only if the
     * inner-most method, constructor, instance initializer, static initializer,
     * field initializer, or explicit constructor statement enclosing the 
     * statement or expressions is a static method, static initializer, the 
     * variable initializer of a static variable, or an explicity constructor 
     * invocation statment. (Java Language Spec, 2nd Edition, 8.1.2)
     */
    boolean inStaticContext();

    /** Return the innermost class in scope. */
    X10ClassType currentClass();
 
    /** Return the innermost class in scope. */
    X10ClassDef currentClassDef();

    /** Return the innermost method or constructor in scope. */
    CodeDef currentCode();

    /** The current package, or null if not in a package. */
    Package package_();

    
    
    
    
    
    
    /**
     * The prefix for compiler generated variables. No user-specified
     * type or package or parameter name or local variable should begin
     * with this prefix.
     */
    String MAGIC_VAR_PREFIX = "x10$__var";
    // Use addVariable to add a PropertyInstance to the context.

    /** Context name table */
    String MAGIC_NAME_PREFIX = "X10$";
    Map<String,Name> contextNameTable = CollectionFactory.newHashMap();
    /** Return the same mangled name if has been created using the same string. */ 
    Name makeFreshName(String name);
    
    /** Return the locals declared in this scope (and only this scope). */
    List<LocalDef> locals();
    
    /** Current constraint on variables in scope. */
    CConstraint currentConstraint();
    void setCurrentConstraint(CConstraint c);

    /**
     * Return any known constraint on this.home (as an XConstrainedTerm).
     * If none is known, return null.
     * 
     * @return
     */
    XConstrainedTerm currentPlaceTerm();

    /**
     * Return any known constraint on the place of the lexically enclosing finish (as an XConstrainedTerm).
     * If none is known, return null.
     * 
     * @return
     */
    XConstrainedTerm currentFinishPlaceTerm();

    /**
     * Push a new context, and set currentPlaceTerm to t.
     * Intended to be set when entering the scope of a place changing control construct
     * such as at(p) S, or when entering the body of a method. 
     * @param t, t != null
     */
    Context pushPlace(XConstrainedTerm t);
    
    /**
     * Get the place for this. When entering a class decl, thisPlace
     * is set to the currentPlaceTerm. Thus |this| will not be in the currentPlace 
     * within the scope of at's in the bodies of methods in the class. 
     * 
     * @param t
     */
    XConstrainedTerm currentThisPlace();
    
    /**
     * We are entering the scope of a collecting finish. All offers
     * within this scope must return an expression of type t.
     * @param t -- the type of the collecting finish.
     * @return
     */
    Context pushCollectingFinishScope(Type t);
    
    /**
     * The type of the collecting finish whose scope we are in.
     * null if we are not in the scope of a collecting finish
     * @return
     */
    Type collectingFinishType();
    
    /** Current constraint on here. */
    //CConstraint currentPlaceConstraint();
    
    //void setCurrentPlaceConstraint(CConstraint c);

    /** Current constraint on type variables in scope */
    TypeConstraint currentTypeConstraint();
    void setCurrentTypeConstraint(Ref<TypeConstraint> c);

    /**
     * Looks up a property in the current scope.
     * @param name
     * @return
     * @throws SemanticException
     */
    X10FieldInstance findProperty(Name name) throws SemanticException;
    
    /**
     * Finds the type which added a property to the scope.
     * This is usually a subclass of <code>findProperty(name).container()</code>.
     */
    X10ClassType findPropertyScope(Name name) throws SemanticException;
    
    /**
     * Looks up a method in the current scope.
     */
    MethodInstance findMethod(TypeSystem_c.MethodMatcher matcher) throws SemanticException;

    /** Looks up a local variable in the current scope. */
    X10LocalInstance findLocal(Name name) throws SemanticException;

    /** Looks up a field in the current scope. */
    X10FieldInstance findField(Name name) throws SemanticException;

    /**
     * Finds the type which added a field to the scope.
     * This is usually a subclass of <code>findField(name).container()</code>.
     */
    X10ClassType findFieldScope(Name name) throws SemanticException;
    
    /**
     * Finds the type which added a method to the scope.
     * This is usually a subclass of <code>findMethod(name).container()</code>.
     */
    X10ClassType findMethodScope(Name name) throws SemanticException;

    // Set if we are in a supertype declaration of this type. 
    boolean inSuperTypeDeclaration();
    X10ClassDef supertypeDeclarationType();
    Context pushSuperTypeDeclaration(X10ClassDef type);

    /**
     * Disambiguating the LHS of an assignment?
     * @return
     */
    boolean inAssignment();
    void setInAssignment();
    Context pushAssignment();
    
    Context pushFinishScope(boolean isClocked);
    boolean inClockedFinishScope();
    /**
     * Push a new block, and sets its currentConstraint to old currentConstraint + env.
     * 
     * @param env: The new constraint to be pushed. Should have no self var.
     * @param env: The position in the AST this is being called (needed by SemanticException).
     * @return
     * @throw SemanticException if adding this constraint would cause inconsistency
     */
    Context pushAdditionalConstraint(CConstraint env, Position pos) throws SemanticException ;
    
    /** Enter the scope of a deptype. */
    Context pushDepType(Ref<? extends Type> ref);
    
    /** Return the current deptype, null if there is none. */
    Type currentDepType();
    Ref<? extends Type> depTypeRef();

    /** Return whether innermost scope is a deptype scope. */
    boolean inDepType();
    
    /**
     * Enter the scope of an atomic block. The body of such a block must be local,
     * sequential and nonblocking.
     * @return a new context
     */
    Context pushAtomicBlock(); 
    
    Name getNewVarName();
    
    void setVarWhoseTypeIsBeingElaborated(VarDef var);
    VarDef varWhoseTypeIsBeingElaborated();

    /** Return true if within an annotation. */
    boolean inAnnotation();
    void setAnnotation();
    void clearAnnotation();
    
    /**
     * Set that the body of a new Object() {...} has been entered. This is done
     * during code generation, e.g. for dep type casts. Now, references to this
     * must be fully qualified.
     */
    void setAnonObjectScope();
    boolean inAnonObjectScope();
    void restoreAnonObjectScope(boolean anonObjectScope);

    X10CodeDef definingCodeDef(Name name);

    XVar thisVar();

    CConstraint constraintProjection(CConstraint... cs);
    
    /** 
     * Is the current code context clocked?
     * @return
     */
    boolean isClocked();

    /**
     * Record the variable represented by vi in the enclosing closure (if any).
     */
    void recordCapturedVariable(VarInstance<? extends VarDef> vi);

    /**
     * Return the enclosing code context (or this if the current context is a code context).
     */
    Context popToCode();

    /**
     * Return the enclosing code context that can capture variables (null if none).
     */
    Context findEnclosingCapturingScope();
}
