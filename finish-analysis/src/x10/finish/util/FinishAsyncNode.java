package x10.finish.util;


import java.util.LinkedList;


import x10.finish.table.CallTableVal;


import com.ibm.wala.util.debug.Assertions;
import com.ibm.wala.util.graph.INodeWithNumber;


public class FinishAsyncNode implements INodeWithNumber{
    public enum FinishAsyncType{Finish, Async_Key, Async_Call, Method_Key, Method_Call, At_Key, At_Call}
    //NodeManager.addNode adds a node to a graph if this field is -1
    private int number = -1;
    public final FinishAsyncType type;
    public final String sig;
    private final int line;
    private final int column;
    private final LinkedList<Integer> pc;
    private final boolean is_last;
    private final CallTableVal.Arity a;
    
    public FinishAsyncNode(String sig, int line, int column, boolean is_last, 
	    FinishAsyncType type, CallTableVal.Arity a, LinkedList<Integer> pc){
	this.type = type;
	this.sig = sig;
	this.line = line;
	this.column = column;
	this.is_last = is_last;
	this.pc = pc;
	this.a = a;
     }
    
    public FinishAsyncNode(FinishAsyncNode n2) {
	this.type = n2.type;
	this.sig = n2.sig;
	this.line = n2.line;
	this.column = n2.column;
	this.is_last = n2.is_last;
	this.pc = n2.pc;
	this.a = n2.a;
    }

    public int getLine(){
	switch(type){
	case Finish:
	case Async_Key:
	case At_Key:
	case Async_Call:
	    return line;
	default:
	    Assertions.UNREACHABLE("line number is not valid in "+type.toString());
	    return -1;
	}
    }
    
    public int getColumn(){
	switch(type){
	case Finish:
	case Async_Key:
	case At_Key:
	case Async_Call:
	    return column;
	default:
	    Assertions.UNREACHABLE("column number is not valid in "+type.toString());
	    return -1;
	}
    }
    
    public LinkedList<Integer> getPC(){
	if(type==FinishAsyncType.Method_Call){
	    return pc;
	}
	Assertions.UNREACHABLE("pc is not valid in "+type.toString());
	return null;
    }
    
    public boolean getLastFlag(){
	switch(type){
	case Finish:
	case Async_Key:
	case At_Key:
	case Method_Key:
	    Assertions.UNREACHABLE("is_last flag is not valid in "+type.toString());
	    return false;
	default:
	    return is_last;
	}
    }
    public CallTableVal.Arity getArity(){
	switch(type){
	case Finish:
	case Async_Key:
	case At_Key:
	case Method_Key:
	    Assertions.UNREACHABLE("arity is not valid in "+type.toString());
	    return CallTableVal.Arity.One;
	default:
	    return a;
	}
    }
    @Override
    public int getGraphNodeId() {
	return number;
    }

    @Override
    public void setGraphNodeId(int number) {
	this.number = number;
    }
    
    public String toString(){
	String s="";
	for(int i=0;i<pc.size();i++){
	    s = s + "@"+pc.get(i);
	}
	switch(type){
	case Finish: 
	case Async_Key:
	case At_Key:return (sig+"-"+type.toString()+"-"+line+"-"+column);
	case Method_Key: return (sig+"-"+type.toString());
	case Async_Call: 
	case At_Call:return (sig+"-"+type.toString()+"-"+line+"-"+column+"-"+a.toString()+"-"+is_last);
	case Method_Call:return (sig+"-"+type.toString()+"-"+s +"-"+a.toString()+"-"+is_last);
	}
	return "";
    }
}