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

import java.util.Set;

import polyglot.ast.*;
import polyglot.frontend.Job;
import polyglot.types.*;
import x10.errors.Errors;
import x10.util.CollectionFactory;

/** Visitor which ensures that field intializers and initializers do not
 * make illegal forward references to fields.
 *  This is an implementation of the rules of the Java Language Spec, 2nd
 * Edition, Section 8.3.2.3
 *
 * Yoav: It now only checks for illegal forward ref of static fields.
 */
public class FwdReferenceChecker extends ContextVisitor
{
    public FwdReferenceChecker(Job job, TypeSystem ts, NodeFactory nf) {
	super(job, ts, nf);
    }

    private boolean inInitialization = false;
    private Set<FieldDef> declaredFields = CollectionFactory.newHashSet();
    
    protected NodeVisitor enterCall(Node n) {
        if (n instanceof FieldDecl) {
            FieldDecl fd = (FieldDecl)n;
            if (fd.flags().flags().isStatic()) {
            FwdReferenceChecker frc = (FwdReferenceChecker)this.shallowCopy();
            frc.declaredFields = CollectionFactory.newHashSet(declaredFields);
            declaredFields.add(fd.fieldDef());
            frc.inInitialization = true;
            return frc;
            }
        }
        else if (n instanceof Initializer && ((Initializer)n).flags().flags().isStatic()) {
            FwdReferenceChecker frc = (FwdReferenceChecker)this.shallowCopy();
            frc.inInitialization = true;
            return frc;
        }
        else if (n instanceof Field) {
            if (inInitialization) {
                // we need to check if this is an illegal fwd reference.
                Field f = (Field)n;
                
                // an illegal fwd reference if a usage of an instance 
                // (resp. static) field occurs in an instance (resp. static)
                // initialization, and the innermost enclosing class or 
                // interface of the usage is the same as the container of
                // the field, and we have not yet seen the field declaration.
                //
                
                ClassType currentClass = context().currentClass();
                ContainerType fContainer = f.fieldInstance().container();

                if (f.fieldInstance().flags().isStatic() &&
                    currentClass.typeEquals(fContainer, context) &&
                   !declaredFields.contains(f.fieldInstance().def()))
                {
                    Errors.issue(job(), new SemanticException("Illegal forward reference", f.position()));
                }
            }
        }
        return this;        
    }
}
