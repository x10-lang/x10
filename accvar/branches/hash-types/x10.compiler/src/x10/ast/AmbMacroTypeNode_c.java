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

import polyglot.ast.AmbExpr;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Disamb;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Id;
import polyglot.ast.Local;
import polyglot.ast.NamedVariable;
import polyglot.ast.Node;
import polyglot.ast.Prefix;
import polyglot.ast.TypeCheckTypeGoal;
import polyglot.ast.TypeNode;
import polyglot.ast.TypeNode_c;
import polyglot.frontend.Globals;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.LocalDef;
import polyglot.types.Name;
import polyglot.types.Named;
import polyglot.types.Ref;
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
import polyglot.visit.ContextVisitor;
import polyglot.visit.ExceptionChecker;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;
import x10.constraint.XRoot;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.errors.Errors;
import x10.extension.X10Del;
import x10.extension.X10Del_c;
import x10.types.MacroType;
import x10.types.ParameterType;
import x10.types.TypeDef_c;
import x10.types.X10ClassType;
import x10.types.X10Context;
import x10.types.X10ParsedClassType;
import x10.types.X10TypeEnv_c;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.visit.X10TypeChecker;
import x10.types.checker.VarChecker;


public class AmbMacroTypeNode_c extends TypeNode_c implements AmbMacroTypeNode, AddFlags {
   
    protected Prefix prefix;
    protected Id name;
    protected List<TypeNode> typeArgs;
    protected List<Expr> args;
    
    public AmbMacroTypeNode_c(Position pos, Prefix prefix, Id name, List<TypeNode> typeArgs, List<Expr> args) {
        super(pos);
        this.prefix = prefix;
        this.name = name;
        this.typeArgs = TypedList.copyAndCheck(typeArgs, TypeNode.class, true);
        this.args = TypedList.copyAndCheck(args, Expr.class, true);
    }
    
//    public void setResolver(Node parent, final TypeCheckPreparer v) {
////      if (parent instanceof ClassMember)
//          System.out.println("am parent=" + parent + " n=" + this + " this=" + ((X10Context) v.context()).thisVar());
//      super.setResolver(parent, v);
//    }

    public Prefix prefix() { return prefix;}
    public AmbMacroTypeNode prefix(Prefix prefix)  {
	if (prefix == this.prefix)  return this;
	AmbMacroTypeNode_c n = (AmbMacroTypeNode_c) copy();
	n.prefix = prefix;
	return n;
    } 
    public Id name() { return name;}
    public AmbMacroTypeNode name(Id name)  {
        if (name == this.name)  return this;
        AmbMacroTypeNode_c n = (AmbMacroTypeNode_c) copy();
        n.name = name;
        return n;
    } 
    
    public List<TypeNode> typeArgs() {
	    return this.typeArgs;
    }
    public AmbMacroTypeNode typeArgs(List<TypeNode> typeArgs) {
	    AmbMacroTypeNode_c n = (AmbMacroTypeNode_c) copy();
	    n.typeArgs = TypedList.copyAndCheck(typeArgs, TypeNode.class, true);
	    return n;
    }
    
    public List<Expr> args() {
        return this.args;
    }
    Flags flags;
    public void addFlags(Flags f) {
  	  this.flags = f;
    }
    public AmbMacroTypeNode args(List<Expr> args) {
	    AmbMacroTypeNode_c n = (AmbMacroTypeNode_c) copy();
	    n.args = TypedList.copyAndCheck(args, Expr.class, true);
	    return n;
    }
    
    public AmbMacroTypeNode reconstruct(Prefix prefix, Id name, List<TypeNode> typeArgs, List<Expr> args) {
        if (prefix == this.prefix && name == this.name && typeArgs == this.typeArgs && args == this.args) return this;
        AmbMacroTypeNode_c n = (AmbMacroTypeNode_c) copy();
        n.prefix = prefix;
        n.name = name;
        n.typeArgs = typeArgs;
        n.args = args;
        return n;
    }
    
    @Override
    public void setResolver(Node parent, final TypeCheckPreparer v) {
    	if (typeRef() instanceof LazyRef) {
    		LazyRef<Type> r = (LazyRef<Type>) typeRef();
    		TypeChecker tc = new X10TypeChecker(v.job(), v.typeSystem(), v.nodeFactory(), v.getMemo());
    		tc = (TypeChecker) tc.context(v.context().freeze());
    		r.setResolver(new TypeCheckTypeGoal(parent, this, tc, r, false));
    	}
    }
    public Node visitChildren(NodeVisitor v) {
        Prefix prefix = (Prefix) visitChild(this.prefix, v);
        Id name = (Id) visitChild(this.name, v);
	List<TypeNode> typeArgs = visitList(this.typeArgs, v);
        List<Expr> args = visitList(this.args, v);
        return reconstruct(prefix, name, typeArgs, args);
    }
    
    public String toString() {
    	return (prefix != null ? prefix.toString() + "." : "") + name.toString() + (typeArgs.isEmpty() ? "" : typeArgs) + (args.isEmpty() ? "" : "(" + CollectionUtil.listToString(args) + ")");
    }
    
    protected TypeNode disambiguateAnnotation(ContextVisitor tc) throws SemanticException {
	Position pos = position();
	
	X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
        X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
        X10Context c = (X10Context) tc.context();

        if (! c.inAnnotation())
            return null;
        
        SemanticException ex;
        
        // Look for a simply-named type.
        try {
            Disamb disamb = tc.nodeFactory().disamb();
	    Node n = disamb.disambiguate(this, tc, pos, prefix, name);

            if (n instanceof TypeNode) {
        	TypeNode tn = (TypeNode) n;
        	Ref<Type> tref = (Ref<Type>) tn.typeRef();
        	Type t = tref.get();
        	if (t instanceof X10ClassType) {
        	    X10ClassType ct = (X10ClassType) t;
        	    if (ct.flags().isInterface()) {
        		List<Type> typeArgs2 = new ArrayList<Type>();
        		for (TypeNode a : typeArgs) {
        		    typeArgs2.add(a.type());
        		}
        		if (ct.x10Def().typeParameters().size() == typeArgs2.size()) {
        		    if (typeArgs2.size() > 0) {
        		        ct = ct.typeArguments(typeArgs2);
        		    }
        		}
        		else {
        		    throw new SemanticException("Incorrect number of type arguments for annotation type " + ct + ".", position());
        		}
        		if (ct.x10Def().properties().size() == args().size()) {
        			if (args().size() > 0) {
        				ct = ct.propertyInitializers(args());
        			}
        		}
        		else {
        			throw new SemanticException("Incorrect number of property initializers for annotation type " + ct + ".", position());
        		}
        		tref.update(ct);
        		return tn;
        	    }
        	}

        	throw new SemanticException("Annotation type must be an interface.", position());
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

    protected TypeNode disambiguateBase(ContextVisitor tc) throws SemanticException {
	SemanticException ex;
	
	Position pos = position();
	
	X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
        X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
        X10Context c = (X10Context) tc.context();

        XRoot thisVar = c.thisVar();

        List<Type> typeArgs = new ArrayList<Type>(this.typeArgs.size());

        for (TypeNode tn2 : this.typeArgs) {
            typeArgs.add(tn2.type());
        }

        List<Type> argTypes = new ArrayList<Type>(this.args.size());

        for (Expr e : this.args) {
            argTypes.add(e.type());
        }
        
        // First look for a typedef.
	try {
            MacroType mt = null;
            
            if (prefix == null) {
                // Search the context.
                Named n = c.find(ts.TypeDefMatcher(null, name.id(), typeArgs, argTypes, c));
                if (n instanceof MacroType) {
                    mt = (MacroType) n;
                }
            }
            else {
                if (prefix instanceof TypeNode) {
                    TypeNode tn = (TypeNode) prefix;
                    Type container = tn.type();
                    mt = ts.findTypeDef(container, ts.TypeDefMatcher(container, name.id(), typeArgs, argTypes, c), c);
                }
            }
            
            if (mt != null) {
                LazyRef<Type> sym = (LazyRef<Type>) type;
                sym.update(mt);
                
                // Reset the resolver goal to one that can run when the ref is deserialized.
                Goal resolver = Globals.Scheduler().LookupGlobalType(sym);
                resolver.update(Goal.Status.SUCCESS);
                sym.setResolver(resolver);

                return nf.CanonicalTypeNode(pos, sym);
            }
        }
        catch (SemanticException e) {
        }
        
        // Otherwise, look for a simply-named type.
        try {
            Disamb disamb = tc.nodeFactory().disamb();
	    Node n = disamb.disambiguate(this, tc, pos, prefix, name);

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
      
   
    
    public Node typeCheckOverride(Node parent, ContextVisitor tc) throws SemanticException {
	X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
	X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
	
	AmbMacroTypeNode_c n = this;
	
	LazyRef<Type> sym = (LazyRef<Type>) n.type;
	if (sym == null)
	assert sym != null;

        final TypeChecker childtc = (TypeChecker) tc.enter(parent, n);
        
        Prefix prefix = (Prefix) visitChild(n.prefix, childtc);
        Id name = (Id) visitChild(n.name, childtc);

        n = (AmbMacroTypeNode_c) n.prefix(prefix);
        n = (AmbMacroTypeNode_c) n.name(name);

       
        List<TypeNode> typeArgs = visitList(n.typeArgs, childtc);
        List<Expr> args = visitList(n.args, childtc);
        n = (AmbMacroTypeNode_c) n.typeArgs(typeArgs);
        n = (AmbMacroTypeNode_c) n.args(args);
        
        TypeNode tn;
        
        try {
            tn = n.disambiguateAnnotation(childtc);
            if (tn != null)
        	return postprocess((CanonicalTypeNode) tn, n, childtc);
        }
        catch (SemanticException e) {
            // Mark the type resolved to prevent us from trying to resolve this again and again.
            sym.update(ts.unknownType(position()));
            throw e;
        }
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
            return postprocess(nf.CanonicalTypeNode(position(), sym), n, childtc);
        }
        
        if (t instanceof MacroType) {
            n = (AmbMacroTypeNode_c) n.typeArgs(Collections.EMPTY_LIST);
            n = (AmbMacroTypeNode_c) n.args(Collections.EMPTY_LIST);
        }
        
        if (! typeArgs.isEmpty()) {
            if (t instanceof X10ParsedClassType) {
        	X10ParsedClassType ct = (X10ParsedClassType) t;
        	int numParams = ct.x10Def().typeParameters().size();
        	if (numParams > 0) {
        	    if (numParams == typeArgs.size()) {
        		List<Type> typeArgsTypes = new ArrayList<Type>(numParams);
        		for (TypeNode tni : typeArgs) {
        		    typeArgsTypes.add(tni.type());
        		}
        		t = ct.typeArguments(typeArgsTypes);
        	        n = (AmbMacroTypeNode_c) n.typeArgs(Collections.EMPTY_LIST);
        	        typeArgs = Collections.EMPTY_LIST;
        	    }
        	    else {
        		throw new SemanticException("Number of type arguments (" + typeArgs.size() + ") for " + ct.fullName() + " is not the same as number of type parameters (" + numParams + ").", position());
        	    }
        	}
            }
        }
        if (n.flags != null) {
        	t = X10TypeMixin.processFlags(flags, t);
        	n.flags = null;
        }

        // Update the symbol with the base type so that if we try to get the type while checking the constraint, we don't get a cyclic
        // dependency error, but instead get a less precise type.
        sym.update(t);
        
        if (! n.typeArgs().isEmpty() || ! n.args().isEmpty())
            throw new SemanticException("Could not find or instantiate type \"" + n + "\".", position());
            
        CanonicalTypeNode result = nf.CanonicalTypeNode(position(), sym);
        // FIXME: [IP] HACK
        if (t instanceof MacroType) {
            TypeDef_c def = (TypeDef_c) ((MacroType) t).def();
            TypeNode defNode = def.astNode();
            if (defNode instanceof AmbDepTypeNode) {
                AmbDepTypeNode adtNode = (AmbDepTypeNode) defNode;
                adtNode = (AmbDepTypeNode) adtNode.typeCheck(childtc);
                DepParameterExpr depExpr = adtNode.constraint();
                final List<LocalDef> names = def.formalNames();
                final XVar selfBinding = XTerms.makeEQV("self");
                final Type selfType = X10TypeMixin.setSelfVar(t, selfBinding);
                x10.visit.Desugarer.Substitution<Expr> subst =
                    new x10.visit.Desugarer.Substitution<Expr>(Expr.class, args) {
                        protected Expr subst(Expr n) {
                            if (n instanceof Local) {
                                LocalDef d = ((Local) n).localInstance().def();
                                for (int i = 0; i < names.size(); i++) {
                                    if (names.get(i) == d)
                                        return by.get(i);
                                }
                            } else if (n instanceof AmbExpr) {
                                Name name = ((AmbExpr) n).name().id();
                                for (int i = 0; i < names.size(); i++) {
                                    if (names.get(i).name().equals(name))
                                        return by.get(i);
                                }
                            } else if (n instanceof X10Special_c) {
                                return ((X10Special_c) n).type(selfType);
                            }
                            try {
                                return (Expr) n.typeCheck(childtc);
                            } catch (SemanticException e) {
                                e.printStackTrace();
                                return n;
                            }
                        }
                    };
                depExpr = (DepParameterExpr) depExpr.visit(subst);
                result = ((X10CanonicalTypeNode)result).constraintExpr(depExpr);
            }
        }
        return postprocess(result, n, childtc);   
    }
    
    Node postprocess(CanonicalTypeNode result, AmbMacroTypeNode_c n, ContextVisitor childtc) throws SemanticException {
    	n = (AmbMacroTypeNode_c) X10Del_c.visitAnnotations(n, childtc);

    	result = (CanonicalTypeNode) ((X10Del) result.del()).annotations(((X10Del) n.del()).annotations());
    	result = (CanonicalTypeNode) ((X10Del) result.del()).setComment(((X10Del) n.del()).comment());
    	result = (CanonicalTypeNode) result.del().typeCheck(childtc);
    	 {
          	

          	VarChecker ac = (VarChecker) new VarChecker(childtc.job(), Globals.TS(), Globals.NF()).context(childtc.context());
          	result.visit(ac);
          	
          	if (ac.error != null) {
          		throw ac.error;
          	}

          } 
    	return result;
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
        if (typeArgs != null && typeArgs.isEmpty()) {
            w.write("[");
            String sep = "";
            for (TypeNode a : typeArgs) {
                w.write(sep);
                sep = ", ";
                tr.print(this, a, w);
            }
            w.write("]");
        }
        if (args != null && args.isEmpty()) {
            w.write("(");
            String sep = "";
            for (Expr a : args) {
                w.write(sep);
                sep = ", ";
                tr.print(this, a, w);
            }
            w.write(")");
        }
    }
}
