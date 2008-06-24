/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.Collections;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ast.TypeNode_c;
import polyglot.ext.x10.types.ConstrainedType;
import polyglot.ext.x10.types.ConstrainedType_c;
import polyglot.ext.x10.types.MacroType;
import polyglot.ext.x10.types.MacroType_c;
import polyglot.ext.x10.types.TypeProperty;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.LazyRef;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.ExceptionChecker;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XLit;
import x10.constraint.XPromise;
import x10.constraint.XSelf;
import x10.constraint.XTerm;
import x10.constraint.XTerms;


public class AmbDepTypeNode_c extends TypeNode_c implements AmbDepTypeNode {
   
    protected TypeNode base;
    protected List<TypeNode> typeArgs;
    protected List<Expr> args;
    protected DepParameterExpr dep;
    
    public AmbDepTypeNode_c(Position pos, TypeNode b, List<TypeNode> typeArgs, List<Expr> args, DepParameterExpr d) {
        super(pos);
        this.base = b;
        this.typeArgs = TypedList.copyAndCheck(typeArgs, TypeNode.class, true);
        this.args = TypedList.copyAndCheck(args, Expr.class, true);
        this.dep = d;
    }
    
    public TypeNode base() { return base;}
    public AmbDepTypeNode base(TypeNode base)  {
        if (base == this.base)  return this;
        AmbDepTypeNode_c n = (AmbDepTypeNode_c) copy();
        n.base = base;
        return n;
    } 
    
    public List<TypeNode> typeArgs() {
	    return this.typeArgs;
    }
    public AmbDepTypeNode typeArgs(List<TypeNode> typeArgs) {
	    AmbDepTypeNode_c n = (AmbDepTypeNode_c) copy();
	    n.typeArgs = TypedList.copyAndCheck(typeArgs, TypeNode.class, true);
	    return n;
    }
    
    public List<Expr> args() {
        return this.args;
    }
    public AmbDepTypeNode args(List<Expr> args) {
	    AmbDepTypeNode_c n = (AmbDepTypeNode_c) copy();
	    n.args = TypedList.copyAndCheck(args, Expr.class, true);
	    return n;
    }
    
    public DepParameterExpr constraint() { return dep;}
    public AmbDepTypeNode constraint( DepParameterExpr expr) {
        if (expr == this.dep)  return this;
        AmbDepTypeNode_c n = (AmbDepTypeNode_c) copy();
        n.dep = expr;
       
        return n;
        }  
    
    public AmbDepTypeNode reconstruct(TypeNode tn, List<TypeNode> typeArgs, List<Expr> args, DepParameterExpr d) {
        if (tn == base && typeArgs == this.typeArgs && args == this.args && d==dep) return this;
        AmbDepTypeNode_c n = (AmbDepTypeNode_c) copy();
        n.base=tn;
        n.typeArgs = typeArgs;
        n.args = args;
        n.dep=d;
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
        TypeNode tn = (TypeNode) visitChild(this.base, v);
	List<TypeNode> typeArgs = visitList(this.typeArgs, v);
        List<Expr> args = visitList(this.args, v);
        DepParameterExpr dep = (DepParameterExpr) visitChild(this.dep, v);
        return reconstruct(tn, typeArgs, args, dep);
    }
    
    public String toString() {
    	return base.toString() + (typeArgs.isEmpty() ? "" : typeArgs) + (args.isEmpty() ? "" : "(" + CollectionUtil.listToString(args) + ")") + (dep != null ? "{" + dep + "}" : "");
    }
      
    public Node typeCheckOverride(Node parent, TypeChecker tc) throws SemanticException {
        // Override to disambiguate the base type and rebuild the node before type checking the dep clause.

        X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
        NodeFactory nf = tc.nodeFactory();

        AmbDepTypeNode_c n = this;

        TypeChecker childtc = (TypeChecker) tc.enter(parent, n);
        
        TypeNode tn = (TypeNode) n.visitChild(n.base, childtc);
        n = (AmbDepTypeNode_c) n.base(tn);

        LazyRef<Type> sym = (LazyRef<Type>) n.type;
        
        if (tn.type() instanceof UnknownType) {
            // Mark the type resolved to prevent us from trying to resolve this again and again.
            sym.update(ts.unknownType(position()));
            return n;
        }
        
        Type t = tn.type();

        // Update the symbol with the base type so that if we try to get the type while checking the constraint, we don't get a cyclic
        // dependency error, but instead get a less precise type.
        sym.update(t);
        
	List<TypeNode> typeArgs = visitList(this.typeArgs, childtc);
        List<Expr> args = visitList(this.args, childtc);

        DepParameterExpr dep = (DepParameterExpr) n.visitChild(n.dep, childtc);
        
        XConstraint c = null;
        if (dep != null)
        	c = dep.xconstraint().get();

        // Desugar the type args list [T1,...,Tn] into {X1==T1,...,Xn==Tn}
        if (! typeArgs.isEmpty()) {
        	List<TypeProperty> typeProps = X10TypeMixin.typeProperties(t);
        	if (typeProps.size() != typeArgs.size()) {
        		throw new SemanticException("Number of type property initializers is not the same as number of type properties.", position());
        	}
        	if (c == null) c = new XConstraint_c();
        	for (int i = 0; i < typeProps.size(); i++) {
        		TypeProperty pi = typeProps.get(i);
        		TypeNode tni = typeArgs.get(i);
			Type ti = tni.type();
        		try {
				c.addBinding(pi.asVar(), ts.xtypeTranslator().trans(ti));
			}
			catch (XFailure e) {
				throw new SemanticException("Cannot bind type property " + pi.name() + " to " + ti + "; " + e.getMessage(), tni.position());
			}
        	}
        }

        // Desugar the value args list (e1,...,en) into {x1==e1,...,xn==en}
        if (! args.isEmpty()) {
        	List<FieldInstance> props = X10TypeMixin.properties(t);
        	if (props.size() != args.size()) {
        		throw new SemanticException("Number of value property initializers is not the same as number of value properties.", position());
        	}
        	if (c == null) c = new XConstraint_c();
        	for (int i = 0; i < props.size(); i++) {
        		FieldInstance pi = props.get(i);
        		Expr ei = args.get(i);
        		try {
				c.addBinding(ts.xtypeTranslator().trans(XSelf.Self, pi), ts.xtypeTranslator().trans(ei));
			}
			catch (XFailure e) {
				throw new SemanticException("Cannot bind value property " + pi.name() + " to " + ei + "; " + e.getMessage(), ei.position());
			}
        	}
        }

        if (t instanceof ConstrainedType) {
        	ConstrainedType ct = (ConstrainedType) t;
        	XConstraint ctc = ct.constraint().get();
        	if (c == null) {
        		c = ctc;
        	}
        	else {
        		try {
        			c.addIn(ctc);
        		}
        		catch (XFailure e) {
        			throw new SemanticException(e.getMessage(), position());
        		}
        	}
        	t = X10TypeMixin.xclause(ct, null);
        }
        
//        if (ts.typeEquals(t, ts.Box())) {
//        	if (c != null) {
//        		List<TypeProperty> typeProps = X10TypeMixin.typeProperties(t);
//        		if (typeProps.size() == 1) {
//        			TypeProperty pi = typeProps.get(0);
//        			try {
//        				XPromise p = c.lookup(pi.asVar());
//        				if (p != null && p.term() instanceof XLit && ((XLit) p.term()).val() instanceof Type) {
//        					Type arg = (Type) ((XLit) p.term()).val();
//        					if (ts.descendsFrom(X10TypeMixin.xclause(arg, null), ts.Ref())) {
//        						// type Box[T]{T <: Ref} = T;
//        						// We could just return T, but error reporting will change Box[T] to T, which might
//        						// be confusing.
//        						MacroType mt = new MacroType_c(ts, position(), Types.ref(ts.BoxRefTypeDef()));
//        						mt = (MacroType) mt.typeParams(Collections.singletonList(arg));
//        						mt = mt.definedType(arg);
//        						mt = (MacroType) mt.whereClause(new XConstraint_c());
//        						XConstraint c2 = Types.get(dep.xconstraint());
//							t = X10TypeMixin.xclause(mt, c2);
//        					}
//        					else {
//        						// Really box!
//        						t = ts.boxOf(Types.ref(arg));
//        					}
//        					sym.update(t);
//        					return nf.CanonicalTypeNode(position(), t);
//        				}
//        			}
//        			catch (XFailure e) {
//        			}
//        		}
//        	}
//        }

        if (c != null)
        	t = X10TypeMixin.xclause(t, c);

        sym.update(t);

        return nf.CanonicalTypeNode(position(), sym);
    }
    
    public Node exceptionCheck(ExceptionChecker ec) throws SemanticException {
        throw new InternalCompilerError(position(),
            "Cannot exception check ambiguous node " + this + ".");
    }
    
    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        tr.print(this, base, w);
        if (dep != null) {
            tr.print(this, dep, w);
        }
    }
}
