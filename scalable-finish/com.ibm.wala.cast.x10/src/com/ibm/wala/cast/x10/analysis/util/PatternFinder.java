package com.ibm.wala.cast.x10.analysis.util;

import java.util.HashMap;
import java.util.HashSet;

import x10.finish.table.*;

public abstract class PatternFinder{
	public enum Filter {FINISH,ASYNC,AT,METHOD};
	protected HashMap<CommunicationNode,Integer> results;
	protected CommunicationGraph g;
	protected HashSet<Filter> mask = null;
	public abstract boolean isLabelSpecial(CommunicationLabel l);
	public abstract int hasPattern(CommunicationNode n);
	public boolean isFiltered(CommunicationNode n){
		if(mask==null)
			return false;
		CallTableObj cto = n.node;
		
		if((cto instanceof CallTableScopeKey) && ((CallTableScopeKey)cto).isFinish 
				&& mask.contains(Filter.FINISH))
			return true;
		
		if(mask.contains(Filter.AT)&&
				( (cto instanceof CallTableScopeKey) && (!((CallTableScopeKey)cto).isFinish) )
				||(cto instanceof CallTableAtVal))
			return true;
		
		if(mask.contains(Filter.AT)&&
				(cto instanceof CallTableAtVal))
			return true;
				
		if(mask.contains(Filter.ASYNC)&&
				( (cto instanceof CallTableMethodKey)&&(cto.toString().contains("activity"))))
			return true;
		
		if(mask.contains(Filter.ASYNC)&&
				( (cto instanceof CallTableMethodVal)&&((CallTableMethodVal)cto).isAsync))
			return true;
		
		if(mask.contains(Filter.METHOD)&&
				( (cto instanceof CallTableMethodKey)&&(!cto.toString().contains("activity"))))
			return true;
		
		if(mask.contains(Filter.METHOD)&&
				( (cto instanceof CallTableMethodVal)&&(!((CallTableMethodVal)cto).isAsync)))
			return true;
		
		return false;
	}
}
