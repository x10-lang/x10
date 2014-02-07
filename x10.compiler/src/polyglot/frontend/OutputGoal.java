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

package polyglot.frontend;

import polyglot.ast.Node;
import polyglot.util.InternalCompilerError;
import polyglot.visit.Translator;

/** An output pass generates output code from the processed AST. */
public class OutputGoal extends SourceGoal_c
{
    private static final long serialVersionUID = 6861293948942017627L;

    protected Translator translator;

    /**
     * Create a Translator.  The output of the visitor is a collection of files
     * whose names are added to the collection <code>outputFiles</code>.
     */
    public OutputGoal(Job job, Translator translator) {
    	this(job, translator, "CodeGenerated");
    }

    public OutputGoal(Job job, Translator translator, String name) {
    	super(name, job);
        this.translator = translator;
    }

    public boolean runTask() {
        Node ast = job().ast();

        if (ast == null) {
            throw new InternalCompilerError("AST is null");
        }

        if (translator.translate(ast)) {
            return true;
        }
        
        return false;
    }
}
