/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.visit;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.MethodInstance;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.SettableAssign_c;
import x10.ast.X10NodeFactory;
import x10.types.X10Context;
/**
 * Visitor to desugar the AST before code gen.
 */
public class EarlyDesugarer extends ContextVisitor {
    private final X10NodeFactory xnf;
    
    public EarlyDesugarer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xnf = (X10NodeFactory) nf;
    }

    protected X10Context xContext() { return (X10Context) context;}
    
    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        if (n instanceof SettableAssign_c)
            return visitSettableAssign((SettableAssign_c) n);
        return n;
    }

    // a(i)=v -> a.set(v, i) or a(i)op=v -> ((x:A,y:I,z:T)=>x.set(x.apply(y) op z,y))(a,i,v)
    protected Expr visitSettableAssign(SettableAssign_c n) {
        // if (1==2-1) return n;
        Position pos = n.position();
        MethodInstance mi = n.methodInstance();
        List<Expr> args = new ArrayList<Expr>(n.index());
        if (n.operator() == Assign.ASSIGN) {
            // FIXME: this changes the order of evaluation, (a,i,v) -> (a,v,i)!
            args.add(0, n.right());
            return xnf.Call(pos, n.array(), xnf.Id(pos, mi.name()),
                    args).methodInstance(mi).type(mi.returnType());
        }
        return n;
    }

}
