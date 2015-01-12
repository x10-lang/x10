/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.visit;

import java.io.*;

import polyglot.ast.Node;
import polyglot.frontend.Compiler;
import polyglot.util.CodeWriter;


/** Visitor which dumps the AST to a file. */
public class DumpAst extends NodeVisitor
{
    protected PrintWriter fw;
    protected CodeWriter w;

    /** @deprecated Use the other constructor. */
    public DumpAst(String name, int width) throws IOException {
        this.fw = new PrintWriter(new FileWriter(name));
        this.w = Compiler.createCodeWriter(fw, width);
    }

    public DumpAst(CodeWriter w) {
        this.w = w;
    }

    /** 
     * Visit each node before traversal of children. Call <code>dump</code> for
     * that node. Then we begin a new <code>CodeWriter</code> block and traverse
     * the children.
     */
    @Override
    public NodeVisitor enter(Node n) {
        w.write("(");
        n.dump(w);
        w.allowBreak(4);
        w.begin(0);
        return this;
    }

    /**
     * This method is called only after normal traversal of the children. Thus
     * we must end the <code>CodeWriter</code> block that was begun in 
     * <code>enter</code>.
     */
    @Override
    public Node leave(Node old, Node n, NodeVisitor v) {
        w.end();
        w.write(")");
        w.allowBreak(0);
        return n;
    }

    @Override
    public void finish() {
        try {
            w.flush();

            if (fw != null) {
                fw.flush();
                fw.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
