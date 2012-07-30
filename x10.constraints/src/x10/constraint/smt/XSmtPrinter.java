package x10.constraint.smt;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import x10.constraint.XConstraintManager;
import x10.constraint.XLabeledOp;
import x10.constraint.XType;
import x10.constraint.XTypeSystem;

/**
 * 
 * @author lshadare
 *
 * @param <T>
 */
public class XSmtPrinter<T extends XType> implements XPrinter<T> {
	/**
	 * Set containing the variables that need to be declared. 
	 */
	Set<XSmtVar<T>> varDeclarations;
	/**
	 * Set containing all the function symbols that need to be declared. 
	 */
	Map<XLabeledOp<T,Object>, List<T>> funDeclarations; 
	/**
	 * Set containing all the new sorts that need to be declared. 
	 */
	Set<T> sortDeclarations; 
	/**
	 * The header of the smt2 file. 
	 */
	private static final String prelude = "(set-logic QF_AUFLIRA)  \n" +
			"(set-info :smt-lib-version 2.0) \n" +
			"(set-info :status unknown) \n";
	/**
	 * The end of the smt2 file. 
	 */
	private static final String prologue = "(check-sat) \n" +
			"(exit)"; 
	/**
	 * This is the stream everything will be dumped to. The writer and declWriter
	 * writers share this stream.  
	 */
	private final FileOutputStream fileOS;
	/**
	 * The actual XTerms will be use this writer. 
	 */
	private final OutputStreamWriter writer;
	/**
	 * Declarations will use this writer. 
	 */
	private final OutputStreamWriter declWriter;
	
	public XSmtPrinter(String outFile) {
		try {
			this.fileOS = new FileOutputStream(outFile);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Problem writing to smt2 file " + e);
		}
		this.writer =  new OutputStreamWriter(fileOS);
		this.declWriter = new OutputStreamWriter(fileOS);
		this.varDeclarations = new HashSet<XSmtVar<T>>(); 
		this.funDeclarations = new HashMap<XLabeledOp<T, Object>, List<T>>();
	}

	@Override
	public void out(XSmtTerm<T> term) {
		if (term instanceof XSmtVar) {
			// collect variable declarations
			varDeclarations.add((XSmtVar<T>)term);
			sortDeclarations.add(term.type()); 
		}
		if (term instanceof XSmtExpr) {
			// collect uninterpreted function symbol declarations
			XSmtExpr<T> exp = (XSmtExpr<T>) term; 
			if (exp.op() instanceof XLabeledOp) {
				List<T> types = new ArrayList<T>(exp.children().size() + 1);
				for (XSmtTerm<T> t : exp.children()) 
					types.add(t.type());
				// adding the result type
				XTypeSystem<? extends T> ts = term.type().xTypeSystem();
				types.add(exp.op().type(ts));
			}
		}
		if (term instanceof XSmtLit && ((XSmtLit<?,?>)term).val() == null) {
			@SuppressWarnings("unchecked")
			XSmtVar<T> nullVar = (XSmtVar<T>)XConstraintManager.getConstraintSystem().makeVar(term.type(), nullVar(term.type()));
			varDeclarations.add(nullVar);
		}
	}
	@Override
	public String mangle(String string) {
		return "?" + string.replaceAll("[\\.\\(\\):#\\s]", "_");
	}

	@Override
	public void append(CharSequence ch) {
		try {
			writer.append(ch);
		} catch (IOException e) {
			throw new IllegalStateException("Problem writing to smt2 " + e);
		}
	}

	@Override
	public void dump() {
		try {
			generateDeclarations();
			writeToFile();
		} catch (IOException e) {
			throw new IllegalStateException("Problem writing to smt2 " + e);
		}
		clear(); 
	}

	private void generateDeclarations() throws IOException {
		for(T type : sortDeclarations) {
			declWriter.append(declareType(type));
		}
		for (XSmtVar<T> var : varDeclarations) {
			declWriter.append(declareVar(var));
		}

		for (XLabeledOp<T, Object> op : funDeclarations.keySet()) {
			declWriter.append(declareFun(op, funDeclarations.get(op)));
		}
	}

	private void writeToFile() throws IOException {
		OutputStreamWriter ow = new OutputStreamWriter(fileOS);
		ow.append(prelude);
		ow.flush(); 
		declWriter.flush();
		writer.flush();
		ow.append(prologue);
		ow.flush(); 
		ow.close(); 
		fileOS.flush();
	}

	private String declareType(T type) {
		if (type.isJavaPrimitive())
			return ""; 
		return "(declare-sort " + smt2(type) + " 0 )\n";
	}

	private String declareVar(XSmtVar<T> var) {
		return "(declare-fun " + mangle(var.toString()) + " () " + smt2(var.type()) + ")";
	}

	private String declareFun(XLabeledOp<T, ?> op, List<T> types) {
		StringBuilder sb = new StringBuilder(); 
		sb.append("declare-fun (");
		for (int i = 0; i < types.size() -1; ++i) {
			T type = types.get(i); 
			sb.append((i==0 ? "" : " ") + smt2(type)); 
		}
		sb.append(") ");
		T returnType = types.get(types.size() - 1);
		sb.append(smt2(returnType));
		sb.append(")\n");
		return sb.toString(); 
	}

	private String smt2(T type) {
		if (type.isBoolean())
			return "Boolean"; 
		if (type.isInt() || type.isLong())
			return "Int"; 
		if (type.isFloat())
			return "Real"; 
		if (!type.isJavaPrimitive())
			return mangle(type.toString()); 

		throw new IllegalArgumentException("Unsupported smt2 type " + type);
	}
	@Override
	public String nullVar(T type) {
		return mangle("null" + type.toString()); 
	}

	private void clear() {
		varDeclarations.clear();
		funDeclarations.clear(); 
		sortDeclarations.clear(); 
	}

}
