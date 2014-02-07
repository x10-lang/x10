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
import java.util.Iterator;
import java.util.List;
import java.util.Collections;

import polyglot.ast.ArrayInit;
import polyglot.ast.Expr;
import polyglot.ast.FlagsNode;
import polyglot.ast.Id;
import polyglot.ast.LocalDecl_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeCheckFragmentGoal;
import polyglot.ast.TypeNode;
import polyglot.frontend.Globals;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.LocalDef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;
import x10.errors.Errors;
import x10.extension.X10Del;
import x10.extension.X10Del_c;
import x10.extension.X10Ext;

import x10.types.X10ClassType;
import polyglot.types.Context;
import x10.types.X10FieldDef;

import x10.types.X10LocalDef;
import x10.types.X10ParsedClassType_c;
import polyglot.types.TypeSystem;
import x10.types.checker.Converter;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.XConstrainedTerm;
import x10.visit.X10PrettyPrinterVisitor;
import x10.visit.X10TypeChecker;

public class X10LocalDecl_c extends LocalDecl_c implements X10VarDecl {
	TypeNode hasType;
    private final List<Id> exploded;

    public X10LocalDecl_c(NodeFactory nf, Position pos, FlagsNode flags, TypeNode type,
			Id name, Expr init, List<Id> exploded) {
		super(pos, flags, 
				type instanceof HasTypeNode_c ? nf.UnknownTypeNode(type.position()) : type, name, init);
        this.exploded = exploded==null ? Collections.EMPTY_LIST : exploded;
        if (type instanceof HasTypeNode_c)
			hasType = ((HasTypeNode_c) type).typeNode();
	}

	/**
	 * This method is called by TypeChecker after the node has been type-checked to give this node a chance
	 * to update the current scope with declarations introduced by this node.
	 * 
	 * (Added this delegating method here merely to record how this piece of functionality works.
	 */
	@Override
	public void addDecls(Context c) {
        if (li!=null) // if we had errors in type checking, li might be null (e.g., "var x = ...")
		    super.addDecls(c);
	}
		

	@Override
	public Node buildTypes(TypeBuilder tb) {
		X10LocalDecl_c n = this;
		if (type instanceof UnknownTypeNode) {
			if (init == null)
			    Errors.issue(tb.job(),
			            new Errors.CannotInferVariableType(name().id(), position()));
			// TODO: For now, since there can be more than once assignment to a mutable variable, 
			// do not allow mutable variable types to be inferred.
			// This can be fixed later for local variables by doing better 
			// type inference.  This should never be done for fields.
			if (! flags.flags().isFinal())
			    Errors.issue(tb.job(),
			            new Errors.CannotInferTypeofMutalVariable(position()));
		}

		// This installs a LocalDef 
		n = (X10LocalDecl_c) super.buildTypes(tb);
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
	
    public NodeVisitor typeCheckEnter(TypeChecker tc) {
        return super.typeCheckEnter(tc);
    }

    /** Reconstruct the declaration. */
    protected LocalDecl_c reconstruct(FlagsNode flags, TypeNode type, TypeNode htn, Id name, Expr init) {
        if (this.flags != flags || this.type != type || this.hasType != htn || this.name != name || this.init != init) {
        	X10LocalDecl_c n =  (X10LocalDecl_c) reconstruct(flags, type, name, init);
        	n.hasType = htn;
        	return n;
        }

        return this;
    }
    protected X10LocalDecl_c hasType(TypeNode hasType) {
    	if (this.hasType != hasType)  {
    		X10LocalDecl_c n = (X10LocalDecl_c) copy();
    		n.hasType = hasType;
    		return n;
    	}
    	return this;
    }

	/**
	 * If the initializer is not null, and the type is Unknown (e.g. because the type was not explicitly
	 * specified by the programmer, and hence must be inferred), then update the type to be the type
	 * of the initializer. 
	 */
    @Override
    public Node typeCheckOverride(Node parent, ContextVisitor tc) {
        NodeVisitor childtc = tc.enter(parent, this);

        XConstrainedTerm  pt = ((Context) tc.context()).currentPlaceTerm();
        if (pt != null && pt.term() != null)
            ((X10LocalDef) localDef()).setPlaceTerm(pt.term());
        if (type() instanceof UnknownTypeNode) {

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
                TypeNode htn  = null;
                if (hasType != null) {
                    htn = (TypeNode) visitChild(hasType, childtc);
                    boolean checkSubType = true;
                    try {
                        Types.checkMissingParameters(htn);
                    } catch (SemanticException e) {
                        Errors.issue(tc.job(), e, htn);
                        checkSubType = false;
                    }
                    if (checkSubType && ! tc.typeSystem().isSubtype(type().type(), htn.type(), tc.context())) {
                        Context xc = (Context) enterChildScope(init, tc.context());
                        Expr newInit = Converter.attemptCoercion(tc.context(xc), init, htn.type());
                        if (newInit == null) {
                            Errors.issue(tc.job(),
                                         new Errors.TypeIsNotASubtypeOfTypeBound(type().type(),
                                                                                 htn.type(),
                                                                                 position()));
                        } else {
                            init = newInit;
                            r.update(newInit.type()); 
                        }
                    }
                }

                Node n = tc.leave(parent, this, reconstruct(flags, tn, htn, name, init), childtc);
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
        return null;
    }

    /**
     * At this point, the type of the declaration should be known. If the type was not specified
     * then typeCheckOverride would have set it from the type of the initializer.
     */
    @Override
    public Node typeCheck(ContextVisitor tc) {
        final TypeNode typeNode = type();
        Type type = typeNode.type();

        try {
            Types.checkMissingParameters(typeNode);
        } catch (SemanticException e) {
            Errors.issue(tc.job(), e, typeNode);
        }
        // Replace here by PlaceTerm because this local variable may be referenced
        // later by code that has been place-shifted, and will have a different 
        // interpretation of here. 
        type = PlaceChecker.ReplaceHereByPlaceTerm(type, (Context) tc.context());
        Ref<Type> r = (Ref<Type>) typeNode.typeRef();
        r.update(type);

        if (type.isVoid()) {
            SemanticException e = new Errors.LocalVariableCannotHaveType(this.type().type(), position());
            Errors.issue(tc.job(), e);
        } else {
            X10Formal_c.checkExplodedVars(exploded.size(), (Ref<Type>)this.type().typeRef(), position(), tc);
        }

        TypeSystem ts = tc.typeSystem();

        try {
            ts.checkLocalFlags(flags.flags());
        }
        catch (SemanticException e) {
            Errors.issue(tc.job(), e, this);
        }

        X10LocalDecl_c n = (X10LocalDecl_c) this.type(tc.nodeFactory().CanonicalTypeNode(typeNode.position(), type));

        // Need to check that the initializer is a subtype of the (declared or inferred) type of the variable,
        // or can be implicitly coerced to the type.
        if (n.init != null) {
            Expr newInit = Converter.attemptCoercion(tc, n.init, type);
            if (newInit != null)
                return n.init(newInit);
            Errors.issue(tc.job(), Errors.CannotAssign.make(n.init, type, tc, n.init.position()), n);
        }

        return n;
    }

	public String shortToString() {
		return "<X10LocalDecl_c #" + hashCode()  // todo: using hashCode leads to non-determinism in the output of the compiler
		// + " flags= |" + flags + "|"
		+(type() == null ? "" : " <TypeNode #" + type().hashCode()+"type=" + type().type() + ">")
		+ " name=|" + name().id() + "|"
		+ (init() == null ? "" : " <Expr #" + init().hashCode() +">")
		+ (localDef() == null ? "" : " <LocalInstance #" + localDef().hashCode() +">")
		+ ">";
	}


    /**
     * Add the declaration of the variable as we enter the scope of the
     * intializer.
     * In Java you can write this code:
     * int i= (i=2)+4;
     * But after XTENLANG-2387 we disallow it in X10:
     * var i:Int = (i=2)+4; // ERR!
     */
	public Context enterChildScope(Node child, Context c) {
		Context oldC=c;
        if (child == init) {
            c = c.pushBlock();
            // addDecls(c); - this will allow writing code like a local: int i= (i=2);
        }
		if (child == this.type || child == this.hasType) {
			if (oldC==c)
			 c=c.pushBlock();
			LocalDef li = localDef();
			c.addVariable(li.asInstance());
			c.setVarWhoseTypeIsBeingElaborated(li);
		}
		Context cc = super.enterChildScope(child, c);
		return cc;
	}
	
	/** Visit the children of the declaration. */
	public Node visitChildren(NodeVisitor v) {
	    X10LocalDecl_c n = (X10LocalDecl_c) super.visitChildren(v);
	    TypeNode hasType = (TypeNode) visitChild(n.hasType, v);
	    return n.hasType(hasType);
	}

	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	    boolean printSemi = tr.appendSemicolon(true);
	    boolean printType = tr.printType(true);

	    Flags f = flags.flags();
	    Boolean fin = f.isFinal();
	    f = f.clearFinal();
	    w.write(f.translate());
	    for (Iterator<AnnotationNode> i = (((X10Ext) this.ext()).annotations()).iterator(); i.hasNext(); ) {
	        AnnotationNode an = i.next();
	        an.prettyPrint(w, tr);
	        w.allowBreak(0, " ");
	    }
	    if (fin)
	        w.write("val ");
	    else
	        w.write("var ");

	    tr.print(this, name, w);
	    if (printType) {
	        w.write(":");
	        print(type, w, tr);
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

    @Override
    public String toString() {
        Flags flags = this.flags.flags();
        return flags.clearFinal().translate() + (flags.isFinal() ? "val" : "var") + " " + name + ":" +
               type + (init != null ? " = " + init : "") + ";";
    }
}
