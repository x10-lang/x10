package x10.optimizations.atOptimzer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

import x10.optimizations.atOptimzer.ForClosureObjectField;

public class ForClosureObject {
	public String varName = "";
	public boolean ambiguity = false;
	public LinkedList<ForClosureObjectField> fieldDetails = null;
	
	public ForClosureObject () {		
	}
	
	public ForClosureObject(String varName, boolean ambiguity) {
		this.varName = varName;
		this.ambiguity = ambiguity;
		this.fieldDetails = new LinkedList<ForClosureObjectField>();
	}

}
