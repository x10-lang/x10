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
import polyglot.ext.x10.types.constr.C_Here_c;
import polyglot.ext.x10.types.constr.C_Lit;
import polyglot.ext.x10.types.constr.C_Lit_c;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.main.Report;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;

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
			List arguments) {
		super(pos, target, name, arguments);
	
	}
	public Node typeCheck(TypeChecker tc) throws SemanticException {
        X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
        RegionMaker_c n = (RegionMaker_c) super.typeCheck(tc);
        Expr left = (Expr) n.arguments.get(0);
      
        X10ParsedClassType type = (X10ParsedClassType) n.type();
        type =        type.setRank(xts.ONE());
        type =  type.setRect();
        // vj: Also may wish to check for the type being int(:self==0).
        Object leftVal = left.constantValue();
        if ((leftVal instanceof Integer && ((Integer) leftVal).intValue()==0)) {
            type = type.setZeroBased();
        }
        
		RegionMaker result = (RegionMaker) n.type(type);
		//Report.report(1, "RegionMaker_c: type of |" + result + "| is " + result.type());
		return result;
    }
	
}
