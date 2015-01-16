package x10.parser.antlr;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.io.File;

import polyglot.types.QName;
import polyglot.types.Name;
import polyglot.ast.AmbTypeNode;
import polyglot.ast.AmbExpr;
import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Case;
import polyglot.ast.Catch;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.ConstructorCall;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FloatLit;
import polyglot.ast.ForInit;
import polyglot.ast.ForUpdate;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Import;
import polyglot.ast.IntLit;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.FieldDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.PackageNode;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.SourceFile;
import polyglot.ast.Stmt;
import polyglot.ast.SwitchElement;
import polyglot.ast.TopLevelDecl;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ast.FlagsNode;
import polyglot.parse.ParsedName;
import x10.ast.AmbMacroTypeNode;
import x10.ast.AnnotationNode;
import x10.ast.AtExpr;
import x10.ast.ClosureCall;
import x10.ast.SettableAssign;
import x10.ast.DepParameterExpr;
import x10.ast.Tuple;
import x10.ast.X10Formal;
import x10.ast.TypeDecl;
import x10.ast.TypeParamNode;
import x10.types.ParameterType;
import polyglot.types.TypeSystem;
import x10.ast.PropertyDecl;
import x10.ast.X10Binary_c;
import x10.ast.X10Unary_c;
import x10.extension.X10Ext;
import polyglot.frontend.FileSource;
import polyglot.frontend.Parser;
import polyglot.lex.BooleanLiteral;
import polyglot.lex.CharacterLiteral;
import polyglot.lex.DoubleLiteral;
import polyglot.lex.FloatLiteral;
import polyglot.lex.Identifier;
import polyglot.lex.LongLiteral;
import polyglot.lex.NullLiteral;
import polyglot.lex.Operator;
import polyglot.lex.StringLiteral;
import polyglot.types.Flags;
import x10.types.checker.Converter;
import x10.errors.Errors;
import polyglot.util.CollectionUtil;
import x10.util.CollectionFactory;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.util.TypedList;
import x10.parser.X10SemanticRules.JPGPosition;
import x10.parserGen.X10BaseListener;
import x10.parserGen.X10Listener;
import x10.parserGen.X10Parser.CompilationUnitContext;
import x10.parserGen.X10Parser.ImportDeclarationContext;
import x10.parserGen.X10Parser.ImportDeclarationsoptContext;
import x10.parserGen.X10Parser.PackageDeclarationContext;
import x10.parserGen.X10Parser.TypeDeclarationContext;
import x10.parserGen.X10Parser.TypeDeclarationsoptContext;

public class ParseTreeListener extends X10BaseListener implements X10Listener {
    private static JPGPosition pos(RuleContext ctx) {
        return new JPGPosition(); // TODO XXXX
    }

    private NodeFactory nf;

    // final public ParseTreeProperty<Node> ast = new ParseTreeProperty<Node>();
    //
    // public Node get(ParseTree tree) {
    // return ast.get(tree);
    // }
    private ParseTreeListener() {
    }

    public ParseTreeListener(NodeFactory n) {
        nf = n;
    }

    @Override
    public void exitCompilationUnit(CompilationUnitContext ctx) {
        List<Import> importDeclarationsopt = ctx.importDeclarationsopt().ast;
        List<TopLevelDecl> TypeDeclarationsopt = ctx.typeDeclarationsopt().ast;
        if (null == ctx.packageDeclaration()) {
            ctx.ast = nf.SourceFile(pos(ctx), importDeclarationsopt,
                    TypeDeclarationsopt);
        } else {
            PackageNode packageDeclaration = ctx.packageDeclaration().ast;
            ctx.ast = nf.SourceFile(pos(ctx), packageDeclaration,
                    importDeclarationsopt, TypeDeclarationsopt);
        }
    }

    @Override
    public void exitImportDeclarationsopt(ImportDeclarationsoptContext ctx) {
        List<Import> l = new TypedList<Import>(new LinkedList<Import>(),
                Import.class, false);
        for (ImportDeclarationContext importDeclaration : ctx
                .importDeclaration()) {
            l.add(importDeclaration.ast);
        }
        ctx.ast = l;
    }

    @Override
    public void exitTypeDeclarationsopt(TypeDeclarationsoptContext ctx) {
        List<TopLevelDecl> l = new TypedList<TopLevelDecl>(
                new LinkedList<TopLevelDecl>(), TopLevelDecl.class, false);
        for (TypeDeclarationContext typeDecl : ctx.typeDeclaration()) {
            l.add(typeDecl.ast);
        }
        ctx.ast = l;
    }

    @Override
    public void exitTypeDeclaration(TypeDeclarationContext ctx) {
        // TODO Auto-generated method stub
        super.exitTypeDeclaration(ctx);
    }

    @Override
    public void exitPackageDeclaration(PackageDeclarationContext ctx) {
        List<AnnotationNode> Annotationsopt = ctx.annotationsopt().ast;
        ParsedName PackageName = ctx.packageName().ast;
        PackageNode pn = PackageName.toPackage();
        pn = (PackageNode) ((X10Ext) pn.ext()).annotations(Annotationsopt);
        ctx.ast = pn;
    }

}
