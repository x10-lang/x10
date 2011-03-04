/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by igor on Jan 24, 2006
 */
package x10.ast;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.main.Report;
import polyglot.types.Context;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.types.X10Context;
import x10.types.X10MethodDef;
import x10.types.X10TypeSystem;

/**
 * Captures the commonality of foreach and ateach loops in X10.
 * @author Igor Peshansky
 */
public abstract class X10ClockedLoop_c extends X10Loop_c implements Clocked {

	protected List clocks;

	/**
	 * @param pos
	 */
	public X10ClockedLoop_c(Position pos) {
		super(pos);
	}

	/**
	 * @param pos
	 * @param formal
	 * @param domain
	 * @param clocks
	 * @param body
	 */
	public X10ClockedLoop_c(Position pos, Formal formal, Expr domain,
							List clocks, Stmt body)
	{
		super(pos, formal, domain, body);
		this.clocks = TypedList.copyAndCheck(clocks, Expr.class, true);
	}

	/** Clocks */
	public List clocks() {
		return Collections.unmodifiableList(this.clocks);
	}

	/** Set clocks */
	public Clocked clocks(List clocks) {
		X10ClockedLoop_c n = (X10ClockedLoop_c) copy();
		n.clocks = TypedList.copyAndCheck(clocks, Expr.class, true);
		return n;
	}

	public Node visitChildren(NodeVisitor v) {
		Formal formal = (Formal) visitChild(this.formal, v);
		Expr domain = (Expr) visitChild(this.domain, v);
		List clocks = visitList(this.clocks, v);
		Stmt body = (Stmt) visitChild(this.body, v);
		return ((Clocked) reconstruct(formal, domain, body)).clocks(clocks);
	}

	public Context enterChildScope(Node child, Context c) {
	    X10Context xc = (X10Context) c;
	    if (child == this.body) {
	        X10TypeSystem ts = (X10TypeSystem) c.typeSystem();
	        X10MethodDef asyncInstance = (X10MethodDef) ts.asyncCodeInstance(c.inStaticContext());
	        if (xc.currentCode() instanceof X10MethodDef) {
	            X10MethodDef outer = (X10MethodDef) c.currentCode();
	            List<Ref<? extends Type>> capturedTypes = outer.typeParameters();
	            if (!capturedTypes.isEmpty()) {
	                asyncInstance = ((X10MethodDef) asyncInstance.copy());
	                asyncInstance.setTypeParameters(capturedTypes);
	            }
	        }
	        xc = (X10Context) xc.pushCode(asyncInstance);
	    }
	    return xc;
	}

	
	public Node typeCheck(ContextVisitor tc) throws SemanticException {
		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
	        for (Expr clock : (List<Expr>) clocks) {
	            if (! ts.isImplicitCastValid(clock.type(), ts.Clock(), tc.context())) {
	        	throw new SemanticException("Clocked loop may only be clocked on a clock.", clock.position());
	            }
	        }
	        
		X10Context c = (X10Context) tc.context();
		if (c.inSequentialCode()) {
			throw new SemanticException("foreach/ateach may not be invoked in sequential code.", position());
		}

		return super.typeCheck(tc);
	}
}
