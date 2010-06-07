/**
 * 
 */
package x10.visit;

import java.util.Map;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.main.Report;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorInfo;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;
import x10.errors.Errors;

/**
 * @author vj
 *
 */
public class X10TypeChecker extends TypeChecker {

	  Map<Node,Node> memo;
	/**
	 * @param job
	 * @param ts
	 * @param nf
	 * @param memo
	 */
	public X10TypeChecker(Job job, TypeSystem ts, NodeFactory nf,
			Map<Node, Node> memo) {
		super(job, ts, nf, memo);
		this.extensionInfo = (x10.ExtensionInfo) job.extensionInfo();
		this.memo = memo;
	}
	
	x10.ExtensionInfo extensionInfo;
	
	  public Node override(Node parent, Node n) {
	    	Node n_ = memo.get(n);
	    	n_ = null;
			if (n_ != null) {
		        this.addDecls(n_);
				return n_;
			}
	    	
	        try {
	            if (Report.should_report(Report.visit, 2))
	                Report.report(2, ">> " + this + "::override " + n);
	            
	            Node m = n.del().typeCheckOverride(parent, this);
	            
	            if (m != null) {
	            	memo.put(n, m);
	            	memo.put(m, m);
	            }
	            
	            return m;
	        }
	        catch (SemanticException e) {
	        	Errors.issue(job(), e);
	            
	            // IMPORTANT: Mark the goal as failed, otherwise we may run dependent goals
	            // that depend on this pass completing successfully.
	            if (goal() != null)
	        	goal().fail();
	            
	            return n;
	        }
	    }
	 protected NodeVisitor enterCall(Node n) throws SemanticException {
		 try {
			 return super.enterCall(n);
		 } catch (SemanticException z) {
			 boolean newp = extensionInfo.errorSet().add(z);
			 if (newp)
				 throw z;
			 else throw new SemanticException();
		 }
	     
	    }
	    
	    protected Node leaveCall(Node old, final Node n, NodeVisitor v) throws SemanticException {
	    	 try {
				 return super.leaveCall(old, n, v);
			 } catch (SemanticException z) {
				 boolean newp = extensionInfo.errorSet().add(z);
				 if (newp)
					 throw z;
				
			 }
			 // continue, errors have been reported, maybe you will find more errors.
			  if (goal() != null)
		        	goal().fail();
			 return old;
	    }

}
