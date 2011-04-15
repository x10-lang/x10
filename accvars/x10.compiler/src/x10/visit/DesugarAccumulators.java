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
import polyglot.types.ProcedureInstance;
import x10.types.X10FieldDef;

import x10.types.MethodInstance;
import x10.types.X10ParsedClassType_c;
import x10.types.X10ProcedureDef;
import x10.types.X10MethodDef;
import x10.types.X10ConstructorDef;
import x10.types.X10FieldDef_c;
import x10.types.X10ConstructorInstance;
import x10.types.X10ParsedClassType;
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
    private HashSet<Local> okLocals = new HashSet<Local>(); // for locals we usually desugar into: local.result(), but for okLocals we do not desugar (either for pass by reference into method calls, or LocalAssign)
    private HashSet<Node> changeCalls = new HashSet<Node>(); // for optimization: so I won't need to handle all calls
    
    public DesugarAccumulators(Job job, TypeSystem ts, NodeFactory nf) {
        super(job,ts,nf);
    }

    @Override 
    public NodeVisitor enterCall(Node n) {
        if (n instanceof X10Call || n instanceof X10New) {
            List<Expr> arguments = n instanceof X10Call ?
                    ((X10Call) n).arguments() :
                    ((X10New) n).arguments();
            ProcedureInstance<? extends ProcedureDef> mi = n instanceof X10Call ?
                    ((X10Call) n).methodInstance() :
                    ((X10New) n).constructorInstance();
            List<LocalDef> formals = mi.def().formalNames();
            int pos = 0;
            for (LocalDef li : formals) {
                if (li.flags().isAcc()) {
                    changeCalls.add(n);
                    Expr arg = arguments.get(pos);
                    Local local = (Local)arg;
                    okLocals.add(local);
                }
                pos++;
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
        return t instanceof X10ParsedClassType && ((X10ParsedClassType)t).def()==ts.Accumulator().def() ? t : // to prevent against creating Accumulator[Accumulator[...Int]]
                ts.Accumulator().typeArguments(Collections.singletonList(t));
    }
    final static Name supply = Name.make("supply");
    final static Name result = Name.make("result");
    private void changeDef(ProcedureDef def) {
        int pos = 0;
        for (LocalDef li : def.formalNames()) {
            if (li.flags().isAcc()) {
                ((Ref<Type>)li.type()).update( accType(li.type().get()) );
                Ref<? extends Type> ref = def.formalTypes().get(pos);
                ((Ref<Type>) ref).update( accType(ref.get()) );
            }
            li.setFlags(update(li.flags()));
            pos++;
        }
    }
    
    // C++ backend has problems if you capture a var in an async (so acc must be final after desugaring)
    private FlagsNode update(FlagsNode flagsNode) {
    	Flags flags = flagsNode.flags();
    	return flags.isAcc() ? flagsNode.flags( flags.set(Flags.FINAL) ) : flagsNode;
    }
    private Flags update(Flags flags) {
    	return flags.isAcc() ? flags.set(Flags.FINAL) : flags;
    }
    
    @Override
    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        if ((n instanceof X10ProcedureCall)
             && changeCalls.remove(old)) {
            X10ProcedureCall call = (X10ProcedureCall) n;
            ProcedureInstance<? extends ProcedureDef> mi = call.procedureInstance();
            List<LocalInstance> newNames = new ArrayList<LocalInstance>(mi.formalNames());
            List<Type> newTypes = new ArrayList<Type>(mi.formalTypes());
            ProcedureDef def = mi.def();
            changeDef(def);
            int pos = 0;
            for (LocalDef li : def.formalNames()) {
                if (li.flags().isAcc()) {
                    newNames.set(pos, accLocal(newNames.get(pos)));
                    newTypes.set(pos, accType(newTypes.get(pos)));
                }
                pos++;
            }
            ProcedureCall res = call.procedureInstance(mi.formalTypes(newTypes).formalNames(newNames));
            return res;

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
        } else if( n instanceof FlagsNode ) {
        	FlagsNode flagsNode = (FlagsNode) n ;
        	return update(flagsNode) ; // after desugaring, all "acc" are not final
        }
        else if (n instanceof ProcedureDecl) {
            changeDef(((ProcedureDecl)n).procedureInstance());
        }
        return n;
    }
}