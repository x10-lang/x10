package x10.refactorings;

import static x10.refactorings.ExtractAsyncStaticTools.extractArrayName;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Expr;
import polyglot.ast.NamedVariable;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Variable;
import polyglot.types.ArrayType;
import polyglot.visit.NodeVisitor;

public abstract class PolyglotUtils {

    public static Node findNodeOfType(List<Node> path, Class clazz) {
        for(Node node : path) {
    	if (clazz.isInstance(node))
    	    return node;
        }
        return null;
    }

    public static Node findInnermostNodeOfType(List<Node> path, Class clazz) {
        Node result= null;
        for(Node node : path) {
    	if (clazz.isInstance(node))
    	    result= node;
        }
        return result;
    }

    public static Node findInnermostNodeOfTypes(List<Node> path, Class[] classes) {
        Node result= null;
        for(Node node : path) {
    	for(Class clazz: classes) {
    	    if (clazz.isInstance(node))
    		result= node;
    	}
        }
        return result;
    }

    private static int getNodeLocation(List<Stmt> stmtList, final Node n){
    	int nodeLocation = stmtList.indexOf(n);
    	if (nodeLocation == -1){
    		final ContainsNodeObserver contains_n = new ContainsNodeObserver();
    		for (int i=0; i<stmtList.size(); i++){
    			stmtList.get(i).visit(new NodeVisitor() {
    				
					@Override
					public NodeVisitor enter(Node n3) {
						if (n.equals(n3))
							contains_n.contains();

						return super.enter(n);
					}
    				
    			});
    			if (contains_n.containsNode) {
    				nodeLocation = i;
    				break;
    			}
    		}
    	}
    	return nodeLocation;
    }
    
    public static List<Stmt> splitBlockBeforeNode(Block b, Node n){
    	List<Stmt> stmtList = (List<Stmt>) b.statements();
    	int nodeLocation = getNodeLocation(stmtList, n);
    	return (nodeLocation != 0)?stmtList.subList(0, nodeLocation):new ArrayList<Stmt>();
    }

    public static List<Stmt> splitBlockAfterNode(Block b, Node n){
    	List<Stmt> stmtList = (List<Stmt>) b.statements();
    	int nodeLocation = getNodeLocation(stmtList,n);
    	return (nodeLocation != stmtList.size())?stmtList.subList(nodeLocation+1, stmtList.size()):new ArrayList<Stmt>();
    	
    }
    
    public static String extractArray(Expr e){
    	NodeVisitor arrayExtractor = new NodeVisitor() {
		    		NamedVariable arrayName;
		
					@Override
					public NodeVisitor enter(Node parent, Node n) {
						// TODO Auto-generated method stub
						if (n instanceof Variable && ((Variable)n).type() instanceof ArrayType) {
							NamedVariable extractedName = extractArrayName((Variable)n);
							if (extractedName != null)
								arrayName = extractedName;
						}
						return this;
					} 
				
					public String toString() {
						if (arrayName != null)
							return arrayName.toString();
						else
							return null;
					}
		    	};
		e.visitChildren(arrayExtractor);
    	return arrayExtractor.toString();
    }
}
