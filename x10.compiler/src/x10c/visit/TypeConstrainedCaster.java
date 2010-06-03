package x10c.visit;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Call;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.NullLit;
import polyglot.ast.Stmt;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.ClosureCall;
import x10.ast.X10NodeFactory;
import x10.ast.X10Return_c;
import x10.types.ParameterType;
import x10.types.X10ClassType;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.constraints.SubtypeConstraint;

public class TypeConstrainedCaster extends ContextVisitor {
    
    private final X10TypeSystem xts;
    private final X10NodeFactory xnf;

    public TypeConstrainedCaster(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (X10TypeSystem) ts;
        xnf = (X10NodeFactory) nf;
    }
    
    @Override
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {

        Expr e = null;
        if (n instanceof MethodDecl) {
            MethodDecl md = (MethodDecl) n;
            if (md.body() == null) {
                return n;
            }
            List<Stmt> stmts = md.body().statements();
            for (Stmt stmt : stmts) {
                if (stmt instanceof X10Return_c) {
                    X10Return_c r = (X10Return_c) stmt;
                    if (r.expr().type() != md.returnType().type()) {
                        e = r.expr();
                    }
                }
            }
        }
        
        if (e == null) {
            if (!(old instanceof Expr)) {
                return n;
            }
            e = (Expr) old;
        }
        
        if (!(X10TypeMixin.baseType(e.type()) instanceof ParameterType)) {
            return n;
        }

        Type superType = null;
        if (context.currentClass() instanceof X10ClassType) {
            List<SubtypeConstraint> terms = ((X10ClassType) context.currentClass()).x10Def().typeBounds().get().terms();
            for (SubtypeConstraint sc : terms) {
                if (sc.subtype().typeEquals(X10TypeMixin.baseType(e.type()), context) && sc.subtype() instanceof ParameterType) {
                    superType = sc.supertype();
                }
            }
        }
        if (superType == null) {
            return n;
        }

        if (n instanceof MethodDecl) {
            MethodDecl md = (MethodDecl) n;
            List<Stmt> stmts = md.body().statements();
            List<Stmt> newstmts = new ArrayList<Stmt>();
            for (Stmt stmt : stmts) {
                if (stmt instanceof X10Return_c) {
                    X10Return_c rt = (X10Return_c) stmt;
                    Expr expr = rt.expr();
                    if (expr.type() != md.returnType().type()) {
                        newstmts.add(rt.expr(cast(expr, md.returnType().type())));
                        return md.body(md.body().statements(newstmts));
                    }
                    return n;
                }
                newstmts.add(stmt);
            }
        }
        
        if (n instanceof NullLit) {
            return n;
        }

        Type toType = null;
        if (parent instanceof Call) {
            Call p = (Call) parent;
            for (int i = 0; i < p.arguments().size(); i++) {
                if (e == p.arguments().get(i)) {
                    if (p.methodInstance().formalTypes().get(i) == superType) {
                        toType = superType;
                        break;
                    }
                }
            }
        }
        else if (parent instanceof New) {
            New p = (New) parent;
            for (int i = 0; i < p.arguments().size(); i++) {
                if (e == p.arguments().get(i)) {
                    if (p.constructorInstance().formalTypes().get(i) == superType) {
                        toType = superType;
                        break;
                    }
                    break;
                }
            }
        }
        else if (parent instanceof ConstructorCall) {
            ConstructorCall p = (ConstructorCall) parent;
            for (int i = 0; i < p.arguments().size(); i++) {
                if (e == p.arguments().get(i)) {
                    if (p.constructorInstance().formalTypes().get(i) == superType) {
                        toType = superType;
                        break;
                    }
                }
            }
        }
        else if (parent instanceof ClosureCall) {
            ClosureCall p = (ClosureCall) parent;
            for (int i = 0; i < p.arguments().size(); i++) {
                if (e == p.arguments().get(i)) {
                    if (p.closureInstance().formalTypes().get(i) == superType) {
                        toType = superType;
                        break;
                    }
                }
            }

        }
        else if (parent instanceof LocalDecl) {
            LocalDecl p = (LocalDecl) parent;
            if (e == p.init()) {
                if (p.localDef().asInstance().type() == superType) {
                    toType = superType;
                }
            }
        }
        else if (parent instanceof FieldDecl) {
            FieldDecl p = (FieldDecl) parent;
            if (e == p.init()) {
                if (p.fieldDef().asInstance().type() == superType) {
                    toType = superType;
                }
            }

        }
        else if (parent instanceof Assign) {
            Assign p = (Assign) parent;
            if (p.operator() == Assign.ASSIGN) {
                if (e == p.right()) {
                    if (p.leftType() == superType) {
                        toType = superType;
                    }
                }
            }
        }

        if (toType != null) {
            return cast((Expr) n, toType);
        }
        return n;
    }

    private Expr cast(Expr n, Type toType) throws SemanticException {
        Expr e = xnf.X10Cast(n.position(), xnf.CanonicalTypeNode(n.position(), toType), n);
        return (Expr) e.del().disambiguate(this).typeCheck(this).checkConstants(this);
    }
}
