package x10.constraint.smt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.UndeclaredThrowableException;
import x10.constraint.XType;

/**
 * Wrapper around an external process calling to the SMT solver CVC3. Currently
 * constraints are dumped to a file and read in by the solver. 
 * TODO: use more efficient means of communication (pipe, library call etc.)
 * @author lshadare
 *
 */
public class Cvc3Solver<T extends XType> implements XSmtSolver {
	/**
	 * Some useful String constants for now. 
	 */
	
	private static final String solverPath = XSolverEngine.path+"cvc3"; 
	private final ProcessBuilder pb;
	private static Cvc3Solver<? extends XType> instance = null;
	
	private Cvc3Solver() {
		this.pb = new ProcessBuilder(solverPath, "-lang","smt2",XSolverEngine.outFile);
		this.pb.redirectErrorStream(true);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends XType> Cvc3Solver<T> getInstance() {
		if (instance == null)
			instance = new Cvc3Solver<T>(); 
		return (Cvc3Solver<T>)instance;
	}
	
	
	private static int debugCounter = 0; 
	@Override
	public XSolverEngine.Result run() {
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
				return XSolverEngine.Result.UNSAT;

			if (output.contains("sat"))
				return XSolverEngine.Result.SAT;
			
			if (output.contains("unknown")) {
				return XSolverEngine.Result.UKNOWN;
			}
			
			throw new IOException("CVC3 SmtSolver Error: "+ output);
		}
		catch (Exception e) {
			System.out.println(e); 
			throw new UndeclaredThrowableException(e); 
		}
		
	}

}
