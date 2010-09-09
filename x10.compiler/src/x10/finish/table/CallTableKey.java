package x10.finish.table;
import java.io.Serializable;

/**
 * key of the "calltable", we have two kinds of keys, one represents a "finish" or "at"
 * and the other means a "normal method"
 */
public abstract class CallTableKey implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public final String scope;
	public final String name;
	public final int line;
	public final int column;
	public CallTableVal lastStmt;
	//default pattern
	public int pattern=0;
	public CallTableKey(String s, String n, int l, int c){
	    scope = s;
	    name = n;
	    line = l;
	    column = c;
	    lastStmt = null;
	}
	/**
	 * scope is usually the class where "key" is defined
	 * so scope+line+column should be able to distiguish 
	 * different "keys"
	 * @return
	 */
	public String genSignature(){
	    return scope + "."+line+"."+column;
	}
}
