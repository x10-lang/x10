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

import polyglot.types.LocalInstance;
import polyglot.types.ProcedureDef;
import polyglot.types.ProcedureInstance;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;


/**
 * @author vj
 *
 */
public interface X10ProcedureInstance<T extends ProcedureDef> extends ProcedureInstance<T> {
}
