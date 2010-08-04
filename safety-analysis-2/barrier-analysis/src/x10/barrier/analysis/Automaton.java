package x10.barrier.analysis;

import java.util.ArrayList;
import java.util.List;


public class Automaton {
    State root;

    
    public void setRoot(State state) {
	root = state;
    }    

    List visitedStates;
    public void traverse(State head) {
	visitedStates.add(head);
	System.out.println(head);
	for (Object o: head.outgoingEdges) {
	    State out = ((Edge) o).to;
	    if (!visitedStates.contains(out))
		traverse(out);
	}
	
    }
    
    public void print() {
	visitedStates = new ArrayList();
	traverse(root);
	visitedStates = null;
    }
}
