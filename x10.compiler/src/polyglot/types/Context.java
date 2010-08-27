/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import java.util.List;

import polyglot.util.Copy;

/**
 * A context represents a stack of scopes used for looking up types, methods,
 * and variables.  To push a new scope call one of the <code>push*</code>
 * methods to return a new context.  The old context may still be used
 * and may be accessed directly through a call to <code>pop()</code>.
 * While the stack of scopes is treated functionally, each individual
 * scope is updated in place.  Names added to the context are added
 * in the current scope.
 */
public interface Context extends Resolver, Copy
{
    /** Deep copy the context so that it can be saved away. */
    Context freeze();
    
    /** The type system. */
    TypeSystem typeSystem();

    /** Add a variable to the current scope. */
    void addVariable(VarInstance vi);

    /** Add a named type object to the current scope. */
    void addNamed(Named t);

    /** Looks up a method in the current scope.
     * @param formalTypes A list of <code>Type</code>.
     * @see polyglot.types.Type
     */
    MethodInstance findMethod(TypeSystem_c.MethodMatcher matcher) throws SemanticException;

    /** Looks up a local variable or field in the current scope. */
    VarInstance<?> findVariable(Name name) throws SemanticException;

    /** Looks up a local variable or field in the current scope. */
    VarInstance<?> findVariableSilent(Name name);

    /** Looks up a local variable in the current scope. */
    LocalInstance findLocal(Name name) throws SemanticException;

    /** Looks up a field in the current scope. */
    FieldInstance findField(Name name) throws SemanticException;

    /**
     * Finds the class which added a field to the scope.
     * This is usually a subclass of <code>findField(name).container()</code>.
     */
    ClassType findFieldScope(Name name) throws SemanticException;

    /**
     * Finds the class which added a method to the scope.
     * This is usually a subclass of <code>findMethod(name).container()</code>.
     */
    ClassType findMethodScope(Name name) throws SemanticException;

    /** Get import table currently in scope. */
    ImportTable importTable();

    /** Enter the scope of a source file. */
    Context pushSource(ImportTable it);

    /** Enter the scope of a class. */
    Context pushClass(ClassDef scope, ClassType type);

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
    ClassType currentClass();
 
    /** Return the innermost class in scope. */
    ClassDef currentClassDef();

    /** Return the innermost method or constructor in scope. */
    CodeDef currentCode();

    /** The current package, or null if not in a package. */
    Package package_();
}
