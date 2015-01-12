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

import x10.types.X10ProcedureInstance;

import java.util.List;


public interface ConstructorInstance extends X10ProcedureInstance<ConstructorDef>, MemberInstance<ConstructorDef> {
    ConstructorInstance container(ContainerType container);
    ConstructorInstance formalTypes(List<Type> formalTypes);
    ConstructorInstance throwTypes(List<Type> throwTypes);
    void setOrigMI(ConstructorInstance orig);
    ConstructorInstance origMI();
}
