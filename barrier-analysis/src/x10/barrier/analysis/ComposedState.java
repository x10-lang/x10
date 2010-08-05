package x10.barrier.analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class ComposedState extends State {
 
    List states = new ArrayList();

    
    public ComposedState () {

    }
   
    public void addState (State s) {
	states.add(s);
    }
    
    public boolean isEqual(ComposedState cs) {
	boolean result = true;
	Iterator<State> i, j;
	for(Object o1: cs.states) {
	    boolean res = false;
	    for (Object o2: this.states) {
		State s1 = (State) o1;
		State s2 = (State) o2;
		if (s1.isEqual(s2)) {
		    res = true;
		    break;
		}
	    }
	    result = result & res;
	}
	 return result;
    }
    
    public void addOutgoingEdge(Edge e) {
	this.outgoingEdges.add(e);
    }
    
    public void addIncomingEdge(Edge e) {
	this.incomingEdges.add(e);
    }
    
    public String toString() {
	String str = ""; // = this.startInst + "-" + this.endInst + " " + this.funName + "\n";
	for (Object o: this.outgoingEdges) {
	    Edge edge = (Edge) o;
	    str += "  ";
	    switch (edge.type) {
	    	case Edge.COND: str += "C"; break;
	    	case Edge.PAR: str += "P"; break;
	    	case Edge.NEXT: str += "N"; break;
	    	default: assert(false); break;
	    }
	    str += " " + edge.to.startInst + "-" + edge.to.endInst + " " + edge.to.funName;
	    str += "\n";
	}
	return str;
    }
}