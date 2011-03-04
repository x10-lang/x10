package x10.barrier.analysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ibm.wala.cast.loader.AstMethod;
import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;

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
    
    public State(int startInstruction, int endInstruction, String funcName, boolean clocked, AstMethod m) {
	startInst = startInstruction;
	endInst = endInstruction;
	funName = funcName;
	isClocked = clocked;
	method = m;
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
	if (this.isEqual(s)) 
	    return;
	for (Object o: this.parallelBlocks) {
	    if (s.isEqual((State) o))
		return;
	}
	this.parallelBlocks.add(s);
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
      String str = "[" + this.startInst + ":" + this.endInst + "](" + funName + ")" + " " + " --- "; //+ this.counter;
	/*int start, end;
	if (startInst == 0)
	    start = 1;
	else start = startInst;
         Set sourcePosSet = new HashSet();
	for (int i = start; i <= endInst; i++) {
	    Position sourcePos = method.getSourcePosition(i);
	    if (sourcePosSet.contains(sourcePos))
		continue;
	    sourcePosSet.add(sourcePos);
	    int sl = sourcePos.getFirstLine();
	    int sc = sourcePos.getFirstCol();
	
	    int el = sourcePos.getLastLine();
	    int ec = sourcePos.getLastCol();
	
	     str +=  "[" + sl + ":" + sc + "->" + el + ":" + ec + "]" + "    ";
	}*/
	return str;
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