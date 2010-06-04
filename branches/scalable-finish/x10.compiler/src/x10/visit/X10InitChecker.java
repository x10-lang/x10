/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.visit;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.visit.InitChecker;
import polyglot.visit.NodeVisitor;
import x10.ast.X10ClassDecl;

public class X10InitChecker extends InitChecker {

    public X10InitChecker(Job job, TypeSystem ts, NodeFactory nf) {
	super(job, ts, nf);
    }

    /**
     * Overridden superclass method.
     * 
     * Set up the state that must be tracked during a Class Declaration.
     */
    protected NodeVisitor enterCall(Node parent, Node n) throws SemanticException {
        if (n instanceof ClassBody) {
            // we are starting to process a class declaration, but have yet
            // to do any of the dataflow analysis.
            ClassBody cb = (ClassBody) n;
            
            // Add the properties to the class body when initializing.
	    if (parent instanceof X10ClassDecl) {
                List<ClassMember> members;
                members = new ArrayList<ClassMember>();
                members.addAll(cb.members());
                members.addAll(((X10ClassDecl) parent).properties());
                cb = cb.members(members);
            }
	    
	    // set up the new ClassBodyInfo, and make sure that it forms
	    // a stack.
	    ClassDef ct = null;
            if (parent instanceof ClassDecl) {
        	ct = ((ClassDecl) parent).classDef();
            }
            else if (parent instanceof New) {
                ct = ((New) parent).anonType();
            }
            if (ct == null) {
                throw new InternalCompilerError("ClassBody found but cannot find the class.", n.position());
            }
            
            setupClassBody(ct, cb);
        }
      
        return super.enterCall(n);
    }

}
