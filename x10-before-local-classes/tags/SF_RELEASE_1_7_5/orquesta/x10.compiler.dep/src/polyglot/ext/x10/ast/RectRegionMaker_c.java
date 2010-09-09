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

import polyglot.ast.Expr;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.C_BinaryTerm_c;
import polyglot.ext.x10.types.constr.C_Field_c;
import polyglot.ext.x10.types.constr.C_Lit_c;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Special_c;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.types.FieldInstance;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

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
			List arguments) {
		super(pos, target, name, arguments);
		
	}
	public Node typeCheck(TypeChecker tc) throws SemanticException {
        X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
        RectRegionMaker_c n = (RectRegionMaker_c) super.typeCheck(tc);
       // Report.report(1, "Tuple_c.typecheck result had type " + n.type());
		X10ParsedClassType type = (X10ParsedClassType) n.type();
		type = type.setRank(new C_Lit_c(new Integer(arguments.size()), xts.Int()));
		boolean isZeroBased=true;
		for (int i=0; i < arguments.size(); i++) {
			Expr e = (Expr) arguments.get(i);
			
			X10Type t = (X10Type) e.type();
			
			if (! xts.isSubtype(t, xts.region())) {
				throw new SemanticException("The argument, " + e + ", should be of type region instead of " + t
						+".", position());
			}
			X10ParsedClassType tp = (X10ParsedClassType) t;
			C_Term rank = tp.rank();
			if (! xts.ONE().equals(rank)) {
				// Slow!
				C_Special self = new C_Special_c(X10Special.SELF, t);
				FieldInstance fi = xts.findField(tp, "rank", tc.context().currentClassScope());
				Constraint c = new Constraint_c(xts);
				c.addTerm(new C_BinaryTerm_c("==", xts.ONE(), new C_Field_c(fi, self), xts.Boolean()));
				if (! tp.depClause().entails(c)) {
					throw new SemanticException("The argument, " + e + ", should be of type region(:rank==1) instead of " + t
							+".", position());
				}
			}
			isZeroBased &= tp.isZeroBased();
			
		}
		if (isZeroBased) type = type.setZeroBased();
		type = type.setRect();
		Node ret = n.type(type);
		//Report.report(1, "RectRegionMaker: returning  " + ret + "(#" + hashCode() + ") with type "  + ((Call) ret).type());
		return ret;
    }
}
