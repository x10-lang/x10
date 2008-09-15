/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Formal;
import polyglot.ast.TypeNode;

public interface FunctionTypeNode extends TypeNode {

	/** Get the return type of the method. */
	public abstract TypeNode returnType();

	/** Set the return type of the method. */
	public abstract FunctionTypeNode returnType(TypeNode returnType);

	/** Get the formals of the method. */
	public abstract List<TypeParamNode> typeParameters();

	/** Set the formals of the method. */
	public abstract FunctionTypeNode typeParameters(List<TypeParamNode> typeParams);

	/** Get the formals of the method. */
	public abstract List<Formal> formals();

	/** Set the formals of the method. */
	public abstract FunctionTypeNode formals(List<Formal> formals);

	public abstract DepParameterExpr guard();

	public abstract FunctionTypeNode guard(DepParameterExpr guard);

	/** Get the exception types of the method. */
	public abstract List<TypeNode> throwTypes();

	/** Set the exception types of the method. */
	public abstract FunctionTypeNode throwTypes(List<TypeNode> throwTypes);

}
