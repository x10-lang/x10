/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
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
