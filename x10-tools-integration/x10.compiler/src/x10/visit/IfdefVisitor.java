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

import java.util.Iterator;
import java.util.List;

import polyglot.ast.ClassDecl;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.Import;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.PackageNode;
import polyglot.ast.SourceFile;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.frontend.Job;
import polyglot.types.ClassType;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.AnnotationNode;
import x10.config.Configuration;
import x10.config.ConfigurationError;
import x10.config.OptionError;
import x10.extension.X10Ext;
import x10.types.X10ClassType;
import x10.errors.Errors;

// Drop node from ast according to @Ifdef and @Ifndef annotations
// 1. drop node if it has an @Ifndef annotation for a defined macro
// 2. drop node if it has at least one @Ifdef annotation and none of the macros are defined

public class IfdefVisitor extends ContextVisitor {
    public IfdefVisitor(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }
    
    public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) {

        if (! (n.ext() instanceof X10Ext)) {
            return n;
        }
        
        init();

        List<AnnotationNode> annotations = ((X10Ext) n.ext()).annotations();

        for (Iterator<AnnotationNode> i = annotations.iterator(); i.hasNext(); ) {
            AnnotationNode a = i.next();
            X10ClassType at = a.annotationInterface();
            if (at.error() != null || !at.isSubtype(Ifndef, context)) {
                continue;
            }
            List<Expr> l = at.propertyInitializers();
            if (l.size() != 1 || !l.get(0).isConstant() || !l.get(0).type().isSubtype(ts.String(), context)) {
                Errors.issue(job, new SemanticException("@Ifndef must have a unique constant String parameter"), n);
                continue;
            }
            String macro = (String) l.get(0).constantValue();
            if (job.extensionInfo().getOptions().macros.contains(macro)) {
                return null;
            }
        }

        boolean ifdef = true;
        for (Iterator<AnnotationNode> i = annotations.iterator(); i.hasNext(); ) {
            AnnotationNode a = i.next();
            X10ClassType at = a.annotationInterface();
            if (at.error() != null || !at.isSubtype(Ifdef, context)) {
                continue;
            }
            ifdef = false;
            List<Expr> l = at.propertyInitializers();
            if (l.size() != 1 || !l.get(0).isConstant() || !l.get(0).type().isSubtype(ts.String(), context)) {
                Errors.issue(job, new SemanticException("@Ifdef must have a unique constant String parameter"), n);
                continue;
            }
            String macro = (String) l.get(0).constantValue();
            if (job.extensionInfo().getOptions().macros.contains(macro)) {
                return n;
            }
        }

        return ifdef ? n : null;
    }
    
    Type Ifdef,Ifndef;
    
    public void init() {
        if (Ifdef != null) return;
        try {
            Ifdef = ts.systemResolver().findOne(QName.make("x10.compiler.Ifdef"));
            Ifndef = ts.systemResolver().findOne(QName.make("x10.compiler.Ifndef"));
        } catch (SemanticException e) {
            throw new RuntimeException(e);
        }
    }
}
