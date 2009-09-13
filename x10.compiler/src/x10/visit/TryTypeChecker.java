package x10.visit;

import polyglot.ast.Node;
import polyglot.main.Report;
import polyglot.types.SemanticException;
import polyglot.util.ErrorInfo;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ErrorHandlingVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;

/**
 * A TypeChecker that has a parallel try* chain that lets exceptions through.
 * 
 * In particular, clients may be interested in invoking tryVisitEdge as a substitute
 * for visitEdge, the entry point into the visitor for visiting a child from a parent.
 * 
 * Of use in implementing constructor invocations for structs in X10 2.0.
 * 
 * Note that internally, this visitor will visit nodes, rather than tryVisit them. This is 
 * inevitable because to really make this work we need the tryVisit defined on Node_c which 
 * it is not.
 * 
 * Therefore, some errors may already be reported in the errorQueue before
 * SemanticException is thrown.
 * 
 * 
 * @author vj
 *
 */
public class TryTypeChecker extends TypeChecker implements TryVisitorI {
	public TryTypeChecker(TypeChecker tc) {
		super(tc.job(), tc.typeSystem(), tc.nodeFactory(), tc.job().nodeMemo());
		context = tc.context();
	}

	
	/**
	 * Same as override, but let exceptions flow through.
	 * @param parent
	 * @param n
	 * @return
	 * @throws SemanticException
	 */
	
	public Node tryOverride(Node parent, Node n) throws SemanticException {
		//Node n_ = memo.get(n);
		Node n_ = n;
		

		// may throw SemanticExceptions, hence does not need a try* variant.
		Node m = n.del().typeCheckOverride(parent, this);


		return m;
	}
	
	  public Node tryVisitEdge(Node parent, Node child) throws SemanticException {
	        try {
	            Node n = tryOverride(parent, child);

	            if (n == null) {
	                return tryVisitEdgeNoOverride(parent, child);
	            }
	            
	            return n;
	        }
	        catch (InternalCompilerError e) {
	            if (e.position() == null && child != null)
	                e.setPosition(child.position());
	            throw e;
	        }
	    }
	  
	  public Node tryVisitEdgeNoOverride(Node parent, Node child) throws SemanticException {
		
	        if (child == null) {
	            return null;
	        }

	        // Cannot throw SemanticExceptions even internally.
	        NodeVisitor v_ = enter(parent, child);
	        
	        if (v_ == null) {
	            throw new InternalCompilerError("TryTypeChecker.enter() returned null.");
	        }

	        Node n = child.del().visitChildren(v_);
		    
	        if (n == null) {
	            throw new InternalCompilerError("TryTypeChecker.visitChildren() returned null.");
	        }
	        
	        try {
	            n = this.tryLeave(parent, child, n, v_);
	        }
	        catch (InternalCompilerError e) {
	            if (e.position() == null && n != null)
	                e.setPosition(n.position());
	            throw e;
	        }
	        
	        return n;
	    }
	  
	  public Node tryLeave(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
		  // Ignore error processing performed by the real ErrorHandler. Dont know how to 
		  // handle that here.
	            return leaveCall(old, n, v);
	    }
	
}

