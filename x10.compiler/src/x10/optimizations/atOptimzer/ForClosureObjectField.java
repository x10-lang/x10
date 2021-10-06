package x10.optimizations.atOptimzer;

import java.util.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

public class ForClosureObjectField {
	public String fieldName = "";
	public String tempStoredName = "";
	public String fieldType = "";
	public boolean ambiguity = false;
	public String fieldObjName = "";
	
	public ForClosureObjectField () {
		
	}
	
	public ForClosureObjectField(String fieldName, String tempStoredName, String fieldType) {
		this.fieldName = fieldName;
		this.tempStoredName = tempStoredName;
		this.fieldType = fieldType;
	}
	
	public ForClosureObjectField(String fieldName, String tempStoredName, String fieldType, boolean ambiguity, String fieldObjName) {
		this.fieldName = fieldName;
		this.tempStoredName = tempStoredName;
		this.fieldType = fieldType;
		this.ambiguity = ambiguity;
		this.fieldObjName = fieldObjName;
	}
}
