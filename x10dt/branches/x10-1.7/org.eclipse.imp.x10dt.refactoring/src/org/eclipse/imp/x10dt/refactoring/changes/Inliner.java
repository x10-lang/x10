package org.eclipse.imp.x10dt.refactoring.changes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.CodeBlock;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.SourceFile;
import polyglot.types.MethodInstance;
import polyglot.types.TypeSystem;
import polyglot.types.VarDef;
import polyglot.types.VarInstance;
import polyglot.visit.NodeVisitor;

/**
 * Inlines a given method call.
 */
public class Inliner {
    private class InlinePerformer extends NodeVisitor {
        private final Block fBlock;

        public InlinePerformer(Block block) {
            fBlock= block;
        }

        @Override
        public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
            // TODO Translate return stmts to assignments to the receptacle for the method return value
            return super.leave(parent, old, n, v);
        }

        public Node perform() {
            Node result= fBlock.visit(this);
            return result;
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

    public Node perform() {
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
        SubstitutionPerformer subPerf= new SubstitutionPerformer(subs);

        Block blockSubbed= (Block) subPerf.perform(md.body(), fRoot);

        InlinePerformer inPerf= new InlinePerformer(blockSubbed);

        Node newBody= inPerf.perform();

        return newBody;
    }

    private MethodDecl findDecl(final MethodInstance mi, SourceFile root) {
        final MethodDecl[] result= new MethodDecl[1];
        root.visit(new NodeVisitor() {
            @Override
            public NodeVisitor enter(Node n) {
                if (n instanceof MethodDecl) {
                    MethodDecl md= (MethodDecl) n;
                    if (md.methodDef().asInstance().equals(mi)) {
                        result[0]= md;
                    }
                }
                return super.enter(n);
            }
        });
        return result[0];
    }
}
