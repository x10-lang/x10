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

import java.io.Serializable;
import java.util.List;

import polyglot.util.Copy;
import x10.constraint.XRoot;
import x10.constraint.XTerm;
import polyglot.types.Type;

public interface TypeConstraint extends Copy, Serializable {
    
    List<SubtypeConstraint> terms();
    TypeConstraint subst(XTerm y, XRoot x);
    void setInconsistent();
    boolean consistent(X10Context context);
    
    TypeConstraint addIn(TypeConstraint c);
    void addTerm(SubtypeConstraint c);
    void addTerms(List<SubtypeConstraint> terms);
    
    boolean entails(TypeConstraint c, X10Context xc);
    
    /**
     * Returns the type constraint obtained by unifying the two types.
     * The returned constraint will be inconsistent if the two types
     * are not unifiable.
     * @param t1
     * @param t2
     * @return
     */
    TypeConstraint unify(Type t1, Type t2, X10TypeSystem ts);
}