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
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10MethodInstance;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.visit.ExprFlattener;
import polyglot.ext.x10.visit.ExprFlattener.Flattener;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.constraint.XConstraint;

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

	/** Get the precedence of the expression. */
//	public Precedence precedence() {
//		/* [IP] TODO: This should be the real precedence */
//		Type l = left.type();
//		X10TypeSystem xts = (X10TypeSystem) l.typeSystem();
//		if (xts.isPoint(l) || xts.isPlace(l) || xts.isDistribution(l)
//				|| xts.isRegion(l) ||
//			xts.isPrimitiveTypeArray(l) || xts.isDistributedArray(l))
//		{
//			return Precedence.LITERAL;
//		}
//		return super.precedence();
//	}

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
		    if (xts.isValueType(lt) && xts.isReferenceOrInterfaceType(rt))
			return true;
		    if (xts.isReferenceOrInterfaceType(lt) && xts.isValueType(rt))
			return true;
		    if ( xts.isValueType(lt) && rt.isNull())
			return true;
		    if ( xts.isValueType(rt) && lt.isNull())
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

		Object lv = left.constantValue();
		Object rv = right.constantValue();
		Type lt = left.type();
		Type rt = right.type();
		X10TypeSystem xts = (X10TypeSystem) lt.typeSystem();

		
		// [IP] An optimization: an value and null can never be equal
		if (op == EQ) {
		    if (xts.isValueType(lt) && xts.isReferenceOrInterfaceType(rt))
			return Boolean.FALSE;
		    if (xts.isReferenceOrInterfaceType(lt) && xts.isValueType(rt))
			return Boolean.FALSE;
		    if ( xts.isValueType(lt) && rt.isNull())
			return Boolean.FALSE;
		    if ( xts.isValueType(rt) && lt.isNull())
			return Boolean.FALSE;
		}
		if (op == NE) {
		    if (xts.isValueType(lt) && xts.isReferenceOrInterfaceType(rt))
			return Boolean.TRUE;
		    if (xts.isReferenceOrInterfaceType(lt) && xts.isValueType(rt))
			return Boolean.TRUE;
		    if ( xts.isValueType(lt) && rt.isNull())
			return Boolean.TRUE;
		    if ( xts.isValueType(rt) && lt.isNull())
			return Boolean.TRUE;
		    return null;
		}
//
//		try {
//			if (xts.isDistribution(lt)) {
//				dist l = (dist) lv;
//				if (xts.isDistribution(rt)) {
//					if (op == COND_OR) return l.union((dist) rv);
//				}
//				if (xts.isPlace(rt)) {
//					if (op == BIT_OR) return l.restriction((place) rv);
//				}
//				if (xts.isRegion(rt)) {
//					if (op == BIT_OR) return l.restriction((region) rv);
//					if (op == SUB) return l.difference((region) rv);
//				}
//			}
//			if (xts.isRegion(lt)) {
//				region l = (region) lv;
//				if (xts.isRegion(rt)) {
//					region r = (region) rv;
//					if (op == SUB) return l.difference(r);
//					if (op == COND_OR) return l.union(r);
//					if (op == COND_AND) return l.intersection(r);
//				}
//			}
//		} catch (ArithmeticException e) {
//			// ignore div by 0
//		}
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
		
		String methodName = methodNameMap.get(op);
		if (methodName == null)
		    return null;
		return Name.make(methodName);
	}
	
	public static Name invBinaryMethodName(Binary.Operator op) {
	    Name n = binaryMethodName(op);
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
		
		Type l = left.type();
		Type r = right.type();

		if (op == EQ || op == NE) {
		    if (xts.isNumeric(l) && xts.isNumeric(r)) {
		        return type(xts.Boolean());
		    }

		    if (xts.isCastValid(l, r) || xts.isCastValid(r, l)) {
		        return type(xts.Boolean());
		    }
		    
		    if (xts.isImplicitCastValid(l, r) || xts.isImplicitCastValid(r, l)) {
		        return type(xts.Boolean());
		    }
		    
		    throw new SemanticException("The " + op +
		                                " operator must have operands of comparable type; the types " + l + " and " + r + " do not share any values.",
		                                position());
		}
		
		X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
		Name methodName = binaryMethodName(op);
		
		if (methodName != null) {
		    // Check if there is a method with the appropriate name and type with the left operand as receiver.   
		    X10Call_c n = (X10Call_c) nf.X10Call(position(), left, nf.Id(position(), methodName), Collections.EMPTY_LIST, Collections.singletonList(right));

		    try {
		        n = (X10Call_c) n.del().disambiguate(tc).typeCheck(tc).checkConstants(tc);
		        return type(n.methodInstance().returnType());
		    }
		    catch (SemanticException e2) {
		        // Cannot find the method.  Fall through.
		    }
		}
		
		Name invMethodName = invBinaryMethodName(op);
		
		if (invMethodName != null) {
		    // Check if there is a method with the appropriate name and type with the left operand as receiver.   
		    X10Call_c n = (X10Call_c) nf.X10Call(position(), right, nf.Id(position(), invMethodName), Collections.EMPTY_LIST, Collections.singletonList(left));

		    try {
		        n = (X10Call_c) n.del().disambiguate(tc).typeCheck(tc).checkConstants(tc);
		        return invert(true).type(n.methodInstance().returnType());
		    }
		    catch (SemanticException e2) {
		        // Cannot find the method.  Fall through.
		    }
		}
		
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
        
        if (op == GT || op == LT || op == GE || op == LE) {
            if (! l.isNumeric()) {
        	throw new SemanticException("The " + op +
        	    " operator must have numeric operands, not type " +
                    l + ".", left.position());
            }
        
            if (! r.isNumeric()) {
        	throw new SemanticException("The " + op +
        	    " operator must have numeric operands, not type " +
                    r + ".", right.position());
            }
        
            return type(ts.Boolean());
        }
        
        if (op == EQ || op == NE) {
            if (! ts.isCastValid(l, r) && ! ts.isCastValid(r, l)) {
        	throw new SemanticException("The " + op +
        	    " operator must have operands of similar type.",
        	    position());
            }
        
            return type(ts.Boolean());
        }
        
        if (op == COND_OR || op == COND_AND) {
            if (! l.isBoolean()) {
        	throw new SemanticException("The " + op +
        	    " operator must have boolean operands, not type " +
                    l + ".", left.position());
            }
        
            if (! r.isBoolean()) {
        	throw new SemanticException("The " + op +
        	    " operator must have boolean operands, not type " +
                    r + ".", right.position());
            }
        
            return type(ts.Boolean());
        }
        
        if (op == ADD) {
            if (ts.isSubtype(l, ts.String()) || ts.isSubtype(r, ts.String())) {
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
        
                return precedence(Precedence.STRING_ADD).type(ts.String());
            }
        }
        
        if (op == ADD) {
            if (! l.isNumeric()) {
                throw new SemanticException("The " + op +
                    " operator must have numeric or String operands, not type " +
                    l + ".", left.position());
            }
        
            if (! r.isNumeric()) {
                throw new SemanticException("The " + op +
                    " operator must have numeric or String operands, not type " +
                    r + ".", right.position());
            }
        }
        
        if (op == BIT_AND || op == BIT_OR || op == BIT_XOR) {
            if (l.isBoolean() && r.isBoolean()) {
                return type(ts.Boolean());
            }
        }

        if (op == BIT_AND || op == BIT_OR || op == BIT_XOR) {
            if (! ts.isLongOrLess(l)) {
                throw new SemanticException("The " + op +
                    " operator must have numeric or boolean operands, not type " +
                    l + ".", left.position());
            }
        
            if (! ts.isLongOrLess(r)) {
                throw new SemanticException("The " + op +
                    " operator must have numeric or boolean operands, not type " +
                    r + ".", right.position());
            }
        }
        
        if (op == SUB || op == MUL || op == DIV || op == MOD) {
            if (! l.isNumeric()) {
                throw new SemanticException("The " + op +
                    " operator must have numeric operands, not type " +
                    l + ".", left.position());
            }
        
            if (! r.isNumeric()) {
                throw new SemanticException("The " + op +
                    " operator must have numeric operands, not type " +
                    r + ".", right.position());
            }
        }
        
        if (op == SHL || op == SHR || op == USHR) {
            if (! ts.isLongOrLess(l)) {
                throw new SemanticException("The " + op +
                    " operator must have numeric operands, not type " +
                    l + ".", left.position());
            }
        
            if (! ts.isLongOrLess(r)) {
                throw new SemanticException("The " + op +
                    " operator must have numeric operands, not type " +
                    r + ".", right.position());
            }
        }
        
        if (op == SHL || op == SHR || op == USHR) {
            // For shift, only promote the left operand.
            Type t = ts.promote(l);
            Expr le = X10New_c.attemptCoercion(tc, left, t);
            return left(le).type(t);
        }
        
        Type t = ts.promote(l, r);
        Expr le = X10New_c.attemptCoercion(tc, left, t);
        Expr re = X10New_c.attemptCoercion(tc, right, t);
        return left(le).right(re).type(t);
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

