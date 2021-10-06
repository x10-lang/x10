package x10.optimizations.atOptimzer;

import java.util.*;

public class ClassInfo {

	public String classifier;
	public String type;
	public String name;
	/* x10Type also using it for parameter optimization as well. 
	 * for field X10Type is used for storing the type.
	 * for parameter x10Type is used to maintain the count
	 */
	public String x10Type = "";
	public int classNo = 0;
	public int methodNo = 0;
	public int methodNoIn = 0;
	public LinkedList<ClassInfo> methodPara = null;
	/* field to store the unique id for identifying function-overloading - for method only*/
	public String uniqueId;

	public ClassInfo() {
		this.classifier = null;
		this.type = null;
		this.name = null;
	}	

	public ClassInfo(String f_classifier, String f_type, String f_name) {
		this.classifier = f_classifier;
		this.type = f_type;
		this.name = f_name;
	}

}
