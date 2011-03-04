/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.ast;

import polyglot.ast.Id;
import polyglot.ast.Term;
import polyglot.types.Ref;
import polyglot.types.Type;
import x10.types.ParameterType;

public interface TypeParamNode extends Term {
	Id name();
	TypeParamNode name(Id id);

	ParameterType type();
	TypeParamNode type(ParameterType type);
	public ParameterType.Variance variance();
	public TypeParamNode variance(ParameterType.Variance variance);
}
