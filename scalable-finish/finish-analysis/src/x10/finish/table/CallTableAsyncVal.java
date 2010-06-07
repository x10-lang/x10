package x10.finish.table;

import java.util.LinkedList;


/**
 * Similar to CallTableKey, this class represents the "value" part of the
 * key-value map. All "values" in the calltable will be method invocation, and
 * we distinguish normal methods call from ansyc statement by its signature
 */

public class CallTableAsyncVal extends CallTableVal {

    private static final long serialVersionUID = 1L;
    public String sig;
    public final boolean is_async;
    public final int line,column;
    public int pc;
    public CallTableAsyncVal(String str, int pc, int b,boolean is_async) {
	super(str,b);
	this.pc = pc;
	 
	sig = genSignature();
	this.is_async = is_async;
	if(!is_async){
	    line = -1;
	    column = -1;
	   
	}
	else{
	    int i1 = sig.indexOf(':');
	    int i2 = sig.indexOf(':', i1+1);
	    int i3 = sig.indexOf('@',i2+1);
	    String l = sig.substring(i1+1,i2);
	    String c = sig.substring(i2+1,i3);
	    line = Integer.valueOf(l).intValue();
	    column = Integer.valueOf(c).intValue();
	}
    	
    }
    public CallTableAsyncVal(String str, Arity a, int pc,int b, boolean is_async) {
	super(str,b,a);
	this.pc = pc;
	 
	sig = genSignature();
	this.is_async = is_async;
	if(!is_async){
	    line = -1;
	    column = -1;
	   
	}
	else{
	    int i1 = sig.indexOf(':');
	    int i2 = sig.indexOf(':', i1+1);
	    int i3 = sig.indexOf('@',i2+1);
	    String l = sig.substring(i1+1,i2);
	    String c = sig.substring(i2+1,i3);
	    line = Integer.valueOf(l).intValue();
	    column = Integer.valueOf(c).intValue();
	}
    }
    
    
    public String genSignature(){
	return scope+"@"+pc;
    }
    public boolean equals(Object o) {
	boolean result = false;
	if (o instanceof CallTableAsyncVal){
	    result = sig.equals(((CallTableAsyncVal) o).sig);
	}
	return result;
    }
    public int hashCode(){
	return sig.hashCode();
    }
    public String toString(){
	String tmp="";
	if(aslast!=null){
	    tmp = "-last";
	}
	return a.toString()+"-"+sig+tmp;
    }

    
}
