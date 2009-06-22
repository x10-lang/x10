/**
 * 
 */
package x10.refactorings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.ast.Assign;
import polyglot.ast.Node;
import polyglot.visit.NodeVisitor;

public class AssignmentCollectorVisitor extends NodeVisitor {
    private List<Assign> fResult;

    // This should really be more robust than doing string checking
    private HashMap<String, Assign> firstUpdateByToString;

    public AssignmentCollectorVisitor() {
    	fResult = new ArrayList<Assign>();
    	firstUpdateByToString = new HashMap<String, Assign>();
    }
    
    @Override
	public NodeVisitor enter(Node n) {
    	if (n instanceof Assign) {
    		if (!firstUpdateByToString.containsKey(((Assign)n).left().toString())){
    			firstUpdateByToString.put(((Assign)n).left().toString(), (Assign)n);
    		}
    	}
		return super.enter(n);
	}

	@Override
    public Node leave(Node old, Node n, NodeVisitor v) {
	if (n instanceof Assign)
	    fResult.add((Assign)n);
	return n;
    }
	
    public List<Assign> getResult() {
	return fResult;
    }
    
    public Map<String,Assign> getFirstUpdateMap() {
    	return firstUpdateByToString;
    }
}