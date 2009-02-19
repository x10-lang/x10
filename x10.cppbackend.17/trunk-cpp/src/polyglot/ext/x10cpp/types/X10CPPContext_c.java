/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10cpp.types;

/**
 * This class extends the X10 notion of Context to keep track of
 * the translation state for the C++ backend.
 *
 * @author igor
 * @author pvarma
 * @author nvk
 * @see X10Context_c
 */
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector;

import polyglot.ast.Stmt;
import polyglot.types.Context_c;
import polyglot.types.LocalInstance;
import polyglot.types.TypeSystem;
import polyglot.types.VarInstance;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10cpp.visit.X10SummarizingRules;  // PV-IPA
import static polyglot.ext.x10cpp.visit.Emitter.mangled_method_name;
import static polyglot.ext.x10cpp.visit.Emitter.mangled_non_method_name;

public class X10CPPContext_c extends polyglot.ext.x10.types.X10Context_c implements X10Context {

	public X10CPPContext_c(TypeSystem ts) {
		super(ts);
		finish_depth = 0;
		ateach_depth = 0;
		block_depth = 0;
		condDepth = 0;
		inprocess = false;
		mainMethod = false;
		requireMangling = true;
	}

	public boolean inClosure;
	public boolean insideClosure;

	public ArrayList variables = new ArrayList();
	public ArrayList SPMDVars = new ArrayList();
	public ArrayList GlobalVars = new ArrayList();
	public HashSet Unbroadcastable = new HashSet();

	public HashMap InlineMap = new HashMap();
	public HashMap RenameMap = new HashMap();
	public HashMap duplicateId = new HashMap();
	public boolean inlining;
	public static String returnLabel;
	public static String returnVar;
	public static String lastReturnVar;
	public boolean canInline;

	public ArrayList pendingImports = new ArrayList();
	public static HashMap classesWithAsyncSwitches = new HashMap();
	public static HashMap classesWithArrayCopySwitches = new HashMap();

	public HashSet warnedAbout = new HashSet();

	public void setinClosure(boolean b) {
		inClosure = b;
	}

	public void setinsideClosure(boolean b) {
		insideClosure = b;
	}

	protected Context_c push() {
		X10CPPContext_c v = (X10CPPContext_c) super.push();
		return v;
	}
	
/**	 Stack of constructorDecl frames used in processing a constructor Decl with this calls. */
	public Stack cDecls = new Stack(); // FIXME  Efficiency tip -- this needs to be initialized only once per outermost context and not everywhere as here.    

	public LinkedList<X10SummarizingRules.Summary> summaries = null;  // PV-IPA

	public X10SummarizingRules.Collection harvest = null;              // PV-IPA

	public X10CPPContext_c outerContext() {  // PV-IPA
		return (X10CPPContext_c)outer;
	}

	public X10CPPContext_c outermostContext() {  // PV-IPA
		if (outer == null)
			return this;
		else
			return ((X10CPPContext_c) outer).outermostContext();
	}

	public static class Closures {
		public ArrayList asyncs = new ArrayList();
		public ArrayList asyncsParameters = new ArrayList();

		public ArrayList arrayInitializers = new ArrayList();
		public ArrayList arrayInitializerParameters = new ArrayList();

		public HashMap arrayCopyClosures = new HashMap();
		public HashMap asyncContainers = new HashMap();

		public int nesting = 0;
	}

	public Closures closures = new Closures();

	public Closures getClosures() {
		if (isSource()) {
			System.err.println("found source context");
			return closures;
		} else
			return ((X10CPPContext_c) outer).getClosures();
	}

	public void saveEnvVariableInfo(String name) {
		VarInstance vi = findVariableInThisScope(name);
		if (vi != null) {  // found declaration 
			return;  // local variable
		} else if (inClosure) {
			addVar(name);
			return;  // local variable
		} else {  // Captured Variable
			((X10CPPContext_c) outer).saveEnvVariableInfo(name);
		}
	}

	public void addVar(String name) {
		VarInstance vi = lookup(name);
		boolean contains = false;
		for (int i = 0; i < variables.size(); i++) {
			if (((VarInstance) variables.get(i)).name().toString().equals(vi.name().toString())) {
				contains = true;
				break;
			}
		}
		if (!contains) variables.add(vi);
	}


	private boolean listContains(ArrayList list, Object val) {
		for (int i = 0; i < list.size(); i++)
			if (val == list.get(i))
				return true;
		return false;
	}

	public boolean isSPMDVar(VarInstance var) {
		if (!mainMethod)
			return false;  // Cannot have a global outside of main
		if (var instanceof LocalInstance && isInlineMapped(var))
			var = getInlineMapping(var);
		if (listContains(SPMDVars, var))
			return true;
		if (outer != null)
			return ((X10CPPContext_c) outer).isSPMDVar(var);
		return false;
	}

	public void addSPMDVar(VarInstance var) {
		if (isSPMDVar(var))
			return;
		assert (!isSPMDVar(var));
		assert (mainMethod);
		SPMDVars.add(var);
	}

	public void addGlobalVar(VarInstance var, Integer id) {
		if (isGlobalVar(var)) //if it is the exact same instance, then don't add.
			return;
		assert (mainMethod);
		assert (!isGlobalVar(var));
		X10CPPContext_c outer = (X10CPPContext_c) this.outer;
		// [IP] The condition below is too strict.
		// We can also have global vars inside conditionals.
//		assert (outer.isCode());
		assert (outer.isMainMethod());

		// If it is a different varInstance but same name then
		// rename.
		if (findGlobalVarSilent(var.name().toString()) != null) {
			duplicateId.put(var,id);
		}
		GlobalVars.add(var);
	}

	public boolean isGlobalVar(VarInstance var) {
		if (!mainMethod)
			return false;  // Cannot have a global outside of main
		// TODO: should we go via the inline mapping here as well?
		if (GlobalVars.contains(var))
			return true;
		if (outer != null)
			return ((X10CPPContext_c) outer).isGlobalVar(var);
		return false;
	}

	public ArrayList getGlobals() {
		return GlobalVars;
	}

	public VarInstance findGlobalVarSilent(String name) {
		for (int i = 0; i < GlobalVars.size(); i++) {
			VarInstance var = (VarInstance)GlobalVars.get(i);
			if (var.name().toString().equals(name))
				return var;
		}
		return null;
	}

	public void addUnbroadcastable(VarInstance var) {
		if (isUnbroadcastable(var))
			return;
		assert (mainMethod);
		assert (!isUnbroadcastable(var));
		Unbroadcastable.add(var);
	}

	public boolean isUnbroadcastable(VarInstance var) {
		if (!mainMethod)
			return false;  // Cannot have a global outside of main
		// TODO: should we go via the inline mapping here as well?
		if (Unbroadcastable.contains(var))
			return true;
		if (outer != null)
			return ((X10CPPContext_c) outer).isUnbroadcastable(var);
		return false;
	}

	public void addInlineMapping(LocalInstance alias, LocalInstance var) {
		if (isInlineMapped(alias))
			return;
		assert (!isInlineMapped(alias));
		X10CPPContext_c outer = (X10CPPContext_c) this.outer;
		assert (outer.isMainMethod());
		InlineMap.put(alias, var);
	}

	public boolean isInlineMapped(VarInstance var) {
		if (InlineMap.containsKey(var))
			return true;
		if (outer != null)
			return ((X10CPPContext_c) outer).isInlineMapped(var);
		return false;
	}

	public LocalInstance getInlineMapping(VarInstance var) {
		assert (var instanceof LocalInstance);
		LocalInstance result = (LocalInstance) InlineMap.get(var);
		if (result == null && outer != null) {
			result = ((X10CPPContext_c) outer).getInlineMapping(var);
		}
		return result;
	}
	public Object getDuplicateId(VarInstance var) {
		 return duplicateId.get(var);
	}

	public void addRenameMapping(VarInstance var, String newName) {
		assert (inlining);
		List names = (List) RenameMap.get(var);
		if (names == null)
			RenameMap.put(var, names = new ArrayList());
		names.add(newName);
	}

	public String getCurrentName(VarInstance var) {
		String D = "";
		if (insideClosure && isGlobalVar(var) && getDuplicateId(var) != null) {
			D = getDuplicateId(var).toString();
		}
		if (!inlining)
			return mangled_non_method_name(var.name().toString() + D); 
		List names = getRenameMapping(var);
		if (names == null)
			return mangled_non_method_name(var.name().toString() + D); 
		return mangled_non_method_name((String) names.get(names.size()-1) + D);
	}

	public List getRenameMapping(VarInstance var) {
		return (List) RenameMap.get(var);
	}

	public void removeRenameMapping(VarInstance var) {
		RenameMap.remove(var);
	}

	private VarInstance lookup(String name) {
		VarInstance vi = findVariableInThisScope(name);
		if (vi != null) return vi;
		else if (outer != null) return ((X10CPPContext_c) outer).lookup(name);
		else return null;
	}

	public void finalizeClosureInstance() {
		// remove all from current context. Move those pertinent to an outer
		// closure to the outer closure.
//		for (int i = variables.size() - 1; i >= 0; i--) {
//			VarInstance v = (VarInstance) variables.remove(i);
//			((X10Context_c) outer).saveEnvVariableInfo(v.name());
//		}
		for (int i = 0; i < variables.size(); i++) {
			VarInstance v = (VarInstance) variables.get(i);
			VarInstance cvi = findVariableInThisScope(v.name().toString());
			if (cvi == null)   // declaration not found 
				((X10CPPContext_c) outer).saveEnvVariableInfo(v.name().toString());
		}
	}

	public Object copy() {
		X10CPPContext_c res = (X10CPPContext_c) super.copy();
		res.variables = new ArrayList();  // or whatever the initial value is
		res.inClosure = false;
		res.InlineMap = new HashMap();
		res.summaries = null;  // PV-IPA
		return res;
	}

	private boolean mainMethod;

	public boolean isMainMethod() {
		return mainMethod;
	}

	public void setMainMethod() {
		mainMethod = true;
	}

	public void resetMainMethod() {
		mainMethod = false;
	}

	private boolean asyncInlinable;

	// this variable is set, if we are inside a loop structure or
	// nested asyncs, throw statement, or conditional atomics. Q: How
	// to detect if there are messages that are being initiated?
	// -Krishna.
	public boolean inlinableAsyncsOnly() {
		return asyncInlinable;
	}

	public void setInlinableAsyncsOnly() {
		asyncInlinable = true;
	}

	public void resetInlinableAsyncsOnly() {
		asyncInlinable = false;
	}

	private boolean seqCode;

	public boolean noAtEachCode() {
		return seqCode;
	}

	public void setAtEachCode() {
		seqCode = true;
	}

	public void resetAtEachCode() {
		seqCode = false;
	}

	public static int async_depth = 0;

	public static int finish_cnt = 0;

	public int finish_depth;

	public int ateach_depth;

	private int block_depth;

	private static int curCondCnt;

	public void incrCurCond() {
		curCondCnt += 2;
	}

	public int curCond() {
		return curCondCnt;
	}

	private int condDepth;

	public int curCondDepth() {
		return condDepth;
	}

	public void incrCondDepth() {
		condDepth++;
	}

	public void decrCondDepth() {
		condDepth--;
	}

	public boolean inprocess;

	public static boolean inplace0;

	// I would think that we do not need static here.
	// But someone is overwriting the context and I am
	// ending up having wrong labels.
	// -Krishna. TODO: Fixit.
	public static int nextLabel;

	private String label;

	private Stmt stmt;

	public void setLabel(String label, Stmt stmt) {
		this.label = label;
		this.stmt = stmt;
	}

	public String getLabel() {
		return label;
	}

	public Stmt getLabeledStatement() {
		return stmt;
	}

	private String excVar;

	public boolean hasInits = false;

	public void setExceptionVar(String var) {
		this.excVar = var;
	}

	public String getExceptionVar() {
		return excVar;
	}
	private boolean requireMangling;
	public boolean requireMangling() { return requireMangling; }
	public void setRequireMangling() { requireMangling = true; }
	public void resetRequireMangling() { requireMangling = false; }
        private static int localClassDepth;
	public boolean inLocalClass(){ return localClassDepth != 0; }
	public void pushInLocalClass(){ localClassDepth ++ ; }
	public void popInLocalClass(){ localClassDepth--; }

	private static String selfStr;
	public void setSelf(String str){ selfStr = str;};
	public void resetSelf() { selfStr = null;};
	public String Self() { return selfStr;};

}
