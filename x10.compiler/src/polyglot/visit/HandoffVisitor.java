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
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.visit;

import polyglot.ast.*;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;

/**
 * This visitor adds jobs for <code>SourceFile</code>s in the AST to the
 * schedule of another extension.
 */
public class HandoffVisitor extends NodeVisitor
{
    protected ExtensionInfo ext;

    public HandoffVisitor(ExtensionInfo ext) {
        this.ext = ext;
    }

    @Override
    public Node override(Node n) {
        if (n instanceof SourceFile || n instanceof SourceCollection) {
            return null;
        }
        return n;
    }

    @Override
    public Node leave(Node old, Node n, NodeVisitor v) {
        if (n instanceof SourceFile) {
            SourceFile sf = (SourceFile) n;
            Job job = ext.scheduler().addJob(sf.source(), sf);
            ext.scheduler().addDependenciesForJob(job, true);
        }
        return n;
    }
}
