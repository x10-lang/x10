/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.visit;

import java.util.HashMap;
import java.util.Map;

import polyglot.ast.*;
import polyglot.frontend.Job;
import polyglot.types.*;
import polyglot.util.InternalCompilerError;
import polyglot.util.CollectionUtil; import x10.errors.Errors;
import x10.util.CollectionFactory;

/** Visitor which ensures that constructor calls are not recursive. */
public class ConstructorCallChecker extends ContextVisitor
{
    public ConstructorCallChecker(Job job, TypeSystem ts, NodeFactory nf) {
	super(job, ts, nf);
    }

    protected Map<ConstructorDef,ConstructorDef> constructorInvocations = CollectionFactory.newHashMap();

    protected NodeVisitor enterCall(Node n) {
        if (n instanceof ConstructorCall) {
            ConstructorCall cc = (ConstructorCall)n;
            if (cc.kind() == ConstructorCall.THIS) {
                // the constructor calls another constructor in the same class
                Context ctxt = context();

                if (!(ctxt.currentCode() instanceof ConstructorDef)) {
                    throw new InternalCompilerError("Constructor call occurring in a non-constructor.", cc.position());
                }
                ConstructorDef srcCI = (ConstructorDef)ctxt.currentCode();
                ConstructorDef destCI = cc.constructorInstance().def();
                
                constructorInvocations.put(srcCI, destCI);
                while (destCI != null) {
                    destCI = constructorInvocations.get(destCI);
                    if (destCI != null && srcCI.equals(destCI)) {
                        // loop in the constructor invocations!
                        Errors.issue(job(), new SemanticException("Recursive constructor invocation.", cc.position()));
                        break;
                    }
                }
            }
        }
        return this;        
    }
}

