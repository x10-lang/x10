package x10.wala.util;

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
	    file_path = "/Users/blshao/Desktop/";
	} else {
	    dot_path = "/usr/bin/dot";
	    file_path = "/home/blshao/Desktop/";
	}
	String full_name = file_path+name;
	try {
	    DotUtil.dotify(g, mnd, (full_name + ".dot"), 
		    (full_name + ".pdf"),dot_path);
	} catch (WalaException e) {
	    System.out.println(e.toString());
	}
    }
  
}
    
class MyNumberedGraphDecorator<T> implements NodeDecorator {
    
    @SuppressWarnings("unchecked")
    public String getLabel(Object o) throws WalaException {
	T bb = (T) o;
	return bb.toString();
    }

}

// Decorators are for printing purposes
class MySSACFGNodeDecorator implements NodeDecorator {
   
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


