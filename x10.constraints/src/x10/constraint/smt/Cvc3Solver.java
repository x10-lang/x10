package x10.constraint.smt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
 * Wrapper around an external process calling to the SMT solver Z3. Currently
 * constraints are dumped to a file and read in by the solver. 
 * TODO: use more efficient means of communication (pipe, library call etc.)
 * @author lshadare
 *
 */
public class Cvc3Solver<T extends XType> implements XSmtSolver<T> {
	/**
	 * Some useful String constants for now. 
	 */
	private static final String outFile = "/home/lshadare/temp/constraints-dump/x10-constraint.smt2";
	private static final String solverPath = "/home/lshadare/solvers/cvc3-2.4.1";
	private static final String args = "-lang smt2";
	private final ProcessBuilder pb;
	private static Cvc3Solver<? extends XType> instance = null;
	private XConstraintSystem<T> cs = XConstraintManager.<T>getConstraintSystem(); 
	
	private Cvc3Solver() {
		this.pb = new ProcessBuilder(solverPath, "-lang","smt2",outFile);
		this.pb.redirectErrorStream(true);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends XType> Cvc3Solver<T> getInstance() {
		if (instance == null)
			instance = new Cvc3Solver<T>(); 
		return (Cvc3Solver<T>)instance;
	}
	
	private Result solve(XSmtTerm<T> query) {
//		XSmtPrinter<T> p = new XSmtPrinter<T>(outFile);
//		p.dump(query); 
//		return run();
		// we only need the axioms 
		if (query instanceof XSmtQuantifier)
			query = instantiateAxioms(query);
		XSmtPrinter<T> p = new XSmtPrinter<T>(outFile);
		p.dump(query); 
		return run(); 
	}

	/**
	 * Based on the terms in the query it instantiates various axioms that help the
	 * solver. 
	 * @param query
	 */
	private XSmtTerm<T> instantiateAxioms(XSmtTerm<T> query) {
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

	private void collectFieldAccess(XSmtTerm<T> term, Set<XSmtField<T, ?>> fields) {
		if (term instanceof XSmtExpr) {
			XSmtExpr<T> expr = (XSmtExpr<T>)term; 
			for (XSmtTerm<T> t : expr.children())
				collectFieldAccess(t, fields);
		}
		if (term instanceof XSmtQuantifier)
			collectFieldAccess(((XSmtQuantifier<T>)term).body(), fields); 
		if (term instanceof XSmtField) 
			fields.add((XSmtField<T,?>) term); 
	}

	@Override
	public boolean isValid(XSmtTerm<T> formula) throws XSmtFailure {
		//@SuppressWarnings("unchecked")
		// make sure the quantifiers are top level
		
		XSmtQuantifier<T> query = new XSmtQuantifier<T>(formula);
		if (query.onlyUniversal()) {
			return solve((XSmtTerm<T>)cs.makeNot(formula)) == Result.UNSAT;
		}
		if (query.onlyExistential()) 
			return solve(formula) == Result.SAT;
		
		Result res = solve(query);
		if (res == Result.UKNOWN)
			throw new XSmtFailure("Unknown result");
		return res == Result.SAT;
	}

	@Override
	public boolean isSatisfiable(XSmtTerm<T> formula) throws XSmtFailure {
		//return solve(formula) == Result.SAT;
		Result res = solve(new XSmtQuantifier<T>(formula)); 

		if (res == Result.UKNOWN)
			throw new XSmtFailure("Unknown result");

		return res == Result.SAT;
	}
	
	private static int debugCounter = 0; 
	private XSmtSolver.Result run() {
		debugCounter++; 
		try {
			Process pr = pb.start(); 
			// note that Process.getInputStream() obtains an input stream with 
			// data piped from the standard output of the process
			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line=null;
			StringBuffer sb = new StringBuffer(); 

			while((line=input.readLine()) != null) {
				sb.append(line);
			}

			input.close();
			int exitVal = pr.waitFor();
			pr.destroy();

			String output = sb.toString(); 
				
			if (exitVal != 0)
				throw new IOException("SmtSolver Error exit code " + exitVal + ": " + output);

			if (output.contains("unsat"))
				return Result.UNSAT;

			if (output.contains("sat"))
				return Result.SAT;
			
			if (output.contains("unknown")) {
				return Result.UKNOWN;
			}
			
			throw new IOException("CVC3 SmtSolver Error: "+ output);
		}
		catch (Exception e) {
			System.out.println(e); 
			throw new UndeclaredThrowableException(e); 
		}
		
	}

	@Override
	public boolean entails(XSmtTerm<T> t1, XSmtTerm<T> t2) throws XSmtFailure {
		XSmtTerm<T> impl = (XSmtTerm<T>)cs.makeExpr(XOp.<T>IMPL(), t1, t2); 
		//XSmtTerm<T> query = new XSmtQuantifier<T>(impl);
		try {
			return isValid(impl);
		} catch (XSmtFailure e) {
			// try to check if the second term is valid. 
			try {
				return isValid(t2);
			} catch(XSmtFailure f) {
				throw new UndeclaredThrowableException(f); 	
				//return false; 
			}
		}
	}
}
