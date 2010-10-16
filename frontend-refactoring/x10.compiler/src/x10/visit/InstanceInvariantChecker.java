package x10.visit;

import polyglot.util.Position;
import polyglot.util.ErrorInfo;
import polyglot.visit.NodeVisitor;
import polyglot.frontend.Job;
import polyglot.main.Report;
import x10.ast.AmbAssign;
import x10.ast.Ambiguous;
import x10.ast.AnnotationNode_c;
import x10.ast.AssignPropertyCall;
import x10.ast.Assign_c;
import x10.ast.ClassMember;
import x10.ast.DepParameterExpr;
import x10.ast.FieldAssign;
import x10.ast.FieldDecl;
import x10.ast.FlagsNode_c;
import x10.ast.LocalDecl;
import x10.ast.NamedVariable;
import x10.ast.Node;
import x10.ast.ProcedureCall;
import x10.ast.Typed;
import x10.ast.VarDecl;
import x10.ast.X10Formal_c;
import x10.ast.Closure;
import x10.ast.SettableAssign;
import x10.errors.X10ErrorInfo;
import x10.types.SemanticException;

public class InstanceInvariantChecker extends NodeVisitor
{
    private Job job;

    public InstanceInvariantChecker(Job job) {
        this.job = job;
    }

    public Node visitEdgeNoOverride(Node parent, Node n) {
    	if (Report.should_report("InstanceInvariantChecker", 2))
    		Report.report(2, "Checking invariants for: " + n);
    	String m = checkInvariants(n);

    	if (m!=null) {
    		String msg = m+("!")+(" n=")+(n).toString();
    		job.compiler().errorQueue().enqueue(X10ErrorInfo.INVARIANT_VIOLATION_KIND,msg,n.position());
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