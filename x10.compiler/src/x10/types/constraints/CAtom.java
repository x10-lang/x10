/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.types.constraints;

import java.util.ArrayList;
import java.util.List;

import polyglot.types.FieldDef;
import polyglot.types.MemberDef;
import polyglot.types.MethodDef;
import x10.constraint.XFormula;
import x10.constraint.XTerm;
import x10.constraint.XVar;

/**
 * An atomic formula built from a property field (this has an associated nullary
 * method) or from a method def.
 * The CAtom knows about the MemberDef it is built form. X10 type information
 * about the CAtom can be obtained from this MemberDef.
 * @author vj
 *
 */
public interface CAtom extends XFormula<MemberDef> {
    public MemberDef def();
    public MemberDef exprDef();
}
