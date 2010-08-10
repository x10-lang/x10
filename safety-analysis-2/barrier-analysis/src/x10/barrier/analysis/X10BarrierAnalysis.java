package x10.barrier.analysis;
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
import java.util.List;
import java.util.Set;
import java.util.Stack;

import x10.finish.util.MyExceptionPrunedCFG;



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
public class X10BarrierAnalysis {
  
    CallGraph cg;
    
   
    /**mr
     * A Depth-First Search of a Control Flow Graph
     * 
     * @param epcfg
     * @param env
     */
    
    
    private Automaton parseIR(int nodenum) {
	
	Automaton a = new Automaton();
	CGNode md = cg.getNode(nodenum);

	String funName =  md.getMethod().getName().toString();
	//System.err.println(md.getMethod().getSignature());
	IR ir = md.getIR();
	
	if (funName.contains("activity file")) {
	    int index =  funName.lastIndexOf("x10");
	    funName = "<async" + funName.substring(index + 3);
	}
	System.out.println("-----------" + funName +  "------------");
	
	
	if (ir != null) {
	    for (int j = 0; j < ir.getInstructions().length; j++) {
	    	System.out.println(j + ": " + ir.getInstructions()[j]);
	    }
	    // original control flow graph
	    ControlFlowGraph<SSAInstruction, ISSABasicBlock> cfg = ir
		    .getControlFlowGraph();
	    
	    // control flow graph without exception
	    PrunedCFG<SSAInstruction, ISSABasicBlock> epcfg = MyExceptionPrunedCFG.make(cfg);
	    //ControlFlowGraph<SSAInstruction, ISSABasicBlock> epcfg = cfg;
	    if (epcfg != null) {
			//System.out.println(epcfg);
		
		State [] s = new State [epcfg.getMaxNumber() + 1];
		
		for (int i = 0; i <=  epcfg.getMaxNumber(); i++) {
		    ISSABasicBlock node = epcfg.getNode(i);
		    int startIndex = node.getFirstInstructionIndex();
		    s[i] = new State(startIndex, -1, funName);
		}
		for (int i = 0; i <= epcfg.getMaxNumber(); i++) {
		        
		    	//printBlockInfo(epcfg.getNode(i));
		    	ISSABasicBlock node = epcfg.getNode(i);
		    	//s[i].set (node.getFirstInstructionIndex(), node.getLastInstructionIndex());
		    	State incomingState = s[i];
		    	State currState = s[i];
		    	int startIndex = node.getFirstInstructionIndex();

		   
		    	
		    	for (int j = node.getFirstInstructionIndex(); j <= node.getLastInstructionIndex() && j >= 0 ; j++) {
		    	 SSAInstruction currInst = epcfg.getInstructions()[j];
		    	
		    	    if (currInst != null && currInst instanceof  SSANextInstruction) {
		    		currState = new State(j, j, funName);

		    		Edge e = new Edge(incomingState, currState, Edge.NEXT);
		    		incomingState = currState; 
		    	
		    	    } else if (currInst != null && currInst instanceof  AsyncInvokeInstruction) {
		    		AsyncInvokeInstruction asyncInst = (AsyncInvokeInstruction) currInst;
		    		CallSiteReference asyncSite = asyncInst.getCallSite();
		    		Set<CGNode> asyncNodes = cg.getNodes(asyncSite.getDeclaredTarget());
		    		for (CGNode asyncNode: asyncNodes) {
		    		    Automaton asyncAutomaton = this.parseIR(asyncNode.getGraphNodeId());
		    		    Edge e = new Edge (incomingState, asyncAutomaton.root, Edge.PAR);
		    		    currState = new State(j, j, funName);
		    		    e = new Edge(incomingState, currState, Edge.PAR);
		    		    incomingState = currState; 
		    		
		    		}
		    	    } else {
		    		currState = new State(j, j, funName);
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

		
		       // System.out.println("Neighbors:");
		    
		
		
	    }// end of (epcfg!=null)
	}// end of (ir!=null)
	return a;

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
	    if (method_name.contains("fake") || method_name.contains("init") || method_name.contains("make")) {
		continue;
	    }
	    //if (one_method.toString().contains("x10/lang"))
		//continue;
	    String declaringClass = one_method.getMethod().getDeclaringClass().toString();
	    if (declaringClass.contains("x10/lang") || declaringClass.contains("x10/util") ||  declaringClass.contains("x10/compiler"))
		continue;
	    Automaton a = parseIR(cg.getNumber(one_method));
	    a.compress();
	    //a.print();
	    a.composePar();
	    a.print();
	    a.mayHappenInParallel();
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
	//engine.addX10SourceModule(new SourceDirectoryTreeModule(new File("../x10.tests/examples/x10lib/")));
	//System.err.println(testedFile.getName());
	engine.addX10SourceModule(new SourceFileModule(testedFile, testedFile.getName()));
	
	
	boolean ifSaved = false;
	boolean ifExpanded = true;
	boolean ifStat = false;
	boolean ifDump = true;
	boolean[] mask = {true,true,true};
	// build the call graph: ExplicitCallGraph
	cg = engine.buildDefaultCallGraph();
	
	//GraphUtil.printNumberedGraph(cg, "test");
	buildCallTable();
	
	System.out.println("finished ... ");
    } // end of compile
}


