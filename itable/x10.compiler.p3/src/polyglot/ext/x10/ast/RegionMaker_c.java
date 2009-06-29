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
//	
//	public node typecheck(typechecker tc) throws semanticexception {
//        x10typesystem xts = (x10typesystem) tc.typesystem();
//        regionmaker_c n = (regionmaker_c) super.typecheck(tc);
//        expr left = (expr) n.arguments.get(0);
//      
//        type type = n.type();
//        type =        x10arraysmixin.setrank(type, xts.one());
//        type =  x10arraysmixin.setrect(type);
//        // vj: also may wish to check for the type being int(:self==0).
//        object leftval = left.constantvalue();
//        if ((leftval instanceof integer && ((integer) leftval).intvalue()==0)) {
//            type = x10arraysmixin.setzerobased(type);
//        }
//        
//		regionmaker result = (regionmaker) n.type(type);
//		//report.report(1, "regionmaker_c: type of |" + result + "| is " + result.type());
//		return result;
//    }
	
}
