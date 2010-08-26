package x10.finish.table;
import java.io.Serializable;

/**
 * key of the "calltable", we have two kinds of keys, one represents a "finish"
 * and the other means a "normal method"
 */
public abstract class CallTableKey implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public final String scope;
	public final String name;
	public final int line;
	public final int column;
	public CallTableVal lastStmt;
	public CallTableKey(String s, String n, int l, int c){
	    scope = s;
	    name = n;
	    line = l;
	    column = c;
	    lastStmt = null;
	}
	public String genSignature(){
	    return scope + "."+line+"."+column;
	}
}
