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

import polyglot.types.Type;
import polyglot.util.InternalCompilerError;
import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.constraint.XType;

/**
 * A representation of a constrained term: an XTerm<Type> t with a constraint c
 * on the XTerm<Type> (and possibly other variables). c is required to have a bound self
 * variable, and t cannot contain that variable.
 * 
 * @author vj 09/02/09
 *
 */
public class XConstrainedTerm  {

    XTerm<Type> term;
    CConstraint constraint;

    /**
     * Return a (non-null) XConstrainedTerm built from t and c. 
     * c must have a bound self variable, and t cannot contain that variable.
     * c is modified in place.
     * @param t
     * @param c
     * @return
     * @throws XFailure
     */
    public static XConstrainedTerm make(XTerm<Type> t, CConstraint c)  {
        return new XConstrainedTerm(t, c);
    }

    /**
     * Return a (non-null) XConstrainedTerm built from t and c. The constraint
     * recorded with the returned value is c{self==t}.
     * c is modified in place.
     * @param t
     * @param c
     * @return
     * @throws XFailure
     */
    public static XConstrainedTerm instantiate(CConstraint c, XTerm<Type> t) throws XFailure {
        c.addSelfEquality(t);
        // the self variable in c is now bound.
        return new XConstrainedTerm(t, c);
    }

    /**
     * Return a (non-null) XConstrainedTerm built from t and {}.
     * @param t
     * @return
     */
    public static XConstrainedTerm make(XTerm<Type> t, Type type) {
        try {
            return instantiate(ConstraintManager.getConstraintSystem().makeCConstraint(type,type.typeSystem()), t);
        } catch (XFailure r) {
            throw new InternalCompilerError("Cannot constrain " + t);
        }
    }
    /**
     * Returns a constrained term. 
     * @param t
     * @param c
     * @throws XFailure
     */
    private XConstrainedTerm(XTerm<Type> t, CConstraint c)  {
        assert t!= null;
        assert c!= null;
        assert c.consistent();

        this.term=t;
        this.constraint=c;
    }

    public XTerm<Type> term(){return term;}
    public CConstraint constraint() { return constraint;}

    /**
     * Returns true iff every value of term() (satisfying constraint()) is also a value of t.term(),
     * and satisfies t.constraint().
     * @param t
     * @return
     */
    public boolean entails(XConstrainedTerm t) {
        assert t!= null;
        XTerm<Type> eq = ConstraintManager.getConstraintSystem().makeEquals(t.constraint().ts(), term(), t.term());
        return constraint.entails(eq) && constraint.entails(t.constraint());
    }
    /**
     * Add t1==t2 to the underlying constraint.
     * @param t1
     * @param t2
     */
    public void addBinding(XTerm<Type> t1, XTerm<Type> t2) {
        constraint.addEquality(t1, t2);
    }
    public XConstrainedTerm copy() {
        return new XConstrainedTerm(term(), constraint().copy());
    }
    /**
     * Return the constraint, instantiated with the term.
     * @return
     */
    public CConstraint xconstraint(Type t) {
        CConstraint s = constraint();
        s = s == null ? ConstraintManager.getConstraintSystem().makeCConstraint(t,t.typeSystem()) : s.copy();
        s = s.instantiateSelf(term());
        return s;
    }

    public String toString() { return term.toString() + constraint.toString();}
}
