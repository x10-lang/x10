package x10.visit;

import polyglot.ast.*;
import polyglot.util.ErrorInfo;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import polyglot.visit.DataFlow;
import polyglot.visit.FlowGraph;
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
    private static int afterAsync(int i1, int i2) { return build(isRead(i1)||isRead(i2),isWrite(i1)||isWrite(i2),isSeqWrite(i1)&&isSeqWrite(i2)); }
    private static int afterIf(int i1, int i2) { return build(isRead(i1)||isRead(i2),isWrite(i1)&&isWrite(i2),isSeqWrite(i1)&&isSeqWrite(i2)); }
    private static String flagsToString(int i) { return i==0? "none," : (isRead(i)?"read," : "")+(isWrite(i)?"write," : "")+(isSeqWrite(i)?"seqWrite," : ""); }


    private class DataFlowItem extends DataFlow.Item {
        private final Map<FieldDef, Integer> initStatus = new HashMap<FieldDef, Integer>(); // immutable map of fields to 3flags

        private DataFlowItem() {
            for (FieldDef f : fields)
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
        private final DataFlowItem INIT = new DataFlowItem();
        private final DataFlowItem CTOR_INIT = new DataFlowItem();
        private ProcedureDecl currDecl;
        public FieldChecker() {
            super(CheckEscapingThis.this.job, CheckEscapingThis.this.ts, CheckEscapingThis.this.nf,
                  true /* forward analysis */,
                  false /* perform dataflow when leaving CodeDecls, not when entering */);

            for (FieldDef field : superFields) {
                CTOR_INIT.initStatus.put(field, build(false, true,true));
            }
        }
        protected Item createInitialItem(FlowGraph graph, Term node, boolean entry) {
            return isCtor() ? CTOR_INIT : INIT;
        }
        protected Item confluence(List items, List itemKeys,
            Term node, boolean entry, FlowGraph graph) {
            if (node instanceof ProcedureDecl) {
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

                    int i_res = isAsync ? afterAsync(i1,i2) : afterIf(i1,i2);
                    res.initStatus.put(key,i_res);
                }
            }
            return res;
        }
        protected FlowGraph initGraph(CodeNode code, Term root) {
            currDecl = (ProcedureDecl)code; // we do not analyze closures (Closure_c) since we require that "this" does not escape there
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
            // todo: closure call: check that all field access are legal
            if (isAssign || n instanceof Field) {
                final Receiver target = isAssign ? ((FieldAssign) n).target() : ((Field) n).target();
                final FieldInstance fi = isAssign ? ((FieldAssign) n).fieldInstance() : ((Field) n).fieldInstance();
                FieldDef field = findField(fi.def());
                if (field!=null && isThis(target)) {
                    res = new DataFlowItem();
                    res.initStatus.putAll(inItem.initStatus);
                    final int valueBefore = inItem.initStatus.get(field);
                    int valueAfter = isAssign ? afterAssign(valueBefore) : afterRead(valueBefore);
                    res.initStatus.put(field,valueAfter);
                }

            } else if (n instanceof ConstructorCall) {
                ConstructorCall ctorCall = (ConstructorCall) n;
                assert (ctorCall.kind()==ConstructorCall.SUPER) : "We do not analyze ctors with 'this()' calls, because they're always correct - everything must be initialized after the 'this()' call";
                //assert res==INIT : "It must be the first statement in the ctor";
                // Note that the super call should be the first statement, but I inline the field-inits before it.

            } else if (n instanceof X10Call) {
                X10Call call = (X10Call) n;
                Receiver receiver = call.target();
                if (isThis(receiver)) {
                    X10MethodDecl_c methodDecl = findMethod(call);
                    if (methodDecl!=null) {
                        final ProcedureDef procDef = methodDecl.procedureInstance();
                        if (!isProperty(procDef)) {
                            res = new DataFlowItem();
                            res.initStatus.putAll(inItem.initStatus);
                            MethodInfo info = allMethods.get(procDef);
                            assert info !=null : methodDecl;
                            for (FieldDef field : fields) {
                                boolean isRead = info.read.contains(field);
                                boolean isWrite = info.write.contains(field);
                                boolean isWriteSeq = info.seqWrite.contains(field);
                                res.initStatus.put(field, afterSeqBlock(res.initStatus.get(field),build(isRead,isWrite, isWriteSeq)));
                            }
                        }
                    }
                }
            } else if (n instanceof Expr && ((Expr)n).type().isBoolean() &&
                    (n instanceof Binary || n instanceof Unary)) {
                final Map map = flowBooleanConditions(trueItem, falseItem, inItem, graph, (Expr) n, succEdgeKeys);
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
                final Set<FieldDef> canReadFrom = ctor ? Collections.EMPTY_SET : info.canReadFrom;
                // can't read from an un-init var in the ctors (I want to catch it here, so I can give the exact position info)
                // can't read from any var not in @NonEscaping(...)
                if (canReadFrom!=null) {
                    for (FieldDef f : fields) {
                        boolean readBefore = isRead(inItem.initStatus.get(f));
                        if (!readBefore && isRead(res.initStatus.get(f))) {
                            if (!canReadFrom.contains(f)) {
                                // wasn't read before, and we read it now (either because of Field access, or X10Call)
                                reportError("Cannot read from field '"+f.name()+"' before it is definitely assigned.",n.position());
                                wasError = true;
                            }
                        }
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
                final ProcedureDef procDef = decl.procedureInstance();
                MethodInfo newInfo = new MethodInfo();
                for (Map.Entry<FieldDef, Integer> pair : dfIn.initStatus.entrySet()) {
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
                            if (!newInfo.seqWrite.contains(f)) {
                                reportError("Field '"+f.name()+"' was not definitely assigned.",f.position());
                            }
                    }
                } else {
                    MethodInfo oldInfo = allMethods.get(procDef);
                    assert oldInfo!=null : n;                    
                    if (hasNonEscapingAnnot(procDef)) {
                        // the read-set should not change (we already reported an error if it did change)
                        newInfo.read.clear();
                        newInfo.read.addAll(oldInfo.read);
                    }
                    if (noWrites(procDef)) {
                        newInfo.write.clear();
                        newInfo.seqWrite.clear();
                    }
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
                        newInfo.canReadFrom = oldInfo.canReadFrom;
                        allMethods.put(procDef,newInfo);
                    }
                }
            }

        }
    }
    private boolean hasNonEscapingAnnot(ProcedureDef def) {
        return X10TypeMixin.getNonEscapingReadsFrom((X10ProcedureDef) def, ts) != null;
    }
    private boolean noWrites(ProcedureDef def) {
        // if it is not a final/private method (which can be the case for @NonEscaping),
        // then the write-set must be empty (because it might be overriden and then the write doesn't happen)
        final boolean hasNonEscapingAnnot = hasNonEscapingAnnot(def);
        return hasNonEscapingAnnot && !isPrivateOrFinal(def);
    }
    private boolean isProperty(ProcedureDef def) {
        final X10Flags flags = X10Flags.toX10Flags(((ProcedureDef_c)def).flags());
        return flags.isProperty();
    }
    private boolean isPrivateOrFinal(ProcedureDef def) {
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
            if (n instanceof X10ClassDecl_c)
                new CheckEscapingThis((X10ClassDecl_c)n,job,
                        (X10TypeSystem)job.extensionInfo().typeSystem());
            return this;
        }
    }


    // we gather info on every private/final/@NonEscaping method
    private static class MethodInfo {
        private Set<FieldDef> canReadFrom = null;
        private final Set<FieldDef> read = new HashSet<FieldDef>();
        private final Set<FieldDef> write = new HashSet<FieldDef>();
        private final Set<FieldDef> seqWrite = new HashSet<FieldDef>();
    }
    private final Job job;
    private final X10NodeFactory nf;
    private final X10TypeSystem ts;
    private final X10ClassDecl_c xlass;
    private final Type xlassType;
    // the keys are either X10ConstructorDecl_c or X10MethodDecl_c
    private final HashMap<ProcedureDef,MethodInfo> allMethods = new LinkedHashMap<ProcedureDef, MethodInfo>(); // all ctors and methods recursively called from allMethods on receiver "this"
    private final ArrayList<ProcedureDecl> dfsMethods = new ArrayList<ProcedureDecl>(); // to accelerate the fix-point alg
    // the set of all VAR and VAL fields, including those in the superclass due to @NonEscaping (we need to check that VAL are read properly, and that VAR are written and read properly.)
    // InitChecker already checks that VAL are assigned in every ctor exactly once (and never assigned in other methods)
    // Therefore we can now treat both VAL and VAR identically.
    private final HashSet<FieldDef> fields = new HashSet<FieldDef>();
    private final HashSet<FieldDef> superFields = new HashSet<FieldDef>(); // after the "super()" call, these fields are initialized
    private boolean wasChange = true, wasError = false; // for fixed point alg
    private HashSet<FieldDef> globalRef = new HashSet<FieldDef>();// There is one exception to the "this cannot escape" rule:  val root = GlobalRef[...](this)

    private void checkGlobalRef(Node n) {
        // you cannot access a globalRef field via this  (but you can assign to them)
        if (n instanceof Field) {
            Field f = (Field) n;
            FieldDef def = f.fieldInstance().def();
            if (isThis(f.target()) && globalRef.contains(def))
                reportError("Cannot use '"+def.name()+"' because a GlobalRef[...](this) cannot be used in a field initializer, constructor, or methods called from a constructor.",n.position());
        }          
    }
    public CheckEscapingThis(X10ClassDecl_c xlass, Job job, X10TypeSystem ts) {
        this.job = job;
        this.ts = ts;
        nf = (X10NodeFactory)ts.extensionInfo().nodeFactory();
        this.xlass = xlass;
        this.xlassType = X10TypeMixin.baseType(xlass.classDef().asType());
        typeCheck();
    }
    private void calcFields() {
        final ClassDef myClassDef = xlass.classDef();
        ClassDef currClass = myClassDef;
        while (currClass!=null) {
            List<FieldDef> list = currClass.fields();
            // a VAR marked with @Uninitialized is not tracked
            List<FieldDef> init = new ArrayList<FieldDef>(list.size());
            for (FieldDef f : list) {
                if (!X10TypeMixin.isUninitializedField((X10FieldDef)f,ts) &&
                    !((X10FieldDef)f).isProperty() && // not tracking property fields (checking property() call was done elsewhere)
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
                            if (!field.flags().flags().isPrivate())
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

        // calculate the set of all fields (including inherited fields)
        calcFields();

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
            if (!globalRef.contains(def)) init.visit(this); // field init are implicitly NonEscaping
        }
        Block newInit = nf.Block(pos,fieldInits);


        // visit every ctor, every @NonEscaping method, and every method recursively called from them, and check that this and super do not escape
        ArrayList<X10ConstructorDecl_c> ctorsForDataFlow = new ArrayList<X10ConstructorDecl_c>();
        for (ClassMember classMember : body.members()) {
            if (classMember instanceof ProcedureDecl) {
                final ProcedureDecl proc = (ProcedureDecl) classMember;
                final X10ProcedureDef def = (X10ProcedureDef)proc.procedureInstance();
                final Block procBody = proc.body();

                if (def instanceof X10MethodDef) {
                    String readsFrom = X10TypeMixin.getNonEscapingReadsFrom(def,ts);

                    // check that overriding perserves the exact @NonEscaping annotation
                    X10MethodDef x10def = (X10MethodDef) def;
                    final MethodInstance instance = x10def.asInstance();
                    final Context emptyContext = ts.emptyContext();
                    final List<MethodInstance> overriddenMethods = ts.overrides(instance, emptyContext);
                    for (MethodInstance overriddenMI : overriddenMethods) {
                        MethodDef overriddenDef = overriddenMI.def();
                        if (overriddenDef==def) continue; // me
                        String overriddenReadsFrom = X10TypeMixin.getNonEscapingReadsFrom((X10MethodDef)overriddenDef,ts);
                        if (overriddenReadsFrom!=null) {
                            if (readsFrom==null || !overriddenReadsFrom.equals(readsFrom)) {
                                reportError("You must annotate "+proc+" with @NonEscaping(\""+overriddenReadsFrom+"\") because it overrides a method annotated with that.", proc.position());
                                break; // one such error msg is enough
                            }
                        }
                    }
                    
                    if (readsFrom==null) continue;

                    // check all the fields in the annotation are found
                    final MethodInfo info = new MethodInfo();
                    info.canReadFrom = new HashSet<FieldDef>();
                    String[] fieldNames = readsFrom.split(",");
                    for (String fieldName : fieldNames) {
                        String trimmed = fieldName.trim();
                        if (trimmed.equals("")) continue;
                        FieldDef field = findField(trimmed);
                        if (field==null) reportError("Could not find field '"+trimmed+"' used in the annotation @NonEscaping.", proc.position());
                        info.canReadFrom.add(field);
                        info.read.add(field);
                    }
                    if (procBody==null) continue; // for native methods/ctors, we don't have a body

                    allMethods.put(proc.procedureInstance(), info);
                    dfsMethods.add(proc);

                } else {
                    if (procBody==null) continue;
                    assert proc instanceof X10ConstructorDecl_c : proc;
                    final X10ConstructorDecl_c ctor = (X10ConstructorDecl_c) proc;
                    // ctors are implicitly NonEscaping
                    if (isCallingOtherCtor(ctor)) {
                        // ignore in dataflow ctors that call other ctors (using "this(...)"). We can reuse ConstructorCallChecker, but for better efficiency, we just check it directly
                    } else {
                        // add field initializers to all ctors
                        ctorsForDataFlow.add(ctor);
                    }
                }
                procBody.visit(this);

            }
        }
        if (fields.size()==0) return; // done! all fields have an init, thus all reads are legal (and no writes must be done).

        // do init for the fixed point alg
        for (Map.Entry<ProcedureDef, MethodInfo> entry : allMethods.entrySet()) {
            MethodInfo info = entry.getValue();
            if (!noWrites(entry.getKey())) {
                info.write.addAll(fields);
                info.seqWrite.addAll(fields);
            }
        }
        // run fix point alg: ctors do not need to be in the fixed point alg because nobody can call them directly
        final FieldChecker fieldChecker = new FieldChecker();
        while (wasChange && !wasError) {
            wasChange =false;
            // do a DFS: starting from private/final methods, and then the ctors (this will reach a fixed-point fastest)
            for (ProcedureDecl p : dfsMethods)
                fieldChecker.dataflow(p);
        }
        // handle ctors and field initializers        
        //fieldChecker.dataflow(newInit); // todo: make a new method for field-init, and the ctors will call it at the begining
        for (X10ConstructorDecl_c ctor : ctorsForDataFlow) {
            X10ConstructorDecl_c newCtor = (X10ConstructorDecl_c) ctor.body( nf.Block(pos,newInit,ctor.body()) );
            fieldChecker.dataflow(newCtor);
        }
    }
    private FieldDef findField(String name) {
        for (FieldDef fieldDef : fields)
            if (fieldDef.name().toString().equals(name)) {
                return fieldDef;
            }
        return null;
    }
    private static boolean isCallingOtherCtor(X10ConstructorDecl_c ctor) {
        final Block ctorBody = ctor.body();
        assert ctorBody!=null;
        final List<Stmt> stmts = ctorBody.statements();
        for (Stmt s : stmts) {
            if (s instanceof ConstructorCall) {
                ConstructorCall cc = (ConstructorCall) s;
                if (cc.kind() == ConstructorCall.THIS) {
                    return true;
                }
            }
        }
        return false;
    }
    private FieldDef findField(FieldDef def) {
        for (FieldDef f : fields)
            if (f.equalsImpl(def)) return f;
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
                if (isProperty(procDef)) {
                    // property-method calls are ok
                } else {
                    // the method must be final or private or NonEscaping
                    String readsFrom = X10TypeMixin.getNonEscapingReadsFrom(procDef,ts);
                    if (readsFrom==null && !isPrivateOrFinal(procDef))
                        reportError("The call "+call+" is illegal because you can only call private/final/@NonEscaping methods from a NonEscaping method (such as a constructor, a field initializer, or a @NonEscaping method)", call.position());
                    X10MethodDecl_c method = findMethod(call);
                    if (method==null) {
                        if (readsFrom==null)
                            reportError("The call "+call+" is illegal because you can only call @NonEscaping methods of a superclass from a constructor or from methods called from a constructor",call.position());
                    } else {
                        ProcedureDef pd = method.procedureInstance();
                        if (allMethods.containsKey(pd)) {
                            // we already analyzed this method (or it is an error method)
                        } else {
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
    private boolean isThis(Node n) {
        if (n==null || !(n instanceof Special)) return false;
        final Special special = (Special) n;
        return //special.kind()==Special.THIS && // both this and super cannot escape
               ts.typeEquals(X10TypeMixin.baseType(special.type()), xlassType,null);
    }
}