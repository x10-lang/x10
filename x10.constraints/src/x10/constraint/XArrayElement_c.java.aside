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
 * A representation of a(i).
 * TODO: Implement multidimensional access?
 * @author vj
 *
 */
public class XArrayElement_c extends XFormula_c  implements XArrayElement {

	public XArrayElement_c(XArray array, XTerm index) {
		super(XTerms.arrayAccessName, array, index);
		// TODO Auto-generated constructor stub
	}
	public XTermKind kind() { return XTermKind.ARRAY_ELEMENT;}
	public XArray array() { return (XArray) arguments.get(0);}
	public XTerm index() { return arguments.get(1);}
	public XVar rootVar() { return this;}
	public XVar[] vars() { return new XVar[] { this};}

}
