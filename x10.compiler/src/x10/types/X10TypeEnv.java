/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.types;

import java.util.List;

import polyglot.types.ClassType;

import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeEnv;
import x10.constraint.XConstraint;
import x10.constraint.XTerm;
import polyglot.types.TypeSystem_c.Kind;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;

public interface X10TypeEnv extends TypeEnv {
   // public void checkOverride(ClassType thisType, MethodInstance mi, MethodInstance mj) throws SemanticException;

    /** Return true if the constraint is consistent. */
    boolean consistent(CConstraint c);

    /** Return true if the constraint is consistent. */
    public boolean consistent(TypeConstraint c);

    /** Return true if constraints in the type are all consistent. */
    boolean consistent(Type t);

    /**
     * Return a lit of upper bounds for type t.
     * @param t
     * @param includeObject
     * @return
     */
    List<Type> upperBounds(Type t, boolean includeObject);
    List<Type> upperBounds(Type t);

    List<Type> lowerBounds(Type t);

    List<Type> equalBounds(Type t);

    List<MacroType> findAcceptableTypeDefs(Type container, TypeDefMatcher matcher) throws SemanticException;

    boolean isSubtype(Type t1, Type t2);
   
    boolean entails(CConstraint c1, CConstraint c2);

    boolean hasSameClassDef(Type t1, Type t2);

    boolean equivClause(Type me, Type other);

    boolean entailsClause(Type me, Type other);

    boolean typeBaseEquals(Type type1, Type type2);

    boolean typeDeepBaseEquals(Type type1, Type type2);

    boolean equalTypeParameters(List<Type> a, List<Type> b);

    boolean primitiveClausesConsistent(CConstraint c1, CConstraint c2);

    boolean clausesConsistent(CConstraint c1, CConstraint c2);

    Kind kind(Type t);
    
    boolean numericConversionValid(Type toType, Type fromType, Object value);
    
    
    

}