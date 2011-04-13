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
import polyglot.types.LocalInstance;
import x10.types.X10FieldDef;

import x10.types.MethodInstance;
import x10.types.X10ParsedClassType_c;
import x10.types.X10ProcedureDef;
import x10.types.X10MethodDef;
import x10.types.X10ConstructorDef;
import x10.types.X10FieldDef_c;
import x10.types.X10ConstructorInstance;
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
 * Converts "acc" keywords into "Accumulator" objects.
 * For example,
 * * locals
 * acc i:Int = EXPR;
 * into
 * val i:Accumulator[Int] = new Accumulator[Int](EXPR);
 * * formals
 * def m(acc i:Int) {...}
 * into
 * def m(val i:Accumulator[Int]) {...}
 * * Local (not in method calls or LocalAssign)
 * i
 * into
 * i.result()
 * * LocalAssign
 * i = EXPR;
 * into
 * i.supply(EXPR)
 */
public class DesugarAccumulators extends ContextVisitor {
    private HashSet<Local> okLocals = new HashSet<Local>();
    private HashSet<X10Call> changeCalls = new HashSet<X10Call>();
    public DesugarAccumulators(Job job, TypeSystem ts, NodeFactory nf) {
        super(job,ts,nf);
    }

    @Override
    public Node override(Node n) {
        return super.override(n);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override public NodeVisitor enterCall(Node n) {
        if (n instanceof X10Call) {
            X10Call call = (X10Call) n;
            MethodInstance mi = call.methodInstance();
            if (mi.error()==null) {
                List<LocalDef> formals = mi.def().formalNames();
                int pos = 0;
                for (LocalDef li : formals) {
                    if (li.flags().isAcc()) {
                        changeCalls.add(call);
                        Expr arg = call.arguments().get(pos);
                        Local local = arg instanceof Local ? (Local)arg : null;
                        if (local!=null && local.flags().isAcc()) {
                            okLocals.add(local);
                        }
                    }
                    pos++;
                }
            }
        } else if (n instanceof LocalAssign) {
            LocalAssign assign = (LocalAssign) n;
            Local local = assign.local();
            if (local.flags().isAcc()) {
                okLocals.add(local);
            }
        }
        return this;
    }
    Local accLocal(Local l) {
        return (Local) l.type(accType(l.type()));
    }
    LocalInstance accLocal(LocalInstance l) {
        return (LocalInstance) l.type(accType(l.type()));
    }
    Type accType(Type t) {
        return ts.Accumulator().typeArguments(Collections.singletonList(t));
    }
    final static Name supply = Name.make("supply");
    final static Name result = Name.make("result");
    @Override
    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        if (n instanceof X10Call && changeCalls.remove(old)) {
            X10Call call = (X10Call) n;
            MethodInstance mi = call.methodInstance();
            List<LocalInstance> newNames = new ArrayList<LocalInstance>(mi.formalNames());
            List<Type> newTypes = new ArrayList<Type>(mi.formalTypes());
            if (mi.error()==null) {
                List<LocalDef> formals = mi.def().formalNames();
                int pos = 0;
                for (LocalDef li : formals) {
                    if (li.flags().isAcc()) {
                        newNames.set(pos, accLocal(newNames.get(pos)));
                        newTypes.set(pos, accType(newTypes.get(pos)));
                    }
                    pos++;
                }
                X10Call res = call.methodInstance(mi.formalTypes(newTypes).formalNames(newNames));
                return res;
            }
        } else if (n instanceof LocalAssign) {
            LocalAssign assign = (LocalAssign) n;
            Local local = assign.local();
            if (local.flags().isAcc()) {
                Expr expr = assign.right();
                // l.supply(expr)
                Position pos = n.position().markCompilerGenerated();
                Type accType = local.type();
                MethodInstance mi = ts.findMethod(accType,ts.MethodMatcher(accType,supply,Collections.singletonList(expr.type()),context));
                Expr res = nf.Call(pos, local, nf.Id(pos, supply), expr).methodInstance(mi).type((((LocalAssign)old).type()));
                return res;
            }
        } else if (n instanceof Local) {
            Local local = (Local) n;
            if (local.flags().isAcc()) {
                if (okLocals.remove(local)) {
                    return accLocal(local);
                }
                // l.result()
                Position pos = n.position().markCompilerGenerated();
                Local newlocal = accLocal(local);
                Type accType = newlocal.type();
                MethodInstance mi = ts.findMethod(accType,ts.MethodMatcher(accType,result,Collections.<Type>emptyList(),context));
                Expr res = nf.Call(pos, newlocal, nf.Id(pos, result)).methodInstance(mi).type(local.type());
                return res;
            }
        } else if (n instanceof VarDecl) {
            // removing the ACC flag from the decl (in case this phase is called again, I want to make sure we're idempotent)
            VarDecl varDecl = (VarDecl) n;
            FlagsNode flags = varDecl.flags();
            if (flags.flags().isAcc()) {
                Position pos = n.position().markCompilerGenerated();
                varDecl = varDecl.flags( flags.flags(flags.flags().set(Flags.FINAL).clear(Flags.ACC)) );
                // adding the Accumulator type
                TypeNode typeNode = varDecl.type();
                Type accGeneric = accType(typeNode.type());
                CanonicalTypeNode accNode = nf.CanonicalTypeNode(pos, accGeneric);
                varDecl = varDecl.type(accNode);
                // changing the init expr for LocalDecl
                if (varDecl instanceof LocalDecl) {
                    LocalDecl localDecl = (LocalDecl) varDecl;
                    Expr init = localDecl.init();
                    // new Accumulator[Int](init);
                    X10ConstructorInstance ci = ts.findConstructor(accGeneric, ts.ConstructorMatcher(accGeneric, Collections.singletonList(init.type()), context));
                    init = nf.New(pos,accNode,Collections.singletonList(init)).constructorInstance(ci).type(accGeneric);
                    return localDecl.init(init);
                }
                return varDecl;
            }
        }
        return n;
    }
}