package x10.visit;

import polyglot.ast.*;
import polyglot.util.ErrorInfo;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import polyglot.visit.DataFlow;
import polyglot.visit.FlowGraph;
import polyglot.frontend.Job;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.VarDef;
import polyglot.types.MethodDef;
import polyglot.types.FieldInstance;
import polyglot.types.ProcedureDef;
import x10.ast.*;
import x10.types.X10TypeMixin;
import x10.types.X10Flags;
import x10.types.X10FieldDef_c;
import x10.types.X10TypeSystem;
import x10.types.X10FieldDef;

import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;

public class CheckEscapingThis extends NodeVisitor
{
    // does one iteration in our fixed-point method analysis
    // I use the first 3 bits of an integer to represent the 3 flags: read, write and seqWrite
    private static final int READ = 1;
    private static final int WRITE = 2;
    private static final int SEQ_WRITE = 4;
    private static boolean isRead(int i) { return (i&READ)==READ; }
    private static boolean isWrite(int i) { return (i&WRITE)==WRITE; }
    private static boolean isSeqWrite(int i) { return (i&SEQ_WRITE)==SEQ_WRITE; }
    private static int build(boolean read,boolean write,boolean seqWrite) { return (read?READ:0) | (write?WRITE:0) | (seqWrite?SEQ_WRITE:0); }
    private static int afterFinish(int i) { return build(isRead(i),isWrite(i),isWrite(i)); }
    private static int afterAssign(int i) { return build(isRead(i),true,true); }
    private static int afterRead(int i) { return build(isRead(i) || !isSeqWrite(i),isWrite(i),isSeqWrite(i)); }
    private static int afterSeqBlock(int bBefore, int bAfter) { return build(isRead(bBefore) || (!isSeqWrite(bBefore) && isRead(bAfter)), isWrite(bBefore) || isWrite(bAfter),isSeqWrite(bBefore) || isSeqWrite(bAfter)); }
    private static int afterAsync(int i1, int i2) { return build(isRead(i1)||isRead(i2),isWrite(i1)||isWrite(i2),isSeqWrite(i1)&&isSeqWrite(i2)); }
    private static int afterIf(int i1, int i2) { return build(isRead(i1)||isRead(i2),isWrite(i1)&&isWrite(i2),isSeqWrite(i1)&&isSeqWrite(i2)); }


    private class DataFlowItem extends DataFlow.Item {
        private final Map<X10FieldDecl_c, Integer> initStatus = new HashMap<X10FieldDecl_c, Integer>(); // immutable map of fields to 3flags

        private DataFlowItem() {
            for (X10FieldDecl_c f : fields)
                initStatus.put(f,0);
        }
        public boolean equals(Object o) {
            if (o instanceof DataFlowItem) {
                return this.initStatus.equals(((DataFlowItem)o).initStatus);
            }
            return false;
        }
        public int hashCode() {
            return (initStatus.hashCode());
        }
    }
    class FieldChecker extends DataFlow {
        private final DataFlowItem INIT = new DataFlowItem();
        private ProcedureDecl currDecl;
        public FieldChecker() {
            super(CheckEscapingThis.this.job, CheckEscapingThis.this.ts, CheckEscapingThis.this.nf,
                  true /* forward analysis */,
                  false /* perform dataflow when leaving CodeDecls, not when entering */);
            for (X10FieldDecl_c f : fields)
                INIT.initStatus.put(f,0);
        }
        protected Item createInitialItem(FlowGraph graph, Term node, boolean entry) {
            return INIT;
        }
        protected Item confluence(List items, List itemKeys,
            Term node, boolean entry, FlowGraph graph) {
            if (node instanceof ConstructorDecl) {
                List filtered = filterItemsNonException(items, itemKeys);
                if (filtered.isEmpty()) {
                    return INIT;
                }
                else if (filtered.size() == 1) {
                    return (Item)filtered.get(0);
                }
                else {
                   return confluence(filtered, node, entry, graph);
                }
            }
            return confluence(items, node, entry, graph);
        }
        protected Item confluence(List items, Term node, boolean entry, FlowGraph graph) {
            //if (items.size()==0) return INIT;
            //if (items.size()==1) return (DataFlowItem)items.get(0);
            assert items.size()>=2;

            boolean isAsync = !entry && node instanceof Async;
            DataFlowItem res = new DataFlowItem();
            for (Object itemObj : items) {
                DataFlowItem item = (DataFlowItem) itemObj;
                for (Map.Entry<X10FieldDecl_c, Integer> pair : item.initStatus.entrySet()) {
                    final X10FieldDecl_c key = pair.getKey();
                    int i1 = res.initStatus.get(key);
                    int i2 = pair.getValue();
                    // Async must be handled here, because DataFlow first calls safeConfluence, and only then it calls flow:
                    //p.inItem = this.safeConfluence(...
                    //p.outItems = this.flow(...

                    int i_res = isAsync ? afterAsync(i1,i2) : afterIf(i1,i2);
                    res.initStatus.put(key,i_res);
                }
            }
            return res;
        }
        protected FlowGraph initGraph(CodeNode code, Term root) {
            currDecl = (ProcedureDecl)code;
            return super.initGraph(code,root);
        }

        protected Map flow(List inItems, List inItemKeys, FlowGraph graph,
                Term n, boolean entry, Set edgeKeys) {
            return this.flowToBooleanFlow(inItems, inItemKeys, graph, n, entry, edgeKeys);
        }
        public Map flow(Item trueItem, Item falseItem, Item otherItem,
                    FlowGraph graph, Term n, boolean entry, Set succEdgeKeys) {
            final DataFlowItem inItem = (DataFlowItem) safeConfluence(trueItem, FlowGraph.EDGE_KEY_TRUE,
                                         falseItem, FlowGraph.EDGE_KEY_FALSE,
                                         otherItem, FlowGraph.EDGE_KEY_OTHER,
                                         n, entry, graph);
            DataFlowItem res = inItem;
            if (trueItem == null) trueItem = inItem;
            if (falseItem == null) falseItem = inItem;
            if (entry) {
                return itemToMap(inItem, succEdgeKeys);
            }

            boolean isAssign = n instanceof FieldAssign;
            if (isAssign || n instanceof Field) {
                final Receiver target = isAssign ? ((FieldAssign) n).target() : ((Field) n).target();
                final FieldInstance fi = isAssign ? ((FieldAssign) n).fieldInstance() : ((Field) n).fieldInstance();
                X10FieldDecl_c field = findField(fi.def());
                if (field!=null && isThis(target)) {
                    res = new DataFlowItem();
                    res.initStatus.putAll(inItem.initStatus);
                    final int valueBefore = inItem.initStatus.get(field);
                    int valueAfter = isAssign ? afterAssign(valueBefore) : afterRead(valueBefore);
                    res.initStatus.put(field,valueAfter);
                }

            } else if (n instanceof X10Call) {
                X10Call call = (X10Call) n;
                Receiver receiver = call.target();
                if (isThis(receiver)) {
                    X10MethodDecl_c methodDecl = findMethod(call);
                    if (methodDecl!=null) {
                        res = new DataFlowItem();
                        res.initStatus.putAll(inItem.initStatus);
                        MethodInfo info = allMethods.get(methodDecl.procedureInstance());
                        for (X10FieldDecl_c field : fields) {
                            boolean isRead = info.read.contains(field);
                            boolean isWrite = info.write.contains(field);
                            boolean isWriteSeq = info.seqWrite.contains(field);
                            res.initStatus.put(field, afterSeqBlock(res.initStatus.get(field),build(isRead,isWrite, isWriteSeq)));
                        }
                    }
                }
            } else if (n instanceof Expr && ((Expr)n).type().isBoolean() &&
                    (n instanceof Binary || n instanceof Unary)) {
                return flowBooleanConditions(trueItem, falseItem, inItem, graph, (Expr)n, succEdgeKeys);
            } else if (n instanceof ParExpr && ((ParExpr)n).type().isBoolean()) {
                return itemsToMap(trueItem, falseItem, inItem, succEdgeKeys);
            } else if (n instanceof Finish) {
                res = new DataFlowItem();
                for (Map.Entry<X10FieldDecl_c, Integer> pair : inItem.initStatus.entrySet()) {
                    res.initStatus.put(pair.getKey(),afterFinish(pair.getValue()));
                }
            }
            if (res!=inItem && isCtor()) {
                // can't read from an un-init var in the ctors (I want to catch it here, so I can give the exact position info)
                for (X10FieldDecl_c f : fields) {
                    boolean readBefore = isRead(inItem.initStatus.get(f));
                    if (!readBefore && isRead(res.initStatus.get(f))) {
                        // wasn't read before, and we read it now (either because of Field access, or X10Call)
                        reportError("Cannot read from field '"+f.name()+"' before it is definitely assigned.",n.position());
                        wasError = true;
                    }
                }
            }
            return itemToMap(res, succEdgeKeys);
        }
        private boolean isCtor() { return currDecl instanceof ConstructorDecl; }

        @Override
        protected void check(FlowGraph graph, Term n, boolean entry, Item inItem, Map outItems) {
            DataFlowItem dfIn = (DataFlowItem)inItem;
            if (dfIn == null) dfIn = INIT;
            if (n == graph.root() && !entry) {
                // finish method/ctor
                ProcedureDecl decl = (ProcedureDecl)n;
                assert decl!=null;
                MethodInfo oldInfo = allMethods.get(decl.procedureInstance());
                MethodInfo newInfo = new MethodInfo();
                for (Map.Entry<X10FieldDecl_c, Integer> pair : dfIn.initStatus.entrySet()) {
                    int val = pair.getValue();
                    final X10FieldDecl_c f = pair.getKey();
                    if (isRead(val)) newInfo.read.add(f);
                    if (isWrite(val)) newInfo.write.add(f);
                    if (isSeqWrite(val)) newInfo.seqWrite.add(f);
                }
                if (newInfo.read.equals(oldInfo.read) &&
                    newInfo.write.equals(oldInfo.write) &&
                    newInfo.seqWrite.equals(oldInfo.seqWrite)) {
                    // no change!
                } else {
                    wasChange = true;
                }
                if (isCtor()) {
                    int size = fields.size();
                    if (!newInfo.read.isEmpty()) assert wasError;
                    assert newInfo.write.containsAll(newInfo.seqWrite);
                    if (newInfo.seqWrite.size()!=size) {
                        wasError = true;
                        // report the field that wasn't written to
                        for (X10FieldDecl_c f : fields)
                            if (!newInfo.seqWrite.contains(f)) {
                                reportError("Field '"+f.name()+"' was not definitely assigned in this constructor.",decl.position());
                            }
                    }
                }
            }

        }
    }
    
    // Main visits all the classes, and type-checks them (verify that "this" doesn't escape, etc)
    public static class Main extends NodeVisitor {
        private final Job job;
        public Main(Job job) {
            this.job = job;
        }
        @Override public NodeVisitor enter(Node n) {
            if (n instanceof X10ClassDecl_c)
                new CheckEscapingThis((X10ClassDecl_c)n,job,
                        (X10TypeSystem)job.extensionInfo().typeSystem());
            return this;
        }
    }


    // we gather info on every private/final method
    private static class MethodInfo {
        private final Set<X10FieldDecl_c> read = new HashSet<X10FieldDecl_c>();
        private final Set<X10FieldDecl_c> write = new HashSet<X10FieldDecl_c>();
        private final Set<X10FieldDecl_c> seqWrite = new HashSet<X10FieldDecl_c>();
    }
    private final Job job;
    private final X10NodeFactory nf;
    private final X10TypeSystem ts;
    private final X10ClassDecl_c xlass;
    private final Type xlassType;
    // the keys are either X10ConstructorDecl_c or X10MethodDecl_c
    private final HashMap<ProcedureDef,MethodInfo> allMethods = new LinkedHashMap<ProcedureDef, MethodInfo>(); // all ctors and methods recursively called from allMethods on receiver "this"
    private final ArrayList<ProcedureDecl> dfsMethods = new ArrayList<ProcedureDecl>(); // to accelerate the fix-point alg
    private final HashSet<X10ConstructorDecl_c> allCtors = new HashSet<X10ConstructorDecl_c>();
    // the set of VAR and VAL without initializers (we need to check that VAL are read properly, and that VAR are written and read properly.)
    // InitChecker already checks that VAL are assigned in every ctor exactly once (and never assigned in other methods)
    // Therefore we can now treat both VAL and VAR identically.
    private final HashSet<X10FieldDecl_c> fields = new HashSet<X10FieldDecl_c>();
    private boolean wasChange = true, wasError = false; // for fixed point alg

    public CheckEscapingThis(X10ClassDecl_c xlass, Job job, X10TypeSystem ts) {
        this.job = job;
        this.ts = ts;
        nf = (X10NodeFactory)ts.extensionInfo().nodeFactory();
        this.xlass = xlass;
        this.xlassType = X10TypeMixin.baseType(xlass.classDef().asType());
        typeCheck();
    }
    private void typeCheck() {
        // visit every ctor, and every method called from a ctor, and check that this and super do not escape
        final X10ClassBody_c body = (X10ClassBody_c)xlass.body();
        for (ClassMember classMember : body.members()) {
            if (classMember instanceof X10ConstructorDecl_c) {
                final X10ConstructorDecl_c ctor = (X10ConstructorDecl_c) classMember;
                final Block ctorBody = ctor.body();
                // for native ctors, we don't have a body
                if (ctorBody!=null) {
                    allCtors.add(ctor);
                    ctorBody.visit(this);
                }
            } else if (classMember instanceof X10FieldDecl_c) {
                X10FieldDecl_c field = (X10FieldDecl_c) classMember;
                final Flags flags = field.flags().flags();
                if (flags.isStatic()) continue;
                // if the field has an init, then we do not need to track it (it is always assigned before read)
                if (field.init()!=null) continue;
                if (field.init()!=null) continue;
                if (X10TypeMixin.isUninitializedField((X10FieldDef)field.fieldDef(),ts)) continue;
                // if a VAR has a default value, then we already created an init() expr in X10FieldDecl_c.typeCheck
                fields.add(field);
            }
        }
        if (fields.size()==0) return; // done! all fields have an init, thus all reads are legal.

        // ignore ctors that call other ctors (using "this(...)"). We can reuse ConstructorCallChecker, but for better efficiency, we just check it directly
        for (X10ConstructorDecl_c ctor: allCtors) {
            boolean callThis = false;
            final Block ctorBody = ctor.body();
            final List<Stmt> stmts = ctorBody.statements();
            for (Stmt s : stmts) {
                if (s instanceof ConstructorCall) {
                    ConstructorCall cc = (ConstructorCall) s;
                    if (cc.kind() == ConstructorCall.THIS) {
                        callThis = true;
                        break;
                    }
                }
            }
            if (!callThis) {
                allMethods.put(ctor.procedureInstance(), new MethodInfo());
                dfsMethods.add(ctor);
            }
        }
        // do init for the fixed point alg
        for (MethodInfo info : allMethods.values()) {
            info.write.addAll(fields);
            info.seqWrite.addAll(fields);
        }
        // run fix point alg
        final FieldChecker fieldChecker = new FieldChecker();
        while (wasChange && !wasError) {
            wasChange = false;
            // do a DFS: starting from private/final methods, and then the ctors (this will reach a fixed-point fastest)
            for (ProcedureDecl p : dfsMethods)
                p.visit(fieldChecker);
        }
    }
    private X10FieldDecl_c findField(FieldDef def) {
        for (X10FieldDecl_c f : fields)
            if (f.fieldDef().equalsImpl(def)) return f;
        return null;
    }
    private X10MethodDecl_c findMethod(X10Call call) {
        MethodInstance mi2 = call.methodInstance();
        final X10ClassBody_c body = (X10ClassBody_c)xlass.body();
        for (ClassMember classMember : body.members()) {
            if (classMember instanceof X10MethodDecl_c) {
                X10MethodDecl_c mdecl = (X10MethodDecl_c) classMember;
                final MethodDef md = mdecl.methodDef();
                if (mi2.def().equals(md)) return mdecl;
            }
        }
        return null;
    }

    @Override
    public Node visitEdgeNoOverride(Node parent, Node n) {
        // You can access "this" for field access and field assignment.
        // field assignment:
        if (n instanceof FieldAssign) {
            FieldAssign assign = (FieldAssign) n;
            if (assign.target() instanceof Special) {
                assign.right().visit(this);
                return n;
            }
        }
        // field access:
        if (n instanceof Field && ((Field)n).target() instanceof Special) {
            return n;
        }
        // You can also access "this" as the receiver of property calls (because they are MACROS that are expanded to field access)
        // and as the receiver of private/final calls
        if (n instanceof X10Call) {
            final X10Call call = (X10Call) n;
            final Flags flags = call.methodInstance().flags();
            if (isThis(call.target())) {
                if (flags.contains(X10Flags.PROPERTY)) {
                    // property-method calls are ok
                } else {
                    // the method must be final or private
                    X10MethodDecl_c method = findMethod(call);
                    ProcedureDef pd = method.procedureInstance();
                    if (method==null || allMethods.containsKey(pd)) {
                        // we already analyzed this method (or it is an error method)
                    } else {
                        allMethods.put(pd,new MethodInfo()); // prevent infinite recursion
                        // verify the method is indeed private/final
                        if (!flags.isFinal() && !flags.isPrivate())  {
                            report("The call "+call+" is illegal because you can only call private or final methods from a constructor or from methods called from a constructor",call.position());
                        }
                        method.body().visit(this);
                        dfsMethods.add(method);
                    }

                }

                // it is enough to just recurse into the arguments (because the receiver is either this or super)
                for (Expr e : call.arguments())
                    e.visit(this);
                return n;
            }                        
        }
        // You cannot use "this" for anything else!
        if (isThis(n)) {
            report("'this' and 'super' cannot escape from a constructor or from methods called from a constructor",n.position());
        }
        n.del().visitChildren(this);
        return n;
    }
    private void report(String s, Position p) {
        job.compiler().errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR,s,p);
    }
    private boolean isThis(Node n) {
        if (n==null || !(n instanceof Special)) return false;
        final Special special = (Special) n;
        return //special.kind()==Special.THIS && // both this and super cannot escape
               ts.typeEquals(X10TypeMixin.baseType(special.type()), xlassType,null);
    }
}