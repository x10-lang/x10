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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.ast.AmbExpr;
import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Binary_c;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Id;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Precedence;
import polyglot.ast.Prefix;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.types.X10Context;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.checker.Converter;
import x10.visit.ExprFlattener;
import x10.visit.ExprFlattener.Flattener;

/**
 * An immutable representation of a binary operation Expr op Expr.
 * Overridden from Java to allow distributions, regions, points and places to
 * participate in binary operations.
 *
 * @author vj Jan 21, 2005
 */
public class X10Binary_c extends Binary_c implements X10Binary {

    boolean invert;

    /**
     * @param pos
     * @param left
     * @param op
     * @param right
     */
    public X10Binary_c(Position pos, Expr left, Operator op, Expr right) {
        super(pos, left, op, right);
        invert = false;
    }

	public boolean isConstant() {
		if (super.isConstant())
			return true;
		// [IP] An optimization: an object of a non-nullable type and "null"
		// can never be equal.
		Type lt = left.type();
		Type rt = right.type();
		X10TypeSystem xts = (X10TypeSystem) lt.typeSystem();
		if (lt == null || rt == null)
			return false;
	/*	In X10 2.0, nothing can be of Value type.
	 * if (operator() == Binary.EQ || operator() == Binary.NE) {
		    X10Context context = (X10Context) xts.emptyContext();
		    if (xts.isValueType(lt, context) && xts.isReferenceOrInterfaceType(rt, context))
			return true;
		    if (xts.isReferenceOrInterfaceType(lt, context) && xts.isValueType(rt, context))
			return true;
		    if (xts.isValueType(lt, context) && rt.isNull())
			return true;
		    if (xts.isValueType(rt, context) && lt.isNull())
			return true;
		}
		*/
		return false;
	}

    // TODO: take care of the base cases for regions, distributions, points and places.
    public Object constantValue() {
        Object result = super.constantValue();
        if (result != null)
            return result;
        if (!isConstant())
            return null;
        
        Type lt = left.type();
        Type rt = right.type();
        X10TypeSystem xts = (X10TypeSystem) lt.typeSystem();
		X10Context context = (X10Context) xts.emptyContext();
		
		// [IP] An optimization: an value and null can never be equal
	
	 if (op == EQ) {
		    if (xts.isStructType(lt) && xts.isReferenceOrInterfaceType(rt, context))
			return Boolean.FALSE;
		    if (xts.isReferenceOrInterfaceType(lt, context) && xts.isStructType(rt))
			return Boolean.FALSE;
		    if ( xts.isStructType(lt) && rt.isNull())
			return Boolean.FALSE;
		    if ( xts.isStructType(rt) && lt.isNull())
			return Boolean.FALSE;
		}
		if (op == NE) {
		    if (xts.isStructType(lt) && xts.isReferenceOrInterfaceType(rt, context))
			return Boolean.TRUE;
		    if (xts.isReferenceOrInterfaceType(lt, context) && xts.isStructType(rt))
			return Boolean.TRUE;
		    if (xts.isStructType(lt) && rt.isNull())
			return Boolean.TRUE;
		    if (xts.isStructType(rt) && lt.isNull())
			return Boolean.TRUE;
		    return null;
		}
		
        return null;
    }

    /** If the expression was parsed as an ambiguous expression, return a Receiver that would have parsed the same way.  Otherwise, return null. */
    private static Receiver toReceiver(X10NodeFactory nf, Expr e) {
        if (e instanceof AmbExpr) {
            AmbExpr e1 = (AmbExpr) e;
            return nf.AmbReceiver(e.position(), null, e1.name());
        }
        if (e instanceof Field) {
            Field f = (Field) e;
            if (f.target() instanceof Expr) {
                Prefix p = toPrefix(nf, (Expr) f.target());
                if (p == null)
                    return null;
                return nf.AmbReceiver(e.position(), p, f.name());
            }
            else {
                return nf.AmbReceiver(e.position(), f.target(), f.name());
            }
        }
        return null;
    }

    /** If the expression was parsed as an ambiguous expression, return a Prefix that would have parsed the same way.  Otherwise, return null. */
    private static Prefix toPrefix(X10NodeFactory nf, Expr e) {
        if (e instanceof AmbExpr) {
            AmbExpr e1 = (AmbExpr) e;
            return nf.AmbPrefix(e.position(), null, e1.name());
        }
        if (e instanceof Field) {
            Field f = (Field) e;
            if (f.target() instanceof Expr) {
                Prefix p = toPrefix(nf, (Expr) f.target());
                if (p == null)
                    return null;
                return nf.AmbPrefix(e.position(), p, f.name());
            }
            else {
                return nf.AmbPrefix(e.position(), f.target(), f.name());
            }
        }
        return null;
    }

    // HACK: T1==T2 can sometimes be parsed as e1==e2.  Correct that.
    @Override
    public Node typeCheckOverride(Node parent, ContextVisitor tc) throws SemanticException {
        if (op == EQ) {
            X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
            Receiver t1 = toReceiver(nf, left);
            Receiver t2 = toReceiver(nf, right);

            if (t1 != null && t2 != null) {
                Node n1 = this.visitChild(t1, tc);
                Node n2 = this.visitChild(t2, tc);

                if (n1 instanceof TypeNode && n2 instanceof TypeNode) {
                    return nf.SubtypeTest(position(), (TypeNode) n1, (TypeNode) n2, true).disambiguate(tc).typeCheck(tc).checkConstants(tc);
                }
            }
        }

        return null;
    }

    public static Name binaryMethodName(Binary.Operator op) {
        Map<Binary.Operator,String> methodNameMap = new HashMap<Operator, String>();
        methodNameMap.put(ADD, "operator+");
        methodNameMap.put(SUB, "operator-");
        methodNameMap.put(MUL, "operator*");
        methodNameMap.put(DIV, "operator/");
        methodNameMap.put(MOD, "operator%");
        methodNameMap.put(BIT_AND, "operator&");
        methodNameMap.put(BIT_OR, "operator|");
        methodNameMap.put(BIT_XOR, "operator^");
        methodNameMap.put(COND_OR, "operator||");
        methodNameMap.put(COND_AND, "operator&&");
        methodNameMap.put(SHL, "operator<<");
        methodNameMap.put(SHR, "operator>>");
        methodNameMap.put(USHR, "operator>>>");
        methodNameMap.put(LT, "operator<");
        methodNameMap.put(GT, "operator>");
        methodNameMap.put(LE, "operator<=");
        methodNameMap.put(GE, "operator>=");

        // these should not be overridable
        methodNameMap.put(EQ, "operator==");
        methodNameMap.put(NE, "operator!=");

        String methodName = methodNameMap.get(op);
        if (methodName == null)
            return null;
        return Name.make(methodName);
    }

    public static Name invBinaryMethodName(Binary.Operator op) {
        Name n = binaryMethodName(op);
        if (n == null)
            return null;
        return Name.make("inverse_" + n.toString());
    }
    
    private static Type promote(X10TypeSystem ts, Type t1, Type t2) {
        if (ts.isByte(t1)) {
            return t2;
        }
        if (ts.isShort(t1)) {
            if (ts.isByte(t2))
                return t1;
            return t2;
        }
        if (ts.isInt(t1)) {
            if (ts.isByte(t2) || ts.isShort(t2))
                return t1;
            return t2;
        }
        if (ts.isLong(t1)) {
            if (ts.isByte(t2) || ts.isShort(t2) || ts.isInt(t2))
                return t1;
            return t2;
        }

        if (ts.isUByte(t1)) {
            return t2;
        }
        if (ts.isUShort(t1)) {
            if (ts.isUByte(t2))
                return t1;
            return t2;
        }
        if (ts.isUInt(t1)) {
            if (ts.isUByte(t2) || ts.isUShort(t2))
                return t1;
            return t2;
        }
        if (ts.isULong(t1)) {
            if (ts.isUByte(t2) || ts.isUShort(t2) || ts.isUInt(t2))
                return t1;
            return t2;
        }

        if (ts.isFloat(t1)) {
            if (ts.isByte(t2) || ts.isShort(t2) || ts.isInt(t2) || ts.isLong(t2))
                return t1;
            if (ts.isUByte(t2) || ts.isUShort(t2) || ts.isUInt(t2) || ts.isULong(t2))
                return t1;
            return t2;
        }
        if (ts.isDouble(t1)) {
            return t1;
        }
        
        return null;
    }

    /**
     * Type check a binary expression. Must take care of various cases because
     * of operators on regions, distributions, points, places and arrays.
     * An alternative implementation strategy is to resolve each into a method
     * call.
     */
    public Node typeCheck(ContextVisitor tc) throws SemanticException {
        X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
        TypeSystem ts = xts;
        Context context = tc.context();

        Type lbase = X10TypeMixin.baseType(left.type());
        Type rbase = X10TypeMixin.baseType(right.type());

        if (op == EQ || op == NE) {
        	if (xts.isExactlyFunctionType(lbase)) {
        		 throw new SemanticException("The " + op +
                         " operator cannot be applied to the function " + left,
                         position());
        	}
        	if (xts.isExactlyFunctionType(rbase)) {
       		 throw new SemanticException("The " + op +
                        " operator cannot be applied to the function " + right,
                        position());
       	}
        	
        }
        if (op == EQ || op == NE || op == LT || op == GT || op == LE || op == GE) {
            Object lv = left.isConstant() ? left.constantValue() : null;
            Object rv = right.isConstant() ? right.constantValue() : null;
            
            // If comparing signed vs. unsigned, check if one operand is a constant convertible to the other (base) type.
            // If so, insert the conversion and check again.
            
            if (xts.isSigned(lbase) && xts.isUnsigned(rbase) || xts.isSigned(lbase) && xts.isUnsigned(rbase)) {
                if (lv != null && xts.numericConversionValid(rbase, lv, context)) {
                    Expr e = Converter.attemptCoercion(tc, left, rbase);
                    return Converter.check(left(e), tc);
                }
                if (rv != null && xts.numericConversionValid(lbase, rv, context)) {
                    Expr e = Converter.attemptCoercion(tc, right, lbase);
                    return Converter.check(right(e), tc);
                }
            }
            
            if (xts.isUnsigned(lbase) && xts.isSigned(rbase))
                throw new SemanticException("Cannot compare unsigned versus signed values.", position());

            if (xts.isSigned(lbase) && xts.isUnsigned(rbase))
                throw new SemanticException("Cannot compare signed versus unsigned values.", position());
            
            Type promoted = promote(xts, lbase, rbase);
            
            if (promoted != null &&
                (! xts.typeBaseEquals(lbase, promoted, context) ||
                 ! xts.typeBaseEquals(rbase, promoted, context))) {
                Expr el = Converter.attemptCoercion(tc, left, promoted);
                Expr er = Converter.attemptCoercion(tc, right, promoted);
                if (el != left || er != right)
                	return Converter.check(left(el).right(er), tc);
            }
        }
        
        if (op == EQ || op == NE) {
            // == and != are allowed if the *unconstrained* types can be cast to each other without coercions.
            // Coercions are handled above for numerics.  No other coercions are allowed.
            // Constraints are ignored so that things like x==k will not cause compile-time errors
            // when x is a final variable initialized to a constant != k.
            if (xts.isCastValid(lbase, rbase, context) || xts.isCastValid(rbase, lbase, context)) {
                return type(xts.Boolean());
            }
//
//            if (xts.isImplicitCastValid(lbase, rbase, context) || xts.isImplicitCastValid(rbase, lbase, context)) {
//                assert false : "isCastValid but isImplicitCastValid not for " + lbase + " and " + rbase;
//                return type(xts.Boolean());
//            }

            throw new SemanticException("The " + op +
                                        " operator must have operands of comparable type; the types " + lbase + " and " + rbase + " do not share any values.",
                                        position());
        }

        Type l = left.type();
        Type r = right.type();
        
        Call c = desugarBinaryOp(this, tc);
        if (c != null) {
            // rebuild the unary using the call's arguments.  We'll actually use the call node after desugaring.
            if (c.methodInstance().flags().isStatic()) {
                return this.left(c.arguments().get(0)).right(c.arguments().get(1)).type(c.type());
            }
            else {
            	return c;
               // return this.left((Expr) c.target()).right(c.arguments().get(0)).type(c.type());
            }
        }
        
        if (op == COND_OR || op == COND_AND) {
            if (l.isBoolean() && r.isBoolean()) {
                return type(ts.Boolean());
            }
        }

        if (op == ADD) {
            if (ts.isSubtype(l, ts.String(), context) || ts.isSubtype(r, ts.String(), context)) {
                if (!ts.canCoerceToString(l, tc.context())) {
                    throw new SemanticException("Cannot coerce an expression " + 
                                                "of type " + l + " to a String.", 
                                                left.position());
                }
                if (!ts.canCoerceToString(r, tc.context())) {
                    throw new SemanticException("Cannot coerce an expression " + 
                                                "of type " + r + " to a String.", 
                                                right.position());
                }

                Expr newLeft = coerceToString(tc, left);
                Expr newRight = coerceToString(tc, right);

                return precedence(Precedence.STRING_ADD).left(newLeft).right(newRight).type(ts.String());
            }
        }

        throw new SemanticException("No operation " + op + " found for operands " + l + " and " + r + ".", position());
    }
    
    public static X10Call_c desugarBinaryOp(Binary n, ContextVisitor tc) throws SemanticException {
        Expr left = n.left();
        Expr right = n.right();
        Binary.Operator op = n.operator();
        Position pos = n.position();
        
        Type l = left.type();
        Type r = right.type();
        
        X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
        Name methodName = X10Binary_c.binaryMethodName(op);
        Name invMethodName = X10Binary_c.invBinaryMethodName(op);
        
        // TODO: byte+byte should convert both bytes to int and search int
        // For now, we have to define byte+byte in byte.x10.
        
        X10Call_c virtual_left = null;
        X10Call_c virtual_right = null;
        X10Call_c static_left = null;
        X10Call_c static_right = null;
        
        if (methodName != null) {
            // Check if there is a method with the appropriate name and type with the left operand as receiver.   
            X10Call_c n2 = (X10Call_c) nf.X10Call(pos, left, nf.Id(pos, methodName), Collections.EMPTY_LIST, Collections.singletonList(right));
            if (n.isConstant())
                n2 = n2.constantValue(n.constantValue());
        
            try {
                n2 = Converter.check(n2, tc);
                if (! n2.methodInstance().def().flags().isStatic())
                    virtual_left = n2;
            }
            catch (SemanticException e2) {
                // Cannot find the method.  Fall through.
            }
        }
        
        if (methodName != null) {
            // Check if there is a static method of the left type with the appropriate name and type.   
            X10Call_c n4 = (X10Call_c) nf.X10Call(pos, nf.CanonicalTypeNode(pos, Types.ref(l)), nf.Id(pos, methodName), Collections.EMPTY_LIST, CollectionUtil.list(left, right));
            if (n.isConstant())
                n4 = n4.constantValue(n.constantValue());
        
            try {
                n4 = Converter.check(n4, tc);
                if (n4.methodInstance().def().flags().isStatic())
                    static_left = n4;
            }
            catch (SemanticException e2) {
                // Cannot find the method.  Fall through.
            }
        }
        
        if (methodName != null) {
            // Check if there is a static method of the right type with the appropriate name and type.   
            X10Call_c n3 = (X10Call_c) nf.X10Call(pos, nf.CanonicalTypeNode(pos, Types.ref(r)), nf.Id(pos, methodName), Collections.EMPTY_LIST, CollectionUtil.list(left, right));
            if (n.isConstant())
                n3 = n3.constantValue(n.constantValue());
        
            try {
                n3 = Converter.check(n3, tc);
                if (n3.methodInstance().def().flags().isStatic())
                    static_right = n3;
            }
            catch (SemanticException e2) {
                // Cannot find the method.  Fall through.
            }
        }
        
        if (invMethodName != null) {
            // Check if there is a method with the appropriate name and type with the left operand as receiver.   
            X10Call_c n5 = (X10Call_c) nf.X10Call(pos, right, nf.Id(pos, invMethodName), Collections.EMPTY_LIST, Collections.singletonList(left));
            if (n.isConstant())
                n5 = n5.constantValue(n.constantValue());
        
            try {
                n5 = Converter.check(n5, tc);
                if (! n5.methodInstance().def().flags().isStatic())
                    virtual_right = n5;
            }
            catch (SemanticException e2) {
                // Cannot find the method.  Fall through.
            }
        }
        
        List<X10Call_c> defs = new ArrayList<X10Call_c>();
        if (virtual_left != null) defs.add(virtual_left);
        if (virtual_right != null) defs.add(virtual_right);
        if (static_left != null) defs.add(static_left);
        if (static_right != null) defs.add(static_right);
        
        if (defs.size() > 1) {
            X10TypeSystem  ts = (X10TypeSystem) tc.typeSystem();
        
            X10Call_c best = null;
            boolean bestNeedsConversion = false;
            boolean bestNeedsExplicitConversion = false;
        
            for (int i = 0; i < defs.size(); i++) {
                X10Call_c n1 = defs.get(i);
        
                // Check if n needs a conversio
                boolean needsConversion = false;
                boolean needsExplicitConversion = false;
                if (n1.arguments().size() == 1 && n1.target() instanceof Expr) {
                    Expr[] actuals = new Expr[] { (Expr) n1.target(), n1.arguments().get(0) };
                    Expr[] original = new Expr[] { left, right };
                    needsConversion = needsConversion(actuals, original);
                    needsExplicitConversion = needsExplicitConversion(actuals, original);
                }
                else if (n1.arguments().size() == 2) {
                    Expr[] actuals = new Expr[] { n1.arguments().get(0), n1.arguments().get(1) };
                    Expr[] original = new Expr[] { left, right };
                    needsConversion = needsConversion(actuals, original);
                    needsExplicitConversion = needsExplicitConversion(actuals, original);
                }
        
                if (best == null) {
                    best = n1;
                    bestNeedsConversion = needsConversion;
                    bestNeedsExplicitConversion = needsExplicitConversion;
                }
                else if (needsExplicitConversion && ! bestNeedsExplicitConversion) {
                    // best is still the best
                }
                else if (needsConversion && ! bestNeedsConversion) {
                    // best is still the best
                }
                else if (! needsExplicitConversion && bestNeedsExplicitConversion) {
                    best = n1;
                    bestNeedsConversion = needsConversion;
                    bestNeedsExplicitConversion = needsExplicitConversion;
                }
                else if (! needsConversion && bestNeedsConversion) {
                    best = n1;
                    bestNeedsConversion = needsConversion;
                    bestNeedsExplicitConversion = needsExplicitConversion;
                }
                else {
                    MethodDef m0 = best.methodInstance().def();
                    MethodDef mi = n1.methodInstance().def();
                    if (m0 == mi)
                        continue;
        
                    Type t0 = Types.get(m0.container());
                    Type ti = Types.get(mi.container());
                    
                    ClassDef d0 = def(t0);
                    ClassDef di = def(ti);
                    
                    if (d0 == null || di == null) {
                        throw new SemanticException("Ambiguous operator: multiple methods match operator " + op + ": " + m0 + " and " + mi + ".", pos);
                    }
                    
                    if (ts.descendsFrom(d0, di)) {
                        // best is still the best
                    }
                    else if (ts.descendsFrom(di, d0)) {
                        best = n1;
                        bestNeedsConversion = needsConversion;
                        bestNeedsExplicitConversion = needsExplicitConversion;
                    }
                    else {
                        throw new SemanticException("Ambiguous operator: multiple methods match operator " + op + ": " + m0 + " and " + mi + ".", pos);
                    }
                }
            }
        
            assert best != null;
            return best;
        }
        
        if (defs.size() == 1)
            return defs.get(0);
        
        return null;
    }
    
    public static ClassDef def(Type t) {
        Type base = X10TypeMixin.baseType(t);
        if (base instanceof ClassType)
            return ((ClassType) base).def();
        return null;
    }
    
    public static boolean needsExplicitConversion(Expr[] actuals, Expr[] original) {
        for (int k = 0; k < actuals.length; k++) {
            if (actuals[k] != original[k])
                if (actuals[k] instanceof X10Call) {
                    X10Call c = (X10Call) actuals[k];
                    if (c.methodInstance().name() == Converter.operator_as)
                        return true;
                }
        }
        return false;
    }

    public static boolean needsConversion(Expr[] actuals, Expr[] original) {
        for (int k = 0; k < actuals.length; k++) {
            if (actuals[k] != original[k])
                return true;
        }
        return false;
    }

    public static Expr coerceToString(ContextVisitor tc, Expr e) throws SemanticException {
        TypeSystem ts = tc.typeSystem();

        if (!e.type().isSubtype(ts.String(), tc.context())) {
            X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
            e = nf.X10Call(e.position(), nf.CanonicalTypeNode(e.position(), ts.String()),
                           nf.Id(e.position(), Name.make("valueOf")), Collections.EMPTY_LIST,
                           Collections.singletonList(e));
            return (Expr) e.del().disambiguate(tc).typeCheck(tc).checkConstants(tc);
        }

        return e;
    }

    public boolean invert() {
        return invert;
    }

    public X10Binary_c invert(boolean invert) {
        X10Binary_c n = (X10Binary_c) copy();
        n.invert = invert;
        return n;
    }

    /** Flatten the expressions in place and body, creating stmt if necessary.
     * The place field must be visited by the given flattener since those statements must be executed outside
     * the future. Howeever, the body must be visited in a new flattener and the statements produced
     * captured and stored in stmt. 
     * Note that this works by side-effecting the current node. This is necessary
     * because the method is called from within an enter call for a Visitor. I dont know
     * of any way of making the enter call return a copy of the node. 
     * @param fc
     * @return
     */
    public Expr flatten(ExprFlattener.Flattener fc) {
        //Report.report(1, "X10Binary_c: entering X10Binary " + this);
        assert (op== Binary.COND_AND || op==Binary.COND_OR);
        X10Context xc = (X10Context) fc.context();

        final NodeFactory nf = fc.nodeFactory();
        final TypeSystem ts = fc.typeSystem();
        final Position pos = position();
        final Id resultVarName = nf.Id(pos, xc.getNewVarName());
        final TypeNode tn = nf.CanonicalTypeNode(pos,type);
        Flags flags = Flags.NONE;

        final LocalDef li = ts.localDef(pos, flags, Types.ref(type), resultVarName.id());
        // Evaluate the left.
        Expr nLeft = (Expr) left.visit(fc);
        final LocalDecl ld = nf.LocalDecl(pos, nf.FlagsNode(pos, flags), tn, resultVarName, nLeft).localDef(li);
        fc.add(ld);

        final Local ldRef = (Local) nf.Local(pos,resultVarName).localInstance(li.asInstance()).type(type);
        Flattener newVisitor = (Flattener) new ExprFlattener.Flattener(fc.job(), ts, nf, this).context(xc);
        Expr nRight = (Expr) right.visit(newVisitor);
        List condBody = newVisitor.stmtList(); 
        Expr assign = nf.Assign(pos, ldRef, Assign.ASSIGN, nRight ).type(type);
        Stmt eval = nf.Eval(pos, assign);
        condBody.add(eval);
        final Local ldRef2 = (Local) nf.Local(pos,resultVarName).localInstance(li.asInstance()).type(type);

        if (op == Binary.COND_AND) {
            final Stmt ifStm = nf.If(pos, ldRef2, nf.Block(pos, condBody));
            fc.add(ifStm);
        } else {
            Expr cond = nf.Unary(pos, ldRef2, Unary.NOT).type(type);
            final Stmt ifStm = nf.If(pos, cond, nf.Block(pos, condBody));
            fc.add(ifStm);
        }

        final Local ldRef3 = (Local) nf.Local(pos,resultVarName).localInstance(li.asInstance()).type(type);
        //Report.report(1, "X10Binary_c: returning " + ldRef3);
        return ldRef3;

    }
}

