/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.wala.translator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import polyglot.main.Report;
import x10.wala.classLoader.AsyncCallSiteReference;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.util.graph.traverse.NumberedDFSDiscoverTimeIterator;

/**
 * @author Haichuan
 * Analyze the call graph from wala and identify all transformation targets
 */

public class X10WSCallGraphAnalyzer {
	public static final String WS_TOPIC = "workstealing";
	public static final void wsReport(int level, String message){
		if(Report.should_report(WS_TOPIC, level)){
			Report.report(level, message);
		}
	}
	public static final boolean wsShouldReport(int level){
		return Report.should_report(WS_TOPIC, level);
	}
	static{
		Report.topics.add(WS_TOPIC);
		wsReport(1, "[WS_CallGraph]X10WSCallGraphAnalyzer started...");
	}
	
	protected CallGraph callGraph;
	
	public X10WSCallGraphAnalyzer(CallGraph callGraph){
		this.callGraph = callGraph;
	}
	
	/**
	 * Identify all concurrent methods, and
	 * Use DFS to traverse all methods that directly or indirectly call the concurrent methods
	 * This method doesn't use class hierarchy information.
	 * @return
	 */
	public List<String> simpleAnalyze(){
		if(callGraph == null){
			new ArrayList<String>();
		}
		
		//identify all concurrent methods;
		HashSet<CGNode> conMethodNodes = new HashSet<CGNode>();
		for(CGNode cgn : callGraph){
			if(cgn.getMethod() == null){
				continue;
			}			
			for (Iterator<CallSiteReference> sites = cgn.iterateCallSites(); sites.hasNext();) {
		        CallSiteReference site = (CallSiteReference) sites.next();
		        if(site instanceof AsyncCallSiteReference){
		        	conMethodNodes.add(cgn);
		        	break; //no need identify again;
		        }
			}
		}
		
		//now build a reverse DFS iterator;
		Iterator<CGNode> dfsI = new NumberedDFSDiscoverTimeIterator<CGNode>(callGraph, conMethodNodes.iterator()){

			private static final long serialVersionUID = -8848061109177832957L;

			protected Iterator<? extends CGNode> getConnected(CGNode n) {
                return callGraph.getPredNodes(n); //reverse traverse;
            }
		};
		
		//now add all reachable nodes, but filter out fake root and async activity
		HashSet<CGNode> targetMethodNodes = new HashSet<CGNode>();
		while(dfsI.hasNext()){
			CGNode cgn = dfsI.next();
			if(cgn != callGraph.getFakeRootNode()
					&& cgn.getMethod() != null
					&& !cgn.getMethod().getSignature().startsWith("A<activity")){
				targetMethodNodes.add(cgn);
			}
		}
		
		//get the signature and store into the result
		ArrayList<String> result = new ArrayList<String>();
		wsReport(3, "[WS_CallGraph]Found " + conMethodNodes.size() + " concurrent methods.");
		wsReport(3, "[WS_CallGraph]Found " + targetMethodNodes.size() + " target methods.");
		for(CGNode cgn : targetMethodNodes){
			String type = conMethodNodes.contains(cgn) ? "[C]" : "[D]";
			String signature = type + cgn.getMethod().getSignature();
			wsReport(5, signature);
			result.add(signature);
		}
		return result;
	}
}
