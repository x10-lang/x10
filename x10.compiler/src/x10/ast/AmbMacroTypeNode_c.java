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
import polyglot.ast.AmbTypeNode_c;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Disamb;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Id;
import polyglot.ast.Local;
import polyglot.ast.NamedVariable;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Prefix;
import polyglot.ast.TypeNode;
import polyglot.frontend.Globals;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.LocalDef;
import polyglot.types.Name;
import polyglot.types.Named;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
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
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.errors.Errors;
import x10.errors.Warnings;
import x10.extension.X10Del;
import x10.extension.X10Del_c;
import x10.types.MacroType;
import x10.types.ParameterType;
import x10.types.TypeDef_c;
import x10.types.X10ClassType;
import polyglot.types.Context;
import x10.types.X10Def;
import x10.types.X10ParsedClassType;
import polyglot.types.TypeSystem;

import x10.types.X10Use;
import x10.visit.X10TypeChecker;
import x10.visit.ChangePositionVisitor;
import x10.types.checker.VarChecker;


public class AmbMacroTypeNode_c extends X10AmbTypeNode_c implements AmbMacroTypeNode, AddFlags {
   
    protected List<TypeNode> typeArgs;
    protected List<Expr> args;
    
    public AmbMacroTypeNode_c(Position pos, Prefix prefix, Id name, List<TypeNode> typeArgs, List<Expr> args) {
        super(pos, prefix, name);
        this.typeArgs = TypedList.copyAndCheck(typeArgs, TypeNode.class, true);
        this.args = TypedList.copyAndCheck(args, Expr.class, true);
    }
    
    public AmbMacroTypeNode prefix(Prefix prefix) {
        if (prefix == this.prefix) return this;
        return (AmbMacroTypeNode) super.prefix(prefix);
    }
    
    public AmbMacroTypeNode name(Id name) {
        if (name == this.name) return this;
        return (AmbMacroTypeNode) super.name(name);
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
    	if (typeRef() instanceof LazyRef<?>) {
    		LazyRef<Type> r = (LazyRef<Type>) typeRef();
    		TypeChecker tc = new X10TypeChecker(v.job(), v.typeSystem(), v.nodeFactory(), v.getMemo());
    		tc = (TypeChecker) tc.context(v.context().freeze());
    		r.setResolver(new X10TypeCheckTypeGoal(parent, this, tc, r));
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
    	return super.toString() + (typeArgs.isEmpty() ? "" : typeArgs) + (args.isEmpty() ? "" : "(" + CollectionUtil.listToString(args) + ")");
    }
    
    protected TypeNode disambiguateAnnotation(ContextVisitor tc) throws SemanticException {
        Position pos = position();
        
        TypeSystem ts = (TypeSystem) tc.typeSystem();
        NodeFactory nf = (NodeFactory) tc.nodeFactory();
        Context c = (Context) tc.context();
        
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

            ex = new SemanticException("Could not find type \"" +(prefix == null ? name.toString() : prefix.toString() + "." + name.toString()) + "\".", pos);
        }
        catch (SemanticException e) {
            ex = e;
        }

        throw ex;
    }

    protected TypeNode disambiguateBase(ContextVisitor tc) throws SemanticException {
        SemanticException ex;
        
        Position pos = position();
        
        // First look for a typedef.
        try {
            MacroType mt = null;
            
            TypeSystem ts = (TypeSystem) tc.typeSystem();
            NodeFactory nf = (NodeFactory) tc.nodeFactory();
            Context c = (Context) tc.context();

            List<Type> typeArgs = new ArrayList<Type>(this.typeArgs.size());

            for (TypeNode tn2 : this.typeArgs) {
                typeArgs.add(tn2.type());
            }

            List<Type> argTypes = new ArrayList<Type>(this.args.size());

            for (Expr e : this.args) {
                argTypes.add(e.type());
            }
            
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
                Warnings.wasGuardChecked(tc,mt,this);

                LazyRef<Type> sym = (LazyRef<Type>) type;
                sym.update(mt);
                
                // Reset the resolver goal to one that can run when the ref is deserialized.
                Goal resolver = tc.job().extensionInfo().scheduler().LookupGlobalType(sym);
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

            ex = new SemanticException("Could not find type \"" + (prefix == null ? name.toString() : prefix.toString() + "." + name.toString()) + "\".", pos);
        }
        catch (SemanticException e) {
            ex = e;
        }

        throw ex;
    }
    
    public Node typeCheckOverride(Node parent, ContextVisitor tc) {
        TypeSystem ts =  tc.typeSystem();
        NodeFactory nf = (NodeFactory) tc.nodeFactory();
        
        AmbMacroTypeNode_c n = this;
        
        LazyRef<Type> sym = (LazyRef<Type>) n.type;
        assert sym != null;
        
        final TypeChecker childtc = (TypeChecker) tc.enter(parent, n);
        
        Prefix prefix = (Prefix) n.visitChild(n.prefix, childtc);
        Id name = (Id) n.visitChild(n.name, childtc);

        n = (AmbMacroTypeNode_c) n.prefix(prefix);
        n = (AmbMacroTypeNode_c) n.name(name);
        
        List<TypeNode> typeArgs = n.visitList(n.typeArgs, childtc);
        List<Expr> args = n.visitList(n.args, childtc);
        n = (AmbMacroTypeNode_c) n.typeArgs(typeArgs);
        n = (AmbMacroTypeNode_c) n.args(args);
        
        TypeNode tn;
        
        try {
            tn = n.disambiguateAnnotation(childtc);
            if (tn != null)
                return postprocess((X10CanonicalTypeNode) tn, n, childtc);
        }
        catch (SemanticException e) {
            // Mark the type resolved to prevent us from trying to resolve this again and again.
            X10ClassType ut = ts.createFakeClass(QName.make(null, name().id()), e);
            ut.def().position(n.position());
            sym.update(ut);
        }
        try {
            tn = n.disambiguateBase(tc);
        }
        catch (SemanticException e) {
            // Mark the type resolved to prevent us from trying to resolve this again and again.
            X10ClassType ut = ts.createFakeClass(QName.make(null, name().id()), e);
            ut.def().position(n.position());
            sym.update(ut);
            tn = n;
        }
        
        Type t = tn.type();

        if (ts.isUnknown(t)) {
            // Mark the type resolved to prevent us from trying to resolve this again and again.
            sym.update(ts.unknownType(n.position()));
            return postprocess(nf.CanonicalTypeNode(n.position(), sym), n, childtc);
        }
        
        if (t instanceof MacroType) {
            // FIXME: [IP] We are losing the arguments here!
            n = (AmbMacroTypeNode_c) n.typeArgs(Collections.<TypeNode>emptyList());
            n = (AmbMacroTypeNode_c) n.args(Collections.<Expr>emptyList());
        }
        
        if (t instanceof X10Use<?>) Warnings.checkErrorAndGuard(tc,((X10Use<?>) t), n);

        if (! typeArgs.isEmpty()) {
            if (t instanceof X10ParsedClassType) {
        	X10ParsedClassType ct = (X10ParsedClassType) t;
        	int numParams = ct.x10Def().typeParameters().size();
        	if (numParams > 0) {
        	    if (numParams != typeArgs.size()) {
        	        Errors.issue(tc.job(),
        	                new SemanticException("Number of type arguments (" + typeArgs.size() + ") for " + ct.fullName() + " is not the same as number of type parameters (" + numParams + ").", n.position()));
        	        typeArgs = new ArrayList<TypeNode>(typeArgs);
        	        while (numParams < typeArgs.size()) {
        	            typeArgs.remove(typeArgs.size()-1);
        	        }
        	        while (numParams > typeArgs.size()) {
        	            typeArgs.add(nf.CanonicalTypeNode(Position.COMPILER_GENERATED, ts.Any()));
        	        }
        	    }
        	    List<Type> typeArgsTypes = new ArrayList<Type>(numParams);
        	    for (TypeNode tni : typeArgs) {
        	        typeArgsTypes.add(tni.type());
        	    }
        	    t = ct.typeArguments(typeArgsTypes);
        	    n = (AmbMacroTypeNode_c) n.typeArgs(Collections.<TypeNode>emptyList());
        	    typeArgs = Collections.<TypeNode>emptyList();
        	}
            }
        }
        if (n.flags != null) {
        	t = Types.processFlags(n.flags, t);
        	n.flags = null;
        }

        // Update the symbol with the base type so that if we try to get the type while checking the constraint, we don't get a cyclic
        // dependency error, but instead get a less precise type.
        sym.update(t);
        
        /*if (! n.typeArgs().isEmpty() || ! n.args().isEmpty())
            throw new SemanticException("Could not find or instantiate type \"" + n + "\".", position());
         */   
        X10CanonicalTypeNode result = nf.CanonicalTypeNode(n.position(), sym);
        return postprocess(result, n, childtc);   
    }
    
    public static Node postprocess(X10CanonicalTypeNode result, AmbMacroTypeNode_c n, ContextVisitor childtc) {
    	n = (AmbMacroTypeNode_c) X10Del_c.visitAnnotations(n, childtc);

    	result = (X10CanonicalTypeNode) ((X10Del) result.del()).annotations(((X10Del) n.del()).annotations());
    	result = (X10CanonicalTypeNode) ((X10Del) result.del()).setComment(((X10Del) n.del()).comment());
    	result = (X10CanonicalTypeNode) result.typeCheck(childtc);
    	{
    	    VarChecker ac = (VarChecker) new VarChecker(childtc.job()).context(childtc.context());
    	    try {
    	        result.visit(ac);
    	    } catch (InternalCompilerError e) {
    	        Errors.issue(childtc.job(),
    	                new SemanticException(e.getMessage(), e.position()), result);
    	    }
    	    
    	    if (ac.error != null) {
    	        Errors.issue(childtc.job(), ac.error, result);
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
