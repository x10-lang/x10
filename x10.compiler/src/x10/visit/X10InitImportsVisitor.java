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

import polyglot.frontend.Job;
import polyglot.visit.InitImportsVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.Import;
import x10.ast.Node;
import x10.ast.NodeFactory;
import x10.ast.SourceFile;
import x10.types.ImportTable;
import x10.types.SemanticException;
import x10.types.TypeSystem;

public class X10InitImportsVisitor extends InitImportsVisitor {

    public X10InitImportsVisitor(Job job, TypeSystem ts, NodeFactory nf) {
	super(job, ts, nf);
    }

//    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
//        if (n instanceof Import) {
//            Import im = (Import) n;
//            
//            if (im.kind() == Import.EXPLICIT) {
//                this.importTable.addExplicitImport(im.name(), im.position());
//            }
//            else if (im.kind() == Import.ON_DEMAND) {
//                this.importTable.addOnDemandImport(im.name(), im.position());
//            }
//        }
//
//        return super.leaveCall(old, n, v);
//    }

}
