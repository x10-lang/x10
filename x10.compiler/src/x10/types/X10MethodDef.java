/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.types;

import java.util.List;

import polyglot.types.MethodDef;
import polyglot.types.Ref;

import polyglot.types.SemanticException;
import polyglot.types.Type;
import x10.constraint.XTerm;

public interface X10MethodDef extends MethodDef, X10ProcedureDef {

    Ref<XTerm> body();
    void body(Ref<XTerm> body);

    Ref<? extends Type> offerType();
    void setOfferType(Ref<? extends Type> s);
    
    List<ParameterType> typeParameters();
    void setTypeParameters(List<ParameterType> typeParameters);

    MethodInstance asInstance();
}
