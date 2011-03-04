package com.ibm.wala.cast.x10.analysis.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class LocalPatternFinder extends PatternFinder{
	public LocalPatternFinder(CommunicationGraph g, HashMap<CommunicationNode, Integer> r){
		this.results = r;
		this.g = g;
	}
	public LocalPatternFinder(CommunicationGraph g, HashMap<CommunicationNode, Integer> r,
			HashSet<PatternFinder.Filter> mask){
		this.results = r;
		this.g = g;
		this.mask = mask;
	}
	public boolean isLabelSpecial(CommunicationLabel l){
		return l.isLocal;
	}
	public int hasPattern(CommunicationNode n){
		if(results.containsKey(n)){
			if(results.get(n).intValue()==1)
				return 1;
			return 0;
		}
		
		if(g.getSuccNodeCount(n)==0){
			results.put(n, new Integer(1));
			return 1;
		}
		
		boolean flag = true;
		Iterator<CommunicationLabel> sucLabels = (Iterator<CommunicationLabel>) g.getSuccLabels(n);
		while(sucLabels.hasNext()){
			CommunicationLabel sl = sucLabels.next();
			flag = flag && isLabelSpecial(sl);
			if(flag == false){
				results.put(n, new Integer(0));
				return 0;
			}
		}
		results.put(n, new Integer(1));
		Iterator<CommunicationNode> it = g.getSuccNodes(n);
		while(it.hasNext()){
			CommunicationNode suc = it.next();
			if(isFiltered(suc)){
				results.put(suc, new Integer(0));
				break;
			}
			flag = flag && (hasPattern(suc)==1);
			if(flag == false){
				results.remove(n);
				results.put(n, new Integer(0));
				return 0;
			}
		}
		return 1;
	}
}






