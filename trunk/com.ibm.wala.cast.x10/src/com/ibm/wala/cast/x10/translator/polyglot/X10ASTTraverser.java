/*
 * Created on Oct 21, 2005
 */
package com.ibm.domo.ast.x10.translator.polyglot;

import polyglot.ast.Node;
import polyglot.ext.x10.ast.Async;
import polyglot.ext.x10.ast.Finish;
import polyglot.ext.x10.ast.ForEach;
import polyglot.ext.x10.ast.ForLoop_c;
import polyglot.ext.x10.ast.Future;
import polyglot.ext.x10.ast.Here;
import polyglot.ext.x10.ast.When;

import com.ibm.capa.ast.CAstNode;
import com.ibm.domo.ast.java.translator.polyglot.ASTTraverser;
import com.ibm.domo.ast.java.translator.polyglot.PolyglotJava2CAstTranslator.WalkContext;

public class X10ASTTraverser extends ASTTraverser {
    private X10ASTTraverser() { }

    public static CAstNode visit(Node n, X10TranslatorVisitor xtv, WalkContext wc) {
	if (n instanceof Async)
	    return xtv.visit((Async) n, wc);
	else if (n instanceof Here)
	    return xtv.visit((Here) n, wc);
	else if (n instanceof Future)
	    return xtv.visit((Future) n, wc);
	else if (n instanceof Finish)
	    return xtv.visit((Finish) n, wc);
	else if (n instanceof ForLoop_c)
	    return xtv.visit((ForLoop_c) n, wc);
	else if (n instanceof ForEach)
	    return xtv.visit((ForEach) n, wc);
	else if (n instanceof When)
	    return xtv.visit((When) n, wc);
	else
	    return ASTTraverser.visit(n, xtv, wc);
    }
}
