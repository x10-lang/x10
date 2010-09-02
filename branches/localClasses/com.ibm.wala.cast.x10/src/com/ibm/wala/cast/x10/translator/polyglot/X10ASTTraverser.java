package com.ibm.wala.cast.x10.translator.polyglot;

import polyglot.ast.Node;
import x10.ast.Async;
import x10.ast.AtEach;
import x10.ast.AtStmt;
import x10.ast.Atomic;
import x10.ast.Await;
import x10.ast.Closure;
import x10.ast.ClosureCall;
import x10.ast.Finish;
import x10.ast.ForEach;
import x10.ast.ForLoop;
import x10.ast.Future;
import x10.ast.Here;
import x10.ast.LocalTypeDef;
import x10.ast.Next;
import x10.ast.ParExpr;
import x10.ast.SettableAssign;
import x10.ast.Tuple;
import x10.ast.When;
import x10.ast.X10Formal;

import com.ibm.wala.cast.java.translator.polyglot.ASTTraverser;
import com.ibm.wala.cast.java.translator.polyglot.PolyglotJava2CAstTranslator.WalkContext;
import com.ibm.wala.cast.tree.CAstNode;

public class X10ASTTraverser extends ASTTraverser {
  private X10ASTTraverser() {
  }

  public static CAstNode visit(Node n, X10TranslatorVisitor xtv, WalkContext wc) {
    if (n instanceof Async)
      return xtv.visit((Async) n, wc);
    else if (n instanceof AtEach)
      return xtv.visit((AtEach) n, wc);
    else if (n instanceof Atomic)
      return xtv.visit((Atomic) n, wc);
    else if (n instanceof AtStmt)
      return xtv.visit((AtStmt) n, wc);
    else if (n instanceof Await)
      return xtv.visit((Await) n, wc);
    else if (n instanceof Closure)
      return xtv.visit((Closure) n, wc);
    else if (n instanceof ClosureCall)
      return xtv.visit((ClosureCall) n, wc);
    else if (n instanceof Finish)
      return xtv.visit((Finish) n, wc);
    else if (n instanceof ForEach)
      return xtv.visit((ForEach) n, wc);
    else if (n instanceof ForLoop)
      return xtv.visit((ForLoop) n, wc);
    else if (n instanceof Future)
      return xtv.visit((Future) n, wc);
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
    else
      return ASTTraverser.visit(n, xtv, wc);
  }
  
}
