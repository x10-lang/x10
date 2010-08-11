package x10.barrier.util;

import java.util.HashMap;

import java.util.Iterator;
import java.util.Stack;

import com.ibm.wala.cfg.ControlFlowGraph;
import com.ibm.wala.ipa.callgraph.impl.ExplicitCallGraph.ExplicitNode;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.util.graph.NumberedGraph;
import com.ibm.wala.util.graph.impl.SparseNumberedGraph;
import com.ibm.wala.util.warnings.WalaException;
import com.ibm.wala.viz.DotUtil;
import com.ibm.wala.viz.NodeDecorator;

public class GraphUtil {
    
    
    public static void printCFG(
	    ControlFlowGraph<SSAInstruction, ISSABasicBlock> epcfg, String name) {
	String os = System.getProperty("os.name");
	String dot_path = "";
	 
	MySSACFGNodeDecorator mnd = new MySSACFGNodeDecorator();
	if (!os.contains("Linux")) {
	    dot_path = "/Applications/Graphviz.app/Contents/MacOS/dot";
	    
	} else {
	    dot_path = "/usr/bin/dot";
	     
	}
	 
	try {
	    DotUtil.dotify(epcfg, mnd, (name + ".dot"), 
		    (name + ".pdf"),dot_path);
	} catch (WalaException e) {
	    System.out.println(e.toString());
	}
    }

    public static void printNumberedGraph(NumberedGraph g, String name){
	String os = System.getProperty("os.name");
	String dot_path = "";
	String file_path = "";
	MyNumberedGraphDecorator mnd = new MyNumberedGraphDecorator();
	if (!os.contains("Linux")) {
	    dot_path = "/Applications/Graphviz.app/Contents/MacOS/dot";
	    file_path = "/tmp/";
	} else {
	    dot_path = "/usr/bin/dot";
	    file_path = "/tmp/";
	}
	String full_name = file_path+name;
	try {
	    DotUtil.dotify(g, mnd, (full_name + ".dot"), 
		    (full_name + ".pdf"),dot_path);
	} catch (WalaException e) {
	    System.out.println(e.toString());
	}
    }
    /**
     * connect g1's connection node with g2's root
     * @param g1
     * @param g2
     * @param connection
     */
/*    public static void connectGraphs(SparseNumberedGraph<FinishAsyncNode> g1,
	    SparseNumberedGraph<FinishAsyncNode> g2, int connection){
	HashMap<FinishAsyncNode,FinishAsyncNode> m = 
	    new HashMap<FinishAsyncNode,FinishAsyncNode>();
	
	Iterator<FinishAsyncNode> it = g2.iterator();
	while(it.hasNext()){
	    FinishAsyncNode n2 = it.next();
	    FinishAsyncNode copyn2 = new FinishAsyncNode(n2);
	    g1.addNode(copyn2);
	    m.put(n2, copyn2);
	}
	
	Stack<FinishAsyncNode> st = new Stack<FinishAsyncNode>();
	st.add(g2.getNode(0));
	while(!st.isEmpty()){
	    FinishAsyncNode n = st.pop();
	    FinishAsyncNode copyn = m.get(n);
	    Iterator<FinishAsyncNode> it2 = g2.getSuccNodes(n);
	    while(it2.hasNext()){
		FinishAsyncNode m1 = it2.next();
		FinishAsyncNode copym1 = m.get(m1);
		g1.addEdge(copyn,copym1);		
		st.push(m1);
	    }
	}
	g1.addEdge(g1.getNode(connection), m.get(g2.getNode(0)));
    }*/
}
    
class MyNumberedGraphDecorator<T> implements NodeDecorator {
    /*
     * private NumberedGraph<T> g;
     * 
     * public MyNumberedGraphDecorator(NumberedGraph<T> g) { this.g = g; }
     */

    @SuppressWarnings("unchecked")
    @Override
    public String getLabel(Object o) throws WalaException {
	T bb = (T) o;
	return bb.toString();
    }

}

// Decorators are for printing purposes
class MySSACFGNodeDecorator implements NodeDecorator {
    /*
     * private ControlFlowGraph<SSAInstruction, ISSABasicBlock> epcfg;
     * 
     * public MySSACFGNodeDecorator( ControlFlowGraph<SSAInstruction,
     * ISSABasicBlock> g) { epcfg = g; }
     */
    public String getLabel(Object o) throws WalaException {
	ISSABasicBlock bb = (ISSABasicBlock) o;
	StringBuffer insts = new StringBuffer();
	Iterator<SSAInstruction> itt = bb.iterator();
	while (itt.hasNext()) {
	    SSAInstruction inst = itt.next();
	    insts.append(inst.toString());
	}
	return ("Block(" + bb.getNumber() + ") " + insts.toString() + "   ");
    }
}

class MyCGNodeDecorator implements NodeDecorator {
    public String getLabel(Object o) throws WalaException {
	ExplicitNode bb = (ExplicitNode) o;
	return bb.toString() + "   ";
    }
}


