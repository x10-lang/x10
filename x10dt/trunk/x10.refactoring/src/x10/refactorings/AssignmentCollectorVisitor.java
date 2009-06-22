/**
 * 
 */
package x10.refactorings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.ast.Assign;
import polyglot.ast.Eval;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.visit.NodeVisitor;

public class AssignmentCollectorVisitor extends NodeVisitor {
    private List<Eval> fResult;

    // This should really be more robust than doing string checking
    private HashMap<String, Assign> firstUpdateByToString;

    public AssignmentCollectorVisitor() {
    	fResult = new ArrayList<Eval>();
    	firstUpdateByToString = new HashMap<String, Assign>();
    }
    
    @Override
	public NodeVisitor enter(Node n) {
    	if (n instanceof Eval) {
    		Eval n_e = (Eval)n;
    		if (n_e.expr() instanceof Assign){
    			if (!firstUpdateByToString.containsKey(((Assign)n_e.expr()).left().toString())){
    				firstUpdateByToString.put(((Assign)n_e.expr()).left().toString(), (Assign)n_e.expr());
    			}
    		}
    	}
		return super.enter(n);
	}

	@Override
    public Node leave(Node old, Node n, NodeVisitor v) {
	if (n instanceof Eval)
		if (((Eval) n).expr() instanceof Assign)
			fResult.add((Eval)n);
	return n;
    }
	
    public List<Eval> getResult() {
	return fResult;
    }
    
    public Map<String,Assign> getFirstUpdateMap() {
    	return firstUpdateByToString;
    }
}