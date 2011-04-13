package x10.visit;

import polyglot.ast.*;
import polyglot.util.ErrorInfo;
import polyglot.util.Position;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.visit.NodeVisitor;
import polyglot.visit.DataFlow;
import polyglot.visit.FlowGraph;
import polyglot.visit.ContextVisitor;
import polyglot.visit.InitChecker;
import polyglot.visit.DataFlow.Item;
import polyglot.visit.FlowGraph.EdgeKey;
import polyglot.frontend.Job;
import polyglot.types.Flags;
import polyglot.types.Type;
import polyglot.types.FieldDef;
import polyglot.types.MethodDef;
import polyglot.types.FieldInstance;
import polyglot.types.ProcedureDef;
import polyglot.types.QName;
import polyglot.types.ClassDef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.Context;
import polyglot.types.ProcedureDef_c;
import polyglot.types.Types;
import x10.ast.*;
import x10.errors.Errors;
import polyglot.types.TypeSystem;
import polyglot.types.VarDef;
import polyglot.types.LocalDef;
import polyglot.types.ClassType;
import polyglot.types.ContainerType;
import polyglot.types.ProcedureInstance;
import x10.types.X10FieldDef;

import x10.types.MethodInstance;
import x10.types.X10ParsedClassType_c;
import x10.types.X10ProcedureDef;
import x10.types.X10MethodDef;
import x10.types.X10ConstructorDef;
import x10.types.X10FieldDef_c;
import x10.types.checker.ThisChecker;
import x10.util.Synthesizer;

import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Collections;
import static polyglot.visit.InitChecker.*;

/**
 * Checks the rules for accumulators (the "acc" keyword).
 * We create a new visitor for each CodeBlock (method, closure, field init, etc),
 * and within the code block we maintain a mapping from LocalDef->Integer
 * (the Integer denotes the number of "finish"es we crossed.
 * A formal start with 1, and a local with 0,
 * when passing a finish we increment by 1, and when leaving a finish we decrement by 1.
 * if Integer==0 we're in read-write mode, otherwise we're in write-only mode.
 * When calling a method with "acc" formal, the argument must have Integer>0.
 * Inside an async you can only 
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
    private HashSet<Local> okLocals = new HashSet<Local>();
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
    @Override public NodeVisitor enter(Node n) {
        if (n instanceof CodeBlock ||
            n instanceof ClassDecl || // for inner classes
            n instanceof FieldDecl) {  // for field initializers of anonymous local classes
            return new CheckAcc(job);
        } else if (n instanceof Finish) {
            finishCount++;
        } else if (n instanceof Async) {
            asyncCount++;
        } else if (n instanceof VarDecl) {
            VarDecl var = (VarDecl) n;
            if (var.flags().flags().isAcc()) {
                LocalInfo info = new LocalInfo();
                boolean isFormal = n instanceof Formal;
                assert !isFormal || (finishCount==0 && asyncCount==0); // when we encounter a formal, finishCount must be 0.
                info.finishCount = isFormal ? -1 : finishCount;
                info.asyncCount = asyncCount;
                LocalInfo old = accs.put(var.localDef(),info);
                assert old==null : "Found previous value="+old+" for var="+var;
            }
        } else if (n instanceof X10ProcedureCall) {
            X10ProcedureCall call = (X10ProcedureCall) n;
            List<Expr> arguments = call.arguments();
            ProcedureInstance<? extends ProcedureDef> mi = call.procedureInstance();
            List<LocalDef> formals = mi.def().formalNames();
            int pos = 0;
            for (LocalDef li : formals) {
                if (li.flags().isAcc()) {
                    Expr arg = arguments.get(pos);
                    Local local = arg instanceof Local ? (Local)arg : null;
                    if (local!=null && local.flags().isAcc()) {
                        okLocals.add(local);
                        // check the acc state (must be write-only)
                        LocalInfo info = accs.get(local.localInstance().def());
                        if (info!=null && isWriteOnly(info)) {
                            // ok
                        } else {
                            Errors.issue(job, new SemanticException("When passing an accumulator as a method argument it must be in a write-only state."), n);
                        }
                    } else {
                        Errors.issue(job, new SemanticException("Cannot pass a non-accumulator argument in the position of an accumulator formal."), n);
                    }
                }
                pos++;
            }
        } else if (n instanceof LocalAssign) {
            LocalAssign assign = (LocalAssign) n;
            Local local = assign.local();
            if (local.flags().isAcc()) {
                okLocals.add(local);
                // when writing an acc, it must be in canWrite
                LocalInfo info = accs.get(local.localInstance().def());
                if (info==null || !canWrite(info)) {
                    Errors.issue(job, new SemanticException("Cannot write to an accumulator in an async that is not enclosed by a finish."), n);
                }
            }
        } else if (n instanceof Local) {
            Local local = (Local) n;
            if (local.flags().isAcc() && !okLocals.remove(local)) {
                // when reading a local, it must be in a readonly
                LocalInfo info = accs.get(local.localInstance().def());
                if (info==null || !canRead(info)) {
                    Errors.issue(job, new SemanticException("Cannot read from an accumulator in write-only state or inside an async."), n);
                }
            }
        }
        return this;
    }
    @Override
    public Node leave(Node old, Node n, NodeVisitor v) {
        if (n instanceof Finish) {
            finishCount--;
        } else if (n instanceof Async) {
            asyncCount--;
        }
        return n;
    }
}