package x10cpp;

import polyglot.ast.ArrayAccess;
import polyglot.ast.ArrayInit;
import polyglot.ast.LocalClassDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.Try;
import polyglot.frontend.Job;
import polyglot.util.ErrorInfo;
import polyglot.visit.NodeVisitor;
import x10.ast.Async;
import x10.ast.AtExpr;
import x10.ast.AtStmt;
import x10.ast.Atomic;
import x10.ast.Finish;
import x10.ast.Here;
import x10.ast.Next;
import x10.ast.SettableAssign;
import x10.ast.When;
import x10.ast.X10Loop;

/**
 * The job of this visitor is to run immediately before the
 * final C++ code generation pass and verify that no unexpected
 * kinds of ASTs are being handed to the codegen pass.
 */
public class PreCodeGenASTChecker extends NodeVisitor {
    private Job job;

    public PreCodeGenASTChecker(Job job) {
        this.job = job;
    }

    public Node visitEdgeNoOverride(Node parent, Node n) {
    	String m = isIllegalAST(n);

    	if (m!=null) {
    		String msg = "c++ codegen: "+m+("!")+(" n=")+(n).toString();
    		job.compiler().errorQueue().enqueue(ErrorInfo.INVARIANT_VIOLATION_KIND,msg,n.position());
    	} else {
    	    n.del().visitChildren(this); // if there is an error, I don't recurse to the children
        }
    	return n;
    }
    
    private String isIllegalAST(Node n) {
        if (n == null) return "Cannot visit null";

        if (n instanceof X10Loop) {
            return "X10Loop should have been expanded before codegen";
        }
        
        if (n instanceof LocalClassDecl) {
            return "LocalClasses should have been rewritten before codegen";
        }
        
        if (n instanceof Atomic || n instanceof Next || n instanceof Finish ||
                n instanceof AtExpr  || n instanceof AtStmt || n instanceof Here ||
                n instanceof When || n instanceof Async) {
            return "High-level X10 construct should have been lowered";
        }

        if (n instanceof ArrayAccess || n instanceof ArrayInit) {
            return "Unexpected Java AST";
        }
        
        if (n instanceof SettableAssign) {
            return "Settable assign should have been expanded";
        }
        
        if (n instanceof Try && ((Try) n).finallyBlock() != null) {
            return "Finally block not eliminated before codegen";
        }

        return null;
    }
}