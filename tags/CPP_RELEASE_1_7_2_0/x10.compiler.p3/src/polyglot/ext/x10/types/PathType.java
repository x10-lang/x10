/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package polyglot.ext.x10.types;

import polyglot.types.MemberInstance;
import polyglot.types.Type;
import x10.constraint.XVar;

public interface PathType extends ParametrizedType, MemberInstance<TypeProperty> {
	XVar base();
	Type baseType();
	PathType base(XVar base, Type baseType);
	TypeProperty property();
}