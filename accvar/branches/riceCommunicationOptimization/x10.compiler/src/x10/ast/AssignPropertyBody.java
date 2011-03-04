/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
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
