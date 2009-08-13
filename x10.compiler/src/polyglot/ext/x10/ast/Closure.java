/*
 * Created on Feb 26, 2007
 */
/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.CodeBlock;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.ClosureDef;

public interface Closure extends Expr, CodeBlock {
	    
    List<TypeParamNode> typeParameters();
    Closure typeParameters(List<TypeParamNode> typeParams);

    /** The closure's formal parameters.
     * @return A list of {@link polyglot.ast.Formal Formal}
     */
    List<Formal> formals();

    /** The closure's exception throw types.
     * @return A list of {@link polyglot.ast.TypeNode TypeNode}
     */
    List<TypeNode> throwTypes();

    /**
     * @return the closure's return type
     */
    TypeNode returnType();
    
    public DepParameterExpr guard();
    public Closure guard(DepParameterExpr guard);

    ClosureDef closureDef();
    Closure closureDef(ClosureDef ci);
}
