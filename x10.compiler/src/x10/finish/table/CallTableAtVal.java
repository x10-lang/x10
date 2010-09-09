package x10.finish.table;
import java.util.HashSet;


import java.util.Iterator;
import java.util.LinkedList;

public class CallTableAtVal extends CallTableVal {
    
    private static final long serialVersionUID = 1L;
    public CallTableAtVal(String s, String n, int l,int c,int b, CallTableKey p) {
	super(s,n,l,c,b,p);
    }
    public CallTableAtVal(String s, String n, Arity a, int l,int c,int b,CallTableKey p) {
	super(s,n,l,c,b,p,a);
    }
    public String genSignature(){
	return scope+"."+line+"."+column;
    }
    public int hashCode(){
	return genSignature().hashCode();
    }
    
    public boolean equals(Object o) {
	boolean result = false;
	if (o instanceof CallTableAtVal){
	    result = genSignature().equals(((CallTableAtVal) o).genSignature());
	}
	return result;
    }
    
    public String toString(){
	String tmp="";
	if(isLast){
	    tmp = "-last";
	}
	return (a.toString()+"-"+scope+".at."+line+"."+column+tmp);
    }
    
}
