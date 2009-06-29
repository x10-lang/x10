/*
 * Created on Oct 21, 2005
 */
package com.ibm.wala.cast.x10.translator.polyglot;

import polyglot.ast.Node;
import polyglot.ext.x10.ast.ArrayConstructor;
import polyglot.ext.x10.ast.Async;
import polyglot.ext.x10.ast.AtEach;
import polyglot.ext.x10.ast.Atomic;
import polyglot.ext.x10.ast.Await;
import polyglot.ext.x10.ast.Closure;
import polyglot.ext.x10.ast.Finish;
import polyglot.ext.x10.ast.ForEach;
import polyglot.ext.x10.ast.ForLoop;
import polyglot.ext.x10.ast.Future;
import polyglot.ext.x10.ast.Here;
import polyglot.ext.x10.ast.Next;
import polyglot.ext.x10.ast.ParExpr;
import polyglot.ext.x10.ast.When;
import polyglot.ext.x10.ast.X10ArrayAccess;
import polyglot.ext.x10.ast.X10ArrayAccess1;
import polyglot.ext.x10.ast.X10Formal;

import com.ibm.wala.cast.java.translator.polyglot.ASTTraverser;
import com.ibm.wala.cast.java.translator.polyglot.PolyglotJava2CAstTranslator.WalkContext;
import com.ibm.wala.cast.tree.CAstNode;

public class X10ASTTraverser extends ASTTraverser {
    private X10ASTTraverser() { }

    public static CAstNode visit(Node n, X10TranslatorVisitor xtv, WalkContext wc) {
	if (n instanceof ArrayConstructor)
	    return xtv.visit((ArrayConstructor) n, wc);
	else if (n instanceof Async)
	    return xtv.visit((Async) n, wc);
	else if (n instanceof AtEach)
	    return xtv.visit((AtEach) n, wc);
	else if (n instanceof Atomic)
	    return xtv.visit((Atomic) n, wc);
	else if (n instanceof Await)
	    return xtv.visit((Await) n, wc);
	else if (n instanceof Closure)
	    return xtv.visit((Closure) n, wc);
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
	else if (n instanceof X10ArrayAccess)
	    return xtv.visit((X10ArrayAccess) n, wc);
	else if (n instanceof X10ArrayAccess1)
	    return xtv.visit((X10ArrayAccess1) n, wc);
	else if (n instanceof X10Formal)
	    return xtv.visit((X10Formal) n, wc);
	else
	    return ASTTraverser.visit(n, xtv, wc);
    }
}
