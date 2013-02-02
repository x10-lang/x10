package x10.wala.translator;

import polyglot.ast.ArrayAccess;
import polyglot.ast.ArrayAccessAssign;
import polyglot.ast.ArrayInit;
import polyglot.ast.ArrayTypeNode;
import polyglot.ast.Assert;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.BooleanLit;
import polyglot.ast.Branch;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Case;
import polyglot.ast.Cast;
import polyglot.ast.Catch;
import polyglot.ast.CharLit;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassLit;
import polyglot.ast.Conditional;
import polyglot.ast.ConstructorCall;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Do;
import polyglot.ast.Empty;
import polyglot.ast.Eval;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.FieldDecl;
import polyglot.ast.FloatLit;
import polyglot.ast.For;
import polyglot.ast.Formal;
import polyglot.ast.If;
import polyglot.ast.Import;
import polyglot.ast.Initializer;
import polyglot.ast.Instanceof;
import polyglot.ast.IntLit;
import polyglot.ast.Labeled;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalClassDecl;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.New;
import polyglot.ast.NewArray;
import polyglot.ast.Node;
import polyglot.ast.NullLit;
import polyglot.ast.PackageNode;
import polyglot.ast.Return;
import polyglot.ast.Special;
import polyglot.ast.StringLit;
import polyglot.ast.Switch;
import polyglot.ast.SwitchBlock;
import polyglot.ast.Throw;
import polyglot.ast.Try;
import polyglot.ast.Unary;
import polyglot.ast.While;
import x10.ast.AssignPropertyCall;
import x10.ast.Async;
import x10.ast.AtStmt;
import x10.ast.Atomic;
import x10.ast.Closure;
import x10.ast.ClosureCall;
import x10.ast.Finish;
import x10.ast.ForLoop;
import x10.ast.Here;
import x10.ast.LocalTypeDef;
import x10.ast.Next;
import x10.ast.ParExpr;
import x10.ast.SettableAssign;
import x10.ast.StmtSeq;
import x10.ast.Tuple;
import x10.ast.When;
import x10.ast.X10Formal;

import com.ibm.wala.cast.tree.CAstNode;
import com.ibm.wala.util.debug.Assertions;

public class X10ASTTraverser {
  private X10ASTTraverser() {
  }

  public static CAstNode visit(Node n, X10TranslatorVisitor xtv, WalkContext wc) {
    if (n instanceof Async)
      return xtv.visit((Async) n, wc);
    if (n instanceof AssignPropertyCall)
        return xtv.visit((AssignPropertyCall) n, wc);
    else if (n instanceof Atomic)
      return xtv.visit((Atomic) n, wc);
    else if (n instanceof AtStmt)
      return xtv.visit((AtStmt) n, wc);
    else if (n instanceof Closure)
      return xtv.visit((Closure) n, wc);
    else if (n instanceof ClosureCall)
      return xtv.visit((ClosureCall) n, wc);
    else if (n instanceof Finish)
      return xtv.visit((Finish) n, wc);
    else if (n instanceof ForLoop)
      return xtv.visit((ForLoop) n, wc);
    else if (n instanceof Here)
      return xtv.visit((Here) n, wc);
    else if (n instanceof Next)
      return xtv.visit((Next) n, wc);
    else if (n instanceof ParExpr)
      return xtv.visit((ParExpr) n, wc);
    else if (n instanceof When)
      return xtv.visit((When) n, wc);
    else if (n instanceof X10Formal)
      return xtv.visit((X10Formal) n, wc);
    else if (n instanceof Tuple)
      return xtv.visit((Tuple) n, wc);
    else if (n instanceof SettableAssign)
      return xtv.visit((SettableAssign) n, wc);
    else if (n instanceof LocalTypeDef)
      return xtv.visit((LocalTypeDef) n, wc);
    else if (n instanceof MethodDecl) {
        return xtv.visit((MethodDecl) n, wc);
      } else if (n instanceof ConstructorDecl) {
        return xtv.visit((ConstructorDecl) n, wc);
      } else if (n instanceof FieldDecl) {
        return xtv.visit((FieldDecl) n, wc);
      } else if (n instanceof Import) {
        return xtv.visit((Import) n, wc);
      } else if (n instanceof PackageNode) {
        return xtv.visit((PackageNode) n, wc);
      } else if (n instanceof CanonicalTypeNode) {
        return xtv.visit((CanonicalTypeNode) n, wc);
      } else if (n instanceof ArrayTypeNode) {
        return xtv.visit((ArrayTypeNode) n, wc);
      } else if (n instanceof ArrayInit) {
        return xtv.visit((ArrayInit) n, wc);
      } else if (n instanceof ArrayAccessAssign) {
        return xtv.visit((ArrayAccessAssign) n, wc);
      } else if (n instanceof FieldAssign) {
        return xtv.visit((FieldAssign) n, wc);
      } else if (n instanceof LocalAssign) {
        return xtv.visit((LocalAssign) n, wc);
      } else if (n instanceof Binary) {
        return xtv.visit((Binary) n, wc);
      } else if (n instanceof Call) {
        return xtv.visit((Call) n, wc);
      } else if (n instanceof ConstructorCall) {
        return xtv.visit((ConstructorCall) n, wc);
      } else if (n instanceof Cast) {
        return xtv.visit((Cast) n, wc);
      } else if (n instanceof Conditional) {
        return xtv.visit((Conditional) n, wc);
      } else if (n instanceof Instanceof) {
        return xtv.visit((Instanceof) n, wc);
      } else if (n instanceof BooleanLit) {
        return xtv.visit((BooleanLit) n, wc);
      } else if (n instanceof ClassLit) {
        return xtv.visit((ClassLit) n, wc);
      } else if (n instanceof FloatLit) {
        return xtv.visit((FloatLit) n, wc);
      } else if (n instanceof NullLit) {
        return xtv.visit((NullLit) n, wc);
      } else if (n instanceof CharLit) {
        return xtv.visit((CharLit) n, wc);
      } else if (n instanceof IntLit) {
        return xtv.visit((IntLit) n, wc);
      } else if (n instanceof StringLit) {
        return xtv.visit((StringLit) n, wc);
      } else if (n instanceof New) {
        return xtv.visit((New) n, wc);
      } else if (n instanceof NewArray) {
        return xtv.visit((NewArray) n, wc);
      } else if (n instanceof Special) {
        return xtv.visit((Special) n, wc);
      } else if (n instanceof Unary) {
        return xtv.visit((Unary) n, wc);
      } else if (n instanceof ArrayAccess) {
        return xtv.visit((ArrayAccess) n, wc);
      } else if (n instanceof Field) {
        return xtv.visit((Field) n, wc);
      } else if (n instanceof Local) {
        return xtv.visit((Local) n, wc);
      } else if (n instanceof ClassBody) {
        return xtv.visit((ClassBody) n, wc);
      } else if (n instanceof ClassDecl) {
        return xtv.visit((ClassDecl) n, wc);
      } else if (n instanceof Initializer) {
        return xtv.visit((Initializer) n, wc);
      } else if (n instanceof Assert) {
        return xtv.visit((Assert) n, wc);
      } else if (n instanceof Branch) {
        return xtv.visit((Branch) n, wc);
      } else if (n instanceof SwitchBlock) { // must test for this one before Block
        return xtv.visit((SwitchBlock) n, wc);
      } else if (n instanceof StmtSeq) { // must test for this one before Block
          return xtv.visit((StmtSeq) n, wc);
      } else if (n instanceof Block) { // must test for this one before Block
        return xtv.visit((Block) n, wc);
      } else if (n instanceof Catch) {
        return xtv.visit((Catch) n, wc);
      } else if (n instanceof If) {
        return xtv.visit((If) n, wc);
      } else if (n instanceof Labeled) {
        return xtv.visit((Labeled) n, wc);
      } else if (n instanceof LocalClassDecl) {
        return xtv.visit((LocalClassDecl) n, wc);
      } else if (n instanceof Do) {
        return xtv.visit((Do) n, wc);
      } else if (n instanceof For) {
        return xtv.visit((For) n, wc);
      } else if (n instanceof While) {
        return xtv.visit((While) n, wc);
      } else if (n instanceof Switch) {
        return xtv.visit((Switch) n, wc);
      } else if (n instanceof Try) {
        return xtv.visit((Try) n, wc);
      } else if (n instanceof Empty) {
        return xtv.visit((Empty) n, wc);
      } else if (n instanceof Eval) {
        return xtv.visit((Eval) n, wc);
      } else if (n instanceof LocalDecl) {
        return xtv.visit((LocalDecl) n, wc);
      } else if (n instanceof Return) {
        return xtv.visit((Return) n, wc);
      } else if (n instanceof Case) {
        return xtv.visit((Case) n, wc);
      } else if (n instanceof Throw) {
        return xtv.visit((Throw) n, wc);
      } else if (n instanceof Formal) {
        return xtv.visit((Formal) n, wc);
      } else {
        Assertions.UNREACHABLE("Unhandled node " + n + " of type " + n.getClass().getName() + " in ASTTraverser.visit().");
        return null;
      }  }
  
}
