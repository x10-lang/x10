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

import x10.ast.X10Cast;
import x10.types.checker.Converter;

/**
 * A <code>Cast</code> is an immutable representation of a casting
 * operation.  It consists of an <code>Expr</code> being cast and a
 * <code>TypeNode</code> being cast to.
 */ 
public interface Cast extends Expr
{
    /**
     * The type to cast to.
     */
    TypeNode castType();

    /**
     * Set the type to cast to.
     */
    Cast castType(TypeNode castType);

    /**
     * The expression to cast.
     */
    Expr expr();

    /**
     * Set the expression to cast.
     */
    Cast expr(Expr expr);
    
    public Converter.ConversionType conversionType();
    public X10Cast conversionType(Converter.ConversionType convert);
    public X10Cast exprAndConversionType(Expr c, Converter.ConversionType checked);
}
