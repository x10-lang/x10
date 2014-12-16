/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */
package x10.compiler.ws.util;

import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;

import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.CodeBlock;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.MethodDecl;
import polyglot.types.MethodDef;
import polyglot.util.Position;
import polyglot.util.CollectionUtil;
import x10.util.CollectionFactory;
import x10.ast.Closure;
import x10.compiler.ws.WSTransformState.CallSiteType;
import x10.compiler.ws.WSTransformState.MethodType;

/**
 * @author Haichuan
 * This is a container to store all information about WS transformation content.
 * Including all target methods
 * and all call sites
 * 
 * Right now, we only use WSSourcePosition as precise match
 * And we only support basic method decl. no constructor and closure support now
 * 
 * 
 * Note, it is not thread safe. The thread safe is ensured by polyglot scheduler
 */
public class WSTransformationContent {
	
					   
	public class MethodAttribute {
		protected String desc;
		protected MethodType type;
		protected boolean isDead; //the node is in dead code;
		
		public MethodAttribute(String desc, MethodType type){
			this.desc = desc;
			this.type = type;
		}
		
		public boolean isDead() {
			return isDead;
		}

		public void setDead(boolean isDead) {
			this.isDead = isDead;
		}

		public MethodType getType() {
			return type;
		}

		public String getDesc(){
			return desc;
		}
		
		public String toString(){
			StringBuffer sb = new StringBuffer();
			sb.append(getDesc());
			sb.append(" : ");
			sb.append(type);
			if(isDead){
				sb.append("[Dead]");
			}
			return sb.toString();
		}
	}
	
	public class CallSiteAttribute {
		protected String desc;
		protected CallSiteType type;
		
		public CallSiteAttribute(String desc, CallSiteType type){
			this.desc = desc;
			this.type = type;
		}

		public String getDesc() {
			return desc;
		}

		public CallSiteType getType() {
			return type;
		}
		
		public String toString(){
			return getDesc() + " : " + type;
		}
	}
	
	//Store the concurrent methods
	//Note, the url has no header "file:" string, and the position is method body's position
	//The String is method name, and [C] or [D]. C: contains concurrent, [D]: derived parallel
	//             (src url   ->       ( method body's position, method name))
	protected TreeMap<String, Map<WSSourcePosition, MethodAttribute>> conMethodMap;

	//Store all the call sites to concurrent methods
	//Note, the url has no header "file:" string. and the position is call instruction's position
	//The String is no clear usage now. Just store debug information now
	//             (src url  ->      ( call inst's pos, debug info)
	protected TreeMap<String, Map<WSSourcePosition, CallSiteAttribute>> callSiteMap;
	
	//Record all dead methods's def. These methods are not in call graph.
	//So we cannot get their call sites directly.
	//The content will be populated by a special barrier pass.
	//In the pass, all methoddecl will be checked, if they are dead method decl (by source code line number)
	//add the def in the set
	//Then in the transformation pass, we check the call's method def', if it is in the deadMethoDefs, return matched call.
	protected Set<MethodDef> deadMethodDefs;
	
	public WSTransformationContent(){
		conMethodMap = new TreeMap<String, Map<WSSourcePosition, MethodAttribute>>();
		callSiteMap = new TreeMap<String, Map<WSSourcePosition, CallSiteAttribute>>();
		deadMethodDefs = CollectionFactory.newHashSet();
	}
	
	public void addConcurrentMethod(String url, int startLine, int startColumn, int endLine, int endColumn, String info){
		WSSourcePosition wsPosition = new WSSourcePosition(url, startLine, startColumn, endLine, endColumn);
		
		Map<WSSourcePosition, MethodAttribute> innerMap;
		if(conMethodMap.containsKey(url)){
			innerMap = conMethodMap.get(url);
		}
		else{
			innerMap = CollectionFactory.newHashMap();
			conMethodMap.put(url, innerMap);
		}
		
		MethodAttribute ma = new MethodAttribute(info, MethodType.BODYDEF_TRANSFORMATION);
		innerMap.put(wsPosition, ma);
	}
	
	/**
	 * @param url
	 * @param startLine
	 * @param startColumn
	 * @param endLine
	 * @param endColumn
	 * @param info
	 * @return the added method attributes if the node is a def only transformation node
	 */
	public MethodAttribute addImpactedMethod(String url, int startLine, int startColumn, int endLine, int endColumn, String info){
		WSSourcePosition wsPosition = new WSSourcePosition(url, startLine, startColumn, endLine, endColumn);
		
		Map<WSSourcePosition, MethodAttribute> innerMap;
		if(conMethodMap.containsKey(url)){
			innerMap = conMethodMap.get(url);
		}
		else{
			innerMap = CollectionFactory.newHashMap();
			conMethodMap.put(url, innerMap);
		}
		
		//check whether this node has been added. If not added, add it, and return it
		if(!innerMap.containsKey(wsPosition)){
			MethodAttribute ma = new MethodAttribute(info, MethodType.DEFONLY_TRANSFORMATION);
			innerMap.put(wsPosition, ma);
			return ma;
		}
		return null;
	}
	
	
	public void addConcurrentCallSite(String url, int startLine, int startColumn, int endLine, int endColumn, String info){
		WSSourcePosition wsPosition = new WSSourcePosition(url, startLine, startColumn, endLine, endColumn);
		
		Map<WSSourcePosition, CallSiteAttribute> innerMap;
		if(callSiteMap.containsKey(url)){
			innerMap = callSiteMap.get(url);
		}
		else{
			innerMap = CollectionFactory.newHashMap();
			callSiteMap.put(url, innerMap);
		}
		
		CallSiteAttribute ca = new CallSiteAttribute(info, CallSiteType.CONCURRENT_CALL);
		innerMap.put(wsPosition, ca);
	}
	
	
	public void addMatchedCallSite(String url, int startLine, int startColumn, int endLine, int endColumn, String info){
		WSSourcePosition wsPosition = new WSSourcePosition(url, startLine, startColumn, endLine, endColumn);
		
		Map<WSSourcePosition, CallSiteAttribute> innerMap;
		if(callSiteMap.containsKey(url)){
			innerMap = callSiteMap.get(url);
		}
		else{
			innerMap = CollectionFactory.newHashMap();
			callSiteMap.put(url, innerMap);
		}
		
		if(!innerMap.containsKey(wsPosition)){
			CallSiteAttribute ma = new CallSiteAttribute(info, CallSiteType.MATCHED_CALL);
			innerMap.put(wsPosition, ma);
		}
		else{
			System.err.println("[WALA_WS_ERR]One call site cannot be both concurrent call and matched call:"+ info);
		}
	}
	
	
	/**
	 * Input a code block, return the method attribute
	 * If it is null. Not found. 
	 * @param codeBlock method decl or interface
	 * @return the found method attribute object. If not found, null
	 */
	protected MethodAttribute getMethodAttribute(CodeBlock codeBlock){
		Block methodBody = codeBlock.body();
		Position pos;
		if(methodBody != null){
			pos = methodBody.position();
		}
		else{ //a interface
			pos = codeBlock.position();
		}
		WSSourcePosition wsPos = new WSSourcePosition(pos);
		//DEBUG 
		//System.out.printf("[MethodPos]%s: %s\n", mDecl, wsPos);
		
		if(conMethodMap.containsKey(wsPos.getUrl())){
			Map<WSSourcePosition, MethodAttribute> innerMap = conMethodMap.get(wsPos.getUrl());
			if(innerMap.containsKey(wsPos)){
				//DEBUG
				//System.out.println(" Concurrent method info:" + innerMap.get(wsPos));
				return innerMap.get(wsPos);
			}
		}
		return null;
	}
	
	/**
	 * The input should be MethodDecl, ConstructorDecl, or Closure
	 * @param codeBlock
	 * @return whether the node is concurrent method
	 */
	public MethodType getMethodType(CodeBlock codeBlock){
		MethodAttribute ma = getMethodAttribute(codeBlock);

		if(ma != null){
			return ma.getType();
		}
		return MethodType.NORMAL;
	}
	
	public void checkAndMarkDeadMethodDef(CodeBlock codeBlock){
		MethodAttribute ma = getMethodAttribute(codeBlock);
		
		if(ma != null && ma.isDead()){
			//locate the method def from the code block
			if(codeBlock instanceof MethodDecl){
				deadMethodDefs.add(((MethodDecl)codeBlock).methodDef());
			}			
		}
	}
	
	public CallSiteType getCallSiteType(Call call){
		Position pos = call.position();
		WSSourcePosition wsPos = new WSSourcePosition(pos);
		//DEBUG 
		//System.out.printf("[CallSitePos]%s: %s\n", call, wsPos);
		
		if(callSiteMap.containsKey(wsPos.getUrl())){
			Map<WSSourcePosition, CallSiteAttribute> innerMap = callSiteMap.get(wsPos.getUrl());
			if(innerMap.containsKey(wsPos)){
				//DEBUG
				//System.out.println(" Concurrent call site info:" + innerMap.get(wsPos));
				return innerMap.get(wsPos).getType();
			}
		}
		//And check the dead method def
		MethodDef mDef = call.methodInstance().def();
		if(deadMethodDefs.contains(mDef)){
			return CallSiteType.MATCHED_CALL;
		}
		return CallSiteType.NORMAL;
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
			Map<WSSourcePosition, MethodAttribute> innerMap = conMethodMap.get(url);
			sb.append("  ").append(url).append('\n');
			for(WSSourcePosition pos : innerMap.keySet()){
				sb.append("    ").append(pos.toShortString()).append("  ").append(innerMap.get(pos)).append('\n');
			}
		}
		sb.append('\n');
		//then iterate all call sites
		
		sb.append(">>>> Call Site to Concurrent Method List\n");
		for(String url : callSiteMap.keySet()){
			Map<WSSourcePosition, CallSiteAttribute> innerMap = callSiteMap.get(url);
			sb.append("  ").append(url).append('\n');
			for(WSSourcePosition pos : innerMap.keySet()){
				sb.append("    ").append(pos.toShortString()).append("  ").append(innerMap.get(pos)).append('\n');
			}
		}
		sb.append('\n');
		return sb.toString();
	}
	
}
