package x10c.visit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ast.Assign;
import polyglot.ast.Block;
import polyglot.ast.Block_c;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldDecl;
import polyglot.ast.Id;
import polyglot.ast.IntLit;
import polyglot.ast.IntLit.Kind;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.Local_c;
import polyglot.ast.Loop;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ast.Try;
import polyglot.frontend.Job;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.LocalDef_c;
import polyglot.types.LocalInstance;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.VarDef;
import polyglot.types.VarDef_c;
import polyglot.util.Pair;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.Closure;
import x10.ast.ClosureCall;
import x10.ast.SettableAssign;
import x10.ast.SettableAssign_c;
import x10.ast.X10Call;
import x10.ast.X10CanonicalTypeNode;
import x10.ast.X10LocalAssign_c;
import x10.ast.X10Local_c;
import x10.ast.X10NodeFactory;
import x10.ast.X10Special;
import x10.extension.X10Ext;
import x10.extension.X10Ext_c;
import x10.types.ParameterType;
import x10.types.X10ClassType;
import x10.types.X10Flags;
import x10.types.X10MethodInstance;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10c.ast.BackingArray;
import x10c.ast.BackingArrayAccess;
import x10c.ast.BackingArrayNewArray;
import x10c.ast.X10CBackingArrayAccess_c;
import x10c.ast.X10CNodeFactory_c;
import x10c.types.X10CTypeSystem_c;

public class AsyncInitializer extends ContextVisitor {

    private final X10CTypeSystem_c xts;
    private final X10CNodeFactory_c xnf;

    private List<Expr> initVarList = null;
    private int nestLevel = 0;

    public AsyncInitializer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (X10CTypeSystem_c) ts;
        xnf = (X10CNodeFactory_c) nf;
    }

    @Override
    protected NodeVisitor enterCall(Node parent, Node n) throws SemanticException {
        if (n instanceof X10Call) {
            X10Call call = (X10Call)n;
            X10MethodInstance mi = (X10MethodInstance) call.methodInstance();
            if (mi.container().isClass() && ((X10ClassType) mi.container().toClass()).
                    fullName().toString().equals("x10.lang.Runtime")) {
                // adjust nest level counter of finish block
                if (mi.signature().startsWith("startFinish"))
                    nestLevel++;
                else if (mi.signature().startsWith("stopFinish"))
                    nestLevel--;
            }
        }
        return super.enterCall(parent, n);
    }

    @Override
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        if (!(n instanceof Try))
            return n;

        X10Ext_c ext = (X10Ext_c) n.ext();
        Set<VarDef> asyncInitVal = ext.asyncInitVal;
        if (asyncInitVal == null)
            return n;

        // process only the outermost "finish"
        if (nestLevel > 0)
            return n;

        Try tcfBlock = (Try)n;
        // n.dump(System.err);

        final Map<VarDef, Id> initValToId = new HashMap<VarDef, Id>();

        // box vars under the try-catch-finally block
        tcfBlock = replaceVariables(tcfBlock, asyncInitVal, initValToId);

        List<Stmt> stmts = new ArrayList<Stmt>();
        // declare boxed vars
        for (VarDef var : asyncInitVal) {
            stmts.add(genBoxDeclaration(var, initValToId, n));
        }
        stmts.add(tcfBlock);
        // store the boxed vars back into original final vars
        for (VarDef var : asyncInitVal) {
            stmts.add(genBackingStore(var, initValToId, n));
        }

        Block bb = xnf.Block(tcfBlock.position(), stmts);
        return bb;
    }

    private Try replaceVariables(Try tcfBlock, final Set<VarDef> asyncInitVal, final Map<VarDef, Id> initValToId) {

        Try newBlock = (Try)tcfBlock.visit(new NodeVisitor() {
            @Override
            public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
                if (n instanceof X10LocalAssign_c) {
                    X10LocalAssign_c la = (X10LocalAssign_c) n;
                    Type type = X10TypeMixin.baseType(la.type());
                    Expr left = la.left();

                    if (!(left instanceof Local)) {
                        return n;
                    }

                    VarDef initVal = checkInitValList((Local)left, asyncInitVal);
                    if (initVal == null)
                        return n;

                    Id id = getId(initVal, initValToId, n);
                    BackingArray ba = xnf.BackingArray(n.position(), id, createArrayType(type), left);
                    LocalDef ldef = xts.localDef(n.position(), xts.NoFlags(), Types.ref(type), id.id());
                    IntLit idx0 = xnf.IntLit(n.position(), IntLit.INT, 0);
                    return xnf.BackingArrayAccessAssign(n.position(), xnf.Local(n.position(), id).localInstance(ldef.asInstance()), 
                                 idx0, la.operator(), la.right()).type(type);
                }
                if (n instanceof X10Local_c) {
                    if ((parent instanceof X10LocalAssign_c) && n.equals(((X10LocalAssign_c)parent).left()))
                        // should be processed in the above
                        return n;

                    VarDef initVal = checkInitValList((Local)n, asyncInitVal);
                    if (initVal == null)
                        return n;

                    Type type = X10TypeMixin.baseType(((X10Local_c) n).type());

                    Id id = getId(initVal, initValToId, n);
                    LocalDef ldef = xts.localDef(n.position(), xts.NoFlags(), Types.ref(type), id.id());
                    IntLit idx0 = xnf.IntLit(n.position(), IntLit.INT, 0);
                    return xnf.BackingArrayAccess(n.position(), xnf.Local(n.position(), id).localInstance(ldef.asInstance()), idx0, type);
                }
                return n;
            };
        });
        // newBlock.dump(System.err);
        return newBlock;
    }

    private Id getId(VarDef initVal, Map<VarDef, Id> initValToId, Node n) {
        Id id = initValToId.get(initVal);
        if (id == null) {
            id = xnf.Id(n.position(), Name.makeFresh("_$$"+initVal.name()));
            initValToId.put(initVal, id);
        }
        return id;
    }

    private VarDef checkInitValList(Local lv, Set<VarDef> asyncInitVal) {

        Name name1 = lv.name().id();
        Flags flags1 = lv.localInstance().flags();
        Type t1 = lv.localInstance().type();

        for (VarDef var : asyncInitVal) {
            Name name2 = ((LocalDef_c)var).name();
            Flags flags2 = ((LocalDef_c)var).flags();
            Type t2 = ts.createLocalInstance(lv.position(), Types.ref((LocalDef_c)var)).type();
            if (name1.toString().equals(name2.toString()) && flags1.equals(flags2) && t1.typeEquals(t2, context))
                // found the match
                return var;
        }
        return null;
    }

    private Type createArrayType(Type t) {
        return xts.createBackingArray(t.position(), Types.ref(t));
    }

    private Stmt genBoxDeclaration(VarDef initVal, Map<VarDef, Id> initValToId, Node n) {
        Type type = ts.createLocalInstance(n.position(), Types.ref((LocalDef_c)initVal)).type();
        Type arrayType = createArrayType(type);
        X10CanonicalTypeNode tnArray = xnf.X10CanonicalTypeNode(n.position(), arrayType);

        // right-hand side (array creation)
        List<Expr> dims = new ArrayList<Expr>();
        dims.add(xnf.IntLit(n.position(), IntLit.INT, 1));
        BackingArrayNewArray ba = xnf.BackingArrayNewArray(n.position(), tnArray, dims, 0, arrayType);

        // creation of declaration node
        Id id = getId(initVal, initValToId, n);
        LocalDef ldef = xts.localDef(n.position(), xts.Final(), Types.ref(arrayType), id.id());
        LocalDecl ld = xnf.LocalDecl(n.position(), xnf.FlagsNode(n.position(), Flags.FINAL), tnArray, id, ba)
                .localDef(ldef);

        // ld.dump(System.err);
        return ld;
    }

    private Stmt genBackingStore(VarDef initVal, Map<VarDef, Id> initValToId, Node n) {
        Name name = ((LocalDef_c)initVal).name();
        Type type = ts.createLocalInstance(n.position(), Types.ref((LocalDef_c)initVal)).type();

        // right-hand side (boxed var)
        Id id = getId(initVal, initValToId, n);
        LocalDef rdef = xts.localDef(n.position(), xts.NoFlags(), Types.ref(type), id.id());
        BackingArrayAccess right = xnf.BackingArrayAccess(n.position(), xnf.Local(n.position(), id).localInstance(rdef.asInstance()), 
                                                          xnf.IntLit(n.position(), IntLit.INT, 0), type);
        // left-hand side (original final var)
        LocalDef ldef = xts.localDef(n.position(), xts.NoFlags(), Types.ref(type), name);
        Local left = (Local) xnf.Local(n.position(), xnf.Id(n.position(), name)).localInstance(ldef.asInstance()).type(type);

        // creation of assignment node
        Expr expr = xnf.LocalAssign(n.position(), left, Assign.ASSIGN, right).type(type);
        Stmt stmt = xnf.Eval(n.position(), expr);

        // stmt.dump(System.err);
        return stmt;
    }
}
