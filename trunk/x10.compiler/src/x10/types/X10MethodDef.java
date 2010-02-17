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
     * Is this method a proto method?
     * @return
     */
    boolean isProto();
}
