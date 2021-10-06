package x10.optimizations.atOptimzer;

import java.util.*;

public class ObjNode {
	public String name;
	public String objType;
	public boolean copyFlag = false;
	public int counter = 0;

	public ObjNode(String objName, String objType) {
		this.name = objName;
		this.objType = objType;
	}
	
	public ObjNode(String objName, String objType, boolean copyFlag) {
		this.name = objName;
		this.objType = objType;
		this.copyFlag = copyFlag;
	}
}
