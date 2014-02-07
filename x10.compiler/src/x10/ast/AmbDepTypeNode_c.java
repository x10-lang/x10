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

import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ast.TypeNode_c;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.ExceptionChecker;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;
import x10.errors.Errors;
import x10.extension.X10Del;
import x10.extension.X10Del_c;
import x10.types.X10ClassType;
import polyglot.types.Context;
import polyglot.types.TypeSystem;
import x10.types.checker.VarChecker;
import x10.types.constraints.CConstraint;
import x10.types.constraints.TypeConstraint;
import x10.visit.X10TypeChecker;


public class AmbDepTypeNode_c extends TypeNode_c implements AmbDepTypeNode, AddFlags {
   
    protected TypeNode base;
    protected DepParameterExpr dep;
    
    public AmbDepTypeNode_c(Position pos, TypeNode base, DepParameterExpr d) {
        super(pos);
        assert base != null;
        assert d != null;
        this.base = base;
        this.dep = d;
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
	return n;
    } 
    
    public DepParameterExpr constraint() { return dep;}
    public AmbDepTypeNode constraint(DepParameterExpr expr) {
        if (expr == this.dep)  return this;
        AmbDepTypeNode_c n = (AmbDepTypeNode_c) copy();
        n.dep = expr;
        return n;
    }
    
    public AmbDepTypeNode reconstruct(TypeNode base, DepParameterExpr d) {
        if (base == this.base && d==dep) return this;
        AmbDepTypeNode_c n = (AmbDepTypeNode_c) copy();
        n.base = base;
        n.dep=d;
        return n;
    }
    
    public Context enterChildScope(Node child, Context c) {
    	if (child == this.dep) {
    	    TypeSystem ts = c.typeSystem();
    	    c = ((Context) c).pushDepType(base.typeRef());
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
    
    public Node typeCheckOverride(Node parent, ContextVisitor tc) {
        TypeSystem ts =  tc.typeSystem();
        NodeFactory nf = (NodeFactory) tc.nodeFactory();

        LazyRef<Type> sym = (LazyRef<Type>) this.type;
        assert sym != null;
	
        TypeChecker childtc = (TypeChecker) tc.enter(parent, this);
        
        TypeNode tn = (TypeNode) visitChild(base, childtc);
        Type t = tn.type();

        if (ts.isUnknown(t)) {
            // Mark the type resolved to prevent us from trying to resolve this again and again.
            assert (false);
            sym.update(ts.unknownType(position()));
            X10CanonicalTypeNode result = postprocess(nf.CanonicalTypeNode(position(), sym), this, childtc);
            return result.typeCheck(childtc);
        }
        
        DepParameterExpr constr = (DepParameterExpr) visitChild(dep, childtc);
        
        VarChecker ac = (VarChecker) new VarChecker(childtc.job()).context(childtc.context());
        try {
            constr.visit(ac);
        } catch (InternalCompilerError e) {
            Errors.issue(childtc.job(),
                    new Errors.GeneralError(e.getMessage(), e.position()), constr);
        }
        if (ac.error != null) {
            Errors.issue(childtc.job(), ac.error, constr);
        }

        CConstraint c = Types.get(constr.valueConstraint());
        t = Types.xclause(t, c);
        if (flags != null) {
        	t = Types.processFlags(flags, t);
        	flags = null;
        }

        sym.update(t);

        X10CanonicalTypeNode result = nf.CanonicalTypeNode(position(), sym);
        result = postprocess(result, this, childtc);
        return (TypeNode) result.typeCheck(childtc);
    }
    
    static X10CanonicalTypeNode postprocess(X10CanonicalTypeNode result, TypeNode n, ContextVisitor childtc) {
        n = (TypeNode) X10Del_c.visitAnnotations(n, childtc);

        result = (X10CanonicalTypeNode) ((X10Del) result.del()).annotations(((X10Del) n.del()).annotations());
        result = (X10CanonicalTypeNode) ((X10Del) result.del()).setComment(((X10Del) n.del()).comment());

        return result;
    }
    
    public Node exceptionCheck(ExceptionChecker ec) {
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
