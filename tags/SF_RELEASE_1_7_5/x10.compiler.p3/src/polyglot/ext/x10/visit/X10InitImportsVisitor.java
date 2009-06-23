/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package polyglot.ext.x10.visit;

import polyglot.ast.Import;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.SourceFile;
import polyglot.frontend.Job;
import polyglot.types.ImportTable;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.InitImportsVisitor;
import polyglot.visit.NodeVisitor;

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
