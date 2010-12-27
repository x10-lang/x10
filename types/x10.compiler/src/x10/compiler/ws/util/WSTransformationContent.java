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
package x10.compiler.ws.util;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import polyglot.ast.Call;
import polyglot.ast.CodeBlock;
import polyglot.ast.MethodDecl;
import polyglot.util.Position;

/**
 * @author Haichuan
 * This is a container to store all information about WS transformation content.
 * Including all target methods
 * and all call sites
 * 
 * Right now, we only use WSSourcePosition as precise match
 * 
 * Note, it is not thread safe. The thread safe is ensured by polyglot scheduler
 */
public class WSTransformationContent {
	
	//Store the concurrent methods
	//Note, the url has no header "file:" string, and the position is method body's position
	//The String is method name, and [C] or [D]. C: contains concurrent, [D]: derived parallel
	//             (src url   ->       ( method body's position, method name))
	protected TreeMap<String, HashMap<WSSourcePosition, String>> conMethodMap;

	//Store all the call sites to concurrent methods
	//Note, the url has no header "file:" string. and the position is call instruction's position
	//The String is no clear usage now. Just store debug information now
	//             (src url  ->      ( call inst's pos, debug info)
	protected TreeMap<String, HashMap<WSSourcePosition, String>> callSiteMap;
	
	
	public WSTransformationContent(){
		conMethodMap = new TreeMap<String, HashMap<WSSourcePosition, String>>();
		callSiteMap = new TreeMap<String, HashMap<WSSourcePosition, String>>();
	}
	
	public void addConcurrentMethod(String url, int startLine, int startColumn, int endLine, int endColumn, String info){
		WSSourcePosition wsPosition = new WSSourcePosition(url, startLine, startColumn, endLine, endColumn);
		
		HashMap<WSSourcePosition, String> innerMap;
		if(conMethodMap.containsKey(url)){
			innerMap = conMethodMap.get(url);
		}
		else{
			innerMap = new HashMap<WSSourcePosition, String>();
			conMethodMap.put(url, innerMap);
		}
		innerMap.put(wsPosition, info);
	}
	
	public void addConcurrentCallSite(String url, int startLine, int startColumn, int endLine, int endColumn, String info){
		WSSourcePosition wsPosition = new WSSourcePosition(url, startLine, startColumn, endLine, endColumn);
		
		HashMap<WSSourcePosition, String> innerMap;
		if(callSiteMap.containsKey(url)){
			innerMap = callSiteMap.get(url);
		}
		else{
			innerMap = new HashMap<WSSourcePosition, String>();
			callSiteMap.put(url, innerMap);
		}
		innerMap.put(wsPosition, info);
	}
	
	
	/**
	 * The input should be MethodDecl, ConstructorDecl, or Closure
	 * @param codeBlock
	 * @return whether the node is concurrent method
	 */
	public boolean isTargetMethod(CodeBlock codeBlock){
		Position pos = codeBlock.body().position();
		WSSourcePosition wsPos = new WSSourcePosition(pos);
		//DEBUG 
		//System.out.printf("[MethodPos]%s: %s\n", mDecl, wsPos);
		
		if(conMethodMap.containsKey(wsPos.getUrl())){
			HashMap<WSSourcePosition, String> innerMap = conMethodMap.get(wsPos.getUrl());
			if(innerMap.containsKey(wsPos)){
				//DEBUG
				//System.out.println(" Concurrent method info:" + innerMap.get(wsPos));
				return true;
			}
			else{
				return false;
			}
		}
		return false;
	}
	
	public boolean isTargetCallSite(Call call){
		Position pos = call.position();
		WSSourcePosition wsPos = new WSSourcePosition(pos);
		//DEBUG 
		//System.out.printf("[CallSitePos]%s: %s\n", call, wsPos);
		
		if(callSiteMap.containsKey(wsPos.getUrl())){
			HashMap<WSSourcePosition, String> innerMap = callSiteMap.get(wsPos.getUrl());
			if(innerMap.containsKey(wsPos)){
				//DEBUG
				//System.out.println(" Concurrent call site info:" + innerMap.get(wsPos));
				return true;
			}
			else{
				return false;
			}
		}
		return false;
	}
	
	/* 
	 * Used to show the findings in CallGraph analysis
	 */
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("-------------- WS CallGraph Analysis Result -------------\n");
		//first iterate all concurrent method node
		sb.append(">>>> Concurrent Method List\n");
		for(String url : conMethodMap.keySet()){
			Map<WSSourcePosition, String> innerMap = conMethodMap.get(url);
			sb.append("  ").append(url).append('\n');
			for(WSSourcePosition pos : innerMap.keySet()){
				sb.append("    ").append(pos).append("  ").append(innerMap.get(pos)).append('\n');
			}
		}
		sb.append('\n');
		//then iterate all call sites
		
		sb.append(">>>> Call Site to Concurrent Method List\n");
		for(String url : callSiteMap.keySet()){
			Map<WSSourcePosition, String> innerMap = callSiteMap.get(url);
			sb.append("  ").append(url).append('\n');
			for(WSSourcePosition pos : innerMap.keySet()){
				sb.append("    ").append(pos).append("  ").append(innerMap.get(pos)).append('\n');
			}
		}
		sb.append('\n');
		return sb.toString();
	}
	
}
