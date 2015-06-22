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

import polyglot.ast.Typed;
import polyglot.types.Type;
import polyglot.types.Types;
import x10.constraint.XLocal;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.types.X10LocalDef;


/**
 * A representation of a local variable, with its associated type information.
 * <p>In essence, a CLocal is a serializable representation of an X10LocalInstance.
 * @author vj
 */
public interface CLocal extends XLocal<X10LocalDef>, Typed {
    public X10LocalDef localDef();
}
