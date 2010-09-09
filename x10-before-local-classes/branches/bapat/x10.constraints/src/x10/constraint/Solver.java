package x10.constraint;

import java.util.List;

public interface Solver {

	boolean isConsistent(List<XTerm> atoms);

	boolean isValid(List<XTerm> atoms);

	boolean entails(XConstraint env, XTerm t, XConstraint sigma);

	void addDerivedEqualitiesInvolving(XConstraint c, XTerm t) throws XFailure;

}
