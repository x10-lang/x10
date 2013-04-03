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

package x10.types.constraints;

import java.util.HashMap;

import polyglot.ast.Field;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.LocalDef;
import polyglot.types.MethodDef;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;

import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import polyglot.types.Context;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ast.Field;
import polyglot.types.FieldInstance;
import polyglot.types.SemanticException;


import x10.constraint.XConstraint;
import x10.constraint.XDef;
import x10.constraint.XEQV;
import x10.constraint.XFailure;
import x10.constraint.XLit;
import x10.constraint.XType;
import x10.constraint.XUQV;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.types.X10ClassDef;
import polyglot.types.Context;
import x10.types.X10FieldDef;
import x10.types.X10LocalDef;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.xnative.CNativeConstraintSystem;


/**
 * The compiler's notion of a constraint. 
 * 
 * <p> A CConstraint<T> is an XConstraint, together with machinery to track two
 * special variables of interest to the compiler for this constraint, namely 
 * the self variable and the this variable.
 * 
 * <p>Further, the XTerms occurring in an XConstraint are created using static 
 * methods on the class CConstraintSystem, accessed through the ConstraintManager.
 * 
 * @author vj
 *
 */
public interface CConstraint extends XConstraint<Type> {
	
	/** Return the constraint system used to create this constraint.
	 */
	public CConstraintSystem sys();

    public TypeSystem ts();
    
    /**
     * Variable to use for self in the constraint.
     */
     public XVar<Type> self(); 
    /**
     * Variable to use for this in the constraint (often is null). 
     */
    public XVar<Type> thisVar(); 
    /** Return the var of this name, or null if the constraint doesn't mention it
     */
    //public XVar<Type> varByName(String name);
    /**
     * Return what, if anything, self is bound to in the current constraint.
     * @return
     */
    public XTerm<Type> selfVarBinding(); 
    
    /**
     * Return true if the constraint recursively contains a Place term. 
     * @return
     */
    //public boolean hasPlaceTerm();

    /**
     * Copy this constraint logically; that is, create a new constraint
     * that contains the same equalities (if any) as the current one.
     * vj: 08/12/09
     * Copying also the consistency, and validity status, and thisVar and self.
     * It is critical that the selfVar for the constraint's copy is the same
     * as the selfVar for the original constraint.
     * <p> Always returns a non-null constraint.
     */
    @Override
    public CConstraint copy();


    /**
     * Add constraint c into this, substituting this.self for c.self. Return this.
     * 
     * Note: this is possibly side-effected by this operation.
     * 
     * No change is made to this if c==null
     * 
     * @param c -- the constraint to be added in.
     * @return the possibly modified constraint
     */
    public void addIn(CConstraint c); 


    /** 
     * Add constraint c into this, substituting newSelf for c.self. 
     * Return this.
     * 
     * Note: this is possibly side-effected by this operation.
     * 
     * No change is made to this if c==null or c is valid.
     * 
     * @param c -- the constraint to be added in.
     * @return the possibly modified constraint
     * 
     * */
    public void addIn(XTerm<Type> newSelf, CConstraint c);

    /**
     * Add the binding selfVar == var to this constraint, possibly
     * modifying it in place.
     * @param term
     */
    public void addSelfEquality(XTerm<Type> term);

    /**
     * 
     * Add the binding selfVar != term to this constraint, possibly
     * modifying it in place.
     * @param term
     */
    public void addSelfDisEquality(XTerm<Type> term);
    /**
     * Add the binding selfVar == var to this constraint, possibly
     * modifying it in place. Also adds in the constraints on the XConstraintedTerm
     * var.
     * @param var
     */
    public void addSelfEquality(XConstrainedTerm var);

    /**
     * Add the binding thisVar == term to this constraint, possibly
     * modifying it in place. 
     * @param term
     */
    public void addThisEquality(XTerm<Type> term);

    /**
     * Set thisVar to var (if var is non-null). To be used extremely carefully. Does not change
     * terms in the constraint. So there should not be terms referring to the old thisVar.
     * @param var
     */
    public void setThisVar(XVar<Type> var);
    /**
     * Set self to var (if var is non-null). Note that this does not change the terms in the
     * constraint (to do that use instantiateSelf) 
     * @param self
     */
    
    public void setSelf(XVar<Type> var);
    
    /**
     * Add the binding s=t.term(), and add in the constraints of t into this. This constraint
     * is possibly modified in place. The constraint on t is also added in. 
     * @param s
     * @param t
     */
    public void addEquality(XTerm<Type> s, XConstrainedTerm t);
    
    /**
     * Add the binding s=t to this. This constraint is possibly modified in place.
     * @param s
     * @param t
     */
    public void addEquality(XConstrainedTerm s, XTerm<Type> t);
    
    /**
     * Add the binding s.term()=t.term() to this, and add in s.constraint() and t.constraint(). 
     * This constraint is possibly modified in place.
     * @param s
     * @param t
     */
    public void addEquality(XConstrainedTerm s, XConstrainedTerm t);

    /**
     * Substitute y for x in this, returning a new constraint.
     * 
     */
    public CConstraint substitute(XTerm<Type> y, XTerm<Type> x) throws XFailure;

    /**
     * Return a new constraint obtained from the current one by substituting
     * newSelf for this.self(). 
     * 
     * Consequently, the self variable of the returned constraint does not
     * occur in it.
     * 
     * Note, the resulting constraint may be marked inconsistent.
     * 
     * FIXME: Yoav, this should also take a term to substitute for this, and return an XConstraint
     * @param newSelf
     * @return
     */
    public CConstraint instantiateSelf(XTerm<Type> newSelf);
    
    /**
     * Return those subset of constraints in the base set of other that are 
     * <em>not</em> implied by this. That is, return the residue
     * r such that (r and this) implies other.
     * @param other -- must be checked for consistency before call is made
     * @return
     */
    @Override
    public CConstraint residue(XConstraint<Type> other);
    /**
     * Returns the thisVar of the two constraints if they have the same thisVar,
     * and throws an XFailure if they have different thisVars. If both constraints
     * are null returns null. 
     * 
     * @param t1
     * @param t2
     * @return 
     * @throws XFailure t1, t2 have different this() var
     */
    public XTerm<Type> getThisVar(CConstraint t1, CConstraint t2) throws XFailure;
    
    /**
     * Return the result of substituting each yi for xi in this.
     * 
     * The self var of the resulting constraint is guaranteed different from
     * the self var of this.
     */
    public CConstraint substitute(XTerm<Type>[] ys, XTerm<Type>[] xs) throws XFailure;
    /**
     * Check if the current constraint entails other, assuming the
     * constraints generated by the ConstaintMaker sigma. 
     * @param other
     * @param sigma
     * @return
     */
    public boolean entails(CConstraint other, ConstraintMaker sigma);

    /**
     * Return the term self.fd is bound to in the constraint, and null
     * if there is no such term.
     * 
     * @param fieldName -- Name of field
     * @return
     */
    public XTerm<Type> bindingForSelfField(XDef<Type> fd);

    /** Return the least upper bound of this and other. That is, the resulting constraint has precisely
     * the constraints entailed by both this and other.
     * @param other
     * @param t the least common ancestor of the base types of this and c2
     * @return
     */
    public CConstraint leastUpperBound(CConstraint c2, Type t);
    
    /**
     * Return the constraint obtained by existentially quantifying out the 
     * term v.
     * 
     * The self var of the resulting constraint is guaranteed different from
     * the self var of this.
     * @param v
     * @return
     */
    public CConstraint project(XVar<Type> v);

    /**
     * Return exists self.this. Guaranteed that the self var of the
     *  returned constrained does not occur in the constraint.
     *  
     * The self var of the resulting constraint is guaranteed different from
     * the self var of this.
     * @return
     */
    public CConstraint exists(); 
    /**
     * Add in the constraint c, and all the constraints associated with the
     * types of the terms referenced in t.
     * @param c the constraint to be added in
     * @param m cache of already seen terms and their constraints, to avoid infinite recursion
     * @throws XFailure
     */
    public void addSigma(CConstraint c, Map<XTerm<Type>, CConstraint> m);
    
    public void addSigma(XConstrainedTerm ct, Type t, Map<XTerm<Type>, CConstraint> m);

    /**
     * Return the constraint r generated from this by adding all the constraints
     * specified by the types of the terms occurring in this. This is done 
     * recursively. That is, for each constraint c added to r, we recursively 
     * add the constraints for the terms that occur in c.
     * NOTE: the resulting constraint will have a null self(). It is the 
     * responsibility of the caller to set the constraints' self to an appropriate 
     * value. 
     * @param m cache of seen terms
     * @return
     * @throws XFailure -- if r becomes inconsistent.
     */
    public  CConstraint constraintProjection(Map<XTerm<Type>,CConstraint> m);
	

}

