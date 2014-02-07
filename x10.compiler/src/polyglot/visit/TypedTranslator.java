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

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.frontend.TargetFactory;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;

/**
 * TypedTranslator extends Translator for type-directed code generation.
 * The base Translator uses types only to generate more readable code.
 * If an ambiguous or untyped AST node is encountered, code generation
 * continues. In contrast, with TypedTranslator, encountering an
 * ambiguous or untyped node is considered internal compiler error.
 * TypedTranslator should be used when the output AST is expected to be
 * (or required to be) type-checked before code generation.
 */
public class TypedTranslator extends Translator {

    public TypedTranslator(Job job, TypeSystem ts, NodeFactory nf, TargetFactory tf) {
        super(job, ts, nf, tf);
    }
    
    public void print(Node parent, Node child, CodeWriter w) {
        if (context == null) {
            throw new InternalCompilerError("Null context found during type-directed code generation.", child.position());
        }
        
        super.print(parent, child, w);
    }
}
