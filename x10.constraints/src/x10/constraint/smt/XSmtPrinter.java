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
import x10.constraint.XLit;
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
	private final Set<XSmtVar<T>> varDeclarations;
	/**
	 * Set containing all the function symbols that need to be declared. 
	 */
	private final Map<XLabeledOp<T,Object>, List<T>> funDeclarations; 
	/**
	 * Set containing the names of all the sorts that need to be declared. 
	 */
	private final Set<String> sortDeclarations; 
	/**
	 * Set containing variables for all the string constants. We need this to
	 * explicitly add the constraint that strinVariables with different names
	 * are disequal. 
	 */
	private final Set<XSmtVar<T>> stringVariables; 
	/**
	 * The header of the smt2 file. 
	 */
	private static final String prelude = "(set-logic AUFLIRA)  \n" +
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
	private FileOutputStream fileOS;
	/**
	 * The actual XTerms will be use this writer. 
	 */
	private OutputStreamWriter writer;
	/**
	 * Declarations will use this writer. 
	 */
	private OutputStreamWriter declWriter;
	
	private final String outFile;
	/**
	 * Keeps track whether the writers and declaration maps have been initialized. 
	 */
	private boolean initialized;
	private XSmtVar<T> nullVar; 
	
	public XSmtPrinter(String outFile) {
		this.outFile = outFile; 
		this.varDeclarations = new HashSet<XSmtVar<T>>(); 
		this.funDeclarations = new HashMap<XLabeledOp<T, Object>, List<T>>();
		this.sortDeclarations = new HashSet<String>();
		this.stringVariables = new HashSet<XSmtVar<T>>();
		initialize(); 
	}
	
	private void initialize() {
		try {
			this.fileOS = new FileOutputStream(outFile, false);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Problem writting smt2 file: " + outFile);
		}
		this.writer =  new OutputStreamWriter(fileOS);
		this.declWriter = new OutputStreamWriter(fileOS);
		this.initialized = true; 
	}

	@Override
	public void declare(XSmtTerm<T> term) {
//		if (term instanceof XSmtEQV || 
//			term instanceof XSmtUQV) {
//			// don't need to declare the variables but may need to 
//			// declare the sort
//			if (!term.type().isPrimitive())
//				sortDeclarations.add(smt2(term.type())); 
//		} 
//		else
		if (term instanceof XSmtVar) {
			// collect variable declarations
			varDeclarations.add((XSmtVar<T>)term);
			if (!term.type().isPrimitive())
				sortDeclarations.add(smt2(term.type())); 
		}
		else
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
				funDeclarations.put((XLabeledOp<T,Object>)exp.op(), types); 
			}
		}
		else
		if (term instanceof XSmtLit) {
			// there are several special cases we need to take care of
			XSmtLit<T, ?> lit = (XSmtLit<T, Object>) term; 
			// create a special variable for null
			if (lit.val() == null)
				declare(nullVar(lit.type().<T>xTypeSystem()));
			// create one variable for each string constant
			else if (lit.val() instanceof String) {
				XSmtVar<T> strVar = stringVar(lit.val().toString(), lit.type());
				declare(strVar); 
			}
		}
	}
	@Override
	public String mangle(String string) {
		return "?" + string.replaceAll("[\\.\\(\\):#\\s\\[\\]{},]", "_");
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
	public void dump(XSmtTerm<T> query) {
		assert initialized; 
		
		try {
			OutputStreamWriter ow = new OutputStreamWriter(fileOS);
			// print out the header

			ow.append(prelude);
			ow.flush(); 
			// print out the declarations
			query.declare(this); 
			generateDeclarations();
			
			// print out the actual formula we are asserting
			ow.append("(assert ");
			ow.flush(); 
			query.print(this);
			writer.append(")\n");
			writer.flush();
			// TODO: add String constants disequalities 
			
			// print the prologue
			ow.append(prologue);
			ow.flush(); 
			ow.close(); 
		} catch (IOException e) {
			throw new IllegalStateException("Problem writing to smt2 " + e);
		}
	}

	private void generateDeclarations() throws IOException {
		for(String type : sortDeclarations) {
			declWriter.append(declareType(type));
		}
		for (XSmtVar<T> var : varDeclarations) {
			declWriter.append(declareVar(var));
		}

		for (XLabeledOp<T, Object> op : funDeclarations.keySet()) {
			declWriter.append(declareFun(op, funDeclarations.get(op)));
		}
		declWriter.flush(); 
	}

	private String declareType(String type) {
		return "(declare-datatypes () ((" + type + " (mk-" + type + "(get Int)))))\n"; 
	}

	private String declareVar(XSmtVar<T> var) {
		return "(declare-fun " + mangle(var.toString()) + " () " + smt2(var.type()) + ")\n";
	}

	private String declareFun(XLabeledOp<T, ?> op, List<T> types) {
		StringBuilder sb = new StringBuilder(); 
		sb.append("(declare-fun ");
		sb.append(op.print(this));
		// printing argument types
		sb.append(" (");
		for (int i = 0; i < types.size() -1; ++i) {
			T type = types.get(i); 
			sb.append((i==0 ? "" : " ") + smt2(type)); 
		}
		sb.append(") ");
		// printing return type
		T returnType = types.get(types.size() - 1);
		sb.append(smt2(returnType));
		sb.append(")\n");
		return sb.toString(); 
	}

	public static <T extends XType> String smt2(T type) {
		if (type.isBoolean())
			return "Bool"; 
		if (type.isInt() || type.isLong())
			return "Int"; 
		if (type.isFloat())
			return "Real"; 
		if (type.isDouble())
			return "Real";
		if (!type.isPrimitive())
			return "USort";
		return "USort"; 

		//throw new IllegalArgumentException("Unsupported smt2 type: " + type);
	}
	
	@Override
	public XSmtVar<T> nullVar(XTypeSystem<? extends T> ts) {
		if (nullVar == null)
			nullVar = (XSmtVar<T>)XConstraintManager.getConstraintSystem().makeVar(ts.Null(), "null");
		return nullVar; 
	}
	
	@Override
	public XSmtVar<T> stringVar(String name, T type) {
		String var = mangle(name); 
		return (XSmtVar<T>)XConstraintManager.getConstraintSystem().makeVar(type, var);
	}

	private void clear() {
		varDeclarations.clear();
		funDeclarations.clear(); 
		sortDeclarations.clear();
		try {
			writer.close();
			declWriter.close(); 
		} catch (IOException e) {
			throw new IllegalStateException("Problem closing smt2 writers: " + e);
		}
		
	}

	@Override
	public void reset() {
		clear(); 
		initialize(); 
	}

	@Override
	public String printType(T t) {
		return smt2(t); 
	}

}
