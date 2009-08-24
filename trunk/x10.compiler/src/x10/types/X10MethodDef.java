/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.types;

import java.util.List;

import polyglot.types.MethodDef;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import x10.constraint.XTerm;

public interface X10MethodDef extends MethodDef, X10ProcedureDef {

    /** Set a flag indicating we should infer the return type. */
    boolean inferReturnType();
    void inferReturnType(boolean r);
    
    /**
     * Return an instance of this, specialized with (a) any references
     * to this in the dependent type of the result replaced by
     * selfVar of thisType or an EQV of thisType (with propagation) 
     * (b) any references to this in the dependent
     * type T of an argument replaced by selfVar of thisType or an EQV
     * at T, with no propagation.
     * @param thisType
     * @param argTypes TODO
     * @return
     * @throws SemanticException 
     */
//    X10MethodInstance instantiateForThis(ReferenceType thisType, List<Type> argTypes) throws SemanticException;
    
    Ref<XTerm> body();
    void body(Ref<XTerm> body);
    
    /**
     * Is tihs method a proto method?
     * @return
     */
    boolean isProto();
}
