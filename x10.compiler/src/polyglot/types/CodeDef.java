/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.types;

import x10.types.ParameterType;
import x10.types.X10Def;

import java.util.List;

/**
 * A <code>CodeInstance</code> contains the type information for a Java
 * code-chunk (method, constructor, initializer, closure).
 */
public interface CodeDef extends X10Def
{
    CodeInstance<?> asInstance();
    boolean staticContext();

    List<ParameterType> typeParameters();
    void setTypeParameters(List<ParameterType> typeParameters);
}
