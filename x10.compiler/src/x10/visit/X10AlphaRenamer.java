/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2010.
 */
package x10.visit;

import java.util.Map;
import java.util.Set;

import polyglot.ast.Block;
import polyglot.ast.Formal;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.types.LocalDef;
import polyglot.types.Name;
import polyglot.visit.AlphaRenamer;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.Closure;
import x10.util.CollectionFactory;

public class X10AlphaRenamer extends AlphaRenamer {

    protected Map<Name, LocalDef> localDefMap = CollectionFactory.newHashMap();
    protected TypeRewriter rewriter = new TypeRewriter(renamingMap, localDefMap);
    private ContextVisitor cv;
    
    public X10AlphaRenamer(ContextVisitor visitor) {
        this(visitor, true);
    }
    
    public X10AlphaRenamer(ContextVisitor visitor, boolean clearOutOfScopeMaps) {
        super(clearOutOfScopeMaps);
        cv = visitor;
    }
    
    @Override
    public NodeVisitor enter(Node n) {
        if (n instanceof LocalDecl) {
            LocalDecl l = (LocalDecl) n;
            localDefMap.put(l.name().id(), l.localDef());
        }
        if (n instanceof Formal) {
            Formal f = (Formal) n;
            localDefMap.put(f.name().id(), f.localDef());
        }
        X10AlphaRenamer res = (X10AlphaRenamer) super.enter(n);
        if (n instanceof Closure) {
            // [IP] Closures may have formals that shadow outer locals
            Closure c = (Closure) n;
            res = (X10AlphaRenamer) res.shallowCopy();
            res.renamingMap = CollectionFactory.newHashMap(this.renamingMap);
            for (Formal f : c.formals()) {
                res.renamingMap.remove(f.name());
            }
        }
        return res;
    }

    @Override
    public Node leave(Node old, Node n, NodeVisitor v) {
        Set<Name> s = null;
        if (isNewScope(n)) {
            s = setStack.peek();
        }
        Node res = rewriter.transform(n, old, cv);
        res = super.leave(old, res, v);
        if (clearMaps && isNewScope(res)) {
            localDefMap.keySet().removeAll(s);
        }
        return res;
    }
}
