package x10.barrier.analysis;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
	//System.out.println("------------" + inVisited + inVisited.asyncsRenamed);
    
      for (Object o: inVisited.outgoingEdges) {
	  renameAsync((ComposedState) ((Edge) o).to);
      }
   }
 
   public State compose(State head) {
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
		 ComposedState parStart = new ComposedState(s1.isClocked || s2.isClocked);
		

		parStart.addState(s1);
		parStart.addState(s2);
		ComposedState inVisited = this.inVisited(parStart);
		if (inVisited != null) {
		    	if (inVisited.asyncsRenamed == true)
		    	    return inVisited;
		    	this.renameAsync(inVisited);
		    	parStart.asyncsRenamed = true;
		    	/*System.out.println("s1------" + s1 + s1.getClass());
		    	System.out.println("s2------" + s2 + s2.getClass());
		    	System.out.println("s3------------ " + inVisited);
		    	System.out.println("s4------------ " + parStart);*/
		    	q.add(new Element(s1, s2, parStart));
		    	ComposedState parparStart = new ComposedState(parStart.isClocked || inVisited.isClocked);
		    	parparStart.addState(parStart);
		    	parparStart.addState(inVisited);
		    	q.add(new Element(parStart, inVisited, parparStart));
		    	parparStart.asyncsRenamed = true;
		    	this.composeAutomaton();
		    	return parparStart;
			
		}
		q.add(new Element(s1, s2, parStart));
		this.composeAutomaton();
	       return parStart;
	   
       }
       
       ComposedState s = new ComposedState(head.isClocked);
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
    
    Queue <Element> q = new LinkedList<Element>();
    
    class Element {
	State s1;
	State s2;
	ComposedState prev;
	
	public Element (State ss1, State ss2, ComposedState pprev) {
	    s1 = ss1;
	    s2 = ss2;
	    prev = pprev;
	    
	}
    };
    

    /* Automaton composition rules - implement using bfs */
    public void composeAutomaton () {

      while(q.size() > 0) {
	Element el = q.remove();
	State s1 = el.s1;
	State s2 = el.s2;
	ComposedState prev = el.prev;
	visitedStates.add(prev);
	ComposedState inVisited = null;
	 if (this.isPar(s1))
	     	s1 = this.compose(s1);
	 if (this.isPar(s2))
	     	s2 = this.compose(s2);
	 if (s1.isTerminal && s2.isTerminal)  {
	     prev.isTerminal = true;
	 }
	 else if (s1.isTerminal) {
	     
	      for (Object o2: s2.outgoingEdges) {
		
		
		  Edge e2 = (Edge) o2;
		  State ss2 = e2.to;
		  ComposedState s  = new ComposedState (s1.isClocked || ss2.isClocked);
		  s.addState(s1);
		  s.addState(ss2);
		  inVisited = this.inVisited(s);
		    if (inVisited != null)
			s = inVisited;
		    new Edge(prev, s, e2.type);
		    if (inVisited == null)
			q.add(new Element(s1, ss2, s));
		  
	      }   
	  }
	 else if (s2.isTerminal) {
	     
	      for (Object o1: s1.outgoingEdges) {
		  
		
		  Edge e1 = (Edge) o1;
		  State ss1 = e1.to;
		  ComposedState s  = new ComposedState (ss1.isClocked || s2.isClocked);
		  s.addState(ss1);
		  s.addState(s2);
		  inVisited = this.inVisited(s);
		    if (inVisited != null)
			s = inVisited;
		    new Edge(prev, s, e1.type);
		    if (inVisited == null)
			      q.add(new Element(ss1, s2, s));
		  
	      }   
	  }
	 else {

	     for (Object o1: s1.outgoingEdges)
		 for (Object o2: s2.outgoingEdges) {
		     State ss1 = null, ss2 = null;
		     ComposedState s = null; 
		
		     Edge e1 = (Edge) o1;
		     Edge e2 = (Edge) o2;
		   
		     if (e1.type == Edge.NEXT && e2.type == Edge.NEXT &&  e1.to.isClocked && e2.to.isClocked) {
			 ss1 = e1.to;
			 ss2 = e2.to;
			 s = new ComposedState(ss1.isClocked || ss2.isClocked);
			 s.addState(ss1);
			 s.addState(ss2);
			 inVisited = this.inVisited(s);
			 if (inVisited != null)
		 	 	    s = inVisited;
			 else 
			     q.add(new Element(ss1, ss2, s));
		 	 new Edge(prev, s, Edge.NEXT);
		     
		     } 
		     else if (e1.type == Edge.COND && e2.type == Edge.NEXT && e1.to.isClocked && e2.to.isClocked) {
			 ss1 = e1.to;
			 ss2 = s2;
			 s = new ComposedState (ss1.isClocked || ss2.isClocked);
			 s.addState(ss1);
			 s.addState(ss2);
		 	 inVisited = this.inVisited(s);
			 if (inVisited != null)
			     s = inVisited;
			 else
			     q.add(new Element(ss1, ss2, s));
			 new Edge(prev, s, Edge.COND);
		   
		     }
		     else if (e1.type == Edge.NEXT && e2.type == Edge.COND && e1.to.isClocked && e2.to.isClocked) {
			 ss1 = s1;
			 ss2 = e2.to;
			 s = new ComposedState (ss1.isClocked || ss2.isClocked);
			 s.addState(ss1);
			 s.addState(ss2);
			 inVisited = this.inVisited(s);
			 if (inVisited != null)
			     s = inVisited;
			 else
			     q.add(new Element(ss1, ss2, s));
			 new Edge(prev, s, Edge.COND);
		     }
		     else if ((e1.type == Edge.NEXT && e1.to.isClocked) || (e2.type == Edge.NEXT && e2.to.isClocked)) { // both are not clocked  
			 ss1 = e1.to;
			 ss2 = e2.to;
			 s = new ComposedState(s1.isClocked || ss2.isClocked);
			 s.addState(s1);
			 s.addState(ss2);
			 inVisited = this.inVisited(s);
			 if (inVisited != null)
		 	 	    s = inVisited;
			 else
			     q.add(new Element(s1, ss2, s));
		 	 new Edge(prev, s, e2.type);
		 	 
		 	 s = new ComposedState(ss1.isClocked || s2.isClocked);
			 s.addState(ss1);
			 s.addState(s2);
			 inVisited = this.inVisited(s);
			 if (inVisited != null)
		 	 	    s = inVisited;
			 else
			     q.add(new Element(ss1, s2, s));
		 	 new Edge(prev, s, e1.type);
		     
		     } else {
			 ss1 = e1.to;
			 ss2 = e2.to;
			 s = new ComposedState(ss1.isClocked || ss2.isClocked);
			 s.addState(ss1);
			 s.addState(ss2);
			 inVisited = this.inVisited(s);
			 if (inVisited != null)
		 	 	    s = inVisited;
			 else
			     q.add(new Element(ss1, ss2, s));
		 	 new Edge(prev, s, Edge.COND); 
			 
		     }
		  

		    /* System.out.print("s1: " + s1);
		     System.out.print("s2: " + s2);
		     System.out.println("s3: " + s);*/
		     
		  }
	     
	     	
	 }
      }
    }
    
    public void parallelStatementsof(State head, State state) {
	if (this.visitedLocalStates.contains(head)) 
	    return;
	this.visitedLocalStates.add(head);
	for (Object o: head.outgoingEdges) {
	    Edge e = (Edge) o;
	    if (e.type == Edge.COND) {
		for (Object oo : ((ComposedState) e.to).states) {
		    State s = (State) oo;
			    if (! (s.endInst == -1)) {
				state.addParallelBlock(s);
			        s.addParallelBlock(state);
			    }
		}
		parallelStatementsof(e.to, state);
	    }
	    
	}
	
	
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
	    this.parallelStatementsof(head, s);
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
	System.out.println("Parallel blocks of" + s.stateInsts());
	for (Object p: s.parallelBlocks)
	    System.out.println(" " + ((State) p).stateInsts());
	
    }
	for(Object o: head.outgoingEdges) {
	    printParallelBlocks(((Edge) o).to);
	}
  }
}    
