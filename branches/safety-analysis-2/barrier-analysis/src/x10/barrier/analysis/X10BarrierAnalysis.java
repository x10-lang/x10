package x10.barrier.analysis;
import java.io.ByteArrayInputStream;

import java.io.ByteArrayOutputStream;
import java.io.Console;
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
import java.util.List;
import java.util.Set;
import java.util.Stack;

import x10.barrier.util.MyExceptionPrunedCFG;



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
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.LocalPointerKey;
import com.ibm.wala.ipa.callgraph.propagation.NormalAllocationInNode;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.ipa.callgraph.propagation.PointerKey;
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
public class X10BarrierAnalysis {
  
    CallGraph cg;
    PointerAnalysis pa;

    
private Set retrieveClocks(int nodenum) {
	
    	Set clocks = new HashSet();
	CGNode md = cg.getNode(nodenum);
	IR ir = md.getIR();
	if (ir != null) {
	    SSAInstruction[] insts = ir.getInstructions();
	    for (SSAInstruction inst: insts) {
		//System.out.println(inst);
		if (inst != null && inst.toString().contains("new <X10Source,Lx10/lang/Clock>")) {
		    OrdinalSet<InstanceKey> os = pa
			.getPointsToSet(new LocalPointerKey(cg.getNode(nodenum)
					, inst.getDef()));
			Iterator<InstanceKey> it = os.iterator();
			
			while (it.hasNext()) {
				InstanceKey ik = it.next();
				if (ik.getConcreteType().toString().contains(
				"Clock")) {
					clocks.add(ik);
				
				}
			}
		}
	    }
	    
	}
	return clocks;
}

   private boolean isAsyncClocked (AsyncInvokeInstruction async, NormalAllocationInNode clk, CGNode md) {
 
       int clocks[] = async.getClocks();
	//System.out.println("*****" + clocks.length);
	//System.out.println(((AsyncInvokeInstruction)inst).);
	for (int count = 0; count < clocks.length; count++) {
		//System.out.println(clocks[count]);
		OrdinalSet<InstanceKey> os = pa
		.getPointsToSet(new LocalPointerKey(md, clocks[count]));
		Iterator<InstanceKey> it = os.iterator();
		while (it.hasNext()) {
			InstanceKey ik = it.next();
			if (ik.equals(clk)) {
			    if (os.size() == 1)
				return true;
			    return false;
			}
		}
	}
	
       return false;
   }
   
   private boolean isMethodClocked(SSAInstruction inst, NormalAllocationInNode clk,  CGNode md) {
       if (inst != null && inst.toString().contains("new <X10Source,Lx10/lang/Clock>")) {
   
	   OrdinalSet<InstanceKey> os = pa.getPointsToSet(new LocalPointerKey(md
			, inst.getDef()));
	   Iterator<InstanceKey> it = os.iterator();
	
	   while (it.hasNext()) {
	       InstanceKey ik = it.next();
		if (ik.equals(clk)) {
		    return true;
		}
	   }
	       
	 }
       return false;
       
   }

   
   private void printInstructions (int nodenum) {
       
       CGNode md = cg.getNode(nodenum);
       IR ir = md.getIR();
       if (ir == null)
	   return;
	String funName =  md.getMethod().getName().toString();
	//System.err.println(md.getMethod().getSignature());
	
	if (funName.contains("activity file")) {
	    int index =  funName.lastIndexOf("x10");
	    funName = "<async" + funName.substring(index + 3);
	}
	System.out.println("-----------" + funName +  "------------");
       for (int j = 0; j < ir.getInstructions().length; j++) {
	    	System.out.println(j + ": " + ir.getInstructions()[j]);
	    }
   }
   
    private Automaton parseIR(int nodenum, NormalAllocationInNode clk, boolean amIClocked) {
	
	Automaton a = new Automaton();
	CGNode md = cg.getNode(nodenum);

	String funName =  md.getMethod().getName().toString();
	//System.err.println(md.getMethod().getSignature());
	IR ir = md.getIR();
	
	if (funName.contains("activity file")) {
	    int index =  funName.lastIndexOf("x10");
	    funName = "<async" + funName.substring(index + 3);
	}
	this.printInstructions(nodenum);
	
	if (ir != null) {
	   
	    // original control flow graph
	    ControlFlowGraph<SSAInstruction, ISSABasicBlock> cfg = ir
		    .getControlFlowGraph();
	    AstMethod method = (AstMethod)cfg.getMethod();
	    
	    // control flow graph without exception
	    PrunedCFG<SSAInstruction, ISSABasicBlock> epcfg = MyExceptionPrunedCFG.make(cfg);
	    //ControlFlowGraph<SSAInstruction, ISSABasicBlock> epcfg = cfg;
	    if (epcfg != null) {
			//System.out.println(epcfg);
		
		
		State [] s = new State [epcfg.getMaxNumber() + 1];
		
		for (int i = 0; i <=  epcfg.getMaxNumber(); i++) {
		    ISSABasicBlock node = epcfg.getNode(i);
		    if (node == null)
			continue;
		    int startIndex = node.getFirstInstructionIndex();
		    s[i] = new State(startIndex, -1, funName, amIClocked, method);
		}
		for (int i = 0; i <= epcfg.getMaxNumber(); i++) {
		        
		    	//printBlockInfo(epcfg.getNode(i));
		    	ISSABasicBlock node = epcfg.getNode(i);
		    	if (node == null)
		    	    continue;
		    	//s[i].set (node.getFirstInstructionIndex(), node.getLastInstructionIndex());
		    	State incomingState = s[i];
		    	State currState = s[i];
		    	int startIndex = node.getFirstInstructionIndex();

		   
		    	
		    	for (int j = node.getFirstInstructionIndex(); j <= node.getLastInstructionIndex() && j >= 0 ; j++) {
		    	 SSAInstruction currInst = epcfg.getInstructions()[j];
		    	   if (isMethodClocked(currInst, clk, md))
		    	       amIClocked = true;
		    	 
		    	    if (currInst != null && currInst instanceof  SSANextInstruction && amIClocked) {
		    		currState = new State(j, j, funName, amIClocked, method);

		    		Edge e = new Edge(incomingState, currState, Edge.NEXT);
		    		incomingState = currState; 
		
		    	    }
		    	    else if (currInst != null && currInst instanceof  AsyncInvokeInstruction) {
		    		AsyncInvokeInstruction asyncInst = (AsyncInvokeInstruction) currInst;
		    		boolean isAsyncClocked =  isAsyncClocked(asyncInst, clk, md);
		    		CallSiteReference asyncSite = asyncInst.getCallSite();
		    		Set<CGNode> asyncNodes = cg.getNodes(asyncSite.getDeclaredTarget());
		    		for (CGNode asyncNode: asyncNodes) {
		    		    Automaton asyncAutomaton = this.parseIR(asyncNode.getGraphNodeId(), clk, isAsyncClocked);
		    		    State asyncState = new State (j, -1, funName, amIClocked, method);
		    		    new Edge (incomingState, asyncState, Edge.ASYNC);
		    		 
		    		    currState = new State(j, j, funName, amIClocked, method);
		    		    new Edge(asyncState, currState, Edge.PAR);
		    		    new Edge (asyncState, asyncAutomaton.root, Edge.PAR);
		    		    incomingState = currState; 
		    		
		    		}
		    	    } else {
		    		currState = new State(j, j, funName, amIClocked, method);
		    		Edge e = new Edge(incomingState, currState, Edge.COND);
		    		incomingState = currState; 
		    		
		    	    }
		    	} 
		   
		    	
		    	for ( Iterator it = epcfg.getSuccNodes(epcfg.getNode(i)); it.hasNext(); ) {
		    	     ISSABasicBlock nbr = (ISSABasicBlock) it.next();
		    	     int nbrIndex = nbr.getNumber();
		    	     Edge e = new Edge(incomingState, s[nbrIndex], Edge.COND);
		
		    	}
		}
		a.setRoot(s[epcfg.entry().getNumber()]);
		s[epcfg.entry().getNumber()].isStart = true;
		s[epcfg.exit().getNumber()].isTerminal = true;
		s[epcfg.exit().getNumber()].set(-2, -1);
		if (funName.contains("<async") && (amIClocked)) {
		    State terminal = new State(-3, -1, funName, true, method);
		    s[epcfg.exit().getNumber()].isTerminal = false;
		    terminal.isTerminal = true;
		    new Edge(  s[epcfg.exit().getNumber()], terminal, Edge.NEXT); // add a next edge at the end of async
		  
		    
		}
		
		
		
	    }// end of (epcfg!=null)
	}// end of (ir!=null)
	return a;

    }

    /**
     * "entry point of the real analysis. It analyzes each method in the
     * callgraph of the input program. but it omits some "dummy" method, e.g.
     * "fake..." or "init..."
     */
    private void buildAutomata() {
	Iterator<CGNode> all_methods = cg.iterator();
	while (all_methods.hasNext()) {
	    CGNode one_method = all_methods.next();
	    //System.err.println(one_method.toString());
	    String method_name = one_method.getMethod().getName().toString();

	    assert (method_name != null);
	    if (method_name.contains("fake") || method_name.contains("init") || method_name.contains("make")) {
		continue;
	    }
	    //if (one_method.toString().contains("x10/lang"))
		//continue;
	    String declaringClass = one_method.getMethod().getDeclaringClass().toString();
	    if (declaringClass.contains("x10/lang") || declaringClass.contains("x10/util") ||  declaringClass.contains("x10/compiler"))
		continue;
	    Set clocks = this.retrieveClocks(cg.getNumber(one_method));
	    
	    for (Object o : clocks){
		
		NormalAllocationInNode clk = (NormalAllocationInNode) o;
		System.out.println("\n\n++++++++++++++++++" + clk.getSite() +"+++++++++++++++++++++++++++");
		Automaton a = parseIR(cg.getNumber(one_method), clk, true);
		   a.compress();
		   System.out.println("Automaton Compressed Successfully"); 
		   //a.print();
		    a.composePar();
		    System.out.println("Automata Composed Successfully");
		    //a.print();
		    a.mayHappenInParallel();
	    }
	  
	 
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
	
	long start1, start2, end1, end2;
	
	
     
	
	
	start1 = System.currentTimeMillis();

	engine.addX10SystemModule(new SourceDirectoryTreeModule(new File("../x10.runtime/src-x10/"), "10"));
	//engine.addX10SourceModule(new SourceDirectoryTreeModule(new File("../x10.tests/examples/x10lib/")));
	//System.err.println(testedFile.getName());
	engine.addX10SourceModule(new SourceFileModule(testedFile, testedFile.getName()));
	
	
	
	// build the call graph: ExplicitCallGraph
	cg = engine.buildDefaultCallGraph();
	pa = engine.getPointerAnalysis();
	
	end1 = System.currentTimeMillis();
	
	
	//GraphUtil.printNumberedGraph(cg, "test");
	System.out.println("ANALYZING " + testedFile.getName());
	start2 = System.currentTimeMillis();
	buildAutomata();
	end2 = System.currentTimeMillis();
	
	System.out.println("finished ... ");
	System.out.println("Time to build call graph: " + (end1 - start1)/1000.0 + "s");
	System.out.println("Time to analyse: " + (end2 - start2)/1000.0 + "s");
    } // end of compile
}


