/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Jan 19, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Receiver;
import polyglot.types.MethodInstance;

/**
 * @author vj Jan 19, 2005
 * 
 */
public interface Tuple extends Expr {
	List<Expr> arguments();
	Receiver pointReceiver();
	Receiver regionReceiver();
	MethodInstance pointMI();
	MethodInstance regionMI();

}
