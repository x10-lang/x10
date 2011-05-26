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

import java.util.Map;
import java.util.Set;

import polyglot.ast.Node;
import polyglot.ast.ProcedureDecl;
import polyglot.frontend.Job;
import polyglot.types.Def;
import x10.util.CollectionFactory;

/**
 * @author Bowen Alpern
 *
 */
public final class InlinerCache {

    private final Set<Def> dontInline              = CollectionFactory.newHashSet();
    private final Map<Def, ProcedureDecl> def2decl = CollectionFactory.newHashMap();
    private final Set<Job> badJobs                 = CollectionFactory.newHashSet();
    private final Set<String> badSources           = CollectionFactory.newHashSet();
    private final Map<String, Node> astMap         = CollectionFactory.newHashMap();

    public boolean uninlineable(Def candidate) {
        return dontInline.contains(candidate);
    }

    public void notInlinable(Def candidate) {
        dontInline.add(candidate);
    }

    public boolean okayJob(Job job) {
        boolean result = !badSources.contains(job.source().toString().intern());
        return result;
    }

    public void badJob(Job job) {
        if (null == job) 
            return;
        badJobs.add(job);
        badSources.add(job.source().toString().intern());
    }

    public ProcedureDecl getDecl(Def candidate) {
        return def2decl.get(candidate);
    }

    public void putDecl(Def candidate, ProcedureDecl decl) {
        def2decl.put(candidate, decl);
    }

    public Node getAST(String source) {
        return astMap.get(source);
    }

    public void putAST(String source, Node ast) {
        astMap.put(source, ast);
    }

}
