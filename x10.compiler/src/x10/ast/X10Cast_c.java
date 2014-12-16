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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Cast;
import polyglot.ast.Cast_c;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Precedence;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.main.Options;
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
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import x10.ExtensionInfo;
import x10.X10CompilerOptions;
import x10.errors.Errors;
import x10.errors.Warnings;
import x10.types.ParameterType;
import x10.types.X10ClassType;
import x10.types.X10ParsedClassType;
import polyglot.types.TypeSystem;
import x10.types.checker.Converter;
import x10.types.checker.Converter.ConversionType;
import x10.types.constants.BooleanValue;
import x10.types.constants.CharValue;
import x10.types.constants.ClosureValue;
import x10.types.constants.ConstantValue;
import x10.types.constants.DoubleValue;
import x10.types.constants.FloatValue;
import x10.types.constants.IntegralValue;
import x10.types.constants.NullValue;
import x10.types.constants.StringValue;

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
    protected TypeNode castType;
    protected Expr expr;
    
    public X10Cast_c(Position pos, TypeNode castType, Expr expr, Converter.ConversionType convert) {
        super(pos, castType, expr);
        assert(castType != null && expr != null);
    	this.castType = castType;
    	this.expr = expr;
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
    public X10Cast exprAndConversionType(Expr expr, ConversionType convert) {
    	  X10Cast_c n = (X10Cast_c) copy();
          n.convert = convert;
          n.expr = expr;
          return n;
    }

    /** Get the cast type of the expression. */
    public TypeNode castType() {
	return this.castType;
    }

    /** Set the cast type of the expression. */
    public X10Cast castType(TypeNode castType) {
	X10Cast_c n = (X10Cast_c) copy();
	n.castType = castType;
	return n;
    }

    /** Get the expression being cast. */
    public Expr expr() {
	return this.expr;
    }

    /** Set the expression being cast. */
    public X10Cast expr(Expr expr) {
	X10Cast_c n = (X10Cast_c) copy();
	n.expr = expr;
	return n;
    }

    /** Reconstruct the expression. */
    protected X10Cast_c reconstruct(TypeNode castType, Expr expr) {
    	if (castType != this.castType || expr != this.expr) {
	    	X10Cast_c n = (X10Cast_c) copy();
	    	n.castType = castType;
	    	n.expr = expr;
	    	return n;
		}

		return this;
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

    public Node typeCheck(ContextVisitor tc) {
        if (castType() != null) {
            try {
                Types.checkMissingParameters(castType());
            } catch (SemanticException e) {
                Errors.issue(tc.job(), e, this);
            }
        }
        try {
            Expr e = Converter.converterChain(this, tc);
            final Type type = e.type();
            assert type != null;
            assert ! (e instanceof X10Cast_c) || ((X10Cast_c) e).conversionType() != Converter.ConversionType.UNKNOWN_CONVERSION;
            assert ! (e instanceof X10Cast_c) || ((X10Cast_c) e).conversionType() != Converter.ConversionType.UNKNOWN_IMPLICIT_CONVERSION;

            // todo hack: after constraints will be kept at runtime, and we will do constraint solving at runtime, then all casts will be sound!
            // X10 currently doesn't do constraint solving at runtime (and constraints are erased at runtime!),
            // so given o:Any, a cast:
            //  o as Array[Int]
            // is unsound if "o" had constraints (e.g., Array[Int{self!=0}])
            // obviously,   o as Array[Int{self!=0}]     is always unsound.
            // Note that any generic struct will this warning due to the auto-generated equals method:
            //struct A[T] {
            //  public def equals(o:Any) {
            //    if (o instanceof A[T]) {
            //      val x = o as A[T]; // Warning: unsound cast!
            //      ...
            // Therefore we do not produce warnings in compiler-generated code (too confusing for the programmer).
            // In addition, I also don't report the 3 warnings we have in XRX (or else every client of HashMap will have a warning)
            if (!position.isCompilerGenerated() &&
                    !position.file().contains("Accumulator.x10")&&
                    !position.file().contains("Array.x10")&&
                    !position.file().contains("Box.x10")&&
                    !position.file().contains("HashMap.x10")&&
                    !position.file().contains("FinishState.x10")&&
                    !position.file().contains("Runtime.x10")&& 
                    !position.file().contains("HashSet.x10")) {
                Type base = Types.baseType(type);
                boolean isClassType = base instanceof X10ParsedClassType;
                boolean isParamType = base instanceof ParameterType;
                if (isClassType || isParamType) {
                    List<Type> args = null;
                    if (isClassType) {
                        X10ParsedClassType classType = (X10ParsedClassType) base;
                        args = classType.typeArguments();
                    }
                    if (isParamType || (args!=null && args.size()>0)) {
                        boolean isOk = false;
                        if (e instanceof X10Cast) {
                            // ok, e.g., x:Array[Int],   x as Array[Int](3)
                            final X10Cast cast = (X10Cast) e;
                            if (cast.conversionType()== ConversionType.SUBTYPE)
                                isOk = true;
                            else if (tc.typeSystem().isSubtype(Types.baseType(cast.expr().type()),base, tc.context()))
                                isOk = true;
                        }
                        if (!isOk) {
                            final ExtensionInfo extensionInfo = (ExtensionInfo) tc.job().extensionInfo();
                            X10CompilerOptions opts = extensionInfo.getOptions();
                            if (opts.x10_config.VERBOSE) { // it used to be VERBOSE_CHECKS, but then how do I get this error if I want to run with STATIC_CHECKS???
                                Warnings.issue(tc.job(), "This is an unsound cast because X10 currently does not perform constraint solving at runtime for generic parameters.", position);
                            }
                        }
                    }
                }
            }
            return e;
        } catch (SemanticException e) {
            Errors.issue(tc.job(), e, this);
        }
        return this;
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
        if (expr.type().isReference()) {
            return Collections.<Type>singletonList(ts.ClassCastException());
        }

        return Collections.<Type>emptyList();
    }
    
    /** Write the expression to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr)
    {
        if (convert == ConversionType.UNBOXING) {
            w.begin(0);
            print(castType, w, tr);
            w.write(".$unbox(");
            printSubExpr(expr, w, tr);
            w.write(")");
            w.end();
        } else if (convert == ConversionType.BOXING) {
            w.begin(0);
            print(castType, w, tr);
            w.write(".$box(");
            printSubExpr(expr, w, tr);
            w.write(")");
            w.end();
            
        } else {
            w.begin(0);
            w.write("(");
            print(castType, w, tr);
            w.write(")");
            w.allowBreak(2, " ");
            printSubExpr(expr, w, tr);
            w.end();
        }
    }

    public Term firstChild() {
        return expr;
    }
    
    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        v.visitCFG(expr, castType, ENTRY);
        v.visitCFG(castType, this, EXIT);
        return succs;
    }
    
    /** Visit the children of the expression. */
    public Node visitChildren(NodeVisitor v) {
    	TypeNode castType = (TypeNode) visitChild(this.castType, v);
    	Expr expr = (Expr) visitChild(this.expr, v);
    	return reconstruct(castType, expr);
    }

    public boolean isConstant() {
        if (!expr.isConstant()) return false;

        boolean ctIntrinsic = castType.type().isNumeric() || castType.type().isUnsignedNumeric() || castType.type().isChar();
        boolean etIntrinsic = expr.type().isNumeric() || expr.type().isUnsignedNumeric() || expr.type().isChar();
        if (ctIntrinsic && etIntrinsic) return true;
        
        TypeSystem ts = expr.type().typeSystem();
        ConstantValue exprCV = expr.constantValue();
        if (exprCV instanceof NullValue && ts.isObjectOrInterfaceType(castType.type(), ts.emptyContext())) return true;
        
        if (exprCV instanceof StringValue && ts.String().isSubtype(castType.type(), ts.emptyContext())) return true;

        if (exprCV instanceof BooleanValue && ts.Boolean().isSubtype(castType.type(), ts.emptyContext())) return true;
        
        if (exprCV instanceof ClosureValue) return true;

        // NOTE: The cast might still be statically removable, but that is different than having a constant value.
        return false;
    }
        
    public ConstantValue constantValue() {
    	ConstantValue v = expr.constantValue();

    	if (v == null) {
    	    return null;
    	}
    	
    	Type cType = castType.type();
    	
        if (v instanceof IntegralValue) {
            return ConstantValue.make(cType, ((IntegralValue) v).longValue());
        }
        
    	if (v instanceof DoubleValue) {
    	    return ConstantValue.make(cType, ((DoubleValue) v).value());
    	}
    	
    	if (v instanceof FloatValue) {
            return ConstantValue.make(cType, ((FloatValue) v).value());
    	}

    	if (v instanceof CharValue) {
    	    return ConstantValue.make(cType, (long)((CharValue)v).value());
    	}
    	
    	TypeSystem ts = cType.typeSystem();
    	Context emptyContext = ts.emptyContext();
    	
    	if (v instanceof StringValue) {
    	    if (ts.String().isSubtype(cType, emptyContext)) return v;
    	}
    	
        if (v instanceof BooleanValue) {
            if (ts.Boolean().isSubtype(cType, emptyContext)) return v;
        }
                
        if (v instanceof NullValue) {
            if (ts.isObjectOrInterfaceType(cType, emptyContext)) return v;
        }
        
        if (v instanceof ClosureValue) {
            return v;
        }
    	
    	return null;
    }
}
