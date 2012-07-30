package x10.constraint.smt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.UndeclaredThrowableException;

import x10.constraint.XConstraintManager;
import x10.constraint.XConstraintSystem;
import x10.constraint.XType;

/**
 * Wrapper around an external process calling to the SMT solver CVC4. Currently
 * constraints are dumped to a file and read in by the solver. 
 * TODO: use more efficient means of communication (pipe, library call etc.)
 * @author lshadare
 *
 */
public class XCvc4Solver<T extends XType> implements XSmtSolver<T> {
	/**
	 * Some useful String constants for now. 
	 */
	private static final String outFile = "/home/lshadare/temp/constraints-dump/x10-constraint.smt2";
	private static final String solverPath = "/home/lshadare/solvers/cvc4";
	private final ProcessBuilder pb;
	private final XSmtPrinter<T> printer; 
	private static XCvc4Solver<? extends XType> instance = null;
	private XConstraintSystem<T> cs = XConstraintManager.<T>getConstraintSystem(); 
	
	private XCvc4Solver() {
		this.pb = new ProcessBuilder(solverPath, outFile);
		this.pb.redirectErrorStream(true);
		this.printer = new XSmtPrinter<T>(outFile); 
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends XType> XCvc4Solver<T> getInstance() {
		if (instance == null)
			instance = new XCvc4Solver<T>(); 
		return (XCvc4Solver<T>)instance;
	}
	
	@Override
	public boolean isValid(XSmtTerm<T> formula) throws XSmtFailure {
		@SuppressWarnings("unchecked")
		XSmtTerm<T> query = (XSmtTerm<T>)cs.makeNot(formula);
		query.print(printer); 
		printer.dump(); 
		// a formula is valid if its negation is unsatisfiable 
		return run() == Result.UNSAT;
	}

	@Override
	public boolean isSatisfiable(XSmtTerm<T> formula) throws XSmtFailure {
		formula.print(printer);
		printer.dump(); 
		return run () == Result.SAT; 
	}
	
	private XSmtSolver.Result run() {
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

			if (output.contains("sat") && exitVal == 10)
				return Result.SAT;

			if (output.contains("unsat") && exitVal == 20)
				return Result.UNSAT;

			throw new IOException("SmtSolver Error");
		}
		catch (Exception e) {
			System.out.println(e); 
			throw new UndeclaredThrowableException(e); 
		}
		
	}

}
