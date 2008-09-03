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

import java.util.Collections;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Id;
import polyglot.ast.Receiver;
import polyglot.util.Position;

/**
 * This needs to be pulled out in a separate class because the typeCheck code
 * may be run multiple times. Hence it does not work to run this once and then
 * produce a call. If the typecheck code is called again on the call all information
 * about the RectRegionMaker is lost.
 * @author vj
 *
 */
public class RectRegionMaker_c extends X10Call_c implements RectRegionMaker {

	/**
	 * @param pos
	 * @param target
	 * @param name
	 * @param arguments
	 */
	public RectRegionMaker_c(Position pos, Receiver target, Id name,
			List<Expr> arguments) {
		super(pos, target, name, Collections.EMPTY_LIST, arguments);
		
	}
//	public Node typeCheck(TypeChecker tc) throws SemanticException {
//        X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
//
//    	RectRegionMaker_c n = (RectRegionMaker_c) super.typeCheck(tc);
//       // Report.report(1, "Tuple_c.typecheck result had type " + n.type());
//    	Type type = n.type();
//		type = X10ArraysMixin.setRank(type, xts.xtypeTranslator().trans(arguments.size()));
//		boolean isZeroBased=true;
//		for (int i=0; i < arguments.size(); i++) {
//			Expr e = (Expr) arguments.get(i);
//			
//			X10Type t = (X10Type) e.type();
//			
//			if (! xts.isSubtype(t, xts.region())) {
//				throw new SemanticException("The argument, " + e + ", should be of type region instead of " + t
//						+".", position());
//			}
//			Type tp = t;
//			XTerm rank = X10ArraysMixin.rank(tp);
//			if (! xts.ONE().equals(rank)) {
//				// Slow!
//				FieldInstance fi = X10ArraysMixin.getProperty(tp, "rank");
//				XConstraint c = new XConstraint_c();
//				try {
//					c.addTerm(XTerms.makeEquals(xts.ONE(), xts.xtypeTranslator().trans(c.self(), fi)));
//					if (! X10TypeMixin.xclause(tp).entails(c)) {
//						throw new SemanticException("The argument, " + e + ", should be of type region{rank==1} instead of " + t
//						                            +".", position());
//					}
//				}
//				catch (XFailure e1) {
//					throw new SemanticException(e1.getMessage(), position());
//				}
//			}
//			isZeroBased &= X10ArraysMixin.isZeroBased(tp);
//			
//		}
//		if (isZeroBased) type = X10ArraysMixin.setZeroBased(type);
//		type = X10ArraysMixin.setRect(type);
//		Node ret = n.type(type);
//		//Report.report(1, "RectRegionMaker: returning  " + ret + "(#" + hashCode() + ") with type "  + ((Call) ret).type());
//		return ret;
//    }
}
