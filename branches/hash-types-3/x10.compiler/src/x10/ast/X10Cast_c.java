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

package x10.ast;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Cast;
import polyglot.ast.Cast_c;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Precedence;
import polyglot.ast.TypeNode;
import polyglot.types.ClassDef;
import polyglot.types.ConstructorDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.Context;
import polyglot.types.ErrorRef_c;
import polyglot.types.ObjectType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.types.ParameterType;
import x10.types.X10ClassType;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.checker.Converter;
import x10.types.checker.Converter.ConversionType;

/**
 * Represent java cast operation.
 * (CastType) expression
 * This class is compliant with dependent type constraint.
 * If a dynamic cast is needed, then some code is generated to check 
 * instance's value, field, etc... are valid against declared type constraint.
 *
 * @author vcave
 *
 */
public class X10Cast_c extends Cast_c implements X10Cast, X10CastInfo {
    protected Converter.ConversionType convert;

    
    public X10Cast_c(Position pos, TypeNode castType, Expr expr, Converter.ConversionType convert) {
        super(pos, castType, expr);
        this.convert = convert;
    }

    public Converter.ConversionType conversionType() {
        return convert;
    }

    public X10Cast conversionType(ConversionType convert) {
        X10Cast_c n = (X10Cast_c) copy();
        n.convert = convert;
        return n;
    }
    public X10Cast conversionType(Expr expr, ConversionType convert) {
    	  X10Cast_c n = (X10Cast_c) copy();
          n.convert = convert;
          n.expr = expr;
          return n;
    }

    @Override
    public Precedence precedence() {
        switch (convert) {
        case PRIMITIVE:
        case SUBTYPE:
            return Precedence.CAST;
        default:
            return Precedence.UNKNOWN;
        }
    }

    public Node typeCheck(ContextVisitor tc) throws SemanticException {
    	if (castType()!= null)
    		X10TypeMixin.checkMissingParameters(castType().type());
        Expr e = Converter.converterChain(this, tc);
        assert e.type() != null;
        assert ! (e instanceof X10Cast_c) || ((X10Cast_c) e).convert != Converter.ConversionType.UNKNOWN_CONVERSION;
        assert ! (e instanceof X10Cast_c) || ((X10Cast_c) e).convert != Converter.ConversionType.UNKNOWN_IMPLICIT_CONVERSION;
        return e;
    }

    public TypeNode getTypeNode() {
        return (TypeNode) this.castType().copy();
    }

    public String toString() {
        return expr.toString() + " as " + castType.toString();
    }

    @Override
    public List<Type> throwTypes(TypeSystem ts) {
        // 'e as T' and 'e to T' can throw ClassCastException
        return super.throwTypes(ts);
    }
}
