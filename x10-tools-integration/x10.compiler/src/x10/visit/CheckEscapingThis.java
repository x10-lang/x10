package x10.visit;

import polyglot.ast.*;
import polyglot.util.ErrorInfo;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import polyglot.visit.DataFlow;
import polyglot.visit.FlowGraph;
import polyglot.visit.DataFlow.Item;
import polyglot.visit.FlowGraph.EdgeKey;
import polyglot.frontend.Job;
import polyglot.types.Type;
import polyglot.types.FieldDef;
import polyglot.types.MethodInstance;
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
import x10.ast.*;
import x10.types.X10TypeMixin;
import x10.types.X10Flags;
import x10.types.X10TypeSystem;
import x10.types.X10FieldDef;
import x10.types.X10ParsedClassType_c;
import x10.types.X10ProcedureDef;
import x10.types.X10MethodDef;
import x10.types.checker.ThisChecker;

import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Collections;

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
    private static int afterAsync(int i1, int i2, boolean isUncounted) { return build(isRead(i1)||isRead(i2),!isUncounted ? isWrite(i1)||isWrite(i2) : isWrite(i1)&&isWrite(i2),isSeqWrite(i1)&&isSeqWrite(i2)); }
    private static int afterIf(int i1, int i2) { return build(isRead(i1)||isRead(i2),isWrite(i1)&&isWrite(i2),isSeqWrite(i1)&&isSeqWrite(i2)); }
    private static String flagsToString(int i) { return i==0? "none," : (isRead(i)?"read," : "")+(isWrite(i)?"write," : "")+(isSeqWrite(i)?"seqWrite," : ""); }


    private static class DataFlowItem extends DataFlow.Item {
        private final Map<FieldDef, Integer> initStatus = new HashMap<FieldDef, Integer>(); // immutable map of fields to 3flags

        public boolean equals(Object o) {
            if (o instanceof DataFlowItem) {
                return this.initStatus.equals(((DataFlowItem)o).initStatus);
            }
            return false;
        }
        public int hashCode() {
            return (initStatus.hashCode());
        }

        @Override
        public String toString() {
            StringBuilder res = new StringBuilder();
            for (Map.Entry<FieldDef, Integer> pair : initStatus.entrySet()) {
                res.append(pair.getKey().name()).append(":").append(flagsToString(pair.getValue())).append(" ");
            }
            return res.toString();
        }
    }
    class FieldChecker extends DataFlow {
        private ProcedureDecl currDecl;
        private DataFlowItem init;
        private DataFlowItem finalResult;
        public FieldChecker(DataFlowItem init) {
            super(CheckEscapingThis.this.job, CheckEscapingThis.this.ts, CheckEscapingThis.this.nf,
                  true /* forward analysis */,
                  false /* perform dataflow when leaving CodeDecls, not when entering */);
            this.init = init;
        }
        protected Item createInitialItem(FlowGraph graph, Term node, boolean entry) {
            return init;
        }
        protected Item confluence(List<Item> items, List<EdgeKey> itemKeys,
            Term node, boolean entry, FlowGraph graph) {
            if (node instanceof ProcedureDecl) {
                List<Item> filtered = filterItemsNonException(items, itemKeys);
                if (filtered.isEmpty()) {
                    return init;
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
        protected Item confluence(List<Item> items, Term node, boolean entry, FlowGraph graph) {
            //if (items.size()==0) return INIT;
            //if (items.size()==1) return (DataFlowItem)items.get(0);
            assert items.size()>=2;

            boolean isAsync = !entry && node instanceof Async;
            boolean isUncounted = isAsync ? Desugarer.isUncountedAsync((X10TypeSystem)ts,(Async) node) : false;
            DataFlowItem res = new DataFlowItem();
            res.initStatus.putAll(((DataFlowItem) items.get(0)).initStatus);

            for (int i=1; i<items.size(); i++) {
                DataFlowItem item = (DataFlowItem) items.get(i);
                for (Map.Entry<FieldDef, Integer> pair : item.initStatus.entrySet()) {
                    final FieldDef key = pair.getKey();
                    int i1 = res.initStatus.get(key);
                    int i2 = pair.getValue();
                    // Async must be handled here, because DataFlow first calls safeConfluence, and only then it calls flow:
                    //p.inItem = this.safeConfluence(...
                    //p.outItems = this.flow(...

                    int i_res = isAsync ? afterAsync(i1,i2,isUncounted) : afterIf(i1,i2);
                    res.initStatus.put(key,i_res);
                }
            }
            return res;
        }
        protected FlowGraph initGraph(CodeNode code, Term root) {
            currDecl = (ProcedureDecl)code; // we do not analyze closures (Closure_c) since we require that "this" does not escape there
            return super.initGraph(code,root);
        }

        protected Map<EdgeKey, Item> flow(List<Item> inItems, List<EdgeKey> inItemKeys, FlowGraph graph,
                Term n, boolean entry, Set<EdgeKey> edgeKeys) {
            return this.flowToBooleanFlow(inItems, inItemKeys, graph, n, entry, edgeKeys);
        }
        public Map<EdgeKey, Item> flow(Item trueItem, Item falseItem, Item otherItem,
                    FlowGraph graph, Term n, boolean entry, Set<EdgeKey> succEdgeKeys) {
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
                final FieldInstance fi = isAssign ? ((FieldAssign) n).fieldInstance() : ((Field) n).fieldInstance();
                FieldDef field = fi.def();
                if (field!=null && (isAssign ? isTargetThis((FieldAssign) n) : isTargetThis((Field) n))) {
                    res = new DataFlowItem();
                    res.initStatus.putAll(inItem.initStatus);
                    final int valueBefore = inItem.initStatus.get(field);
                    int valueAfter = isAssign ? afterAssign(valueBefore) : afterRead(valueBefore);
                    res.initStatus.put(field,valueAfter);
                }

            } else if (n instanceof Closure) {
                // the closure can write to whatever it wants and it won't affect the write-set (because it wasn't invoked yet)
                // we only check that it can read what was written to, and call methods whose read-set was written to.
                Closure closure = (Closure) n;
                closure.body().visit(
                    new NodeVisitor() {
                        @Override public NodeVisitor enter(Node n) {
                            if (n instanceof Field) {
                                final Field f = (Field) n;
                                if (isTargetThis(f) && !isWrite(inItem.initStatus.get(f.fieldInstance().def()))) {
                                    reportError("Cannot read from field '"+ f.name()+"' before it is definitely assigned.",n.position());
                                    wasError = true;
                                }
                            }
                            if (n instanceof X10Call) {
                                MethodInfo info = getInfo((X10Call) n);
                                if (info!=null) {
                                    for (FieldDef def : info.read) {
                                        if (!isWrite( inItem.initStatus.get(def))) {
                                            reportError("The method call reads from field '"+ def.name()+"' before it is definitely assigned.",n.position());
                                            wasError = true;
                                        }
                                    }
                                }
                            }
                            return this;
                        }
                    }
                );

            } else if (n instanceof ConstructorCall) {
                ConstructorCall ctorCall = (ConstructorCall) n;
                assert (ctorCall.kind()==ConstructorCall.SUPER) : "We do not analyze ctors with 'this()' calls, because they're always correct - everything must be initialized after the 'this()' call";
                //assert res==INIT : "It must be the first statement in the ctor";
                // Note that the super call should be the first statement, but I inline the field-inits before it.

            } else if (n instanceof X10Call) {
                MethodInfo info = getInfo((X10Call) n);
                if (info!=null) {
                    res = new DataFlowItem();
                    res.initStatus.putAll(inItem.initStatus);
                    for (FieldDef field : fields) {
                        boolean isRead = info.read.contains(field);
                        boolean isWrite = info.write.contains(field);
                        boolean isWriteSeq = info.seqWrite.contains(field);
                        res.initStatus.put(field, afterSeqBlock(res.initStatus.get(field),build(isRead,isWrite, isWriteSeq)));
                    }
                }
            } else if (n instanceof Expr && ((Expr)n).type().isBoolean() &&
                    (n instanceof Binary || n instanceof Unary)) {
                final Map<EdgeKey, Item> map = flowBooleanConditions(trueItem, falseItem, inItem, graph, (Expr) n, succEdgeKeys);
                if (map!=null) return map;
            } else if (n instanceof ParExpr && ((ParExpr)n).type().isBoolean()) {
                return itemsToMap(trueItem, falseItem, inItem, succEdgeKeys);
            } else if (n instanceof Finish) {
                res = new DataFlowItem();
                for (Map.Entry<FieldDef, Integer> pair : inItem.initStatus.entrySet()) {
                    res.initStatus.put(pair.getKey(),afterFinish(pair.getValue()));
                }
            }
            if (res!=inItem) {
                MethodInfo info = allMethods.get(currDecl.procedureInstance());                  
                final boolean ctor = isCtor();
                if (!ctor) assert info!=null : currDecl;
                // can't read from an un-init var in the ctors (I want to catch it here, so I can give the exact position info)
                // can't read from any var if @NoThisAccess
                if (ctor) {
                    for (FieldDef f : fields) {
                        boolean readBefore = isRead(inItem.initStatus.get(f));
                        final int fRes = res.initStatus.get(f);
                        if (!readBefore && isRead(fRes)) {
                            // wasn't read before, and we read it now (either because of Field access, or X10Call)
                            reportError("Cannot read from field '"+f.name()+"' before it is definitely assigned.",n.position());
                            // I want to report more errors with this field, so I remove the read status
                            res.initStatus.put(f,build(false, isWrite(fRes),isSeqWrite(fRes)));
                            wasError = true;
                        }
                    }
                }
            }
            return itemToMap(res, succEdgeKeys);
        }
        private boolean isCtor() { return currDecl instanceof ConstructorDecl; }

        @Override
        protected void check(FlowGraph graph, Term n, boolean entry, Item inItem, Map<EdgeKey, Item> outItems) {
            DataFlowItem dfIn = (DataFlowItem)inItem;
            if (dfIn == null) dfIn = init;
            if (n == graph.root() && !entry) {
                assert n==currDecl : n;
                finalResult = dfIn;
            }
        }
        public void checkResult() {
            // finish method/ctor
            ProcedureDecl decl = currDecl;
            assert decl!=null;
            final ProcedureDef procDef = decl.procedureInstance();
            MethodInfo newInfo = new MethodInfo();
            for (Map.Entry<FieldDef, Integer> pair : finalResult.initStatus.entrySet()) {
                int val = pair.getValue();
                final FieldDef f = pair.getKey();
                if (isRead(val)) newInfo.read.add(f);
                if (isWrite(val)) newInfo.write.add(f);
                if (isSeqWrite(val)) newInfo.seqWrite.add(f);
            }

            // everything must be assigned in a ctor (and nothing read)
            if (isCtor()) {
                int size = fields.size();
                if (!newInfo.read.isEmpty()) assert wasError;
                assert newInfo.write.containsAll(newInfo.seqWrite);
                if (newInfo.seqWrite.size()!=size) {
                    wasError = true;
                    // report the field that wasn't written to
                    for (FieldDef f : fields)
                        // a VAR marked with @Uninitialized is not tracked
                        if (!newInfo.seqWrite.contains(f) && !X10TypeMixin.isUninitializedField((X10FieldDef)f,(X10TypeSystem)ts)) {
                            final Position pos = currDecl.position();
                            if (pos.isCompilerGenerated()) // auto-generated ctor
                                reportError("Field '"+f.name()+"' was not definitely assigned.", f.position());
                            else
                                reportError("Field '"+f.name()+"' was not definitely assigned in this constructor.", pos);
                        }
                }
            } else {
                MethodInfo oldInfo = allMethods.get(procDef);
                assert oldInfo!=null : currDecl;
                assert !X10TypeMixin.isNoThisAccess((X10ProcedureDef)procDef,(X10TypeSystem)ts);

                // proof that the fix-point terminates: write set decreases while the read set increases
                assert oldInfo.write.containsAll(newInfo.write);
                assert oldInfo.seqWrite.containsAll(newInfo.seqWrite);
                assert newInfo.read.containsAll(oldInfo.read);


                // fixed-point reached?
                if (newInfo.read.equals(oldInfo.read) &&
                    newInfo.write.equals(oldInfo.write) &&
                    newInfo.seqWrite.equals(oldInfo.seqWrite)) {
                    // no change!
                } else {
                    wasChange = true;
                    allMethods.put(procDef,newInfo);
                }
            }
        }
    }
    private MethodInfo getInfo(X10Call call) {
        final X10MethodDef def = (X10MethodDef) call.methodInstance().def();
        if (isTargetThis(call) && findMethod(call)!=null && !X10TypeMixin.isNoThisAccess(def,ts)) {
            final MethodInfo info = allMethods.get(def);
            assert info!=null;
            return info;
        }
        return null;
    }
    private boolean isProperty(FieldDef def) {
        final X10Flags flags = X10Flags.toX10Flags(def.flags());
        return flags.isProperty();
    }
    private boolean isProperty(ProcedureDef def) {
        final X10Flags flags = X10Flags.toX10Flags(((ProcedureDef_c)def).flags());
        return flags.isProperty();
    }
    private boolean isPrivate(ProcedureDef def) {
        final X10Flags flags = X10Flags.toX10Flags(((ProcedureDef_c)def).flags());
        return flags.isPrivate();
    }
    private boolean isPrivateOrFinal(ProcedureDef def) {
        if (isXlassFinal) return true;
        final X10Flags flags = X10Flags.toX10Flags(((ProcedureDef_c)def).flags());
        return flags.isPrivate() || flags.isFinal();
    }
    
    // Main visits all the classes, and type-checks them (verify that "this" doesn't escape, etc)
    public static class Main extends NodeVisitor {
        private final Job job;
        public Main(Job job) {
            this.job = job;
        }
        @Override public NodeVisitor enter(Node n) {
            if (n instanceof X10ClassDecl_c) {
                final X10ClassDecl_c classDecl_c = (X10ClassDecl_c) n;
                if (!classDecl_c.flags().flags().isInterface()) // I have nothing to analyze in an interface 
                    new CheckEscapingThis(classDecl_c,job,
                        (X10TypeSystem)job.extensionInfo().typeSystem());
            }
            return this;
        }
    }

    static enum CtorState { Start, SawCtor, SawProperty };
    public class CheckCtor extends NodeVisitor {
        private CtorState state = CtorState.Start;
        private boolean wasSuperCall = false;
        private final X10ConstructorDecl_c ctor;

        private CheckCtor(X10ConstructorDecl_c ctor) {
            this.ctor = ctor;
            if (getConstructorCall(ctor)==null) {
                // There is no 'this(...)' or 'super(...)' calls, so we implicitly start after a 'super()' call
                state = CtorState.SawCtor;
                wasSuperCall = true;
            }
        }
        private void postCheck() {
            switch (state) {
                case Start:
                    assert false : "There must be a super call (either explicit or implicit)";
                case SawCtor:
                    if (hasProperties && wasSuperCall)
                        reportError("You must call 'property(...)' at least once",ctor.position());
                    break;
            }
        }

        @Override public Node visitEdgeNoOverride(Node parent, Node n) {
            Position pos = n.position();
            if (!canUseThis() && n instanceof X10Call) {
                final X10Call call = (X10Call) n;
                if (isTargetThis(call)) {
                    if (X10TypeMixin.isNoThisAccess((X10MethodDef)call.methodInstance().def(),ts)) {
                        // && X10TypeMixin.getNonEscapingReadsFrom((X10MethodDef)call.methodInstance().def(),ts)==null) { // @NonEscaping methods cannot write to any fields
                        // even though we use "this.call(...)", this is legal
                        // because the call doesn't read nor write to "this"
                    } else {
                        MethodInfo info = getInfo(call);
                        if (info!=null && info.read.size()==0 && info.write.size()==0) {
                            // ok
                        } else {
                            reportError("You can use 'this' before 'property(...)' to call only @NoThisAccess methods or NonEscaping methods that do not read nor write any fields.",pos);
                        }
                    }
                    for (Expr e : call.arguments())
                        e.visit(this);
                    return n;
                }
            }

            n.del().visitChildren(this);

            if (n instanceof ConstructorCall) {
                ConstructorCall constructorCall = (ConstructorCall) n;
                switch (state) {
                case Start:
                    state = CtorState.SawCtor;
                    wasSuperCall = constructorCall.kind()==ConstructorCall.SUPER;
                    break;
                case SawCtor:
                    reportError("Can only have a single 'this(...)' or 'super(...)' in a constructor",pos);
                    break;
                case SawProperty:
                    reportError("'this(...)' or 'super(...)' must come before 'property(...)'",pos);
                    break;
                }
            } else if (n instanceof AssignPropertyCall) {
                switch (state) {
                case Start:
                    assert false : "implicit super() call is handled at the beginning, see getConstructorCall";
                case SawCtor:
                    if (!wasSuperCall)
                        reportError("You cannot call 'property(...)' after 'this(...)'",pos);
                    else if (!hasProperties) {
                        // This error is already reported: "The property initializer must have the same number of arguments as properties for the class."
                        //reportError("You can call 'property(...)' only if the class defined properties",pos);
                    } else {
                        state = CtorState.SawProperty;
                    }
                    break;
                case SawProperty:
                    reportError("You can call 'property(...)' at most once",pos);
                    break;
                }

            } else if (isThis(n)) {
                Special special = (Special) n;
                if (special.kind()==Special.SUPER) {
                    if (state==CtorState.Start)
                        reportError("You can use 'super' only after 'super(...)'",pos);
                } else if (!canUseThis())
                    reportError((hasProperties ? "Can use 'this' only after 'property(...)'" : "Can use 'this' only after 'this(...)' or 'super(...)'")+" in "+parent, pos);
            }
            return n;
        }
        private boolean canUseThis() {
            return state==CtorState.SawProperty || // after call to 'property(...)'
                (!hasProperties && state==CtorState.SawCtor) || // after call to 'this(...)' or 'super(...)' if there are no properties
                (state==CtorState.SawCtor && !wasSuperCall); // after call to 'this(...)'
        }

    }


    // we gather info on every private/final/@NonEscaping method called during construction (@NoThisAccess do not access "this", so no need to analyze them)
    private static class MethodInfo {
        private final Set<FieldDef> read = new HashSet<FieldDef>();
        private final Set<FieldDef> write = new HashSet<FieldDef>();
        private final Set<FieldDef> seqWrite = new HashSet<FieldDef>();
    }
    private final Job job;
    private final X10NodeFactory nf;
    private final X10TypeSystem ts;
    private final X10ClassDecl_c xlass;
    private final boolean hasProperties; // this this class defined properties (excluding properties of the sueprclass). if so, there must be exactly one "property(...)"
    private final boolean isXlassFinal;
    private final Type xlassType;
    // the keys are either X10ConstructorDecl_c or X10MethodDecl_c
    private final HashMap<ProcedureDef,MethodInfo> allMethods = new LinkedHashMap<ProcedureDef, MethodInfo>(); // all ctors and methods recursively called from allMethods on receiver "this"
    private final ArrayList<ProcedureDecl> dfsMethods = new ArrayList<ProcedureDecl>(); // to accelerate the fix-point alg
    // the set of all VAR and VAL fields (without properties), including those in the superclass because of super() call (we need to check that VAL are read properly, and that VAR are written and read properly.)
    // InitChecker already checks that VAL are assigned in every ctor exactly once (and never assigned in other methods)
    // Therefore we can now treat both VAL and VAR identically.
    private final HashSet<FieldDef> fields = new HashSet<FieldDef>();
    private final HashSet<FieldDef> superFields = new HashSet<FieldDef>(); // after the "super()" call, these fields are initialized
    private final DataFlowItem INIT = new DataFlowItem();
    private final DataFlowItem CTOR_INIT = new DataFlowItem();

    private boolean wasChange = true, wasError = false; // for fixed point alg
    private HashSet<FieldDef> globalRef = new HashSet<FieldDef>();// There is one exception to the "this cannot escape" rule:  val root = GlobalRef[...](this)

    private void checkGlobalRef(Node n) {
        // you cannot access a globalRef field via this  (but you can assign to them)
        if (n instanceof Field) {
            Field f = (Field) n;
            FieldDef def = f.fieldInstance().def();
            if (isTargetThis(f) && globalRef.contains(def))
                reportError("Cannot use '"+def.name()+"' because a GlobalRef[...](this) cannot be used in a field initializer, constructor, or methods called from a constructor.",n.position());
        }          
    }
    public CheckEscapingThis(X10ClassDecl_c xlass, Job job, X10TypeSystem ts) {
        this.job = job;
        this.ts = ts;
        nf = (X10NodeFactory)ts.extensionInfo().nodeFactory();
        this.xlass = xlass;
        hasProperties = xlass.properties()!=null && xlass.properties().size()>0;
        isXlassFinal = xlass.flags().flags().isFinal();
        this.xlassType = X10TypeMixin.baseType(xlass.classDef().asType());
        // calculate the set of all fields (including inherited fields)
        calcFields();
        int notInited = build(false, false,false);
        int inited = build(false, true,true);
        for (FieldDef f : fields) {
            INIT.initStatus.put(f,notInited);
            CTOR_INIT.initStatus.put(f,notInited);
        }
        for (FieldDef field : superFields) {
            CTOR_INIT.initStatus.put(field, inited);
        }
        typeCheck();
    }
    private void calcFields() {
        final ClassDef myClassDef = xlass.classDef();
        ClassDef currClass = myClassDef;
        while (currClass!=null) {
            List<FieldDef> list = currClass.fields();
            List<FieldDef> init = new ArrayList<FieldDef>(list.size());
            for (FieldDef f : list) {
                if (!isProperty(f) && // not tracking property fields (checking property() call was done elsewhere)
                    !f.flags().isStatic()) { // static fields are checked in FwdReferenceChecker
                    init.add(f);
                }
            }
            fields.addAll(init);
            if (myClassDef!=currClass) superFields.addAll(init);
            final Ref<? extends Type> superType = currClass.superType();
            if (superType==null) break;
            currClass = superType.get().toClass().def();
        }
    }
    private void calcGlobalRefs(ArrayList<X10FieldDecl_c> nonStaticFields) {
        for (X10FieldDecl_c field : nonStaticFields) {
            final Expr init = field.init();
            // check for the pattern: val/var someField = GlobalRef[...](this)
            if (init instanceof X10New_c) {
                X10New_c new_c = (X10New_c) init;
                final TypeNode typeNode = new_c.objectType();
                final List<Expr> args = new_c.arguments();
                if (args.size()==1 && isThis(args.get(0)) && // the first and only argument is "this"
                    typeNode instanceof X10CanonicalTypeNode_c) { // now checking the ctor is of GlobalRef
                    X10CanonicalTypeNode_c tn = (X10CanonicalTypeNode_c) typeNode;
                    final Type type = tn.type();
                    if (type instanceof X10ParsedClassType_c) {
                        X10ParsedClassType_c classType_c = (X10ParsedClassType_c) type;
                        final QName qName = classType_c.def().fullName();
                        if (qName.equals(QName.make("x10.lang","GlobalRef"))) {
                            // found the pattern!
                            // must be private
                            if (!isXlassFinal && !field.flags().flags().isPrivate())
                                reportError("In order to use the pattern GlobalRef[...](this) the field must be private.",field.position());
                            globalRef.add(field.fieldDef());
                        }
                    }
                }
            }
        }
    }
    private void typeCheck() {
        final X10ClassBody_c body = (X10ClassBody_c)xlass.body();
        // visit all (non-static) field initializers and check that they do not have forward references nor that "this" escapes
        ArrayList<X10FieldDecl_c> nonStaticFields = new ArrayList<X10FieldDecl_c>();
        for (ClassMember classMember : body.members()) {
            if (classMember instanceof X10FieldDecl_c) {
                X10FieldDecl_c field = (X10FieldDecl_c) classMember;
                if (field.flags().flags().isStatic()) continue;
                nonStaticFields.add(field);
            }
        }
        // Find globalRefs
        calcGlobalRefs(nonStaticFields);

        // inline the field-initializers in every ctor
        ArrayList<Stmt> fieldInits = new ArrayList<Stmt>();
        final Position pos = Position.COMPILER_GENERATED;
        for (X10FieldDecl_c field : nonStaticFields) {
            final Expr init = field.init();
            final X10FieldDef def = (X10FieldDef) field.fieldDef();
            if (init==null) continue;
            final Special This = (Special) nf.Special(pos, Special_c.THIS).type(def.container().get().toType());
            final FieldAssign fieldAssign = (FieldAssign) nf.FieldAssign(pos, This, field.name(), Assign_c.ASSIGN, init).
                    fieldInstance(def.asInstance()).
                    type(init.type());
            fieldInits.add(nf.Eval(pos, fieldAssign));
            if (!globalRef.contains(def)) init.visit(this); // field init are like a ctor
        }
        Block newInit = nf.Block(pos,fieldInits);


        // visit every ctor, every @NoThisAccess/@NonEscaping method, and every method recursively called from them, and check that this and super do not escape
        ArrayList<X10ConstructorDecl_c> allCtors = new ArrayList<X10ConstructorDecl_c>();
        for (ClassMember classMember : body.members()) {
            if (classMember instanceof ProcedureDecl) {
                final ProcedureDecl proc = (ProcedureDecl) classMember;
                final X10ProcedureDef def = (X10ProcedureDef)proc.procedureInstance();
                final Block procBody = proc.body();

                if (def instanceof X10MethodDef) {
                    X10MethodDef x10def = (X10MethodDef) def;
                    boolean isNoThisAccess = X10TypeMixin.isNoThisAccess(x10def,ts);
                    boolean isNonEscaping = X10TypeMixin.isNonEscaping(x10def,ts);

                    // if we overrode a method with @NoThisAccess, then we must be annotated with @NoThisAccess
                    // (NonEscaping is private/final, so cannot be overriden)
                    if (!isNoThisAccess) {
                        final MethodInstance instance = x10def.asInstance();
                        final Context emptyContext = ts.emptyContext();
                        final List<MethodInstance> overriddenMethods = ts.overrides(instance, emptyContext);
                        for (MethodInstance overriddenMI : overriddenMethods) {
                            MethodDef overriddenDef = overriddenMI.def();
                            if (overriddenDef==def) continue; // me
                            boolean overriddenIsNoThisAccess = X10TypeMixin.isNoThisAccess((X10MethodDef)overriddenDef,ts);
                            if (overriddenIsNoThisAccess) {
                                reportError("You must annotate "+proc+" with @NoThisAccess because it overrides a method annotated with that.", proc.position());
                                break; // one such error msg is enough
                            }
                        }
                    }


                    if (isNoThisAccess) { // NoThisAccess is stronger than NonEscaping so we check it first (in case someone wrote both annotations)
                        // check "this" is not accessed at all
                        if (procBody!=null) { // native/abstract methods
                            ThisChecker thisChecker = new ThisChecker(job);
                            procBody.visit(thisChecker);
                            if (thisChecker.error())
                                reportError("You cannot use 'this' or 'super' in a method annotated with @NoThisAccess",procBody.position());
                        }
                        // No need to do procBody.visit(this)  because "this"/"super" are not used in a NoThisAccess method.
                    } else if (isNonEscaping) {
                        if (!isPrivateOrFinal(x10def))
                            reportError("A @NonEscaping method must be private or final.", proc.position());
                        if (procBody!=null && !allMethods.containsKey(def)) { // for native methods/ctors, we don't have a body
                            final MethodInfo info = new MethodInfo();
                            allMethods.put(def, info);
                            procBody.visit(this);
                            dfsMethods.add(proc);
                        }
                    }
                } else {
                    if (procBody==null) continue; // native ctors
                    assert proc instanceof X10ConstructorDecl_c : proc;
                    final X10ConstructorDecl_c ctor = (X10ConstructorDecl_c) proc;
                    allCtors.add(ctor);
                    procBody.visit(this);
                }
            }
        }
        // we still need to CheckCtor (make sure super, this and property is correct)
        //if (fields.size()==0) return; // done! all fields have an init, thus all reads are legal (and no writes must be done).

        // do init for the fixed point alg
        for (Map.Entry<ProcedureDef, MethodInfo> entry : allMethods.entrySet()) {
            MethodInfo info = entry.getValue();
            info.write.addAll(fields);
            info.seqWrite.addAll(fields);
        }
        // run fix point alg: ctors do not need to be in the fixed point alg because nobody can call them directly
        final FieldChecker fieldChecker = new FieldChecker(INIT);
        while (wasChange && !wasError) {
            wasChange =false;
            // do a DFS: starting from private/final methods, and then the ctors (this will reach a fixed-point fastest)
            for (ProcedureDecl p : dfsMethods) {
                fieldChecker.dataflow(p);
                fieldChecker.checkResult();
            }
        }
        // handle ctors and field initializers        
        // make a new special ctor for field-init, and the ctors will use its data-flow for their INIT
        if (allCtors.size()>0) { // there should be at least one auto-generated ctor
            fieldChecker.init = CTOR_INIT;
            X10ConstructorDecl_c fieldInitCtor = (X10ConstructorDecl_c) allCtors.get(0).body(newInit);
            fieldChecker.dataflow(fieldInitCtor);
            fieldChecker.init = fieldChecker.finalResult;
            for (X10ConstructorDecl_c ctor : allCtors) {
                // check super, this, and property calls
                final CheckCtor checkCtor = new CheckCtor(ctor);
                ctor.visit(checkCtor);
                checkCtor.postCheck();

                final ConstructorCall cc = getConstructorCall(ctor);
                if (cc!=null && cc.kind() == ConstructorCall.THIS) {
                    // ignore in dataflow ctors that call other ctors (using "this(...)").
                    continue;
                }
                // X10ConstructorDecl_c newCtor = (X10ConstructorDecl_c) ctor.body( nf.Block(pos,newInit,ctor.body()) ); //reports errors in the field-inits multiple times (if we have multiple ctors)
                fieldChecker.dataflow(ctor);
                fieldChecker.checkResult();
            }
        }
    }
    private static ConstructorCall getConstructorCall(X10ConstructorDecl_c ctor) {
        // We can reuse ConstructorCallChecker, but for better efficiency, we just check it directly
        final Block ctorBody = ctor.body();
        assert ctorBody!=null;
        final List<Stmt> stmts = ctorBody.statements();
        for (Stmt s : stmts) {
            if (s instanceof ConstructorCall) {
                return (ConstructorCall) s;
            }
        }
        return null;
    }
    private X10MethodDecl_c findMethod(X10Call call) {
        MethodInstance mi2 = call.methodInstance();
        final X10ClassBody_c body = (X10ClassBody_c)xlass.body();
        for (ClassMember classMember : body.members()) {
            if (classMember instanceof X10MethodDecl_c) {
                X10MethodDecl_c mdecl = (X10MethodDecl_c) classMember;
                if (mdecl.body()==null) continue; // for native methods (like typeName in structs)
                final MethodDef md = mdecl.methodDef();
                if (mi2.def().equals(md)) return mdecl;
            }
        }
        return null;
    }

    @Override public Node visitEdgeNoOverride(Node parent, Node n) {
        checkGlobalRef(n); // check globalRef usage in ctors and methods called from ctors

        if (n instanceof New) {
            New aNew = (New) n;
            if (aNew.qualifier()==null && aNew.body()!=null) {
                reportError("'this' cannot escape via an anonymous class during construction", n.position());
            }
        }
        // You can access "this" for field access and field assignment.
        // field assignment:
        if (n instanceof FieldAssign) {
            FieldAssign f = (FieldAssign) n;
            if (isThis(f.target())) {
                f.right().visit(this);
                return n;
            }
        }
        // field access:
        if (n instanceof Field) {
            final Field f = (Field) n;
            if (isThis(f.target())) return n;
        }
        // You can also access "this" as the receiver of property calls (because they are MACROS that are expanded to field access)
        // and as the receiver of private/final calls
        if (n instanceof X10Call) {
            final X10Call call = (X10Call) n;
            final MethodInstance methodInstance = call.methodInstance();
            final X10ProcedureDef procDef = (X10ProcedureDef) methodInstance.def();
            if (isThis(call.target())) {
                boolean hasNoThisAccess = X10TypeMixin.isNoThisAccess(procDef,ts);
                if (isProperty(procDef) || hasNoThisAccess) {
                    // property-method calls and calls to @NoThisAccess are ok
                } else {
                    // the method must be final or private (or @NoThisAccess)
                    final Position callPos = call.position();
                    boolean isNonEscaping = X10TypeMixin.isNonEscaping(procDef,ts);
                    X10MethodDecl_c method = findMethod(call);
                    if (method==null) {
                        // in the future: we could infer nonescaping from the superclass. The problem is that it is hard to understand the error messages that result from such inference
                        // Igor: I think we should disallow the call to foo() when we infer that foo() escapes this.  The error message may mention @NonEscaping -- once the user annotates foo() with @NonEscaping, the compiler will tell him/her where the potential points of escape are. 
                        if (!isNonEscaping)
                            reportError("The call "+call+" is illegal because you can only call a superclass method during construction only if it is annotated with @NonEscaping.", callPos);
                    } else {
                        if (!isPrivateOrFinal(procDef) && !isNonEscaping) // if it is NonEscaping, we will already report the error: "A @NonEscaping method must be private or final."
                            reportError("The call "+call+" is illegal because you can only call private/final @NonEscaping methods or @NoThisAccess methods during construction.", callPos);

                        ProcedureDef pd = method.procedureInstance();
                        if (allMethods.containsKey(pd)) {
                            // we already analyzed this method (or it is an error method)
                        } else {
                            if (!isNonEscaping && !isXlassFinal && !isPrivate(procDef))
                                job.compiler().errorQueue().enqueue(ErrorInfo.WARNING,"Method '"+procDef.signature()+"' is called during construction and therefore should be marked as @NonEscaping.", method.position());                            
                            final Block body = method.body();
                            if (body!=null) {
                                allMethods.put(pd,new MethodInfo()); // prevent infinite recursion
                                body.visit(this);
                                dfsMethods.add(method);
                            }
                        }
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
            reportError("'this' and 'super' cannot escape from a constructor or from methods called from a constructor",n.position());
        }
        n.del().visitChildren(this);
        return n;
    }
    private void reportError(String s, Position p) {
        job.compiler().errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR,s,p);
    }


    private boolean isTargetThis(X10Call call) {
        final MethodInstance methodInstance = call.methodInstance();
        final MethodDef def = methodInstance.def();
        return !isProperty(def) && !def.flags().isStatic() && isThis(call.target());
    }
    private boolean isTargetThis(FieldAssign f) {
        FieldDef def = f.fieldInstance().def();
        return !isProperty(def) && !def.flags().isStatic() && isThis(f.target());
    }
    private boolean isTargetThis(Field f) {
        FieldDef def = f.fieldInstance().def();
        return !isProperty(def) && !def.flags().isStatic() && isThis(f.target());
    }
    private boolean isThis(Node n) {
        if (n==null || !(n instanceof Special)) return false;
        final Special special = (Special) n;
        final Type type = X10TypeMixin.baseType(special.type());
        // both this and super cannot escape
        // for "super.", it resolves to the superclass, so I need to go up the superclasses
        Type thisClass = xlassType;
        while (thisClass!=null) {
            if (ts.typeEquals(type, thisClass,null)) return true;
            if (!thisClass.isClass()) return false;
            final Type superClass = thisClass.toClass().superClass();
            if (superClass==null) return false;
            thisClass = X10TypeMixin.baseType(superClass);
        }
        return false;
    }
}