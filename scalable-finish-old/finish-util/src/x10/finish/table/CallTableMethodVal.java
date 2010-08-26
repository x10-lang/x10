package x10.finish.table;

import java.io.Serializable;
import java.util.LinkedList;


/**
 * Similar to CallTableKey, this class represents the "value" part of the
 * key-value map. All "values" in the calltable will be method invocation, and
 * we distinguish normal methods call from ansyc statement by its signature
 */

public class CallTableMethodVal extends CallTableVal {

    private static final long serialVersionUID = 1L;
    public final boolean is_async;
    public final CallSite cs;
    public CallTableMethodVal(String method_pack, String method_name, int method_line, int method_column, 
	    String callsite_pack, String callsite_name, int callsite_line, int callsite_column, int b, boolean is_async) {
	super(method_pack,method_name,method_line,method_column,b);
	cs = new CallSite(callsite_pack, callsite_name, callsite_line,callsite_column);
	this.is_async = is_async;
    }
    public CallTableMethodVal(String method_pack, String method_name, int method_line, int method_column, 
	    Arity a, String callsite_pack, String callsite_name, int callsite_line, int callsite_column, int b, boolean is_async) {
	super(method_pack,method_name,method_line,method_column,b,a);
	cs = new CallSite(callsite_pack,callsite_name, callsite_line,callsite_column);
	this.is_async = is_async;
    }
    public String genSignature(){
	return scope+"."+line+"."+column+"@"+cs.toString();
    }
    public boolean equals(Object o) {
	boolean result = false;
	if (o instanceof CallTableMethodVal){
	    result = genSignature().equals(((CallTableMethodVal) o).genSignature());
	}
	return result;
    }
    public int hashCode(){
	return genSignature().hashCode();
    }
    public String toString(){
	String tmp="";
	if(aslast!=null){
	    tmp = "-last";
	}
	return a.toString()+"-"+scope+"."+name+"."+line+"."+column+"-"+cs.toString()+tmp;
    }
}

class CallSite implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public final int line;
    public final int column;
    public final String srcpack;
    public final String srcname;
    public CallSite(String s, String n, int l, int c){
	srcpack = s;
	srcname = n;
	line = l;
	column = c;
    }
    public String toString(){
	return srcpack+"."+srcname+"."+line+"."+column;
    }
}