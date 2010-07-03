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
import polyglot.main.Report;
import x10.ast.AnnotationNode_c;
import x10.ast.DepParameterExpr;
import x10.ast.X10Formal_c;
import x10.ast.Closure;
import x10.ast.AssignPropertyBody;
import x10.ast.SettableAssign;

public class InstanceInvariantChecker extends NodeVisitor
{
    private Job job;

    public InstanceInvariantChecker(Job job) {
        this.job = job;
    }

    public NodeVisitor enter(Node n)
    {
        try
        {
            if (Report.should_report("InstanceInvariantChecker", 2))
	            Report.report(2, "Checking invariants for: " + n);
            checkInvariants(n);

        } catch (AssertionError e) {
            String msg = e.getMessage()+("!")+
                    (" n=")+(n).toString();
            job.compiler().errorQueue().enqueue(ErrorInfo.INTERNAL_ERROR,msg,n.position());
        }
        return this;
    }
    
    private boolean isAmbiguous(Node n){
    	if (n instanceof Ambiguous && !(n instanceof DepParameterExpr)
    			&&(!(n instanceof Assign_c) || n instanceof AmbAssign)) {
    			return true;
    	}
    	return false;
    }
    
    private void checkInvariants(Node n) {
        assert (n != null) : "Cannot visit null";

        if (isAmbiguous(n))
        	assert false : "Ambiguous node found in AST";
        
        if (n instanceof Typed) {
            assert ((Typed)n).type()!=null;
        }

        if (n instanceof ClassMember) {
            assert ((ClassMember)n).memberDef()!=null;
        }
        if (n instanceof VarDecl) {
            assert ((VarDecl)n).localDef()!=null;
        }

        if (n instanceof ProcedureCall) {
            assert ((ProcedureCall)n).procedureInstance()!=null;
        }
        if (n instanceof NamedVariable) {
            assert ((NamedVariable)n).varInstance()!=null;
        }
        if (n instanceof FieldAssign) {
            assert ((FieldAssign)n).fieldInstance()!=null;
        }
        // x10 specific
        if (n instanceof Closure) {
            assert ((Closure)n).closureDef()!=null;
        }
        if (n instanceof AssignPropertyBody) {
            assert ((AssignPropertyBody)n).constructorInstance()!=null;
        }
        if (n instanceof SettableAssign) {
            assert ((SettableAssign)n).methodInstance()!=null;
        }
    }
}