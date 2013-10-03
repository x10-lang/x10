package x10cpp.visit;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.frontend.Job;
import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.SettableAssign;
import x10.ast.StmtExpr;
import x10.ast.Tuple;
import x10.util.AltSynthesizer;

/**
 * A Visitor that replaces Tuple expressions with
 * an equivalent StatementExpr.
 * 
 * We do this to simplify C++ codegen and avoid needing
 * to use StmtExprs in codegen (since they are not supported 
 * by all C++ compilers)
 */
public final class TupleRemover extends ContextVisitor {

    private final TypeSystem xts;
    private AltSynthesizer syn;
    
    private static final Name ALLOC_RAIL     = Name.make("allocRailUninitialized");

    public TupleRemover(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = ts;
        syn = new AltSynthesizer(ts, nf);
    }

    @Override
    public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) {
        if (n instanceof Tuple) {
            Tuple expr = (Tuple)n;
            Position pos = expr.position();
            List<Stmt> stmts = new ArrayList<Stmt>();
            
            // Allocate an uninitialized Rail of the right type
            List<Type> allocTypeList = new ArrayList<Type>();
            Type elemType = Types.getParameterType(expr.type(), 0);
            allocTypeList.add(elemType);
            Expr allocCall = syn.createStaticCall(pos, xts.Unsafe(), ALLOC_RAIL, allocTypeList, 
                                                  syn.createLongLit(expr.arguments().size()));
            Type tmpT = (Type)expr.type().copy();
            LocalDecl railLDecl = syn.createLocalDecl(expr.position(), Flags.NONE, syn.createTemporaryName(), expr.type(), allocCall);
            stmts.add(railLDecl);
            
            // Initialize the rail by calling set one element at a time.
            long index = 0;
            for (Expr elem : expr.arguments()) {
                Expr set = syn.createInstanceCall(pos, syn.createLocal(pos, railLDecl), SettableAssign.SET, context, syn.createLongLit(index++), elem);
                stmts.add(syn.createEval(set));
            }
            
            // Bundle up in a StmtExpr
            return syn.createStmtExpr(pos, stmts, syn.createLocal(pos, railLDecl));
        }
        return n;
    }
    
}
