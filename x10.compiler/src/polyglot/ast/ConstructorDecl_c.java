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

import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;
import x10.errors.Errors;

/**
 * A <code>ConstructorDecl</code> is an immutable representation of a
 * constructor declaration as part of a class body.
 */
public abstract class ConstructorDecl_c extends Term_c implements ConstructorDecl
{
    protected FlagsNode flags;
    protected Id name;
    protected List<Formal> formals;
  // protected List<TypeNode> throwTypes;
    public Block body;
    protected ConstructorDef ci;

    public ConstructorDecl_c(Position pos, FlagsNode flags, Id name, List<Formal> formals,  Block body) {
        super(pos);
        assert(flags != null && name != null && formals != null); // body may be null
        this.flags = flags;
        this.name = name;
        this.formals = TypedList.copyAndCheck(formals, Formal.class, true);
       // this.throwTypes = TypedList.copyAndCheck(throwTypes, TypeNode.class, true);
        this.body = body;
    }

    public List<Def> defs() {
        return Collections.<Def>singletonList(ci);
    }

    public MemberDef memberDef() {
        return ci;
    }

    /** Get the flags of the constructor. */
    public FlagsNode flags() {
        return this.flags;
    }

    /** Set the flags of the constructor. */
    public ConstructorDecl flags(FlagsNode flags) {
        ConstructorDecl_c n = (ConstructorDecl_c) copy();
        n.flags = flags;
        return n;
    }

    /** Get the name of the constructor. */
    public Id name() {
        return this.name;
    }

    /** Set the name of the constructor. */
    public ConstructorDecl name(Id name) {
        ConstructorDecl_c n = (ConstructorDecl_c) copy();
        n.name = name;
        return n;
    }

    /** Get the formals of the constructor. */
    public List<Formal> formals() {
        return Collections.unmodifiableList(this.formals);
    }

    /** Set the formals of the constructor. */
    public ConstructorDecl formals(List<Formal> formals) {
        ConstructorDecl_c n = (ConstructorDecl_c) copy();
        n.formals = TypedList.copyAndCheck(formals, Formal.class, true);
        return n;
    }

    public Term codeBody() {
        return this.body;
    }

    /** Get the body of the constructor. */
    public Block body() {
        return this.body;
    }

    /** Set the body of the constructor. */
    public CodeBlock body(Block body) {
        ConstructorDecl_c n = (ConstructorDecl_c) copy();
        n.body = body;
        return n;
    }

    /** Get the constructorInstance of the constructor. */
    public ConstructorDef constructorDef() {
        return ci;
    }


    /** Get the procedureInstance of the constructor. */
    public ProcedureDef procedureInstance() {
        return ci;
    }

    public CodeDef codeDef() {
        return procedureInstance();
    }

    /** Set the constructorInstance of the constructor. */
    public ConstructorDecl constructorDef(ConstructorDef ci) {
        if (ci == this.ci) return this;
        ConstructorDecl_c n = (ConstructorDecl_c) copy();
        n.ci = ci;
        return n;
    }

    /** Reconstruct the constructor. */
    protected ConstructorDecl_c reconstruct(FlagsNode flags, Id name, List<Formal> formals, Block body) {
        if (flags != this.flags || name != this.name || ! CollectionUtil.allEqual(formals, this.formals)  || body != this.body) {
            ConstructorDecl_c n = (ConstructorDecl_c) copy();
            n.flags = flags;
            n.name = name;
            n.formals = TypedList.copyAndCheck(formals, Formal.class, true);
            n.body = body;
            return n;
        }

        return this;
    }

    /** Visit the children of the constructor. */
    public Node visitChildren(NodeVisitor v) {
        ConstructorDecl_c n = (ConstructorDecl_c) visitSignature(v);
        Block body = (Block) n.visitChild(n.body, v);
        return body == n.body ? n : n.body(body);
    }


    protected abstract ConstructorDef createConstructorDef(TypeSystem ts, ClassDef ct, Flags flags);

    public Context enterScope(Context c) {
        return c.pushCode(ci);
    }

    public Node visitSignature(NodeVisitor v) {
	FlagsNode flags = (FlagsNode) this.visitChild(this.flags, v);
        Id name = (Id) this.visitChild(this.name, v);
        List<Formal> formals = this.visitList(this.formals, v);
        return reconstruct(flags, name, formals,  this.body);
    }

    /** Type check the declaration. */
    public Node typeCheckBody(Node parent, TypeChecker tc, TypeChecker childtc) {
        ConstructorDecl_c n = this;
        Block body = (Block) n.visitChild(n.body, childtc);
        n = (ConstructorDecl_c) n.body(body);
        return n;
    }

    /** Type check the constructor. */
    public Node typeCheck(ContextVisitor tc) {
        TypeSystem ts = tc.typeSystem();


        return this;
    }
    
    public Node conformanceCheck(ContextVisitor tc) {
	Context c = tc.context();
	TypeSystem ts = tc.typeSystem();
	
	ClassType ct = c.currentClass();
	
	if (ct.flags().isInterface()) {
	    Errors.issue(tc.job(), 
	    		new Errors.CannotDeclareConstructorInInterface(name().position()));
	}
	
	if (ct.isAnonymous()) {
	    Errors.issue(tc.job(),
	            new Errors.CannotDeclareConstructorInAnonymousClass(name().position()));
	}
	
	Name ctName = ct.name();
	
	if (! ctName.equals(name.id())) {
	    Errors.issue(tc.job(), 
	    		new Errors.ConstructorNameDoesNotMatchContainingClassName(name, ctName, name().position()));
	}
	
	Flags flags = flags().flags();
	
	try {
	    ts.checkConstructorFlags(flags);
	}
	catch (SemanticException e) {
	    Errors.issue(tc.job(), e, this);
	}
	
	if (body == null && ! flags.isNative()) {
	    Errors.issue(tc.job(), new Errors.MissingConstructorBody(name().position()));
	}
	
	if (body != null && flags.isNative()) {
	    Errors.issue(tc.job(), new Errors.NativeConstructorCannotHaveABody(name().position()));
	}
	
	return this;
    }

    public abstract String toString();

    /** Write the constructor to an output file. */
    public abstract void prettyPrintHeader(CodeWriter w, PrettyPrinter tr);

    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        prettyPrintHeader(w, tr);

        if (body != null) {
            printSubStmt(body, w, tr);
        }
        else {
            w.write(";");
        }
    }

    public void dump(CodeWriter w) {
        super.dump(w);

        if (ci != null) {
            w.allowBreak(4, " ");
            w.begin(0);
            w.write("(instance " + ci + ")");
            w.end();
        }
    }

    public Term firstChild() {
        return listChild(formals(), body() != null ? body() : null);
    }

    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        if (body() != null) {
            v.visitCFGList(formals(), body(), ENTRY);
            v.visitCFG(body(), this, EXIT);
        }
        else {
            v.visitCFGList(formals(), this, EXIT);
        }

        return succs;
    }

}
