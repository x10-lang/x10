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
package x10.optimizations.inlining;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.SourceFile;
import polyglot.frontend.Job;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;

/**
 * @author Bowen Alpern
 * TODO: cache the Decl's and there inline cost info
 *
 */
public class InlineDeclHarvester extends ContextVisitor {

    private final DeclStore repository;
    /**
     * @param job
     * @param ts
     * @param nf
     */
    public InlineDeclHarvester(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        repository = job.extensionInfo().compiler().getInlinerData(job, ts, nf);
    }

    /* (non-Javadoc)
     * @see polyglot.visit.NodeVisitor#override(polyglot.ast.Node)
     */
    @Override
    public Node override(Node n) {
        if (n instanceof SourceFile) {
            String source = ((SourceFile) n).source().toString().intern();
            if (null == repository.cache.getAST(source))
                repository.cache.putAST(source, n);
            return n;
        }
        assert false;
        return null;
    }

}
