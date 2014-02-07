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

package polyglot.ast;

import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.types.Type;
import polyglot.types.Context;
import polyglot.types.Types;
import polyglot.util.*;
import polyglot.visit.ContextVisitor;
import polyglot.visit.PrettyPrinter;
import x10.types.XTypeTranslator;
import x10.types.constants.ConstantValue;
import x10.types.constraints.CConstraint;
import x10.types.constraints.ConstraintManager;
import x10.constraint.XTerm;
import x10.constraint.XFailure;
import x10.errors.Errors;
import x10.errors.Errors.IllegalConstraint;

/** 
 * An <code>CharLit</code> represents a literal in java of
 * <code>char</code> type.
 */
public class CharLit_c extends NumLit_c implements CharLit
{
    public CharLit_c(Position pos, char value) {
	super(pos, value);
    }

    /** Get the value of the expression. */
    public char value() {
	return (char) longValue();
    }

    /** Set the value of the expression. */
    public CharLit value(char value) {
	CharLit_c n = (CharLit_c) copy();
	n.value = value;
	return n;
    }

    /** Type check the expression. */
    public Node typeCheck(ContextVisitor tc) {
		TypeSystem xts = (TypeSystem) tc.typeSystem();
		Type charType = xts.Char();

		CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
		try {
			XTerm term = xts.xtypeTranslator().translate(c, this.type(charType),  tc.context());
			c.addSelfBinding(term);
		} catch (IllegalConstraint z) {
			Errors.issue(tc.job(), z);
		}
		Type newType = Types.xclause(charType, c);
	    return type(newType);
    }  

    public String toString() {
        return "'" + StringUtil.escape((char) value) + "'";
    }

    /** Write the expression to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        w.write("'");
	w.write(StringUtil.escape((char) value));
        w.write("'");
    }

    public ConstantValue constantValue() {
      return ConstantValue.makeChar((char)value);
    }

}
