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

package x10.ast;

import java.util.List;

import polyglot.types.FieldInstance;
import x10.types.X10ConstructorDef;
import x10.types.X10ConstructorInstance;

public interface AssignPropertyBody extends StmtSeq {
	public X10ConstructorDef constructorInstance();
	public List<FieldInstance> fieldInstances();
}
