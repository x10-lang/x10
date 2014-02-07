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
import polyglot.frontend.Job;
import polyglot.types.*;

/** Visitor which traverses the AST constructing type objects. */
public class InitImportsVisitor extends ErrorHandlingVisitor
{
    protected ImportTable importTable;
    
    public InitImportsVisitor(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }
    
    public NodeVisitor enterCall(Node n) throws SemanticException {
        if (n instanceof SourceFile) {
            SourceFile sf = (SourceFile) n;
            
            PackageNode pn = sf.package_();
            
            ImportTable it;
            
            if (pn != null) {
                it = ts.importTable(sf.source().name(), pn.package_());
            }
            else {
                it = ts.importTable(sf.source().name(), null);
            }
            
            InitImportsVisitor v = (InitImportsVisitor) shallowCopy();
            v.importTable = it;
            return v;
        }
        
        return this;
    }
    
    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        if (n instanceof SourceFile) {
            SourceFile sf = (SourceFile) n;
            InitImportsVisitor v_ = (InitImportsVisitor) v;
            ImportTable it = v_.importTable;
            return sf.importTable(it);
        }
        if (n instanceof Import) {
            Import im = (Import) n;
            
            if (im.kind() == Import.CLASS) {
                this.importTable.addExplicitImport(im.name(), im.position());
            }
            else if (im.kind() == Import.PACKAGE) {
                this.importTable.addOnDemandImport(im.name(), im.position());
            }
        }

        return n;
    }
}
