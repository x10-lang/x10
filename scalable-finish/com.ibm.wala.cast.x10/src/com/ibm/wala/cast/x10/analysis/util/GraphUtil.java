package com.ibm.wala.cast.x10.analysis.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;

import x10.finish.table.CallTableKey;
import x10.finish.table.CallTableMethodVal;
import x10.finish.table.CallTableVal;
import x10.finish.table.OutputUtil;

import com.ibm.wala.cfg.ControlFlowGraph;
import com.ibm.wala.ipa.callgraph.impl.ExplicitCallGraph.ExplicitNode;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.util.collections.Iterator2Collection;
import com.ibm.wala.util.debug.Assertions;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.util.graph.NumberedGraph;
import com.ibm.wala.util.graph.impl.SparseNumberedGraph;
import com.ibm.wala.util.graph.labeled.LabeledGraph;
import com.ibm.wala.util.warnings.WalaException;
import com.ibm.wala.viz.DotUtil;
import com.ibm.wala.viz.NodeDecorator;
import com.ibm.wala.viz.DotUtil.DotOutputType;

public class GraphUtil {
    
	public static CommunicationGraph Table2Graph(HashMap<CallTableKey, LinkedList<CallTableVal>> calltable){
		CommunicationGraph cg = new CommunicationGraph(new CommunicationLabel());
		Set<CallTableKey> keyset;
		Iterator<CallTableKey> keyit;
		keyset = calltable.keySet();
	    keyit = keyset.iterator();
	    while (keyit.hasNext()) {
	    	CallTableKey key = keyit.next();
	    	CommunicationNode src = new CommunicationNode(key);
	    	cg.addNode(src);
	    	LinkedList<CallTableVal> vals = calltable.get(key);
	    	LinkedList<CallTableVal> new_vals = (LinkedList<CallTableVal>) OutputUtil.copy(vals);
	    	for (int i = 0; i < vals.size(); i++) {
	    		CallTableVal callee = vals.get(i);
	    		CommunicationNode dst = new CommunicationNode(callee);
	    		cg.addNode(dst);
	    		CommunicationLabel l = null;
	    		if(callee instanceof CallTableMethodVal){
	    			l = new CommunicationLabel(callee.a,((CallTableMethodVal) callee).cs, ((CallTableMethodVal) callee).isLocal,callee.isLast);
	    		}else{
	    			l = new CommunicationLabel(callee.a,null,callee.isLocal,callee.isLast);
	    		}
	    		cg.addEdge(src, dst,l);
	    	}
	    }
		return cg;
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
	    MyDotUtil.dotify(g, mnd, (full_name + ".dot"), 
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

class MyDotUtil {

	  /**
	   * possible output formats for dot
	   * 
	   */
	  public static enum DotOutputType {
	    PS, SVG, PDF, EPS
	  }

	  private static DotOutputType outputType = DotOutputType.PDF;
	  
	  private static int fontSize = 6;
	  private static String fontColor = "black";
	  private static String fontName = "Arial";

	  public static void setOutputType(DotOutputType outType) {
	    outputType = outType;
	  }

	  public static DotOutputType getOutputType() {
	    return outputType;
	  }

	  private static String outputTypeCmdLineParam() {
	    switch (outputType) {
	    case PS:
	      return "-Tps";
	    case EPS:
	      return "-Teps";
	    case SVG:
	      return "-Tsvg";
	    case PDF:
	      return "-Tpdf";
	    default:
	      Assertions.UNREACHABLE();
	      return null;
	    }
	  }

	  /**
	   * Some versions of dot appear to croak on long labels. Reduce this if so.
	   */
	  private final static int MAX_LABEL_LENGTH = Integer.MAX_VALUE;


	  /**
	   */
	  public static <T> void dotify(Graph<T> g, NodeDecorator labels, String dotFile, String outputFile, String dotExe)
	    throws WalaException {
	    dotify(g, labels, null, dotFile, outputFile, dotExe);
	  }

	  public static <T> void dotify(Graph<T> g, NodeDecorator labels, String title, String dotFile, String outputFile, String dotExe)
	      throws WalaException {
	    if (g == null) {
	      throw new IllegalArgumentException("g is null");
	    }
	    File f = MyDotUtil.writeDotFile(g, labels, title, dotFile);
	    spawnDot(dotExe, outputFile, f);
	  }

	  public static void spawnDot(String dotExe, String outputFile, File dotFile) throws WalaException {
	    if (dotFile == null) {
	      throw new IllegalArgumentException("dotFile is null");
	    }
	    String[] cmdarray = { dotExe, outputTypeCmdLineParam(), "-o", outputFile, "-v", dotFile.getAbsolutePath() };
	    System.out.println("spawning process " + Arrays.toString(cmdarray));
	    BufferedInputStream output = null;
	    BufferedInputStream error = null;
	    try {
	      Process p = Runtime.getRuntime().exec(cmdarray);
	      output = new BufferedInputStream(p.getInputStream());
	      error = new BufferedInputStream(p.getErrorStream());
	      boolean repeat = true;
	      while (repeat) {
	        try {
	          Thread.sleep(500);
	        } catch (InterruptedException e1) {
	          e1.printStackTrace();
	          // just ignore and continue
	        }
	        if (output.available() > 0) {
	          byte[] data = new byte[output.available()];
	          int nRead = output.read(data);
	          System.err.println("read " + nRead + " bytes from output stream");
	        }
	        if (error.available() > 0) {
	          byte[] data = new byte[error.available()];
	          int nRead = error.read(data);
	          System.err.println("read " + nRead + " bytes from error stream");
	        }
	        try {
	          p.exitValue();
	          // if we get here, the process has terminated
	          repeat = false;
	          System.out.println("process terminated with exit code " + p.exitValue());
	        } catch (IllegalThreadStateException e) {
	          // this means the process has not yet terminated.
	          repeat = true;
	        }
	      }
	    } catch (IOException e) {
	      e.printStackTrace();
	      throw new WalaException("IOException in " + MyDotUtil.class);
	    } finally {
	      if (output != null) {
	        try {
	          output.close();
	        } catch (IOException e) {
	          e.printStackTrace();
	        }
	      }
	      if (error != null) {
	        try {
	          error.close();
	        } catch (IOException e) {
	          e.printStackTrace();
	        }
	      }
	    }
	  }

	  public static <T> File writeDotFile(Graph<T> g, NodeDecorator labels, String title, String dotfile) throws WalaException {

	    if (g == null) {
	      throw new IllegalArgumentException("g is null");
	    }
	    StringBuffer dotStringBuffer = dotOutput(g, labels, title);

	    // retrieve the filename parameter to this component, a String
	    if (dotfile == null) {
	      throw new WalaException("internal error: null filename parameter");
	    }
	    try {
	      File f = new File(dotfile);
	      FileWriter fw = new FileWriter(f);
	      fw.write(dotStringBuffer.toString());
	      fw.close();
	      return f;

	    } catch (Exception e) {
	      throw new WalaException("Error writing dot file " + dotfile);
	    }
	  }

	  /**
	   * @return StringBuffer holding dot output representing G
	   * @throws WalaException
	   */
	  private static <T> StringBuffer dotOutput(Graph<T> g, NodeDecorator labels, String title) throws WalaException {
	    StringBuffer result = new StringBuffer("digraph \"DirectedGraph\" {\n");

	    if (title != null) {
	      result.append("graph [label = \""+title+"\", labelloc=t, concentrate = true];");
	    } else {
	      result.append("graph [concentrate = true];");
	    }
	    
	    String rankdir = getRankDir();
	    if (rankdir != null) {
	      result.append("rankdir=" + rankdir + ";");
	    }
	    String fontsizeStr = "fontsize=" + fontSize;
	    String fontcolorStr = (fontColor != null) ? ",fontcolor="+fontColor : "";
	    String fontnameStr = (fontName != null) ? ",fontname="+fontName : "";
	         
	    result.append("center=true;");
	    result.append(fontsizeStr);
	    result.append(";node [ color=blue,shape=\"box\"");
	    result.append(fontsizeStr);
	    result.append(fontcolorStr);
	    result.append(fontnameStr);
	    result.append("];edge [ color=black,");
	    result.append(fontsizeStr);
	    result.append(fontcolorStr);
	    result.append(fontnameStr);
	    result.append("]; \n");

	    Collection dotNodes = computeDotNodes(g);

	    outputNodes(labels, result, dotNodes);

	    for (Iterator<? extends T> it = g.iterator(); it.hasNext();) {
	      T n = it.next();
	      for (Iterator<? extends T> it2 = g.getSuccNodes(n); it2.hasNext();) {
	        T s = it2.next();
	        if(g instanceof LabeledGraph){
	        	Set labelSet = ((LabeledGraph)g).getEdgeLabels(n, s);
	        	Iterator iterator = labelSet.iterator();
	        	while(iterator.hasNext()){
	        		result.append(" ");
	    	        result.append(getPort(n, labels));
	    	        result.append(" -> ");
	    	        result.append(getPort(s, labels));
	    	        result.append(" [label=\""+iterator.next().toString()+"\" ]\n");
	        	}
	        	continue;
	        }
	        result.append(" ");
	        result.append(getPort(n, labels));
	        result.append(" -> ");
	        result.append(getPort(s, labels));
	        result.append(" \n");
	      }
	    }

	    result.append("\n}");
	    return result;
	  }

	  private static void outputNodes(NodeDecorator labels, StringBuffer result, Collection dotNodes) throws WalaException {
	    for (Iterator it = dotNodes.iterator(); it.hasNext();) {
	      outputNode(labels, result, it.next());
	    }
	  }

	  private static void outputNode(NodeDecorator labels, StringBuffer result, Object n) throws WalaException {
	    result.append("   ");
	    result.append("\"");
	    result.append(getLabel(n, labels));
	    result.append("\"");
	    result.append(decorateNode(n, labels));
	  }

	  /**
	   * Compute the nodes to visualize
	   */
	  private static <T> Collection<T> computeDotNodes(Graph<T> g) throws WalaException {
	    return Iterator2Collection.toSet(g.iterator());
	  }

	  private static String getRankDir() throws WalaException {
	    return null;
	  }

	  /**
	   * @param n node to decorate
	   * @param d decorating master
	   */
	  private static String decorateNode(Object n, NodeDecorator d) throws WalaException {
	    StringBuffer result = new StringBuffer();
	    result.append(" [  ]\n");
	    return result.toString();
	  }

	  private static String getLabel(Object o, NodeDecorator d) throws WalaException {
	    String result = null;
	    if (d == null) {
	      result = o.toString();
	    } else {
	      result = d.getLabel(o);
	      result = result == null ? o.toString() : result;
	    }
	    if (result.length() >= MAX_LABEL_LENGTH) {
	      result = result.substring(0, MAX_LABEL_LENGTH - 3) + "...";
	    }
	    return result;
	  }

	  private static String getPort(Object o, NodeDecorator d) throws WalaException {
	    return "\"" + getLabel(o, d) + "\"";

	  }

	  public static int getFontSize() {
	    return fontSize;
	  }

	  public static void setFontSize(int fontSize) {
	    MyDotUtil.fontSize = fontSize;
	  }
}




