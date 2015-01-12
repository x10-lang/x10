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
import polyglot.types.ConstructorDef;
import polyglot.types.Ref;

import polyglot.types.SemanticException;
import polyglot.types.Type;
import x10.types.constraints.CConstraint;

public interface X10ConstructorDef extends ConstructorDef, X10ProcedureDef {
    /**
     * Set the returnType associated with this constructor.
     * @param t
     */
    void setReturnType(Ref<? extends Type> t);
    
    boolean derivedReturnType();
    void derivedReturnType(boolean r);

    /** Return type associated with the constructor. */
    Ref<? extends Type> returnType();
    
    /** Return the return type of the call
     * to super in the body of this constructor. 
     * @return
     */
    Type supType();

    /** Set the return type of the call to super. Set when type-checking
     * the code in the body of the constructor for which this is the constructor instance.
     *
     * @param c
     */
    void setSupType(Type t);

//    /**
//     * Return an instance of this, specialized with (a) any references
//     * to this in the dependent type of the result replaced by
//     * selfVar of thisType or an EQV of thisType (with propagation) 
//     * (b) any references to this in the dependent
//     * type T of an argument replaced by selfVar of thisType or an EQV
//     * at T, with no propagation.
//     * @param thisType
//     * @return
//     * @throws SemanticException 
//     */
//    X10ConstructorInstance instantiateForThis(ReferenceType thisType) throws SemanticException;
    
   
}
