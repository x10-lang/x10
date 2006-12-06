/**
 * 
 */
package polyglot.ext.x10.visit;

import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.ast.X10LocalDecl_c;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;


/**
 * TypeElaborator is a modified TypeChecker pass that is run before the main TypeChecker pass. Its job
 * is to typecheck the expressions occurring in types. 
 * 
 *
 * @author vj
 *
 */
public class TypeElaborator extends TypeChecker {
	
	public TypeElaborator(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
	}
	boolean inTypeNode;
	
	@Override
	public NodeVisitor enterCall(Node parent, Node n) throws SemanticException {
		NodeVisitor v = super.enterCall(parent, n);
		if (v instanceof TypeElaborator && n instanceof TypeNode) {
			TypeElaborator v2 = (TypeElaborator) this.copy();
			v2.inTypeNode = true;
			return v2;
		}
		return v;
	}
	protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
       // Report.report(1, "TypeElaborator: entering " + n);
        if (n instanceof LocalDecl) {
    		X10LocalDecl_c result = (X10LocalDecl_c) n;
    		result.pickUpTypeFromTypeNode(this);
    	}
        //return super.leaveCall(old, n, v);
        if (inTypeNode || n instanceof TypeNode) {
        	return super.leaveCall(old, n, v);
        } 
        return n;
    }   

}
