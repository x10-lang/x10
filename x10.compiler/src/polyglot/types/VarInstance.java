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
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.types;

import x10.types.constants.ConstantValue;

public interface VarInstance<T extends VarDef> extends Use<T> {
    /**
     * The flags of the variable.
     */
    Flags flags();
//    VarInstance<T> flags(Flags flags);   // FIXME causes problems with javac; eclipse doesn't complain

    /**
     * The name of the variable.
     */
    Name name();
    VarInstance<T> name(Name name);

    /**
     * The type of the variable.
     */
    Type type();
    VarInstance<T> type(Type type);

    /**
     * The variable's constant value, or null.
     */
    ConstantValue constantValue();
    VarInstance<T> constantValue(ConstantValue o);
    VarInstance<T> notConstant();

    /**
     * Whether the variable has a constant value.
     */
    boolean isConstant();

    boolean lval();
    VarInstance<T> lval(boolean lval);
}
