package x10.effects.constraints;

import x10.constraint.XTerm;
import x10.constraint.XVar;

public interface ArrayElementLocs extends Locs {
	
	XTerm array();
	XTerm index();
	ArrayLocs generalize(XVar x);

}
