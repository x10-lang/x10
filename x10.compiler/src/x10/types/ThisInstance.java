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

import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.VarInstance;
import x10.types.constants.ConstantValue;

public interface ThisInstance extends VarInstance<ThisDef>, TypeObject, X10Use<ThisDef> {
    ThisInstance flags(Flags flags);
    ThisInstance name(Name name);
    ThisInstance type(Type type);
    ThisInstance constantValue(ConstantValue o);
    ThisInstance notConstant();
    ThisInstance error(SemanticException e);
}
