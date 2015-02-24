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
import java.util.Map;

import polyglot.ast.AmbExpr;
import polyglot.ast.AmbTypeNode_c;
import polyglot.ast.Binary;
import polyglot.ast.Binary.Operator;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Disamb;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Id;
import polyglot.ast.Local;
import polyglot.ast.NamedVariable;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.PackageNode;
import polyglot.ast.Prefix;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.frontend.Globals;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.LocalDef;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.Resolver;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.util.CodedErrorInfo;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.ContextVisitor;
import polyglot.visit.ExceptionChecker;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;
import x10.constraint.XTerm;
import x10.types.constraints.ConstraintManager;
import x10.constraint.XVar;
import x10.errors.Errors;
import x10.errors.Errors.IllegalConstraint;
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
import polyglot.types.TypeSystem_c;
import polyglot.types.NoClassException;

import x10.types.X10Use;
import x10.types.TypeDefMatcher;
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

            String typeName = (prefix == null ? name.toString() : prefix.toString() + "." + name.toString());
            ex = new SemanticException("Could not find type \"" + typeName + "\".", pos);
            Map<String, Object> map = CollectionFactory.newHashMap();
            map.put(CodedErrorInfo.ERROR_CODE_KEY, CodedErrorInfo.ERROR_CODE_TYPE_NOT_FOUND);
            map.put("TYPE", typeName);
            ex.setAttributes(map);
        }
        catch (SemanticException e) {
            ex = e;
        }

        throw ex;
    }

    protected TypeNode disambiguateBase(ContextVisitor tc) throws SemanticException {
        SemanticException ex = null;
        
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

            // FIXME: move this code into X10Disamb_c
            if (prefix == null) {
                // Search the context.
                TypeDefMatcher matcher = new TypeDefMatcher(null, name.id(), typeArgs, argTypes, c);
                List<Type> tl = c.find(matcher);
                for (Type n : tl) {
                    if (n instanceof MacroType) {
                        mt = (MacroType) n;
                        break;
                    }
                }
            }
            else if (prefix instanceof PackageNode) {
                PackageNode pn = (PackageNode) prefix;
                Resolver pc = ts.packageContextResolver(pn.package_().get());
                TypeDefMatcher matcher = new TypeDefMatcher(null, name.id(), typeArgs, argTypes, c);
                List<Type> tl = pc.find(matcher);
                if (tl != null) {
                    for (Type n : tl) {
                        if (n instanceof MacroType) {
                            mt = (MacroType) n;
                            break;
                        }
                    }
                }
            }
            else if (prefix instanceof TypeNode) {
                TypeNode tn = (TypeNode) prefix;
                Type container = tn.type();
                mt = ts.findTypeDef(container, name.id(), typeArgs, argTypes, c);
            }
            
            if (mt != null) {
                LazyRef<Type> sym = (LazyRef<Type>) type;
                sym.update(mt);
                
                // Reset the resolver goal to one that can run when the ref is deserialized.
                Goal resolver = tc.job().extensionInfo().scheduler().LookupGlobalType(sym);
                resolver.update(Goal.Status.SUCCESS);
                sym.setResolver(resolver);

                return nf.CanonicalTypeNode(pos, sym);
            }
        } catch (SemanticException e) {
            // These can happen normally:
            // polyglot.types.SemanticException: No type defintion found in x10.util.Map for x10.util.Map.Entry[K, V].
            // NoClassException
            // But in other cases we want to report the error, e.g.,
            //class TestMemberTypeResolution {
            //	static type Foo(i:Int{self!=0}) = Int;
            //	var x:Foo(0); // ERR: todo: improve error: Semantic Error: Could not find type "Foo".
            //}
            //  throw the error: (but we ignore it)
            // x10.errors.Errors$InvalidParameter:    Invalid Parameter.
            //     Expected type: x10.lang.Int{self!=0}
            //     Found type: x10.lang.Int{self==0}
            Throwable e2 = e;
        }
        
        // Otherwise, if there are no arguments, look for a simply-named type.
        if (this.args.isEmpty()) {
            try {
                Disamb disamb = tc.nodeFactory().disamb();
                Node n = disamb.disambiguate(this, tc, pos, prefix, name);

                if (n instanceof TypeNode) {
                    TypeNode tn = (TypeNode) n;
                    return tn;
                }
            }
            catch (SemanticException e) {
                ex = e;
            }
        }

        if (ex == null) {
        	String typeName =  (prefix == null ? name.toString() : prefix.toString() + "." + name.toString());
            ex = new SemanticException("Could not find type \"" + typeName + argsString() + "\".", pos);
            Map<String, Object> map = CollectionFactory.newHashMap();
            map.put(CodedErrorInfo.ERROR_CODE_KEY, CodedErrorInfo.ERROR_CODE_TYPE_NOT_FOUND);
            map.put("TYPE", typeName);
            ex.setAttributes(map);
        }

        throw ex;
    }
    
    private String argsString() {
        if (this.args.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder("(");
        boolean first = true;
        for (Expr e : this.args) {
            if (!first) {
                sb.append(",");
            } else {
                first = false;
            }
            sb.append(e.type().fullName());
        }
        sb.append(")");
        return sb.toString();
    }
    
    public Context enterChildScope(Node child, Context c) {
        Context oldC = c;
        if (child != this.prefix) {
            TypeSystem ts = c.typeSystem();
            c = c.pushDepType(Types.<Type>ref(ts.unknownType(this.position)));
        }
        if (c == oldC && c.inAnnotation()) {
            c = c.shallowCopy();
        }
        c.clearAnnotation();
        Context cc = super.enterChildScope(child, c);
        return cc;
    }
    
    public Node typeCheckOverride(Node parent, ContextVisitor tc) {
        TypeSystem ts = tc.typeSystem();
        NodeFactory nf = tc.nodeFactory();
        
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
        boolean foundError = false;
        
        try {
            tn = n.disambiguateAnnotation(childtc);
            if (tn != null)
                return postprocess((X10CanonicalTypeNode) tn, n, childtc);
        }
        catch (SemanticException e) {
            if (!foundError) {
                // Mark the type resolved to prevent us from trying to resolve this again and again.
                X10ClassType ut = ts.createFakeClass(QName.make(fullName(prefix), name().id()), e);
                ut.def().position(n.position());
                sym.update(ut);
                foundError = true;
            }
        }
        // Do not permit arguments to macro calls to be Boolean and
        // &&, || or !. These cannot be handled by the constraint system.
        class CheckMacroCallArgsVisitor extends NodeVisitor {
        	IllegalConstraint error;
        	@Override
        	public Node override(Node n) {
        		if (n instanceof Binary) {
        			Binary b = (Binary) n;
        			Binary.Operator bop = b.operator();
        			if (b.type().isBoolean() && bop.equals(Binary.COND_AND) 
        					|| bop.equals(Binary.COND_OR)) {
        				error = new IllegalConstraint(b);
        			}
        		}
        		if (n instanceof Unary) {
        			Unary u = (Unary) n;
        			Unary.Operator uop = u.operator();
        			if (u.type().isBoolean() && uop.equals(Unary.NOT)) {
        				error = new IllegalConstraint(u);
        			}
        		}
        		return null;
        	}
        }
        for (Expr arg : args) {
            CheckMacroCallArgsVisitor v = new CheckMacroCallArgsVisitor();
            arg.visit(v);
            if (v.error != null) {
                Errors.issue(tc.job(), v.error, arg);
            }
        }

        for (Expr arg : args) {
            VarChecker ac = (VarChecker) new VarChecker(childtc.job()).context(childtc.context());
            try {
                arg.visit(ac);
            } catch (InternalCompilerError e) {
                Errors.issue(childtc.job(),
                        new Errors.GeneralError(e.getMessage(), e.position()), arg);
            }
            if (ac.error != null) {
                Errors.issue(childtc.job(), ac.error, arg);
            }
        }

        try {
            tn = n.disambiguateBase(tc);
        }
        
        catch (SemanticException e) {
            if (!foundError) {
                // Mark the type resolved to prevent us from trying to resolve this again and again.
                X10ClassType ut = ts.createFakeClass(QName.make(fullName(prefix), name().id()), e);
                ut.def().position(n.position());
                sym.update(ut);
                foundError = true;
            }
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
                if (numParams != typeArgs.size()) {
                    if (ct.error() == null) {
                        SemanticException e = new Errors.NumberTypeArgumentsNotSameAsNumberTypeParameters(typeArgs.size(), ct.fullName(), numParams, n.position());
                        Errors.issue(tc.job(), e, this);
                        ct = (X10ParsedClassType) ts.createFakeClass(ct.fullName(), e);
                        int i = 0;
                        for (TypeNode ta : typeArgs) {
                            ct.def().addTypeParameter(new ParameterType(ts, Position.COMPILER_GENERATED, Position.COMPILER_GENERATED, Name.make("T"+(i++)), Types.ref(ct.def())), ParameterType.Variance.INVARIANT);
                        }
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
    	return result;
    }
    
    public Node exceptionCheck(ExceptionChecker ec) {
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
