/**
 * 
 */
package polyglot.ext.x10.visit;

import polyglot.ast.ArrayAccess;
import polyglot.ast.Assign;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Return;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ast.VarDecl;
import polyglot.ext.x10.ast.X10ArrayAccess;
import polyglot.ext.x10.ast.X10ArrayAccess1;
import polyglot.ext.x10.ast.X10Binary_c;
import polyglot.ext.x10.ast.X10Call_c;
import polyglot.ext.x10.ast.X10ClockedLoop;
import polyglot.ext.x10.ast.X10LocalDecl_c;
import polyglot.frontend.Job;
import polyglot.frontend.goals.Goal;
import polyglot.main.Report;
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;


/**
 * TypeElaborator is a modified TypeChecker pass that is run before the main TypeChecker pass. Its job
 * is to typecheck the expressions occurring in types. It does a normal TypeChecker pass, except that
 * it does not typecheck method calls. Recall that invocations of binary operators, such as ==, are
 * not treated as method calls. Therefore the bodies of depclauses, which can contain only calls to
 * unary and binary operators, in the EQ constraint system are typechecked. 
 * 
 * Also to be typechecked are final field declarations, formal parameters and variables. 
 * However, when typechecking them, do not typecheck their initializers. We simply wish to 
 * get the correct VarInstance and FieldInstance identified with the variable/field. 
 * 
 *
 * @author vj
 *
 */
public class TypeElaborator extends TypeChecker {
	
	public TypeElaborator(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
		
		
	}
	public Node override(Node parent, Node n) {
		// a null return indicates that node processing is *not* being overriden.
		// a non-null value indicates overriding, with the returned node being
		// the result of running this visitor on n.
		Node result = null;
		if (parent instanceof LocalDecl) 
			return result = (n instanceof TypeNode) ? null : n;
		if (parent instanceof FieldDecl) 
			return result = (n instanceof TypeNode) ? null : n;
		if (parent instanceof Formal) 
			return result = (n instanceof TypeNode) ? null : n;
		//if (parent instanceof X10ClockedLoop)
		//	return result = (n instanceof Formal || n instanceof Stmt) ? null : n;
		
		// Bypass all nodes which cannot have a type declaration under them.
		
		  
		// Otherwise, proceed as usual.
        return result = super.override(parent, n);
		
    }
 
	protected static class AmbChecker extends NodeVisitor {
        boolean amb;
        
        public Node override(Node n) {   
            if (! n.isDisambiguated() || ! n.isTypeChecked()) {
              //  Report.report(3, "  !!!!! no type at " + n + " (" + n.getClass().getName() + ")");
              // if (n instanceof Expr)  
               //     Report.report(3, "   !!!! " + n  + ".type = " + ((Expr) n).type());
                amb = true;
            }
            return n;
        }
    }
	protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
       
       // Report.report(1, "TypeElaborator: entering " + n);
        if (n instanceof ArrayAccess || n instanceof X10ArrayAccess
        		|| n instanceof X10ArrayAccess1) {
        	return n;
        }
       
        if (n instanceof Call) {
        	return n;
        }
        if (n instanceof Return) {
        	return n;
        }
      
        if (n instanceof LocalDecl) {
    		X10LocalDecl_c result = (X10LocalDecl_c) n;
    		result.pickUpTypeFromTypeNode(this);
    	}
        AmbChecker ac = new AmbChecker();
        n.visitChildren(ac);
        Node m = n;
        boolean  disambiguated = ! ac.amb && m.isDisambiguated();
        if (disambiguated ) {
        //  Report.report(3, "running typeCheck for " + m);
            m = m.del().typeCheck((TypeChecker) v);
            
        }
       // else do nothing. 
        else {
        	
              //  Report.report(1, "TypeElaborator.leaveCall:  no type at " + m + "(#"+ m.hashCode() 
              // 		 + ")" + ac.amb + " " + m.isDisambiguated());
            //Goal g = job.extensionInfo().scheduler().currentGoal();
           // g.setUnreachableThisRun();
        }
        //Report.report(1, "TypeElaborator: leaving " + n + "==>" + m);
        return m;
    }   

}
