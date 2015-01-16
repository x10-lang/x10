package x10.parser.antlr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.JDialog;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import polyglot.ast.Import;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.PackageNode;
import polyglot.ast.TopLevelDecl;
import polyglot.frontend.FileSource;
import polyglot.frontend.Compiler;
import polyglot.parse.ParsedName;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.Position;
import polyglot.util.TypedList;
import x10.X10CompilerOptions;
import x10.ast.AnnotationNode;
import x10.extension.X10Ext;
import x10.parserGen.X10BaseListener;
import x10.parserGen.X10Lexer;
import x10.parserGen.X10Listener;
import x10.parserGen.X10Parser;
import x10.parserGen.X10Parser.CompilationUnitContext;
import x10.parserGen.X10Parser.ImportDeclarationContext;
import x10.parserGen.X10Parser.ImportDeclarationsoptContext;
import x10.parserGen.X10Parser.PackageDeclarationContext;
import x10.parserGen.X10Parser.PackageName0Context;
import x10.parserGen.X10Parser.PackageName1Context;
import x10.parserGen.X10Parser.PackageNameContext;
import x10.parserGen.X10Parser.TypeDeclarationContext;
import x10.parserGen.X10Parser.TypeDeclarationsoptContext;

public class ASTBuilder extends X10BaseListener implements X10Listener, polyglot.frontend.Parser {

    private final X10Parser p;
    private final X10Lexer lexer;

    private X10CompilerOptions compilerOpts;
    private ErrorQueue eq;
    private TypeSystem ts;
    private NodeFactory nf;
    private FileSource srce;

    public ASTBuilder(X10CompilerOptions opts, TypeSystem t, NodeFactory n,
            FileSource source, ErrorQueue q) {
        compilerOpts = opts;
        ts = t;
        nf = n;
        srce = source;
        eq = q;

        String fileName = source.toString();
        ANTLRInputStream input;
        try {
            input = new ANTLRInputStream(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            input = null;
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            input = null;
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        lexer = new X10Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        p = new X10Parser(tokens);
        p.removeErrorListeners();
        p.addErrorListener(new ParserErrorListener(eq, fileName));
    }
    
    private Position pos(ParserRuleContext ctx) {
    	if (ctx.getStop() == null){
    		return new Position(null, srce.path(), ctx.getStart().getLine(), 
					ctx.getStart().getCharPositionInLine());
    	} else {
    		return new Position(null, srce.path(), ctx.getStart().getLine(), 
        					ctx.getStart().getCharPositionInLine(), ctx.getStop().getLine(), 
        					ctx.getStop().getCharPositionInLine()); 
    	}
    }

    @Override
    public Node parse() {
        CompilationUnitContext tree = p.compilationUnit();
        if (compilerOpts.x10_config.DISPLAY_PARSE_TREE) {
            Future<JDialog> dialogHdl = tree.inspect(p);
            try {
                JDialog dialog = dialogHdl.get();
                dialog.setTitle(srce.toString());
                Utils.waitForClose(dialog);
            } catch (Exception e) {
                eq.enqueue(ErrorInfo.WARNING, srce
                        + ": unable to display the parse tree.");
            }
        }
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(this, tree);
        return tree.ast;
    }
    
    @Override
    public void exitCompilationUnit(CompilationUnitContext ctx) {
        List<Import> importDeclarationsopt = ctx.importDeclarationsopt().ast==null?new TypedList<Import>(new LinkedList<Import>(), Import.class, false):ctx.importDeclarationsopt().ast;
        List<TopLevelDecl> typeDeclarationsopt = ctx.typeDeclarationsopt().ast==null?new TypedList<TopLevelDecl>(new LinkedList<TopLevelDecl>(), TopLevelDecl.class, false):ctx.typeDeclarationsopt().ast;
      
        PackageNode packageDeclaration = ctx.packageDeclaration()==null?null:ctx.packageDeclaration().ast ;
        ctx.ast = nf.SourceFile(pos(ctx), packageDeclaration,
                    importDeclarationsopt, typeDeclarationsopt);
        
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
    
    @Override
    public void exitPackageName0(PackageName0Context ctx){
    	ctx.ast = new ParsedName(nf,ts,pos(ctx),ctx.identifier().ast);
    }
    
    @Override
    public void exitPackageName1(PackageName1Context ctx){
    	ctx.ast = new ParsedName(nf,ts,pos(ctx),ctx.packageName().ast,ctx.identifier().ast);
    }
 
}