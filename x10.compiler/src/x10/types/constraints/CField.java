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
import polyglot.types.Def;
import polyglot.types.FieldDef;
import polyglot.types.LocalDef;
import polyglot.types.MethodDef;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.Types;
import polyglot.types.VarDef;
import x10.constraint.XField;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.types.X10ClassDef;
import x10.types.X10FieldDef;
import x10.types.X10FieldInstance;
import x10.types.X10MethodDef;

/**
 * An XField with type information (either a MethodDef or a FieldDef).
 * 
 * @author vj
 */
public interface CField extends XField<Def>, Typed {
    public Def def(); 
    public XVar thisVar();
    boolean isHidden(); 
 }
