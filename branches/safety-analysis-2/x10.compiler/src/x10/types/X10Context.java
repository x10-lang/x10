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

import java.util.List;

import polyglot.types.ClassType;
import polyglot.types.CodeDef;
import polyglot.types.Context;
import polyglot.types.LocalDef;
import polyglot.types.Name;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.VarDef;
import x10.constraint.XFailure;
import x10.constraint.XRoot;
import x10.constraint.XTerm;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;
import x10.types.constraints.XConstrainedTerm;

public interface X10Context extends Context {
	/**
	 * The prefix for compiler generated variables. No user-specified
	 * type or package or parameter name or local variable should begin
	 * with this prefix.
	 */
	String MAGIC_VAR_PREFIX = "x10$__var";
	// Use addVariable to add a PropertyInstance to the context.
	
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
    ClassType findPropertyScope(Name name) throws SemanticException;
	
    // Set if we are in a supertype declaration of this type. 
    boolean inSuperTypeDeclaration();
    X10ClassDef supertypeDeclarationType();
    X10Context pushSuperTypeDeclaration(X10ClassDef type);

    /**
     * Disambiguating the LHS of an assignment?
     * @return
     */
    boolean inAssignment();
    void setInAssignment();
    X10Context pushAssignment();
    /**
     * Push a new block, and sets its currentConstraint to old currentConstraint + env.
     * 
     * @param env: The new constraint to be pushed. Should have no self var.
     * @return
     * @throw SemanticException if adding this constraint would cause inconsistency
     */
    X10Context pushAdditionalConstraint(CConstraint env) throws SemanticException ;
    
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

    CodeDef definingCodeDef(Name name);

    XRoot thisVar();

    CConstraint constraintProjection(CConstraint... cs) throws XFailure;
    
  
}
