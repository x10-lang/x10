/*
 * Created on Oct 6, 2005
 */
package com.ibm.wala.cast.x10.translator.polyglot;

import x10.ast.Async;
import x10.ast.AtEach;
import x10.ast.AtStmt;
import x10.ast.Atomic;
import x10.ast.Await;
import x10.ast.Clocked;
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
import x10.ast.PlaceCast;
import x10.ast.Point;
import x10.ast.Range;
import x10.ast.Region;
import x10.ast.SettableAssign;
import x10.ast.Tuple;
import x10.ast.When;
import x10.ast.X10Formal;

import com.ibm.wala.cast.java.translator.polyglot.TranslatingVisitor;
import com.ibm.wala.cast.java.translator.polyglot.PolyglotJava2CAstTranslator.WalkContext;
import com.ibm.wala.cast.tree.CAstNode;

public interface X10TranslatorVisitor extends TranslatingVisitor {
    CAstNode visit(Async a, WalkContext context);
    CAstNode visit(AtEach a, WalkContext context);
    CAstNode visit(Atomic a, WalkContext context);
    CAstNode visit(AtStmt atStmt, WalkContext context);
    CAstNode visit(Await a, WalkContext context);
    CAstNode visit(Clocked c, WalkContext context);
    CAstNode visit(Closure closure, WalkContext context);
    CAstNode visit(ClosureCall closureCall, WalkContext context);
    CAstNode visit(Finish f, WalkContext context);
    CAstNode visit(ForLoop f, WalkContext context);
    CAstNode visit(ForEach f, WalkContext context);
    CAstNode visit(Future f, WalkContext context);
    CAstNode visit(Here h, WalkContext context);
    CAstNode visit(LocalTypeDef l, WalkContext context);
    CAstNode visit(Next n, WalkContext context);
    CAstNode visit(ParExpr expr, WalkContext wc);
    CAstNode visit(PlaceCast pc, WalkContext context);
    CAstNode visit(Point p, WalkContext context);
    CAstNode visit(Region r, WalkContext context);
    CAstNode visit(Range r, WalkContext context);
    CAstNode visit(Tuple t, WalkContext context);
    CAstNode visit(When w, WalkContext context);
    CAstNode visit(X10Formal f, WalkContext context);
    CAstNode visit(SettableAssign n, WalkContext wc);
}
