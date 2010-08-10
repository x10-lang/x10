package x10.barrier.analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class ComposedState extends State {
 
    List states = new ArrayList();
    boolean asyncsRenamed = false;

    
    public ComposedState () {
	this.counter = Automaton.stateCounter++;
    }
   
    public void addState (State s) {
	   if (s instanceof ComposedState) {
	       for (Object o: ((ComposedState) s).states) {
		   	this.addState((State)o);
	       }
	   } else
	    states.add(s);
    }
    

    
    public boolean isSuperSetof (ComposedState cs) { 
	boolean result = true;
	 // this is visited;'l
	for(Object o1: cs.states) {
	    boolean res = false;
	    for (Object o2: this.states) {
		State s1 = (State) o1;
		State s2 = (State) o2;
		if (s1.isEqual(s2)) {
		    res = true;
		   
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
    
    public String stateInsts() {
	String str = " ";
	for (Object o: this.states ) {
	    State s = (State) o;
	    str += s.stateInsts();
	}
	return str;
    }
    
    public String toString() {
	String str = this.stateInsts() + "\n";
	for (Object o: this.outgoingEdges) {
	    Edge edge = (Edge) o;
	    str += "  ";
	    switch (edge.type) {
	    	case Edge.COND: str += "C"; break;
	    	case Edge.PAR: str += "P"; break;
	    	case Edge.NEXT: str += "N"; break;
	    	default: assert(false); break;
	    }
	    str += edge.to.stateInsts();
	    str += "\n";
	}
	str += "\n";
	return str;
    }
}