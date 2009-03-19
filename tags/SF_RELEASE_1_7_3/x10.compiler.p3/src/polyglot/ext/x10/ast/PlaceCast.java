/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on May 18, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;


import polyglot.ast.Expr;


/**
 * @author vj May 18, 2005
 * 
 */
public interface PlaceCast extends Expr {
	
	 /**
     * The type to cast to.
     */
    Expr placeCastType();

    /**
     * Set the type to cast to.
     */
    PlaceCast placeCastType(Expr place);

    /**
     * The expression to cast.
     */
    Expr expr();

    /**
     * Set the expression to cast.
     */
    PlaceCast expr(Expr expr);

}
