/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Jan 21, 2005
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.ast.AmbExpr;
import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Binary_c;
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
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.visit.ExprFlattener;
import polyglot.ext.x10.visit.ExprFlattener.Flattener;
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
		if (operator() == Binary.EQ || operator() == Binary.NE) {
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
		    if (xts.isValueType(lt, context) && xts.isReferenceOrInterfaceType(rt, context))
			return Boolean.FALSE;
		    if (xts.isReferenceOrInterfaceType(lt, context) && xts.isValueType(rt, context))
			return Boolean.FALSE;
		    if ( xts.isValueType(lt, context) && rt.isNull())
			return Boolean.FALSE;
		    if ( xts.isValueType(rt, context) && lt.isNull())
			return Boolean.FALSE;
		}
		if (op == NE) {
		    if (xts.isValueType(lt, context) && xts.isReferenceOrInterfaceType(rt, context))
			return Boolean.TRUE;
		    if (xts.isReferenceOrInterfaceType(lt, context) && xts.isValueType(rt, context))
			return Boolean.TRUE;
		    if (xts.isValueType(lt, context) && rt.isNull())
			return Boolean.TRUE;
		    if (xts.isValueType(rt, context) && lt.isNull())
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
        methodNameMap.put(ADD, "$plus");
        methodNameMap.put(SUB, "$minus");
        methodNameMap.put(MUL, "$times");
        methodNameMap.put(DIV, "$over");
        methodNameMap.put(MOD, "$percent");
        methodNameMap.put(BIT_AND, "$ampersand");
        methodNameMap.put(BIT_OR, "$bar");
        methodNameMap.put(BIT_XOR, "$caret");
        methodNameMap.put(COND_OR, "$or");
        methodNameMap.put(COND_AND, "$and");
        methodNameMap.put(SHL, "$left");
        methodNameMap.put(SHR, "$right");
        methodNameMap.put(USHR, "$righter");
        methodNameMap.put(LT, "$lt");
        methodNameMap.put(GT, "$gt");
        methodNameMap.put(LE, "$le");
        methodNameMap.put(GE, "$ge");

        // these should not be overridable
        methodNameMap.put(EQ, "$eq");
        methodNameMap.put(NE, "$ne");

        String methodName = methodNameMap.get(op);
        if (methodName == null)
            return null;
        return Name.make(methodName);
    }

    public static Name invBinaryMethodName(Binary.Operator op) {
        Name n = binaryMethodName(op);
        if (n == null)
            return null;
        return Name.make("inv" + n.toString());
    }

    /**
     * Type check a binary expression. Must take care of various cases because
     * of operators on regions, distributions, points, places and arrays.
     * An alternative implementation strategy is to resolve each into a method
     * call.
     */
    public Node typeCheck(ContextVisitor tc) throws SemanticException {
        X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
        
        Context context = tc.context();
        
        X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
        Name methodName = binaryMethodName(op);
        Name invMethodName = invBinaryMethodName(op);
        
        Type l = left.type();
        Type r = right.type();
        
        if (op == EQ || op == NE) {
            if (xts.isNumeric(l) && xts.isNumeric(r)) {
                return type(xts.Boolean());
            }

            if (xts.isCastValid(l, r, context) || xts.isCastValid(r, l, context)) {
                return type(xts.Boolean());
            }

            if (xts.isImplicitCastValid(l, r, context) || xts.isImplicitCastValid(r, l, context)) {
                return type(xts.Boolean());
            }

            throw new SemanticException("The " + op +
                                        " operator must have operands of comparable type; the types " + l + " and " + r + " do not share any values.",
                                        position());
        }

        X10Call_c virtual_left = null;
        X10Call_c virtual_right = null;
        X10Call_c static_left = null;
        X10Call_c static_right = null;

        if (methodName != null) {
            // Check if there is a method with the appropriate name and type with the left operand as receiver.   
            X10Call_c n = (X10Call_c) nf.X10Call(position(), left, nf.Id(position(), methodName), Collections.EMPTY_LIST, Collections.singletonList(right));

            try {
                n = (X10Call_c) n.del().disambiguate(tc).typeCheck(tc).checkConstants(tc);
                virtual_left = n;
            }
            catch (SemanticException e2) {
                // Cannot find the method.  Fall through.
            }
        }

        if (methodName != null) {
            // Check if there is a static method of the left type with the appropriate name and type.   
            X10Call_c n = (X10Call_c) nf.X10Call(position(), nf.CanonicalTypeNode(position(), Types.ref(l)), nf.Id(position(), methodName), Collections.EMPTY_LIST, CollectionUtil.list(left, right));

            try {
                n = (X10Call_c) n.del().disambiguate(tc).typeCheck(tc).checkConstants(tc);
                static_left = n;
            }
            catch (SemanticException e2) {
                // Cannot find the method.  Fall through.
            }
        }

        if (methodName != null) {
            // Check if there is a static method of the right type with the appropriate name and type.   
            X10Call_c n = (X10Call_c) nf.X10Call(position(), nf.CanonicalTypeNode(position(), Types.ref(r)), nf.Id(position(), methodName), Collections.EMPTY_LIST, CollectionUtil.list(left, right));

            try {
                n = (X10Call_c) n.del().disambiguate(tc).typeCheck(tc).checkConstants(tc);
                static_right = n;
            }
            catch (SemanticException e2) {
                // Cannot find the method.  Fall through.
            }
        }


        if (invMethodName != null) {
            // Check if there is a method with the appropriate name and type with the left operand as receiver.   
            X10Call_c n = (X10Call_c) nf.X10Call(position(), right, nf.Id(position(), invMethodName), Collections.EMPTY_LIST, Collections.singletonList(left));

            try {
                n = (X10Call_c) n.del().disambiguate(tc).typeCheck(tc).checkConstants(tc);
                virtual_right = n;
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

            X10Call_c best = null;

            for (int i = 0; i < defs.size(); i++) {
                X10Call_c n = defs.get(i);
                if (best == null)
                    best = n;
                else {
                    MethodDef m0 = best.methodInstance().def();
                    MethodDef mi = n.methodInstance().def();
                    if (m0 == mi)
                        continue;
                    Type t0 = Types.get(m0.container());
                    Type ti = Types.get(mi.container());
                    t0 = X10TypeMixin.baseType(t0);
                    ti = X10TypeMixin.baseType(ti);
                    
                    if (t0 instanceof ClassType && ti instanceof ClassType) {
                    ClassDef c0 = ((ClassType) t0).def();
                    ClassDef ci = ((ClassType) ti).def();
                    if (xts.descendsFrom(ci, c0)) {
                        best = n;
                    }
                    else if (xts.descendsFrom(c0, ci)) {
                        // best is still the best
                    }
                    else {
                        throw new SemanticException("Ambiguous operator: multiple methods match operator " + op + ".", position());
                    }
                    }
                }
            }

            assert best != null;
            return best;
        }

        if (defs.size() == 1)
            return defs.get(0);

        X10Binary_c n = (X10Binary_c) superTypeCheck(tc);

        Type resultType = n.type();
        resultType = xts.performBinaryOperation(resultType, l, r, op);
        if (resultType != n.type()) {
            n = (X10Binary_c) n.type(resultType);
        }

        return n;
    }

    // Override to insert explicit coercions.
    private Expr superTypeCheck(ContextVisitor tc) throws SemanticException {
        Type l = left.type();
        Type r = right.type();

        TypeSystem ts = tc.typeSystem();
        Context context = tc.context();

        if (op == GT || op == LT || op == GE || op == LE) {
            if (l.isNumeric() && r.isNumeric()) {
                return type(ts.Boolean());
            }
        }

        if (op == COND_OR || op == COND_AND) {
            if (l.isBoolean() && r.isBoolean()) {
                return type(ts.Boolean());
            }
        }

        if (op == ADD) {
            if (ts.isSubtype(l, ts.String(), context) || ts.isSubtype(r, ts.String(), context)) {
                if (!ts.canCoerceToString(r, tc.context())) {
                    throw new SemanticException("Cannot coerce an expression " + 
                                                "of type " + r + " to a String.", 
                                                right.position());
                }
                if (!ts.canCoerceToString(l, tc.context())) {
                    throw new SemanticException("Cannot coerce an expression " + 
                                                "of type " + l + " to a String.", 
                                                left.position());
                }
                
                Expr newLeft = coerceToString(tc, left);
                Expr newRight = coerceToString(tc, right);

                return precedence(Precedence.STRING_ADD).left(newLeft).right(newRight).type(ts.String());
            }
        }

        if (op == ADD || op == SUB || op == MUL || op == DIV || op == MOD) {
            if (l.isNumeric() && r.isNumeric()) {
                Type t = ts.promote(l, r);
                Expr le = X10New_c.attemptCoercion(tc, left, t);
                Expr re = X10New_c.attemptCoercion(tc, right, t);
                return left(le).right(re).type(t);
            }
        }

        if (op == BIT_AND || op == BIT_OR || op == BIT_XOR) {
            if (l.isBoolean() && r.isBoolean()) {
                return type(ts.Boolean());
            }

            if (ts.isLongOrLess(l) && ts.isLongOrLess(r)) {
                Type t = ts.promote(l, r);
                Expr le = X10New_c.attemptCoercion(tc, left, t);
                Expr re = X10New_c.attemptCoercion(tc, right, t);
                return left(le).right(re).type(t);
            }
        }

        if (op == SHL || op == SHR || op == USHR) {
            if (ts.isLongOrLess(l) && ts.isLongOrLess(r)) {
                // For shift, only promote the left operand.
                Type t = ts.promote(l);
                Expr le = X10New_c.attemptCoercion(tc, left, t);
                return left(le).type(t);
            }
        }

        throw new SemanticException("No operation " + op + " found for operands " + l + " and " + r + ".", position());
    }

    protected static Expr coerceToString(ContextVisitor tc, Expr e) throws SemanticException {
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
        X10Binary_c	    n = (X10Binary_c) copy();
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

