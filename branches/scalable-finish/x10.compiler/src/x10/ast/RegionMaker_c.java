/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/**
 * 
 */
package x10.ast;

import java.util.Collections;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.TypeChecker;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;

/**
 * @author vj
 *
 */
public class RegionMaker_c extends X10Call_c implements RegionMaker {

	/**
	 * @param pos
	 * @param target
	 * @param name
	 * @param arguments
	 */
	public RegionMaker_c(Position pos, Receiver target, Id name,
			List<Expr> arguments) {
		super(pos, target, name, Collections.EMPTY_LIST, arguments);
	
	}
	public Node typeCheck(ContextVisitor tc) throws SemanticException {
		X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
		RegionMaker_c n = (RegionMaker_c) super.typeCheck(tc);
		Expr left = (Expr) n.arguments.get(0);
		Type type = n.type();
		Type lType = left.type();
		if (X10TypeMixin.entails(lType, X10TypeMixin.self(lType), xts.ZERO())) {
			XVar self = X10TypeMixin.self(type);
			type = X10TypeMixin.addTerm(type, X10TypeMixin.makeZeroBased(type));
			n= (RegionMaker_c) n.type(type);
		}
	
		return n;
		   
	}
	
}
