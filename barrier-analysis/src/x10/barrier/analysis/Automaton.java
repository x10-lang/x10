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
    
    
 public boolean isPar (State head) {
     for (Object o : head.outgoingEdges) {
	   Edge e = (Edge) o;
	   if (e.type == Edge.PAR) {
	       return true;
	   }
     }
     return false;
 }
    
   public State compose(State head) {
       if(this.isPar(head)) {
	       ComposedState parStart = new ComposedState();
		int i = 0;
		State s1 = null, s2 = null; 
		for(Object oe: head.outgoingEdges) {
		    if (i == 0)
			s1 =( (Edge) oe).to;
		    else 
			s2 = ((Edge) oe).to;
		    i++;
		}
		parStart.addState(s1);
		parStart.addState(s2);
		ComposedState inVisited = this.inVisited(parStart);
		if (inVisited != null) {
		    return inVisited;
		}
		this.composeAutomaton(s1, s2, parStart);
	       return parStart;
	   
       }
       
       ComposedState s = new ComposedState();
       s.addState(head);
       ComposedState inVisited = this.inVisited(s);
       if (inVisited != null)
	    return inVisited;
       visitedStates.add(s);
       for (Object o : head.outgoingEdges) {
	   Edge e = (Edge) o;
	   State result = compose(e.to);
	   new Edge(s, result, e.type);
       }
	return s;
    }
    
    public void composePar () {
	visitedStates = new ArrayList();
        root = this.compose(root);	
	visitedStates = null;
	
	
    }
    
    public ComposedState inVisited (ComposedState s) {
	for (Object v: this.visitedStates) {
	    ComposedState vs = (ComposedState) v;
	    if (vs.isSuperSetof(s)) 
	        return vs;
	    
	}
	return null;
	    
    }
    
    public void composeAutomaton (State s1, State s2, ComposedState prev) {
	ComposedState inVisited = null;
	 visitedStates.add(prev);
	 
	 if (this.isPar(s1))
	     	s1 = this.compose(s1);
	 if (this.isPar(s2))
	     	s2 = this.compose(s2);
	 if (s1.isTerminal && s2.isTerminal)  {
	     prev.isTerminal = true;
	 }
	 else if (s1.isTerminal) {
	     
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
	 else if (s2.isTerminal) {
	     
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
		     System.out.print("s1: " + s1);
		     System.out.print("s2: " + s2);
		     ComposedState s = new ComposedState (); 
		
		     Edge e1 = (Edge) o1;
		     Edge e2 = (Edge) o2;
		     if (e1.type == Edge.COND && e2.type == Edge.COND) {
			 s1 = e1.to;
			 s2 = e2.to;
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
		     System.out.println(s);
		     if (inVisited == null)
			 this.composeAutomaton(s1, s2, s);
		     
		     }
	     	
	 }
	
    }
}
