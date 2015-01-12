/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.constraint;

import java.util.List;

/**
 * A representation of an atomic formula op(t1,..., tn).
 * 
 * @author vj
 *
 */
public interface XFormula<T> extends XTerm {
	    public boolean isAtomicFormula();
	    //public XTermKind kind();
	    public T operator();
	    public boolean isUnary();
	    public boolean isBinary();
	    public XTerm unaryArg();
	    public XTerm left();
	    public XTerm right();
	    public XTerm[] arguments();
	    public T asExprOperator(); 
}
