/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types;

import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.VarDef;
import polyglot.types.VarInstance;

public interface X10Context extends Context {
	/**
	 * The prefix for compiler generated variables. No user-specified
	 * type or package or parameter name or local variable should begin
	 * with this prefix.
	 */
	String MAGIC_VAR_PREFIX = "x10$__var";
	// Use addVariable to add a PropertyInstance to the context.
	
	/**
	 * Looks up a property in the current scope.
	 * @param name
	 * @return
	 * @throws SemanticException
	 */
	X10FieldInstance findProperty(String name) throws SemanticException;
	
	/**
     * Finds the type which added a property to the scope.
     * This is usually a subclass of <code>findProperty(name).container()</code>.
     */
    ClassType findPropertyScope(String name) throws SemanticException;
	
    // Set if we are in a supertype declaration of this type. 
    boolean inSuperTypeDeclaration();
    X10ClassDef supertypeDeclarationType();
    X10Context pushSuperTypeDeclaration(X10ClassDef type);

    /** Enter the scope of a deptype. */
    X10Context pushDepType(Ref<? extends Type> ref);
    
    /** Return the current deptype, null if there is none. */
    X10NamedType currentDepType();
    Ref<? extends Type> depTypeRef();

    /** Return whether innermost scope is a deptype scope. */
    boolean inDepType();
    
    /**
     * Enter the scope of an atomic block. The body of such a block must be local,
     * sequential and nonblocking.
     * @return a new context
     */
    X10Context pushAtomicBlock(); 
    
    void setSafeCode();
    void setNonBlockingCode();
    void setSequentialCode();
    void setLocalCode();
    boolean inSafeCode();
    boolean inLocalCode();
    boolean inSequentialCode();
    boolean inNonBlockingCode();
    
    String getNewVarName();
    
    void setVarWhoseTypeIsBeingElaborated(VarDef var);
    VarDef varWhoseTypeIsBeingElaborated();

    /** Return true if within an annotation. */
    boolean inAnnotation();
    void setAnnotation();
    void clearAnnotation();
   
}
