package x10.constraint.smt;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import x10.constraint.XConstraintManager;
import x10.constraint.XConstraintSystem;
import x10.constraint.XOp;
import x10.constraint.XTerm;
import x10.constraint.XType;

/**
 * Static class that calls out to one or more external SMT solvers. 
 * @author lshadare
 *
 */
public class XSolverEngine<T extends XType> {
	
	enum Result {
		SAT,
		UNSAT,
		UKNOWN
	}
	public static final String path ="/home/lshadare/x10-constraints2/solvers/";
	protected static final String outFile = path + "temp/x10-constraint.smt2";
	
	private static boolean solving = false; 
	private static <T extends XType> Result solve(XSmtTerm<T> query) {
		if (solving)
			throw new IllegalStateException("Cannot call solve while in solving");
		solving = true; 
		// we only need the axioms if the query has quantifiers 
		if (query instanceof XSmtQuantifier)
			query = instantiateAxioms(query);
		XSmtPrinter<T> p = new XSmtPrinter<T>(outFile);
		p.dump(query); 
		Result res = run(); 
		solving = false;
		return res; 
	}

	/**
	 * Based on the terms in the query it instantiates various axioms that help the
	 * solver. 
	 * @param query
	 */
	private static <T extends XType> XSmtTerm<T> instantiateAxioms(XSmtTerm<T> query) {
		XConstraintSystem<T> cs = XConstraintManager.<T>getConstraintSystem(); 
		
		Set<XSmtField<T,?>> fields = new HashSet<XSmtField<T,?>>(); 
		collectFieldAccess(query, fields);
		List<XTerm<T>> conjuncts  = new ArrayList<XTerm<T>>(fields.size() + 1);
		for (XSmtField<T,?> f : fields) {
			// for each field dereference a.f construct the axiom 
			// forall v exists b (b.f = v) 
			XTerm<T> v = cs.makeUQV(f.type());
			XTerm<T> b = cs.makeEQV(f.receiver().type()); 
			XTerm<T> bf = cs.makeField(b, f.field(), f.type()); 
			@SuppressWarnings("unchecked")
			XSmtTerm<T> eq = (XSmtTerm<T>)cs.makeEquals(v, bf);
			// make the quantifiers explicit
			conjuncts.add(new XSmtQuantifier<T>(eq)); 
		}
		// if we have some axioms add them as assumptions 
		if (! conjuncts.isEmpty()) {
			XTerm<T> axioms = cs.makeAnd(conjuncts);
			@SuppressWarnings("unchecked")
			XSmtTerm<T> res = (XSmtTerm<T>)cs.makeExpr(XOp.<T>IMPL(), axioms, query);
			return res;
		}
		return query;
	}

	private static <T extends XType> void collectFieldAccess(XSmtTerm<T> term, Set<XSmtField<T, ?>> fields) {
		if (term instanceof XSmtExpr) {
			XSmtExpr<T> expr = (XSmtExpr<T>)term; 
			for (XSmtTerm<T> t : expr.children())
				collectFieldAccess(t, fields);
		}
		if (term instanceof XSmtQuantifier)
			collectFieldAccess(((XSmtQuantifier<T>)term).body(), fields); 
		if (term instanceof XSmtField) {
			@SuppressWarnings("unchecked")
			XSmtField<T,?> term2 = (XSmtField<T,?>) term;
			fields.add(term2);
		} 
	}
	
	/**
	 * Returns true if the formula is valid i.e. if it is true under
	 * all variable assignments and false otherwise
	 * @param formula
	 * @return
	 * @throws XSmtFailure if there was a problem calling the external solver.
	 */
	public static <T extends XType> boolean isValid(XSmtTerm<T> formula) throws XSmtFailure {
		XConstraintSystem<T> cs = XConstraintManager.<T>getConstraintSystem(); 
		// make sure the quantifiers are top level
		
		XSmtQuantifier<T> query = new XSmtQuantifier<T>(formula);
		if (query.onlyUniversal()) {
			@SuppressWarnings("unchecked")
			XSmtTerm<T> makeNot = (XSmtTerm<T>)cs.makeNot(formula);
			return solve(makeNot) == Result.UNSAT;
		}
		if (query.onlyExistential()) 
			return solve(formula) == Result.SAT;
		
		Result res = solve(query);
		if (res == Result.UKNOWN)
			throw new XSmtFailure("Unknown result");
		return res == Result.SAT;
	}

	/**
	 * Returns true if the formula is satisfiable i.e. iif there is an
	 * assignment to the free variables that renders the formula true, and 
	 * false otherwise
	 * @param formula
	 * @return
	 * @throws XSmtFailure if there was a problem calling the external solver.
	 */
	public static <T extends XType> boolean isSatisfiable(XSmtTerm<T> formula) throws XSmtFailure {
		Result res = solve(new XSmtQuantifier<T>(formula)); 

		if (res == Result.UKNOWN)
			throw new XSmtFailure("Unknown result");

		return res == Result.SAT;
	}

	public static <T extends XType> boolean entails(XSmtTerm<T> t1, XSmtTerm<T> t2) throws XSmtFailure {
		XConstraintSystem<T> cs = XConstraintManager.<T>getConstraintSystem(); 
		@SuppressWarnings("unchecked")
		XSmtTerm<T> impl = (XSmtTerm<T>)cs.makeExpr(XOp.<T>IMPL(), t1, t2);
		XSmtTerm<T> query = new XSmtQuantifier<T>(impl);
		try {
			return isValid(query);
		} catch (XSmtFailure e) {
			// try to check if the second term is valid. 
			try {
				return isValid(new XSmtQuantifier<T>(t2));
			} catch(XSmtFailure f) {
				throw new UndeclaredThrowableException(e); 	
			}
		}
	}
	
	protected static <T extends XType> XSolverEngine.Result run() {
		Result res = XSolverFactory.SmtSolver("z3").run();
//		if (res == Result.UKNOWN)
//			res = XSolverFactory.SmtSolver("cvc3").run(); 
		return res; 
	}
}
