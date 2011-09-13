/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.types;

import java.util.List;
import java.util.Set;

import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;


public interface X10ClassDef extends X10Def, ClassDef, X10MemberDef {
    TypeParamSubst subst();
    void setSubst(TypeParamSubst subst);
    
    // five methods for data-centric synchronizations
    void addAtomicFields(FieldDef fi);
    boolean hasAtomicFields();
    boolean hasAccumulated();
    boolean hasAtomicFields(boolean needCheck);
    void setAccumulated();
    Set<FieldDef> getAtomicFields();
}
