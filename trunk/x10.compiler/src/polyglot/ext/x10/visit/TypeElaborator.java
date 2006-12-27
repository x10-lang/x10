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

import polyglot.ast.Formal;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ast.VarDecl;
import polyglot.ext.x10.ast.X10LocalDecl_c;
import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.ast.X10Special_c;
import polyglot.ext.x10.ast.X10VarDecl;
import polyglot.ext.x10.types.X10Context;
import polyglot.frontend.Job;
import polyglot.types.Context;
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.types.VarInstance;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;


/**
 * TypeElaborator is a modified TypeChecker pass that is run before the main TypeChecker pass.
 * It typecheck (only) the expressions occurring in types. 
 * 
 *
 * @author vj, nystrom
 *
 */
public class TypeElaborator extends TypeChecker {
	
	public TypeElaborator(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
	}
	boolean elaborate;
	
	@Override
	public NodeVisitor enterCall(Node parent, Node n) throws SemanticException {
		NodeVisitor v = super.enterCall(parent, n);
		if (v instanceof TypeElaborator) {
			TypeElaborator te = (TypeElaborator) v;
			if (n instanceof TypeNode) {
				TypeElaborator v2 = (TypeElaborator) this.copy();
				v2.elaborate = true;
				return v2;
			}
			
		}
		return v;
	}
	@Override
	protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        if (n instanceof X10VarDecl) {
    		X10VarDecl result = (X10VarDecl) n;
    		n= result.pickUpTypeFromTypeNode(this);
    		
    	}
        if (elaborate || n instanceof TypeNode) {
        	if (n instanceof Local && v instanceof TypeElaborator) {
        		// Replace occurrences of l with self, where l is the variable being defined.
        		Local nl = (Local) n;
        		TypeElaborator te = (TypeElaborator) v;
        		X10Context ctx = (X10Context) te.context();
        		VarInstance vi = ctx.varWhoseTypeIsBeingElaborated();
        		nl = (Local) super.leaveCall(old, n, v);
        		if (vi != null && vi.equals(nl.localInstance())) {
        			n = X10Special_c.self(n.position());
        		} else {
        			return nl;
        		}
        	}
        	return super.leaveCall(old, n, v);
        } 
        return n;
    }   
	
	

}
