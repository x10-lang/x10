/**
 * 
 */
package x10.types.constraints;

import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.types.Context;
import polyglot.types.LocalDef;
import polyglot.types.Name;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import x10.constraint.XConstraint;
import x10.constraint.XEQV;
import x10.constraint.XFailure;
import x10.constraint.XField;
import x10.constraint.XLit;
import x10.constraint.XLocal;
import x10.constraint.XTerm;
import x10.constraint.XUQV;
import x10.constraint.XVar;
import x10.constraint.visitors.ConstraintGenerator;
import x10.constraint.visitors.SearchValueVisitor;
import x10.types.X10CodeDef;
import x10.types.X10LocalDef;
import x10.types.X10ProcedureDef;
import x10.util.CollectionFactory;

/**
 * @author Louis Mandel
 * @author Olivier Tardieu
 * 
 */
public class CNativeRequirement implements CRequirement {

	CConstraint hypothesis;
	CConstraint require;

	private CNativeRequirement(CConstraint hyp, CConstraint req) {
        Map<XTerm, CConstraint> m = CollectionFactory.newHashMap();
        this.hypothesis = hyp;
        this.require = req;
	}

	@Override
	public CConstraint hypothesis() {
		return hypothesis;
	}

	@Override
	public CConstraint require() {
		return require;
	}

	@Override
	public String toString() {
		return hypothesis + " => " + require;
	}

	/**
	 * Build a new requirement.
	 * @param hyp -- Hypothesis in which the requirement must be satisfied.
	 * @param req -- Property that must be satisfied.
	 * @param ctx -- Context in which the requirement must be satisfied.
	 * @return
	 * @throws XFailure -- Raised if the requirement is inconsistent
	 */
	public static CNativeRequirement make (CConstraint hyp, CConstraint req, Context ctx) throws XFailure {
		CConstraint h = hyp.copy();
		h.addIn(ctx.constraintProjection(hyp, req));
		CConstraint r = h.residue(req);
		XVar selfValue = h.selfVarBinding();
		if (selfValue == null) { selfValue = ConstraintManager.getConstraintSystem().makeUQV(); } // XXX TODO check the quantifier
		CNativeRequirement result = new CNativeRequirement(h.instantiateSelf(selfValue), r.instantiateSelf(selfValue).substitute(selfValue, h.self()));
		return result;
	}

	/**
	 * Build a new requirement.
	 * @param actual -- The type of the expression.
	 * @param expected -- The type which is expected.
	 * @param ctx -- The context of the expression.
	 * @return The requirement that must be added such that the actual type implies the expected type. 
	 * @throws XFailure -- Raised if the requirement is inconsistent
	 */
	public static CNativeRequirement make (Type actual, Type expected, Context ctx) throws XFailure {
		Type actualBase = Types.baseType(actual), expectedBase = Types.baseType(expected);
		TypeSystem ts = ctx.typeSystem();
		assert ts.isSubtype(actualBase, expectedBase,  ctx);
		CConstraint 
		c = Types.xclause(actual), 
		d = Types.xclause(expected);
		c.addIn(ctx.currentConstraint());
		return make(c, d, ctx);
	}


	/**
	 * This method computes a constraints c such that c |- (h => r) and
	 * such that there is no unbound variable in c with respect to the
	 * context ctx.
	 * 
	 * A correct implementation of this method is to choose c = r' where
	 * removeUnboundVarsByReinforcement(h => r) = h' => r'
	 * because r' |- h' => r' and  (h' => r') |- (h => r).
	 * 
	 * We check that h' /\ r' is consistent to be sure that we will a
	 * correct guard.
	 * 
	 * @throws XFailure  -- if the hypothesis or the require is inconsistent
	 *                      or if the requirement contains an universally quantified variable
	 */
	public CConstraint makeGuard(Context ctx) throws XFailure {
		CNativeRequirement req = this.simplifyUnboundVars(ctx);
		req = req.removeUnboundVarsByReinforcement(ctx);
		{
			CConstraint consistancy = ConstraintManager.getConstraintSystem().makeCConstraint();
			consistancy.addIn(req.hypothesis());
			consistancy.addIn(req.require());
			if (!consistancy.consistent()) { throw new XFailure("hypothesis and require are not consistent"); }
		}
		req.checkUQVS();
		return req.require();
	}


	/**
	 * Remove all variables that are not bound in the context ctx from
	 * a requirement h => r. The resulting constraint is such that
	 * removeUnboundVarsByReinforcement(h => r) |- h => r.
	 *
	 * @param ctx
	 * @return
	 * @throws XFailure -- if the hypothesis or the require is inconsistent.
	 */
	public CNativeRequirement removeUnboundVarsByReinforcement(Context ctx) throws XFailure {
		CNativeRequirement result = this;
		for (XVar x: result.hypothesis().vars()) {
			if (!isBound(x, ctx)) {
				result = result.removeVarByReinforcement(x);
			}
		}
		for (XVar x: result.require().vars()) {
			if (!isBound(x, ctx)) {
				result = result.removeVarByReinforcement(x);
			}
		}
		return result;
	}
	
	/**
	 * Throws XFailure if the requirement contains an universally quantified variable.
	 * 
	 * @throws XFailure
	 */
	public void checkUQVS() throws XFailure {
		CNativeRequirement result = this;
		for (XVar x: result.require().vars()) {
			if ( x instanceof XUQV ) {
				throw new XFailure("the requirement contains an universally quantified variable");
			}
		}
	}

	/**
	 * Erase a variable from a requirement h => r. If the variable is binded to a value in h,
	 * it is substituted by this value other wise, the variable is universally quantified.
	 *
	 * @param x -- Variable to substitute.
	 * @return
	 * @throws XFailure -- if the hypothesis or the require is inconsistent.
	 */
	public CNativeRequirement removeVarByReinforcement(XVar x) throws XFailure {
		XTerm value = searchValueInConstraint(this.hypothesis, x);
		if (value == null) { value = ConstraintManager.getConstraintSystem().makeUQV(); }
		return this.substitute(value, x);
	}



	/**
	 * Try to remove all variables that are not bound in the context ctx from
	 * a requirement h => r. The resulting requirement is equivalent to h => r.
	 *
	 * @param ctx
	 * @return
	 * @throws XFailure -- if the hypothesis or the require is inconsistent.
	 */
	public CNativeRequirement simplifyUnboundVars(Context ctx) throws XFailure {
		CNativeRequirement result = this;
		for (XVar x: result.hypothesis().vars()) {
			if (!isBound(x, ctx)) {
				result = result.simplifyVar(x);
			}
		}
		for (XVar x: result.require().vars()) {
			if (!isBound(x, ctx)) {
				result = result.simplifyVar(x);
			}
		}
		return result;
	}

	/**
	 * Try to erase a variable from a requirement h => r. If the variable is binded to a value
	 * in h, it is substituted by this value other wise, it returns the requirement unchanged.
	 *
	 * @param x -- Variable to substitute.
	 * @return
	 * @throws XFailure -- if the hypothesis or the require is inconsistent.
	 */
	public CNativeRequirement simplifyVar(XVar x) throws XFailure {
		XTerm value = searchValueInConstraint(this.hypothesis, x);
		if (value == null) { return this; }
		else { return this.substitute(value, x); }
	}

	/**
	 * Create a new requirement where the variable x is substitued by the value value.
	 *
	 * @param value
	 * @param x
	 * @return
	 * @throws XFailure -- if the hypothesis or the require is inconsistent.
	 */
	public CNativeRequirement substitute(XTerm value, XVar x) throws XFailure {
		return new CNativeRequirement(this.hypothesis().substitute(value, x), this.require().substitute(value, x));
	}

	/**
	 * Search in a constraint c if there is a value v such that c |- x = v. If it do not find a
	 * value, the method returns null. This method is not complete, it may return null even if there exists
	 * v such that c |- x = v.
	 * 
	 * This method is similar to XConstraint.bindingForVar, but not only look at the roots of the constraint.
	 * 
	 * @param c
	 * @param x
	 * @return
	 * 
	 * // TODO move in an other class? (XConstraint?)
	 */
	public static XTerm searchValueInConstraint(XConstraint c, XVar x) {
		SearchValueVisitor gv = new SearchValueVisitor(true, false, x);
		c.visit(gv);
		return gv.result();
	}


	/**
	 * Return true if the variable x is bounded in the context ctx.
	 *
	 * @param x
	 * @param ctx
	 * @return
	 *
	 * // TODO move in an other class? Context? XVar?
	 */
	public boolean isBound(XVar x, Context ctx) {
		if ( x instanceof CSelf ) {
			return true;  // FIXME is it the good choice ? if not, a particular case must be added in eraseUnboundVars
		}
		else if ( x instanceof CThis ) {
			return true;  // FIXME check if there is no problem with a local definition
		}
		else if ( x instanceof XEQV ) {
			return true;
		}
		else if ( x instanceof XField ) {
			XField<?> xf = (XField<?>) x;
			return isBound(xf.rootVar(), ctx);
		}
		else if ( x instanceof XLit ) {
			return true;
		}
		else if ( x instanceof XLocal ) {
			if ( ((XLocal<X10LocalDef>) x).name() instanceof X10LocalDef ) {
				@SuppressWarnings("unchecked")
				X10LocalDef xDef = (((XLocal<X10LocalDef>) x).name());
				return ctx.allLocals().contains(xDef);  // FIXME check if the right comparison function is used
			}
			else {
				assert false;
			}
		}
		else if ( x instanceof XUQV ) {
			return true;
		}
		assert false;
		return true;
	}


}
