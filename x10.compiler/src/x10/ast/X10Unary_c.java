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
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.types.X10TypeSystem;
import x10.types.checker.Converter;

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
        Type t = expr.type();
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
            // rebuild the unary using the call's arguments.  We'll actually use the call node after desugaring.
            if (c.methodInstance().flags().isStatic()) {
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
                    if (e.target() instanceof Expr && e.name().id() == Name.make("apply")) {
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

                    X10Call_c n = (X10Call_c) nf.X10Call(position(), target, nf.Id(position(), Name.make("set")), setTypeArgs, setArgTypes);

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

        X10Unary_c n = (X10Unary_c) super.typeCheck(tc);

        Type resultType = n.type();
        resultType = ts.performUnaryOperation(resultType, t, op);
        if (resultType != n.type()) {
            n = (X10Unary_c) n.type(resultType);
        }

        return n;
    }

    public static X10Call_c desugarUnaryOp(Unary n, ContextVisitor tc) throws SemanticException {
        Expr left = n.expr();
        Unary.Operator op = n.operator();
        Position pos = n.position();
        
        Type l = left.type();
        
        X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
        Name methodName = unaryMethodName(op);
        
        // TODO: byte+byte should convert both bytes to int and search int
        // For now, we have to define byte+byte in byte.x10.
        
        X10Call_c virtual_left = null;
        X10Call_c static_left = null;
        
        if (methodName != null) {
            // Check if there is a method with the appropriate name and type with the operand as receiver.   
            X10Call_c n2 = (X10Call_c) nf.X10Call(pos, left, nf.Id(pos, methodName), Collections.EMPTY_LIST, Collections.EMPTY_LIST);
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
            X10Call_c n4 = (X10Call_c) nf.X10Call(pos, nf.CanonicalTypeNode(pos, Types.ref(l)), nf.Id(pos, methodName), Collections.EMPTY_LIST, Collections.singletonList(left));
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
        
        List<X10Call_c> defs = new ArrayList<X10Call_c>();
        if (virtual_left != null) defs.add(virtual_left);
        if (static_left != null) defs.add(static_left);
        
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
                if (n1.arguments().size() == 0 && n1.target() instanceof Expr) {
                    Expr[] actuals = new Expr[] { (Expr) n1.target() };
                    Expr[] original = new Expr[] { left };
                    needsConversion = X10Binary_c.needsConversion(actuals, original);
                    needsExplicitConversion = X10Binary_c.needsExplicitConversion(actuals, original);
                }
                else if (n1.arguments().size() == 1) {
                    Expr[] actuals = new Expr[] { n1.arguments().get(0) };
                    Expr[] original = new Expr[] { left };
                    needsConversion = X10Binary_c.needsConversion(actuals, original);
                    needsExplicitConversion = X10Binary_c.needsExplicitConversion(actuals, original);
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
                    
                    ClassDef d0 = X10Binary_c.def(t0);
                    ClassDef di = X10Binary_c.def(ti);
                    
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

