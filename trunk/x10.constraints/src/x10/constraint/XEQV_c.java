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

import java.util.Collections;
import java.util.List;


/**
 * A representation of logical variables. 
 * 
 * <p> EQVs (created with hidden = true) are existentially quantified
 * in each constraint in which they occur.

  * <p> UQVs (created with hidden=false) are free in the constraint 
 * and may occur in multiple constraints. 
 * @author vj
 *
 */
public class XEQV_c extends XLocal_c implements XEQV {
	boolean hidden;


	XEQV_c(XName name, boolean hidden) {
		super(name);
		this.hidden = hidden;
	}

	public boolean hasEQV() {
		return hidden;
	}
	public boolean isEQV() {
		return hidden;
	}

	public List<XEQV> eqvs() {
		if (hasEQV())
			return Collections.<XEQV>singletonList(this);
		return Collections.EMPTY_LIST;
	}

}
