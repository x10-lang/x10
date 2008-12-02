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
	 * @param thisClause
	 * @param flags
	 * @param returnType
	 * @param name
	 * @param formals
	 * @param e
	 * @param throwTypes
	 * @param body
	 */
	public AtomicMethodDecl_c(Position pos, DepParameterExpr thisClause,
			Flags flags, TypeNode returnType, Id name, List formals,
			DepParameterExpr e, List throwTypes, Block body) {
		super(pos, thisClause, flags, returnType, name, formals, e, throwTypes,
				body);
		
	}

}
