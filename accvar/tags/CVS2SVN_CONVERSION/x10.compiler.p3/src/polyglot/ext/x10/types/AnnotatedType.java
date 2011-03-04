/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package polyglot.ext.x10.types;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.util.TypedList;

public interface AnnotatedType extends Type {
    public Type baseType();
    public AnnotatedType baseType(Type baseType);
    public List<Type> annotations();
    public AnnotatedType annotations(List<Type> annotations);
}
