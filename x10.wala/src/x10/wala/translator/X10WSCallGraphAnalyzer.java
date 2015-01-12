/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.wala.translator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import polyglot.main.Report;
import x10.compiler.ws.util.WSTransformationContent;
import x10.compiler.ws.util.WSTransformationContent.MethodAttribute;
import x10.wala.classLoader.AsyncCallSiteReference;

import com.ibm.wala.cast.loader.AstMethod;
import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.impl.Everywhere;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.Selector;
import com.ibm.wala.util.graph.traverse.NumberedDFSDiscoverTimeIterator;

/**
 * @author Haichuan
 * Analyze the call graph from wala and identify all transformation targets
 */

public class X10WSCallGraphAnalyzer {
	public static final void wsReport(int level, String message){
		if(Report.workstealing >= level){
			Report.reporter.report(level, message);
		}
	}
	public static final boolean wsShouldReport(int level){
		return Report.workstealing >= level;
	}
	static{
// TODO: stop using static Report
// Report is being moved to Reporter to avoid Report's static data
// Report.topics.add just adds this string to be displayed in the
// help text. Skipping the following until this is resolved.
//		Report.topics.add(WS_TOPIC);
		wsReport(1, "[WS_CallGraph]X10WSCallGraphAnalyzer started...");
	}
	
	protected CallGraph callGraph;
	protected IClassHierarchy classHC;
	
	public X10WSCallGraphAnalyzer(CallGraph callGraph){
		this.callGraph = callGraph;
		classHC = callGraph.getClassHierarchy();
	}
	
	/**
	 * Identify all concurrent methods, and
	 * Use DFS to traverse all methods that directly or indirectly call the concurrent methods
	 * This method doesn't use class hierarchy information.
	 * @return
	 */
	public WSTransformationContent simpleAnalyze(){	

		WSTransformationContent result = new WSTransformationContent();
		
		//identify all concurrent methods by checking the async call site
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
		
		//Iterate all target node to do the following actions
		//1. Add each node's method as target method (body & def) transformation
		//2. Add each call sites to the target method as concurrent call site
		//3. Add each target method's impacted method as def transformation target
		for(CGNode targetNode : targetMethodNodes){

			AstMethod targetMethod = (AstMethod) targetNode.getMethod();
			Position pos = targetMethod.getSourcePosition();
			//DEBUG information
			//show location information
			//System.out.printf("[TargetMethod]%s\n", targetMethod);
			//System.out.printf("          url:%s; line:%d; col:%d\n",
			//		pos.getURL(), pos.getFirstLine(), pos.getFirstCol());

			//Action 1
			String info = conMethodNodes.contains(targetNode) ? "[C]" : "[D]";
			info += targetNode.getMethod();
			String url = pos.getURL().toString().substring(5); //remove "files"
			result.addConcurrentMethod(url, pos.getFirstLine(), pos.getFirstCol(),
					pos.getLastLine(), pos.getLastCol(), info);
			
			//Action 2
			for(Iterator<CGNode> srcNodesI = callGraph.getPredNodes(targetNode); srcNodesI.hasNext();){
				CGNode srcNode = srcNodesI.next();
				IMethod method = srcNode.getMethod();
				if(!(method instanceof AstMethod)){
					continue; //remove the fake root;
				}
				AstMethod srcMethod = (AstMethod) method;
				for(Iterator<CallSiteReference> callSitesI = callGraph.getPossibleSites(srcNode, targetNode);
					callSitesI.hasNext();){
					
					CallSiteReference callSite = callSitesI.next();
					int callPC = callSite.getProgramCounter();
					Position callSitePos = srcMethod.debugInfo().getInstructionPosition(callPC);
					
					//DEBUG
					//IR ir = srcNode.getIR();
					//System.out.printf("[CallSite]%s\n", callSite);
					//System.out.printf("[CallInst]%s\n", ir.getInstructions()[callPC]);
					//System.out.printf("          url:%s; line:%d; col:%d\n",
					//		callSitePos.getURL(), callSitePos.getFirstLine(), callSitePos.getFirstCol());
					result.addConcurrentCallSite(callSitePos.getURL().toString().substring(5),
							callSitePos.getFirstLine(), callSitePos.getFirstCol(),
							callSitePos.getLastLine(), callSitePos.getLastCol(), callSite.toString());
				}
			}
			
			//Action 3, still use try catch. Could remove them	
			try{
				Set<IMethod> methods = getAllImpactedMethodsInClassHierarchy(targetMethod);
				for(IMethod m : methods){
					if(!(m instanceof AstMethod)){
						System.err.println("[WALA_WS_ERR]Found one non-AST but impacted Method" + m);
						continue;
					}
					Position mPos = ((AstMethod)m).getSourcePosition();
					MethodAttribute ma = result.addImpactedMethod(mPos.getURL().toString().substring(5), 
							mPos.getFirstLine(), mPos.getFirstCol(),
							mPos.getLastLine(), mPos.getLastCol(), "[x]"+ m);
					
					//Now check the node's call sites
					if(ma == null){
						continue; //the node is a concurrent method or has been added before
						//no need further processing
					}
					//get the call graph node, if null, dead node, ignore
					CGNode node = callGraph.getNode(m, Everywhere.EVERYWHERE);
					if(node == null){ //the method is in dead code, mark it as dead code
						ma.setDead(true);
						continue; //the method has never been called in the app.
					}
					
					for(Iterator<CGNode> srcNodesI = callGraph.getPredNodes(node); srcNodesI.hasNext();){
						CGNode srcNode = srcNodesI.next();
						IMethod method = srcNode.getMethod();
						if(!(method instanceof AstMethod)){
							continue; //remove the fake root;
						}
						
						AstMethod srcMethod = (AstMethod) method;
						for(Iterator<CallSiteReference> callSitesI = callGraph.getPossibleSites(srcNode, node);
							callSitesI.hasNext();){
							
							CallSiteReference callSite = callSitesI.next();
							int callPC = callSite.getProgramCounter();
							Position callSitePos = srcMethod.debugInfo().getInstructionPosition(callPC);
							
							result.addMatchedCallSite(callSitePos.getURL().toString().substring(5),
									callSitePos.getFirstLine(), callSitePos.getFirstCol(),
									callSitePos.getLastLine(), callSitePos.getLastCol(), callSite.toString());
						} //loop: all call sites
					} //loop: all src node to node
				} // loop: all impacted method
			} //try
			catch(Exception e){
					e.printStackTrace();
			}
		}
		
		//finally show call graph
		//System.out.println(callGraph);
		
		return result;
	}
	
	/**
	 * It will check all the related methods in class hierarchy
	 * including
	 *   1) if the method is defined in a interface, all the methods implemented the interface's definition
	 *   2) the method's containing class's super/sub classes's corresponding methods
	 * @param method the source method
	 * @return a set of methods, not including the source method
	 */
	protected Set<IMethod> getAllImpactedMethodsInClassHierarchy(IMethod method){
		Set<IMethod> result = new HashSet<IMethod>();
		IClass methodClass = method.getDeclaringClass(); //the containing class
		Selector mSelector = method.getSelector(); //used to locate the method
		
		//1st place to search it's interface
		//System.out.println("  --> Scan Interfaces");
		Collection<IClass> interfaceClasses = methodClass.getAllImplementedInterfaces();
		for(IClass ifc : interfaceClasses){
			IMethod m = ifc.getMethod(mSelector);
			if(m != null){
				//System.out.println("  From interface("+ c + ") method:" + m);
				result.add(m);
				
				//Then we need find all the method that implements the interface
				Set<IClass> classes = classHC.getImplementors(ifc.getReference());
				for(IClass ic : classes){
					//each class should have the method
					//System.out.println("      From class"+ic);
					IMethod im = ic.getMethod(mSelector);
					if(im != null){
						//System.out.println("      Method:" + im);
						result.add(im);
					}
				}
			}
		}
		
		//2nd place to search, the class tree
		//locate top class
		IClass curClass = methodClass;
		IClass superClass = curClass;
		while(curClass != null && curClass != classHC.getRootClass()){
			superClass = curClass;
			curClass = curClass.getSuperclass();
		}
		//System.out.println("Scan SuperClass tree" + superClass);
		Collection<IClass> classes = classHC.computeSubClasses(superClass.getReference());
		classes.add(superClass); //add the super class, too;
		//now scan all classes that contains the method
		for(IClass c : classes){
			IMethod m = c.getMethod(mSelector);
			if(m != null){
				//System.out.println("  From class("+ c + ") method:" + m);
				result.add(m);
			}
		}
		//finally, we removed the original method itself
		result.remove(method);
		return result;
	}
}
