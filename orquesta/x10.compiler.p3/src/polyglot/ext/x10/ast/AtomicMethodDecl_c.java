/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/**
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Expr;
import polyglot.ast.FlagsNode;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.X10Flags;
import polyglot.types.Flags;
import polyglot.util.Position;

/**
 * An immutable representation of an atomic X10 method -- a method declared with the atomic flag.
 * Its body has already been wrapped in an atomic statement. 
 * @author vj
 *
 */
public class AtomicMethodDecl_c extends X10MethodDecl_c {

	/**
	 * @param pos
	 * @param flags
	 * @param returnType
	 * @param name
	 * @param typeParams TODO
	 * @param formals
	 * @param where
	 * @param throwTypes
	 * @param body
	 */
	public AtomicMethodDecl_c(Position pos, FlagsNode flags,
			TypeNode returnType, Id name, List<TypeParamNode> typeParams, List<Formal> formals,
			DepParameterExpr where, List<TypeNode> throwTypes, Block body) {
		super(pos, flags, returnType, name, typeParams, formals, where, throwTypes, body);
		
	}

}
