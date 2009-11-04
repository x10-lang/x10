/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.types;

import java.util.List;

import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import x10.constraint.XConstraint;

public interface X10ConstructorDef extends ConstructorDef, X10ProcedureDef {
    /**
     * Set the returnType associated with this constructor.
     * @param t
     */
    void setReturnType(Ref<? extends ClassType> t);
    
    /** Return type associated with the constructor. */
    Ref<? extends Type> returnType();
    
    /** Return the constraint on properties, if any,
     * obtained from the return type of the call
     * to super in the body of this constructor. 
     * @return
     */
    Ref<XConstraint> supClause();
    
    /** Set the constraint on properties obtained from
     * the return type of the call to super. Set when type-checking
     * the code in the body of the constructor for which this is the constructor instance.
     * 
     * @param c
     */
    void setSupClause(Ref<XConstraint> c);

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
