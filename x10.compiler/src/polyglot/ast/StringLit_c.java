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
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.ast;

import java.util.*;

import polyglot.types.SemanticException;
import polyglot.util.*;
import polyglot.visit.ContextVisitor;
import polyglot.visit.PrettyPrinter;
import x10.types.constants.ConstantValue;

/** 
 * A <code>StringLit</code> represents an immutable instance of a 
 * <code>String</code> which corresponds to a literal string in Java code.
 */
public abstract class StringLit_c extends Lit_c implements StringLit
{
    protected String value;

    public StringLit_c(Position pos, String value) {
	super(pos);
	assert(value != null);
	this.value = value;
    }

    /** Get the value of the expression. */
    public String value() {
	return this.value;
    }

    /** Set the value of the expression. */
    public StringLit value(String value) {
	StringLit_c n = (StringLit_c) copy();
	n.value = value;
	return n;
    }

    /** Type check the expression. */
    public abstract Node typeCheck(ContextVisitor tc);

    public String toString() {
        if (StringUtil.unicodeEscape(value).length() > 11) {
            return "\"" + StringUtil.unicodeEscape(value).substring(0,8) + "...\"";
        }
                
	return "\"" + StringUtil.unicodeEscape(value) + "\"";
    }

    protected int MAX_LENGTH = 60;
 
    /** Write the expression to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        List<String> l = breakupString();

        // If we break up the string, parenthesize it to avoid precedence bugs.
        if (l.size() > 1) {
            w.write("(");
        }

        w.begin(0);

        for (Iterator<String> i = l.iterator(); i.hasNext(); ) {
            String s = i.next();

            w.write("\"");
            w.write(StringUtil.escape(s));
            w.write("\"");

            if (i.hasNext()) {
                w.write(" +");
                w.allowBreak(0, " ");
            }
        }

        w.end();

        if (l.size() > 1) {
            w.write(")");
        }
    }

    /**
     * Break a long string literal into a concatenation of small string
     * literals.  This avoids messing up the pretty printer and editors. 
     */
    protected List<String> breakupString() {
        List<String> result = new LinkedList<String>();
        int n = value.length();
        int i = 0;

        while (i < n) {
            int j;

            // Compensate for the unicode transformation by computing
            // the length of the encoded string.
            int len = 0;

            for (j = i; j < n; j++) {
                char c = value.charAt(j);
                int k = StringUtil.unicodeEscape(c).length();
                if (len + k > MAX_LENGTH) break;
                len += k;
            }

            result.add(value.substring(i, j));

            i = j;
        }

        if (result.isEmpty()) {
            // This should only happen when value == "".
            if (! value.equals("")) {
                throw new InternalCompilerError("breakupString failed");
            }
            result.add(value);
        }

        return result;
    }
    
    public ConstantValue constantValue() {
        return ConstantValue.makeString(value);
    }
    

}
