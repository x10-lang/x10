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

package x10.constraint;

import java.util.List;

/**
 * Represents t1 + t2
 * 
 * Intended as placeholder for special treatment for propagation rules and simplification rules
 * for +.
 * @author vj
 *
 */
public class XPlus_c extends XFormula_c {

	public XPlus_c(List<XTerm> args) {
		super(XTerms.plusName, args);
	}

	public XPlus_c(XTerm... args) {
		super(XTerms.plusName, args);
	}

	public String toString() {
		return arguments.get(0) + "+" + arguments.get(1);
	}
}
