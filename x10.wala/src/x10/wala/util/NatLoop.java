package x10.wala.util;
import java.util.HashSet;
import java.util.Iterator;

import java.util.Set;


import com.ibm.wala.cfg.ControlFlowGraph;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.util.graph.dominators.NumberedDominators;

/**
 * This class wraps a natural loop in a control flow graph
 * 
 * @author blshao
 *
 */
public class NatLoop {
    /**
     * the entry point of the loop: a natural loop has only one entry point
     */
    private ISSABasicBlock start;
    /**
     * the exit point of the loop: the node has a back edge
     */
    private ISSABasicBlock end;
    /**
     * all nodes of a loop, including the start and the end
     */
    private HashSet<ISSABasicBlock> body;
    
    
    public NatLoop(){
	body = new HashSet<ISSABasicBlock>();
    }
    public NatLoop(ISSABasicBlock s, ISSABasicBlock e){
	start = s;
	end = e;
	body = new HashSet<ISSABasicBlock>();
	body.add(s);
	body.add(e);
    }
    
    public ISSABasicBlock getStart(){
	return start;
    }
    
    public ISSABasicBlock getEnd(){
	return end;
    }
    
    public void setStart(ISSABasicBlock s){
	start = s;
	if(!body.contains(s)){
	    body.add(s);
	}
    }
    public void setEnd(ISSABasicBlock e){
	end = e;
	if(!body.contains(e)){
	    body.add(e);
	}
    }
    public void addNode(ISSABasicBlock n){
	if(!isInLoop(n)){
	    body.add(n);
	}
    }
    public boolean isInLoop(ISSABasicBlock n){
	return body.contains(n);
    }
    public int hashCode() {
	return toString().hashCode();
    }
    
    public String toString(){
	StringBuffer str= new StringBuffer();
	Iterator<ISSABasicBlock> it = body.iterator();
	while(it.hasNext()){
	    ISSABasicBlock tmp = it.next();
	    str.append(tmp.getNumber());
	    str.append(" ");
	}
	return str.toString();
    }
    
}


