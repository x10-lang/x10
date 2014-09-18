/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10rose.visit;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.ast.Allocation_c;
import polyglot.ast.AmbReceiver_c;
import polyglot.ast.Assert_c;
import polyglot.ast.Block_c;
import polyglot.ast.Branch_c;
import polyglot.ast.Case_c;
import polyglot.ast.Catch_c;
import polyglot.ast.ClassLit_c;
import polyglot.ast.Empty_c;
import polyglot.ast.Eval_c;
import polyglot.ast.FieldAssign_c;
import polyglot.ast.For_c;
import polyglot.ast.Import_c;
import polyglot.ast.IntLit_c;
import polyglot.ast.Labeled_c;
import polyglot.ast.LocalAssign_c;
import polyglot.ast.LocalClassDecl_c;
import polyglot.ast.Node;
import polyglot.ast.Node_c;
import polyglot.ast.NullLit_c;
import polyglot.ast.PackageNode_c;
import polyglot.ast.Return_c;
import polyglot.ast.SourceFile_c;
import polyglot.ast.SwitchBlock_c;
import polyglot.ast.Switch_c;
import polyglot.ast.Throw_c;
import polyglot.ast.Try_c;
import polyglot.util.CodeWriter;
import polyglot.util.StringUtil;
import x10.ast.AssignPropertyCall_c;
import x10.ast.Async_c;
import x10.ast.AtEach_c;
import x10.ast.AtExpr_c;
import x10.ast.AtHomeExpr_c;
import x10.ast.AtHomeStmt_c;
import x10.ast.AtStmt_c;
import x10.ast.Atomic_c;
import x10.ast.ClosureCall_c;
import x10.ast.Closure_c;
import x10.ast.DepParameterExpr_c;
import x10.ast.Finish_c;
import x10.ast.ForLoop_c;
import x10.ast.HasZeroTest_c;
import x10.ast.Here_c;
import x10.ast.LocalTypeDef_c;
import x10.ast.Next_c;
import x10.ast.ParExpr_c;
import x10.ast.PropertyDecl_c;
import x10.ast.SettableAssign_c;
import x10.ast.StmtExpr_c;
import x10.ast.StmtSeq_c;
import x10.ast.SubtypeTest_c;
import x10.ast.Tuple_c;
import x10.ast.TypeDecl_c;
import x10.ast.When_c;
import x10.ast.X10Binary_c;
import x10.ast.X10BooleanLit_c;
import x10.ast.X10Call_c;
import x10.ast.X10CanonicalTypeNode_c;
import x10.ast.X10Cast_c;
import x10.ast.X10CharLit_c;
import x10.ast.X10ClassBody_c;
import x10.ast.X10ClassDecl_c;
import x10.ast.X10Conditional_c;
import x10.ast.X10ConstructorCall_c;
import x10.ast.X10ConstructorDecl_c;
import x10.ast.X10Do_c;
import x10.ast.X10FieldDecl_c;
import x10.ast.X10Field_c;
import x10.ast.X10FloatLit_c;
import x10.ast.X10Formal_c;
import x10.ast.X10If_c;
import x10.ast.X10Instanceof_c;
import x10.ast.X10LocalDecl_c;
import x10.ast.X10Local_c;
import x10.ast.X10MethodDecl_c;
import x10.ast.X10New_c;
import x10.ast.X10Special_c;
import x10.ast.X10StringLit_c;
import x10.ast.X10Unary_c;
import x10.ast.X10While_c;
import x10.visit.X10DelegatingVisitor;

public class DotGenerator extends X10DelegatingVisitor {

    Node parent;
    CodeWriter w;
   
    Map<Node, String> dotNode = new HashMap<Node, String>();
    int counter = 0;
    
    DotGenerator(CodeWriter w, Node parent) {
        this.parent = parent;
        this.w = w;
    }

    String getDotNode(Node n) {
        String id;
        if (dotNode.containsKey(n)) {
            id = dotNode.get(n);
        } else {
            id = Integer.toString(counter++);
            dotNode.put(n, id);
        }
        return id;
    }
    
    void createDotNode(Node n, String name, String... extra) {
        w.write(getDotNode(n) + " [");
        Map<String, String> extraMap = new HashMap<String, String>();
        // defaults
        extraMap.put("style", "filled");
        extraMap.put("penwidth", "2.0");
        extraMap.put("label", n.getClass().toString() + (name != null ? "\\n" + name : ""));
        extraMap.put("shape", "box");
        extraMap.put("color", "#000000");
        extraMap.put("fillcolor", "#FFFFFF");
        // override defaults here
        for (String s : extra) {
            if (!s.contains("=")) {
                System.err.println("Unrecognised dot node option: " + s);
                continue;
            }
            String before = s.substring(0, s.indexOf("="));
            String after = s.substring(s.indexOf("=") + 1);
            extraMap.put(before, after);
        }
        for (String key : extraMap.keySet()) {
            w.writeln(key + "=\"" + extraMap.get(key) + "\", ");
        }
        w.writeln("];");

        if (parent != null)
            w.writeln(getDotNode(parent) + " -> " + getDotNode(n));

        try {
            w.flush();
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    void visitChild(Node p, Node n) {
        if (n == null)
            return;
        new DotGenerator(w, p).visitAppropriate(n);
    }

    void visitChildren(Node p, List<? extends Node> l) {
        if (l == null)
            return;
        for (Node n : l)
            visitChild(p, n);
    }

    public void visit(Node_c n) {
        createDotNode(n, null, "fillcolor=#FF0000", "fontcolor=#ffffff", "shape=Mdiamond");
        System.err.println("UNRECOGNISED NODE in DotTranslator: " + n.getClass());
    }

    public void visit(SourceFile_c n) {
        createDotNode(n, n.source().path(), "shape=folder", "fillcolor=#000040", "fontcolor=#ffffff");
        visitChildren(n, n.decls());
    }

    public void visit(Import_c n) {
        createDotNode(n, n.kind() + " " + n.name().toString());
    }

    public void visit(PackageNode_c n) {
        createDotNode(n, n.package_().get().toString());
    }

    public void visit(X10ClassDecl_c n) {
        createDotNode(n, n.name().id().toString(), "shape=house", "fillcolor=#004000", "fontcolor=#ffffff");
        visitChildren(n, n.typeParameters());
        visitChildren(n, n.properties());
        visitChild(n, n.classInvariant());
        // visitChild(n, n.superClass());
        visitChildren(n, n.interfaces());
        visitChild(n, n.body());
    }

    public void visit(LocalClassDecl_c n) {
        createDotNode(n, null);
        visitChild(n, n.decl());
    }

    public void visit(X10ClassBody_c n) {
        createDotNode(n, null, "shape=house");
        visitChildren(n, n.members());
    }

    public void visit(X10MethodDecl_c n) {
        createDotNode(n, n.name().id().toString(), "shape=trapezium", "fillcolor=#FFC0A0");
        visitChildren(n, n.formals());
        visitChild(n, n.guard());
        visitChild(n, n.offerType());
        visitChildren(n, n.throwsTypes());
        visitChild(n, n.body());
    }

    public void visit(X10Formal_c n) {
        createDotNode(n, n.name().id().toString(), "shape=trapezium", "fillcolor=#FFC0A0");
        visitChild(n, n.type());
    }

    public void visit(X10Call_c n) {
        createDotNode(n, n.name().id().toString(), "fillcolor=#FFC0A0", "shape=oval");
        visitChild(n, n.target());
        visitChildren(n, n.typeArguments());
        visitChildren(n, n.arguments());
    }

    public void visit(X10ConstructorDecl_c n) {
        createDotNode(n, n.name().id().toString(), "shape=trapezium", "fillcolor=#FFFFA0");
        visitChildren(n, n.formals());
        visitChild(n, n.guard());
        visitChild(n, n.offerType());
        visitChildren(n, n.throwsTypes());
        visitChild(n, n.body());
    }

    public void visit(X10ConstructorCall_c n) {
        createDotNode(n, null, "fillcolor=#FFFFC0", "shape=oval");
        visitChild(n, n.target());
        visitChildren(n, n.typeArguments());
        visitChildren(n, n.arguments());
    }

    public void visit(Block_c n) {
        createDotNode(n, null, "fillcolor=#000000", "fontcolor=#ffffff");
        visitChildren(n, n.statements());
    }

    public void visit(StmtSeq_c n) {
        createDotNode(n, null, "fillcolor=#000000", "fontcolor=#ffffff");
        visitChildren(n, n.statements());
    }

    public void visit(AssignPropertyCall_c n) {
        createDotNode(n, null);
        visitChildren(n, n.arguments());
    }

    public void visit(Empty_c n) {
        createDotNode(n, null, "fillcolor=#000000", "fontcolor=#ffffff");
    }

    public void visit(X10CanonicalTypeNode_c n) {
        createDotNode(n, n.nameString(), "shape=oval", "fillcolor=#C0FFC0");

    }

    public void visit(Return_c n) {
        createDotNode(n, null, "fillcolor=#000000", "fontcolor=#ffffff");
        visitChild(n, n.expr());
    }

    public void visit(X10Binary_c n) {
        createDotNode(n, n.operator().toString(), "shape=oval");
        visitChild(n, n.left());
        visitChild(n, n.right());
    }

    public void visit(X10Unary_c n) {
        createDotNode(n, n.operator().toString(), "shape=oval");
        visitChild(n, n.expr());
    }

    public void visit(ParExpr_c n) { // parentheses
        createDotNode(n, "( )", "shape=oval");
        visitChild(n, n.expr());
    }

    public void visit(X10Special_c n) {
        createDotNode(n, n.kind().toString(), "shape=oval", "fillcolor=#C0C0FF");
    }

    public void visit(Here_c n) {
        createDotNode(n, "here", "shape=oval", "fillcolor=#C0C0FF");
    }

    public void visit(X10Local_c n) {
        createDotNode(n, n.name().id().toString(), "shape=oval", "fillcolor=#C0C0C0");
    }

    public void visit(Eval_c n) {
        createDotNode(n, null);
        visitChild(n, n.expr());
    }

    public void visit(For_c n) {
        createDotNode(n, null, "shape=parallelogram", "fillcolor=#000000", "fontcolor=#ffffff");
        visitChildren(n, n.inits());
        visitChild(n, n.cond());
        visitChildren(n, n.iters());
        visitChild(n, n.body());
    }

    public void visit(ForLoop_c n) {
        createDotNode(n, null, "shape=parallelogram", "fillcolor=#000000", "fontcolor=#ffffff");
        visitChild(n, n.formal());
        visitChild(n, n.cond());
        visitChild(n, n.domain());
        visitChild(n, n.body());
    }

    public void visit(Branch_c n) {
        createDotNode(n, n.kind() + (n.labelNode() != null ? "\\n" + n.labelNode().id().toString() : ""), "fillcolor=#000000", "fontcolor=#ffffff");
    }

    public void visit(X10Do_c n) {
        createDotNode(n, null, "shape=parallelogram", "fillcolor=#000000", "fontcolor=#ffffff");
        visitChild(n, n.cond());
        visitChild(n, n.body());
    }

    public void visit(X10While_c n) {
        createDotNode(n, null, "shape=parallelogram", "fillcolor=#000000", "fontcolor=#ffffff");
        visitChild(n, n.cond());
        visitChild(n, n.body());
    }

    public void visit(Tuple_c n) {
        createDotNode(n, null, "shape=oval");
        visitChildren(n, n.arguments());
    }

    public void visit(SettableAssign_c n) {
        createDotNode(n, null, "shape=oval");
        visitChild(n, n.left());
        visitChildren(n, n.index());
        visitChild(n, n.right());
    }

    public void visit(FieldAssign_c n) {
        createDotNode(n, n.name().id().toString(), "shape=oval", "fillcolor=#008000", "fontcolor=#ffffff");
        visit(n.target());
        visit(n.right());
    }

    public void visit(X10Field_c n) {
        createDotNode(n, n.name().id().toString(), "shape=oval", "fillcolor=#008000", "fontcolor=#ffffff");
        visit(n.target());
    }

    public void visit(X10FieldDecl_c n) {
        createDotNode(n, n.name().id().toString(), "fillcolor=#008000", "fontcolor=#ffffff");
        visitChild(n, n.type());
        visitChild(n, n.init());
    }

    public void visit(X10LocalDecl_c n) {
        createDotNode(n, n.name().id().toString());
        visitChild(n, n.type());
        visitChild(n, n.init());
    }

    public void visit(PropertyDecl_c n) {
        createDotNode(n, n.name().id().toString());
        visitChild(n, n.type());
        visitChild(n, n.init());
    }

    public void visit(X10If_c n) {
        createDotNode(n, null, "fillcolor=#000000", "fontcolor=#ffffff");
        visitChild(n, n.cond());
        visitChild(n, n.consequent());
        visitChild(n, n.alternative());
    }

    public void visit(X10Conditional_c n) {
        createDotNode(n, "? :", "shape=oval");
        visitChild(n, n.cond());
        visitChild(n, n.consequent());
        visitChild(n, n.alternative());
    }

    public void visit(Assert_c n) {
        createDotNode(n, null, "color=#FF0000");
        visitChild(n, n.cond());
        visitChild(n, n.errorMessage());
    }

    public void visit(Throw_c n) {
        createDotNode(n, null, "fillcolor=#400000", "fontcolor=#ffffff");
        visitChild(n, n.expr());
    }

    public void visit(Try_c n) {
        createDotNode(n, null, "fillcolor=#400000", "fontcolor=#ffffff");
        visitChild(n, n.tryBlock());
        visitChildren(n, n.catchBlocks());
        visitChild(n, n.finallyBlock());
    }

    public void visit(Catch_c n) {
        createDotNode(n, null, "fillcolor=#400000", "fontcolor=#ffffff");
        visitChild(n, n.formal());
        visitChild(n, n.body());
    }

    public void visit(Labeled_c n) {
        createDotNode(n, n.labelNode().id().toString(), "color=#A0A0A0");
        visitChild(n, n.statement());
    }

    public void visit(X10BooleanLit_c n) {
        createDotNode(n, Boolean.toString(n.value()), "shape=invtriangle", "fillcolor=#A0A0FF");
    }

    public void visit(ClassLit_c n) {
        createDotNode(n, null, "shape=invtriangle", "fillcolor=#A0A0FF");
        visitChild(n, n.typeNode());
    }

    public void visit(X10FloatLit_c n) {
        createDotNode(n, Double.toString(n.value()), "shape=invtriangle", "fillcolor=#A0A0FF");
    }

    public void visit(NullLit_c n) {
        createDotNode(n, "null", "shape=invtriangle", "fillcolor=#A0A0FF");
    }

    public void visit(X10CharLit_c n) {
        createDotNode(n, "" + n.value(), "shape=invtriangle", "fillcolor=#A0A0FF");
    }

    public void visit(IntLit_c n) {
        createDotNode(n, Long.toString(n.value()), "shape=invtriangle", "fillcolor=#A0A0FF");
    }

    public void visit(X10StringLit_c n) {
        createDotNode(n, StringUtil.escape(n.value()), "shape=invtriangle", "fillcolor=#A0A0FF");
    }

    public void visit(Finish_c n) {
        createDotNode(n, null, "fillcolor=#004040", "fontcolor=#ffffff");
        visitChild(n, n.body());
    }

    public void visit(AtStmt_c n) {
        createDotNode(n, null, "fillcolor=#004040", "fontcolor=#ffffff");
        visitChild(n, n.place());
        visitChild(n, n.body());
    }

    public void visit(AtHomeStmt_c n) {
        createDotNode(n, null, "fillcolor=#004040", "fontcolor=#ffffff");
        visitChild(n, n.place());
        visitChild(n, n.body());
    }

    public void visit(AtExpr_c n) {
        createDotNode(n, null, "shape=oval", "fillcolor=#004040", "fontcolor=#ffffff");
        visitChild(n, n.place());
        visitChild(n, n.body());
    }

    public void visit(AtHomeExpr_c n) {
        createDotNode(n, null, "shape=oval", "fillcolor=#004040", "fontcolor=#ffffff");
        visitChild(n, n.place());
        visitChild(n, n.body());
    }

    public void visit(AtEach_c n) {
        createDotNode(n, null, "fillcolor=#004040", "fontcolor=#ffffff");
        visitChild(n, n.formal());
        visitChild(n, n.domain());
        visitChild(n, n.body());
    }

    public void visit(Async_c n) {
        createDotNode(n, null, "fillcolor=#004040", "fontcolor=#ffffff");
        visitChild(n, n.body());
    }

    public void visit(Atomic_c n) {
        createDotNode(n, null, "fillcolor=#004040", "fontcolor=#ffffff");
        visitChild(n, n.body());
    }

    public void visit(When_c n) {
        createDotNode(n, null, "fillcolor=#004040", "fontcolor=#ffffff");
        visitChild(n, n.expr());
        visitChild(n, n.stmt());
    }

    public void visit(X10New_c n) {
        createDotNode(n, null, "shape=oval", "fillcolor=#FFFFC0");
        visitChildren(n, n.typeArguments());
        visitChildren(n, n.arguments());
        visitChild(n, n.objectType());
        visitChild(n, n.body());
    }

    public void visit(Allocation_c n) {
        createDotNode(n, null, "shape=oval");
        visitChild(n, n.objectType());
    }

    public void visit(LocalAssign_c n) {
        createDotNode(n, null, "shape=oval");
        visitChild(n, n.local());
        visitChild(n, n.right());
    }

    public void visit(X10Cast_c n) {
        createDotNode(n, null, "shape=oval");
        visitChild(n, n.castType());
        visitChild(n, n.expr());
    }

    public void visit(X10Instanceof_c n) {
        createDotNode(n, null, "shape=oval");
        visitChild(n, n.compareType());
        visitChild(n, n.expr());
    }

    public void visit(SubtypeTest_c n) {
        createDotNode(n, null, "shape=oval", "fillcolor=#C0FFC0");
        visitChild(n, n.subtype());
        visitChild(n, n.supertype());
    }

    public void visit(DepParameterExpr_c n) {
        createDotNode(n, null, "shape=oval");
        visitChildren(n, n.formals());
        visitChildren(n, n.condition());
    }

    public void visit(HasZeroTest_c n) {
        createDotNode(n, null, "shape=oval", "fillcolor=#C0FFC0");
        visitChild(n, n.parameter());
    }

    public void visit(Closure_c n) {
        createDotNode(n, null, "shape=oval", "fillcolor=#FFA0A0");
        visitChildren(n, n.formals());
        visitChild(n, n.body());
    }

    public void visit(ClosureCall_c n) {
        createDotNode(n, null, "shape=oval", "fillcolor=#FFA0A0");
        visitChild(n, n.target());
        visitChildren(n, n.arguments());
    }

    public void visit(StmtExpr_c n) {
        createDotNode(n, null, "shape=oval");
        visitChildren(n, n.statements());
    }

    public void visit(AmbReceiver_c n) {
        createDotNode(n, n.nameNode().id().toString());
    }

    public void visit(Switch_c n) {
        createDotNode(n, null);
        visitChild(n, n.expr());
        visitChildren(n, n.elements());
    }

    public void visit(SwitchBlock_c n) {
        createDotNode(n, null);
        visitChildren(n, n.statements());
    }

    public void visit(Case_c n) {
        createDotNode(n, null);
        visitChild(n, n.expr());
    }

    public void visit(LocalTypeDef_c n) {
        createDotNode(n, null);
        visitChild(n, n.typeDef());
    }

    public void visit(Next_c n) {
        createDotNode(n, "here", "shape=oval", "fillcolor=#C0C0FF");
    }

    public void visit(TypeDecl_c n) {
        createDotNode(n, n.name().id().toString(), "fillcolor=#C0FFC0");
        visitChild(n, n.type());
    }

}