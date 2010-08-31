package com.ibm.wala.cast.x10.analysis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;
import x10.finish.table.*;

import com.ibm.wala.cast.java.ssa.AstJavaInvokeInstruction;
import com.ibm.wala.cast.loader.AstMethod;
import com.ibm.wala.cast.x10.client.X10SourceAnalysisEngine;
import com.ibm.wala.cast.x10.ssa.*;
import com.ibm.wala.cast.x10.translator.polyglot.X10SourceLoaderImpl;
import com.ibm.wala.cfg.AbstractCFG;
import com.ibm.wala.cfg.ControlFlowGraph;
import com.ibm.wala.cfg.IBasicBlock;
import com.ibm.wala.cfg.ShrikeCFG;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IBytecodeMethod;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.SourceDirectoryTreeModule;
import com.ibm.wala.classLoader.SourceFileModule;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.DefaultEntrypoint;
import com.ibm.wala.ipa.callgraph.impl.ExplicitCallGraph.ExplicitNode;
import com.ibm.wala.ipa.cfg.BasicBlockInContext;
import com.ibm.wala.ipa.cfg.ExceptionPrunedCFG;
import com.ibm.wala.ipa.cfg.ExplodedInterproceduralCFG;
import com.ibm.wala.ipa.cfg.PrunedCFG;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.shrikeBT.IInstruction;
import com.ibm.wala.ssa.*;
import com.ibm.wala.ssa.analysis.IExplodedBasicBlock;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.Descriptor;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.collections.Filter;
import com.ibm.wala.util.graph.AbstractNumberedGraph;
import com.ibm.wala.util.graph.EdgeManager;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.util.graph.GraphPrint;
import com.ibm.wala.util.graph.NodeManager;
import com.ibm.wala.util.graph.NumberedGraph;
import com.ibm.wala.util.graph.dominators.NumberedDominators;
import com.ibm.wala.util.graph.impl.GraphInverter;
import com.ibm.wala.util.graph.impl.InvertedNumberedGraph;
import com.ibm.wala.util.graph.impl.SparseNumberedGraph;
import com.ibm.wala.util.graph.traverse.DFS;
import com.ibm.wala.util.intset.OrdinalSet;
import com.ibm.wala.util.strings.Atom;
import com.ibm.wala.util.warnings.WalaException;
import com.ibm.wala.viz.DotUtil;
import com.ibm.wala.viz.NodeDecorator;
import com.ibm.wala.cast.x10.analysis.util.*;

/**
 * 
 * 
 * @author baolin
 */
public class X10FinishAsyncAnalysis {
	private static boolean DEBUG = false;
	private static final boolean DEBUG_INSTRUCTION = DEBUG|false;
	private static final boolean DEBUG_IR = DEBUG|false;
	private static final boolean DEBUG_CFG = DEBUG|false;
	private static final boolean DEBUG_CG = DEBUG|false;
	private HashMap<CallTableKey, LinkedList<CallTableVal>> calltable;
	private CallGraph cg;
	
	// to store an instruction's index in a cfg and used later to get source
	// information
	HashMap<SSAInstruction, Integer> instmap;
	CallTableVal last_inst;
	/**
	 * used to hold visited states in deepth-first traversal of a cfg
	 */
	HashSet<Integer> visited;
	/**
	 * {@link com.ibm.wala.util.graph.dominators.NumberedDominators} dom is
	 * generated per control flow graph (hence per method).
	 */
	NumberedDominators<ISSABasicBlock> dom;

	/**
	 * similar to dom, invert_dom has the dominator information for an inverted
	 * control flow graph. It is used to check whether all paths from node n
	 * will pass node m.
	 */
	NumberedDominators<ISSABasicBlock> invert_dom;

	/**
	 * to handle loops in a control flow graph, we compute all natural loops
	 * (loop with single-entry point) before hand.
	 */
	HashSet<NatLoop> loops;
	
	private void init(){
		instmap = null;
		last_inst = null;
		visited = null;
		dom = null;
		invert_dom = null;
		loops = null;
	}
	/**
	 * hashmap does not provide "remove and add" functionality, and it is what
	 * this method for.
	 * 
	 * @param k
	 *            - key in the table
	 * @param v
	 *            - value of the key
	 */
	private void replaceTable(CallTableKey k, CallTableVal v) {
		LinkedList<CallTableVal> old_vals = calltable.get(k);
		if (!old_vals.contains(v)) {
			calltable.remove(k);
			old_vals.add(v);
			calltable.put(k, old_vals);
		}
	}

	/**
	 * for an "async" invocation, this method checks whether there is any
	 * "if-eles" and "loop" along the way from the "finish" node, if any, to
	 * this "async" node, in the control flow graph
	 * 
	 * @param finish
	 *            : node of the "finish enter" statement
	 * @param async
	 *            : node of the "async" statement
	 * @return: {@link CallTableVal$Arity}
	 */
	private CallTableVal.Arity checkAsync(ISSABasicBlock finish,
			ISSABasicBlock async) {
		if (invert_dom.isDominatedBy(finish, async)) {
			// finish{ async }
			return CallTableVal.Arity.One;
		}
		if (NatLoopSolver.containsNode(loops, async)) {
			if (NatLoopSolver.containsNode(loops, finish)) {
				HashSet<NatLoop> fl = NatLoopSolver.getLoops(loops, finish);
				HashSet<NatLoop> al = NatLoopSolver.getLoops(loops, async);
				if (fl.equals(al)) {
					// for(){ finish{ async } }
					return CallTableVal.Arity.One;
				}
				// for(){ finish{ for(){ async } } }
				return CallTableVal.Arity.Unbounded;
			}

			// finish{ for(){ async } }
			return CallTableVal.Arity.Unbounded;

		}
		// finish{ if(){ async } }
		return CallTableVal.Arity.ZerOrOne;

	}

	private void visitFinishInstruction(
			ControlFlowGraph<SSAInstruction, ISSABasicBlock> epcfg,
			AnalysisEnvironment env, SSAFinishInstruction finst) {

		int index = instmap.get(finst).intValue();
		int line = ((AstMethod) epcfg.getMethod()).getSourcePosition(index)
				.getFirstLine();
		int column = ((AstMethod) epcfg.getMethod()).getSourcePosition(index)
				.getLastCol();

		if (finst.isFinishEnter()) {
			// assert(env.cur_scope!=null);
			env.uncompleted_scope.push(env.cur_scope);

			// get package
			CGNode caller = cg.getNode(env.cur_method_node);
			String scope = getPackage(caller.getMethod().getDeclaringClass()
					.getName().toString());
			// get the name of a method that has this finish
			String name = env.cur_scope.name;
			// use package +line + column to distinguish finish
			env.cur_scope = new CallTableScopeKey(scope, name, line, column,
					env.cur_block, true);
			calltable.put(env.cur_scope, new LinkedList<CallTableVal>());
		} else {
			// finish exit
			if (last_inst != null) {
				last_inst.isLast = true;
				env.cur_scope.lastStmt = last_inst;
				last_inst = null;
			}
			// assert(env.uncompleted_scope.isEmpty()==false);
			env.cur_scope = env.uncompleted_scope.pop();
		}
	}

	private void visitAtInstruction(
			ControlFlowGraph<SSAInstruction, ISSABasicBlock> epcfg,
			AnalysisEnvironment env, SSAAtStmtInstruction atinst) {

		ISSABasicBlock curblk = (epcfg.getNode(env.cur_block));
		int instindex = instmap.get(atinst).intValue();
		int line = ((AstMethod) epcfg.getMethod()).getSourcePosition(instindex)
				.getFirstLine();
		int column = ((AstMethod) epcfg.getMethod()).getSourcePosition(
				instindex).getLastCol();

		if (atinst.toString().contains("enter")) {
			// assert(env.cur_scope!=null);
			// step 1: treat "at" as a "value" in the calltable
			CallTableVal.Arity arity;
			CallTableKey tmpkey = env.cur_scope;
			// calculate arity
			if (tmpkey instanceof CallTableScopeKey) {
				CallTableScopeKey cf = (CallTableScopeKey) tmpkey;
				ISSABasicBlock fb = epcfg.getNode(cf.blk);
				arity = checkAsync(fb, curblk);

			} else {
				// current scope is a method, neither a finish nor an at
				ISSABasicBlock root = epcfg.entry();
				arity = checkAsync(root, curblk);
			}
			CallTableAtVal atval = new CallTableAtVal(env.cur_scope.scope, "",
					arity, line, column, env.cur_block, env.cur_scope);
			updateLastInst(env.cur_scope, atval);
			replaceTable(tmpkey, atval);

			// step2: treat "at" as a "key" in the calltable
			env.uncompleted_scope.push(env.cur_scope);
			// create a CallTableKey object for this at
			String name = env.cur_scope.name;
			CGNode caller = cg.getNode(env.cur_method_node);
			String scope = getPackage(caller.getMethod().getDeclaringClass()
					.getName().toString());
			env.cur_scope = new CallTableScopeKey(scope, name, line, column,
					env.cur_block, false);
			calltable.put(env.cur_scope, new LinkedList<CallTableVal>());
		} else {
			// at exit
			// assert(env.cur_scope instanceof CallTableScopeKey);

			// deal with current scope, the scope of at, last statement
			if (last_inst != null) {
				last_inst.isLast = true;
				env.cur_scope.lastStmt = last_inst;
				last_inst = null;
			}

			// handle scoping
			CallTableScopeKey curkey = (CallTableScopeKey) env.cur_scope;
			CallTableKey prekey = env.uncompleted_scope.pop();
			env.cur_scope = prekey;

			// update previous scope's last instruction, which is this at
			// instruction

			// create a fake "at" with the same signature as the one we want to
			// fetch
			// that one in the table
			CallTableAtVal tmpatval = new CallTableAtVal(curkey.scope, "",
					curkey.line, curkey.column, env.cur_block, env.cur_scope);
			int index = calltable.get(prekey).indexOf(tmpatval);
			last_inst = calltable.get(prekey).get(index);
		}
	}

	/**
	 * create a CallTableMethodVal based on parameters and add it to the
	 * corresponding CallTableKey
	 * 
	 * @param epcfg
	 * @param env
	 * @param defpack
	 *            : package this method is defined
	 * @param defname
	 *            : this method's name
	 * @param defline
	 *            : line this method is defined
	 * @param defcol
	 *            : column this method is defined
	 * @param callpack
	 *            : package this method is called
	 * @param callname
	 *            : name of the method which calls this one
	 * @param calline
	 *            : line this method is called
	 * @param callcol
	 *            : column this method is called
	 * @param is_async
	 *            : flag to indicate it is an async
	 */
	private void updateTable(
			ControlFlowGraph<SSAInstruction, ISSABasicBlock> epcfg,
			AnalysisEnvironment env, String defpack, String defname,
			int defline, int defcol, String callpack, String callname,
			int calline, int callcol, boolean isAsync, boolean isLocal) {
		// calculate arity
		CallTableKey tmpkey = env.cur_scope;
		CallTableVal.Arity arity;
		ISSABasicBlock curblk = (epcfg.getNode(env.cur_block));
		if (tmpkey instanceof CallTableScopeKey) {

			CallTableScopeKey cf = (CallTableScopeKey) tmpkey;
			ISSABasicBlock fb = epcfg.getNode(cf.blk);
			arity = checkAsync(fb, curblk);
		} else {
			ISSABasicBlock root = epcfg.entry();
			arity = checkAsync(root, curblk);
		}
		CallTableMethodVal aval = new CallTableMethodVal(defpack, defname,
				defline, defcol, arity, callpack, callname, calline, callcol,
				env.cur_block, env.cur_scope, isAsync, isLocal);
		updateLastInst(env.cur_scope, aval);
		replaceTable(tmpkey, aval);
	}

	private void visitAsyncInstruction(
			ControlFlowGraph<SSAInstruction, ISSABasicBlock> epcfg,
			AnalysisEnvironment env, AsyncInvokeInstruction inst) {
		MethodReference mr = inst.getDeclaredTarget();
		// each async is different and cannot be invoked twice, so use a dummy
		// pc
		String pack = mr.getDeclaringClass().getName().toString();
		String callsite_pack = env.cur_scope.scope;
		String callsite_name = env.cur_scope.name;
		pack = getPackage(pack);
		int index = instmap.get(inst).intValue();
		int line = ((AstMethod) epcfg.getMethod()).getSourcePosition(index)
				.getFirstLine();
		int column = ((AstMethod) epcfg.getMethod()).getSourcePosition(index)
				.getLastCol();
		updateTable(epcfg, env, pack, "activity", line, column, callsite_pack,
				callsite_name, line, column, true, inst.isHere);

	}

	private void visitMethodInvocation(
			ControlFlowGraph<SSAInstruction, ISSABasicBlock> epcfg,
			AnalysisEnvironment env, AstJavaInvokeInstruction inst) {
		int index = instmap.get(inst).intValue();
		/* the method that we are analyzing and the one invokes "inst" */
		CGNode caller;
		String callerPack;
		String callerName;
		int invokeLine;
		int invokeCol;
		/* same for all possible targets of "inst" */
		caller = cg.getNode(env.cur_method_node);
		callerPack = getPackage(caller.getMethod().getDeclaringClass()
				.getName().toString());
		callerName = getName(caller.getMethod().getName().toString());
		invokeLine = ((AstMethod) epcfg.getMethod()).getSourcePosition(index)
				.getFirstLine();
		invokeCol = ((AstMethod) epcfg.getMethod()).getSourcePosition(index)
				.getLastCol();

		/* the method that is possibly being called */
		CGNode callee;
		String defPack;
		String defName;
		int defLine;
		int defCol;

		CallSiteReference callsite = inst.getCallSite();
		Set<CGNode> allcallees = cg.getPossibleTargets(caller, callsite);
		if (allcallees.size() > 0) {
			Iterator<CGNode> it = allcallees.iterator();
			while (it.hasNext()) {
				callee = it.next();
				defPack = getPackage(callee.getMethod().getDeclaringClass()
						.getName().toString());
				defName = getName(callee.getMethod().getName().toString());
				defLine = ((AstMethod) callee.getMethod()).getSourcePosition()
						.getFirstLine();
				defCol = ((AstMethod) callee.getMethod()).getSourcePosition()
						.getLastCol();

				updateTable(epcfg, env, defPack, defName, defLine, defCol,
						callerPack, callerName, invokeLine, invokeCol, false,
						true);
			}
		} else {
			if (DEBUG) {
				System.err.println("cannot find callee's cgnode");
				System.err.println(inst.toString());
			}
		}
	}

	/**
	 * Analyze each instruction in a BasicBlock of a control flow graph and
	 * update the {@link CallTableVal,CallTableKey}
	 * 
	 * @param epcfg
	 *            :control flow graph
	 * @param env
	 *            : global information need to keep during the analysis of epcfg
	 *            {@link AnalysisEnvironment}
	 */
	private void visitAllInstructions(
			ControlFlowGraph<SSAInstruction, ISSABasicBlock> epcfg,
			AnalysisEnvironment env) {
		ISSABasicBlock curblk = (epcfg.getNode(env.cur_block));
		// assert (curblk != null);
		visited.add(Integer.valueOf(env.cur_block));
		Iterator<SSAInstruction> itt = curblk.iterator();
		while (itt.hasNext()) {
			SSAInstruction inst = itt.next();
			if (inst != null) {
				if(DEBUG_INSTRUCTION){
					System.out.println(inst);
				}
				if (inst instanceof SSAFinishInstruction) {
					SSAFinishInstruction finst = (SSAFinishInstruction) inst;
					visitFinishInstruction(epcfg, env, finst);
				} else if (inst instanceof SSAAtStmtInstruction) {
					SSAAtStmtInstruction atinst = (SSAAtStmtInstruction) inst;
					visitAtInstruction(epcfg, env, atinst);
				} else if (inst instanceof AsyncInvokeInstruction) {
					visitAsyncInstruction(epcfg, env,
							(AsyncInvokeInstruction) inst);
				} else if (inst instanceof AstJavaInvokeInstruction) {
					visitMethodInvocation(epcfg, env,
							(AstJavaInvokeInstruction) inst);
				}
				// TODO: when instruction
				else {
					updateLastInst(null, null);
				}
			}
		}
	}

	/**
	 * A Depth-First Search of a Control Flow Graph
	 * 
	 * @param epcfg
	 * @param env
	 */
	private void parseBlock(
			ControlFlowGraph<SSAInstruction, ISSABasicBlock> epcfg,
			AnalysisEnvironment env) {
		visitAllInstructions(epcfg, env);
		// DFS
		ISSABasicBlock curb = epcfg.getNode(env.cur_block);
		assert (curb != null);
		Iterator<ISSABasicBlock> it = epcfg.getSuccNodes(curb);
		while (it.hasNext()) {
			ISSABasicBlock cur = it.next();
			env.cur_block = cur.getNumber();
			if (!visited.contains(Integer.valueOf(env.cur_block))) {
				// need a deep copy of env because env is mutable and parseBlock
				// has side effects on it
				AnalysisEnvironment dupenv = (AnalysisEnvironment) OutputUtil
						.copy(env);
				parseBlock(epcfg, dupenv);

			}
		}
	}

	/**
	 * Analyze a method in the callgraph
	 * 
	 * @param nodenum
	 *            : node number of this method in callgraph
	 */
	private void parseIR(int nodenum) {
		CGNode md = cg.getNode(nodenum);
		IR ir = md.getIR();
		if (ir != null) {
			if(DEBUG_IR){
				System.out.println(ir);
			}
			// original control flow graph
			ControlFlowGraph<SSAInstruction, ISSABasicBlock> cfg = ir
					.getControlFlowGraph();
			if (cfg != null) {
				// remove machine added exceptions
				/*
				 * FIXME: this might cause the last statement of a block
				 * inaccurate, because "goto" is added with exceptions even
				 * though exceptions are removed, "goto" are not.
				 */
				PrunedCFG<SSAInstruction, ISSABasicBlock> epcfg = MyExceptionPrunedCFG
						.make(cfg);

				// create a new row for this method in the calltable
				String _package = ((AstMethod) epcfg.getMethod())
						.getReference().getDeclaringClass().getName()
						.toString();
				String _name = epcfg.getMethod().getName().toString();
				_name = getName(_name);
				_package = getPackage(_package);
				int line = ((AstMethod) epcfg.getMethod()).debugInfo()
						.getCodeBodyPosition().getFirstLine();
				int column = ((AstMethod) epcfg.getMethod()).debugInfo()
						.getCodeBodyPosition().getLastCol();
				CallTableKey ctk = new CallTableMethodKey(_package, _name,
						line, column);
				calltable.put(ctk, new LinkedList<CallTableVal>());

				// map each instruction to its index in the cfg and used later
				// to retrieve source info
				SSAInstruction[] insts = epcfg.getInstructions();
				instmap = new HashMap<SSAInstruction, Integer>();
				for (int i = 0; i < insts.length; i++) {
					instmap.put(insts[i], new Integer(i));
				}

				// prepare an analysis environment to hold tmp info during
				// traveral of cfg
				AnalysisEnvironment env = new AnalysisEnvironment(ctk, nodenum,
						epcfg);
				visited = new HashSet<Integer>();
				ISSABasicBlock root = epcfg.entry();
				env.cur_block = root.getNumber();

				// calculate domination relationship of cfg
				dom = new NumberedDominators<ISSABasicBlock>(epcfg, root);

				// calculate inverse domination relationship of cfg
				InvertedNumberedGraph<ISSABasicBlock> invert_epcfg = (InvertedNumberedGraph<ISSABasicBlock>) GraphInverter
						.invert(epcfg);
				ISSABasicBlock invert_root = invert_epcfg.getNode(epcfg.exit()
						.getNumber());
				invert_dom = new NumberedDominators<ISSABasicBlock>(
						invert_epcfg, invert_root);

				// find out each loop in terms of all nodes it has
				HashSet<Integer> loopvisited = new HashSet<Integer>();
				loops = new HashSet<NatLoop>();
				NatLoopSolver
						.findAllLoops(epcfg, dom, loops, loopvisited, root);


				if (DEBUG_CFG) {
					GraphUtil.printCFG(epcfg,md.getMethod().getName().toString()+"_removed");
					GraphUtil.printCFG(cfg,md.getMethod().getName().toString());
				}

				// traverse cfg
				parseBlock(epcfg, env);

				// update last instruction
				if (last_inst != null) {
					last_inst.isLast = true;
					ctk.lastStmt = last_inst;
					last_inst = null;
				}
			}// end of (epcfg!=null)
		}// end of (ir!=null)

	}

	/**
	 * "entry point of the real analysis. It analyzes each method in the
	 * callgraph of the input program. but it omits some "dummy" method, e.g.
	 * "fake..." or "init..."
	 */
	private void buildCallTable() {
		assert cg != null;
		Iterator<CGNode> all_methods = cg.iterator();
		while (all_methods.hasNext()) {
			CGNode one_method = all_methods.next();
			String method_name = one_method.getMethod().getName().toString();
			// "fake" and "init" is not of interest
			if (method_name.contains("fake") || method_name.contains("init")) {
				continue;
			}
			parseIR(cg.getNumber(one_method));

		}
	}

	/**
	 * invoke this method when use wala as a stand-alone application and this method is supposed to build a complete calltable once
	 * @param testedFile
	 * @param defaultEntryPoint
	 * @return
	 * @throws Exception
	 */
	public HashMap<CallTableKey, LinkedList<CallTableVal>> build(final File testedFile, String[] defaultEntryPoint) throws Exception {
		this.calltable = new HashMap<CallTableKey, LinkedList<CallTableVal>>();
		cg = this.buildCallGraph(testedFile, defaultEntryPoint);
		// to make sure different invocations for building call table don't have side-effect on this call
		init();
		buildCallTable();
		return this.calltable;
	}
	

	public HashMap<CallTableKey, LinkedList<CallTableVal>> build(CallGraph cg,HashMap<CallTableKey, LinkedList<CallTableVal>> calltable){
		this.calltable = calltable;
		this.cg = cg;
		// to make sure different invocations only have side-effects on calltable
		init();
		buildCallTable();
		return this.calltable;
	}
	
	private CallGraph buildCallGraph(final File testedFile, String[] defaultEntryPoint) throws Exception{
		final String pack = defaultEntryPoint[0];
		final String entrymethod = defaultEntryPoint[1];
		final String methodtype = defaultEntryPoint[2];
		final String mainClass = testedFile.getName().substring(0,
				testedFile.getName().lastIndexOf('.'));

		final X10SourceAnalysisEngine engine = new X10SourceAnalysisEngine() {

			protected Iterable<Entrypoint> makeDefaultEntrypoints(
					final AnalysisScope scope, final IClassHierarchy cha) {

				final ClassLoaderReference loaderRef = X10SourceLoaderImpl.X10SourceLoader;
				final TypeReference typeRef = TypeReference.findOrCreate(
						loaderRef, 'L' + pack + mainClass);
				final MethodReference mainRef = MethodReference.findOrCreate(
						typeRef, Atom.findOrCreateAsciiAtom(entrymethod),
						Descriptor.findOrCreateUTF8(methodtype));
				// Lx10/lang/Rail;
				final Collection<Entrypoint> entryPoints = new ArrayList<Entrypoint>(
						1);
				entryPoints.add(new DefaultEntrypoint(mainRef, cha));
				return entryPoints;
			}

			public String getExclusionsFile() {
				return null;
			}
		};
		engine.addX10SystemModule(new SourceDirectoryTreeModule(new File(
				"../x10.runtime/src-x10/"), "10"));
		engine.addX10SourceModule(new SourceFileModule(testedFile, testedFile
				.getName()));
		cg = engine.buildDefaultCallGraph();
		if(DEBUG_CG){
			GraphUtil.printNumberedGraph(cg, "cg");
		}
		return cg;
	}
	/**
	 * 
	 * @param s
	 *            - fully qualified class name obtained from CGNode
	 * @return - only the package part
	 */
	private String getPackage(String s) {
		// an "async" method has a different format as a normal method
		if (s.contains("activity")) {
			int start = s.indexOf(':');
			int end = s.indexOf(".x10:");
			s = s.substring(start + 2, end);
			s = s.replace('/', '.');
			return s;
		}
		s = s.substring(1);
		s = s.replace('/', '.');
		return s;
	}

	/**
	 * 
	 * @param s
	 *            - name of a method obtained from CGNode's getName method
	 * @return - if this method is just a wrapper for an async, return
	 *         "activity" as the name
	 */
	private String getName(String s) {
		if (s.contains("activity")) {
			return "activity";
		}
		return s;
	}

	private void updateLastInst(CallTableKey k, CallTableVal v) {
		if (last_inst != null)
			last_inst.isLast = false;
		last_inst = v;
		if (last_inst != null)
			last_inst.isLast = true;
	}
} // end of class

/*
 * This class is used to carry "path independent" information during the
 * analysis.
 */
class AnalysisEnvironment implements Serializable {
	private static final long serialVersionUID = 1L;

	// hold nested finish/at
	Stack<CallTableKey> uncompleted_scope;
	// current finish/at/method
	CallTableKey cur_scope;
	int cur_block;
	// method's index in callgraph
	int cur_method_node;

	public AnalysisEnvironment(CallTableKey n, int nd,
			PrunedCFG<SSAInstruction, ISSABasicBlock> epcfg) {
		uncompleted_scope = new Stack<CallTableKey>();
		cur_scope = n;
		cur_block = 0;
		cur_method_node = nd;
	}
}
