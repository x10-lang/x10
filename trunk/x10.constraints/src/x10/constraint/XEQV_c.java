/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
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
