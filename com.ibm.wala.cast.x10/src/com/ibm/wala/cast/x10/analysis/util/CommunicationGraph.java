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
	public final boolean isLocal;
	public final boolean isLast;
	public CommunicationLabel(CallTableVal.Arity a, CallSite c, boolean local, boolean last){
		arity = a;
		cs = c;
		isLocal = local;
		isLast = last;
	}
	public CommunicationLabel(CallTableVal.Arity a,boolean last){
		arity = a;
		isLast = last;
		// only method has a callsite
		cs = new CallSite("","",0,0);//dummy cs
		isLocal = false;
	}
	@Deprecated
	public CommunicationLabel(){
		arity = CallTableVal.Arity.One;
		cs = new CallSite("","",0,0);//dummy cs
		isLocal = false;
		isLast = false;
	}

/*	public boolean equals(Object o) {
		boolean result = false;
		if (o instanceof CommunicationLabel) {
			CommunicationLabel cn = (CommunicationLabel) o;
			String sig1 = arity.toString() + "-" + cs.toString() + "-"
					+ (isLocal == true ? "local" : "remote");
			String sig2 = cn.arity.toString() + "-" + cn.cs.toString() + "-"
					+ (cn.isLocal == true ? "local" : "remote");
			result = sig1.equals(sig2);
		}
		return result;
	}*/
	public String toString(){
		return arity.toString()+"-"+(isLocal==true?"local":"remote")+(isLast==true?"-last":"");
	}
}
