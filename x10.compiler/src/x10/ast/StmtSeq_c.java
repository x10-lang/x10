/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.NodeList;
import polyglot.ast.Stmt;
import polyglot.ast.AbstractBlock_c;
import polyglot.types.Context;
import polyglot.util.Position;
import polyglot.util.TypedList;

/**
 * @author vj
 * @author igor
 */
public class StmtSeq_c extends AbstractBlock_c implements StmtSeq {

    private final NodeFactory xnf;

    /**
     * @param xnf
     * @param pos
     * @param statements
     */
    public StmtSeq_c(NodeFactory xnf, Position pos, List<Stmt> statements) {
        super(pos, statements);
        this.xnf = xnf;

    }
    public NodeFactory nodeFactory() {
        return xnf;
    }
    public List<Node> nodes() {
        List<Node> res = new ArrayList<Node>();
        res.addAll(statements);
        return TypedList.copyAndCheck(res, Node.class, true);
    }
    public NodeList nodes(List<Node> nodes) {
        List<Stmt> res = new ArrayList<Stmt>();
        for (Node n : nodes) {
            if (!(n instanceof Stmt)) throw new IllegalArgumentException("Adding a non-statement to a StmtSeq: "+n);
            res.add((Stmt) n);
        }
        return (NodeList) this.statements(res);
    }
    public Block toBlock() {
        return xnf.Block(position(), statements());
    }
    // Do not push a block in. StmtSeq_c differs from AbstractBlock_c
    // only in that it does not create a new scope block.
    public Context enterScope(Context c) {
        return c;
    }
    public String toString() {
        StringBuffer sb = new StringBuffer();
        // sb.append("{");
        int count = 0;
        for (Iterator<Stmt> i = statements.iterator(); i.hasNext(); ) {
            if (count++ > 20) {
                sb.append(" ...");
                break;
            }
            Stmt n = i.next();
            sb.append(" ");
            sb.append(n.toString());
        }
        // sb.append(" }");
        return sb.toString();
    }
}
