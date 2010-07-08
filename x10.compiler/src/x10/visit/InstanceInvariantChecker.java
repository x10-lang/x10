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
import polyglot.types.SemanticException;
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

    public Node visitEdgeNoOverride(Node parent, Node n) {
        try
        {
            if (Report.should_report("InstanceInvariantChecker", 2))
	            Report.report(2, "Checking invariants for: " + n);
            checkInvariants(n);
            n.del().visitChildren(this); // if there is an error, I don't recurse to the children

        } catch (SemanticException e) {
            String msg = e.getMessage()+("!")+
                    (" n=")+(n).toString();
            job.compiler().errorQueue().enqueue(ErrorInfo.INTERNAL_ERROR,msg,n.position());
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
    private void myAssert(boolean cond, String msg) throws SemanticException {
        if (!cond)
            throw new SemanticException(msg);
    }
    
    private void checkInvariants(Node n) throws SemanticException {
        myAssert(n != null,"Cannot visit null");

        myAssert(!isAmbiguous(n), "Ambiguous node found in AST");
        
        if (n instanceof Typed) {
            myAssert(((Typed)n).type()!=null,"Typed node is missing type");
        }

        if (n instanceof ClassMember) {
            myAssert(((ClassMember)n).memberDef()!=null,"ClassMember missing memberDef");
        }
        if (n instanceof VarDecl) {
            myAssert(((VarDecl)n).localDef()!=null,"VarDecl missing localDef");
        }

        if (n instanceof ProcedureCall) {
            myAssert(((ProcedureCall)n).procedureInstance()!=null,"ProcedureCall missing procedureInstance");
        }
        if (n instanceof NamedVariable) {
            myAssert(((NamedVariable)n).varInstance()!=null,"NamedVariable missing varInstance");
        }
        if (n instanceof FieldAssign) {
            myAssert(((FieldAssign)n).fieldInstance()!=null,"FieldAssign missing fieldInstance");
        }
        // x10 specific
        if (n instanceof Closure) {
            myAssert(((Closure)n).closureDef()!=null,"Closure missing closureDef");
        }
        if (n instanceof AssignPropertyBody) {
            myAssert(((AssignPropertyBody)n).constructorInstance()!=null,"AssignPropertyBody missing constructorInstance");
        }
        if (n instanceof SettableAssign) {
            myAssert(((SettableAssign)n).methodInstance()!=null,"SettableAssign missing methodInstance");
        }
    }
}