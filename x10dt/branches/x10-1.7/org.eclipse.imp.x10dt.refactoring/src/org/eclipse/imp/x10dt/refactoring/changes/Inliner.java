package org.eclipse.imp.x10dt.refactoring.changes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.CodeBlock;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.If;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Return;
import polyglot.ast.SourceFile;
import polyglot.ast.Stmt;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.TypeSystem;
import polyglot.types.VarDef;
import polyglot.types.VarInstance;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;

/**
 * Inlines a given method call.
 */
public class Inliner {
    /**
     * Performs the necessary transformations on the inlined method body.
     * Not yet prepared to handle value-producing methods in arbitrary expression contexts.
     */
    private class InlinePerformer extends NodeVisitor {
        private final Block fBlock;

        private boolean fHasReturns;

        private final Id fLabelID;

        private final Expr fAssignTarget;

        public InlinePerformer(Block block, Expr assignTarget) {
            fBlock= block;
            fAssignTarget= assignTarget;
            fLabelID= fNodeFactory.Id(fBlock.position(), Name.makeFresh("label"));
        }

        @Override
        public NodeVisitor enter(Node n) {
            return super.enter(n);
        }
        @Override
        public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
            if (n instanceof Return) {
                Return ret= (Return) n;
                Expr expr= ret.expr();

                if (expr != null) {
                    // Translate return stmts to assignments to the receptacle for the method return value
                    Assign ass= fNodeFactory.Assign(Position.COMPILER_GENERATED, fAssignTarget, Assign.ASSIGN, expr);
                    return ass;
                }
                // Translate return stmts to break stmts to the appropriate label for the enclosing block
                Stmt break_ = fNodeFactory.Break(Position.COMPILER_GENERATED, fLabelID);
                fHasReturns= true;
                return break_;
            }
            return super.leave(parent, old, n, v);
        }

        public Node perform() {
            Stmt result= (Block) fBlock.visit(this);
            if (fHasReturns) {
                result= fNodeFactory.Labeled(Position.COMPILER_GENERATED, fLabelID, result);
            }
            return result;
        }
    }

    private class Simplifier extends NodeVisitor {
        private Map<Expr,Object> fEvalResults= new HashMap<Expr, Object>();

        @Override
        public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
            if (n instanceof Binary) {
                Binary b= (Binary) n;
                Binary.Operator op= b.operator();
                Expr left= b.left();
                Expr right= b.right();
                if (op == Binary.COND_AND || op == Binary.COND_OR) {
                    if (fEvalResults.containsKey(left) && fEvalResults.containsKey(right)) {
                        Boolean leftVal= (Boolean) fEvalResults.get(left);
                        Boolean rightVal= (Boolean) fEvalResults.get(right);
                        Boolean result= (op == Binary.COND_AND) ? leftVal && rightVal : leftVal || rightVal;

                        fEvalResults.put(b, result);
                    }
                } else if (op == Binary.EQ || op == Binary.GE || op == Binary.GT || op == Binary.LE || op == Binary.LT || op == Binary.NE) {
                    if (left.type().isInt() && right.type().isInt() && fEvalResults.containsKey(left) && fEvalResults.containsKey(right)) {
                        Integer leftVal= (Integer) fEvalResults.get(left);
                        Integer rightVal= (Integer) fEvalResults.get(right);
                        boolean result= false;

                        if (op == Binary.EQ) {
                            result= leftVal == rightVal;
                        } else if (op == Binary.GE) {
                            result= leftVal >= rightVal;
                        } else if (op == Binary.GT) {
                            result= leftVal > rightVal;
                        } else if (op == Binary.LE) {
                            result= leftVal <= rightVal;
                        } else if (op == Binary.LT) {
                            result= leftVal < rightVal;
                        } else if (op == Binary.NE) {
                            result= leftVal != rightVal;
                        }
                        fEvalResults.put(b, result);
                    }
                }
            } else if (n instanceof If) {
                If ifStmt= (If) n;
                Stmt thenStmt= ifStmt.consequent();
                Stmt elseStmt= ifStmt.alternative();
                Expr cond= ifStmt.cond();
                if (fEvalResults.containsKey(cond)) {
                    return (Boolean) fEvalResults.get(cond) ? thenStmt : elseStmt;
                }
            } else if (n instanceof Expr) {
                Expr e= (Expr) n;
                if (e.isConstant()) {
                    fEvalResults.put(e, e.constantValue());
                }
            }
            return super.leave(parent, old, n, v);
        }
    }

    private final Call fCall;
    private final CodeBlock fOwner;
    private final SourceFile fRoot;
    private final NodeFactory fNodeFactory;
    private final TypeSystem fTypeSystem;

    public Inliner(Call call, CodeBlock owner, SourceFile root, NodeFactory nf, TypeSystem ts) {
        fCall= call;
        fOwner= owner;
        fRoot= root;
        fNodeFactory= nf;
        fTypeSystem= ts;
    }

    public Node perform(boolean simplify) {
        List<Expr> actuals= fCall.arguments();
        MethodInstance mi= fCall.methodInstance();
        MethodDecl md= findDecl(mi, fRoot);

        if (md == null) {
            return fCall; // no transformation
        }
        if (mi.returnType() != fTypeSystem.Void()) {
            return fCall; // for now, don't handle value-returning methods
        }
        List<Formal> formals= md.formals();
        Map<VarInstance<VarDef>,Node> subs= new HashMap<VarInstance<VarDef>, Node>(formals.size());

        for(int i=0; i < formals.size(); i++) {
            Formal formal= formals.get(i);
            Expr actual= actuals.get(i);

            subs.put((VarInstance) formal.localDef().asInstance(), actual);
        }
        Expr assignTarget= null; // TODO Create a local var if needed
        SubstitutionPerformer subPerf= new SubstitutionPerformer(subs);
        Block blockSubbed= (Block) subPerf.perform(md.body(), fRoot);
        InlinePerformer inPerf= new InlinePerformer(blockSubbed, mi.returnType().isVoid() ? null : assignTarget);
        Node result= inPerf.perform();

        if (simplify) {
            Simplifier simplifier= new Simplifier();

            result= result.visit(simplifier);
        }

        return result;
    }

    private MethodDecl findDecl(final MethodInstance mi, SourceFile root) {
        final MethodDecl[] result= new MethodDecl[1];
        root.visit(new NodeVisitor() {
            @Override
            public NodeVisitor enter(Node n) {
                if (n instanceof MethodDecl) {
                    MethodDecl md= (MethodDecl) n;
                    if (md.methodDef().equals(mi.def())) {
                        result[0]= md;
                    }
                }
                return super.enter(n);
            }
        });
        return result[0];
    }
}
