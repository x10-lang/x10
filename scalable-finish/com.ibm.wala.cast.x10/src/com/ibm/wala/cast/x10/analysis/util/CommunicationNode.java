package com.ibm.wala.cast.x10.analysis.util;

import x10.finish.table.CallTableObj;

public class CommunicationNode {
	public CallTableObj node;
	private int number = -1;
	public CommunicationNode(CallTableObj n) {
		node = n;
	}

	public String toString() {
		return "("+number+")"+node.toString();
				
	}
	
	protected void setNumber(int n){
		number = n;
	}
	public int getNumber(){
		return number;
	}
	// Tow nodes are the same iff their corresponding language constructs have
	// the same definition
	public boolean equals(Object o) {
		boolean result = false;
		if (o instanceof CommunicationNode) {
			CommunicationNode cn = (CommunicationNode) o;
			String sig1 = node.scope + node.line + node.column;
			String sig2 = cn.node.scope + cn.node.line + cn.node.column;
			result = sig1.equals(sig2);
		}
		return result;
	}

	public int hashCode() {
		String sig = node.scope + node.line + node.column;
		return sig.hashCode();
	}
}
