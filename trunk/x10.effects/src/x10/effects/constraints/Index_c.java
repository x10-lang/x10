package x10.effects.constraints;

import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XTerm;

/**
 * Implements an index (integer or long valued expression) into an array.
 * 
 * @author vj
 *
 */
public class Index_c extends RigidTerm_c implements Index {

	public Index_c(XTerm d) {
		super(d);
	}


}
