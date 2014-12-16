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

package x10.types;

import java.util.List;

import polyglot.types.Flags;
import polyglot.types.ParsedClassType;
import polyglot.types.SemanticException;
import polyglot.types.ContainerType;
import polyglot.types.Type;
import x10.constraint.XConstraint;
import x10.types.constraints.CConstraint;

/**
 * @author vj
 *
 *
 */
public interface X10ParsedClassType extends ParsedClassType, X10ClassType, X10ThisVar {

    CConstraint getXClause();
    TypeParamSubst subst();

    boolean isMissingTypeArguments();
    X10ParsedClassType instantiateTypeParametersExplicitly();

    X10ParsedClassType error(SemanticException e);
    X10ParsedClassType typeArguments(List<Type> typeArgs);
    X10ParsedClassType container(ContainerType container);
}
