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

package x10.compiler.tests;

import x10.lang.annotations.*;

/**
 * @ERR marks a compiler error on the line where the @ERR marker is written.
 */
public interface ERR extends MethodAnnotation, ClassAnnotation, FieldAnnotation, ImportAnnotation, PackageAnnotation, TypeAnnotation, ExpressionAnnotation, StatementAnnotation { }
