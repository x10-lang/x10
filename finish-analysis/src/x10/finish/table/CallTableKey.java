package x10.finish.table;
import java.io.Serializable;

/**
 * key of the "calltable", we have two kinds of keys, one represents a "finish"
 * and the other means a "normal method"
 */
public abstract class CallTableKey implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public final String scope;
	public CallTableVal lastStmt;
	public CallTableKey(String s){
	    scope = renameMethod(s);
	    lastStmt = null;
	}
	public abstract String genSignature();
	
	/* wala returns a long signature for an async statement, I rename it as follow:
	 * activity + absolut_path + line + column */
	public String renameMethod(String name) {
	    if (name.contains("activity") && (name.contains(">"))) {
		int first_colon = name.indexOf(':');
		int second_colon = name.indexOf(':', first_colon + 1);
		String file_name = name.substring(first_colon + 1, second_colon);
		int last = name.indexOf('>');
		String line = name.substring(second_colon + 1, last);
		int hashed_name = file_name.hashCode();
		return "activity"+file_name+":"+line;
                //return "activity" + String.valueOf(hashed_name) + ":" + line;
	    }
	    return name;
	}
}
