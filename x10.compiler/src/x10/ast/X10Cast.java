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
package x10.ast;

import polyglot.ast.Cast;
import polyglot.ast.Expr;
import polyglot.visit.ContextVisitor;
import x10.types.checker.Converter;

public abstract interface X10Cast extends Cast {
    public abstract Converter.ConversionType conversionType();
    public abstract X10Cast conversionType(Converter.ConversionType convert);
    public abstract X10Cast exprAndConversionType(Expr c, Converter.ConversionType checked);

}
