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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import polyglot.ast.AmbExpr;
import polyglot.ast.Binary;
import polyglot.ast.Binary_c;
import polyglot.ast.BooleanLit;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Precedence;
import polyglot.ast.Prefix;
import polyglot.ast.Receiver;
import polyglot.ast.StringLit;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.ast.Binary.Operator;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.util.InternalCompilerError;
import polyglot.util.Pair;
import polyglot.util.Position;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.FlowGraph;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import x10.constraint.XFailure;
import x10.constraint.XLit;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.errors.Errors;
import x10.errors.Warnings;
import x10.types.MethodInstance;
import x10.types.X10Use;
import x10.types.checker.Checker;
import x10.types.checker.Converter;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.BuiltInTypeRules;

/**
 * An immutable representation of a binary operation Expr op Expr.
 * Overridden from Java to allow distributions, regions, points and places to
 * participate in binary operations.
 *
 * @author vj Jan 21, 2005
 */
public class X10Binary_c extends Binary_c implements X10Binary {

    protected Expr left;
    protected Operator op;
    protected Expr right;
    protected Precedence precedence;

    /**
     * @param pos
     * @param left
     * @param op
     * @param right
     */
    public X10Binary_c(Position pos, Expr left, Operator op, Expr right) {
        super(pos, left, op, right);
    	assert(left != null && op != null && right != null);
    	this.left = left;
    	this.op = op;
    	this.right = right;
    	this.precedence = op.precedence();
    	if (op == ADD && (left instanceof StringLit || right instanceof StringLit)) {
            this.precedence = Precedence.STRING_ADD;
        }
        invert = false;
    }

    private boolean invert;

    public boolean invert() {
        return invert;
    }

    public X10Binary_c invert(boolean invert) {
        if (invert == this.invert) return this; 
        X10Binary_c n = (X10Binary_c) copy();
        n.invert = invert;
        return n;
    }

    private MethodInstance mi;

    public MethodInstance methodInstance() {
        return mi;
    }

    public X10Binary_c methodInstance(MethodInstance mi) {
        if (mi == this.mi) return this;
        X10Binary_c n = (X10Binary_c) copy();
        n.mi = mi;
        return n;
    }

    // FIXME: need to figure out if the implementation is pure (can't assume that for user-overloadable operators)
    protected static boolean isPureOperation(Type left, Operator op, Type right) {
        switch (op) {
        case ADD:
        case SUB:
        case MUL:
            return true;
        case DIV:
        case MOD:
            return false; // division/modulus can throw an exception, thus is not pure
        case EQ:
        case NE:
        case GT:
        case LT:
        case GE:
        case LE:
            return true;
        case SHL:
        case SHR:
        case USHR:
            return true;
        case BIT_AND:
        case BIT_OR:
        case BIT_XOR:
            return true;
        case COND_AND:
        case COND_OR:
            return true;
        case ARROW:
        case DOT_DOT:
            return false;
        }
        return false;
    }

	public boolean isConstant() {
	    // Polyglot doesn't understand how to constant fold unsigned types.
	    if (left.type().isUnsignedNumeric() || right.type().isUnsignedNumeric()) return false;
	    
		if (left.isConstant() && right.isConstant() && isPureOperation(left.type(), op, right.type()))
			return true;
		// FIXME [IP] An optimization: an object of a non-nullable type and "null"
		// can never be equal.
		Type lt = left.type();
		Type rt = right.type();
		TypeSystem xts = (TypeSystem) lt.typeSystem();
		if (lt == null || rt == null)
			return false;
		return false;
	}

    // TODO: take care of the base cases for regions, distributions, points and places.
    public Object constantValue() {
        Object result = superConstantValue();
        if (result != null)
            return result;
        if (!isConstant())
            return null;
        
        Type lt = left.type();
        Type rt = right.type();
        TypeSystem xts = (TypeSystem) lt.typeSystem();
		Context context = (Context) xts.emptyContext();
		
		// [IP] An optimization: an value and null can never be equal
	
	 if (op == EQ) {
		    if (xts.isStructType(lt) && xts.isObjectOrInterfaceType(rt, context))
			return Boolean.FALSE;
		    if (xts.isObjectOrInterfaceType(lt, context) && xts.isStructType(rt))
			return Boolean.FALSE;
		    if ( xts.isStructType(lt) && rt.isNull())
			return Boolean.FALSE;
		    if ( xts.isStructType(rt) && lt.isNull())
			return Boolean.FALSE;
		}
		if (op == NE) {
		    if (xts.isStructType(lt) && xts.isObjectOrInterfaceType(rt, context))
			return Boolean.TRUE;
		    if (xts.isObjectOrInterfaceType(lt, context) && xts.isStructType(rt))
			return Boolean.TRUE;
		    if (xts.isStructType(lt) && rt.isNull())
			return Boolean.TRUE;
		    if (xts.isStructType(rt) && lt.isNull())
			return Boolean.TRUE;
		    return null;
		}
		
        return null;
    }
    
    private Object superConstantValue() {
    	if (! isConstant()) {
    		return null;
    	}

	    if (left.type().isUnsignedNumeric() || right.type().isUnsignedNumeric()) {
	    	return null;
	    }

	    Object lv = left.constantValue();
        Object rv = right.constantValue();

        if (op == ADD && (lv instanceof String || rv instanceof String)) {
            // toString() does what we want for String, Number, and Boolean
        	if (lv == null) lv = "null";
        	if (rv == null) rv = "null";
        	return lv.toString() + rv.toString();       
        }

        if (op == EQ && (lv instanceof String && rv instanceof String)) {
            return Boolean.valueOf(((String) lv).intern() == ((String) rv).intern());
        }

        if (op == NE && (lv instanceof String && rv instanceof String)) {
            return Boolean.valueOf(((String) lv).intern() != ((String) rv).intern());
        }

        // promote chars to ints.
        if (lv instanceof Character) {
            lv = Integer.valueOf(((Character) lv).charValue());
        }

        if (rv instanceof Character) {
            rv = Integer.valueOf(((Character) rv).charValue());
        }

        try {
            if (lv instanceof Number && rv instanceof Number) {
                if (lv instanceof Double || rv instanceof Double) {
                    double l = ((Number) lv).doubleValue();
                    double r = ((Number) rv).doubleValue();
                    if (op == ADD) return Double.valueOf(l + r);
                    if (op == SUB) return Double.valueOf(l - r);
                    if (op == MUL) return Double.valueOf(l * r);
                    if (op == DIV) return Double.valueOf(l / r);
                    if (op == MOD) return Double.valueOf(l % r);
                    if (op == EQ) return Boolean.valueOf(l == r);
                    if (op == NE) return Boolean.valueOf(l != r);
                    if (op == LT) return Boolean.valueOf(l < r);
                    if (op == LE) return Boolean.valueOf(l <= r);
                    if (op == GE) return Boolean.valueOf(l >= r);
                    if (op == GT) return Boolean.valueOf(l > r);
                    return null;
                }

                if (lv instanceof Float || rv instanceof Float) {
                    float l = ((Number) lv).floatValue();
                    float r = ((Number) rv).floatValue();
                    if (op == ADD) return Float.valueOf(l + r);
                    if (op == SUB) return Float.valueOf(l - r);
                    if (op == MUL) return Float.valueOf(l * r);
                    if (op == DIV) return Float.valueOf(l / r);
                    if (op == MOD) return Float.valueOf(l % r);
                    if (op == EQ) return Boolean.valueOf(l == r);
                    if (op == NE) return Boolean.valueOf(l != r);
                    if (op == LT) return Boolean.valueOf(l < r);
                    if (op == LE) return Boolean.valueOf(l <= r);
                    if (op == GE) return Boolean.valueOf(l >= r);
                    if (op == GT) return Boolean.valueOf(l > r);
                    return null;
                }

                if (lv instanceof Long && rv instanceof Number) {
                    long l = ((Long) lv).longValue();
                    long r = ((Number) rv).longValue();
                    if (op == SHL) return Long.valueOf(l << r);
                    if (op == SHR) return Long.valueOf(l >> r);
                    if (op == USHR) return Long.valueOf(l >>> r);
                }

                if (lv instanceof Long || rv instanceof Long) {
                    long l = ((Number) lv).longValue();
                    long r = ((Number) rv).longValue();
                    if (op == ADD) return Long.valueOf(l + r);
                    if (op == SUB) return Long.valueOf(l - r);
                    if (op == MUL) return Long.valueOf(l * r);
                    if (op == DIV) return Long.valueOf(l / r);
                    if (op == MOD) return Long.valueOf(l % r);
                    if (op == EQ) return Boolean.valueOf(l == r);
                    if (op == NE) return Boolean.valueOf(l != r);
                    if (op == LT) return Boolean.valueOf(l < r);
                    if (op == LE) return Boolean.valueOf(l <= r);
                    if (op == GE) return Boolean.valueOf(l >= r);
                    if (op == GT) return Boolean.valueOf(l > r);
                    if (op == BIT_AND) return Long.valueOf(l & r);
                    if (op == BIT_OR) return Long.valueOf(l | r);
                    if (op == BIT_XOR) return Long.valueOf(l ^ r);
                    return null;
                }

                // At this point, both lv and rv must be ints.
                int l = ((Number) lv).intValue();
                int r = ((Number) rv).intValue();

                if (op == ADD) return Integer.valueOf(l + r);
                if (op == SUB) return Integer.valueOf(l - r);
                if (op == MUL) return Integer.valueOf(l * r);
                if (op == DIV) return Integer.valueOf(l / r);
                if (op == MOD) return Integer.valueOf(l % r);
                if (op == EQ) return Boolean.valueOf(l == r);
                if (op == NE) return Boolean.valueOf(l != r);
                if (op == LT) return Boolean.valueOf(l < r);
                if (op == LE) return Boolean.valueOf(l <= r);
                if (op == GE) return Boolean.valueOf(l >= r);
                if (op == GT) return Boolean.valueOf(l > r);
                if (op == BIT_AND) return Integer.valueOf(l & r);
                if (op == BIT_OR) return Integer.valueOf(l | r);
                if (op == BIT_XOR) return Integer.valueOf(l ^ r);
                if (op == SHL) return Integer.valueOf(l << r);
                if (op == SHR) return Integer.valueOf(l >> r);
                if (op == USHR) return Integer.valueOf(l >>> r);
                return null;
            }
        }
        catch (ArithmeticException e) {
            // ignore div by 0
            return null;
        }

        if (lv instanceof Boolean && rv instanceof Boolean) {
            boolean l = ((Boolean) lv).booleanValue();
            boolean r = ((Boolean) rv).booleanValue();

            if (op == EQ) return Boolean.valueOf(l == r);
            if (op == NE) return Boolean.valueOf(l != r);
            if (op == BIT_AND) return Boolean.valueOf(l & r);
            if (op == BIT_OR) return Boolean.valueOf(l | r);
            if (op == BIT_XOR) return Boolean.valueOf(l ^ r);
            if (op == COND_AND) return Boolean.valueOf(l && r);
            if (op == COND_OR) return Boolean.valueOf(l || r);
        }

        return null;
    }

    /** If the expression was parsed as an ambiguous expression, return a Receiver that would have parsed the same way.  Otherwise, return null. */
    private static Receiver toReceiver(NodeFactory nf, Expr e) {
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
    private static Prefix toPrefix(NodeFactory nf, Expr e) {
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
    public Node typeCheckOverride(Node parent, ContextVisitor tc) {
        if (op == EQ) {
            NodeFactory nf = (NodeFactory) tc.nodeFactory();
            Receiver t1 = toReceiver(nf, left);
            Receiver t2 = toReceiver(nf, right);

            if (t1 != null && t2 != null) {
                Node n1 = this.visitChild(t1, tc);
                Node n2 = this.visitChild(t2, tc);

                if (n1 instanceof TypeNode && n2 instanceof TypeNode) {
                    SubtypeTest n = nf.SubtypeTest(position(), (TypeNode) n1, (TypeNode) n2, true);
                    n = (SubtypeTest) n.typeCheck(tc);
                    return n;
                }
            }
        }

        return null;
    }

    public static Name binaryMethodName(Binary.Operator op) {
        Map<Binary.Operator,String> methodNameMap = CollectionFactory.newHashMap();
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
        methodNameMap.put(DOT_DOT, "operator..");
        methodNameMap.put(ARROW, "operator->");

        String methodName = methodNameMap.get(op);
        if (methodName == null)
            return null;
        return Name.makeUnchecked(methodName);
    }

    public static final String INVERSE_OPERATOR_PREFIX = "inverse_";
    public static Name invBinaryMethodName(Binary.Operator op) {
        Name n = binaryMethodName(op);
        if (n == null)
            return null;
        return Name.makeUnchecked(INVERSE_OPERATOR_PREFIX + n.toString());
    }

    public static boolean isInv(Name name) {
        return name.toString().startsWith(INVERSE_OPERATOR_PREFIX);
    }
    
    private static Type promote(TypeSystem ts, Type t1, Type t2) {
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
        TypeSystem xts = (TypeSystem) tc.typeSystem();
        Context context = (Context) tc.context();

        Type lbase = Types.baseType(left.type());
        Type rbase = Types.baseType(right.type());
        if (xts.hasUnknown(lbase) || xts.hasUnknown(rbase))
        	return this.type(xts.unknownType(position()));
        
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
                        if (e != null)
                            return Converter.check(this.left(e), tc);
                    }
                    if (rv != null && xts.numericConversionValid(lbase, rbase, rv, context)) {
                        Expr e = Converter.attemptCoercion(tc, right, lbase);
                        if (e == right)
                            return this.type(xts.Boolean());
                        if (e != null)
                            return Converter.check(this.right(e), tc);
                    }
                } catch (SemanticException e) { } // FIXME
            }
            
            if (xts.isUnsigned(lbase) && xts.isSigned(rbase))
                Errors.issue(tc.job(),
                        new Errors.CannotCompareUnsignedVersusSignedValues(position()));

            if (xts.isSigned(lbase) && xts.isUnsigned(rbase))
                Errors.issue(tc.job(),
                        new Errors.CannotCompareSignedVersusUnsignedValues(position()));
            
            Type promoted = promote(xts, lbase, rbase);
            
            if (promoted != null &&
                (! xts.typeBaseEquals(lbase, promoted, context) ||
                 ! xts.typeBaseEquals(rbase, promoted, context)))
            {
                try {
                Expr el = Converter.attemptCoercion(tc, left, promoted);
                Expr er = Converter.attemptCoercion(tc, right, promoted);
                if (el != null && er != null && (el != left || er != right))
                	return Converter.check(this.left(el).right(er), tc);
                } catch (SemanticException e) { } // FIXME
            }
        }
        
        if (op == EQ || op == NE) {
            // == and != are allowed if the *unconstrained* types can be cast to each other without coercions.
            // Coercions are handled above for numerics.  No other coercions are allowed.
            // Constraints are ignored so that things like x==k will not cause compile-time errors
            // when x is a final variable initialized to a constant != k.

            // Yoav: I remove the constraints from inside generic arguments as well (see XTENLANG-2022)
            Type lbbase = Types.stripConstraints(lbase);
            Type rbbase = Types.stripConstraints(rbase);
            if (xts.isCastValid(lbbase, rbbase, context) || xts.isCastValid(rbbase, lbbase, context)) {
                return type(xts.Boolean());
            }
//
//            if (xts.isImplicitCastValid(lbase, rbase, context) || xts.isImplicitCastValid(rbase, lbase, context)) {
//                assert false : "isCastValid but isImplicitCastValid not for " + lbase + " and " + rbase;
//                return type(xts.Boolean());
//            }

            Errors.issue(tc.job(),
                    new Errors.OperatorMustHaveOperandsOfComparabletype(lbase, rbase, position()));
            return this.type(xts.Boolean());
        }

        X10Call c = desugarBinaryOp(this, tc);

        if (c != null) {
            MethodInstance mi = (MethodInstance) c.methodInstance();
            Warnings.checkErrorAndGuard(tc,mi, this);

            X10Binary_c result = (X10Binary_c) this.methodInstance(mi).type(c.type());

            // rebuild the binary using the call's arguments.  We'll actually use the call node after desugaring.
            if (mi.flags().isStatic()) {
                return result.left(c.arguments().get(0)).right(c.arguments().get(1));
            }
            else if (!c.name().id().equals(invBinaryMethodName(this.operator()))) {
                assert (c.name().id().equals(binaryMethodName(this.operator())));
                return result.left((Expr) c.target()).right(c.arguments().get(0));
            }
            else {
                return result.invert(true).left(c.arguments().get(0)).right((Expr) c.target());
            }
        }
        
        Type l = left.type();
        Type r = right.type();

        if (!xts.hasUnknown(l) && !xts.hasUnknown(r)) {
            if (op == COND_OR || op == COND_AND) {
            	Type result = xts.Boolean();
            	if (op == COND_OR)
            		return this.type(xts.Boolean());
                if (l.isBoolean() && r.isBoolean()) {
                	return this.type(BuiltInTypeRules.adjustReturnTypeForConjunction(l,r, context));
                }
            }

            Errors.issue(tc.job(),
                    new Errors.NoOperationFoundForOperands(op, l, r, position()));
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
        MethodInstance mi = null;
        List<Expr> args = null;
        // First try to find the method without implicit conversions.
        Pair<MethodInstance, List<Expr>> p = Checker.findMethod(tc, call, targetType, call.name().id(), typeArgs, argTypes);
        mi = p.fst();
        args = p.snd();
        if (mi.error() != null) {
            try {
                // Now, try to find the method with implicit conversions, making them explicit.
                p = Checker.tryImplicitConversions(call, tc, targetType, call.name().id(), typeArgs, argTypes);
                mi =  p.fst();
                args = p.snd();
            } catch (SemanticException e) {
                // FIXME: [IP] The exception may indicate that there's an ambiguity, which is better than reporting that a method is not found.
                int i = 3;
            }
        }
        Type rt = Checker.rightType(mi.rightType(), mi.x10Def(), call.target(), tc.context());
        call = (X10Call_c) call.methodInstance(mi).type(rt);
        call = (X10Call_c) call.arguments(args);
        return call;
    }

    public static X10Call_c searchInstance1(Name methodName, Position pos, ContextVisitor tc, Expr first, Expr second) {
        NodeFactory nf = tc.nodeFactory();
        // Check if there is a method with the appropriate name and type with the left operand as receiver.
        X10Call_c n2 = (X10Call_c) nf.X10Call(pos, first, nf.Id(pos, methodName), Collections.<TypeNode>emptyList(), Collections.singletonList(second));
        n2 = typeCheckCall(tc, n2);
        MethodInstance mi2 = (MethodInstance) n2.methodInstance();
        if (mi2.error() == null && !mi2.def().flags().isStatic())
            return n2;
        return null;
    }
    public static X10Call_c searchInstance(Name methodName, Position pos, ContextVisitor tc, Expr first, Expr second) {
        if (methodName != null) {
            X10Call_c res = searchInstance1(methodName,pos,tc,first,second);
            if (res!=null) return res;

            // maybe the left operand can be cast to the right operand (e.g., Byte+Int should use Int.operator+(Int) and not Byte.operator+(Byte))
            Expr newFirst = Converter.attemptCoercion(
                    tc, first,
                    Types.baseType(second.type())); // I use baseType because the constraints are irrelevant for resolution (and it can cause an error if the constraint contain "place23423423")
            if (newFirst!=first && newFirst!=null) {
                return searchInstance1(methodName,pos,tc,newFirst,second);
            }
        }
        return null;
    }
    public static X10Call_c desugarBinaryOp(Binary n, ContextVisitor tc) {
        Expr left = n.left();
        Expr right = n.right();
        Binary.Operator op = n.operator();
        Position pos = n.position();
        TypeSystem xts = tc.typeSystem();

        Type l = left.type();
        Type r = right.type();

        // Equality operators are special
        if (op == EQ || op == NE)
            return null;

        // Conditional operators on Booleans are special
        if ((op == COND_OR || op == COND_AND) && l.isBoolean() && r.isBoolean())
            return null;

        NodeFactory nf = tc.nodeFactory();
        Name methodName = X10Binary_c.binaryMethodName(op);
        Name invMethodName = X10Binary_c.invBinaryMethodName(op);

        // TODO: byte+byte should convert both bytes to int and search int
        // For now, we have to define byte+byte in byte.x10.

        X10Call_c virtual_left;
        X10Call_c virtual_right;
        X10Call_c static_left = null;
        X10Call_c static_right = null;

        virtual_right = searchInstance(invMethodName,pos,tc,right,left);
        virtual_left = searchInstance(methodName,pos,tc,left,right);

        if (methodName != null) {
            // Check if there is a static method of the left type with the appropriate name and type.   
            X10Call_c n4 = (X10Call_c) nf.X10Call(pos, nf.CanonicalTypeNode(pos, Types.ref(l)), 
            		nf.Id(pos, methodName), Collections.<TypeNode>emptyList(), 
            		CollectionUtil.list(left, right));
            n4 = typeCheckCall(tc, n4);
            MethodInstance mi4 = (MethodInstance) n4.methodInstance();
            if (mi4.error() == null && mi4.def().flags().isStatic())
                static_left = n4;
        }

        if (methodName != null && !xts.hasSameClassDef(l, r)) {
            // Check if there is a static method of the right type with the appropriate name and type.   
            X10Call_c n3 = (X10Call_c) nf.X10Call(pos, nf.CanonicalTypeNode(pos, Types.ref(r)), 
            		nf.Id(pos, methodName), Collections.<TypeNode>emptyList(), 
            		CollectionUtil.list(left, right));
            n3 = typeCheckCall(tc, n3);
            MethodInstance mi3 = (MethodInstance) n3.methodInstance();
            if (mi3.error() == null && mi3.def().flags().isStatic())
                static_right = n3;
        }


        List<X10Call_c> defs = new ArrayList<X10Call_c>();
        if (virtual_left != null) defs.add(virtual_left);
        if (virtual_right != null) defs.add(virtual_right);
        if (static_left != null) defs.add(static_left);
        if (static_right != null) defs.add(static_right);

        if (defs.size() == 0) {
            if (methodName == null)
                return null;
            // Create a fake static method in the left type with the appropriate name and type.   
            X10Call_c fake = (X10Call_c) nf.Call(pos, nf.CanonicalTypeNode(pos, left.type()), nf.Id(pos, methodName), left, right);
            fake = typeCheckCall(tc, fake);
            return fake;
        }

        List<X10Call_c> best = new ArrayList<X10Call_c>();
        X10Binary_c.Conversion bestConversion = X10Binary_c.Conversion.UNKNOWN;

        for (int i = 0; i < defs.size(); i++) {
            X10Call_c n1 = defs.get(i);

            // Check if n needs a conversion
            Expr[] actuals = new Expr[] {
                n1.arguments().size() != 2 ? (Expr) n1.target() : n1.arguments().get(0),
                n1.arguments().size() != 2 ? n1.arguments().get(0) : n1.arguments().get(1),
            };
            boolean inverse = isInv(n1.name().id());
            Expr[] original = new Expr[] {
                inverse ? right : left,
                inverse ? left : right,
            };
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

                boolean isBetter = false;
                for (Iterator<X10Call_c> ci = best.iterator(); ci.hasNext(); ) {
                    X10Call_c c = ci.next();
                    MethodDef bestmd = c.methodInstance().def();
                    if (bestmd == md) break;  // same method by a different path; already in best

                    Type besttd = Types.get(bestmd.container());
                    if (xts.isUnknown(besttd) || xts.isUnknown(td)) {
                        // going to create a fake one anyway; might as well get more data
                        isBetter = true;
                        continue;
                    }

                    ClassDef bestcd = def(besttd);
                    assert (bestcd != null && cd != null);

                    if (cd != bestcd && xts.descendsFrom(cd, bestcd)) {
                        // we found the method of a subclass; remove the superclass one
                        ci.remove();
                        isBetter = true;
                        assert (bestConversion == conversion);
                        bestConversion = conversion;
                    }
                    else if (cd != bestcd && xts.descendsFrom(bestcd, cd)) {
                        // best is still the best
                        isBetter = false;
                        break;
                    }
                    else {
                        isBetter = true;
                    }
                }
                if (isBetter)
                    best.add(n1);
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
                        rt = Types.baseType(rt);
                    } else {
                        rt = null;
                    }
                }
                if (!ctset) {
                    ct = xmi.container().toClass();
                    ctset = true;
                } else if (ct != null && !xts.typeEquals(ct, xmi.container(), tc.context())) {
                    if (xts.typeBaseEquals(ct, xmi.container(), tc.context())) {
                        ct = Types.baseType(ct).toClass();
                    } else {
                        ct = null;
                    }
                }
            }
            if (ct == null) ct = l.toClass();  // arbitrarily pick the left operand
            SemanticException error = new Errors.AmbiguousOperator(op, bestmis, pos);
            MethodInstance mi = xts.createFakeMethod(ct, Flags.PUBLIC.Static(), methodName,
                    Collections.<Type>emptyList(), CollectionUtil.list(l, r), error);
            if (rt != null) mi = mi.returnType(rt);
            result = (X10Call_c) nf.X10Call(pos, nf.CanonicalTypeNode(pos, Types.ref(ct)),
                    nf.Id(pos, methodName), Collections.<TypeNode>emptyList(),
                    CollectionUtil.list(left, right)).methodInstance(mi).type(mi.returnType());
        } 
        {
        MethodInstance mi = result.methodInstance();
        boolean inverse = isInv(mi.name());
        left = result.arguments().size() != 2 ? (Expr) result.target() : result.arguments().get(0);
        right = result.arguments().size() != 2 ? result.arguments().get(0) : result.arguments().get(1);
        Type lbase = Types.baseType(left.type());
        Type rbase = Types.baseType(right.type());
        Context context = (Context) tc.context();

        // Add support for patching up the return type of Region's operator*().
        // The rank of the result is a+b, if the rank of the left arg is a and of the right arg is b,
        // and a and b are literals. Further the result is rect if both args are rect, and zeroBased
        // if both args are zeroBased.
        if (op == Binary.MUL && xts.typeEquals(xts.Region(), lbase, context)
        		&& xts.typeEquals(xts.Region(), rbase, context)) {
            Type type = result.type();
            type = BuiltInTypeRules.adjustReturnTypeForRegionMult(left, right, type, context);
            mi = mi.returnType(type);
            result = (X10Call_c) result.methodInstance(mi).type(type);
        }
        // Add support for patching up the return type of IntRegion's operator*().
        // The rank of the result is 2.  Further the result is zeroBased if both args are zeroBased.
        if (op == Binary.MUL && xts.typeEquals(xts.IntRange(), lbase, context)
                && xts.typeEquals(xts.IntRange(), rbase, context)) {
            Type type = result.type();
            type = BuiltInTypeRules.adjustReturnTypeForRangeRangeMult(left, right, type, context);
            mi = mi.returnType(type);
            result = (X10Call_c) result.methodInstance(mi).type(type);
        }
        // Add support for patching up the return type of Int's operator..(),
        // The result is zeroBased if the left arg is 0.
        if (op == Binary.DOT_DOT && xts.typeEquals(xts.Int(), lbase, context)
                && xts.typeEquals(xts.Int(), rbase, context)) {
            Type type = result.type();
            type = BuiltInTypeRules.adjustReturnTypeForIntRange(left, right, type, context);
            mi = mi.returnType(type);
            result = (X10Call_c) result.methodInstance(mi).type(type);
        }
        }
        try {
            result = (X10Call_c) PlaceChecker.makeReceiverLocalIfNecessary(result, tc);
        } catch (SemanticException e) {
            MethodInstance mi = (MethodInstance) result.methodInstance();
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
        Type base = Types.baseType(t);
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
            NodeFactory nf = tc.nodeFactory();
            e = nf.X10Call(e.position(), nf.CanonicalTypeNode(e.position(), ts.String()),
                           nf.Id(e.position(), Name.make("valueOf")),
                           Collections.<TypeNode>emptyList(), Collections.singletonList(e));
            return (Expr) e.del().disambiguate(tc).typeCheck(tc).checkConstants(tc);
        }

        return e;
    }
    
    /** Get the left operand of the expression. */
    public Expr left() {
	return this.left;
    }

    /** Set the left operand of the expression. */
    public Binary left(Expr left) {
    	X10Binary_c n = (X10Binary_c) copy();
    	n.left = left;
    	return n;
    }

    /** Get the operator of the expression. */
    public Operator operator() {
	return this.op;
    }

    /** Set the operator of the expression. */
    public Binary operator(Operator op) {
    	X10Binary_c n = (X10Binary_c) copy();
    	n.op = op;
    	return n;
    }

    /** Get the right operand of the expression. */
    public Expr right() {
	return this.right;
    }

    /** Set the right operand of the expression. */
    public Binary right(Expr right) {
    	X10Binary_c n = (X10Binary_c) copy();
    	n.right = right;
    	return n;
    }

    /** Get the precedence of the expression. */
    public Precedence precedence() {
	return this.precedence;
    }

    public Binary precedence(Precedence precedence) {
    	X10Binary_c n = (X10Binary_c) copy();
    	n.precedence = precedence;
    	return n;
    }

    /** Reconstruct the expression. */
    protected Binary_c reconstruct(Expr left, Expr right) {
	if (left != this.left || right != this.right) {
	    X10Binary_c n = (X10Binary_c) copy();
	    n.left = left;
	    n.right = right;
	    return n;
	}

	return this;
    }

    /** Visit the children of the expression. */
    public Node visitChildren(NodeVisitor v) {
	Expr left = (Expr) visitChild(this.left, v);
	Expr right = (Expr) visitChild(this.right, v);
	return reconstruct(left, right);
    }
    
    public Type childExpectedType(Expr child, AscriptionVisitor av) {
        Expr other;

        if (child == left) {
            other = right;
        }
        else if (child == right) {
            other = left;
        }
        else {
            return child.type();
        }

        TypeSystem ts = av.typeSystem();
        Context context = av.context();

        try {
            if (op == EQ || op == NE) {
                // Coercion to compatible types.
                if ((child.type().isReference() || child.type().isNull()) &&
                    (other.type().isReference() || other.type().isNull())) {
                    return ts.leastCommonAncestor(child.type(), other.type(), context);
                }

                if (child.type().isBoolean() && other.type().isBoolean()) {
                    return ts.Boolean();
                }

                if (child.type().isNumeric() && other.type().isNumeric()) {
                    return ts.promote(child.type(), other.type());
                }

                if (child.type().isImplicitCastValid(other.type(), context)) {
                    return other.type();
                }

                return child.type();
            }

            if (op == ADD && ts.typeEquals(type(), ts.String(), context)) {
                // Implicit coercion to String. 
                return ts.String();
            }

            if (op == GT || op == LT || op == GE || op == LE) {
                if (child.type().isNumeric() && other.type().isNumeric()) {
                    return ts.promote(child.type(), other.type());
                }

                return child.type();
            }

            if (op == COND_OR || op == COND_AND) {
                return ts.Boolean();
            }

            if (op == BIT_AND || op == BIT_OR || op == BIT_XOR) {
                if (other.type().isBoolean()) {
                    return ts.Boolean();
                }

                if (child.type().isNumeric() && other.type().isNumeric()) {
                    return ts.promote(child.type(), other.type());
                }

                return child.type();
            }

            if (op == ADD || op == SUB || op == MUL || op == DIV || op == MOD) {
                if (child.type().isNumeric() && other.type().isNumeric()) {
                    Type t = ts.promote(child.type(), other.type());

                    if (ts.isImplicitCastValid(t, av.toType(), context)) {
                        return t;
                    }
                    else {
                        return av.toType();
                    }
                }

                return child.type();
            }

            if (op == SHL || op == SHR || op == USHR) {
                if (child.type().isNumeric() && other.type().isNumeric()) {
                    if (child == left) {
                        Type t = ts.promote(child.type());

                        if (ts.isImplicitCastValid(t, av.toType(), context)) {
                            return t;
                        }
                        else {
                            return av.toType();
                        }
                    }
                    else {
                        return ts.promote(child.type());
                    }
                }

                return child.type();
            }

            return child.type();
        }
        catch (SemanticException e) {
        }

        return child.type();
    }

    /** Get the throwsArithmeticException of the expression. */
    public boolean throwsArithmeticException() {
	// conservatively assume that any division or mod may throw
	// ArithmeticException this is NOT true-- floats and doubles don't
	// throw any exceptions ever...
	return op == DIV || op == MOD;
    }

    public String toString() {
	return left + " " + op + " " + right;
    }

    /** Write the expression to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	printSubExpr(left, true, w, tr);
	w.write(" ");
	w.write(op.toString());
	w.allowBreak(type() == null || type().isJavaPrimitive() ? 2 : 0, " ");
	printSubExpr(right, false, w, tr);
    }

  public void dump(CodeWriter w) {
    super.dump(w);

    if (type != null) {
      w.allowBreak(4, " ");
      w.begin(0);
      w.write("(type " + type + ")");
      w.end();
    }

    w.allowBreak(4, " ");
    w.begin(0);
    w.write("(operator " + op + ")");
    w.end();
  }

  public Term firstChild() {
    return left;
  }

  public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
    if (op == COND_AND || op == COND_OR) {
      // short-circuit
      if (left instanceof BooleanLit) {
        BooleanLit b = (BooleanLit) left;
        if ((b.value() && op == COND_OR) || (! b.value() && op == COND_AND)) {
          v.visitCFG(left, this, EXIT);
        }
        else {
          v.visitCFG(left, right, ENTRY);
          v.visitCFG(right, this, EXIT);
        }
      }
      else {
        if (op == COND_AND) {
          // AND operator
          // short circuit means that left is false
          v.visitCFG(left, FlowGraph.EDGE_KEY_TRUE, right, 
                           ENTRY, FlowGraph.EDGE_KEY_FALSE, this, EXIT);
        }
        else {
          // OR operator
          // short circuit means that left is true
          v.visitCFG(left, FlowGraph.EDGE_KEY_FALSE, right, 
                           ENTRY, FlowGraph.EDGE_KEY_TRUE, this, EXIT);
        }
        v.visitCFG(right, FlowGraph.EDGE_KEY_TRUE, this,
                          EXIT, FlowGraph.EDGE_KEY_FALSE, this, EXIT);
      }
    }
    else {
      if (left.type().isBoolean() && right.type().isBoolean()) {        
          v.visitCFG(left, FlowGraph.EDGE_KEY_TRUE, right,
                           ENTRY, FlowGraph.EDGE_KEY_FALSE, right, ENTRY);
          v.visitCFG(right, FlowGraph.EDGE_KEY_TRUE, this,
                            EXIT, FlowGraph.EDGE_KEY_FALSE, this, EXIT);
      }
      else {
          v.visitCFG(left, right, ENTRY);
          v.visitCFG(right, this, EXIT);
      }
    }

    return succs;
  }

  public List<Type> throwTypes(TypeSystem ts) {
    if (throwsArithmeticException()) {
      return Collections.<Type>singletonList(ts.ArithmeticException());
    }

    return Collections.<Type>emptyList();
  }
}

