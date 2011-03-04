package com.ibm.wala.cast.x10.analysis.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class SimplePatternFinder extends PatternFinder{
	private HashSet<CommunicationNode> visited;
	public SimplePatternFinder(CommunicationGraph g, HashMap<CommunicationNode, Integer> r){
		this.results = r;
		this.g = g;
		this.visited = new HashSet<CommunicationNode>();
	}
	public SimplePatternFinder(CommunicationGraph g, HashMap<CommunicationNode, Integer> r,
			HashSet<PatternFinder.Filter> mask){
		this.results = r;
		this.g = g;
		this.visited = new HashSet<CommunicationNode>();
		this.mask = mask;
	}
	public boolean areLabelsSpecial(Iterator<? extends CommunicationLabel> il) {
		boolean hasRemote = false;
		while(il.hasNext()){
			CommunicationLabel l = il.next();
			if(l.isLocal==false){
				hasRemote = true;
				break;
			}
		}
		return hasRemote;
	}

	@Override
	public int hasPattern(CommunicationNode n) {
		visited.add(n);
		if(!results.containsKey(n)){
			new LocalPatternFinder(g,results).hasPattern(n);
		}
		if(results.get(n).intValue()==1){
			return 1;
		}
		int p = 0;
		Iterator<CommunicationNode> it = g.getSuccNodes(n);
		while(it.hasNext()){
			CommunicationNode suc = it.next();
			if(isFiltered(suc)){
				results.put(suc,new Integer(0));
				break;
			}
			if(!visited.contains(suc)){
				p = hasPattern(suc);
			}else{
				p = results.get(suc).intValue();
			}
			if(p==0){
				results.remove(n);
				results.put(n, new Integer(0));
				return 0;
			}
			if(p==2){
				boolean hasRemote = areLabelsSpecial(g.getEdgeLabels(n, suc).iterator());
				if(hasRemote){
					results.remove(n);
					results.put(n, new Integer(0));
					return 0;
				}else{
					results.remove(n);
					results.put(n, new Integer(2));
					return 2;
				}
			}
			
		}
		results.put(n, new Integer(2));
		return 2;
	}

	@Override
	public boolean isLabelSpecial(CommunicationLabel l) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
