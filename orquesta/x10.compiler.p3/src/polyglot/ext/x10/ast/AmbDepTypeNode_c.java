/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Disamb;
import polyglot.ast.Expr;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.PackageNode;
import polyglot.ast.Prefix;
import polyglot.ast.TypeNode;
import polyglot.ast.TypeNode_c;
import polyglot.ext.x10.types.ConstrainedType;
import polyglot.ext.x10.types.MacroType;
import polyglot.ext.x10.types.TypeProperty;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.frontend.Globals;
import polyglot.frontend.Goal;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.LazyRef;
import polyglot.types.Named;
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
import x10.constraint.XSelf;


public class AmbDepTypeNode_c extends TypeNode_c implements AmbDepTypeNode {
   
    protected Prefix prefix;
    protected Id name;
    protected List<TypeNode> typeArgs;
    protected List<Expr> args;
    protected DepParameterExpr dep;
    
    public AmbDepTypeNode_c(Position pos, Prefix prefix, Id name, List<TypeNode> typeArgs, List<Expr> args, DepParameterExpr d) {
        super(pos);
        this.prefix = prefix;
        this.name = name;
        this.typeArgs = TypedList.copyAndCheck(typeArgs, TypeNode.class, true);
        this.args = TypedList.copyAndCheck(args, Expr.class, true);
        this.dep = d;
    }
    
    public Prefix prefix() { return prefix;}
    public AmbDepTypeNode prefix(Prefix prefix)  {
	if (prefix == this.prefix)  return this;
	AmbDepTypeNode_c n = (AmbDepTypeNode_c) copy();
	n.prefix = prefix;
	return n;
    } 
    public Id name() { return name;}
    public AmbDepTypeNode name(Id name)  {
        if (name == this.name)  return this;
        AmbDepTypeNode_c n = (AmbDepTypeNode_c) copy();
        n.name = name;
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
    
    public AmbDepTypeNode reconstruct(Prefix prefix, Id name, List<TypeNode> typeArgs, List<Expr> args, DepParameterExpr d) {
        if (prefix == this.prefix && name == this.name && typeArgs == this.typeArgs && args == this.args && d==dep) return this;
        AmbDepTypeNode_c n = (AmbDepTypeNode_c) copy();
        n.prefix = prefix;
        n.name = name;
        n.typeArgs = typeArgs;
        n.args = args;
        n.dep=d;
        return n;
    }
    
    public Context enterChildScope(Node child, Context c) {
    	if (child == this.dep) {
    	    TypeSystem ts = c.typeSystem();
    	    c = ((X10Context) c).pushDepType(type);
    	}
        Context cc = super.enterChildScope(child, c);
        return cc;
    }
    
    public Node visitChildren(NodeVisitor v) {
        Prefix prefix = (Prefix) visitChild(this.prefix, v);
        Id name = (Id) visitChild(this.name, v);
	List<TypeNode> typeArgs = visitList(this.typeArgs, v);
        List<Expr> args = visitList(this.args, v);
        DepParameterExpr dep = (DepParameterExpr) visitChild(this.dep, v);
        return reconstruct(prefix, name, typeArgs, args, dep);
    }
    
    public String toString() {
    	return (prefix != null ? prefix.toString() + "." : "") + name.toString() + (typeArgs.isEmpty() ? "" : typeArgs) + (args.isEmpty() ? "" : "(" + CollectionUtil.listToString(args) + ")") + (dep != null ? dep.toString() : "");
    }

    protected TypeNode disambiguateBase(TypeChecker ar) throws SemanticException {
	SemanticException ex;
	
	Position pos = position();
	TypeChecker tc = ar;
	
        X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
        X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();

        // First look for a typedef.
	try {
            X10ParsedClassType typeDefContainer = null;
            
            if (prefix == null) {
        	String dummyName = "package";
        	Named n = ar.context().find(dummyName);
        	if (n instanceof X10ParsedClassType) {
        	    typeDefContainer = (X10ParsedClassType) n;
        	}
            }
            else if (prefix instanceof PackageNode) {
        	PackageNode pn = (PackageNode) prefix;
        	String dummyName = "package";
        	String fullName = (pn != null ? Types.get(pn.package_()).fullName() + "." : "") + dummyName;
        	Named n = ts.systemResolver().find(fullName);
        	if (n instanceof X10ParsedClassType) {
        	    typeDefContainer = (X10ParsedClassType) n;
        	}
            }
            else if (prefix instanceof TypeNode) {
        	TypeNode tn = (TypeNode) prefix;
        	if (tn.type() instanceof X10ParsedClassType) {
        	    typeDefContainer = (X10ParsedClassType) tn.type();
        	}
            }
            else if (prefix instanceof Expr) {
        	throw new InternalCompilerError("non-static type members not implemented", pos);
            }

            if (typeDefContainer != null) {
                List<Type> typeArgs = new ArrayList<Type>(this.typeArgs.size());

                for (TypeNode tn2 : this.typeArgs) {
                    typeArgs.add(tn2.type());
                }

                List<Type> argTypes = new ArrayList<Type>(this.args.size());

                for (Expr e : this.args) {
                    argTypes.add(e.type());
                }

                MacroType mt = ts.findTypeDef(typeDefContainer, ts.TypeDefMatcher(typeDefContainer, name.id(), typeArgs, argTypes), tc.context().currentClassDef());
                LazyRef<Type> sym = (LazyRef<Type>) type;
                sym.update(mt);
                
                // Reset the resolver goal to one that can run when the ref is deserialized.
                Goal resolver = Globals.Scheduler().LookupGlobalType(sym);
                resolver.update(Goal.Status.SUCCESS);
                sym.setResolver(resolver);

                return nf.CanonicalTypeNode(pos, sym);            }
        }
        catch (SemanticException e) {
        }
        
        // Otherwise, look for a simply-named type.
        try {
            Disamb disamb = ar.nodeFactory().disamb();
	    Node n = disamb.disambiguate(this, ar, pos, prefix, name);

            if (n instanceof TypeNode) {
        	TypeNode tn = (TypeNode) n;
        	return tn;
            }

            ex = new SemanticException("Could not find type \"" +
                                       (prefix == null ? name.toString() : prefix.toString() + "." + name.toString()) +
                                       "\".", pos);
        }
        catch (SemanticException e) {
            ex = e;
        }

        throw ex;
    }
      
    public Node typeCheckOverride(Node parent, TypeChecker tc) throws SemanticException {
	X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
	NodeFactory nf = tc.nodeFactory();
	
	AmbDepTypeNode_c n = this;
	
	LazyRef<Type> sym = (LazyRef<Type>) n.type;

        TypeChecker childtc = (TypeChecker) tc.enter(parent, n);
        
        Prefix prefix = (Prefix) visitChild(n.prefix, childtc);
        Id name = (Id) visitChild(n.name, childtc);

        n = (AmbDepTypeNode_c) n.prefix(prefix);
        n = (AmbDepTypeNode_c) n.name(name);

        List<TypeNode> typeArgs = visitList(n.typeArgs, childtc);
        List<Expr> args = visitList(n.args, childtc);
        n = (AmbDepTypeNode_c) n.typeArgs(typeArgs);
        n = (AmbDepTypeNode_c) n.args(args);
        
        TypeNode tn;
        
        try {
            tn = n.disambiguateBase(tc);
        }
        catch (SemanticException e) {
            // Mark the type resolved to prevent us from trying to resolve this again and again.
            sym.update(ts.unknownType(position()));
            throw e;
        }
        
        Type t = tn.type();

        if (t instanceof UnknownType) {
            // Mark the type resolved to prevent us from trying to resolve this again and again.
            sym.update(ts.unknownType(position()));
            return n;
        }
        
        if (! typeArgs.isEmpty()) {
            if (t instanceof X10ParsedClassType) {
        	X10ParsedClassType ct = (X10ParsedClassType) t;
        	int numParams = ct.x10Def().typeParameters().size();
        	if (numParams > 0) {
        	    if (numParams == typeArgs.size()) {
        		List<Type> typeArgsTypes = new ArrayList<Type>();
        		for (TypeNode tni : typeArgs) {
        		    typeArgsTypes.add(tni.type());
        		}
        		t = ct.typeArguments(typeArgsTypes);
        	        n = (AmbDepTypeNode_c) n.typeArgs(Collections.EMPTY_LIST);
        	        typeArgs = Collections.EMPTY_LIST;
        	    }
        	    else {
        		throw new SemanticException("Number of type arguments (" + typeArgs.size() + ") for " + t + " is not the same as number of type parameters (" + numParams + ").", position());
        	    }
        	}
            }
        }

        // Update the symbol with the base type so that if we try to get the type while checking the constraint, we don't get a cyclic
        // dependency error, but instead get a less precise type.
        sym.update(t);
        
        DepParameterExpr dep = (DepParameterExpr) n.visitChild(n.dep, childtc);
        
        XConstraint c = dep != null ? Types.get(dep.xconstraint()) : null;
        
        if (t instanceof MacroType) {
            MacroType mt = (MacroType) t;
            
            // The type has already been instantiated on the arguments.
            if (mt.formals().size() == args.size() && mt.typeParameters().size() == typeArgs.size()) {
        	if (c != null)
        	    t = X10TypeMixin.xclause(t, c);

        	sym.update(t);
        	return nf.CanonicalTypeNode(position(), sym);
            }
            
            if (mt.formals().size() == 0 && mt.typeParameters().size() == 0) {
        	t = mt.definedType();
            }
            else {
        	throw new SemanticException("Could not instantiate type definition " + mt.fullName() + "; incorrect number of arguments.", position());
            }
        }
        // Desugar the type args list [T1,...,Tn] into {X1==T1,...,Xn==Tn}
        if (! typeArgs.isEmpty()) {
        	List<TypeProperty> typeProps = X10TypeMixin.typeProperties(t);
        	if (typeProps.size() != typeArgs.size()) {
        		throw new SemanticException("Number of type property initializers (" + typeArgs.size() + ") for " + t + " is not the same as number of type properties (" + typeProps.size() + ").", position());
        	}
        	if (c == null)
        	    c = new XConstraint_c();
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
        	    throw new SemanticException("Number of value property initializers (" + args.size() + ") for " + t + " is not the same as number of value properties (" + props.size() + ").", position());
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
        	t = X10TypeMixin.xclause(ct, (XConstraint) null);
        }
        
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
        if (prefix != null) {
	    tr.print(this, prefix, w);
	    w.write(".");
	}
        tr.print(this, name, w);
        if (dep != null) {
            tr.print(this, dep, w);
        }
    }
}
