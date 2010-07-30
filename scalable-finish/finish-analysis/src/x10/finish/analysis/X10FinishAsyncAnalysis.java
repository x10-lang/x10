package x10.finish.analysis;
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
import x10.finish.util.GraphUtil;
import x10.finish.util.MyExceptionPrunedCFG;
import x10.finish.util.NatLoop;
import x10.finish.util.NatLoopSolver;

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

/**
 * 
 * 
 * @author baolin
 */
public class X10FinishAsyncAnalysis {
    HashMap<SSAInstruction,Integer> instmap;
    // per program
    /*
     * data structure we use to capture caller-callee relationship in a x10
     * program a key-value entry in the maps means the "key" calls the "value",
     * if the "key" is a method or the "key" contains the "value" if the key is
     * a finish. The "value" is always a method.
     */
    HashMap<CallTableKey, LinkedList<CallTableVal>> calltable;
    /* for dumping epcfg */
    private int cnt = 0;
    CallGraph cg;
    CallTableVal last_inst;
    /**
     * used to hold visited states in deepth-first searching a control flow
     * graph
     */
    HashSet<Integer> visited;

    /**
     * {@link com.ibm.wala.util.graph.dominators.NumberedDominators} dom is
     * generated per control flow graph (hence per method). After it is assigned
     * a new value each time, its previous one becomes garbage.
     */
    NumberedDominators<ISSABasicBlock> dom;

    /**
     * similar to dom, invert_dom has the dominator information for an inverted
     * control flow graph. It is used to check whether all paths from node n of
     * a control flow graph will pass node m.
     */
    NumberedDominators<ISSABasicBlock> invert_dom;

    /**
     * to handle loops in a control flow graph, we compute all natural loops
     * (loop with single-entry point) before hand.
     */
    HashSet<NatLoop> loops;

    
    public X10FinishAsyncAnalysis() {
	calltable = new HashMap<CallTableKey, LinkedList<CallTableVal>>();
	last_inst = null;
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
	     //finish{ async } 
	    return CallTableVal.Arity.One;
	}
	if (NatLoopSolver.containsNode(loops, async)) {
	    if (NatLoopSolver.containsNode(loops, finish)) {
		HashSet<NatLoop> fl = NatLoopSolver.getLoops(loops, finish);
		HashSet<NatLoop> al = NatLoopSolver.getLoops(loops, async);
		if (fl.equals(al)) {
		     //for(){ finish{ async } } 
		    return CallTableVal.Arity.One;
		}
		 //for(){ finish{ for(){ async } } } 
		return CallTableVal.Arity.Unbounded;
	    }

	     //finish{ for(){ async } } 
	    return CallTableVal.Arity.Unbounded;

	}
	 //finish{ if(){ async } } 
	return CallTableVal.Arity.ZerOrOne;

    }

    private void visitFinishInstruction(
	    ControlFlowGraph<SSAInstruction, ISSABasicBlock> epcfg,
	    AnalysisEnvironment env, SSAFinishInstruction finst) {
	int index = instmap.get(finst).intValue();
	int line = ((AstMethod) epcfg.getMethod()).getSourcePosition(index).getFirstLine();
	int column = ((AstMethod) epcfg.getMethod()).getSourcePosition(index).getLastCol();
	if (finst.isFinishEnter()) {
	     //use stack to handle nested finish 
	    assert(env.cur_scope!=null);
	    String name = env.cur_scope.name;
	    env.uncompleted_scope.push(env.cur_scope);
	    CGNode caller = cg.getNode(env.cur_method_node);
	    String scope = getPackage(caller.getMethod().getDeclaringClass().getName().toString());
 	    env.cur_scope = new CallTableScopeKey(scope, name, line, column,env.cur_block,true);
	    // System.out.println(env.cur_finish.toString());
	    calltable.put(env.cur_scope, new LinkedList<CallTableVal>());
	} else {
	    // finish exit
	    if(last_inst!=null){
		last_inst.aslast = env.cur_scope;
		env.cur_scope.lastStmt = last_inst;
		last_inst = null;
	    }
	    assert(env.uncompleted_scope.isEmpty()==false);
	    env.cur_scope = env.uncompleted_scope.pop();
	}
    }

    private void visitAtInstruction(
	    ControlFlowGraph<SSAInstruction, ISSABasicBlock> epcfg,
	    AnalysisEnvironment env, SSAAtStmtInstruction atinst) {

	ISSABasicBlock curblk = (epcfg.getNode(env.cur_block));
	int instindex = instmap.get(atinst).intValue();
	int line = ((AstMethod) epcfg.getMethod()).getSourcePosition(instindex).getFirstLine();
	int column = ((AstMethod) epcfg.getMethod()).getSourcePosition(instindex).getLastCol();
	if (atinst.toString().contains("enter")) {
	    assert(env.cur_scope!=null);
	    CallTableKey tmpkey = env.cur_scope;
	    if (tmpkey instanceof CallTableScopeKey) {
		CallTableScopeKey cf =(CallTableScopeKey)tmpkey;
		ISSABasicBlock fb = epcfg.getNode(cf.blk);
		CallTableVal.Arity arity = checkAsync(fb, curblk);
		CallTableAtVal atval = 
		    new CallTableAtVal(env.cur_scope.scope,"",arity,line,
			    column,env.cur_block);
		updateLastInst(env.cur_scope,atval);
		replaceTable(tmpkey, atval);
	    } else {
		ISSABasicBlock root = epcfg.entry();
		CallTableVal.Arity arity = checkAsync(root, curblk);
		CallTableAtVal atval = new CallTableAtVal(env.cur_scope.scope,
			"",arity,line,column,env.cur_block);
		//System.err.println(atval.toString());
		updateLastInst(env.cur_scope,atval);
		replaceTable(tmpkey, atval);
		
	    }
	    env.uncompleted_scope.push(env.cur_scope);
	    String name = env.cur_scope.name;
	    CGNode caller = cg.getNode(env.cur_method_node);
	    String scope = getPackage(caller.getMethod().getDeclaringClass().getName().toString());
	    //-1 is the dummy blk
	    env.cur_scope = new CallTableScopeKey(scope,name, line,column,env.cur_block,false);
	    calltable.put(env.cur_scope, new LinkedList<CallTableVal>());
	} else {
	    // at exit
	    // add this "at" statement to its "finish" or "method"
	    assert(env.cur_scope instanceof CallTableScopeKey);
	    CallTableKey prekey;
	    if(last_inst!=null){
		last_inst.aslast = env.cur_scope;
		env.cur_scope.lastStmt = last_inst;
		last_inst = null;
	    }
	    
	    CallTableScopeKey curkey = (CallTableScopeKey)env.cur_scope;
	    //mark the current "at" as the last instruction for its scope
	    // create a dummy CallTableAtVal with the same signature as the target one in the talbe 
	    CallTableAtVal tmpatval = new CallTableAtVal(curkey.scope,"",curkey.line,
		    curkey.column,env.cur_block);
	    //System.err.println(tmpatval.toString());
	    assert(env.uncompleted_scope.isEmpty()==false);
	    prekey = env.uncompleted_scope.pop();	    
	    env.cur_scope = prekey;
	    // get the target CallTableAtval's index in its key's list
	    int index = calltable.get(prekey).indexOf(tmpatval);
	    // get the target and save it as the last instruction of this scope
	    last_inst = calltable.get(prekey).get(index);
	}
    }
    
    private void updateTable(ControlFlowGraph<SSAInstruction, ISSABasicBlock> epcfg,
	    AnalysisEnvironment env, String defpack, String defname, int defline, int defcol, 
	    String callpack, String callname, int calline, int callcol, boolean is_async){
	
	

	ISSABasicBlock curblk = (epcfg.getNode(env.cur_block));
	CallTableKey tmpkey = env.cur_scope;
	//System.err.println(tmpkey.toString());
	CallTableVal.Arity arity;	
	if (tmpkey instanceof CallTableScopeKey) {
	    CallTableScopeKey cf =(CallTableScopeKey)tmpkey;
	    ISSABasicBlock fb = epcfg.getNode(cf.blk);
	    arity = checkAsync(fb, curblk);
	} else {
	    ISSABasicBlock root = epcfg.entry();
	    arity = checkAsync(root, curblk);
	}
	CallTableMethodVal aval = new CallTableMethodVal(defpack, defname, defline, defcol,arity,
		callpack, callname, calline, callcol, env.cur_block, is_async);
	updateLastInst(env.cur_scope,aval);
	replaceTable(tmpkey, aval);
    }
    private void visitAsyncInstruction(
	    ControlFlowGraph<SSAInstruction, ISSABasicBlock> epcfg,
	    AnalysisEnvironment env, AsyncInvokeInstruction inst) {
	MethodReference mr = inst
		.getDeclaredTarget();
	// each async is different and cannot be invoked twice, so use a dummy pc 
	String pack = mr.getDeclaringClass().getName().toString();
	String callsite_pack = env.cur_scope.scope;
	String callsite_name = env.cur_scope.name;
	pack = getPackage(pack);
	int index = instmap.get(inst).intValue();
	int line = ((AstMethod) epcfg.getMethod()).getSourcePosition(index).getFirstLine();
	int column = ((AstMethod) epcfg.getMethod()).getSourcePosition(index).getLastCol();
	updateTable(epcfg,env,pack,"activity",line,column,callsite_pack,callsite_name,line,column,true);

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
	callerPack = getPackage(caller.getMethod().getDeclaringClass().getName().toString());
	callerName = getName(caller.getMethod().getName().toString());
	invokeLine = ((AstMethod) epcfg.getMethod()).getSourcePosition(index).getFirstLine();
	invokeCol = ((AstMethod) epcfg.getMethod()).getSourcePosition(index).getLastCol();
	
	/* the method that is possibly being called */
	CGNode callee;
	String defPack;
	String defName;
	int defLine;
	int defCol;
	
	
	CallSiteReference callsite =inst.getCallSite();
	Set<CGNode> allcallees = cg.getPossibleTargets(caller,callsite);
 	if (allcallees.size() > 0) {
	    Iterator<CGNode> it = allcallees.iterator();
	    while (it.hasNext()) {
		callee = it.next();
		defPack = getPackage(callee.getMethod().getDeclaringClass().getName().toString());
		defName = getName(callee.getMethod().getName().toString());
		defLine = ((AstMethod)callee.getMethod()).getSourcePosition().getFirstLine();
		defCol = ((AstMethod)callee.getMethod()).getSourcePosition().getLastCol();
		
		updateTable(epcfg,env,defPack,defName,defLine,defCol,
			callerPack,callerName,invokeLine,invokeCol,false);
	    }
	} else {
	    System.err.println("cannot find callee's cgnode");
	    System.err.println(inst.toString());
	    /*
	     * FIXME: broken code
	    String calleepack = getPackage(inst.getDeclaredTarget().getDeclaringClass().getName().toString());
	    String calleename = getName(inst.getDeclaredTarget().getName().toString());
	    int index = instmap.get(inst).intValue();
	    int calledline = ((AstMethod) epcfg.getMethod()).getSourcePosition(index).getFirstLine();
	    int calledcolumn = ((AstMethod) epcfg.getMethod()).getSourcePosition(index).getLastCol();
	    updateTable(epcfg,env,calleepack,calleename,defline,defcolumn,
			callerpack,calleename,calledline,calledcolumn,false);
			*/
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
	
	//System.out.println("Block "+env.cur_block);
	ISSABasicBlock curblk = (epcfg.getNode(env.cur_block));
	assert (curblk != null);
	visited.add(Integer.valueOf(env.cur_block));
	int inst_cnt = 0;
	Iterator<SSAInstruction> itt = curblk.iterator();
	 //to distiguish finish in each block 
	while (itt.hasNext()) {
	    
	    SSAInstruction inst = itt.next();
	    if (inst != null) {
		
		/* finish instruction: a finish instruction is disassembled into
		 * finish_enter and finish_exit in IR
		 */
		//System.out.println("\tinst:"+inst.toString());
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
		else{
		    updateLastInst(null,null);   
		}
	    }
	    inst_cnt++;
	}
    }
    

    /**mr
     * A Depth-First Search of a Control Flow Graph
     * 
     * @param epcfg
     * @param env
     */
    private void parseBlock(
	    ControlFlowGraph<SSAInstruction, ISSABasicBlock> epcfg,
	    AnalysisEnvironment env) {
	visitAllInstructions(epcfg, env);

	// handle if-else: for each finish block, during the anlaysis we want
	// to keep track of whether we are in a position of a branch

	// DFS
	ISSABasicBlock curb = epcfg.getNode(env.cur_block);
	assert (curb != null);
	Iterator<ISSABasicBlock> it = epcfg.getSuccNodes(curb);
	while (it.hasNext()) {
	    ISSABasicBlock cur = it.next();
	    env.cur_block = cur.getNumber();
	    if (!visited.contains(Integer.valueOf(env.cur_block))) {
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
	//System.err.println(md.getMethod().getSignature());
	IR ir = md.getIR();
	if (ir != null) {
	    // original control flow graph
	    ControlFlowGraph<SSAInstruction, ISSABasicBlock> cfg = ir
		    .getControlFlowGraph();
	    
	    // control flow graph without exception
	    PrunedCFG<SSAInstruction, ISSABasicBlock> epcfg = MyExceptionPrunedCFG.make(cfg);
	    //ControlFlowGraph<SSAInstruction, ISSABasicBlock> epcfg = cfg;
	    if (epcfg != null) {
		String _package = 
		    ((AstMethod) epcfg.getMethod()).getReference().getDeclaringClass().getName().toString();
		String _name = epcfg.getMethod().getName().toString();
		_name = getName(_name);
		_package = getPackage(_package);
		int line = ((AstMethod) epcfg.getMethod()).debugInfo().getCodeBodyPosition().getFirstLine();
		int column = ((AstMethod) epcfg.getMethod()).debugInfo().getCodeBodyPosition().getLastCol();
		CallTableKey ctk = new CallTableMethodKey(_package,_name,line,column);
		SSAInstruction[] insts = epcfg.getInstructions();
		instmap = new HashMap<SSAInstruction,Integer>();
		for(int i=0;i<insts.length;i++){
		    instmap.put(insts[i], new Integer(i));
		}	
		AnalysisEnvironment env = new AnalysisEnvironment(ctk,nodenum,epcfg);
		visited = new HashSet<Integer>();
		calltable.put(ctk, new LinkedList<CallTableVal>());
		ISSABasicBlock root = epcfg.entry();
		assert (root != null);
		env.cur_block = root.getNumber();
		// dom is a "global" variable, this stmt will generate garbage
		// every time
		dom = new NumberedDominators<ISSABasicBlock>(epcfg, root);
		InvertedNumberedGraph<ISSABasicBlock> invert_epcfg = 
		    (InvertedNumberedGraph<ISSABasicBlock>) GraphInverter.invert(epcfg);
		assert (invert_epcfg != null);

		ISSABasicBlock invert_root = invert_epcfg.getNode(epcfg.exit()
			.getNumber());
		// calculate dominators for the inverted epcfg
		invert_dom = new NumberedDominators<ISSABasicBlock>(invert_epcfg,
			invert_root);
		HashSet<Integer> loopvisited = new HashSet<Integer>();
		loops = new HashSet<NatLoop>();
		NatLoopSolver.findAllLoops(epcfg, dom, loops, loopvisited, root);
		//printGraph(epcfg);
		if (md.getMethod().getName().toString().contains("run")){
		    //GraphUtil.printCFG(epcfg, md.getMethod().getName().toString()+"_removed");
		    //GraphUtil.printCFG(cfg, md.getMethod().getName().toString());
		}
		parseBlock(epcfg, env);
		if(last_inst!=null){
		    last_inst.aslast=ctk;
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
	Iterator<CGNode> all_methods = cg.iterator();
	while (all_methods.hasNext()) {
	    CGNode one_method = all_methods.next();
	    //System.err.println(one_method.toString());
	    String method_name = one_method.getMethod().getName().toString();
	    assert (method_name != null);
	    if (method_name.contains("fake") || method_name.contains("init")) {
		continue;
	    }

	    parseIR(cg.getNumber(one_method));

	}
    }
    
    /**
     * interface to other classes for this finish-async analysis
     * 
     * @param testedFile
     *            : file name
     * @param pack
     *            : package of this program it declares. For example, it an x10
     *            program declares itself as, package x10.array, para should be
     *            "x10/array/" (the last "/" is necessary)
     * @param entrymethod
     *            : name of the mehtod the anlaysis will start with
     * @param methodtype
     *            : signature of a method, including all arguments and return
     *            value's qualified types.
     * @throws Exception
     */
    public void compile(final File testedFile, final String pack,
	    final String entrymethod, final String methodtype) throws Exception {
	
	final String mainClass = testedFile.getName().substring(0,
		testedFile.getName().lastIndexOf('.'));
	
	final X10SourceAnalysisEngine engine = 
	    new X10SourceAnalysisEngine() {
	    
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

	engine.addX10SystemModule(new SourceDirectoryTreeModule(new File("../x10.runtime/src-x10/"), "10"));
	engine.addX10SourceModule(new SourceDirectoryTreeModule(new File("../x10.tests/examples/x10lib/")));
	System.err.println(testedFile.getName());
	engine.addX10SourceModule(new SourceFileModule(testedFile, testedFile.getName()));
	
	
	boolean ifSaved = false;
	boolean ifExpanded = true;
	boolean ifStat = false;
	boolean ifDump = true;
	boolean[] mask = {true,true,true};
	// build the call graph: ExplicitCallGraph
	cg = engine.buildDefaultCallGraph();
	
	//GraphUtil.printNumberedGraph(cg, "test");
	System.out.println("Baolin here again --- Call Graph");
	buildCallTable();
	if(ifStat){
	    System.out.println("Intitial Table:");
	    CallTableUtil.getStat(calltable);
	}
	if(ifDump){
	    CallTableUtil.dumpCallTable(calltable);
	}
	if(ifSaved){
	    System.out.println("saving ... ...");
	    CallTableUtil.saveCallTable("calltable.dat", calltable);
	}
	if(ifExpanded){
	    System.out.println("Expanding Talbe:");
	    CallTableUtil.expandCallTable(calltable, mask);
	    //CallTableUtil.updateAllArity(calltable);
	    //CallTableUtil.expandCallTable(calltable, mask);	
	}
	if(ifStat && ifExpanded){
	    
	    CallTableUtil.getStat(calltable);
	}
	if(ifDump && ifExpanded){
	    System.out.println("New Talbe:");
	    CallTableUtil.dumpCallTable(calltable);
	}
	System.out.println("finished ... ");
    } // end of compile
    private String getPackage(String s){
	if(s.contains("activity")){
	    int start = s.indexOf(':');
	    int end = s.indexOf(".x10:");
	    s = s.substring(start+2,end);
	    s = s.replace('/', '.');
	    //System.err.println(s);
	    return s;
	}
	s = s.substring(1);
	s = s.replace('/','.');
	return s;
    }
    private String getName(String s){
	if(s.contains("activity")){
	    return "activity";
	}
	return s;
    }
    private void updateLastInst(CallTableKey k, CallTableVal v){
	if(last_inst!=null)
	    last_inst.aslast = null;
	last_inst = v;
	if(last_inst!=null)
	    last_inst.aslast = k;
    }
} // end of class


/*
 * This class is used to carry "path independent" information during the
 * analysis. 
 * */
class AnalysisEnvironment implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// hold nested finish
	Stack<CallTableKey> uncompleted_scope;
	// current finish
	CallTableKey cur_scope;
	// block is being processed
	int cur_block;
	// the method calltalbe's active entry represents
	int cur_method_node;
	public AnalysisEnvironment(CallTableKey n, int nd,
		PrunedCFG<SSAInstruction, ISSABasicBlock> epcfg) {
		uncompleted_scope = new Stack<CallTableKey>();
		cur_scope = n;
		cur_block = 0;;
		cur_method_node = nd;
	}
}
