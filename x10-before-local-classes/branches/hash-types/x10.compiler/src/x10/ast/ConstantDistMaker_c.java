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

import java.util.Collections;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;
import x10.constraint.XTerm;
import x10.types.X10TypeSystem;

/**
 * @author vj
 *
 */
public class ConstantDistMaker_c extends X10Call_c implements ConstantDistMaker {

	/**
	 * @param pos
	 * @param target
	 * @param name
	 * @param arguments
	 */
	public ConstantDistMaker_c(Position pos, Receiver target, Id name,
			List<Expr> arguments) {
		super(pos, target, name, Collections.EMPTY_LIST, arguments);
	}
	 
//    public Node typeCheck(TypeChecker tc) throws SemanticException {
//        X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
//        ConstantDistMaker_c n = (ConstantDistMaker_c) super.typeCheck(tc);
//        Expr left = (Expr) n.arguments.get(0);
//        Type lType = left.type();
//       // Report.report(1, "ConstantDistMaker..ltype " + left+ "(#" + left.hashCode() + ") is " + lType);
//        Type type = xts.distribution();
//		XTerm rank = X10ArraysMixin.rank(lType);
//		if (rank != null) type = X10ArraysMixin.setRank(type, rank);
//		type = X10ArraysMixin.setOnePlace(type, xts.here());
//		boolean zeroB = X10ArraysMixin.isZeroBased(lType);
//		if (zeroB) type = X10ArraysMixin.setZeroBased(type);
//		
//		boolean isRect = X10ArraysMixin.isRect(lType);
//		if (isRect) type = X10ArraysMixin.setRect(type);
//
//	        type = X10ArraysMixin.setConstantDist(type);
//
//	//Report.report(1, "ConstantDistMaker.. returning " + this + " with type "  + type);
//		return n.type(type);
//    }
    
   /* public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
    	Report.report(1, "ConstantDistMaker outputting " + arguments.get(0)
    			+ " " + name.id() + " " + arguments.get(1));
		X10NodeFactory_c.getNodeFactory()
		.Call(position(), (Expr) arguments.get(0), name, 
				(Expr) arguments.get(1)).prettyPrint(w, tr);
    }*/
}
