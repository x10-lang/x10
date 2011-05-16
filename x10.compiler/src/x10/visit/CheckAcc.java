package x10.visit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import polyglot.ast.ClassDecl;
import polyglot.ast.CodeBlock;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.Node;
import polyglot.ast.VarDecl;
import polyglot.frontend.Job;
import polyglot.types.LocalDef;
import polyglot.types.ProcedureDef;
import polyglot.types.ProcedureInstance;
import polyglot.types.SemanticException;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.Async;
import x10.ast.Finish;
import x10.ast.X10ProcedureCall;
import x10.errors.Errors;

/**
 * Checks the rules for accumulators (the "acc" keyword).
 * We create a new visitor for each CodeBlock (method, closure, field init, etc),
 * and within the code block we maintain a mapping from LocalDef->Integer
 * (the Integer denotes the number of "finish"es we crossed.
 * A formal start with 1, and a local with 0,
 * when passing a finish we increment by 1, and when leaving a finish we decrement by 1.
 * if Integer==0 we're in read-write mode, otherwise we're in write-only mode.
 * When calling a method with "acc" formal, the argument must have Integer>0.
 * Inside an async you can write-only
 * @author nathanielclinger
 * 
 */
public class CheckAcc extends NodeVisitor {
    static class LocalInfo {
        int finishCount = 0;
        int asyncCount = 0;

        @Override
        public String toString() {
            return "[finish="+finishCount+" async="+asyncCount+"]";
        }
    }
    private final Job job;
    private HashMap<LocalDef,LocalInfo> accs = new HashMap<LocalDef, LocalInfo>();
    private HashSet<Local> okLocals = new HashSet<Local>();		// for locals we usually desugar into: local.result(), but for okLocals we do not desugar (either for pass by reference into method calls, or LocalAssign)
    private int finishCount;
    private int asyncCount;
    public CheckAcc(Job job) {
        this.job = job;
    }
    int finishCount(LocalInfo info) {
        return finishCount - info.finishCount;
    }
    int asyncCount(LocalInfo info) {
        return asyncCount - info.asyncCount;
    }
    boolean canRead(LocalInfo info) {
        return finishCount(info)==0 && asyncCount(info)==0;
    }
    boolean canWrite(LocalInfo info) {
        int f = finishCount(info);
        return f >0 || (f==0 && asyncCount(info)==0); 
    }
    boolean isWriteOnly(LocalInfo info) {
        return finishCount(info)>0;
    }
    @Override 
    public NodeVisitor enter(Node n) {
        if (n instanceof CodeBlock ||
            n instanceof ClassDecl ||	// for inner classes
            n instanceof FieldDecl) {	// for field initializers of anonymous local classes
            return new CheckAcc(job);
        } else if (n instanceof Finish) {	// if its a finish, increment our finish counter
            finishCount++;
        } else if (n instanceof Async) {	// if its an async, increment our async counter
            asyncCount++;
        } else if (n instanceof VarDecl) {	// if its a variable declaration,
            VarDecl var = (VarDecl) n;
            if (var.flags().flags().isAcc()) {	// then check if it has the acc flags set
                LocalInfo info = new LocalInfo();	// if so, then now let's define its local info
                boolean isFormal = n instanceof Formal;
                assert !isFormal || (finishCount==0 && asyncCount==0);	// when we encounter a formal, finishCount must be 0.
                info.finishCount = isFormal ? -1 : finishCount;
                info.asyncCount = asyncCount;
                LocalInfo old = accs.put(var.localDef(),info);			// add acc to hash
                assert old==null : "Found previous value="+old+" for var="+var;
            }
        } else if (n instanceof X10ProcedureCall) {						// if inside a method call or construction call
            X10ProcedureCall call = (X10ProcedureCall) n;
            List<Expr> arguments = call.arguments();					// get the method call arguments
            ProcedureInstance<? extends ProcedureDef> mi = call.procedureInstance();
            List<LocalDef> formals = mi.def().formalNames();			// get a list of the formal declarations of the method arguments
            int pos = 0;
            for (LocalDef li : formals) {								// iterate through them
                if (li.flags().isAcc()) {								// check if any of them are accs
                    Expr arg = arguments.get(pos);
                    Local local = arg instanceof Local ? (Local)arg : null;	// make sure that its defined locally (and doesn't go to heap)
                    if (local!=null && local.flags().isAcc()) {				// if its a locally defined, and is acc
                        okLocals.add(local);								// add to hash set
                        // check the acc state (must be write-only)
                        LocalInfo info = accs.get(local.localInstance().def());	// check that its previously defined acc
                        if (info!=null && isWriteOnly(info)) {					// and that we are in write-only state when passed (i.e. that the method call is inclosed by a finish)
                            // ok
                        } else {
                            Errors.issue(job, new Errors.AccMostBeWriteOnly(n.position()), n);
                        }
                    } else {
                        Errors.issue(job, new Errors.CannotPassNonAccAsAcc(n.position()), n);
                    }
                    }
                pos++;
            }
        } else if (n instanceof LocalAssign) {	// if trying to assign an argument
            LocalAssign assign = (LocalAssign) n;
            Local local = assign.local();
            if (local.flags().isAcc()) {		// and its an acc
                okLocals.add(local);			// add to locals
                // when writing an acc, it must be in canWrite
                LocalInfo info = accs.get(local.localInstance().def());
                if (info==null || !canWrite(info)) {	// make sure that we can write to it
                    Errors.issue(job, new Errors.CannotWriteToAccInAsyncWithNoFinish(n.position()), n);
                }
            }
        } else if (n instanceof Local) {
            Local local = (Local) n;
            if (local.flags().isAcc() && !okLocals.remove(local)) {		// check if the local is an acc, and that its not coming from a method call
                // when reading a local, it must be in a readonly
                LocalInfo info = accs.get(local.localInstance().def());
                if (info==null || !canRead(info)) {						// make sure that we can read from it at this point
                    Errors.issue(job, new SemanticException(""), n);
                }
            }
        }
        return this;
    }
    @Override
    public Node leave(Node old, Node n, NodeVisitor v) {
        if (n instanceof Finish) {	// if leaving a finish node then decrement finish
            finishCount--;
        } else if (n instanceof Async) {	// if leaving an async node then decrement async
            asyncCount--;
        }
        return n;
    }
}