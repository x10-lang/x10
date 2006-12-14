/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/**
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Block;
import polyglot.ast.CompoundStmt;

/** A StmtSeq is an immutable representation of a sequence of statements. Usually 
 * sequences of statements are represented in a block. However a block 
 * has scoping properties. Local variables declared in a block are not visible
 * outside the block. For some code transformations it is necessary to treat
 * a sequence of statements as a single statement. For instance we wish to
 * flatten out expressions, in preparation for inlining, say. Thus from a single
 * statement x=foo(fum(i,j),k) we may wish to produce a sequence of statements
 * int x10$_var_1 = fum(i,j);
 * x = foo(x10$_var_1,k);
 * The current Node/Visitor infrastructure treats a Visitor as a Node-> 
 * Node transformation. The introduction of StmtSeq permits us to use
 * that infrastructure unchanged while enabling us to rewrite a statement into
 * a sequence of statements.
 * @see NodeVisitor
 * @see polyglot.ext.x10.visit.ExprFlattener
 * @author vj
 *
 */
public interface StmtSeq extends Block {

}
