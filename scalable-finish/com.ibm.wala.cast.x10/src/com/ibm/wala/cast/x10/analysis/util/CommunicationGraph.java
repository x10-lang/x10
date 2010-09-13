package com.ibm.wala.cast.x10.analysis.util;

import java.util.HashMap;

import x10.finish.table.*;

import com.ibm.wala.util.graph.labeled.SlowSparseNumberedLabeledGraph;

public class CommunicationGraph extends SlowSparseNumberedLabeledGraph<CommunicationNode,CommunicationLabel>{
	private HashMap<CallTableObj,CommunicationNode> obj2node;
	public CommunicationGraph(CommunicationLabel defaultLabel) {
		super(defaultLabel);
		// TODO Auto-generated constructor stub
		obj2node = new HashMap<CallTableObj,CommunicationNode>();
	}
	public void addNode(CommunicationNode n){
		obj2node.put(n.node, n);
		super.addNode(n);
		n.setNumber(this.getNumber(n));
	}
	public CommunicationNode Obj2Node(CallTableObj n){
		return obj2node.get(n);
	}
}
 
