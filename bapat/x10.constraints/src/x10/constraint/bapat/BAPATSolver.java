package x10.constraint.bapat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import stp.Expr;
import stp.VC;

import x10.constraint.Solver;
import x10.constraint.XConstraint;
import x10.constraint.XEQV;
import x10.constraint.XEquals;
import x10.constraint.XFailure;
import x10.constraint.XField;
import x10.constraint.XField_c;
import x10.constraint.XFormula;
import x10.constraint.XIn;
import x10.constraint.XLit;
import x10.constraint.XLit_c;
import x10.constraint.XLocal;
import x10.constraint.XMul;
import x10.constraint.XName;
import x10.constraint.XNameWrapper;
import x10.constraint.XPlus;
import x10.constraint.XRoot;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XTranslate;
import x10.constraint.XVar;

public class BAPATSolver implements Solver {
	
	// We're a singleton.
	public final static Solver solver = new BAPATSolver();
	private BAPATSolver() {
		vars = new HashMap<String, Var>();
		vc = new VC();
	}
	
	protected final static int NUM_BITS = 32;
	
	// Maps unique names to their Var
	private Map<String, Var> vars;
	private VC vc;

	// TODO: See XTypeTranslator.transCall for a way to do this that works.  But I don't have a TypeSystem, so I can't do that here.
	public void addDerivedEqualitiesInvolving(XConstraint c, XTerm t) throws XFailure {
		/*XTerm left = null, right = null;
		if (t instanceof XIn) {
			XIn xin = (XIn) t;
			XVar leftVar = (XVar) xin.left(), rightVar = (XVar) xin.right();
			if (xin.leftIsRegion() || xin.leftIsPoint()) {
				XName leftRankName = null;
				if (xin.leftIsRegion())
					leftRankName = new XNameWrapper<String>("x10.lang.Region#rank");
				else if (xin.leftIsPoint())
					leftRankName = new XNameWrapper<String>("x10.lang.Point#rank");
				else
					assert false;
				left = new XField_c(leftVar, leftRankName);
				right = new XField_c(rightVar, new XNameWrapper<String>("x10.lang.Region#rank"));
			} else if (xin.leftIsInt()) {
				left = new XField_c(rightVar, new XNameWrapper<String>("x10.lang.Region#rank"));
				right = new XLit_c(1);
			}
			if (left != null && right != null) {
				if (leftVar.rootVar().equals(c.self()) && leftVar != c.self())
					left = left.subst(c.self(), (XRoot) leftVar.rootVar());
				if (rightVar.rootVar().equals(c.self()) && rightVar != c.self())
					right = right.subst(c.self(), (XRoot) rightVar.rootVar());
				if (!c.entails(left, right)) {
					XTerm newEquality = XTerms.makeEquals(left, right);
					c.addTerm(newEquality);
				}
			}
		}*/
	}
	
	public boolean entails(XConstraint env, XTerm t, XConstraint sigma) {
		//System.out.println("Trying " + env + " => " + t + ".");
		boolean v = realEntails(env, t);
		//System.out.println("Got " + v);
		vars.clear();
		return v;
	}

	// Decomped into its own function only to aid debugging.
	private boolean realEntails(XConstraint env, XTerm t) {
		// Quick ... X ... => X check.
		for (XTerm term: env.constraints())
			if (term.equals(t)/* || (term instanceof XIn && t instanceof XIn && env.entails(((XIn)term).left(), ((XIn)t).left()) && env.entails(((XIn)term).right(), ((XIn)t).right()))*/)
				return true;
		// Heavy-duty work.
		List<Constraint> constraints = new LinkedList<Constraint>();
		for (XTerm term: env.constraints())
			constraints.add(genConstraint(term, env));
		Constraint assertion = genConstraint(t, env);
		List<Var> allVars = new LinkedList<Var>();
		for (Constraint c: constraints) {
			allVars.add(c.left);
			allVars.add(c.right);
		}
		allVars.add(assertion.left);
		allVars.add(assertion.right);
		/*System.out.println("-----");
		for (Constraint c: constraints)
			System.out.println(c.toLongString());
		System.out.println(assertion.toLongString());
		System.out.println("-----");*/
		propagateIntegerValues(allVars);
		vc.push();
		for (Constraint c: constraints)
			assertConstraint(c);
		addExtraAssertions(allVars);
		boolean isValid = isValid(assertion);
		vc.pop();
		return isValid;
	}

	private Constraint genConstraint(XTerm t, XConstraint env) {
		Constraint c = parseTerm(t, env);
		c.addConstraintToVars();
		return c;
	}
	
	/**
	 * Parses an XTerm constraint into a Constraint.
	 */
	private Constraint parseTerm(XTerm t, XConstraint env) {
		//System.out.println(" " + t);
		if (t instanceof XFormula) {
			XFormula f = (XFormula) t;
			if (f.isBinary()) {
				// Parse the two atoms
				Var left = parseAtom(f.left());
				Var right = parseAtom(f.right());
				// Get the constraint
				Constraint constraint = null;
				if (f instanceof XIn)
					constraint = new InConstraint(left, right, ((XIn)f));
				else if (f instanceof XEquals)
					constraint = new EqualityConstraint(left, right);
				assert constraint != null;
				//System.out.println("  " + constraint.toLongString());
				return constraint;
			}
		}
		throw new BadBapatParse(t.toString() + " of class " + t.getClass());
	}
	
	/**
	 * Parses an XTerm atom into a Var.
	 */
	private Var parseAtom(XTerm t) {
		if (t instanceof XEQV) {
			return getVar(((XEQV)t).name().toString());
		} else if (t instanceof XLocal) {
			return getVar(((XLocal)t).name().toString());
		} else if (t instanceof XField) {
			return parseField((XField)t);
		} else if (t instanceof XLit) {
			XLit lit = (XLit) t;
			Object val = lit.val();
			if (val instanceof Integer)
				return new IntConstant((Integer)val);
			else if (val instanceof Boolean)
				return new BooleanConstant((Boolean)val);
		} else if (t instanceof XFormula) {
			XFormula f = (XFormula) t;
			if (f instanceof XPlus) {
				return new PlusVar(parseAtom(f.left()), parseAtom(f.right()));
			} else if (t instanceof XMul) {
				return new MulVar(parseAtom(f.left()), parseAtom(f.right()));
			} else if (t instanceof XTranslate) {
				return new TranslateVar(parseAtom(f.left()), parseAtom(f.right()));
			}
		}
		throw new BadBapatParse(t.toString() + " of class " + t.getClass());
	}
	
	/**
	 * Parses an XField atom into a Var.
	 */
	private Var parseField(XVar v) {
		if (v instanceof XLocal) {
			return getVar(((XLocal)v).name().toString());
		} else if (v instanceof XField) {
			XField f = (XField) v;
			NamedVar receiver = (NamedVar) parseField(f.receiver());
			String field = f.field().toString();
			String[] parts = field.split("#");  // Split off the previous field's type from the current field's name.
			assert parts.length == 2 || parts.length == 1 : field;
			NamedVar cur = null;
			if (parts.length == 2) {
				receiver.setType(parts[0]);
				cur = (NamedVar) getVar(parts[1], receiver);
			} else if (parts.length == 1)
				cur = (NamedVar) getVar(parts[0], receiver);
			receiver.addField(cur);
			return cur;
		} else if (v instanceof XLit) {
			XLit lit = (XLit) v;
			Object val = lit.val();
			return getVar(val.toString());
		} else
			throw new BadBapatParse(v.toString() + " of class " + v.getClass());
	}
	
	/**
	 * Gets the Var representation of the given name.  Checks
	 * the map of created Vars and returns it if it exists,
	 * and creates a new one and adds it to the map otherwise.
	 */
	private Var getVar(String name) {
		return getVar(name, null);
	}
	private Var getVar(String name, NamedVar parent) {
		String uniqueName = name;
		if (parent != null)
			uniqueName = parent.getUniqueName() + "__" + uniqueName;
		if (vars.containsKey(uniqueName))
			return vars.get(uniqueName);
		else {
			NamedVar var = new NamedVar(name, uniqueName, parent);
			vars.put(uniqueName, var);
			return var;
		}
	}
	
	/**
	 * Propagates integer values into all things that can have them
	 * and that are directly in a constraint.  We start with the
	 * integer constants and rank 1 regions/points and propagate 
	 * them through the constraints and up from a foo.rank to the 
	 * foo itself (which allows us to propagate rank across in/subset
	 * constraints).
	 */
	private void propagateIntegerValues(List<Var> allVars) {
		LinkedList<IntInformationVar> toPropagate = new LinkedList<IntInformationVar>();
		Set<IntInformationVar> propagated = new HashSet<IntInformationVar>();
		// Initially start with all the integer constants and regions of a type with a fixed rank
		for (Var v: allVars)
			checkVarForIntegerValue(v, toPropagate);
		// Now propagate those as far as we can
		while (!toPropagate.isEmpty()) {
			IntInformationVar cur = toPropagate.remove();
			if (propagated.contains(cur))
				continue;
			propagated.add(cur);
			// Propagate through constraints
			for (Constraint c: cur.getConstraints()) {
				IntInformationVar other = (IntInformationVar) c.getOtherVar(cur);
				// Do not propagate information through "x in y" constraints where x is an int
				// since the value for ints is their value and for other things are their rank.
				if (c instanceof InConstraint && ((InConstraint) c).leftIsInt())
					continue;
				other.setIntInformation(cur.getIntInformation());
				toPropagate.add(other);
			}
			// Propagate up to a region (this is unsound, but I don't know a better way since we don't have actual types in our constraints).
			if (cur instanceof NamedVar) {
				NamedVar named = (NamedVar) cur;
				if (named.getName().equals("rank") && named.getParent() != null) {
					IntInformationVar parent = (IntInformationVar) named.getParent();
					parent.setIntInformation(named.getIntInformation());
					toPropagate.add(parent);
				}
			}
		}
	}
	
	/**
	 * Helper function for propagateIntegerValues that adds a Var to
	 * the list of Vars that have an initial integer variable.  These
	 * are either integer constants or region variables whose type
	 * implies their region (e.g. RectRegion1 must have rank 1).
	 */
	private void checkVarForIntegerValue(Var v, List<IntInformationVar> toPropagate) {
		if (v instanceof IntConstant)
			toPropagate.add((IntInformationVar) v);
		else if (v instanceof NamedVar) {
			NamedVar var = (NamedVar) v;
			String type = var.getType();
			if ("x10.array.RectRegion1".equals(type) || "x10.array.Point1".equals(type)) {
				var.setIntInformation(1);
				toPropagate.add(var);
			}
		}
	}
	
	/**
	 * Asserts in STP that this constraint holds.
	 */
	private void assertConstraint(Constraint c) {
		Expr e = c.toSTPExpr(vc);
		if (e != null)
			vc.assertFormula(e);
	}

	private void addExtraAssertions(List<Var> allVars) {
		// Add each term's assertion.
		for (Var v: allVars) {
			if (v.getAssertions(vc) != null)
				vc.assertFormula(v.getAssertions(vc));
		}
		// For each term a+b, if b+a exists add a+b = b+a
		// For each pair of terms "a+b,c+d add a <= b && b <= d => a+b <= c+d
		Set<BinaryFormulaVar> seenAdditions = new HashSet<BinaryFormulaVar>();
		for (Var v: allVars) {
			if (v instanceof PlusVar || v instanceof TranslateVar) {
				BinaryFormulaVar bfv = (BinaryFormulaVar) v;
				if (!seenAdditions.contains(bfv)) {
					// For each term a+b, if b+a exists add a+b = b+a
					BinaryFormulaVar sibling = null;
					if (bfv instanceof PlusVar)
						sibling = new PlusVar(bfv.getRight(), bfv.getLeft());
					else if (bfv instanceof TranslateVar)
						sibling = new TranslateVar(bfv.getRight(), bfv.getLeft());
					else
						assert false;
					if (vars.containsKey(sibling) && !seenAdditions.contains(sibling))
						vc.assertFormula(vc.eqExpr(bfv.toSTPExpr(vc), sibling.toSTPExpr(vc)));
					seenAdditions.add(bfv);
				}
			}
		}
		// For each pair of terms "a+b,c+d add a <= b && b <= d => a+b <= c+d
		for (BinaryFormulaVar v1: seenAdditions) {
			for (BinaryFormulaVar v2: seenAdditions) {
				if (v1 != v2) {
					Expr v1l = v1.getLeft().toSTPExpr(vc), v1r = v1.getRight().toSTPExpr(vc), v2l = v2.getLeft().toSTPExpr(vc), v2r = v2.getRight().toSTPExpr(vc);
					Expr lhs = vc.andExpr(vc.sbvLeExpr(v1l, v2l), vc.sbvLeExpr(v1r, v2r));
					Expr rhs = vc.sbvLeExpr(v1.toSTPExpr(vc), v2.toSTPExpr(vc));
					vc.assertFormula(vc.impliesExpr(lhs, rhs));
				}
			}
		}
	}
	
	/**
	 * Checks using STP whether this constraint holds given
	 * all the constraints previously asserted.
	 */
	private boolean isValid(Constraint c) {
		Expr e = c.toSTPExpr(vc);
		if (e == null)
			return false;
		//System.out.println("--------------------");
		//vc.printAsserts(true);
		//vc.printExpr(e);
		boolean result = vc.query(e) == 1;
		//System.out.println(result);
		return result;
	}

	public boolean isConsistent(List<XTerm> atoms) {
		// TODO: I think this is correct, but I'm not sure.
		return true;
	}

	public boolean isValid(List<XTerm> atoms) {
		// TODO: I think this is correct, but I'm not sure.
		return atoms.size() == 0;
	}
	
	private static class BadBapatParse extends RuntimeException {
		private String msg;
		public BadBapatParse(String msg) {
			this.msg = msg;
		}
		@Override
		public String toString() {
			return "BadBapatParse: " + msg;
		}
	}

}
