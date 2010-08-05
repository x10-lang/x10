package x10.barrier.analysis;

import java.util.ArrayList;
import java.util.List;


public class Automaton {
    State root;
    static int stateCounter = 0;

              
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

    public State compressStates(State head) {
	int firstInst = head.startInst;
	int lastInst = head.endInst;
	State currState = head;
	
        while(true) {
           // System.out.println(currState);
            if (currState.outgoingEdges.size() > 1 || currState.outgoingEdges.size() == 0) {
        	if(head == currState) return head; 	
        	head.outgoingEdges = new ArrayList();
        	for (Object o: currState.outgoingEdges) {   
        	    Edge e = (Edge) o;
        	    State outState = e.to;
        	    outState.incomingEdges.remove(currState);
        	    new Edge (head, outState, e.type);
        
        	}
        	head.endInst = lastInst;
        	head.isTerminal = currState.isTerminal;
        	return head;
            }
            for(Object o: currState.outgoingEdges) { // loops once
        	Edge e  = (Edge) o;
        	State outState = e.to;
        	if (outState.incomingEdges.size() > 1 || e.type == Edge.NEXT) {
        	    if (outState == head) 
        		return head;
        	    outState.incomingEdges.remove(currState);
        	    head.outgoingEdges = new ArrayList();
        	    new Edge (head, outState, e.type);
        	    head.endInst  = lastInst;
        	    return head;
        	}
        	lastInst = Math.max(lastInst, outState.endInst);
        	currState = outState;
	     
            }
        }

    }
    
    public void compress(State head) {
	compressStates(head);
	head.counter = this.stateCounter ++;
	visitedStates.add(head);
	for(Object o: head.outgoingEdges) {
	   Edge e = (Edge) o;
	   if (! visitedStates.contains(e.to))
	       compress(e.to);
	}

    }
    
    public void compress() {
	visitedStates = new ArrayList();
	compress(root);
	visitedStates = null;
    }
    
    public void print() {
	visitedStates = new ArrayList();
	traverse(root);
	visitedStates = null;
    }
    
    
    
    
   public State findPar(State head) {
       for (Object o : head.outgoingEdges) {
	   Edge e = (Edge) o;
	   if (e.type == Edge.PAR) 
	       return head;
       }
       visitedStates.add(head);
       for (Object o : head.outgoingEdges) {
	   Edge e = (Edge) o;
	   State result = findPar(e.to);
	   if (result != null) 
	       return result;
       }
	return null;
    }
    
    public void composePar () {
	visitedStates = new ArrayList();
	State parState = findPar(root);
	System.out.println("Par state " +  parState);
	if (parState == null)
	    return;
	visitedStates = null;
	visitedStates = new ArrayList();
	ComposedState parStart = new ComposedState();
	int i = 0;
	State s1 = null, s2 = null; 
	for(Object o: root.outgoingEdges) {
	    if (i == 0)
		s1 =( (Edge) o).to;
	    else 
		s2 = ((Edge) o).to;
	    i++;
	}
	parStart.addState(s1);
	parStart.addState(s2);
	this.composeAutomaton(s1, s2, parStart);
	visitedStates = null;
	visitedStates = new ArrayList();
	traverse(parStart);
	visitedStates = null;
	
    }
    
    public ComposedState inVisited (ComposedState s) {
	for (Object v: this.visitedStates) {
	    ComposedState vs = (ComposedState) v;
	    if (vs.isEqual(s)) 
	        return vs;
	    
	}
	return null;
	    
    }
    
    public void composeAutomaton (State s1, State s2, ComposedState prev) {
	ComposedState inVisited = null;
	 visitedStates.add(prev);
	 
	 if (s1.isTerminal) {
	     
	      for (Object o2: s2.outgoingEdges) {
		  ComposedState s  = new ComposedState ();
		
		  Edge e2 = (Edge) o2;
		  s2 = e2.to;
		  s.addState(s1);
		  s.addState(s2);
		  inVisited = this.inVisited(s);
		    if (inVisited != null)
			s = inVisited;
		    new Edge(prev, s, e2.type);
		    if (inVisited == null)
			      this.composeAutomaton(s1, s2, s);
		  
	      }   
	  }
	 if (s2.isTerminal) {
	     
	      for (Object o1: s1.outgoingEdges) {
		  ComposedState s  = new ComposedState ();
		
		  Edge e1 = (Edge) o1;
		  s1 = e1.to;
		  s.addState(s1);
		  s.addState(s2);
		  inVisited = this.inVisited(s);
		    if (inVisited != null)
			s = inVisited;
		    new Edge(prev, s, e1.type);
		    if (inVisited == null)
			      this.composeAutomaton(s1, s2, s);
		  
	      }   
	  }
	 else {

	     for (Object o1: s1.outgoingEdges)
		 for (Object o2: s2.outgoingEdges) {
		     ComposedState s  = new ComposedState ();
		
		
		     Edge e1 = (Edge) o1;
		     Edge e2 = (Edge) o2;
		     if (e1.type == Edge.COND && e2.type == Edge.COND) {
			 s1 = e1.to;
			 s2 = e1.to;
			 s.addState(s1);
		  	  s.addState(s2);
		  	  inVisited = this.inVisited(s);
		  	  if (inVisited != null)
		  	      s = inVisited;
		  	  new Edge(prev, s, Edge.COND);
		   
		    
		     }
		     else if (e1.type == Edge.NEXT && e2.type == Edge.NEXT) {
			 s1 = e1.to;
			 s2 = e2.to;
			 s.addState(s1);
			 	s.addState(s2);
			 	inVisited = this.inVisited(s);
			 	if (inVisited != null)
			 	    s = inVisited;
			 	new Edge(prev, s, Edge.NEXT);
		    
		     } 
		     else if (e1.type == Edge.COND && e2.type == Edge.NEXT) {
			 s1 = e1.to;
			 s.addState(s1);
			 s.addState(s2);
			 inVisited = this.inVisited(s);
			 if (inVisited != null)
			     s = inVisited;
			 new Edge(prev, s, Edge.COND);
		   
		     }
		     else if (e1.type == Edge.NEXT && e2.type == Edge.COND) {
			 s2 = e2.to;
			 s.addState(s1);
			 s.addState(s2);
			 inVisited = this.inVisited(s);
			 if (inVisited != null)
			     s = inVisited;
			 new Edge(prev, s, Edge.COND);
		     }
		     if (inVisited == null)
			 this.composeAutomaton(s1, s2, s);
		       }
	 }
	
    }
}
