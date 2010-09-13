package com.ibm.wala.cast.x10.analysis.util;

import java.util.HashMap;

public abstract class PatternFinder{
	protected HashMap<CommunicationNode,Integer> results;
	protected CommunicationGraph g;
	public abstract boolean isLabelSpecial(CommunicationLabel l);
	public abstract boolean hasPattern(CommunicationNode n);
}
