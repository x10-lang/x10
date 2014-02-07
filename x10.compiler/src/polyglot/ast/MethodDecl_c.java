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
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.ast;

import java.util.*;

import polyglot.main.Reporter;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;
import x10.errors.Errors;
import x10.errors.Errors.InterfaceMethodsMustBePublic;
import x10.types.MethodInstance;
import x10.types.X10ClassDef;
import x10.types.X10TypeEnv_c;
import x10.visit.Desugarer;
import x10.ast.X10ClassDecl_c;

/**
 * A method declaration.
 */
public abstract class MethodDecl_c extends Term_c implements MethodDecl
{
    protected FlagsNode flags;
    protected TypeNode returnType;
    protected Id name;
    protected List<Formal> formals;
   // protected List<TypeNode> throwTypes;
    protected Block body;
    protected MethodDef mi;

    public MethodDecl_c(Position pos, FlagsNode flags, TypeNode returnType, Id name, List<Formal> formals, Block body) {
	super(pos);
	assert(flags != null && returnType != null && name != null && formals != null); // body may be null
	this.flags = flags;
	this.returnType = returnType;
	this.name = name;
	this.formals = TypedList.copyAndCheck(formals, Formal.class, true);
	this.body = body;
    }

    public List<Def> defs() {
        return Collections.<Def>singletonList(mi);
    }

    public MemberDef memberDef() {
        return mi;
    }

    /** Get the flags of the method. */
    public FlagsNode flags() {
	return this.flags;
    }

    /** Set the flags of the method. */
    public MethodDecl flags(FlagsNode flags) {
	MethodDecl_c n = (MethodDecl_c) copy();
	n.flags = flags;
	return n;
    }

    /** Get the return type of the method. */
    public TypeNode returnType() {
	return this.returnType;
    }

    /** Set the return type of the method. */
    public MethodDecl returnType(TypeNode returnType) {
	MethodDecl_c n = (MethodDecl_c) copy();
	n.returnType = returnType;
	return n;
    }

    /** Get the name of the method. */
    public Id name() {
        return this.name;
    }
    
    /** Set the name of the method. */
    public MethodDecl name(Id name) {
        MethodDecl_c n = (MethodDecl_c) copy();
        n.name = name;
        return n;
    }
    
    /** Get the formals of the method. */
    public List<Formal> formals() {
	return Collections.<Formal>unmodifiableList(this.formals);
    }

    /** Set the formals of the method. */
    public MethodDecl formals(List<Formal> formals) {
	MethodDecl_c n = (MethodDecl_c) copy();
	n.formals = TypedList.copyAndCheck(formals, Formal.class, true);
	return n;
    }

    public Term codeBody() {
        return this.body;
    }
    
    /** Get the body of the method. */
    public Block body() {
	return this.body;
    }

    /** Set the body of the method. */
    public CodeBlock body(Block body) {
	MethodDecl_c n = (MethodDecl_c) copy();
	n.body = body;
	return n;
    }

    /** Get the method instance of the method. */
    public MethodDef methodDef() {
	return mi;
    }

    /** Set the method instance of the method. */
    public MethodDecl methodDef(MethodDef mi) {
        if (mi == this.mi) return this;
	MethodDecl_c n = (MethodDecl_c) copy();
	n.mi = mi;
	return n;
    }

    public CodeDef codeDef() {
	return procedureInstance();
    }

    /** Get the procedure instance of the method. */
    public ProcedureDef procedureInstance() {
	return mi;
    }

    /** Reconstruct the method. */
    protected MethodDecl_c reconstruct(FlagsNode flags, TypeNode returnType, Id name, List<Formal> formals, Block body) {
	if (flags != this.flags || returnType != this.returnType || name != this.name || ! CollectionUtil.<Formal>allEqual(formals, this.formals)  || body != this.body) {
	    MethodDecl_c n = (MethodDecl_c) copy();
	    n.flags = flags;
	    n.returnType = returnType;
            n.name = name;
	    n.formals = TypedList.copyAndCheck(formals, Formal.class, true);
	    n.body = body;
	    return n;
	}

	return this;
    }

    /** Visit the children of the method. */
    public Node visitChildren(NodeVisitor v) {
        MethodDecl_c n = (MethodDecl_c) visitSignature(v);
	Block body = (Block) n.visitChild(n.body, v);
	return body == n.body ? n : n.body(body);
    }

    public abstract Node buildTypesOverride(TypeBuilder tb);

    protected abstract MethodDef createMethodDef(TypeSystem ts, X10ClassDef ct, Flags flags);

    public Context enterScope(Context c) {
        Reporter reporter = c.typeSystem().extensionInfo().getOptions().reporter;
        if (reporter.should_report(TOPICS, 5))
	        reporter.report(5, "enter scope of method " + name);
        c = c.pushCode(mi);
        return c;
    }
    
    public abstract Node visitSignature(NodeVisitor v);
    
    /** Type check the declaration. */
    public Node typeCheckBody(Node parent, TypeChecker tc, TypeChecker childtc) {
        MethodDecl_c n = this;
        Block body = (Block) n.visitChild(n.body, childtc);
        n = (MethodDecl_c) n.body(body);
        return n;
    }

    @Override
    public Node conformanceCheck(ContextVisitor tc) {
        // Get the mi flags, not the node flags since the mi flags
        // account for being nested within an interface.
        Flags flags = mi.flags();
        checkFlags(tc, flags);

        overrideMethodCheck(tc);

        return this;
    }

    protected void checkFlags(ContextVisitor tc, Flags flags) {
        TypeSystem ts = tc.typeSystem();

        if (tc.context().currentClass().flags().isInterface()) {
            if (flags.isProtected() || flags.isPrivate()) {
                Errors.issue(tc.job(), 
                        new Errors.InterfaceMethodsMustBePublic(name().position()));
            }

            if (flags.isStatic()) {
                Errors.issue(tc.job(), new Errors.InterfaceMethodsCannotBeStatic(name().position()));
            }
        }

        try {
            ts.checkMethodFlags(flags);
        }
        catch (SemanticException e) {
            Errors.issue(tc.job(), e, this);
        }

        Type container = Types.get(methodDef().container());
        ClassType ct = container.toClass();

        if (body == null && ! (flags.isAbstract() || flags.isNative())) {
            Errors.issue(tc.job(), new Errors.MissingMethodBody(name().position()));
        }

        if (body != null && ct.flags().isInterface()) {
            Errors.issue(tc.job(), new Errors.InterfaceMethodsCannotHaveBody(name().position()));
        }

        if (body != null && flags.isAbstract()) {
            Errors.issue(tc.job(), new Errors.AbstractMethodCannotHaveBody(name().position()));
        }

        if (body != null && flags.isNative()) {
            Errors.issue(tc.job(), new Errors.NativeMethodCannotHaveBody(name().position()));
        }

        // check that inner classes do not declare static methods
        if (ct != null && flags.isStatic() && (ct.isInnerClass() || ct.isLocal() || ct.isAnonymous())) {
            // it's a static method in an inner class.
            Errors.issue(tc.job(),
                    new Errors.InnerClassesCannotDeclareStaticMethods(methodDef(), ct, name().position()));             
        }
    }

    protected void overrideMethodCheck(ContextVisitor tc) {
        TypeSystem ts = tc.typeSystem();

        MethodInstance mi = methodDef().asInstance();
        for (MethodInstance mj : mi.implemented(tc.context()) ){
            if (! ts.isAccessible(mj, tc.context())) {
                // [DC] presumably this will be raising an error somewhere else?
                continue;
            }

            mj = X10ClassDecl_c.expandMacros(tc, ts, mi, mj);
            try {
                ts.checkOverride(mi, mj, tc.context());
            } catch (SemanticException e) {
                Errors.issue(tc.job(), e, this);
            }
        }
    }

    public NodeVisitor exceptionCheckEnter(ExceptionChecker ec) {
        return ec.push(new ExceptionChecker.CodeTypeReporter("Method " + mi.signature()))
                 .push(methodDef().asInstance().throwTypes());
    }

    public abstract String toString();

    /** Write the method to an output file. */
    public void prettyPrintHeader(CodeWriter w, PrettyPrinter tr) {
	w.begin(0);
	print(flags, w, tr);
	print(returnType, w, tr);
	w.allowBreak(2, 2, " ", 1);
	w.write(name + "(");

	w.allowBreak(2, 2, "", 0);
	w.begin(0);

	for (Iterator<Formal> i = formals.iterator(); i.hasNext(); ) {
	    Formal f = i.next();
	    
	    print(f, w, tr);

	    if (i.hasNext()) {
		w.write(",");
		w.allowBreak(0, " ");
	    }
	}

	w.end();
	w.write(")");
/*
	if (! throwTypes().isEmpty()) {
	    w.allowBreak(6);
	    w.write("throws ");

	    for (Iterator<TypeNode> i = throwTypes().iterator(); i.hasNext(); ) {
	        TypeNode tn = (TypeNode) i.next();
		print(tn, w, tr);

		if (i.hasNext()) {
		    w.write(",");
		    w.allowBreak(4, " ");
		}
	    }
	}
*/
	w.end();
    }

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

	if (mi != null) {
	    w.allowBreak(4, " ");
	    w.begin(0);
	    w.write("(instance " + mi + ")");
	    w.end();
	}

        w.allowBreak(4, " ");
        w.begin(0);
        w.write("(name " + name + ")");
        w.end();
    }

    public Term firstChild() {
        return listChild(formals(), returnType());
    }

    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        v.visitCFGList(formals(), returnType(), ENTRY);
        
        if (body() == null) {
            v.visitCFG(returnType(), this, EXIT);
        }
        else {
            v.visitCFG(returnType(), body(), ENTRY);
            v.visitCFG(body(), this, EXIT);
        }
        
        return succs;
    }

    private static final Collection<String> TOPICS = 
            CollectionUtil.list(Reporter.types, Reporter.context);
}
