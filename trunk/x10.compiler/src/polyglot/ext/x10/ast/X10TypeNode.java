/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Jan 9, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.types.SemanticException;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.TypeChecker;

/**
 * @author vj Jan 9, 2005
 * @author Christian Grothoff
 */
public interface X10TypeNode extends TypeNode {
    
	GenParameterExpr gen();
    X10TypeNode gen(GenParameterExpr g);
    
    DepParameterExpr dep();
    X10TypeNode dep(DepParameterExpr d);
    X10TypeNode dep(GenParameterExpr g, DepParameterExpr d);
   
    Node typeCheckBase(TypeChecker tc)throws SemanticException;
    Node disambiguateBase(AmbiguityRemover d) throws SemanticException;

}
