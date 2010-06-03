package x10.finish.table;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class CallTableAtVal extends CallTableVal {
    
    private static final long serialVersionUID = 1L;
    public final int line;
    public final int column;
    public final String sig;
    public CallTableAtVal(String str, int l,int c,int b) {
	super(str,b);
	this.line = l;
	this.column = c;
	sig = genSignature();
	
    }
    public CallTableAtVal(String str, Arity a, int l,int c,int b) {
	super(str,b,a);
	this.line = l;
	this.column = c;
	sig = genSignature();
    }
    public String genSignature(){
	return scope+"-"+line+"-"+column;
    }
    public int hashCode(){
	return sig.hashCode();
    }
    
    public boolean equals(Object o) {
	boolean result = false;
	if (o instanceof CallTableAtVal){
	    result = sig.equals(((CallTableAtVal) o).sig);
	}
	return result;
    }
    
    public String toString(){
	String tmp="";
	if(aslast!=null){
	    tmp = "-last";
	}
	return (a.toString()+"-"+scope+"-At-"+line+"-"+column+tmp);
    }
    
}
