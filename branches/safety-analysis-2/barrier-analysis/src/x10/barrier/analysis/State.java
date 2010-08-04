package x10.barrier.analysis;

import java.util.ArrayList;
import java.util.List;

class State {
    int startInst;
    int endInst;
    List outgoingEdges = new ArrayList();
    List incomingEdges = new ArrayList();
    boolean isTerminal = false;
    boolean isStart = false;
    
    public State () {
	startInst = -2;
	endInst = -2;
    }
    
    public State(int startInstruction, int endInstruction) {
	startInst = startInstruction;
	endInst = endInstruction;
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
    
    public String toString() {
	String str = this.startInst + "-" + this.endInst + "\n";
	for (Object o: this.outgoingEdges) {
	    Edge edge = (Edge) o;
	    str += "  ";
	    switch (edge.type) {
	    	case Edge.COND: str += "C"; break;
	    	case Edge.PAR: str += "P"; break;
	    	case Edge.NEXT: str += "N"; break;
	    	default: break;
	    }
	    str += " " + edge.to.startInst + "-" + edge.to.endInst;
	    str += "\n";
	}
	return str;
    }
}