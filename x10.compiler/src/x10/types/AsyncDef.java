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

import polyglot.types.ClassType;
import polyglot.types.CodeInstance;
import polyglot.types.Ref;
import polyglot.types.VarDef;
import polyglot.types.VarInstance;
import polyglot.util.Position;

/**
 * An AsyncDef represents the type information for an async.
 */
public interface AsyncDef extends X10MethodDef, EnvironmentCapture {
    
    AsyncInstance asInstance();

    /**
     * Return a copy of this with position reset.
     * @param pos
     * @return
     */
    public AsyncDef position(Position pos);

    Ref<? extends CodeInstance<?>> methodContainer();
    void setMethodContainer(Ref<? extends CodeInstance<?>> mi);

    Ref<? extends ClassType> typeContainer();
    void setTypeContainer(Ref<? extends ClassType> ct);

    boolean staticContext();
}
