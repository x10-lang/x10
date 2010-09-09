package com.ibm.wala.cast.x10.analysis.util;

import x10.finish.table.*;

import com.ibm.wala.util.graph.labeled.SlowSparseNumberedLabeledGraph;

public class CommunicationGraph extends SlowSparseNumberedLabeledGraph<CommunicationNode,CommunicationLabel>{

	public CommunicationGraph(CommunicationLabel defaultLabel) {
		super(defaultLabel);
		// TODO Auto-generated constructor stub
	}
	
}
class CommunicationNode{
	public CallTableObj node;
	public CommunicationNode(CallTableObj n){
		node = n;
	}
	public String toString(){
		return node.toString();
	}
	
	//Tow nodes are the same iff their corresponding language constructs have the same definition
	public boolean equals(Object o) {
		boolean result = false;
		if (o instanceof CommunicationNode){
			CommunicationNode cn = (CommunicationNode)o;
			String sig1 = node.scope + node.line + node.column;
			String sig2 = cn.node.scope + cn.node.line + cn.node.column;
			result = sig1.equals(sig2);
		}
		return result;
	}
	public int hashCode(){
		String sig = node.scope + node.line + node.column;
		return sig.hashCode();
	}
}

class CommunicationLabel {
	public final CallTableVal.Arity arity;
	public final CallSite cs;
	public CommunicationLabel(CallTableVal.Arity a, CallSite c){
		arity = a;
		cs = c;
	}
	public CommunicationLabel(CallTableVal.Arity a){
		arity = a;
		cs = new CallSite("","",0,0);//dummy cs
	}
	@Deprecated
	public CommunicationLabel(){
		arity = CallTableVal.Arity.One;
		cs = new CallSite("","",0,0);//dummy cs
	}
	public String toString(){
		return arity.toString()+"-"+cs.toString();
	}
}
