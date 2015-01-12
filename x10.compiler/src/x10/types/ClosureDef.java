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
import polyglot.types.CodeInstance;
import polyglot.types.FunctionDef;

import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.VarDef;
import polyglot.types.VarInstance;
import polyglot.util.Position;

import x10.types.constraints.XConstrainedTerm;

/**
 * A ClosureDef represents the type information for a closure:
 * its formal parameters, 
 * its return types, 
 * throw types
 * a placeterm representing the place at which the body of this function is intended to execute.
 * @author vj
 *
 */
public interface ClosureDef extends FunctionDef, X10Def, X10ProcedureDef, EnvironmentCapture {
    
    ClosureInstance asInstance();
    X10ClassDef classDef();
    FunctionType asType();

    /**
     * Return a copy of this with position reset.
     * @param pos
     * @return
     */
    ClosureDef position(Position pos);

    Ref<? extends CodeInstance<?>> methodContainer();
    void setMethodContainer(Ref<? extends CodeInstance<?>> mi);
    
    Ref<? extends ClassType> typeContainer();
    void setTypeContainer(Ref<? extends ClassType> ct);
    
    //List<ParameterType> typeParameters();
    void setTypeParameters(List<ParameterType> typeParameters);
    
    boolean staticContext();
    void setStaticContext(boolean v);
}
