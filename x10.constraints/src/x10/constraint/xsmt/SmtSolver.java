package x10.constraint.xsmt;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import x10.constraint.XConstraintManager;
import x10.constraint.XTerm;
import x10.constraint.xsmt.SmtUtil.XSmtKind;

public class SmtSolver {
	public static enum Result {
		SAT,
		UNSAT,
		UNKNOWN
	}
	
	private static SmtSolver instance = null;
	private static ProcessBuilder pb = null; 
	
	/**
	 * Some useful String constants for now. 
	 */
	private static String path = "/home/lshadare/temp/constraints-dump/constraint.smt2";
	private static String solverPath = "/home/lshadare/solvers/cvc4";
	
	private static final String prelude = "(set-logic QF_AUFLIRA)  \n" +
								          "(set-info :smt-lib-version 2.0) \n" +
								          "(set-info :status unknown) \n";
	
	private static final String prologue = "(check-sat) \n" +
										   "(exit)"; 
	
	public static final SmtSolver getInstance() {
		if(instance == null) {
			instance = new SmtSolver();
			pb = new ProcessBuilder(solverPath, path);
			pb.redirectErrorStream(true);
		}
		return instance; 
	}
	
	public boolean valid(SmtTerm formula) {
 		if (!formula.getType().isBoolean())
			throw new IllegalArgumentException("Term type should be Boolean");
 		
 		SmtTerm not_formula = new XSmtNot((XSmtTerm)formula);
 		Result res = solve(not_formula); 
 		return res == Result.UNSAT; 
	}

	public boolean checkSat(SmtTerm formula) {
 		if (!formula.getType().isBoolean())
			throw new IllegalArgumentException("Term type should be Boolean");

		Result res  = solve(formula);
		return res == Result.SAT;
	}
	
	private void dumpSmt2(SmtTerm formula) throws IOException {
		//TODO: one function that does conversion one that dumps
		// pass writer not string buffer, have it take a generic writer as an argument
		
		// collecting variable declarations
		Set<SmtVariable> decl = new HashSet<SmtVariable>(); 
		collectVarDeclarations(formula, decl);
		
		// collecting sort declarations
		Set<SmtBaseType> sorts = new HashSet<SmtBaseType>(); 
		
		// FIXME: little hack for null 
		decl.add(new XSmtLocal<String>("null"));

		for (SmtVariable var : decl) 
			collectSortDeclarations(var.getType(), sorts);
		
		StringBuilder sb = new StringBuilder();
		sb.append(prelude);

		// print sort declarations...
		for (SmtBaseType sort: sorts) {
			// we probably won't have any parametrized sorts	
			sb.append("(declare-sort "+ sort.name() + " " + sort.arity() + ")\n");
		}
		
		// and variable declarations...
		for(SmtVariable var: decl) {
			sb.append("(declare-fun "+ var.getName() + " " + var.getType().toSmt2() + ")\n");
		}

		// finally assert the formula
		sb.append("(assert " + formula.toSmt2() + ")\n");
		sb.append(prologue);

		Writer out = new OutputStreamWriter(new FileOutputStream(path, false));
	    try {
	      out.write(sb.toString());
	    }
	    finally {
	      out.close();
	    }

	}
	
	private void collectVarDeclarations(SmtTerm formula, Set<SmtVariable> vars) {
		if (formula instanceof SmtVariable) 
			vars.add((SmtVariable)formula);
		if (formula instanceof SmtExpr) {
			SmtExpr exp = (SmtExpr)formula; 
			for (SmtTerm t : exp.getChildren()) {
				collectVarDeclarations(t, vars);
			}
		}
	}
	
	private Result solve(SmtTerm assertion) {
		try {
			dumpSmt2(assertion);
			// maybe just have command now since these are constant
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

			System.out.println("Exited with error code "+ exitVal + "\n" + output);
			throw new IOException("SmtSolver Error");
		}
		// FIXME: handle this properly 
		catch (Exception e) {
			System.out.println(e); // UndeclaredThrowableException
			throw new IllegalStateException(e); 
		}
	}
	
	private void collectSortDeclarations(SmtType type, Set<SmtBaseType> sorts) {
		if (type.isUSort())
			sorts.add((SmtBaseType)type);

		if (type instanceof SmtFuncType) {
			for (int i = 0; i <= type.arity(); ++i)
				collectSortDeclarations(type.get(i), sorts);
		}
	}
	
}
