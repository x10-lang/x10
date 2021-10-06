package x10.optimizations.atOptimzer;

import java.util.*;

public class EdgeRep {
	
	public String edgeType;
	public String desName;
	public String fieldName = "";
	public String edgeName = "";
	public boolean copyFlag = false;

	public EdgeRep(String edgeType, String desName) {
		this.edgeType = edgeType;
		this.desName = desName;
	}
	
	public EdgeRep(String edgeType, String desName, boolean copyFlag) {
		this.edgeType = edgeType;
		this.desName = desName;
		this.copyFlag = copyFlag;
	}
	
	public EdgeRep(String edgeType, String desName, String fieldName) {
		this.edgeType = edgeType;
		this.desName = desName;
		this.fieldName = fieldName;
	}
	
	public EdgeRep(String edgeType, String desName, String fieldName, boolean copyFlag) {
		this.edgeType = edgeType;
		this.desName = desName;
		this.fieldName = fieldName;
		this.copyFlag = copyFlag;
	}
	
	public EdgeRep() {
		this.edgeType = "";
		this.desName = "";
		this.fieldName = "";
	}
}
