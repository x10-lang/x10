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
 * @author vj
 *
 */
public class XEQV_c extends XLocal_c implements XEQV {
	boolean hidden;

	public XEQV_c(XName name) {
		this(name, true);
	}

	public XEQV_c(XName name, boolean hidden) {
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
	//public String toString() {
	//	return (hidden? "exists " : "") + super.toString();
	//}


}
