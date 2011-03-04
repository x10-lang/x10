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
import java.util.List;

import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.TypeCheckTypeGoal;
import polyglot.ast.TypeNode;
import polyglot.ast.TypeNode_c;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.ExceptionChecker;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;
import x10.extension.X10Del;
import x10.extension.X10Del_c;
import x10.types.X10ClassType;
import x10.types.X10Context;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;
import x10.visit.X10TypeChecker;


public class AmbDepTypeNode_c extends TypeNode_c implements AmbDepTypeNode, AddFlags {
   
    protected TypeNode base;
    protected DepParameterExpr dep;
    boolean sharp;
    
    public AmbDepTypeNode_c(Position pos, TypeNode base, boolean sharp, DepParameterExpr d) {
        super(pos);
        assert base != null;
        assert d != null;
        this.base = base;
        this.dep = d;
        this.sharp = sharp;
    }
    
//    public void setResolver(Node parent, final TypeCheckPreparer v) {
////        if (parent instanceof ClassMember)
//            System.out.println("ad parent=" + parent + " n=" + this + " this=" + ((X10Context) v.context()).thisVar());
//        super.setResolver(parent, v);
//    }

    public TypeNode base() { return base;}
    public AmbDepTypeNode base(TypeNode base)  {
	if (base == this.base)  return this;
	AmbDepTypeNode_c n = (AmbDepTypeNode_c) copy();
	n.base = base;
	n.sharp=sharp;
	return n;
    } 
    
    public DepParameterExpr constraint() { return dep;}
    public AmbDepTypeNode constraint(DepParameterExpr expr) {
        if (expr == this.dep)  return this;
        AmbDepTypeNode_c n = (AmbDepTypeNode_c) copy();
        n.dep = expr;
        n.sharp=sharp;
        return n;
    }
    
    public AmbDepTypeNode reconstruct(TypeNode base, DepParameterExpr d) {
        if (base == this.base && d==dep) return this;
        AmbDepTypeNode_c n = (AmbDepTypeNode_c) copy();
        n.base = base;
        n.dep=d;
        n.sharp=sharp;
        return n;
    }
    
    public Context enterChildScope(Node child, Context c) {
    	if (child == this.dep) {
    	    TypeSystem ts = c.typeSystem();
    	    c = ((X10Context) c).pushDepType(base.typeRef());
    	}
        Context cc = super.enterChildScope(child, c);
        return cc;
    }
    
    public Node visitChildren(NodeVisitor v) {
        TypeNode base = (TypeNode) visitChild(this.base, v);
        DepParameterExpr dep = (DepParameterExpr) visitChild(this.dep, v);
        return reconstruct(base, dep);
    }
    
    public String toString() {
    	return (base != null ? base.toString() : "") + (dep != null ? dep.toString() : "");
    }
    
    public void setResolver(Node parent, final TypeCheckPreparer v) {
    	if (typeRef() instanceof LazyRef) {
    		LazyRef<Type> r = (LazyRef<Type>) typeRef();
    		TypeChecker tc = new X10TypeChecker(v.job(), v.typeSystem(), v.nodeFactory(), v.getMemo());
    		tc = (TypeChecker) tc.context(v.context().freeze());
    		r.setResolver(new TypeCheckTypeGoal(parent, this, tc, r, false));
    	}
    }
    public Node typeCheckOverride(Node parent, ContextVisitor tc) throws SemanticException {
	X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
	X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
	
	AmbDepTypeNode_c n = this;
	
	LazyRef<Type> sym = (LazyRef<Type>) n.type;
	assert sym != null;
	
        TypeChecker childtc = (TypeChecker) tc.enter(parent, n);
        
        TypeNode tn = (TypeNode) visitChild(n.base, childtc);
        Type t = tn.type();

        if (t instanceof UnknownType) {
            // Mark the type resolved to prevent us from trying to resolve this again and again.
            sym.update(ts.unknownType(position()));
            return postprocess(nf.CanonicalTypeNode(position(), sym), n, childtc);
        }
        // Add placeClause (if needed) to dep, provided that the target type is not a struct.
        if ((!sharp) && ! X10TypeMixin.isX10Struct(t) && ! ts.isVoid(t)) {
        	Position pos = position();
        	if (n.dep == null) {
        		Expr placeClause = nf.Call(pos, nf.Self(pos), nf.Id(pos, "at"), nf.AmbHereThis(pos));
        		n.dep = nf.DepParameterExpr(pos, Collections.singletonList(placeClause));
        	} else {
        		n.dep.addPlaceClauseIfNeeded();
        	}
        }
        DepParameterExpr dep = (DepParameterExpr) n.visitChild(n.dep, childtc);
        
        CConstraint c = Types.get(dep.valueConstraint());
        // Ignore the constraint if it is valid (e.g. dep may have an empty list of constraints).
        if (! c.valid())
        	t = X10TypeMixin.xclause(t, c);
        if (flags != null) {
        	t = X10TypeMixin.processFlags(flags, t);
        	flags = null;
        }

        sym.update(t);

        CanonicalTypeNode result = nf.X10CanonicalTypeNode(position(), sym, dep);
        return postprocess(result, n, childtc);   
    }
    
    static TypeNode postprocess(CanonicalTypeNode result, TypeNode n, ContextVisitor childtc) throws SemanticException {
        n = (TypeNode) X10Del_c.visitAnnotations(n, childtc);

        result = (CanonicalTypeNode) ((X10Del) result.del()).annotations(((X10Del) n.del()).annotations());
        result = (CanonicalTypeNode) ((X10Del) result.del()).setComment(((X10Del) n.del()).comment());

        LazyRef<Type> sym = (LazyRef<Type>) result.typeRef();
        sym.update(fixInnerParams(sym.get()));

        return (TypeNode) result.del().typeCheck(childtc);
    }
    
    // Fix inner classes by adding type arguments from their enclosing classes.
    // That is, convert C[T].D into C[T].D[T].
    static Type fixInnerParams(Type t) {
        Type b = X10TypeMixin.baseType(t);
        if (b instanceof X10ClassType) {
            X10ClassType ct = (X10ClassType) b;
            List<Type> params = new ArrayList<Type>();
            
            if (ct.isMember() && ! ct.flags().isStatic()) {
                X10ClassType outer = (X10ClassType) ct.outer();
                while (outer != null) {
                    params.addAll(outer.typeArguments());
                    if (outer.isMember())
                        outer = (X10ClassType) outer.outer();
                    else
                        outer = null;
                }
            }
            
            if (params.size() > 0) {
                params.addAll(0, ct.typeArguments());
                ct = ct.typeArguments(params);
                t = X10TypeMixin.baseType(ct);
            }
        }
        
        return t;
    }

    public Node exceptionCheck(ExceptionChecker ec) throws SemanticException {
        throw new InternalCompilerError(position(),
            "Cannot exception check ambiguous node " + this + ".");
    }
    
    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        tr.print(this, base, w);
        tr.print(this, dep, w);
    }
    Flags flags;
    public void addFlags(Flags f) {
  	  this.flags = f;
    }
}
