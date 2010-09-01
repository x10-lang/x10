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
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CollectionUtil;
import polyglot.util.Pair;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.errors.Errors;
import x10.types.X10Context;
import x10.types.X10MethodInstance;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.X10TypeSystem_c;
import x10.types.checker.Converter;
import x10.types.checker.PlaceChecker;
import x10.visit.ExprFlattener;
import x10.visit.X10TypeChecker;
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
                    Node n = nf.SubtypeTest(position(), (TypeNode) n1, (TypeNode) n2, true);
                    n = n.del().disambiguate(tc);
                    n = n.del().typeCheck(tc);
                    n = n.del().checkConstants(tc);
                    return n;
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
    public Node typeCheck(ContextVisitor tc) {
        X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
        Context context = tc.context();

        Type lbase = X10TypeMixin.baseType(left.type());
        Type rbase = X10TypeMixin.baseType(right.type());

        if (op == EQ || op == NE) {
            if (xts.isExactlyFunctionType(lbase)) {
                Errors.issue(tc.job(),
                        new SemanticException("The " + op +
                                " operator cannot be applied to the function " + left,
                                position()));
            }
            if (xts.isExactlyFunctionType(rbase)) {
                Errors.issue(tc.job(),
                        new SemanticException("The " + op +
                                " operator cannot be applied to the function " + right,
                                position()));
            }
        }

        if (op == EQ || op == NE || op == LT || op == GT || op == LE || op == GE) {
            Object lv = left.isConstant() ? left.constantValue() : null;
            Object rv = right.isConstant() ? right.constantValue() : null;
            
            // If comparing signed vs. unsigned, check if one operand is a constant convertible to the other (base) type.
            // If so, insert the conversion and check again.
            
            if ((xts.isSigned(lbase) && xts.isUnsigned(rbase)) || (xts.isUnsigned(lbase) && xts.isSigned(rbase))) {
                try {
                    if (lv != null && xts.numericConversionValid(rbase, lbase, lv, context)) {
                        Expr e = Converter.attemptCoercion(tc, left, rbase);
                        if (e == left)
                            return this.type(xts.Boolean());
                        return Converter.check(left(e), tc);
                    }
                    if (rv != null && xts.numericConversionValid(lbase, rbase, rv, context)) {
                        Expr e = Converter.attemptCoercion(tc, right, lbase);
                        if (e == right)
                            return this.type(xts.Boolean());
                        return Converter.check(right(e), tc);
                    }
                } catch (SemanticException e) { } // FIXME
            }
            
            if (xts.isUnsigned(lbase) && xts.isSigned(rbase))
                Errors.issue(tc.job(),
                        new SemanticException("Cannot compare unsigned versus signed values.", position()));

            if (xts.isSigned(lbase) && xts.isUnsigned(rbase))
                Errors.issue(tc.job(),
                        new SemanticException("Cannot compare signed versus unsigned values.", position()));
            
            Type promoted = promote(xts, lbase, rbase);
            
            if (promoted != null &&
                (! xts.typeBaseEquals(lbase, promoted, context) ||
                 ! xts.typeBaseEquals(rbase, promoted, context)))
            {
                try {
                Expr el = Converter.attemptCoercion(tc, left, promoted);
                Expr er = Converter.attemptCoercion(tc, right, promoted);
                if (el != left || er != right)
                	return Converter.check(left(el).right(er), tc);
                } catch (SemanticException e) { } // FIXME
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

            Errors.issue(tc.job(),
                    new SemanticException("The " + op +
                            " operator must have operands of comparable type; the types " + lbase + " and " + rbase + " do not share any values.",
                            position()));
            return type(xts.Boolean());
        }

        Call c = desugarBinaryOp(this, tc);
        if (c != null) {
            X10MethodInstance mi = (X10MethodInstance) c.methodInstance();
            if (mi.error() != null) {
                Errors.issue(tc.job(), mi.error(), this);
            }
            // rebuild the binary using the call's arguments.  We'll actually use the call node after desugaring.
            if (mi.flags().isStatic()) {
                return this.left(c.arguments().get(0)).right(c.arguments().get(1)).type(c.type());
            }
            else if (!c.name().id().equals(invBinaryMethodName(this.operator()))) {
                assert (c.name().id().equals(binaryMethodName(this.operator())));
                return this.left((Expr) c.target()).right(c.arguments().get(0)).type(c.type());
            }
            else {
                return this.left(c.arguments().get(0)).right((Expr) c.target()).type(c.type());
            }
        }
        
        Type l = left.type();
        Type r = right.type();

        if (!xts.hasUnknown(l) && !xts.hasUnknown(r)) {
            if (op == COND_OR || op == COND_AND) {
                if (l.isBoolean() && r.isBoolean()) {
                    return type(xts.Boolean());
                }
            }

            Errors.issue(tc.job(),
                    new SemanticException("No operation " + op + " found for operands " + l + " and " + r + ".", position()));
        }

        return this.type(xts.unknownType(position()));
    }

    public static X10Call_c typeCheckCall(ContextVisitor tc, X10Call_c call) {
        List<Type> typeArgs = new ArrayList<Type>(call.typeArguments().size());
        for (TypeNode tn : call.typeArguments()) {
            typeArgs.add(tn.type());
        }

        List<Type> argTypes = new ArrayList<Type>(call.arguments().size());
        for (Expr e : call.arguments()) {
            Type et = e.type();
            argTypes.add(et);
        }
        Type targetType = call.target().type();
        X10MethodInstance mi = null;
        List<Expr> args = null;
        // First try to find the method without implicit conversions.
        Pair<MethodInstance, List<Expr>> p = X10Call_c.findMethod(tc, call, targetType, call.name().id(), typeArgs, argTypes);
        mi = (X10MethodInstance) p.fst();
        args = p.snd();
        if (mi.error() != null) {
            try {
                // Now, try to find the method with implicit conversions, making them explicit.
                p = X10Call_c.tryImplicitConversions(call, tc, targetType, call.name().id(), typeArgs, argTypes);
                mi = (X10MethodInstance) p.fst();
                args = p.snd();
            } catch (SemanticException e) { }
        }
        Type rt = X10Field_c.rightType(mi.rightType(), mi.x10Def(), call.target(), tc.context());
        call = (X10Call_c) call.methodInstance(mi).type(rt);
        call = (X10Call_c) call.arguments(args);
        return call;
    }
    
    public static X10Call_c desugarBinaryOp(Binary n, ContextVisitor tc) {
        Expr left = n.left();
        Expr right = n.right();
        Binary.Operator op = n.operator();
        Position pos = n.position();

        Type l = left.type();
        Type r = right.type();

        // Equality operators are special
        if (op == EQ || op == NE)
            return null;

        // Conditional operators on Booleans are special
        if ((op == COND_OR || op == COND_AND) && l.isBoolean() && r.isBoolean())
            return null;

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
            X10Call_c n2 = (X10Call_c) nf.X10Call(pos, left, nf.Id(pos, methodName), Collections.<TypeNode>emptyList(), Collections.singletonList(right));
            n2 = typeCheckCall(tc, n2);
            X10MethodInstance mi2 = (X10MethodInstance) n2.methodInstance();
            if (mi2.error() == null && !mi2.def().flags().isStatic())
                virtual_left = n2;
        }

        if (methodName != null) {
            // Check if there is a static method of the left type with the appropriate name and type.   
            X10Call_c n4 = (X10Call_c) nf.X10Call(pos, nf.CanonicalTypeNode(pos, Types.ref(l)), nf.Id(pos, methodName), Collections.<TypeNode>emptyList(), CollectionUtil.list(left, right));
            n4 = typeCheckCall(tc, n4);
            X10MethodInstance mi4 = (X10MethodInstance) n4.methodInstance();
            if (mi4.error() == null && mi4.def().flags().isStatic())
                static_left = n4;
        }

        if (methodName != null) {
            // Check if there is a static method of the right type with the appropriate name and type.   
            X10Call_c n3 = (X10Call_c) nf.X10Call(pos, nf.CanonicalTypeNode(pos, Types.ref(r)), nf.Id(pos, methodName), Collections.<TypeNode>emptyList(), CollectionUtil.list(left, right));
            n3 = typeCheckCall(tc, n3);
            X10MethodInstance mi3 = (X10MethodInstance) n3.methodInstance();
            if (mi3.error() == null && mi3.def().flags().isStatic())
                static_right = n3;
        }

        if (invMethodName != null) {
            // Check if there is a method with the appropriate name and type with the left operand as receiver.   
            X10Call_c n5 = (X10Call_c) nf.X10Call(pos, right, nf.Id(pos, invMethodName), Collections.<TypeNode>emptyList(), Collections.singletonList(left));
            n5 = typeCheckCall(tc, n5);
            X10MethodInstance mi5 = (X10MethodInstance) n5.methodInstance();
            if (mi5.error() == null && !mi5.def().flags().isStatic())
                virtual_right = n5;
        }

        List<X10Call_c> defs = new ArrayList<X10Call_c>();
        if (virtual_left != null) defs.add(virtual_left);
        if (virtual_right != null) defs.add(virtual_right);
        if (static_left != null) defs.add(static_left);
        if (static_right != null) defs.add(static_right);

        if (defs.size() == 0) {
            if (methodName == null)
                return null;
            // Create a fake instance method of the left type with the appropriate name and type.   
            X10Call_c fake = (X10Call_c) nf.X10Call(pos, left, nf.Id(pos, methodName), Collections.<TypeNode>emptyList(), Collections.singletonList(right));
            fake = typeCheckCall(tc, fake);
            return fake;
        }

        X10TypeSystem_c xts = (X10TypeSystem_c) tc.typeSystem();

        List<X10Call_c> best = new ArrayList<X10Call_c>();
        X10Binary_c.Conversion bestConversion = X10Binary_c.Conversion.UNKNOWN;

        for (int i = 0; i < defs.size(); i++) {
            X10Call_c n1 = defs.get(i);

            // Check if n needs a conversion
            Expr[] actuals = new Expr[] {
                n1.arguments().size() != 2 ? (Expr) n1.target() : n1.arguments().get(0),
                n1.arguments().size() != 2 ? n1.arguments().get(0) : n1.arguments().get(1),
            };
            Expr[] original = new Expr[] { left, right };
            X10Binary_c.Conversion conversion = X10Binary_c.conversionNeeded(actuals, original);

            if (bestConversion.harder(conversion)) {
                best.clear();
                best.add(n1);
                bestConversion = conversion;
            }
            else if (conversion.harder(bestConversion)) {
                // best is still the best
            }
            else {  // all other things being equal
                MethodDef md = n1.methodInstance().def();
                Type td = Types.get(md.container());
                ClassDef cd = def(td);

                for (X10Call_c c : best) {
                    MethodDef bestmd = c.methodInstance().def();
                    if (bestmd == md) continue;  // same method by a different path

                    Type besttd = Types.get(bestmd.container());
                    if (xts.isUnknown(besttd) || xts.isUnknown(td)) {
                        best.add(n1);
                        continue;
                    }

                    ClassDef bestcd = def(besttd);
                    assert (bestcd != null && cd != null);

                    if (xts.descendsFrom(cd, bestcd)) {
                        best.clear();
                        best.add(n1);
                        bestConversion = conversion;
                    }
                    else if (xts.descendsFrom(bestcd, cd)) {
                        // best is still the best
                    }
                    else {
                        best.add(n1);
                    }
                }
            }
        }
        assert (best.size() != 0);

        X10Call_c result = best.get(0);
        if (best.size() > 1) {
            List<MethodInstance> bestmis = new ArrayList<MethodInstance>();
            Type rt = null;
            boolean rtset = false;
            ClassType ct = null;
            boolean ctset = false;
            // See if all matches have the same container and return type, and save that to avoid losing information.
            for (X10Call_c c : best) {
                MethodInstance xmi = c.methodInstance();
                bestmis.add(xmi);
                if (!rtset) {
                    rt = xmi.returnType();
                    rtset = true;
                } else if (rt != null && !xts.typeEquals(rt, xmi.returnType(), tc.context())) {
                    if (xts.typeBaseEquals(rt, xmi.returnType(), tc.context())) {
                        rt = X10TypeMixin.baseType(rt);
                    } else {
                        rt = null;
                    }
                }
                if (!ctset) {
                    ct = xmi.container().toClass();
                    ctset = true;
                } else if (ct != null && !xts.typeEquals(ct, xmi.container(), tc.context())) {
                    if (xts.typeBaseEquals(ct, xmi.container(), tc.context())) {
                        ct = X10TypeMixin.baseType(ct).toClass();
                    } else {
                        ct = null;
                    }
                }
            }
            if (ct == null) ct = l.toClass();  // arbitrarily pick the left operand
            SemanticException error = new Errors.AmbiguousOperator(op, bestmis, pos);
            X10MethodInstance mi = xts.createFakeMethod(ct, Flags.PUBLIC.Static(), methodName, Collections.EMPTY_LIST, CollectionUtil.list(l, r), error);
            if (rt != null) mi = mi.returnType(rt);
            result = (X10Call_c) nf.X10Call(pos, nf.CanonicalTypeNode(pos, Types.ref(ct)),
                    nf.Id(pos, methodName), Collections.EMPTY_LIST,
                    CollectionUtil.list(left, right)).methodInstance(mi).type(mi.returnType());
        }
        try {
            result = (X10Call_c) PlaceChecker.makeReceiverLocalIfNecessary(result, tc);
        } catch (SemanticException e) {
            X10MethodInstance mi = (X10MethodInstance) result.methodInstance();
            if (mi.error() == null)
                result = (X10Call_c) result.methodInstance(mi.error(e));
        }
        if (n.isConstant())
            result = result.constantValue(n.constantValue());
        return result;
    }

    /**
     * Get the class that defines type t.  Only returns null if
     * t is a parameter type or an unknown type.
     */
    public static ClassDef def(Type t) {
        Type base = X10TypeMixin.baseType(t);
        if (base instanceof ClassType)
            return ((ClassType) base).def();
        return null;
    }

    public static enum Conversion {
        NONE { boolean harder(Conversion a) { return false; } },
        IMPLICIT { boolean harder(Conversion a) { return a == NONE; } },
        EXPLICIT { boolean harder(Conversion a) { return a == NONE || a == IMPLICIT; } },
        UNKNOWN { boolean harder(Conversion a) { return a != UNKNOWN; } };
        abstract boolean harder(Conversion b);
    }
    public static Conversion conversionNeeded(Expr[] actuals, Expr[] original) {
        Conversion result = Conversion.NONE;
        for (int k = 0; k < actuals.length; k++) {
            if (actuals[k] != original[k]) {
                if (result == Conversion.NONE)
                    result = Conversion.IMPLICIT;
                if (actuals[k] instanceof X10Call) {
                    X10Call c = (X10Call) actuals[k];
                    if (c.methodInstance().name() == Converter.operator_as) {
                        result = Conversion.EXPLICIT;
                    }
                }
            }
        }
        return result;
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

