package com.ibm.wala.cast.x10.analysis.util;

import java.util.HashMap;
import java.util.Iterator;

public class LocalPatternFinder extends PatternFinder{
	public LocalPatternFinder(CommunicationGraph g, HashMap<CommunicationNode, Integer> r){
		this.results = r;
		this.g = g;
	}
	public boolean isLabelSpecial(CommunicationLabel l){
		return l.isLocal;
	}
	public boolean hasPattern(CommunicationNode n){
		if(results.containsKey(n)){
			if(results.get(n).intValue()==1)
				return true;
			return false;
		}
		
		boolean flag = true;
		if(g.getSuccNodeCount(n)==0){
			results.put(n, new Integer(1));
			return flag;
		}
		
		Iterator<CommunicationLabel> sucLabels = (Iterator<CommunicationLabel>) g.getSuccLabels(n);
		while(sucLabels.hasNext()){
			CommunicationLabel sl = sucLabels.next();
			flag = flag && isLabelSpecial(sl);
			if(flag == false){
				results.put(n, new Integer(0));
				return false;
			}
		}
		results.put(n, new Integer(1));
		Iterator<CommunicationNode> it = g.getSuccNodes(n);
		while(it.hasNext()){
			CommunicationNode suc = it.next();
			flag = flag && hasPattern(suc);
			if(flag == false){
				results.remove(n);
				results.put(n, new Integer(0));
				return false;
			}
		}
		return true;
	}
}






