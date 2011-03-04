package x10.barrier.analysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import x10.util.Struct;


public class Automaton {
    State root;
    static int stateCounter = 1;

              
    public void setRoot(State state) {
	root = state;
    }    

    List visitedStates;
    List visitedLocalStates;
    
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
            if (currState.outgoingEdges.size() > 1 || currState.outgoingEdges.size() == 0 || currState.isStart) {
        	if(head == currState) return head; 	
        	head.outgoingEdges = new ArrayList();
        	for (Object o: currState.outgoingEdges) {   
        	    Edge e = (Edge) o;
        	    State outState = e.to;
        	    outState.incomingEdges.remove(currState);
        	    new Edge (head, outState, e.type);
        
        	}
        	head.endInst = lastInst;
        	return head;
            }
            for(Object o: currState.outgoingEdges) { // loops once
        	Edge e  = (Edge) o;
        	State outState = e.to;
        	if (outState.incomingEdges.size() > 1 || e.type == Edge.NEXT ||  e.type == Edge.ASYNC || outState.isTerminal) {
        	    if (e.type == Edge.ASYNC)
        		 e.type = Edge.COND;
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
    
    /* Compresses the automaton - removes unnecessary edges, groups statements into blocks */
    public void compress(State head) {
	compressStates(head);
	head.counter  = this.stateCounter ++;
	visitedStates.add(head);
	for(Object o: head.outgoingEdges) {
	   Edge e = (Edge) o;
	   if (! visitedStates.contains(e.to))
	       
		   compress(e.to);
	}

    }
    
    
    
    public boolean isBlockPresent (State s, Set c) {
	for (Object o : c) {
	    State ss = (State) o;
	    if (s.startInst >= ss.startInst && s.endInst <=  ss.endInst && (s.method.equals(ss.method)))
		   return true;
	}
	return false;
    }
    
    
    public boolean isSubOrConsecutiveBlock(State s1, State s2 ) {
	 if (((s1.startInst >= s2.startInst && s1.endInst <=  s2.endInst) ||
		(s2.startInst >= s1.startInst && s2.endInst <= s1.endInst)  ||
		(s1.startInst <= s2.endInst && s1.endInst >= s2.startInst) ||
		(s2.startInst <= s1.endInst && s2.endInst >= s1.startInst) ||
		s1.startInst == s2.endInst || s1.startInst == s2.endInst + 1 ||
		s2.startInst == s1.endInst || s2.startInst == s1.endInst + 1) 
		
	     && (s1.method.equals(s2.method))) 
		return true;
	 return false;
	
    }
    public Set mergeStates(Set p) {
	Set toRemove = new HashSet();
	Set toAdd = new HashSet();
	boolean changed = true;
	while (changed) {
	   
	  changed = false;
	  for (Object o1: p) {
	    State s1 = (State) o1;
	    for (Object o2: p)  {
		State s2 = (State) o2;
		if (toRemove.contains(s1) || toRemove.contains(s2))
		    continue;
		if (s1.equals(s2)) continue;
		if (this.isSubOrConsecutiveBlock(s1, s2)) {
			int start = Math.min (s1.startInst, s2.startInst);
		        int end = Math.max(s1.endInst, s2.endInst);
		        State s = new State (start, end, s1.funName, false, s1.method);
		        toRemove.add(s2);
		        toRemove.add(s1);
		        if (!this.isBlockPresent(s, toAdd))
		            toAdd.add(s);
		        changed = true;
		    }
			
		}   
	    }
	
	  p.removeAll(toRemove);
	  p.addAll(toAdd);
	  toAdd.clear();
      }// end while
	return p;
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
    
   /* Is there a fork at this state? */
 public boolean isPar (State head) {
     for (Object o : head.outgoingEdges) {
	   Edge e = (Edge) o;
	   if (e.type == Edge.PAR) {
	       return true;
	   }
     }
     return false;
 }
    
   public void renameAsync (ComposedState inVisited) { 
       if (inVisited.asyncsRenamed) 
	   return;

      inVisited.asyncsRenamed = true;

 
       List addList = new ArrayList ();
       List removeList = new ArrayList ();
       for (Object o: inVisited.states) {
	   State s = (State) o;
	   String funName  = s.funName;
	   if (funName.contains("async")) {
	       	State newState = s.cloneMe();
	        removeList.add(s);
	        addList.add(newState);
	       	
	   }
	   
	}
       	inVisited.states.addAll(addList);
	inVisited.states.removeAll(removeList);

 
      for (Object o: inVisited.outgoingEdges) {
	  renameAsync((ComposedState) ((Edge) o).to);
      }
   }
   
   
   public ComposedState composeWithPar(State sleft, State parState) {
         int i = 0;
	 State s1 = null, s2 = null; 

	for(Object oe: parState.outgoingEdges) {
		    if (i == 0)
			s1 =( (Edge) oe).to;
		    else 
			s2 = ((Edge) oe).to;
		    i++;
		} 
		

	
	 ComposedState compState =  composeAutomaton(s1, s2);
	 ComposedState cs = new ComposedState (sleft.isClocked || compState.isClocked);
	 cs.addState(sleft);
	 cs.addState(compState);


	 if (sleft instanceof ComposedState) { /* no repitition */
		 if(!compState.states.containsAll(((ComposedState) sleft).states))
		     return composeAutomaton(compState, sleft);
	 }
	 else if( !compState.states.contains(sleft)) /* no repitition */
	     return composeAutomaton(compState, sleft);
	 ComposedState inVisited = this.inVisitedCopy(cs);
	 if (inVisited.asyncsRenamed) /*Repitition and duplication done */ 
	     return inVisited; 
	 
	 inVisited.asyncsRenamed = true;
	 q.add(new Element (sleft, compState, inVisited, cs)); /* Queue for duplication */
	 return cs;
   }
   
   public State compose(State head) {
       ComposedState s = new ComposedState(head.isClocked);
       s.addState(head);
       ComposedState inVisited = this.inVisited(s);
       if (inVisited != null)
	    return inVisited;
       visitedStates.add(s);
       if(this.isPar(head)) {
	      
		int i = 0;
		State s1 = null, s2 = null; 
		for(Object oe: head.outgoingEdges) {
		    if (i == 0)
			s1 =( (Edge) oe).to;
		    else 
			s2 = ((Edge) oe).to;
		    i++;
		} 
		
		State child = composeAutomaton (s1, s2);
		new Edge (s, child, Edge.NEXT);
       }
       
      
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
	
	while(q.size() > 0) {
	    Element e = q.remove();
	    
	    ComposedState inVisited = e.original;
	    inVisited.asyncsRenamed = false;
	    this.renameAsync(inVisited);  
	    ComposedState s = composeAutomaton (e.s1, e.s2);
	    ComposedState ss = composeAutomaton (inVisited, s);
             new Edge (e.duplicated, ss, Edge.COND);
	    
	}
	
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
    
    public ComposedState inVisitedCopy (ComposedState s) {
	for (Object v: this.visitedStates) {
	    ComposedState vs = (ComposedState) v;
	    if (vs.isSuperSetCopyof(s)) 
	        return vs;
	    
	}
	return null;
	    
    }
    
    Queue <Element> q = new LinkedList<Element>();
    
    class Element {
	State s1;
	State s2;
	ComposedState original;
	ComposedState duplicated;
	
	public Element (State ss1, State ss2, ComposedState orig, ComposedState dup) {
	    s1 = ss1;
	    s2 = ss2;
	    original = orig;
	    duplicated = dup; 

	    
	}
    };
    

    /* Automaton composition rules - implement using dfs */
    public ComposedState composeAutomaton (State s1, State s2) {
	 ComposedState s = new ComposedState(s1.isClocked || s2.isClocked);
	 s.addState(s1);
	 s.addState(s2);
	 ComposedState inVisited = this.inVisited(s);
	 if (inVisited != null)
		return inVisited; // Already found
	 visitedStates.add(s);
	 if (this.isPar(s1)) {
	       //System.out.println("prev" + prev.stateInsts());
	     	State child =  this.composeWithPar(s2, s1);
	     	new Edge (s, child, Edge.COND);

	 }
	 else if (this.isPar(s2)) {
	     //System.out.println("prev" + prev.stateInsts());
	     	State child = composeWithPar(s1, s2);
	     	new Edge (s, child, Edge.COND);
	 }
	 
	
	 else if (s1.isTerminal && s2.isTerminal)  {
	     s.isTerminal = true;
	 }
	 else if (s1.isTerminal) {
	     
	      for (Object o2: s2.outgoingEdges) {
		
		
		  Edge e2 = (Edge) o2;
		  State ss2 = e2.to;
	
		 State child =  composeAutomaton(s1, ss2);
		    new Edge(s, child, e2.type);
			
		  
	      }   
	  }
	 else if (s2.isTerminal) {
	     
	      for (Object o1: s1.outgoingEdges) {
		  
		
		  Edge e1 = (Edge) o1;
		  State ss1 = e1.to;

		  State child =  composeAutomaton(ss1, s2);
		  new Edge(s, child, e1.type);
		  
	      }   
	  }
	 else {

	     for (Object o1: s1.outgoingEdges)
		 for (Object o2: s2.outgoingEdges) {
		     State ss1 = null, ss2 = null;
		
		     Edge e1 = (Edge) o1;
		     Edge e2 = (Edge) o2;
		   
		     if (e1.type == Edge.NEXT && e2.type == Edge.NEXT &&  e1.to.isClocked && e2.to.isClocked) {
			 ss1 = e1.to;
			 ss2 = e2.to;

			 State child =  composeAutomaton(ss1, ss2);
			 new Edge(s, child, Edge.NEXT);

		     
		     } 
		     else if (e1.type == Edge.COND && e2.type == Edge.NEXT && e1.to.isClocked && e2.to.isClocked) {
			 ss1 = e1.to;
			 ss2 = s2;

			 State child =  composeAutomaton(ss1, ss2);
			    new Edge(s, child, Edge.COND);
		   
		     }
		     else if (e1.type == Edge.NEXT && e2.type == Edge.COND && e1.to.isClocked && e2.to.isClocked) {
			 ss1 = s1;
			 ss2 = e2.to;
			 State child =  composeAutomaton(ss1, ss2);
			    new Edge(s, child, Edge.COND);
		     }
		     else { //if ((e1.type == Edge.NEXT && e1.to.isClocked) || (e2.type == Edge.NEXT && e2.to.isClocked)) { // both are not clocked  
			 ss1 = e1.to;
			 ss2 = e2.to;
			 State child =  composeAutomaton(s1, ss2);
			    new Edge(s, child, e2.type);
		 	 
			 child =  composeAutomaton(ss1, s2);
			    new Edge(s, child, e1.type);
		     
		     }
		     
		  }
	     
	     	
	 }
	 return s;

    }
    
    
    /* What blocks may happen in parallel with states of h? */
    public void mayHappenInParallel(State h) {
	ComposedState head = (ComposedState) h;
	if (visitedStates.contains(head)) 
	    return;
	
	visitedStates.add(head);
	for (Object o: head.states) {
	    visitedLocalStates = new ArrayList();
	    State s = (State) o;
	    if (s.endInst == -1)
		continue;
	    for (Object oo: head.states) {
		State ss = (State) oo;
		    if (! (ss.endInst == -1)) {
			s.addParallelBlock(ss);
			ss.addParallelBlock(s);
		    }
	    }
	    //this.parallelStatementsof(head, s);
	}
	for (Object o: head.outgoingEdges) {
	    this.mayHappenInParallel(((Edge) o).to);
	}
	
    }
    
    public void mayHappenInParallel () {
	visitedStates = new ArrayList();
	mayHappenInParallel(root);
	visitedStates = new ArrayList();
	printParallelBlocks(root);
    }
    
    public void printParallelBlocks (State head) {
	if (visitedStates.contains(head)) {
		     return;
	}
	visitedStates.add(head);
	for (Object o: ((ComposedState) head).states) {
	    
	    State s = (State) o;
	    if (visitedStates.contains(s)) {
		     continue;
	    }
	    visitedStates.add(s);

	    if (s.endInst == -1)
		continue;  
	    
	if (s.parallelBlocks.size() == 0)
	    continue;
	System.out.println("\nParallel blocks of" + s.stateInsts());
	s.parallelBlocks = mergeStates(s.parallelBlocks);
	for (Object p: s.parallelBlocks)
	    System.out.println(" " + ((State) p).stateInsts());
	
       }
	for(Object o: head.outgoingEdges) {
	    printParallelBlocks(((Edge) o).to);
	}
  }
}    
