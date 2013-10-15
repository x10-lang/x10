/**
 * 
 */
package x10.types.constraints;

import java.util.LinkedList;
import java.util.List;

import polyglot.types.Context;
import polyglot.types.Type;
import x10.constraint.XFailure;

/**
 * @author Louis Mandel
 * @author Olivier Tardieu
 *
 */
public class CNativeRequirementCollection implements CRequirementCollection {

	private List<CRequirement> requirements = new LinkedList<CRequirement>();


	@Override
	public boolean isEmpty() {
		return this.requirements.isEmpty();
	}

	/* (non-Javadoc)
	 * @see x10.types.constraints.CRequirementCollection#requirements()
	 */
	@Override
	public List<CRequirement> requirements() {
		return this.requirements;
	}

	public void add(CNativeRequirement req) {
		this.requirements.add(req);
	}

	/* (non-Javadoc)
	 * @see x10.types.constraints.CRequirementCollection#addRequirement(x10.types.constraints.CConstraint, x10.types.constraints.CConstraint)
	 */
	@Override
	public void add(CConstraint hyp, CConstraint req, Context ctx) throws XFailure {
		CNativeRequirement r = CNativeRequirement.make(hyp, req, ctx);
		this.add(r);
	}

	/* (non-Javadoc)
	 * @see x10.types.constraints.CRequirementCollection#addRequirement(polyglot.types.Type, polyglot.types.Type, polyglot.types.Context)
	 */
	@Override
	public void add(Type actual, Type expected, Context ctx) throws XFailure {
		CNativeRequirement r = CNativeRequirement.make(actual, expected, ctx);
		this.add(r);
	}

	/* (non-Javadoc)
	 * @see x10.types.constraints.CRequirementCollection#makeGuard(polyglot.types.Context)
	 */
	@Override
	public CConstraint makeGuard(Context ctx) throws XFailure {
		CConstraint result = ConstraintManager.getConstraintSystem().makeCConstraint();
		for (CRequirement req: this.requirements) {
			result.addIn(((CNativeRequirement) req).makeGuard(ctx));
		}
		if (!result.consistent()) {
			throw new XFailure("inconsitant guard");
		}
		return result;
	}

}
