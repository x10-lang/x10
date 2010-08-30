package x10.barrier.analysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ibm.wala.cast.loader.AstMethod;

class State implements Cloneable {
    int startInst;
    int endInst;
    List outgoingEdges = new ArrayList();
    List incomingEdges = new ArrayList();
    boolean isTerminal = false;
    boolean isStart = false;
    String funName;
    int counter = 0;
    Set parallelBlocks = new HashSet();
    boolean isClocked = false;
    State clone;
    AstMethod method;
    
    
    public State () {
	startInst = -2;
	endInst = -2;
    }
    
    public State cloneMe() {
	State s = null;
	try {
	    s = (State) this.clone();
	} catch (CloneNotSupportedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	s.counter = - this.counter;
	s.funName = this.funName.replace("<async", "d<async");
	return s;
    }
    
    public void  addParallelBlock(State s) {
	for (Object o: this.parallelBlocks) {
	    if (s.isEqualOrCopy((State) o))
		return;
	}
	this.parallelBlocks.add(s);
    }
    
    public State(int startInstruction, int endInstruction, String funcName, boolean clocked, AstMethod m) {
	startInst = startInstruction;
	endInst = endInstruction;
	funName = funcName;
	isClocked = clocked;
	method = m;
    }
    
    
    public void setEndInstruction (int endInstruction) {
	endInst = endInstruction;
    }
    
    
    public void set(int startInstruction, int endInstruction) {
	startInst = startInstruction;
	endInst = endInstruction;
    }    
    
    public void addOutgoingEdge(Edge e) {
	this.outgoingEdges.add(e);
    }
    
    public void addIncomingEdge(Edge e) {
	this.incomingEdges.add(e);
    }
    
    public boolean isEqual(State s) {
	return this.counter == s.counter;
    }
    
  
    
    public boolean isEqualOrCopy(State s) {
	return this.counter == s.counter || this.counter == -s.counter;
    }
    
    public String stateInsts() {
	//return "[" + this.startInst + ":" + this.endInst + "](" + funName + ")" + " "; //+ this.counter;
	int start, end;
	if (startInst == 0)
	    start = 1;
	else start = startInst;

	int sl = method.getSourcePosition(start).getFirstLine();
	int sc = method.getSourcePosition(start).getFirstCol();
	
	int el = method.getSourcePosition(endInst).getLastLine();
	int ec = method.getSourcePosition(endInst).getLastCol();
	
	return "[" + sl + ":" + sc + "->" + el + ":" + ec + "](" + funName + ")" + " ";
    }
    
    public String toString() {
	String str = this.startInst + ":" + this.endInst + " " + this.funName + "\n";
	for (Object o: this.outgoingEdges) {
	    Edge edge = (Edge) o;
	    str += "  ";
	    switch (edge.type) {
	    	case Edge.COND: str += "C"; break;
	    	case Edge.PAR: str += "P"; break;
	    	case Edge.NEXT: str += "N"; break;
	    	default: assert(false); break;
	    }
	    str += " " + edge.to.startInst + ":" + edge.to.endInst + " " + edge.to.funName;
	    str += "\n";
	}
	return str;
    }
}