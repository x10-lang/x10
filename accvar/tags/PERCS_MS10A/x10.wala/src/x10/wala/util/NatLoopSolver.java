package x10.wala.util;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

import com.ibm.wala.cfg.ControlFlowGraph;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.util.graph.dominators.NumberedDominators;


public class NatLoopSolver {
    /**
     * given a control flow graph and two nodes in the graph, return
     * a list of nodes that forms a loop. In cfg, "start" dominates "end".
     * @param start of the loop
     * @param end of the loop
     * @param control flow graph
     * @return a loop starts from "start" and ends at "end".
     */
    public static NatLoop findOneLoop(ISSABasicBlock start, ISSABasicBlock end, 
	    ControlFlowGraph<SSAInstruction,ISSABasicBlock> cfg){
	Stack<ISSABasicBlock> st = new Stack<ISSABasicBlock>();
	NatLoop loop = new NatLoop(start,end);
	ISSABasicBlock p,q;
	if(start.getNumber()!=end.getNumber()){
	    st.push(end);
	}
	while(!st.isEmpty()){

	    p = st.pop();
	    Iterator<ISSABasicBlock> it = cfg.getPredNodes(p);
	    while (it.hasNext()) {
		q = it.next();
		if(!loop.isInLoop(q)){
		    loop.addNode(q);
		    st.push(q);
		}
	    }
	}
	return loop;
	
    }
    /**
     * findAllLoops does a depth-first search on cfg, when it sees a "back edge" it calls
     * findOneLoop to find all nodes belongs to this natural loop. Finally, it returns a
     * set of loops in this cfg.
     * 
     * @param cfg is the control flow graph
     * @param dom is a set of "domination-relationship" of all nodes in cfg
     * @param loops contains all loops found in cfg as the return value
     * @param visited contains nodes that have been visited in cfg
     * @param cur is the number of current node to be visited
     */
    public static void findAllLoops(ControlFlowGraph<SSAInstruction,ISSABasicBlock> cfg, 
	    NumberedDominators<ISSABasicBlock> dom, HashSet<NatLoop> loops, HashSet<Integer> visited, ISSABasicBlock curblk){
	 
        //DFS
       	assert(curblk!=null);
       	Iterator<ISSABasicBlock> it = cfg.getSuccNodes(curblk);
       	while (it.hasNext()) {
       		ISSABasicBlock nextblk = it.next();
       		if(dom.isDominatedBy(curblk, nextblk)){
		    NatLoop nl = findOneLoop(nextblk,curblk,cfg);
		    assert(nl!=null);
		    loops.add(nl);  
		}
       		if (!visited.contains(Integer.valueOf(nextblk.getNumber()))) {
       		    	visited.add(Integer.valueOf(nextblk.getNumber()));	
       			findAllLoops(cfg,dom,loops,visited,nextblk);    
        	}
       	}	
    }
    
    /**
     * 
     * @param loops: the set of loops found in a control flow graph
     * @param n: block(node of a cfg) number
     * @return a set of all loops that contain this block n
     */
    public static HashSet<NatLoop> getLoops(HashSet<NatLoop> loops, ISSABasicBlock n){
	HashSet<NatLoop> targets = new HashSet<NatLoop>();
	Iterator<NatLoop> it = loops.iterator();
	while(it.hasNext()){
	    NatLoop l = it.next();
	    if(l.isInLoop(n)){
		targets.add(l);
	    }
	}
	return targets;
    }
    
    public static boolean containsNode(HashSet<NatLoop> loops, ISSABasicBlock n){
	HashSet<NatLoop> targets = getLoops(loops,n);
	if(targets.size()>0){
	    return true;
	}
	return false;
    }
    /**
     * print all loops in a set in the format of "#id loop[start,end]:	{all nodes of this loop}" 
     * For example, we might see "1 loop[2,9]:	{2 3 8 9}".
     * 
     * @param loops to be printed
     */
    public static void printAllLoops(HashSet<NatLoop> loops){
	Iterator<NatLoop> it = loops.iterator();
	int cnt = 1;
	while(it.hasNext()){
	    NatLoop l = it.next();
	    System.out.println(cnt+"\tloop["+l.getStart().getNumber()+","+l.getEnd().getNumber()+"]:\t{"+l.toString()+"}");
	    cnt++;
	}
    }
  
}
