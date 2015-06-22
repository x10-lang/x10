/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.visit;

import polyglot.ast.AmbAssign;
import polyglot.ast.Ambiguous;
import polyglot.ast.Assign_c;
import polyglot.ast.FlagsNode_c;
import polyglot.ast.Node;
import polyglot.ast.LocalDecl;
import polyglot.ast.FieldDecl;
import polyglot.ast.Typed;
import polyglot.ast.ClassMember;
import polyglot.ast.VarDecl;
import polyglot.ast.ProcedureCall;
import polyglot.ast.NamedVariable;
import polyglot.ast.FieldAssign;
import polyglot.util.Position;
import polyglot.util.ErrorInfo;
import polyglot.visit.NodeVisitor;
import polyglot.frontend.Job;
import polyglot.main.Reporter;
import polyglot.types.SemanticException;
import x10.ast.AnnotationNode_c;
import x10.ast.AssignPropertyCall;
import x10.ast.DepParameterExpr;
import x10.ast.X10Formal_c;
import x10.ast.Closure;
import x10.ast.SettableAssign;

public class InstanceInvariantChecker extends NodeVisitor
{
    private Job job;
    private Reporter reporter;

    public InstanceInvariantChecker(Job job) {
        this.job = job;
        this.reporter = job.extensionInfo().getOptions().reporter;
    }

    @Override
    public Node visitEdgeNoOverride(Node parent, Node n) {
    	if (reporter.should_report(Reporter.InstanceInvariantChecker, 2))
    		reporter.report(2, "Checking invariants for: " + n);
    	String m = checkInvariants(n);

    	if (m!=null) {
    		String msg = m+("!")+(" n=")+(n).toString();
    		job.compiler().errorQueue().enqueue(ErrorInfo.INVARIANT_VIOLATION_KIND,msg,n.position());
    	} else {
    	    n.del().visitChildren(this); // if there is an error, I don't recurse to the children
        }
    	return n;
    }
    
    private boolean isAmbiguous(Node n){
    	if (n instanceof Ambiguous && !(n instanceof DepParameterExpr)
    			&&(!(n instanceof Assign_c) || n instanceof AmbAssign)) {
    			return true;
    	}
    	return false;
    }
    
    private String checkInvariants(Node n) {
        if (n == null) return "Cannot visit null";

        if (isAmbiguous(n))
            return "Ambiguous node found in AST";

        if (n instanceof Typed) {
            if (((Typed)n).type()==null)
                return "Typed node is missing type";
        }
        if (n instanceof ClassMember) {
            if (((ClassMember)n).memberDef()==null)
                return "ClassMember missing memberDef";
        }
        if (n instanceof VarDecl) {
            if (((VarDecl)n).localDef()==null)
                return "VarDecl missing localDef";
        }
        if (n instanceof ProcedureCall) {
            if (((ProcedureCall)n).procedureInstance()==null)
                return "ProcedureCall missing procedureInstance";
        }
        if (n instanceof NamedVariable) {
            if (((NamedVariable)n).varInstance()==null)
                return "NamedVariable missing varInstance";
        }
        if (n instanceof FieldAssign) {
            if (((FieldAssign)n).fieldInstance()==null)
                return "FieldAssign missing fieldInstance";
        }
        // x10 specific
        if (n instanceof Closure) {
            if (((Closure)n).closureDef()==null)
                return "Closure missing closureDef";
        }
        if (n instanceof SettableAssign) {
            if (((SettableAssign)n).methodInstance()==null)
                return "SettableAssign missing methodInstance";
        }
        if (n instanceof AssignPropertyCall) {
            if (((AssignPropertyCall)n).properties()==null)
                return "AssignPropertyCall missing properties";
        }
        return null;
    }
}