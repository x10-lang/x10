/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/**
 * 
 */
package polyglot.ext.x10.visit;

import polyglot.ast.ArrayAccess;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Return;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.ast.X10ArrayAccess;
import polyglot.ext.x10.ast.X10ArrayAccess1;
import polyglot.ext.x10.ast.X10LocalDecl_c;

import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;

/**
 * @author vj
 *
 */
public class SignatureElaborator extends TypeChecker {

	/**
	 * @param job
	 * @param ts
	 * @param nf
	 */
	public SignatureElaborator(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
		// TODO Auto-generated constructor stub
	}

	public Node override(Node parent, Node n) {
		// a null return indicates that node processing is *not* being overriden.
		// a non-null value indicates overriding, with the returned node being
		// the result of running this visitor on n.
		Node result = null;
		if (parent instanceof LocalDecl) 
			return null;
		if (parent instanceof FieldDecl) 
			return result = (n instanceof TypeNode) ? null : n;
		if (parent instanceof MethodDecl) 
			return result = (n instanceof Block ) ? n : null;
		if (parent instanceof Formal) 
			return result = (n instanceof TypeNode) ? null : n;
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
       
        //Report.report(1, "SignatureElaborator: entering " + n);
        if (n instanceof ArrayAccess || n instanceof X10ArrayAccess
        		|| n instanceof X10ArrayAccess1) {
        	return n;
        }
        if (n instanceof Block)
        	return n;
        if (n instanceof Call) {
        	return n;
        }
        if (n instanceof Return) {
        	return n;
        }
      
        if (n instanceof LocalDecl) {
    		return n;
    	}
        AmbChecker ac = new AmbChecker();
        n.del().visitChildren(ac);
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
        
        return m;
    }   

}
