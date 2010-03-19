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
import x10.effects.constraints.Effect;

public interface X10MethodDef extends MethodDef, X10ProcedureDef {

    /** Set a flag indicating we should infer the return type. */
    boolean inferReturnType();
    void inferReturnType(boolean r);
    
    Ref<XTerm> body();
    void body(Ref<XTerm> body);
    
    /**
     * Is this method a proto method?
     * @return
     */
    boolean isProto();
   
    Ref<? extends Effect> effect();
    
}
