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
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ext.x10.types.X10ArraysMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
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
			List<Expr> arguments) {
		super(pos, target, name, Collections.EMPTY_LIST, arguments);
	
	}
	public Node typeCheck(TypeChecker tc) throws SemanticException {
        X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
        RegionMaker_c n = (RegionMaker_c) super.typeCheck(tc);
        Expr left = (Expr) n.arguments.get(0);
      
        Type type = n.type();
        type =        X10ArraysMixin.setRank(type, xts.ONE());
        type =  X10ArraysMixin.setRect(type);
        // vj: Also may wish to check for the type being int(:self==0).
        Object leftVal = left.constantValue();
        if ((leftVal instanceof Integer && ((Integer) leftVal).intValue()==0)) {
            type = X10ArraysMixin.setZeroBased(type);
        }
        
		RegionMaker result = (RegionMaker) n.type(type);
		//Report.report(1, "RegionMaker_c: type of |" + result + "| is " + result.type());
		return result;
    }
	
}
