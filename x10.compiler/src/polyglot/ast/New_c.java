/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.ast;

import java.util.*;

import polyglot.frontend.Globals;
import polyglot.frontend.Goal;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;
import x10.errors.Errors;
import x10.ast.X10New;
import x10.types.X10ClassDef;
import x10.types.X10ConstructorInstance;

/**
 * A <code>New</code> is an immutable representation of the use of the
 * <code>new</code> operator to create a new instance of a class.  In
 * addition to the type of the class being created, a <code>New</code> has a
 * list of arguments to be passed to the constructor of the object and an
 * optional <code>ClassBody</code> used to support anonymous classes.
 */
public abstract class New_c extends Expr_c implements X10New
{
    protected Expr qualifier;
    protected TypeNode tn;
    protected List<Expr> arguments;
    protected ClassBody body;
    protected ConstructorInstance ci;
    protected X10ClassDef anonType;

    public New_c(Position pos, Expr qualifier, TypeNode tn, List<Expr> arguments, ClassBody body) {
	super(pos);
        assert(tn != null && arguments != null); // qualifier and body may be null
        this.qualifier = qualifier;
        this.tn = tn;
	this.arguments = TypedList.copyAndCheck(arguments, Expr.class, true);
	this.body = body;
    }

    public List<Def> defs() {
        if (body != null) {
            return Collections.<Def>singletonList(anonType);
        }
        return Collections.<Def>emptyList();
    }

    /** Get the qualifier expression of the allocation. */
    public Expr qualifier() {
        return this.qualifier;
    }

    /** Set the qualifier expression of the allocation. */
    public X10New qualifier(Expr qualifier) {
        New_c n = (New_c) copy();
        n.qualifier = qualifier;
        return n;
    }

    /** Get the type we are instantiating. */
    public TypeNode objectType() {
        return this.tn;
    }

    /** Set the type we are instantiating. */
    public X10New objectType(TypeNode tn) {
        New_c n = (New_c) copy();
	n.tn = tn;
	return n;
    }

    public X10ClassDef anonType() {
	return (X10ClassDef) this.anonType;
    }

    public X10New anonType(X10ClassDef anonType) {
        if (anonType == this.anonType) return this;
	New_c n = (New_c) copy();
	n.anonType = anonType;
	return n;
    }

    public ConstructorInstance procedureInstance() {
	return constructorInstance();
    }

    public X10ConstructorInstance constructorInstance() {
	return (X10ConstructorInstance)this.ci;
    }

    public New procedureInstance(ProcedureInstance<? extends ProcedureDef> pi) {
        return constructorInstance((ConstructorInstance) pi);
    }

    public X10New constructorInstance(ConstructorInstance ci) {
        if (ci == this.ci) return this;
	New_c n = (New_c) copy();
	n.ci = ci;
	return n;
    }

    public List<Expr> arguments() {
	return this.arguments;
    }

    public ProcedureCall arguments(List<Expr> arguments) {
	New_c n = (New_c) copy();
	n.arguments = TypedList.copyAndCheck(arguments, Expr.class, true);
	return n;
    }

    public ClassBody body() {
	return this.body;
    }

    public X10New body(ClassBody body) {
	New_c n = (New_c) copy();
	n.body = body;
	return n;
    }

    /** Reconstruct the expression. */
    protected New_c reconstruct(Expr qualifier, TypeNode tn, List<Expr> arguments, ClassBody body) {
	if (qualifier != this.qualifier || tn != this.tn || ! CollectionUtil.allEqual(arguments, this.arguments) || body != this.body) {
	    New_c n = (New_c) copy();
	    n.tn = tn;
	    n.qualifier = qualifier;
	    n.arguments = TypedList.copyAndCheck(arguments, Expr.class, true);
	    n.body = body;
	    return n;
	}

	return this;
    }

    /** Visit the children of the expression. */
    public abstract Node visitChildren(NodeVisitor v);

    public Context enterChildScope(Node child, Context c) {
        if (child == body && anonType != null && body != null) {
            c = c.pushClass(anonType, anonType.asType());
        }
        return super.enterChildScope(child, c);
    }

    Goal enable = null;
    
    public Node buildTypesOverride(TypeBuilder tb) {
        TypeSystem ts = tb.typeSystem();

        New_c n = this;

        ConstructorInstance ci = ts.createConstructorInstance(position(), position(), new ErrorRef_c<ConstructorDef>(ts, position(), "Cannot get ConstructorDef before type-checking new expression."));
        n = (New_c) n.constructorInstance(ci);
        
        Expr qual = (Expr) n.visitChild(n.qualifier(), tb);
        TypeNode objectType = (TypeNode) n.visitChild(n.objectType(), tb);
        List<Expr> arguments = n.visitList(n.arguments(), tb);
        
        ClassBody body = null;
        
        if (n.body() != null) {
            TypeBuilder tb2 = tb.pushAnonClass(position());
            X10ClassDef type = tb2.currentClass();
            type.superType(objectType.typeRef());
            
            n = (New_c) n.anonType(type);
            
            body = (ClassBody) n.visitChild(n.body(), tb2);
        }
        
        n = n.reconstruct(qual, objectType, arguments, body);
        
        return n.type(ts.unknownType(position()));
    }
    
    public abstract New_c typeCheckObjectType(TypeChecker childtc);
    
    @Override
    public Node typeCheckOverride(Node parent, ContextVisitor tc) {
        TypeChecker childtc;
        NodeVisitor childv = tc.enter(parent, this);
        if (childv instanceof TypeChecker) {
            childtc = (TypeChecker) childv;
        }
        else {
            return this;
        }

        New_c n = typeCheckHeader(childtc);
        
        ClassBody body = (ClassBody) n.visitChild(n.body, childtc);

        n = (New_c) n.body(body);
        return tc.leave(parent, this, n, childtc);
    }

    protected New_c typeCheckHeader(TypeChecker childtc) {
    	TypeSystem ts = childtc.typeSystem();

    	New_c n = this;

    	n = n.typeCheckObjectType(childtc);

    	Expr qualifier = n.qualifier;
    	TypeNode tn = n.tn;
    	List<Expr> arguments = n.arguments;
    	ClassBody body = n.body;

    	if (body != null) {
    		Ref<? extends Type> ct = tn.typeRef();
    		ClassDef anonType = n.anonType();

    		assert anonType != null;

    		if (! ct.get().toClass().flags().isInterface()) {
    			anonType.superType(ct);
    		}
    		else {
    			// [DC] assume that not setting this is OK
    			// we don't have a root class anymore (just a root interface: Any)
    			//anonType.superType(Types.<Type>ref(ts.Object()));
    			assert anonType.interfaces().isEmpty() || anonType.interfaces().get(0) == ct;
    			if (anonType.interfaces().isEmpty())
    				anonType.addInterface(ct);
    		}
    	}

    	arguments = visitList(arguments, childtc);
    	n = n.reconstruct(qualifier, tn, arguments, body);
    	return n;
    }

    /**
     * @param ar
     * @param ct
     * @throws SemanticException
     */
    protected abstract New findQualifier(TypeChecker ar, ClassType ct);
    
    public abstract Node typeCheck(ContextVisitor tc);

    protected void typeCheckNested(ContextVisitor tc) throws SemanticException {
        if (qualifier != null) {
            // We have not disambiguated the type node yet.

            // Get the qualifier type first.
            Type qt = qualifier.type();

            if (! qt.isClass()) {
                throw new SemanticException("Cannot instantiate member class of a non-class type.", qualifier.position());
            }
            
            // Disambiguate the type node as a member of the qualifier type.
            ClassType ct = tn.type().toClass();

            // According to JLS2 15.9.1, the class type being
            // instantiated must be inner.
	    if (! ct.isInnerClass()) {
            if (!(qualifier instanceof Special)) // Yoav added "this" qualifier for non-static anonymous classes
                throw new SemanticException("Cannot provide a containing instance for non-inner class " + ct.fullName() + ".", qualifier.position());
            }
        }
        else {
            ClassType ct = tn.type().toClass();

            if (ct.isMember()) {
                for (ClassType t = ct; t.isMember(); t = t.outer()) {
                    if (! t.flags().isStatic()) {
                        throw new SemanticException("Cannot allocate non-static member class \"" +t + "\".", position());
                    }
                }
            }
        }
    }

    protected void typeCheckFlags(ContextVisitor tc) throws SemanticException {
        ClassType ct = tn.type().toClass();

	if (this.body == null) {
	    if (ct.flags().isInterface()) {
		throw new SemanticException("Cannot instantiate an interface.", position());
	    }

	    if (ct.flags().isAbstract()) {
		throw new SemanticException("Cannot instantiate an abstract class.", position());
	    }
	}
	else {
	    if (ct.flags().isFinal()) {
		throw new SemanticException("Cannot create an anonymous subclass of a final class.", position());
            }

	    if (ct.flags().isInterface() && ! arguments.isEmpty()) {
	        throw new SemanticException("Cannot pass arguments to an anonymous class that implements an interface.",arguments.get(0).position());
	    }
	}
    }

    @Override
    public Node conformanceCheck(ContextVisitor tc) {
        if (body == null) {
            return this;
        }
        // Check that the anonymous class implements all abstract methods that it needs to.
        try {
            TypeSystem ts = tc.typeSystem();
            ts.checkClassConformance(anonType.asType(), enterChildScope(body, tc.context()));
        } catch (SemanticException e) {
            Errors.issue(tc.job(), e, this);
        }
        return this;
    }

    /** Get the precedence of the expression. */
    public Precedence precedence() {
        return Precedence.LITERAL;
    }

    public String toString() {
	return (qualifier != null ? (qualifier.toString() + ".") : "") +
            "new " + tn + "(...)" + (body != null ? " " + body : "");
    }

    protected void printQualifier(CodeWriter w, PrettyPrinter tr) {
        if (qualifier != null) {
            print(qualifier, w, tr);
            w.write(".");
        }
    }

    protected void printArgs(CodeWriter w, PrettyPrinter tr) {
	w.write("(");
	w.allowBreak(2, 2, "", 0);
	w.begin(0);

	for (Iterator<Expr> i = arguments.iterator(); i.hasNext();) {
	    Expr e = i.next();

	    print(e, w, tr);

	    if (i.hasNext()) {
		w.write(",");
		w.allowBreak(0);
	    }
	}

	w.end();
	w.write(")");
    }

    protected void printBody(CodeWriter w, PrettyPrinter tr) {
	if (body != null) {
	    w.write(" {");
	    print(body, w, tr);
            w.write("}");
	}
    }

    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        printQualifier(w, tr);
	w.write("new ");
       
	// We need to be careful when pretty printing "new" expressions for
        // member classes.  For the expression "e.new C()" where "e" has
        // static type "T", the TypeNode for "C" is actually the type "T.C".
        // But, if we print "T.C", the post compiler will try to lookup "T"
        // in "T".  Instead, we print just "C".
        if (qualifier != null) {
            w.write(tn.nameString());
        }
        else {
            print(tn, w, tr);
        }
        
        printArgs(w, tr);
        printBody(w, tr);
    }
    
    public Term firstChild() {
        return qualifier != null ? (Term) qualifier : tn;
    }

    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        if (qualifier != null) {
            v.visitCFG(qualifier, tn, ENTRY);
        }
        
        if (body() != null) {
            v.visitCFG(tn, listChild(arguments, body()), ENTRY);
            v.visitCFGList(arguments, body(), ENTRY);
            v.visitCFG(body(), this, EXIT);
        } else {
            if (!arguments.isEmpty()) {
                v.visitCFG(tn, listChild(arguments, null), ENTRY);
                v.visitCFGList(arguments, this, EXIT);
            } else {
                v.visitCFG(tn, this, EXIT);
            }
        }

        return succs;
    }

    public List<Type> throwTypes(TypeSystem ts) {
        List<Type> l = new ArrayList<Type>();
        assert ci != null : "null ci for " + this;
        l.addAll(ci.throwTypes());
        l.addAll(ts.uncheckedExceptions());
        return l;
    }
    
}
