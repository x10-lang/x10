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

import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.IntLit;
import polyglot.ast.Local;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ast.Unary_c;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.errors.Errors;
import x10.types.X10MethodInstance;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.X10TypeSystem_c;
import x10.types.checker.Converter;
import x10.types.checker.PlaceChecker;
import x10.visit.X10TypeChecker;

/**
 * An immutable representation of a unary operation op Expr.
 * Overridden from Java to allow unary negation of points.
 *
 * @author igor Feb 15, 2006
 */
public class X10Unary_c extends Unary_c {

    /**
     * @param pos
     * @param op
     * @param expr
     */
    public X10Unary_c(Position pos, Operator op, Expr expr) {
        super(pos, op, expr);
    }

    // TODO: take care of constant points.
    public Object constantValue() {
        return super.constantValue();
    }

    /**
     * Type check a binary expression. Must take care of various cases because
     * of operators on regions, distributions, points, places and arrays.
     * An alternative implementation strategy is to resolve each into a method
     * call.
     */
    public Node typeCheck(ContextVisitor tc) throws SemanticException {
        X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
        X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
        Unary.Operator op = this.operator();

        if (op == NEG && expr instanceof IntLit) {
            IntLit.Kind kind = 
                ((IntLit) expr).kind();
            if (kind == IntLit.INT || kind == X10IntLit_c.UINT)
                kind = IntLit.INT;
            else
                kind = IntLit.LONG;
            return Converter.check(nf.IntLit(position(), kind, -((IntLit) expr).longValue()), tc);
        }
        
        Call c = desugarUnaryOp(this, tc);
        if (c != null) {
            X10MethodInstance mi = (X10MethodInstance) c.methodInstance();
            if (mi.error() != null)
                throw mi.error();
            // rebuild the unary using the call's arguments.  We'll actually use the call node after desugaring.
            if (mi.flags().isStatic()) {
                return this.expr(c.arguments().get(0)).type(c.type());
            }
            else {
                return this.expr((Expr) c.target()).type(c.type());
            }
        }

        if (op == POST_INC || op == POST_DEC || op == PRE_INC || op == PRE_DEC) {
            if (! expr.type().isNumeric()) {
                throw new SemanticException("Operand of " + op +
                                            " operator must be numeric.", expr.position());
            }

            if (expr instanceof Local) {
                Local l = (Local) expr;
                if (l.localInstance().flags().isFinal()) {
                    throw new SemanticException("Cannot apply " + op + " to a final variable.", position());
                }
                return type(expr.type());
            }
            else if (expr instanceof Field) {
                Field l = (Field) expr;
                if (l.fieldInstance().flags().isFinal()) {
                    throw new SemanticException("Cannot apply " + op + " to a final variable.", position());
                }
                return type(expr.type());
            }
            else {
                Expr target = null;
                List<Expr> args = null;
                List<TypeNode> typeArgs = null;

                // Handle a(i)++ and a.apply(i)++
                if (expr instanceof ClosureCall) {
                    ClosureCall e = (ClosureCall) expr;
                    target = e.target();
                    args = e.arguments();
                    typeArgs = Collections.EMPTY_LIST; // e.typeArgs();
                }
                else if (expr instanceof X10Call) {
                    X10Call e = (X10Call) expr;
                    if (e.target() instanceof Expr && e.name().id() == ClosureCall.APPLY) {
                        target = (Expr) e.target();
                        args = e.arguments();
                        typeArgs = e.typeArguments();
                    }
                }

                if (target != null) {
                    List<Expr> setArgTypes = new ArrayList<Expr>();
                    List<TypeNode> setTypeArgs = new ArrayList<TypeNode>();

                    // RHS goes before index
                    setArgTypes.add(expr);
                    for (Expr e : args) {
                        setArgTypes.add(e);
                    }
                    for (TypeNode tn : typeArgs) {
                        setTypeArgs.add(tn);
                    }

                    X10Call_c n = (X10Call_c) nf.X10Call(position(), target, nf.Id(position(), SettableAssign.SET), setTypeArgs, setArgTypes);

                    n = (X10Call_c) n.del().disambiguate(tc).typeCheck(tc).checkConstants(tc);

                    // Make sure we don't coerce here.
                    for (int i = 0; i < setArgTypes.size(); i++) {
                        if (setArgTypes.get(i) != n.arguments().get(i))
                            throw new SemanticException("Cannot find method set in " + target.type());
                    }

                    return type(n.methodInstance().returnType());
                }
            }
        }

        Type t = expr.type();

        if (ts.hasUnknown(t))
            throw new SemanticException(); // null message

        X10Unary_c n = (X10Unary_c) super.typeCheck(tc);

        Type resultType = n.type();
        resultType = ts.performUnaryOperation(resultType, t, op);
        if (resultType != n.type()) {
            n = (X10Unary_c) n.type(resultType);
        }

        return n;
    }

    public static X10Call_c desugarUnaryOp(Unary n, ContextVisitor tc) {
        Expr left = n.expr();
        Unary.Operator op = n.operator();
        Position pos = n.position();

        Type l = left.type();

        X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
        Name methodName = unaryMethodName(op);

        if (methodName == null) return null;

        // TODO: byte+byte should convert both bytes to int and search int
        // For now, we have to define byte+byte in byte.x10.

        X10Call_c virtual_left = null;
        X10Call_c static_left = null;

        if (methodName != null) {
            // Check if there is a method with the appropriate name and type with the operand as receiver.   
            X10Call_c n2 = (X10Call_c) nf.X10Call(pos, left, nf.Id(pos, methodName), Collections.EMPTY_LIST, Collections.EMPTY_LIST);
            n2 = X10Binary_c.typeCheckCall(tc, n2);
            X10MethodInstance mi2 = (X10MethodInstance) n2.methodInstance();
            if (mi2.error() == null && !mi2.def().flags().isStatic())
                virtual_left = n2;
        }

        if (methodName != null) {
            // Check if there is a static method of the left type with the appropriate name and type.   
            X10Call_c n4 = (X10Call_c) nf.X10Call(pos, nf.CanonicalTypeNode(pos, Types.ref(l)), nf.Id(pos, methodName), Collections.EMPTY_LIST, Collections.singletonList(left));
            n4 = X10Binary_c.typeCheckCall(tc, n4);
            X10MethodInstance mi4 = (X10MethodInstance) n4.methodInstance();
            if (mi4.error() == null && mi4.def().flags().isStatic())
                static_left = n4;
        }

        List<X10Call_c> defs = new ArrayList<X10Call_c>();
        if (virtual_left != null) defs.add(virtual_left);
        if (static_left != null) defs.add(static_left);

        if (defs.size() == 0) return null;

        X10TypeSystem_c xts = (X10TypeSystem_c) tc.typeSystem();

        List<X10Call_c> best = new ArrayList<X10Call_c>();
        X10Binary_c.Conversion bestConversion = X10Binary_c.Conversion.UNKNOWN;

        for (int i = 0; i < defs.size(); i++) {
            X10Call_c n1 = defs.get(i);

            // Check if n needs a conversion
            Expr[] actuals = new Expr[] {
                n1.arguments().size() != 1 ? (Expr) n1.target() : n1.arguments().get(0)
            };
            Expr[] original = new Expr[] { left };
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
                ClassDef cd = X10Binary_c.def(td);

                for (X10Call_c c : best) {
                    MethodDef bestmd = c.methodInstance().def();
                    assert (bestmd != md) : pos.toString();
                    if (bestmd == md) continue;  // same method by a different path (shouldn't happen for unary)

                    Type besttd = Types.get(bestmd.container());
                    if (xts.isUnknown(besttd) || xts.isUnknown(td)) {
                        best.add(n1);
                        continue;
                    }

                    ClassDef bestcd = X10Binary_c.def(besttd);
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
            if (ct == null) ct = l.toClass();
            SemanticException error = new Errors.AmbiguousOperator(op, bestmis, pos);
            X10MethodInstance mi = xts.createFakeMethod(ct, Flags.PUBLIC.Static(), methodName, Collections.EMPTY_LIST, Collections.singletonList(l), error);
            if (rt != null) mi = mi.returnType(rt);
            result = (X10Call_c) nf.X10Call(pos, nf.CanonicalTypeNode(pos, Types.ref(ct)),
                    nf.Id(pos, methodName), Collections.EMPTY_LIST,
                    Collections.singletonList(left)).methodInstance(mi).type(mi.returnType());
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

    public static Name unaryMethodName(Unary.Operator op) {
        Map<Unary.Operator,String> methodNameMap = new HashMap<Unary.Operator, String>();
        methodNameMap.put(NEG, "operator-");
        methodNameMap.put(POS, "operator+");
        methodNameMap.put(NOT, "operator!");
        methodNameMap.put(BIT_NOT, "operator~");

        String methodName = methodNameMap.get(op);
        if (methodName == null)
            return null;
        return Name.make(methodName);
    }
}

