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

import java.util.List;

import polyglot.visit.TypeChecker;
import x10.types.SemanticException;
import x10.types.Type;

/**
 * @author vj Jan 19, 2005
 * 
 */
public interface Tuple extends Expr {
	List<Expr> arguments();    
	Tuple arguments(List<Expr> elements);
}
