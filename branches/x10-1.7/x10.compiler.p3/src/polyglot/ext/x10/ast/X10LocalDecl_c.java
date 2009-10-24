/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.ArrayInit;
import polyglot.ast.Expr;
import polyglot.ast.FlagsNode;
import polyglot.ast.Id;
import polyglot.ast.LocalDecl_c;
import polyglot.ast.Node;
import polyglot.ast.TypeCheckFragmentGoal;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.extension.X10Del;
import polyglot.ext.x10.extension.X10Del_c;
import polyglot.ext.x10.extension.X10Ext;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10FieldDef;
import polyglot.ext.x10.types.X10LocalDef;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.LocalDef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;

public class X10LocalDecl_c extends LocalDecl_c implements X10VarDecl {
	
	public X10LocalDecl_c(Position pos, FlagsNode flags, TypeNode type,
			Id name, Expr init) {
		super(pos, flags, type, name, init);
	}

	@Override
	public Node buildTypes(TypeBuilder tb) throws SemanticException {
		if (type instanceof UnknownTypeNode && init == null)
			throw new SemanticException("Cannot infer variable type; variable has no initializer.", position());
		// TODO: For now, since there can be more than once assignment to a mutable variable, do not allow mutable variable types to be inferred.
		// This can be fixed later for local variables by doing better type inference.  This should never be done for fields.
		if (type instanceof UnknownTypeNode && ! flags.flags().isFinal())
			throw new SemanticException("Cannot infer type of non-final variable.", position());
		
		X10LocalDecl_c n = (X10LocalDecl_c) super.buildTypes(tb);

		X10LocalDef fi = (X10LocalDef) n.localDef();

	        List<AnnotationNode> as = ((X10Del) n.del()).annotations();
	        if (as != null) {
	            List<Ref<? extends Type>> ats = new ArrayList<Ref<? extends Type>>(as.size());
	            for (AnnotationNode an : as) {
	                ats.add(an.annotationType().typeRef());
	            }
	            fi.setDefAnnotations(ats);
	        }
	        
	        return n;
	}
	
        @Override
        public Node typeCheckOverride(Node parent, ContextVisitor tc) throws SemanticException {
            if (type() instanceof UnknownTypeNode) {
                NodeVisitor childtc = tc.enter(parent, this);
                
                Expr init = (Expr) this.visitChild(init(), childtc);
                if (init != null) {
                    Type t = init.type();
                    if (t instanceof X10ClassType) {
                        X10ClassType ct = (X10ClassType) t;
                        if (ct.isAnonymous()) {
                            if (ct.interfaces().size() > 0)
                                t = ct.interfaces().get(0);
                            else
                                t = ct.superClass();
                        }
                    }
                    LazyRef<Type> r = (LazyRef<Type>) type().typeRef();
                    r.update(t);
                    
                    FlagsNode flags = (FlagsNode) this.visitChild(flags(), childtc);
                    Id name = (Id) this.visitChild(name(), childtc);
                    TypeNode tn = (TypeNode) this.visitChild(type(), childtc);
                    
                    Node n = tc.leave(parent, this, reconstruct(flags, tn, name, init), childtc);
                    List<AnnotationNode> oldAnnotations = ((X10Ext) ext()).annotations();
                    if (oldAnnotations == null || oldAnnotations.isEmpty()) {
                            return n;
                    }
                    List<AnnotationNode> newAnnotations = node().visitList(oldAnnotations, childtc);
                    if (! CollectionUtil.allEqual(oldAnnotations, newAnnotations)) {
                            return ((X10Del) n.del()).annotations(newAnnotations);
                    }
                    return n;
                }
            }
            return super.typeCheckOverride(parent, tc);
        }

	@Override
	public Node typeCheck(ContextVisitor tc) throws SemanticException {
	    if (this.type().type().isVoid())
	        throw new SemanticException("Local variable cannot have type " + this.type().type() + ".", position());

	    TypeSystem ts = tc.typeSystem();

	    try {
	        ts.checkLocalFlags(flags.flags());
	    }
	    catch (SemanticException e) {
	        throw new SemanticException(e.getMessage(), position());
	    }

	    if (init != null) {
	            try {
	                Expr newInit = X10New_c.attemptCoercion(tc, init, this.type().type());
	                return this.init(newInit);
	            }
	            catch (SemanticException e) {
	                throw new SemanticException("The type of the variable " +
	                                            "initializer \"" + init.type() +
	                                            "\" does not match that of " +
	                                            "the declaration \"" +
	                                            type.type() + "\".",
	                                            init.position());
	            }
	    }

	    return this;
	}

	    @Override
	    public Node setResolverOverride(Node parent, TypeCheckPreparer v) {
		    if (type() instanceof UnknownTypeNode && init != null) {
			    UnknownTypeNode tn = (UnknownTypeNode) type();

			    NodeVisitor childv = v.enter(parent, this);
	    	            childv = childv.enter(this, init);
	    		    			    
			    if (childv instanceof TypeCheckPreparer) {
				    TypeCheckPreparer tcp = (TypeCheckPreparer) childv;
				    final LazyRef<Type> r = (LazyRef<Type>) tn.typeRef();
				    TypeChecker tc = new TypeChecker(v.job(), v.typeSystem(), v.nodeFactory(), v.getMemo());
				    tc = (TypeChecker) tc.context(tcp.context().freeze());
				    r.setResolver(new TypeCheckExprGoal(this, init, tc, r));
			    }
		    }
		    return super.setResolverOverride(parent, v);
	    }


	public String shortToString() {
		return "<X10LocalDecl_c #" + hashCode() 
		// + " flags= |" + flags + "|"
		+(type() == null ? "" : " <TypeNode #" + type().hashCode()+"type=" + type().type() + ">")
		+ " name=|" + name().id() + "|"
		+ (init() == null ? "" : " <Expr #" + init().hashCode() +">")
		+ (localDef() == null ? "" : " <LocalInstance #" + localDef().hashCode() +">")
		+ ">";
	}

	public Context enterChildScope(Node child, Context c) {
		if (child == this.type) {
			X10Context xc = (X10Context) c.pushBlock();
			LocalDef li = localDef();
			xc.addVariable(li.asInstance());
			xc.setVarWhoseTypeIsBeingElaborated(li);
			c = xc;
		}
		Context cc = super.enterChildScope(child, c);
		return cc;
	}
	
	public Type childExpectedType(Expr child, AscriptionVisitor av) {
	    if (child == init) {
	        TypeSystem ts = av.typeSystem();
	        return type.type();
	    }

	    return child.type();
	}

	/**
	 * like the base-class impl, but writes "ident: type" instead, per X10 syntax
	 */
	@Override
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        boolean printSemi = tr.appendSemicolon(true);
        boolean printType = tr.printType(true);

        print(flags, w, tr);
        tr.print(this, name, w);
        if (printType) {
            w.write(":");
            print(type, w, tr);
            w.write(" ");
        }

        if (init != null) {
            w.write(" =");
            w.allowBreak(2, " ");
            print(init, w, tr);
        }

        if (printSemi) {
            w.write(";");
        }

        tr.printType(printType);
        tr.appendSemicolon(printSemi);
    }
}
